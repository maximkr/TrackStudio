package com.trackstudio.exception;

public class CantDeleteMessageException extends UserException {
    public CantDeleteMessageException(Exception e, String m) {
        super(e, "ERROR_CAN_NOT_DELETE_MESSAGE", new Object[]{m});
    }
}
