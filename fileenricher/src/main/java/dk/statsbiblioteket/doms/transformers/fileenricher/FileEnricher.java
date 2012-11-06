package dk.statsbiblioteket.doms.transformers.fileenricher;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.transformers.common.DomsWebserviceFactory;
import dk.statsbiblioteket.doms.transformers.common.FileRecordingObjectListHandler;
import dk.statsbiblioteket.doms.transformers.common.ObjectHandler;
import dk.statsbiblioteket.doms.transformers.common.ObjectListHandler;
import dk.statsbiblioteket.doms.transformers.common.TrivialUuidFileReader;
import dk.statsbiblioteket.doms.transformers.common.checksums.ChecksumParser;

import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;

/**
 * Tool for enriching Radio/TV file metadata.
 * Takes as input a file with program uuids (one per line). For each file, enriches metadata.
 */
public class FileEnricher {
    public static void main(String[] args) throws IOException, JAXBException, URISyntaxException, ParseException {
        //TODO: Setup apache CLI

        File configFile;
        ChecksumParser checksumParser;
        List<String> uuids;
        TrivialUuidFileReader uuidFileReader = new TrivialUuidFileReader();

        switch (args.length) {
            case 2:
                configFile = new File(args[0]);
                checksumParser = new ChecksumParser(new BufferedReader(new FileReader(new File(args[1]))));
                System.out.println("Reading uuids from stdin..");
                uuids = uuidFileReader.readUuids(new BufferedReader(new InputStreamReader(System.in)));
                run(configFile, checksumParser, uuids);
                break;
            case 3:
                configFile = new File(args[0]);
                checksumParser = new ChecksumParser(new BufferedReader(new FileReader(new File(args[1]))));
                System.out.println("Reading uuids from " + args[1]);
                File uuidfile = new File(args[2]);
                uuids = uuidFileReader.readUuids(uuidfile);
                run(configFile, checksumParser, uuids);
                break;
            default:
                System.out.println("Usage: bin/fileenricher.sh config-file checksum-file [uuid-file]");
                System.exit(1);
        }
    }

    private static void run(File configFile, ChecksumParser checksumParser, List<String> uuids) throws IOException, JAXBException, URISyntaxException, ParseException {
        FileEnricherConfig config = new FFProbeLocationPropertyBasedDomsConfig(configFile);
        CentralWebservice webservice = new DomsWebserviceFactory(config).getWebservice();

        ObjectHandler delegate = new DomsFFProbeFileEnricherObjectHandler(config, webservice);
        ObjectHandler objectHandler = new DomsFileEnricherObjectHandler(config, webservice, checksumParser.getNameChecksumsMap(), delegate);

        ObjectListHandler objectListHandler = new FileRecordingObjectListHandler(config, objectHandler);
        objectListHandler.transform(uuids);
    }
}
