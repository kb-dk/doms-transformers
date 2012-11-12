package dk.statsbiblioteket.doms.transformers.fileobjectcreator;

import java.io.File;
import java.io.IOException;

public class FileObjectCreatorConfig extends FFProbeLocationPropertyBasedDomsConfig {
    public static final String DOMS_BASE_URL_PROPERTY = "dk.statsbiblioteket.doms.transformers.baseurl";

    public FileObjectCreatorConfig() {
        super();
    }

    public FileObjectCreatorConfig(File configfile) throws IOException {
        super(configfile);
    }

    public String getDomsBaseUrl() {
        return properties.getProperty(DOMS_BASE_URL_PROPERTY);
    }
}
