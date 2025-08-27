package com.trackstudio.exception;

public class CantDeleteEmailTypeException extends UserException {
    public CantDeleteEmailTypeException(Exception e, String m) {
        super(e, "ERROR_CAN_NOT_DELETE_TEMPLATE", new Object[]{m});
    }
}
