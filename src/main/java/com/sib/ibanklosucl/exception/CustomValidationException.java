package com.sib.ibanklosucl.exception;

public class CustomValidationException extends RuntimeException{
    public CustomValidationException (String message) {
        super(message);
    }
}
