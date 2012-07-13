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
    void transform(String uuid) throws Exception;
}
