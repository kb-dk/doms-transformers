package dk.statsbiblioteket.doms.transformers.objectcreator;

import dk.statsbiblioteket.doms.central.CentralWebservice;

public class FileObjectCreatorRunnable implements Runnable {
    private final DomsObject domsObject;

    public FileObjectCreatorRunnable (DomsObject domsObject) {
        this.domsObject = domsObject;
    }

    @Override
    public void run() {
        FileObjectCreaterWorker.doWork(domsObject, "Batch-created by " + getClass().getSimpleName());
    }
}
