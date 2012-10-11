package dk.statsbiblioteket.doms.transformers.common;

/**
 * Configuration for shard migrator.
 */
public interface DomsConfig extends Config {
    /**
     * Get password for DOMS.
     * @return Password for DOMS.
     */
    String getDomsWebserviceUrl();
    /**
     * Get username for DOMS.
     * @return Username for DOMS.
     */
    String getDomsUsername();
    /**
     * Get URL for DOMS.
     * @return URL for DOMS.
     */
    String getDomsPassword();
}
