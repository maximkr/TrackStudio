package com.trackstudio.exception;

public class SecurityViolationException extends UserException {
    public SecurityViolationException(String s) {
        super(s);
    }

    public SecurityViolationException(String s, String[] args) {
        super(s, args);
    }
}
