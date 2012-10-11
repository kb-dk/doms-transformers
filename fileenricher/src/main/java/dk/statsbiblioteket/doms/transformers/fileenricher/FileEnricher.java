package dk.statsbiblioteket.doms.transformers.fileenricher;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.transformers.common.DomsWebserviceFactory;
import dk.statsbiblioteket.doms.transformers.common.FileRecordingObjectListHandler;
import dk.statsbiblioteket.doms.transformers.common.ObjectHandler;
import dk.statsbiblioteket.doms.transformers.common.ObjectListHandler;
import dk.statsbiblioteket.doms.transformers.common.TrivialUuidFileReader;
import dk.statsbiblioteket.doms.transformers.common.UuidFileReader;
import dk.statsbiblioteket.doms.transformers.common.checksums.ChecksumParser;

import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.List;

/**
 * Tool for enriching Radio/TV file metadata.
 * Takes as input a file with program uuids (one per line). For each file, enriches metadata.
 */
public class FileEnricher {
    public static void main(String[] args) throws IOException, JAXBException, URISyntaxException, ParseException {
        //TODO: Setup apache CLI
        File uuidfile = new File(args[0]);

        File configFile = null;

        try {
            URL resource = Thread.currentThread().getContextClassLoader().getResource("fileenricher.properties");
            URI uri = resource.toURI();
            configFile = new File(uri);
        } catch (Exception e) {
            System.err.println("fileenricher.properties not found, try putting it in 'conf'.");
            System.exit(1);
        }

        UuidFileReader uuidFileReader = new TrivialUuidFileReader();
        FileEnricherConfig config = new FFProbeLocationPropertyBasedDomsConfig(configFile);
        CentralWebservice webservice = new DomsWebserviceFactory(config).getWebservice();

        ChecksumParser checksums = new ChecksumParser(new BufferedReader(new InputStreamReader(System.in)));

        ObjectHandler delegate = new DomsFFProbeFileEnricherObjectHandler(config, webservice);
        ObjectHandler objectHandler = new DomsFileEnricherObjectHandler(config, webservice,checksums.getNameChecksumsMap(), delegate);


        ObjectListHandler objectListHandler = new FileRecordingObjectListHandler(config, objectHandler);

        List<String> uuids = uuidFileReader.readUuids(uuidfile);
        objectListHandler.transform(uuids);
    }

}
