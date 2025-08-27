package com.trackstudio.exception;

/**
 * User authentication failed.
 *
 * @author $Author: ap $
 * @version $Revision: 1.1.1.1.2.5 $
 */
public class AuthException extends UserException {

    public AuthException() {
        //super("User " + login + " authentication failed.");
        super("AUTHENTICATION_FAILED");
    }
}

