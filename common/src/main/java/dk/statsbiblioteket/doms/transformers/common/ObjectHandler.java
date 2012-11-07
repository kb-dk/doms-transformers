package dk.statsbiblioteket.doms.transformers.common;

/**
 * Run transformation on an object.
 */
public interface ObjectHandler {
    /**
     * Given a single object UUID, transform it.
     * @param uuid uuid of program objects.
     * @throws Exception on failure.
     */
    MigrationStatus transform(String uuid) throws Exception;

    /**
     * A name of the transformer used as identifier when naming output files.
     * @return A name of the transformer used as identifier when naming output files.
     */
    String getName();
}
