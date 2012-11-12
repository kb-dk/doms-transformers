package dk.statsbiblioteket.doms.transformers.common;

/**
 * Run transformation on an object.
 */
public interface ObjectHandler {
    /**
     * Given a single object UUID, transform it.
     * @param uuid uuid of program objects.
     * @throws Exception on failure.
     * @return COMPLETE when a transform is 100% complete, INCOMPLETE when partially complete, NOOP when the file has already been completed, FAILED when the transform failed, and IGNORED when the transformation wasn't even attempted, eg. due to missing data.
     */
    MigrationStatus transform(String uuid) throws Exception;

    /**
     * A name of the transformer used as identifier when naming output files.
     * @return A name of the transformer used as identifier when naming output files.
     */
    String getName();
}
