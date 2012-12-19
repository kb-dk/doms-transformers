package dk.statsbiblioteket.doms.transformers.fileenricher;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.DatastreamProfile;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.transformers.common.FileRecordingObjectListHandler;
import dk.statsbiblioteket.doms.transformers.common.FFProbeLocationDomsConfig;
import dk.statsbiblioteket.doms.transformers.common.MigrationStatus;
import dk.statsbiblioteket.doms.transformers.common.ObjectHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DomsFileEnricherObjectHandler implements ObjectHandler {
    private FFProbeLocationDomsConfig config;
    private final CentralWebservice webservice;
    private final Map<String, String> checksums;
    private Map<String, String> filesizes;

    private static final Logger log = LoggerFactory.getLogger(DomsFileEnricherObjectHandler.class);

    public DomsFileEnricherObjectHandler(FFProbeLocationDomsConfig config, CentralWebservice webservice, Map<String, String> checksums, Map<String, String> filesizes)
            throws JAXBException, IOException, ParseException, URISyntaxException {

        this.config = config;
        this.webservice = webservice;
        this.checksums = checksums;
        this.filesizes = filesizes;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public MigrationStatus transform(String uuid) throws Exception {
        List<String> datastreamProfilesIDs = getDatastreamProfilesIDs(uuid);

        boolean enrichFFProbeData = shouldEnrich(uuid, datastreamProfilesIDs, BroadcastMetadataEnricher.FFPROBE_DATASTREAM_NAME, BroadcastMetadataEnricher.FFPROBE_ERRORS_DATASTREAM_NAME);
        boolean enrichBroadcastMetadata = shouldEnrich(uuid, datastreamProfilesIDs, BroadcastMetadataEnricher.BROADCAST_METADATA_DATASTREAM_NAME);

        String filename = getFilenameFromObject(uuid);

        if (filename != null && (enrichFFProbeData || enrichBroadcastMetadata)) {
            webservice.markInProgressObject(Arrays.asList(uuid), "Modifying object as part of datamodel upgrade");

            int migrationSuccesses = 0;

            if (enrichFFProbeData) {
                MigrationStatus ffProbeMigrationStatus = new FFProbeEnricher(config, webservice).transform(uuid);
                if (ffProbeMigrationStatus.equals(MigrationStatus.COMPLETE)) {
                    migrationSuccesses++;
                }
            }

            if (enrichBroadcastMetadata) {
                MigrationStatus metadataMigrationStatus = new BroadcastMetadataEnricher(config, webservice, checksums, filesizes, filename).transform(uuid);
                if (metadataMigrationStatus.equals(MigrationStatus.COMPLETE)) {
                    migrationSuccesses++;
                }
            }

            webservice.markPublishedObject(Arrays.asList(uuid), "Modifying object as part of datamodel upgrade");

            switch (migrationSuccesses) {
                case 2:
                    return MigrationStatus.COMPLETE;
                case 1:
                    return MigrationStatus.INCOMPLETE;
                default:
                    return MigrationStatus.FAILED;
            }
        } else {
            return MigrationStatus.NOOP;
        }
    }

    public String getFilenameFromObject(String uuid) throws InvalidCredentialsException, MethodFailedException, InvalidResourceException {
        String label = webservice.getObjectProfile(uuid).getTitle();

        if (label.startsWith("http://")) {
            return label.substring(label.lastIndexOf("/") + 1);
        } else {
            return null;
        }
    }

    public List<String> getDatastreamProfilesIDs(String uuid) throws InvalidCredentialsException, MethodFailedException, InvalidResourceException {
        // Example types: "FFPROBE", "FFPROBE_ERROR_LOG", "BROADCAST_METADATA"
        List<String> result = new LinkedList<String>();
        for (DatastreamProfile datastreamProfile : webservice.getObjectProfile(uuid).getDatastreams()) {
            String type = datastreamProfile.getId();
            result.add(type);
        }

        return result;
    }

    public boolean shouldEnrich(String uuid, String... types) throws InvalidCredentialsException, MethodFailedException, InvalidResourceException {
        List<String> datastreamProfilesIDs = getDatastreamProfilesIDs(uuid);

        return shouldEnrich(uuid, datastreamProfilesIDs, types);
    }

    public boolean shouldEnrich(String uuid, List<String> datastreamProfilesIDs, String... types) {
        log.debug("Datastreams for " + uuid + ": " + join(datastreamProfilesIDs, ", "));
        boolean b = !datastreamProfilesIDs.containsAll(Arrays.asList(types));
        return b;
    }

    private String join(List<String> parts, String separator) {
        boolean first = true;
        String result = "";
        for (String part : parts) {
            if (first) {
                first = false;
                result += part;
            } else {
                result += separator + part;
            }
        }

        return result;
    }
}
