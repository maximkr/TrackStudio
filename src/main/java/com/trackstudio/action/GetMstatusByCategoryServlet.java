package com.trackstudio.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Category;
import com.trackstudio.startup.I18n;

public class GetMstatusByCategoryServlet extends TSDispatchAction   {
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception{
        boolean w = lockManager.acquireConnection();
        try {
            SessionContext sc = GeneralAction.getInstance().imports(request, response, false);
            String category = request.getParameter("id");
            Category c = KernelManager.getFind().findCategory(category);
            if (c != null) {
                List mstatuses = KernelManager.getWorkflow().getMstatusList(c.getWorkflow().getId());
                request.setAttribute("messageTypeColl", mstatuses);
            }
            request.setAttribute("alwaysCreateNewTask", I18n.getString(sc.getLocale(), "ALWAYS_CREATE_NEW_TASK"));
            return mapping.findForward("getMstatusByCategoryServletJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}

