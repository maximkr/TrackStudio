package com.trackstudio.exception;

public class CantDeleteMessageTypeException extends UserException {
    public CantDeleteMessageTypeException(Exception e, String m) {
        super(e, "ERROR_CAN_NOT_DELETE_MTYPE", new Object[]{m});
    }
}
