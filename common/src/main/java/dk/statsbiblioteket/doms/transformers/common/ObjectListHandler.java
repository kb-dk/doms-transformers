package dk.statsbiblioteket.doms.transformers.common;

import dk.statsbiblioteket.doms.transformers.common.callbacks.ObjectListHandlerCallback;
import dk.statsbiblioteket.doms.transformers.common.callbacks.exceptions.StopExecutionException;

import java.util.List;

/**
 * Given a list of objects, call the object handler on each one.
 */
public interface ObjectListHandler {
    /**
     * Given a list of program object UUIDs, transform them.
     * This will call {@link ObjectHandler#transform(String)} and appropriately record progress.
     * @param uuids List of uuids of program objects.
     */
    void transform(List<String> uuids) throws StopExecutionException;
    void addCallback(ObjectListHandlerCallback callback);
    void addCallback(ObjectListHandlerCallback callback, Class clazz);
}
