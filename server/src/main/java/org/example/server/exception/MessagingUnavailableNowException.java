package org.example.server.exception;

import org.example.shared.exception.BaseException;

public class MessagingUnavailableNowException extends BaseException {

    public MessagingUnavailableNowException() {
    }

    public MessagingUnavailableNowException(String message) {
        super(message);
    }

    public MessagingUnavailableNowException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessagingUnavailableNowException(Throwable cause) {
        super(cause);
    }

    public MessagingUnavailableNowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
