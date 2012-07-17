package dk.statsbiblioteket.doms.transformers.fileenricher;

import dk.statsbiblioteket.doms.transformers.common.PropertyBasedDomsConfig;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 7/17/12
 * Time: 3:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class FFProbeLocationPropertyBasedDomsConfig extends PropertyBasedDomsConfig implements FileEnricherConfig{

    private static final String FFPROBE_FILE_LOCATION = "ffprobe.files.location";

     @Override
    public String getFFprobeFilesLocation() {
        return properties.getProperty(FFPROBE_FILE_LOCATION,
                                      "/home/abr/Downloads/ffprobe.result/ffprobe.result/");
    }

    public FFProbeLocationPropertyBasedDomsConfig() {
    }

    public FFProbeLocationPropertyBasedDomsConfig(File configfile) throws IOException {
        super(configfile);
    }
}
