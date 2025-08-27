package com.trackstudio.exception;

public class CantDeletePrstatusException extends UserException {
    public CantDeletePrstatusException(Exception e, String m) {
        super(e, "ERROR_CAN_NOT_DELETE_PRSTATUS", new Object[]{m});
    }
}
