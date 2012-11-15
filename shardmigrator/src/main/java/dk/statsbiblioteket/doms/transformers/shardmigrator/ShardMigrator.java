package dk.statsbiblioteket.doms.transformers.shardmigrator;

import dk.statsbiblioteket.doms.central.CentralWebservice;
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
import dk.statsbiblioteket.doms.transformers.common.callbacks.exceptions.CallbackException;
import dk.statsbiblioteket.doms.transformers.common.callbacks.exceptions.OutputWritingFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Tool for migrating Radio/TV metadata away from shards.
 * Takes as input a file with program uuids (one per line). For each program, resolves shards and files, and updates
 * metadata in program and file to make shard obsolete.
 * See the ShardRemover tool for removing shards once transformation of tools is complete.
 */
public class ShardMigrator {
    private static final Logger log = LoggerFactory.getLogger(ShardMigrator.class);
    public static void main(String[] args) throws IOException, CallbackException {
        //TODO: Setup apache CLI

        if (args.length == 2) {
            File configfile = new File(args[0]);
            File uuidfile = new File(args[1]);
            run(configfile, uuidfile);
        } else {
            System.out.println("bin/shardmigrator.sh config-file uuid-file");
            System.exit(1);
        }
    }

    public static void run(File configfile, File uuidfile) throws IOException, CallbackException {
        UuidFileReader uuidFileReader = new TrivialUuidFileReader();
        PropertyBasedDomsConfig config = new PropertyBasedDomsConfig(configfile);
        CentralWebservice webservice = new DomsWebserviceFactory(config).getWebservice();

        ObjectHandler objectHandler = new DomsShardMigratorObjectHandler(config, webservice);
        ObjectListHandler objectListHandler = new FileRecordingObjectListHandler(config, objectHandler);
        objectListHandler.addCallback(new OutputWriterCallback(config, objectHandler), OutputWritingFailedException.class);
        objectListHandler.addCallback(new StdoutDisplayCallback());
        objectListHandler.addCallback(new ExceptionLoggerCallback(log));
        System.out.println("created all the preliminaries");

        List<String> uuids = uuidFileReader.readUuids(uuidfile);
        try {
            objectListHandler.transform(uuids);
        } catch (OutputWritingFailedException e) {
            String msg = "Failed writing to output-file.";
            log.error(msg, e);
            System.err.println(msg);
        }
    }
}
