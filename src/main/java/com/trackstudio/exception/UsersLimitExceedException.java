package com.trackstudio.exception;

public class UsersLimitExceedException extends UserException {

    public UsersLimitExceedException() {
        super("ERROR_USER_LIMIT_EXCEED");
    }
}
