package com.trackstudio.exception;

import com.trackstudio.app.session.SessionContext;

public class InvalidParameterException extends UserException {

    public InvalidParameterException(String className, String method, String parameter, String userId) {
        super("InvalidParameterException: className=" + className + ", method=" + method + ", parameter=" + parameter + ", " +
                "userId=" + userId, true);
    }

    public InvalidParameterException(Class className, String method, String parameter, SessionContext sc) throws GranException {
        super("InvalidParameterException: className=" + className.getName() + ", method=" + method + ", " +
                "parameter=" + parameter + ", " + "userId=" + (sc != null ? sc.getUserId() : null), true);
    }
}
