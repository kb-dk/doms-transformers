package dk.statsbiblioteket.doms.transformers.common;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Read a list of uuids from a file.
 */
public interface UuidFileReader {
    /**
     *
     * @param file The file to read UUIDs from.
     * @return A list of UUIDs read from the file.
     * @throws IOException on errors.
     */
    List<String> readUuids(File file) throws IOException;
}
