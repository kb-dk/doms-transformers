package dk.statsbiblioteket.doms.transformers.objectcreator;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.transformers.common.DomsConfig;
import dk.statsbiblioteket.doms.transformers.common.DomsWebserviceFactory;
import dk.statsbiblioteket.doms.transformers.common.PropertyBasedDomsConfig;
import dk.statsbiblioteket.doms.transformers.common.muxchannels.MuxFileChannelCalculator;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class FileObjectCreator {
    private static Logger log = LoggerFactory.getLogger(FileObjectCreator.class);
    private static BufferedWriter successWriter;
    private static BufferedWriter failureWriter;
    private static BufferedWriter ignoreWriter;
    private static PropertyBasedDomsConfig config;
    private static final int STATUS_CHAR_PR_LINE = 100;
    private static final char SUCCESS_CHAR = '+';
    private static final char FAILURE_CHAR = '#';
    private static final char IGNORE_CHAR = '.';
    private static int logCounter = 0;
    private static boolean shutdown = false;

    public static void main(String[] args) {
        try {
            BufferedReader uuidFileReader;
            if (args[0].equals("-")) {
                uuidFileReader = new BufferedReader(new InputStreamReader(System.in));
            } else {
                uuidFileReader = new BufferedReader(new FileReader(new File(args[0])));
            }

            System.out.println("Input file: " + args[0]);

            File configFile = new File(args[1]);
            config = new PropertyBasedDomsConfig(configFile);
            System.out.println(config);

            File successLog = new File("fileobjectcreator_successful-uuids");
            File failureLog = new File("fileobjectcreator_failed-uuids");
            File ignoreLog = new File("fileobjectcreator_ignored-files");

            if (successLog.exists()) {
                System.out.println("File already exists: " + successLog);
                System.exit(1);
            } else {
                successWriter = new BufferedWriter(new FileWriter(successLog));
            }

            if (failureLog.exists()) {
                System.out.println("File already exists: " + failureLog);
                System.exit(1);
            } else {
                failureWriter = new BufferedWriter(new FileWriter(failureLog));
            }

            if (ignoreLog.exists()) {
                System.out.println("File already exists: " + ignoreLog);
                System.exit(1);
            } else {
                ignoreWriter = new BufferedWriter(new FileWriter(ignoreLog));
            }


            DomsObject.setBaseUrl(config.getProperty("dk.statsbiblioteket.doms.transformers.baseurl", ""));

            new FileObjectCreator(config, uuidFileReader);

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + args[0]);
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static CentralWebservice newWebservice() {
        return new DomsWebserviceFactory(config).getWebservice();
    }

    public FileObjectCreator(DomsConfig config, BufferedReader reader) {
        CentralWebservice webservice = newWebservice();

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

            ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors()*16);

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
