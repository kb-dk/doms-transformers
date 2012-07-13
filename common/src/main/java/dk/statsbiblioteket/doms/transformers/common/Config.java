package dk.statsbiblioteket.doms.transformers.common;

/**
 * Configuration.
 */
public interface Config {
    /**
     * Get file with full path for reporting success.
     * @return The file for success uuids.
     */
    String getSuccessFile();

    /**
     * Get file with full path for reporting failure.
     * @return The file for success uuids.
     */
    String getFailureFile();
}
