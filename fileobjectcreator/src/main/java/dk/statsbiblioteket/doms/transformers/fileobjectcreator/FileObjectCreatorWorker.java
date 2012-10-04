package dk.statsbiblioteket.doms.transformers.fileobjectcreator;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.transformers.common.muxchannels.MuxFileChannelCalculator;


public class FileObjectCreatorWorker extends RecursiveAction {
    private static Logger log = LoggerFactory.getLogger(FileObjectCreatorWorker.class);

    private MuxFileChannelCalculator muxFileChannelCalculator;
    private List<String> data;

    public FileObjectCreatorWorker(List<String> data, MuxFileChannelCalculator muxFileChannelCalculator) {
        this.data = data;
        this.muxFileChannelCalculator = muxFileChannelCalculator;
    }

    @Override
    protected void compute() {
        if (data.size() == 1) {
            try {
                DomsObject domsObject = DomsFileParser.parse(data.get(0), muxFileChannelCalculator);
                if (domsObject != null) {
                    doWork(domsObject);
                } else {
                    FileObjectCreator.logIgnored(data.get(0));
                }
            } catch (ParseException e) {
                log.info("Error while parsing: " + data.get(0));
            }
        } else if (FileObjectCreator.permissionToRun()) {
            int center = data.size()/2;
            ForkJoinTask<Void> workerA = new FileObjectCreatorWorker(data.subList(0, center),
                        muxFileChannelCalculator);
            ForkJoinTask<Void> workerB = new FileObjectCreatorWorker(data.subList(center, data.size()),
                        muxFileChannelCalculator);
            invokeAll(workerA, workerB);
        }
    }

    public void doWork(DomsObject domsObject) {
        doWork(domsObject, "Batch-created by " + this.getClass().getName());
    }

    public static void doWork(DomsObject domsObject, String comment) {
        if (!FileObjectCreator.permissionToRun()) {
            return;
        }

        String uuid = null;
        if (validObject(domsObject)) {
            String output =
                    String.format("%s %s %s",
                            domsObject.getChecksum(),
                            domsObject.getSize(),
                            domsObject.getFileName());
            try {
                CentralWebservice webservice = FileObjectCreator.newWebservice();
                uuid = webservice.getFileObjectWithURL(domsObject.getPermanentUrl());
                if (uuid == null) {
                    uuid = webservice.newObject (
                            "doms:Template_RadioTVFile",
                            new ArrayList<String>(),
                            comment
                    );

                    webservice.addFileFromPermanentURL(
                            uuid,
                            domsObject.getFileName(),
                            null,
                            domsObject.getPermanentUrl(),
                            domsObject.getFormat(),
                            comment
                    );

                    FileObjectCreator.logSuccess(output + " (" + uuid + ")");
                    log.info("Created (" + uuid + "): " + output);
                    FileObjectCreator.logNewUuid(uuid);
                } else {
                    log.info("Already exists (" + uuid + "): " + output);
                }

            } catch (InvalidCredentialsException e) {
                FileObjectCreator.logFailure(output);
                log.error("Authentication-related error. Requesting shutdown..", e);
                FileObjectCreator.requestShutdown();
            } catch (InvalidResourceException e) {
                FileObjectCreator.logFailure(output);
                if (uuid == null) {
                    log.error("Inconsistent data, this really shouldn't happen: " +
                            "The most likely reason for this is that the template, \"doms:Template_RadioTVFile\", does not exist." +
                            "Requesting shutdown..");
                } else {
                    log.error("Inconsistent data, this really shouldn't happen: " +
                            "The most likely reason for this is that the object with uuid = \"" + uuid + "\" cannot be found." +
                            "Requesting shutdown..");
                }
                FileObjectCreator.requestShutdown();
            } catch (MethodFailedException e) {
                FileObjectCreator.logFailure(output);
                log.warn("Ingest of the following object failed: " + domsObject + "(uuid=\"" + uuid + "\")", e);
            } catch (Exception e) {
                log.error("Failed to enrich: " + uuid, e);
            }
        }
    }

    private static boolean validObject(DomsObject domsObject) {
        return domsObject != null && !anyNull(
                domsObject.getFileName(),
                domsObject.getChecksum(),
                domsObject.getFormat(),
                domsObject.getPermanentUrl());
    }

    private static boolean anyNull(Object... list) {
        for (Object o : list) {
            if (null == o) {
                return true;
            }
        }

        return false;
    }
}
