package me.in1978.tools.anywhere.tr;

public class AnywhereException extends Exception{
    public AnywhereException() {
    }

    public AnywhereException(String message) {
        super(message);
    }

    public AnywhereException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnywhereException(Throwable cause) {
        super(cause);
    }

    public AnywhereException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
