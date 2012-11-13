package dk.statsbiblioteket.doms.transformers.common.callbacks;

import dk.statsbiblioteket.doms.transformers.common.MigrationStatus;
import dk.statsbiblioteket.doms.transformers.common.callbacks.exceptions.CallbackException;
import org.slf4j.Logger;

public class ExceptionLoggerCallback implements ObjectListHandlerCallback {
    private final Logger log;

    public ExceptionLoggerCallback(Logger log) {
        this.log = log;
    }
    @Override
    public void run(String uuid, MigrationStatus migrationStatus) throws CallbackException {
    }

    @Override
    public void run(String uuid, Throwable t) throws CallbackException {
        log.error("Error processing uuid '{}'", uuid, t);
    }
}
