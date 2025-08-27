package com.trackstudio.exception;

public class CantDeleteAclException extends UserException {

    public CantDeleteAclException(Exception e) {
        super(e, "ERROR_CAN_NOT_DELETE_ASSIGNED_STATUS", new Object[]{e.getMessage()});
    }

}
