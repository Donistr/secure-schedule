package org.example.client.exception;

public class RunCommandException extends BaseException {

    public RunCommandException() {
    }

    public RunCommandException(String message) {
        super(message);
    }

    public RunCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public RunCommandException(Throwable cause) {
        super(cause);
    }

    public RunCommandException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
