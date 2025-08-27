package com.trackstudio.exception;

/**
 * @author ddudikov
 */
public class CantDeleteUserException extends UserException {

    public CantDeleteUserException(String userName) {
        super("ERROR_CAN_NOT_DELETE_USER", new Object[]{userName});
    }
}
