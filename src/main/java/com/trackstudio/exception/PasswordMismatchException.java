// $Id: PasswordMismatchException.java,v 1.1.1.1.2.2 2004/01/15 14:01:53 ddudikov Exp $
package com.trackstudio.exception;

public class PasswordMismatchException extends BadPasswordException {

    public PasswordMismatchException() {
        super("ERROR_PASSWORD_MISMATCH");
    }
}

