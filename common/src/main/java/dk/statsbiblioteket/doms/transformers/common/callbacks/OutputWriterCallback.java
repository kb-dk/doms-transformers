package dk.statsbiblioteket.doms.transformers.common.callbacks;

import dk.statsbiblioteket.doms.transformers.common.Config;
import dk.statsbiblioteket.doms.transformers.common.MigrationStatus;
import dk.statsbiblioteket.doms.transformers.common.ObjectHandler;
import dk.statsbiblioteket.doms.transformers.common.callbacks.exceptions.OutputWritingFailedException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OutputWriterCallback implements ObjectListHandlerCallback {
    private BufferedWriter errorWriter;
    private Map<MigrationStatus, BufferedWriter> statusToWriterMap;

    public OutputWriterCallback(Config config, ObjectHandler objectHandler) throws IOException {
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
    public void run(String uuid, MigrationStatus migrationStatus) throws OutputWritingFailedException {
        if (migrationStatus == null) {
            writeError(uuid);
        } else {
            write(migrationStatus, uuid);
        }
    }

    @Override
    public void run(String uuid, Throwable t) throws OutputWritingFailedException {
        writeError(uuid);
    }

    private void write(MigrationStatus migrationStatus, String uuid) throws OutputWritingFailedException {
        BufferedWriter bufferedWriter = statusToWriterMap.get(migrationStatus);
        write(bufferedWriter, uuid);
    }

    private void write(BufferedWriter writer, String uuid) throws OutputWritingFailedException {
        try {
            writer.write(uuid);
            writer.newLine();
            writer.flush();
        } catch (Exception e) {
            throw new OutputWritingFailedException();
        }
    }

    private void writeError(String uuid) throws OutputWritingFailedException {
        write(errorWriter, uuid);
    }
}
