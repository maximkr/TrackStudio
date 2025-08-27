// $Id: CantCreateUserException.java,v 1.1.1.1.2.3 2004/02/11 09:59:09 ap Exp $
package com.trackstudio.exception;

public class CantCreateUserException extends UserException {

    public CantCreateUserException(Exception e) {
        super(e);
    }
}
