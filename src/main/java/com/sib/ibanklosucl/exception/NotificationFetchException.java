package com.sib.ibanklosucl.exception;

public class NotificationFetchException extends RuntimeException {

    public NotificationFetchException(String message) {
        super(message);
    }

    public NotificationFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
