package com.trackstudio.exception;

public class CantDeleteRegistrationException extends UserException {
    public CantDeleteRegistrationException(Exception e, String m) {
        super(e, "ERROR_CAN_NOT_DELETE_REGISTRATION_RULE", new Object[]{m});
    }
}
