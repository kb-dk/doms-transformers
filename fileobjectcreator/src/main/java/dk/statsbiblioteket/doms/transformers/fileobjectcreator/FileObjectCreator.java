package dk.statsbiblioteket.doms.transformers.fileobjectcreator;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.transformers.common.DomsConfig;
import dk.statsbiblioteket.doms.transformers.common.DomsWebserviceFactory;
import dk.statsbiblioteket.doms.transformers.common.muxchannels.MuxFileChannelCalculator;
import dk.statsbiblioteket.doms.transformers.fileenricher.FFProbeLocationPropertyBasedDomsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class FileObjectCreator {
    private static Logger log = LoggerFactory.getLogger(FileObjectCreator.class);
    private static String baseName = "fileobjectcreator_";
    private static BufferedWriter newUuidWriter;
    private static BufferedWriter successWriter;
    private static BufferedWriter failureWriter;
    private static BufferedWriter ignoreWriter;
    private static FFProbeLocationPropertyBasedDomsConfig config;
    private static final int STATUS_CHAR_PR_LINE = 100;
    private static final char SUCCESS_CHAR = '+';
    private static final char FAILURE_CHAR = '#';
    private static final char IGNORE_CHAR = '.';
    private static int logCounter = 0;
    private static boolean shutdown = false;

    private static BufferedReader uuidFileReader = null;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Reading data from stdin..");
            uuidFileReader = new BufferedReader(new InputStreamReader(System.in));
        } else {
            System.out.println("Input file: " + args[0]);
            try {
                uuidFileReader = new BufferedReader(new FileReader(new File(args[0])));
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + args[0]);
                System.exit(1);
            }
        }

        try {
            File configFile = null;

            try {
                URL resource = Thread.currentThread().getContextClassLoader().getResource("fileobjectcreator.properties");
                URI uri = resource.toURI();
                configFile = new File(uri);
            } catch (Exception e) {
                System.err.println("fileobjectcreator.properties not found, try putting it in 'conf'.");
                System.exit(1);
            }

            config = new FFProbeLocationPropertyBasedDomsConfig(configFile);
            System.out.println(config);

            File newUuidLog = new File(baseName + "new-uuids");
            File successLog = new File(baseName + "successful-files");
            File failureLog = new File(baseName + "failed-files");
            File ignoreLog = new File(baseName + "ignored-files");

            List<File> logFiles = new LinkedList<File>();
            List<BufferedWriter> logWriters = new LinkedList<BufferedWriter>();
            logFiles.add(newUuidLog);
            logFiles.add(successLog);
            logFiles.add(failureLog);
            logFiles.add(ignoreLog);

            boolean logsCleared = true;

            for (File f : logFiles) {
                if (f.exists()) {
                    logsCleared = false;
                    System.out.println("File already exists: " + f);
                }
            }

            if (!logsCleared) {
                System.exit(1);
            } else {
                newUuidWriter = new BufferedWriter(new FileWriter(newUuidLog));
                successWriter = new BufferedWriter(new FileWriter(successLog));
                failureWriter = new BufferedWriter(new FileWriter(failureLog));
                ignoreWriter = new BufferedWriter(new FileWriter(ignoreLog));
                logWriters.add(newUuidWriter);
                logWriters.add(successWriter);
                logWriters.add(failureWriter);
                logWriters.add(ignoreWriter);
            }


            DomsObject.setBaseUrl(config.getProperty("dk.statsbiblioteket.doms.transformers.baseurl", ""));

            new FileObjectCreator(uuidFileReader);

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + args[0]);
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public FileObjectCreator(BufferedReader reader) {
        try {
            List<String> data = new ArrayList<String>();

            String line;

            while((line = reader.readLine()) != null) {
                data.add(line);
            }

            System.out.println(
                    String.format(
                            "'%c': added file, '%c': ignored file, '%c': failed file, %d files/line.",
                            SUCCESS_CHAR,
                            IGNORE_CHAR,
                            FAILURE_CHAR,
                            STATUS_CHAR_PR_LINE));

            MuxFileChannelCalculator muxFileChannelCalculator = new MuxFileChannelCalculator(
                    Thread.currentThread().getContextClassLoader().getResourceAsStream("muxChannels.csv"));

            FileObjectCreatorWorker fileObjectCreatorWorker =
                    new FileObjectCreatorWorker(data, muxFileChannelCalculator);

            ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors()*2);

            Long start = System.currentTimeMillis();

            forkJoinPool.invoke(fileObjectCreatorWorker);

            Long end = System.currentTimeMillis();

            System.out.println("Time taken: " + (end-start));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static CentralWebservice newWebservice() {
        try {
            CentralWebservice webservice = new DomsWebserviceFactory(config).getWebservice();
            return webservice;
        } catch (RuntimeException e) {
            System.err.println("Error communication with DOMS. Config: " + config);
            requestShutdown();
        }
        return null;
    }

    public static FFProbeLocationPropertyBasedDomsConfig getConfig() {
        return config;
    }

    public static synchronized void logSuccess(String data) {
        try {
            successWriter.write(data + "\n");
            successWriter.flush();
            logChar(SUCCESS_CHAR);
        } catch (IOException e) {
            System.out.println("OK: " + data);
        }
    }

    public static synchronized void logFailure(String data) {
        try {
            failureWriter.write(data + "\n");
            failureWriter.flush();
            logChar(FAILURE_CHAR);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed: " + data);
        }
    }

    public static synchronized void logIgnored(String data) {
        try {
            ignoreWriter.write(data + "\n");
            ignoreWriter.flush();
            logChar(IGNORE_CHAR);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ignored: " + data);
        }
    }

    public static synchronized void logNewUuid(String uuid) {
        try {
            newUuidWriter.write(uuid + "\n");
            newUuidWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("New: " + uuid);
        }
    }

    private static synchronized void logChar(char c) {
        System.out.print(c + incrementLogCounter());
    }

    private static synchronized String incrementLogCounter() {
        logCounter += 1;

        if (logCounter % 100 == 0) {
            return " " + logCounter + System.lineSeparator();
        }

        return "";
    }

    public static boolean permissionToRun() {
        return !shutdown;
    }

    public static void requestShutdown() {
        shutdown = true;
        log.info("Shutdown requested.");
    }
}
