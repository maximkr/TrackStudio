package com.trackstudio.exception;

public class CantDeleteMailImportException extends UserException {
    public CantDeleteMailImportException(Exception e, String m) {
        super(e, "ERROR_CAN_NOT_DELETE_EMAIL_IMPORT", new Object[]{m});
    }
}
