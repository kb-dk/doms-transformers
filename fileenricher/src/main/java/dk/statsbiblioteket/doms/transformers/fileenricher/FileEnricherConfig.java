package dk.statsbiblioteket.doms.transformers.fileenricher;

import dk.statsbiblioteket.doms.transformers.common.DomsConfig;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 7/17/12
 * Time: 3:39 PM
 * To change this template use File | Settings | File Templates.
 */
public interface FileEnricherConfig extends DomsConfig{

    public String getFFprobeFilesLocation();


}
