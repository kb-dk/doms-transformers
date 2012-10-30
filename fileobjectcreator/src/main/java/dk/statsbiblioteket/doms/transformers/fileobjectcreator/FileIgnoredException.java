package dk.statsbiblioteket.doms.transformers.fileobjectcreator;

public class FileIgnoredException extends Throwable {
    private String filename;

    public FileIgnoredException(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}
