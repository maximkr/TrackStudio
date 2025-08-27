package com.trackstudio.exception;

public class CantDeleteAttachmentsException extends UserException {
    public CantDeleteAttachmentsException(Exception e) {
        super(e, "ERROR_CAN_NOT_DELETE_ATTACHMENT");
    }
}
