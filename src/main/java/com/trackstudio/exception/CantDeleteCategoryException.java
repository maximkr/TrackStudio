package com.trackstudio.exception;

public class CantDeleteCategoryException extends UserException {
    public CantDeleteCategoryException(Exception e, String m) {
        super(e, "ERROR_CAN_NOT_DELETE_CATEGORY_2", new Object[]{m});
    }

    public CantDeleteCategoryException(Exception e) {
        super(e, "ERROR_CAN_NOT_DELETE_CATEGORY");
    }
}
