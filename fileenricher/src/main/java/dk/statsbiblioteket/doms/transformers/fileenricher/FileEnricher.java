package dk.statsbiblioteket.doms.transformers.fileenricher;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;

import javax.xml.bind.JAXBException;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.transformers.common.DomsWebserviceFactory;
import dk.statsbiblioteket.doms.transformers.common.FileRecordingObjectListHandler;
import dk.statsbiblioteket.doms.transformers.common.ObjectHandler;
import dk.statsbiblioteket.doms.transformers.common.ObjectListHandler;
import dk.statsbiblioteket.doms.transformers.common.TrivialUuidFileReader;
import dk.statsbiblioteket.doms.transformers.common.UuidFileReader;
import dk.statsbiblioteket.doms.transformers.common.checksums.ChecksumParser;

/**
 * Tool for enriching Radio/TV file metadata.
 * Takes as input a file with program uuids (one per line). For each file, enriches metadata.
 */
public class FileEnricher {
    public static void main(String[] args) throws IOException, JAXBException, URISyntaxException, ParseException {
        //TODO: Setup apache CLI
        File uuidfile = new File(Thread.currentThread().getContextClassLoader().getResource(args[0]).toURI());
        File configfile = new File(Thread.currentThread().getContextClassLoader().getResource(args[1]).toURI());

        UuidFileReader uuidFileReader = new TrivialUuidFileReader();
        FileEnricherConfig config = new FFProbeLocationPropertyBasedDomsConfig(configfile);
        CentralWebservice webservice = new DomsWebserviceFactory(config).getWebservice();

        ChecksumParser checksums = new ChecksumParser(Thread.currentThread().getContextClassLoader().getResourceAsStream("md5s.zip"));

        ObjectHandler delegate = new DomsFFProbeFileEnricherObjectHandler(config,webservice);
        ObjectHandler objectHandler = new DomsFileEnricherObjectHandler(config, webservice,checksums,delegate);


        ObjectListHandler objectListHandler = new FileRecordingObjectListHandler(config, objectHandler);

        List<String> uuids = uuidFileReader.readUuids(uuidfile);
        objectListHandler.transform(uuids);
    }

}
