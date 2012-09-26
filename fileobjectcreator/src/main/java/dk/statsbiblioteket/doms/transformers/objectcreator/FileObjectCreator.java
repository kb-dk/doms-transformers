package dk.statsbiblioteket.doms.transformers.objectcreator;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.transformers.common.DomsConfig;
import dk.statsbiblioteket.doms.transformers.common.DomsWebserviceFactory;
import dk.statsbiblioteket.doms.transformers.common.PropertyBasedDomsConfig;
import dk.statsbiblioteket.doms.transformers.common.checksums.ChecksumParser;
import dk.statsbiblioteket.doms.transformers.common.muxchannels.MuxFileChannelCalculator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;

public class FileObjectCreator {
    public static void main(String[] args) {
        try {
            BufferedReader uuidFileReader;
            if (args[0].equals("-")) {
                uuidFileReader = new BufferedReader(new InputStreamReader(System.in));
            } else {
                uuidFileReader = new BufferedReader(new FileReader(new File(args[0])));
            }

            File configFile = new File(args[1]);
            DomsConfig config = new PropertyBasedDomsConfig(configFile);
            System.out.println(config);

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

            ChecksumParser checksumParser = new ChecksumParser(Thread.currentThread().getContextClassLoader().getResourceAsStream("md5s.zip"));

            DomsFileParser domsFileParser = new DomsFileParser(reader, checksumParser.getNameChecksumsMap(), muxChannelCalculator);

            for (DomsObject domsObject : domsFileParser) {
                System.out.println(domsObject);

                try {
                    String response = webservice.createFileObject(
                            "doms:Template_RadioTVFile",
                            domsObject.getFileName(),
                            domsObject.getChecksum(),
                            domsObject.getPermanentUrl(),
                            domsObject.getFormat(),
                            "Batch-created by " + this.getClass().getName() // FIXME
                    );
                    System.out.println(response);

                } catch (InvalidCredentialsException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (InvalidResourceException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (MethodFailedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }


        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
