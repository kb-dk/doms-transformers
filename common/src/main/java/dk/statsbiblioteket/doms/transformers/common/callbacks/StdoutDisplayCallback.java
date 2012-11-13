package dk.statsbiblioteket.doms.transformers.common.callbacks;

import dk.statsbiblioteket.doms.transformers.common.MigrationStatus;
import dk.statsbiblioteket.doms.transformers.common.callbacks.exceptions.CallbackException;

public class StdoutDisplayCallback implements ObjectListHandlerCallback {
    @Override
    public void run(String uuid, MigrationStatus migrationStatus) throws CallbackException {
        System.out.println(uuid + " -> " + migrationStatus);
    }

    @Override
    public void run(String uuid, Throwable t) throws CallbackException {
        System.out.println(uuid + " -> Exception: " + t);
    }
}
