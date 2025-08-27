package com.trackstudio.exception;

import com.trackstudio.startup.I18n;

public class CantFindObjectException extends GranException {
    public CantFindObjectException(Exception e, Object[] o) {
        super(e, I18n.getUserExceptionString("en", "ERROR_CANT_FIND_OBJECT", o));
    }

    public CantFindObjectException(Object[] o) {
        super(I18n.getUserExceptionString("en", "ERROR_CANT_FIND_OBJECT", o));
    }
}
