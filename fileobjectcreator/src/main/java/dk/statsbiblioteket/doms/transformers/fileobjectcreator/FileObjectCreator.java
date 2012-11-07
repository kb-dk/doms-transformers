package dk.statsbiblioteket.doms.transformers.fileobjectcreator;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.transformers.common.DomsWebserviceFactory;
import dk.statsbiblioteket.doms.transformers.common.muxchannels.MuxFileChannelCalculator;
import dk.statsbiblioteket.doms.transformers.fileenricher.FFProbeLocationPropertyBasedDomsConfig;
import dk.statsbiblioteket.util.FileAlreadyExistsException;
import jsr166y.ForkJoinPool;
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
import java.util.LinkedList;
import java.util.List;

public class FileObjectCreator {
    private static Logger log = LoggerFactory.getLogger(FileObjectCreator.class);
    private static final String baseName = "fileobjectcreator_";
    private static FFProbeLocationPropertyBasedDomsConfig config;

    private ResultWriter resultWriter;

    private static BufferedReader fileListReader = null;

    public static void main(String[] args) {
        File configFile = null;
        switch (args.length) {
            case 1:
                configFile = new File(args[0]);
                System.out.println("Reading data from stdin..");
                fileListReader = new BufferedReader(new InputStreamReader(System.in));
                run(configFile, fileListReader);
                break;
            case 2:
                configFile = new File(args[0]);
                System.out.println("Input file: " + args[1]);
                try {
                    fileListReader = new BufferedReader(new FileReader(new File(args[1])));
                    run(configFile, fileListReader);
                } catch (FileNotFoundException e) {
                    System.err.println("File not found: " + args[1]);
                    System.exit(1);
                }
                break;
            default:
                System.out.println("Usage: bin/fileobjectcreator.sh config-file [input-file]");
                System.exit(1);
        }
    }

    public static void run(File configFile, BufferedReader fileListReader) {
        if (!configFile.exists()) {
            System.out.println("Config file does not exist: " + config);
            System.exit(1);
        }

        if (!configFile.canRead()) {
            System.out.println("Could not read config file: " + config);
            System.exit(1);
        }

        try {
            config = new FFProbeLocationPropertyBasedDomsConfig(configFile);
            System.out.println(config);

            new FileObjectCreator(fileListReader);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public FileObjectCreator(BufferedReader reader) {
        try {
            resultWriter = new ResultWriter(baseName, new ResultWriterErrorHandler() {
                @Override
                public void handleError(Throwable t) {
                    requestShutdown();
                    t.printStackTrace();
                    System.err.println(String.format("Could not write to logfile, shutting down.."));
                }
            });
            resultWriter.setPrintProgress(true);

            List<String> data = new ArrayList<String>();
            String line;
            while((line = reader.readLine()) != null) {
                data.add(line);
            }

            System.out.println(resultWriter.getHelpMessage());

            MuxFileChannelCalculator muxFileChannelCalculator = new MuxFileChannelCalculator(
                    Thread.currentThread().getContextClassLoader().getResourceAsStream("muxChannels.csv"));

            String baseUrl = config.getProperty("dk.statsbiblioteket.doms.transformers.baseurl", "");
            if (baseUrl.isEmpty()) {
                log.warn("Empty base URL.");
            }

            FileObjectCreatorWorker fileObjectCreatorWorker =
                    new FileObjectCreatorWorker(config, resultWriter, baseUrl, data, muxFileChannelCalculator);

            ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors()*2);
            Long start = System.currentTimeMillis();
            forkJoinPool.invoke(fileObjectCreatorWorker);
            Long end = System.currentTimeMillis();
            System.out.println("Time taken: " + (end-start));

        } catch (FileAlreadyExistsException e) {
            System.exit(1);
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

    public static void requestShutdown() {
        FileObjectCreatorWorker.requestShutdown();
        log.info("Shutdown requested.");
    }
}
