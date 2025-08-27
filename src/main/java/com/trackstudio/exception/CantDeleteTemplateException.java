package com.trackstudio.exception;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 26.10.2006
 * Time: 19:24:27
 * To change this template use File | Settings | File Templates.
 */
public class CantDeleteTemplateException extends UserException {
    public CantDeleteTemplateException(Exception e, String m) {
        super(e, "ERROR_CAN_NOT_DELETE_TASK_TEMPLATE", new Object[]{m});
    }
}
