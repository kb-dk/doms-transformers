package dk.statsbiblioteket.doms.transformers.faultyvhstimestampsfixer;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.transformers.common.DomsConfig;
import dk.statsbiblioteket.doms.transformers.common.DomsWebserviceFactory;
import dk.statsbiblioteket.doms.transformers.common.FileRecordingObjectListHandler;
import dk.statsbiblioteket.doms.transformers.common.ObjectHandler;
import dk.statsbiblioteket.doms.transformers.common.ObjectListHandler;
import dk.statsbiblioteket.doms.transformers.common.PropertyBasedDomsConfig;
import dk.statsbiblioteket.doms.transformers.common.TrivialUuidFileReader;
import dk.statsbiblioteket.doms.transformers.common.UuidFileReader;
import dk.statsbiblioteket.doms.transformers.common.callbacks.ExceptionLoggerCallback;
import dk.statsbiblioteket.doms.transformers.common.callbacks.OutputWriterCallback;
import dk.statsbiblioteket.doms.transformers.common.callbacks.StdoutDisplayCallback;
import dk.statsbiblioteket.doms.transformers.common.callbacks.exceptions.OutputWritingFailedException;
import dk.statsbiblioteket.doms.transformers.common.callbacks.exceptions.StopExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;

/**
 * Tool for fixing wrong timestamps for VHS files in Radio/TV metadata.
 * Takes as input a file with vhs file object uuids (one per line). For each file, fixes date metadata.
 */
public class FaultyVhsTimestampsFixer {
    private static final Logger log = LoggerFactory.getLogger(FaultyVhsTimestampsFixer.class);

    public static void main(String[] args) throws IOException, StopExecutionException, JAXBException, URISyntaxException, ParseException {
        //TODO: Setup apache CLI

        File configFile;

        List<String> uuids;
        UuidFileReader uuidFileReader = new TrivialUuidFileReader();

        switch (args.length) {
            case 1:
                configFile = new File(args[0]);
                System.out.println("Reading uuids from stdin..");
                uuids = uuidFileReader.readUuids(new BufferedReader(new InputStreamReader(System.in)));
                run(configFile, uuids);
                break;
            case 2:
                configFile = new File(args[0]);
                System.out.println("Reading uuids from " + args[1]);
                File uuidfile = new File(args[1]);
                uuids = uuidFileReader.readUuids(uuidfile);
                run(configFile, uuids);
                break;
            default:
                System.out.println("Usage: bin/faultyvhstimestampsfixer.sh config-file [uuid-file]");
                System.exit(1);
        }
    }

    private static void run(File configFile,  List<String> uuids) throws IOException, StopExecutionException, JAXBException, URISyntaxException, ParseException {
        DomsConfig config = new PropertyBasedDomsConfig(configFile);
        CentralWebservice webservice = new DomsWebserviceFactory(config).getWebservice();

        ObjectHandler objectHandler = new DomsFaultyVhsTimestampsFixerObjectHandler(config, webservice );
        ObjectListHandler objectListHandler = new FileRecordingObjectListHandler(config, objectHandler);
        objectListHandler.addCallback(new OutputWriterCallback(config, objectHandler), OutputWritingFailedException.class);
        objectListHandler.addCallback(new StdoutDisplayCallback());
        objectListHandler.addCallback(new ExceptionLoggerCallback(log));
        try {
            objectListHandler.transform(uuids);
        } catch (OutputWritingFailedException e) {
            String msg = "Failed writing to output-file.";
            log.error(msg, e);
            System.err.println(msg);
        }
    }
}
