package dk.statsbiblioteket.doms.transformers.objectcreator;

import dk.statsbiblioteket.doms.central.CentralWebservice;

public class FileObjectCreatorRunnable implements Runnable {
    private final DomsObject domsObject;
    private final CentralWebservice webservice;

    public FileObjectCreatorRunnable (DomsObject domsObject, CentralWebservice webservice) {
        this.domsObject = domsObject;
        this.webservice = webservice;
    }

    @Override
    public void run() {
        FileObjectCreaterWorker.doWork(domsObject, webservice, "Batch-created by " + getClass().getSimpleName());
    }
}
