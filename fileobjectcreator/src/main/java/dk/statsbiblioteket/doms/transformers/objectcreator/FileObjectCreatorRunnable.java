package dk.statsbiblioteket.doms.transformers.objectcreator;

public class FileObjectCreatorRunnable implements Runnable {
    private final DomsObject domsObject;

    public FileObjectCreatorRunnable (DomsObject domsObject) {
        this.domsObject = domsObject;
    }

    @Override
    public void run() {
        FileObjectCreatorWorker.doWork(domsObject, "Batch-created by " + getClass().getSimpleName());
    }
}
