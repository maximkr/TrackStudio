package com.trackstudio.exception;

/**
 * @author ddudikov
 */
public class EmptyListException extends UserException {

    public EmptyListException() {
        super("ERROR_LIST_CAN_NOT_BE_EMPTY");
    }
}
