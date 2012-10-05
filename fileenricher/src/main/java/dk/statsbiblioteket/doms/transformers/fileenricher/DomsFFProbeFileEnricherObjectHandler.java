package dk.statsbiblioteket.doms.transformers.fileenricher;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.client.exceptions.NotFoundException;
import dk.statsbiblioteket.doms.transformers.common.DomsConfig;
import dk.statsbiblioteket.doms.transformers.common.FileRecordingObjectListHandler;
import dk.statsbiblioteket.doms.transformers.common.ObjectHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    private Logger log = LoggerFactory.getLogger("DomsFFProbeFileEnricherObjectHandler");


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
        try {
            getFFProbeXml(uuid);
        } catch (FileNotFoundException e) {
            // error is logged in the function call
        }
    }

    private  String getFFProbeXml(String uuid) throws InvalidCredentialsException, MethodFailedException, InvalidResourceException, IOException {
        String ffprobe;
        String ffprobeErrors;
        String baseFileName = getFFProbeBaseName(uuid);
        try {
            ffprobe = getFFProbeFromObject(uuid);
            log.info(String.format("ffprobe data for %s already exists, not updating.", uuid));
        } catch (NotFoundException e) {
            ffprobe = getFFProbeXMLFromFile(baseFileName);
            addFFProbeToObject(uuid, ffprobe);
        }

        try {
            throw new NotFoundException("foo");
            //ffprobeErrors = getFFProbeErrorsFromObject(uuid);
            //log.info(String.format("ffprobe error data for %s already exists, not updating.", uuid));
        } catch (NotFoundException e) {
            ffprobeErrors = getFFProbeErrorsXMLFromFile(baseFileName);
            addFFProbeToObject(uuid, ffprobeErrors);
        }

        return ffprobe;
    }

    private  String getFFProbeXMLFromFile(String uuid) throws IOException {
        File ffprobeFile = new File(ffprobeDir, uuid + ".stdout");
        String ffprobeContents = org.apache.commons.io.IOUtils.toString(new FileInputStream(ffprobeFile));
        ffprobeContents = ffprobeContents.substring(ffprobeContents.indexOf("<ffprobe"));
        return ffprobeContents;

    }

    private  String getFFProbeXMLFromFile(File file) throws IOException {
        File ffprobeFile = new File(file + ".stdout");
        String ffprobeContents = org.apache.commons.io.IOUtils.toString(new FileInputStream(ffprobeFile));
        ffprobeContents = ffprobeContents.substring(ffprobeContents.indexOf("<ffprobe"));
        return ffprobeContents;

    }

    private  String getFFProbeErrorsXMLFromFile(String file) throws IOException {
        File ffprobeErrorFile = new File(file + ".stderr");
        String ffprobeContents = org.apache.commons.io.IOUtils.toString(new FileInputStream(ffprobeErrorFile));

        return "<ffprobe:ffprobeStdErrorOutput \n" +
                "xmlns:ffprobe='http://www.ffmpeg.org/schema/ffprobe'><![CDATA[\n" +
                ffprobeContents +
                "]]></ffprobe:ffprobeStdErrorOutput>";
    }

    private void addFFProbeToObject(String uuid, String ffprobe) throws InvalidCredentialsException, MethodFailedException, InvalidResourceException {
        webservice.modifyDatastream(uuid,"FFPROBE",ffprobe,"Adding ffprobe as part of the radio/tv datamodel upgrade");
    }

    private void addFFProbeErrorsToObject(String uuid, String ffprobe) throws InvalidCredentialsException, MethodFailedException, InvalidResourceException {
        webservice.modifyDatastream(uuid,"FFPROBE_ERROR_LOG", ffprobe, "Adding ffprobe errors as part of the radio/tv datamodel upgrade");
    }

    private String getFFProbeFromObject(String uuid) throws NotFoundException, InvalidCredentialsException, MethodFailedException {
        try {
            String contents = webservice.getDatastreamContents(uuid, "FFPROBE");
            return contents;
        } catch (InvalidResourceException e) {
            throw new NotFoundException("Failed to retrieve FFPROBE datastream ",e);
        }

    }

    private String getFFProbeErrorsFromObject(String uuid) throws NotFoundException, InvalidCredentialsException, MethodFailedException {
        try {
            String contents = webservice.getDatastreamContents(uuid, "FFPROBE_ERROR_LOG");
            return contents;
        } catch (InvalidResourceException e) {
            throw new NotFoundException("Failed to retrieve FFPROBE_ERROR_LOG datastream ",e);
        }

    }

    private String getFFProbeBaseName(String uuid) throws InvalidCredentialsException, MethodFailedException, InvalidResourceException {
        String url = webservice.getObjectProfile(uuid).getTitle();
        if (url != null && url.contains(".")) {
            String[] parts = url.split("/");
            return ffprobeDir + parts[parts.length-1];
        } else {
            return null;
        }
    }
}
