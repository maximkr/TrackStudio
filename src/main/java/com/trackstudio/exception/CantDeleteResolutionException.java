package com.trackstudio.exception;

public class CantDeleteResolutionException extends UserException {
    public CantDeleteResolutionException(Exception e, String m) {
        super(e, "ERROR_CAN_NOT_DELETE_RESOLUTION", new Object[]{m});
    }
}
