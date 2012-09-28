package dk.statsbiblioteket.doms.transformers.objectcreator;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;

import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;


public class FileObjectCreaterWorker extends RecursiveAction {
    private DomsFileParserIterator domsFileParserIterator;
    private CentralWebservice webservice;

    public FileObjectCreaterWorker(DomsFileParserIterator domsFileParserIterator, CentralWebservice webservice) {
        this.domsFileParserIterator = domsFileParserIterator;
        this.webservice = webservice;
    }

    @Override
    protected void compute() {
        DomsObject domsObject = domsFileParserIterator.next();
        ForkJoinTask<Void> worker = null;

        if (domsObject != null) {
            worker = new FileObjectCreaterWorker(domsFileParserIterator, webservice).fork();
        }

        doWork(domsObject);

        if (worker != null) {
            worker.join();
        }
    }

    private void doWork(DomsObject domsObject) {
        try {
            if (validObject(domsObject)) {
                System.out.println(domsObject);
                String response = webservice.createFileObject(
                        "doms:Template_RadioTVFile",
                        domsObject.getFileName(),
                        domsObject.getChecksum(),
                        domsObject.getPermanentUrl(),
                        domsObject.getFormat(),
                        "Batch-created by " + this.getClass().getName() // FIXME
                );
                System.out.println(response);
            }

        } catch (InvalidCredentialsException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidResourceException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MethodFailedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private boolean validObject(DomsObject domsObject) {
        return domsObject != null && !anyNull(
                domsObject.getFileName(),
                domsObject.getChecksum(),
                domsObject.getFormat(),
                domsObject.getPermanentUrl());
    }

    private boolean anyNull(Object... list) {
        for (Object o : list) {
            if (null == o) {
                return true;
            }
        }

        return false;
    }
}
