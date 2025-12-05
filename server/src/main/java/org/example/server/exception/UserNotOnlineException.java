package org.example.server.exception;

import org.example.shared.exception.BaseException;

public class UserNotOnlineException extends BaseException {

    public UserNotOnlineException() {
    }

    public UserNotOnlineException(String message) {
        super(message);
    }

    public UserNotOnlineException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotOnlineException(Throwable cause) {
        super(cause);
    }

    public UserNotOnlineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
