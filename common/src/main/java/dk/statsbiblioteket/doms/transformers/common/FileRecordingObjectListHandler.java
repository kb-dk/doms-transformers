package dk.statsbiblioteket.doms.transformers.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Run through list of UUIDs, call object handler, and report status in logging and files.
 */
public class FileRecordingObjectListHandler implements ObjectListHandler {
    private static final Logger log = LoggerFactory.getLogger(FileRecordingObjectListHandler.class);
    private ObjectHandler objectHandler;
    private BufferedWriter errorWriter;
    private Map<MigrationStatus, BufferedWriter> statusToWriterMap;

    /**
     * Initialise handler. Will open files for reporting successes and failures.
     *
     * @param config Configuration. Will read file names from here.
     * @param objectHandler The object handler that should handle each uuid.
     * @throws IOException On trouble opening the files.
     */
    public FileRecordingObjectListHandler(Config config, ObjectHandler objectHandler) throws IOException {
        this.objectHandler = objectHandler;
        File outputDirectory = new File(config.getOutputDirectory(), objectHandler.getName());
        if (!outputDirectory.isDirectory() && !outputDirectory.mkdirs()) {
            throw new IOException("Unable to create directory '" + outputDirectory.getPath() + "'");
        }

        statusToWriterMap = new HashMap<MigrationStatus, BufferedWriter>();

        for (MigrationStatus migrationStatus : MigrationStatus.values()) {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(outputDirectory, migrationStatus.name().toLowerCase())));
            statusToWriterMap.put(migrationStatus, bufferedWriter);
        }

        errorWriter = new BufferedWriter(new FileWriter(new File(outputDirectory, "ERROR")));
    }

    @Override
    public void transform(List<String> uuids) {
        for (String uuid : uuids) {
            try {
                System.out.print(uuid + " -> ");
                MigrationStatus migrationStatus = objectHandler.transform(uuid);
                System.out.println(migrationStatus);
                try {
                    if (migrationStatus == null) {
                        writeError(uuid);
                        log.error("'{}' returned null as MigrationStatus.", objectHandler.getClass());
                    } else {
                        write(migrationStatus, uuid);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                    break;
                }
            } catch (Exception e) {
                System.out.println(e.getClass().getName());
                log.error("Error processing uuid '{}'", uuid, e);
                try {
                    writeError(uuid);
                } catch (IOException e2) {
                    e2.printStackTrace();
                    System.exit(1);
                    break;
                }
            }
        }
    }

    public void write(MigrationStatus migrationStatus, String uuid) throws IOException {
        BufferedWriter bufferedWriter = statusToWriterMap.get(migrationStatus);
        write(bufferedWriter, uuid);
    }

    public void write(BufferedWriter writer, String uuid) throws IOException {
        writer.write(uuid);
        writer.newLine();
        writer.flush();
    }

    public void writeError(String uuid) throws IOException {
        errorWriter.write(uuid);
        errorWriter.newLine();
        errorWriter.flush();
    }
}
