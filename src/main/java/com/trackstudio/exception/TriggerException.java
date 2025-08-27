package com.trackstudio.exception;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.trackstudio.tools.textfilter.HTMLEncoder;

public class TriggerException extends UserException {
    private String triggerName;
    private String msg;

    public TriggerException(String triggerName, Throwable ex) {
        super(ex);
        this.triggerName = triggerName;
        HTMLEncoder sb = new HTMLEncoder(ex.toString());
        sb.replace("\r\n", "</br>");
        sb.replace("\n", "</br>");
        this.msg = sb.toString();
        messages = new ActionMessages();
        messages.add("msg", new ActionMessage("ERROR_TRIGGER_EXCEPTION", new String[]{triggerName, msg}));
    }

    public String getMessage() {
        return "Trigger Exception (" + triggerName + "). " + msg;
    }


}
