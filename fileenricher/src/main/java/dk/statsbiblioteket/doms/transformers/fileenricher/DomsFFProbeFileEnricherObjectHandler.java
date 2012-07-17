package dk.statsbiblioteket.doms.transformers.fileenricher;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.client.exceptions.NotFoundException;
import dk.statsbiblioteket.doms.transformers.common.DomsConfig;
import dk.statsbiblioteket.doms.transformers.common.ObjectHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 7/17/12
 * Time: 11:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class DomsFFProbeFileEnricherObjectHandler implements ObjectHandler{

    private final DomsConfig config;
    private final CentralWebservice webservice;

    private final File ffprobeDir;


    /**
     * Initialise object handler.
     * @param config Configuration.
     * @param webservice The DOMS WebService.
     */
    public DomsFFProbeFileEnricherObjectHandler(FileEnricherConfig config, CentralWebservice webservice){
        this.config = config;
        this.webservice = webservice;
        ffprobeDir = new File(config.getFFprobeFilesLocation());
    }



    @Override
    public void transform(String uuid) throws Exception {
        getFFProbeXml(uuid);
    }

    private  String getFFProbeXml(String uuid) throws InvalidCredentialsException, MethodFailedException, InvalidResourceException, IOException {
        String ffprobe;
        try {
            ffprobe = getFFProbeFromObject(uuid);
        } catch (NotFoundException e) {
            ffprobe = getFFProbeXMLFromFile(uuid);
            addFFProbeToObject(uuid,ffprobe);
        }
        return ffprobe;
    }

    private  String getFFProbeXMLFromFile(String uuid) throws IOException {
        File ffprobeFile = new File(ffprobeDir, uuid + ".stdout");
        String ffprobeContents = org.apache.commons.io.IOUtils.toString(new FileInputStream(ffprobeFile));
        ffprobeContents = ffprobeContents.substring(ffprobeContents.indexOf("<ffprobe"));
        return ffprobeContents;

    }

    private void addFFProbeToObject(String uuid, String ffprobe) throws InvalidCredentialsException, MethodFailedException, InvalidResourceException {
        webservice.modifyDatastream(uuid,"FFPROBE",ffprobe,"Adding ffprobe as part of the radio/tv datamodel upgrade");
    }

    private String getFFProbeFromObject(String uuid) throws NotFoundException, InvalidCredentialsException, MethodFailedException {
        try {
            String contents = webservice.getDatastreamContents(uuid, "FFPROBE");
            return contents;
        } catch (InvalidResourceException e) {
            throw new NotFoundException("Failed to retrieve FFPROBE datastream ",e);
        }

    }
}
