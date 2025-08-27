package com.trackstudio.jmx;

import java.util.ArrayList;
import java.util.List;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.app.session.SessionManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.jmx.beans.IUserSessionsMXBean;

public class UserSessionsMXBeanImpl implements IUserSessionsMXBean {
    @Override
    public String[] getUserSessions() throws GranException {
        List<String> users = new ArrayList<String>();
        for (SessionContext context : SessionManager.getInstance().getSessions()) {
            users.add(context.getUser().getLogin() + " " + context.getSession());
        }
        return users.toArray(new String[users.size()]);
    }
}
