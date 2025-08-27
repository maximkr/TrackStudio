package com.trackstudio.exception;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.trackstudio.startup.I18n;

public class UserException extends GranException {
    protected ActionMessages messages = null;

    public String toString() {
        return getMessage();
    }

    public void addActionMessages(ActionMessages a) {
        if (!a.isEmpty()) {
            messages.add(a);
        }
    }

    public UserException(String m) {
        super(I18n.getUserExceptionString(m));
        messages = new ActionMessages();
        messages.add("msg", new ActionMessage(m));
    }

    public UserException(String m, boolean noI18n) {
        super(m);
        messages = new ActionMessages();
        messages.add("msg", new ActionMessage(m, false));
    }

    public UserException(Exception e, String m) {
        super(e, I18n.getUserExceptionString(m));
        messages = new ActionMessages();
        messages.add("msg", new ActionMessage(m));
    }

    public UserException(Throwable e, String m) {
        super(e, I18n.getUserExceptionString(m));
        messages = new ActionMessages();
        messages.add("msg", new ActionMessage(m));
    }

    public UserException(Exception e, String m, Object[] o) {
        super(e, I18n.getUserExceptionString("en", m, o));
        messages = new ActionMessages();
        messages.add("msg", new ActionMessage(m, o));
    }

    public UserException(Throwable e, String m, Object[] o) {
        super(e, I18n.getUserExceptionString("en", m, o));
        messages = new ActionMessages();
        messages.add("msg", new ActionMessage(m, o));
    }

    public UserException(String m, Object[] o) {
        super(I18n.getUserExceptionString("en", m, o));
        messages = new ActionMessages();
        messages.add("msg", new ActionMessage(m, o));
    }

    public UserException(Exception e) {
        super(e);
    }

    public UserException(Throwable e) {
        super(e);
    }

    public ActionMessages getActionMessages() {
        return messages;
    }

}
