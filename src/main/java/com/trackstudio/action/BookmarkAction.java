package com.trackstudio.action;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.BookmarkForm;

public class BookmarkAction extends TSDispatchAction {

    private static Log log = LogFactory.getLog(GeneralAction.class);

    public void save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws GranException {
        boolean w = lockManager.acquireConnection();
        try {
            BookmarkForm bookmarkForm = (BookmarkForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            Calendar createdate = new GregorianCalendar(new Locale(sc.getLocale()));
            AdapterManager.getInstance().getSecuredBookmarkAdapterManager().createBookmark(sc, bookmarkForm.getName(), createdate, bookmarkForm.getFilterId(), bookmarkForm.getTaskId(), bookmarkForm.getUserId(), sc.getUserId());
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
