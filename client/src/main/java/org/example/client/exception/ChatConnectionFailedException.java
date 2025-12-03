package org.example.client.exception;

import org.example.shared.exception.BaseException;

public class ChatConnectionFailedException extends BaseException {

    public ChatConnectionFailedException() {
    }

    public ChatConnectionFailedException(String message) {
        super(message);
    }

    public ChatConnectionFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatConnectionFailedException(Throwable cause) {
        super(cause);
    }

    public ChatConnectionFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
