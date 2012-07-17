package dk.statsbiblioteket.doms.transformers.fileenricher;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.transformers.common.DomsConfig;
import dk.statsbiblioteket.doms.transformers.common.DomsWebserviceFactory;
import dk.statsbiblioteket.doms.transformers.common.FileRecordingObjectListHandler;
import dk.statsbiblioteket.doms.transformers.common.ObjectHandler;
import dk.statsbiblioteket.doms.transformers.common.ObjectListHandler;
import dk.statsbiblioteket.doms.transformers.common.PropertyBasedDomsConfig;
import dk.statsbiblioteket.doms.transformers.common.TrivialUuidFileReader;
import dk.statsbiblioteket.doms.transformers.common.UuidFileReader;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Tool for enriching Radio/TV file metadata.
 * Takes as input a file with program uuids (one per line). For each file, enriches metadata.
 */
public class FileEnricher {
    public static void main(String[] args) throws IOException, JAXBException {
        //TODO: Setup apache CLI
        File uuidfile = new File(args[0]);
        File configfile = new File(args[1]);

        UuidFileReader uuidFileReader = new TrivialUuidFileReader();
        FileEnricherConfig config = new FFProbeLocationPropertyBasedDomsConfig(configfile);
        CentralWebservice webservice = new DomsWebserviceFactory(config).getWebservice();
        ObjectHandler delegate = new DomsFFProbeFileEnricherObjectHandler(config, webservice);
        ObjectHandler objectHandler = new DomsFileEnricherObjectHandler(config, webservice,delegate);


        ObjectListHandler objectListHandler = new FileRecordingObjectListHandler(config, objectHandler);

        List<String> uuids = uuidFileReader.readUuids(uuidfile);
        objectListHandler.transform(uuids);
    }

}
