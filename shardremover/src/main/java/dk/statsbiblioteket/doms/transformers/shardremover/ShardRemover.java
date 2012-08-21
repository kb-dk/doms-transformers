package dk.statsbiblioteket.doms.transformers.shardremover;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.transformers.common.DomsConfig;
import dk.statsbiblioteket.doms.transformers.common.DomsWebserviceFactory;
import dk.statsbiblioteket.doms.transformers.common.FileRecordingObjectListHandler;
import dk.statsbiblioteket.doms.transformers.common.ObjectHandler;
import dk.statsbiblioteket.doms.transformers.common.ObjectListHandler;
import dk.statsbiblioteket.doms.transformers.common.PropertyBasedDomsConfig;
import dk.statsbiblioteket.doms.transformers.common.TrivialUuidFileReader;
import dk.statsbiblioteket.doms.transformers.common.UuidFileReader;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Tool for removing Radio/TV shards.
 * Takes as input a file with program uuids (one per line). For each program, resolves shards and adds the
 * shard UUID as extra ID for this objects, then deletes the reference to the shard object and the shard object.
 * See the ShardMigrator tool for moving metadata from shards to programs first.
 */
public class ShardRemover {
    public static void main(String[] args) throws IOException, URISyntaxException {
        //TODO: Setup apache CLI
        File uuidfile = new File(Thread.currentThread().getContextClassLoader().getResource(args[0]).toURI());
        File configfile = new File(Thread.currentThread().getContextClassLoader().getResource(args[1]).toURI());

        UuidFileReader uuidFileReader = new TrivialUuidFileReader();
        DomsConfig config = new PropertyBasedDomsConfig(configfile);
        CentralWebservice webservice = new DomsWebserviceFactory(config).getWebservice();
        ObjectHandler objectHandler = new DomsShardRemoverObjectHandler(config, webservice);
        ObjectListHandler objectListHandler = new FileRecordingObjectListHandler(config, objectHandler);

        List<String> uuids = uuidFileReader.readUuids(uuidfile);
        objectListHandler.transform(uuids);
    }

}
