package dk.statsbiblioteket.doms.transformers.common;

import java.io.File;

public interface FFProbeLocationConfig {
    public String getFFprobeFilesLocation();
    public File getFFProbeFile(String fileName);
    public File getFFProbeErrorsFile(String fileName);
}
