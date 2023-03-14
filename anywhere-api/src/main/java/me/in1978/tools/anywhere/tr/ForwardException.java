package me.in1978.tools.anywhere.tr;

public class ForwardException extends SocketException{
    public ForwardException() {
    }

    public ForwardException(String message) {
        super(message);
    }

    public ForwardException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForwardException(Throwable cause) {
        super(cause);
    }

    public ForwardException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
