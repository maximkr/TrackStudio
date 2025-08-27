package com.trackstudio.exception;

public class CantDeleteWorkflowException extends UserException {
    public CantDeleteWorkflowException(Exception e, String m) {
        super(e, "ERROR_CAN_NOT_DELETE_WORKFLOW", new Object[]{m});
    }
}
