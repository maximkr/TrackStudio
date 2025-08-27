package com.trackstudio.exception;

public class CantDeleteStateException extends UserException {
    public CantDeleteStateException(Exception e, String m) {
        super(e, "ERROR_CAN_NOT_DELETE_STATE", new Object[]{m});
    }

    public CantDeleteStateException() {
        super("ERROR_CAN_NOT_DELETE_STATE");
    }
}
