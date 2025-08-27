package com.trackstudio.exception;

public class CantDeleteTransitionException extends UserException {
    public CantDeleteTransitionException(Exception e) {
        super(e, "ERROR_CAN_NOT_DELETE_TRANSITION");
    }
}
