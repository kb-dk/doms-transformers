package dk.statsbiblioteket.doms.transformers.common.callbacks;

import dk.statsbiblioteket.doms.transformers.common.MigrationStatus;
import dk.statsbiblioteket.doms.transformers.common.callbacks.exceptions.CallbackException;

public interface ObjectListHandlerCallback {
    void run(String uuid, MigrationStatus migrationStatus) throws CallbackException;
    void run(String uuid, Throwable t) throws CallbackException;
}
