package dk.statsbiblioteket.doms.transformers.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/** Configuration based on property file. */
public class PropertyBasedConfig implements Config {
    /** Property for file for recording success UUIDs. */
    public static final String SUCCESS_FILE_PROPERTY
            = "dk.statsbiblioteket.doms.transformers.successfile";
    /** Property for file for recording failure UUIDs. */
    public static final String FAILURE_FILE_PROPERTY
            = "dk.statsbiblioteket.doms.transformers.failurefile";

    /** Properties containing configuration. */
    protected final Properties properties;

    /** Initialise based on system properties. */
    public PropertyBasedConfig() {
        properties = new Properties(System.getProperties());
    }

    /**
     * Read configuration from property file. Backed by system properties.
     *
     * @param configfile File with properties.
     * @throws IOException If file can not be read.
     */
    public PropertyBasedConfig(File configfile) throws IOException {
        this();
        properties.load(new FileInputStream(configfile));
    }

    public String getProperty(String property) {
        return properties.getProperty(property);
    }

    public String getProperty(String property, String defaultValue) {
        return properties.getProperty(property, defaultValue);
    }

    @Override
    public String getSuccessFile() {
        return properties.getProperty(SUCCESS_FILE_PROPERTY, "success_uuids.txt");
    }

    @Override
    public String getFailureFile() {
        return properties.getProperty(FAILURE_FILE_PROPERTY, "failure_uuids.txt");
    }
}
