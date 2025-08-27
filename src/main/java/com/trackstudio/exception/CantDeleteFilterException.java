package com.trackstudio.exception;

public class CantDeleteFilterException extends UserException {
    public CantDeleteFilterException(Exception e, String m) {
        super(e, "ERROR_CAN_NOT_DELETE_FILTER", new Object[]{m});
    }
}
