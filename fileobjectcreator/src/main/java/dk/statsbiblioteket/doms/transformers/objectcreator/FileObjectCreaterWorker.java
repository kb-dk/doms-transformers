package dk.statsbiblioteket.doms.transformers.objectcreator;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.transformers.common.muxchannels.MuxFileChannelCalculator;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;


public class FileObjectCreaterWorker extends RecursiveAction {
    private MuxFileChannelCalculator muxFileChannelCalculator;
    private List<String> data;

    public FileObjectCreaterWorker(List<String> data,
                                   MuxFileChannelCalculator muxFileChannelCalculator) {
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
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else {
            int center = data.size()/2;
            ForkJoinTask<Void> workerA = new FileObjectCreaterWorker(data.subList(0, center),           muxFileChannelCalculator);
            ForkJoinTask<Void> workerB = new FileObjectCreaterWorker(data.subList(center, data.size()), muxFileChannelCalculator);
            invokeAll(workerA, workerB);
        }
    }

    public void doWork(DomsObject domsObject) {
        doWork(domsObject, "Batch-created by " + this.getClass().getName());
    }

    public static void doWork(DomsObject domsObject, String comment) {
        if (validObject(domsObject)) {
            String output =
                    String.format("%s %s %s",
                            domsObject.getChecksum(),
                            domsObject.getSize(),
                            domsObject.getFileName());
            try {
                System.out.println(domsObject);
                CentralWebservice webservice = FileObjectCreator.newWebservice();
                String fileObjectWithURL = webservice.getFileObjectWithURL(domsObject.getPermanentUrl());
                if (fileObjectWithURL == null) {
                    String uuid = webservice.newObject (
                            "doms:Template_RadioTVFile",
                            new ArrayList<String>(),
                            comment
                    );

                    System.out.println(uuid);

                    webservice.addFileFromPermanentURL(
                            uuid,
                            domsObject.getFileName(),
                            //"DISABLED",
                            null,
                            domsObject.getPermanentUrl(),
                            domsObject.getFormat(),
                            comment
                    );

                    FileObjectCreator.logSuccess(output);
                }

            } catch (InvalidCredentialsException e) {
                FileObjectCreator.logFailure(output);
                e.printStackTrace();
            } catch (InvalidResourceException e) {
                FileObjectCreator.logFailure(output);
                e.printStackTrace();
            } catch (MethodFailedException e) {
                FileObjectCreator.logFailure(output);
                e.printStackTrace();
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
