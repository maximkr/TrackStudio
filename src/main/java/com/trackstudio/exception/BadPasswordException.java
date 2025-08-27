// $Id: BadPasswordException.java,v 1.1.1.1.2.3 2004/02/11 09:59:09 ap Exp $
package com.trackstudio.exception;

/**
 * Invalid user password. Please try again.
 *
 * @author $Author: ap $
 * @version $Revision: 1.1.1.1.2.3 $
 */
public class BadPasswordException extends UserException {

    public BadPasswordException(String mess) {
        super(mess);
    }

    public BadPasswordException(Exception e) {
        super(e, "BAD_PASSWORD");
    }
}
