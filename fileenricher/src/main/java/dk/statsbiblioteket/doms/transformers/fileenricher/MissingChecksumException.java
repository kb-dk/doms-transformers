package dk.statsbiblioteket.doms.transformers.fileenricher;

public class MissingChecksumException extends Exception {
    public MissingChecksumException(String filename) {
        super(filename);
    }
}
