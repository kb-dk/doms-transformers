package dk.statsbiblioteket.doms.transformers.common;

import dk.statsbiblioteket.doms.transformers.common.callbacks.CallbackConfiguration;
import dk.statsbiblioteket.doms.transformers.common.callbacks.ObjectListHandlerCallback;
import dk.statsbiblioteket.doms.transformers.common.callbacks.exceptions.CallbackException;
import dk.statsbiblioteket.doms.transformers.common.callbacks.exceptions.StopExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Run through list of UUIDs, call object handler, and report status in logging and files.
 */
public class FileRecordingObjectListHandler implements ObjectListHandler {
    private static final Logger log = LoggerFactory.getLogger(FileRecordingObjectListHandler.class);
    private final List<CallbackConfiguration> callbackConfigurations = new ArrayList<CallbackConfiguration>();
    private final ObjectHandler objectHandler;

    /**
     * Initialise handler.
     * @param objectHandler The object handler that should handle each uuid.
     */
    public FileRecordingObjectListHandler(Config config, ObjectHandler objectHandler) {
        this.objectHandler = objectHandler;
    }

    @Override
    public void transform(List<String> uuids) throws StopExecutionException {
        for (String uuid: uuids) {
            try {
                MigrationStatus migrationStatus = objectHandler.transform(uuid);
                runCallbacks(uuid, migrationStatus);
            } catch (StopExecutionException e) {
                throw e;
            } catch (Exception e) {
                runCallbacks(uuid, e);
            }
        }
    }

    public void addCallback(ObjectListHandlerCallback callback) {
        callbackConfigurations.add(new CallbackConfiguration(callback));
    }

    public void addCallback(ObjectListHandlerCallback callback, Class clazz) {
        callbackConfigurations.add(new CallbackConfiguration(callback, clazz));
    }

    protected void runCallbacks(String uuid, MigrationStatus migrationStatus) throws StopExecutionException {
        for (CallbackConfiguration callbackConfiguration : callbackConfigurations) {
            ObjectListHandlerCallback callback = callbackConfiguration.getCallback();
            try {
                log.debug("Running callback '{}'", callback.getClass().getName());
                callback.run(uuid, migrationStatus);
            } catch (StopExecutionException e) {
                if (!callbackConfiguration.ignoreExceptions()) {
                    if (callbackConfiguration.getReturnClassToFailOn().isInstance(e)) {
                        throw e;
                    }
                }
                log.info("Ignored '" + e.getClass() + "' from '" + callback.getClass() + "'.");
            } catch (CallbackException e) {
                log.error("Error while running callback '{}'", callback.getClass().getName(), e);
            }
        }
    }

    protected void runCallbacks(String uuid, Throwable throwable) throws StopExecutionException {
        for (CallbackConfiguration callbackConfiguration : callbackConfigurations) {
            ObjectListHandlerCallback callback = callbackConfiguration.getCallback();
            try {
                log.debug("Running callback '{}'", callback.getClass().getName());
                callback.run(uuid, throwable);
            } catch (StopExecutionException e) {
                if (!callbackConfiguration.ignoreExceptions()) {
                    if (callbackConfiguration.getReturnClassToFailOn().isInstance(e)) {
                        throw e;
                    }
                }
                log.info("Ignored '" + e.getClass() + "' from '" + callback.getClass() + "'.");
            } catch (CallbackException e) {
                log.error("Error while running callback '{}'", callback.getClass().getName(), e);
            }
        }
    }
}
