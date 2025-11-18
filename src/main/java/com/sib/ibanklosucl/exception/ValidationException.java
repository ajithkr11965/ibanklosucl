package com.sib.ibanklosucl.exception;

public class ValidationException extends RuntimeException {
    private final ValidationError error;
    private final Object[] args;

    public ValidationException(ValidationError error, Object... args) {
        super(error.getMessage(args));
        this.error = error;
        this.args = args;
    }

    public ValidationError getError() {
        return error;
    }

    public Object[] getArgs() {
        return args;
    }
}

