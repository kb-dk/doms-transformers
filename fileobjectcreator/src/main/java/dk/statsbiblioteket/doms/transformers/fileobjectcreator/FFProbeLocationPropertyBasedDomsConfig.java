package dk.statsbiblioteket.doms.transformers.fileobjectcreator;

import dk.statsbiblioteket.doms.transformers.common.PropertyBasedDomsConfig;

import java.io.File;
import java.io.IOException;

public class FFProbeLocationPropertyBasedDomsConfig extends PropertyBasedDomsConfig implements FFProbeContainingConfig {

    private static final String FFPROBE_FILE_LOCATION_PROPERTY = "ffprobe.files.location";

    @Override
    public String getFFprobeFilesLocation() {
        return properties.getProperty(FFPROBE_FILE_LOCATION_PROPERTY,
                                      "/tmp/ffprobe.result/ffprobe.result/");
    }

    public FFProbeLocationPropertyBasedDomsConfig() {
        super();
    }

    public FFProbeLocationPropertyBasedDomsConfig(File configfile) throws IOException {
        super(configfile);
    }

    public String resolveFFProbeFileLocation(String fileName) {
        return getFFprobeFilesLocation() + System.getProperty("file.separator") + fileName;
    }

    public File getFFProbeFile(String fileName) {
        return new File(resolveFFProbeFileLocation(fileName + ".stdout"));
    }

    public File getFFProbeErrorsFile(String fileName) {
        return new File(resolveFFProbeFileLocation(fileName + ".stderr"));
    }
}
