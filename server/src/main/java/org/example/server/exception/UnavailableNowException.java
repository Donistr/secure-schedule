package org.example.server.exception;

import org.example.shared.exception.BaseException;

public class UnavailableNowException extends BaseException {

    public UnavailableNowException() {
    }

    public UnavailableNowException(String message) {
        super(message);
    }

    public UnavailableNowException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnavailableNowException(Throwable cause) {
        super(cause);
    }

    public UnavailableNowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
