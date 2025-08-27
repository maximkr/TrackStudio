package com.trackstudio.exception;

public class UserMessageException extends UserException {
    public UserMessageException(String msg) {
        super(msg, true);
    }
}
