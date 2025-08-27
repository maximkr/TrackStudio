package com.trackstudio.exception;

public class CantDeleteUdfException extends UserException {
    public CantDeleteUdfException(Exception e, String m) {
        super(e, "ERROR_CAN_NOT_DELETE_UDF", new Object[]{m});
    }

    public CantDeleteUdfException(Exception e) {
        super(e, "ERROR_CAN_NOT_DELETE_UDF_2");
    }
}
