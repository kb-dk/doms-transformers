package dk.statsbiblioteket.doms.transformers.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/** Configuration based on property file. */
public class PropertyBasedConfig implements Config {

    /** Property specifying the directory where output files will be stored. */
    public static final String OUTPUT_DIRECTORY_PROPERTY
            = "dk.statsbiblioteket.doms.transformers.outputdirectory";

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

    @Override
    public String getOutputDirectory() {
        return properties.getProperty(OUTPUT_DIRECTORY_PROPERTY, "output");
    }
}
