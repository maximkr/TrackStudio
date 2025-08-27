package com.trackstudio.exception;


public class DuplicateUserLoginException extends UserException {

    public DuplicateUserLoginException() {
        super("ERROR_DUPLICATE_USER_LOGIN");
    }
}
