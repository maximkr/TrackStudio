package com.trackstudio.exception;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.trackstudio.startup.I18n;

public class ETLException extends UserException {

    public ETLException(String m) {
        super(I18n.getUserExceptionString(m));
        messages = new ActionMessages();
        messages.add("msg", new ActionMessage(m));
    }

}
