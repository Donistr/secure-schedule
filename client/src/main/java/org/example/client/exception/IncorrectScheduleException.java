package org.example.client.exception;

public class IncorrectScheduleException extends BaseException {

    public IncorrectScheduleException() {
    }

    public IncorrectScheduleException(String message) {
        super(message);
    }

    public IncorrectScheduleException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectScheduleException(Throwable cause) {
        super(cause);
    }

    public IncorrectScheduleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
