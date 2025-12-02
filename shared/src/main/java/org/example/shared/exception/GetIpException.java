package org.example.shared.exception;

public class GetIpException extends BaseException {

    public GetIpException() {
    }

    public GetIpException(String message) {
        super(message);
    }

    public GetIpException(String message, Throwable cause) {
        super(message, cause);
    }

    public GetIpException(Throwable cause) {
        super(cause);
    }

    public GetIpException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
