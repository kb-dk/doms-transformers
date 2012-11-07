package dk.statsbiblioteket.doms.transformers.fileenricher;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.client.exceptions.NotFoundException;
import dk.statsbiblioteket.doms.transformers.common.DomsConfig;
import dk.statsbiblioteket.doms.transformers.common.FileRecordingObjectListHandler;
import dk.statsbiblioteket.doms.transformers.common.MigrationStatus;
import dk.statsbiblioteket.doms.transformers.fileobjectcreator.FFProbeContainingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FFProbeEnricher {

    private final DomsConfig config;
    private final CentralWebservice webservice;

    private final String ffprobeDir;

    private static final Logger log = LoggerFactory.getLogger(FFProbeEnricher.class);
    private static final Logger ffProbeLog = LoggerFactory.getLogger("ffprobe");


    /**
     * Initialise object handler.
     * @param config Configuration.
     * @param webservice The DOMS WebService.
     */
    public FFProbeEnricher(FFProbeContainingConfig config, CentralWebservice webservice){
        this.config = config;
        this.webservice = webservice;
        ffprobeDir = config.getFFprobeFilesLocation();
    }


    public MigrationStatus transform(String uuid) throws Exception {
        try {
            getFFProbeXml(uuid);
            return MigrationStatus.COMPLETE;
        } catch (FileNotFoundException e) {
            ffProbeLog.error("Missing ffprobe data for '{}'", uuid, e);
            return MigrationStatus.FAILED;
        }
    }

    private String getFFProbeXml(String uuid) throws InvalidCredentialsException, MethodFailedException, InvalidResourceException, IOException {
        String ffprobe;
        String ffprobeErrors;
        String incompleteFilePath = getFFProbeBaseName(uuid);
        String stdoutFilePath = incompleteFilePath + ".stdout";
        String stderrFilePath = incompleteFilePath + ".stderr";

        try {
            ffprobe = getFFProbeFromObject(uuid);
            log.info(String.format("ffprobe data for %s already exists, not updating.", uuid));
        } catch (NotFoundException e) {
            ffprobe = getFFProbeXMLFromFileName(stdoutFilePath);
            addFFProbeToObject(uuid, ffprobe);
        }

        try {
            ffprobeErrors = getFFProbeErrorsFromObject(uuid);
            log.info(String.format("ffprobe error data for %s already exists, not updating.", uuid));
        } catch (NotFoundException e) {
            ffprobeErrors = getFFProbeErrorsXMLFromFileName(stderrFilePath);
            addFFProbeErrorsToObject(uuid, ffprobeErrors);
        }

        return ffprobe;
    }

    private String getFFProbeXMLFromFile(String uuid) throws IOException {
        File ffprobeFile = new File(ffprobeDir, uuid + ".stdout");
        String ffprobeContents = org.apache.commons.io.IOUtils.toString(new FileInputStream(ffprobeFile));
        ffprobeContents = ffprobeContents.substring(ffprobeContents.indexOf("<ffprobe"));
        return ffprobeContents;
    }

    private void addFFProbeToObject(String uuid, String ffprobe) throws InvalidCredentialsException, MethodFailedException, InvalidResourceException {
        webservice.modifyDatastream(uuid, BroadcastMetadataEnricher.FFPROBE_DATASTREAM_NAME, ffprobe, "Adding ffprobe as part of the radio/tv datamodel upgrade");
    }

    private void addFFProbeErrorsToObject(String uuid, String ffprobeErrors) throws InvalidCredentialsException, MethodFailedException, InvalidResourceException {
        webservice.modifyDatastream(uuid, BroadcastMetadataEnricher.FFPROBE_ERRORS_DATASTREAM_NAME, ffprobeErrors, "Adding ffprobe errors as part of the radio/tv datamodel upgrade");
    }

    private String getFFProbeFromObject(String uuid) throws NotFoundException, InvalidCredentialsException, MethodFailedException {
        try {
            String contents = webservice.getDatastreamContents(uuid, BroadcastMetadataEnricher.FFPROBE_DATASTREAM_NAME);
            return contents;
        } catch (InvalidResourceException e) {
            throw new NotFoundException("Failed to retrieve " + BroadcastMetadataEnricher.FFPROBE_DATASTREAM_NAME + " datastream ",e);
        }

    }

    private String getFFProbeXMLFromFileName(String fileName) throws IOException {
        File ffprobeFile = new File(fileName);
        String ffprobeContents = org.apache.commons.io.IOUtils.toString(new FileInputStream(ffprobeFile));
        ffprobeContents = ffprobeContents.substring(ffprobeContents.indexOf("<ffprobe"));
        return ffprobeContents;

    }

    private String getFFProbeErrorsFromObject(String uuid) throws NotFoundException, InvalidCredentialsException, MethodFailedException {
        try {
            String contents = webservice.getDatastreamContents(uuid, BroadcastMetadataEnricher.FFPROBE_ERRORS_DATASTREAM_NAME);
            return contents;
        } catch (InvalidResourceException e) {
            throw new NotFoundException("Failed to retrieve " + BroadcastMetadataEnricher.FFPROBE_ERRORS_DATASTREAM_NAME + " datastream ", e);
        }

    }

    private String getFFProbeErrorsXMLFromFileName(String fileName) throws IOException {
        File ffprobeErrorFile = new File(fileName);
        String ffprobeContents = org.apache.commons.io.IOUtils.toString(new FileInputStream(ffprobeErrorFile));

        String data = "<ffprobe:ffprobeStdErrorOutput xmlns:ffprobe=\"http://www.ffmpeg.org/schema/ffprobe/stderr\"><![CDATA[\n" +
                ffprobeContents +
                "]]></ffprobe:ffprobeStdErrorOutput>";

        return data;
    }

    private String getFFProbeBaseName(String uuid) throws InvalidCredentialsException, MethodFailedException, InvalidResourceException {
        String fileName = getFileNameFromUuid(webservice, uuid);
        if (fileName != null) {
            return ffprobeDir + fileName;
        } else {
            return null;
        }
    }

    private String getFileNameFromUuid(CentralWebservice webservice, String uuid) throws InvalidCredentialsException, MethodFailedException, InvalidResourceException {
        String url = webservice.getObjectProfile(uuid).getTitle();
        if (url != null && url.contains(".")) {
            String[] parts = url.split("/");
            return parts[parts.length-1];
        } else {
            return null;
        }
    }
}
