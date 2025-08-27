package com.trackstudio.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.app.session.SessionManager;
import com.trackstudio.exception.CantFindObjectException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.secured.SecuredNotificationBean;
import com.trackstudio.secured.SecuredSubscriptionBean;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;

public class UnsubscribeServlet extends HttpServlet {
    private static final LockManager lockManager = LockManager.getInstance();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        boolean w = lockManager.acquireConnection();
        try {
            response.setContentType("text/html; charset=" + Config.getEncoding());
            response.setCharacterEncoding(Config.getEncoding());
            PrintWriter writer = response.getWriter();
            writer.write("<html><head>");
            writer.write("<meta http-equiv=\"content-type\" content=\"text/html; charset=\"" + Config.getEncoding() + "\">");
            writer.write("</head>");
            writer.write("<body>");
            writer.write("<div>");
            SessionContext sc = (SessionContext) request.getAttribute("sc");

            if (sc == null) {
                ArrayList<String> sessionIds = SessionContext.getCookies(request, response);

                for (String sessId : sessionIds) {
                    sc = SessionManager.getInstance().getSessionContext(sessId);
                    if (sc != null) break;
                }
                if (sc != null)
                    sc.setSessionInCookies(true);
            }
            String notificationId = request.getParameter("notificationId");
            if (notificationId != null && sc != null) {
                SecuredNotificationBean not = null;
                try {
                    not = AdapterManager.getInstance().getSecuredFindAdapterManager().findNotificationById(sc, notificationId);
                } catch (CantFindObjectException cf) {
                    not = null;
                }
                if (not != null) {
                    if (sc.canAction(Action.manageEmailSchedules, not.getTaskId())) {
                        writer.write(I18n.getString(sc, "UNSUBSCRIBED_SUCCESSFULLY", new String[]{not.getName()}));
                        AdapterManager.getInstance().getSecuredFilterAdapterManager().unsetNotification(sc, notificationId);


                    } else {
                        writer.write(I18n.getString(sc, "UNSUBSCRIBED_UNSUCCESSFULLY", new String[]{not.getName()}));

                    }
                } else {
                    SecuredSubscriptionBean sub = null;
                    try {
                        sub = AdapterManager.getInstance().getSecuredFindAdapterManager().findSubscriptionById(sc, notificationId);
                    } catch (CantFindObjectException ed) {
                        ed.printStackTrace();
                    }
                    if (sub != null) {
                        if (sc.canAction(Action.manageEmailSchedules, sub.getTaskId())) {
                            writer.write(I18n.getString(sc, "UNSUBSCRIBED_SUCCESSFULLY", new String[]{sub.getName()}));
                            AdapterManager.getInstance().getSecuredFilterAdapterManager().unSubscribe(sc, notificationId);

                        } else {
                            writer.write(I18n.getString(sc, "UNSUBSCRIBED_UNSUCCESSFULLY", new String[]{sub.getName()}));
                        }
                    }
                }
            } else {
                writer.write(I18n.getString("en", "NOT_LOGGED_IN"));
            }
            writer.write("</div>");
            writer.write("</body>");
            writer.write("</html>");
            writer.flush();
            writer.close();
        } catch (IOException ignore) {
        } catch (Exception ex) {
            request.setAttribute("javax.servlet.jsp.jspException", ex);
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/jsp/Error.jsp");
            requestDispatcher.forward(request, response);
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }

    public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        doGet(httpServletRequest, httpServletResponse);
    }
}
