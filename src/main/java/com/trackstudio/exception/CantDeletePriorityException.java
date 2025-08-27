package com.trackstudio.exception;

public class CantDeletePriorityException extends UserException {
    public CantDeletePriorityException(Exception e, String m) {
        super(e, "ERROR_CAN_NOT_DELETE_PRIORITY", new Object[]{m});
    }
}
