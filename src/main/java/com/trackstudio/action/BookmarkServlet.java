package com.trackstudio.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.secured.SecuredBookmarkBean;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.textfilter.MacrosUtil;

public class BookmarkServlet extends HttpServlet {
    private static Log log = LogFactory.getLog(BookmarkServlet.class);
    private static final LockManager lockManager = LockManager.getInstance();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String imageServlet = "/ImageServlet/" + GeneralAction.SERVLET_KEY;
        boolean w = lockManager.acquireConnection();
        try {

            SessionContext sc = GeneralAction.getInstance().imports(request, response, true);
            if (sc == null || sc.getUserId() == null)
                return;

            String bookmarkToDelete = request.getParameter("delete");
            if (bookmarkToDelete != null && !bookmarkToDelete.equals("")) {
                AdapterManager.getInstance().getSecuredBookmarkAdapterManager().deleteBookmark(sc, bookmarkToDelete);
            }

            List<SecuredBookmarkBean> bookmarkList = AdapterManager.getInstance().getSecuredBookmarkAdapterManager().getBookmarkList(sc, sc.getUserId());
            List<SecuredBookmarkBean> taskBookmarks = new ArrayList<SecuredBookmarkBean>();
            List<SecuredBookmarkBean> userBookmarks = new ArrayList<SecuredBookmarkBean>();

            for (SecuredBookmarkBean bookmark : bookmarkList) {
                if (bookmark.getTaskId() != null)
                    taskBookmarks.add(bookmark);
                else
                    userBookmarks.add(bookmark);
            }

            Collections.sort(taskBookmarks);
            Collections.sort(userBookmarks);

            response.setLocale(new Locale(sc.getLocale()));
            response.setCharacterEncoding(Config.getEncoding());

            PrintWriter out = response.getWriter();

            String onClick;
            String img;

            if (taskBookmarks.size() != 0) {
                out.println("<div class=\"bookmarkCaption\">" + I18n.getString(sc.getLocale(), "TASKS") + "</div>");
                for (SecuredBookmarkBean tb : taskBookmarks) {
                    if (tb.getFilterId() != null && !tb.getFilterId().equals("")) {
                        onClick = "self.top.frames[1].location = '" + request.getContextPath() + "/TaskFilterParametersAction.do?method=changeTaskFilter&id=" + tb.getTaskId() + "&filterId=" + tb.getFilterId() + "&go=true'";
                        img = "<img class=\"bookmarkImage\" alt=\"\" src=\"" + request.getContextPath() + imageServlet + "/cssimages/ico.filter.gif\"/>";
                    } else {
                        if (tb==null || tb.getTask() == null || tb.getTask().getCategory()==null)
                            continue; // to avoid NPE
                        String categoryIcon = tb.getTask().getCategory().getIcon();
                        onClick = "self.top.frames[1].location = '" + request.getContextPath() + "/TaskViewAction.do?method=page&id=" + tb.getTaskId() + "'";
                        String statusImg = MacrosUtil.buildImageForState(tb.getTask().getStatus(), request.getContextPath() + imageServlet);
                        img = "<img class=\"bookmarkImage\" alt=\"\" src=\"" + request.getContextPath() + imageServlet + "/icons/categories/" + categoryIcon + "\"/>" + statusImg;
                    }
                    String bookmarkName = tb.getName();
                    int i = bookmarkName.indexOf("[#");
                    int j = bookmarkName.indexOf("]");
                    if (i > -1 && i < j) {
                        bookmarkName = bookmarkName.substring(0, i) + "<span class='number'>" + bookmarkName.substring(i, j + 1) + "</span>" + bookmarkName.substring(j + 1);
                    }
                    out.println("<div class=\"bookmarkItem\"><img class=\"deleteBookmarkImage\" title=\"" + I18n.getString(sc.getLocale(), "DELETE") + "\" alt=\"" + I18n.getString(sc.getLocale(), "DELETE") + "\" src=\"" + request.getContextPath() + imageServlet + "/cssimages/delete.png\" onclick=\"updateBookmarks('" + request.getContextPath() + "/bookmark?delete=" + tb.getId() + "');\"><span class=\"bookmarkName\" title=\"" + tb.getName() + "\" onclick=\"" + onClick + "\">" + img + bookmarkName + "</span></div>");
                }
            }

            out.println("<br/>");

            if (userBookmarks.size() != 0) {
                out.println("<div class=\"bookmarkCaption\">" + I18n.getString(sc.getLocale(), "USERS") + "</div>");
                for (SecuredBookmarkBean ub : userBookmarks) {
                    if (ub.getFilterId() != null && !ub.getFilterId().equals("")) {
                        onClick = "self.top.frames[1].location = '" + request.getContextPath() + "/UserFilterParametersAction.do?method=changeFilter&id=" + ub.getUserId() + "&filterId=" + ub.getFilterId() + "&go=true'";
                        img = "<img class=\"bookmarkImage\" alt=\"\" src=\"" + request.getContextPath() + imageServlet + "/cssimages/ico.filter.gif\"/>";
                    } else {
                        onClick = "self.top.frames[1].location = '" + request.getContextPath() + "/UserViewAction.do?method=page&id=" + ub.getUserId() + "'";
                        String activeUser = ub.getUser().isActive() ? ".a." : ".";
                        img = "<img class=\"bookmarkImage\" alt=\"\" src=\"" + request.getContextPath() + imageServlet + "/cssimages/arw.usr" + activeUser + "gif\"/>";
                    }
                    out.println("<div class=\"bookmarkItem\"><img class=\"deleteBookmarkImage\" title=\"" + I18n.getString(sc.getLocale(), "DELETE") + "\" alt=\"" + I18n.getString(sc.getLocale(), "DELETE") + "\" src=\"" + request.getContextPath() + imageServlet + "/cssimages/ico.delete.bookmark.gif\" onclick=\"updateBookmarks('" + request.getContextPath() + "/bookmark?delete=" + ub.getId() + "');\"><span class=\"bookmarkName\" title=\"" + ub.getName() + "\" onclick=\"" + onClick + "\">" + img + ub.getName() + "</span></div>");
                }
            }
        } catch (GranException e) {
            log.error("Exception ", e);
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }

    public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        doGet(httpServletRequest, httpServletResponse);
    }
}