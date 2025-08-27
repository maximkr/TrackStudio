package com.trackstudio.action;

import java.util.ArrayList;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.servlets.BaseAjaxServlet;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.app.session.SessionManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;

public class ContextHelpServlet extends BaseAjaxServlet {
    private static final LockManager lockManager = LockManager.getInstance();

    public String getXmlContent(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            ArrayList<String> sessionIds = SessionContext.getCookies(request, response);
            SessionContext sc = null;
            for (String sessionId : sessionIds) {
                sc = SessionManager.getInstance().getSessionContext(sessionId);
                if (sc != null) break;
            }
            if (sc != null)
                sc.setSessionInCookies(true);
            if (sc == null) {

                String sessionId = request.getParameter("session");
                sc = SessionManager.getInstance().getSessionContext(sessionId);
                if (sc != null)
                    sc.setSessionInCookies(false);
            }

            if (sc == null) {
                return "";
            }

            String helpContext = request.getParameter("helpContext");

            StringBuffer html = new StringBuffer();
            html.append("<xml>");
            html.append(I18n.getString(sc, helpContext));
            html.append("</xml>");
            response.setLocale(new Locale(sc.getLocale()));
            response.setCharacterEncoding(Config.getInstance().getEncoding());
            return html.toString();
        } finally {
            if (w) lockManager.releaseConnection();
        }


    }
}
