package dk.statsbiblioteket.doms.transformers.fileenricher;

import dk.statsbiblioteket.doms.transformers.common.PropertyBasedDomsConfig;

import java.io.File;
import java.io.IOException;

public class FFProbeLocationPropertyBasedDomsConfig extends PropertyBasedDomsConfig implements FileEnricherConfig{

    private static final String FFPROBE_FILE_LOCATION = "ffprobe.files.location";

    @Override
    public String getFFprobeFilesLocation() {
        return properties.getProperty(FFPROBE_FILE_LOCATION,
                                      "/tmp/ffprobe.result/ffprobe.result/");
    }

    public FFProbeLocationPropertyBasedDomsConfig() {
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
