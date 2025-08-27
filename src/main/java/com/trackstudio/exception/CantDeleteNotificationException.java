package com.trackstudio.exception;

public class CantDeleteNotificationException extends UserException {
    public CantDeleteNotificationException(Exception e) {
        super(e, "ERROR_CAN_NOT_DELETE_NOTIFICATION_RULE");
    }
}
