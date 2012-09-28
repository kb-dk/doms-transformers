package dk.statsbiblioteket.doms.transformers.objectcreator;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.transformers.common.DomsConfig;
import dk.statsbiblioteket.doms.transformers.common.DomsWebserviceFactory;
import dk.statsbiblioteket.doms.transformers.common.PropertyBasedDomsConfig;
import dk.statsbiblioteket.doms.transformers.common.muxchannels.MuxFileChannelCalculator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.concurrent.ForkJoinPool;

public class FileObjectCreator {
    private static BufferedWriter successWriter;
    private static BufferedWriter failureWriter;

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
            DomsConfig config = new PropertyBasedDomsConfig(configFile);
            System.out.println(config);

            File successLog = new File("fileobjectcreator_successful-uuids");
            File failureLog = new File("fileobjectcreator_failed-uuids");

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

    public FileObjectCreator(DomsConfig config, BufferedReader reader) {
        CentralWebservice webservice = new DomsWebserviceFactory(config).getWebservice();

        try {
            MuxFileChannelCalculator muxChannelCalculator = new MuxFileChannelCalculator(
                    Thread.currentThread().getContextClassLoader().getResourceAsStream("muxChannels.csv"));

            DomsFileParser domsFileParser = new DomsFileParser(reader, muxChannelCalculator);

            FileObjectCreaterWorker fileObjectCreaterWorker =
                    new FileObjectCreaterWorker((DomsFileParserIterator) domsFileParser.iterator(), webservice);

            ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors()*16);

            Long start = System.currentTimeMillis();

            forkJoinPool.invoke(fileObjectCreaterWorker);

            Long end = System.currentTimeMillis();

            System.out.println("Time taken: " + (end-start));

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static synchronized void logSuccess(String data) {
        try {
            successWriter.write(data + "\n");
            successWriter.flush();
        } catch (IOException e) {
            System.out.println("OK: " + data);
        }
    }

    public static synchronized void logFailure(String data) {
        try {
            failureWriter.write(data + "\n");
            successWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.out.println("Failed: " + data);
        }
    }
}
