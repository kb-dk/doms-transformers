package dk.statsbiblioteket.doms.transformers.common.callbacks;

public class CallbackConfiguration {
    private final ObjectListHandlerCallback callback;
    private final Class clazz;

    public CallbackConfiguration(ObjectListHandlerCallback callback) {
        this(callback, null);
    }

    public CallbackConfiguration(ObjectListHandlerCallback callback, Class clazz) {
        this.callback = callback;
        this.clazz = clazz;
    }

    public ObjectListHandlerCallback getCallback() {
        return callback;
    }

    public Class getReturnClassToFailOn() {
        return clazz;
    }

    public boolean ignoreExceptions() {
        return clazz == null;
    }
}
