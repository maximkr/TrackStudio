package com.trackstudio.action.task;

import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.CustomEditAction;
import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.UdfValue;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.CustomForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;

public class TaskUdfListValuesAction extends TSDispatchAction {
    public ActionForward page(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            CustomForm form = (CustomForm) actionForm;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null)
                return null;
            String id = GeneralAction.getInstance().taskHeader(form, sc, request, true);
            CustomEditAction.fillListValuesForm(sc, "1", form, request);
            sc.setRequestAttribute(request, "editUdfAction", "/TaskUdfListValuesAction.do");
            sc.setRequestAttribute(request, "_can_view", sc.canAction(Action.manageTaskUDFs, id) || sc.canAction(Action.manageTaskUDFs, id));
            SecuredUDFBean udf = (SecuredUDFBean) request.getAttribute("udf");
            if (udf != null)
                sc.setRequestAttribute(request, "_can_modify", udf.canManage() && sc.canAction(Action.manageTaskUDFs, id) && sc.canAction(Action.manageTaskUDFs, KernelManager.getFind().findUdfsource(udf.getUdfSourceId()).getTask().getId()));
            else
                sc.setRequestAttribute(request, "_can_modify", Boolean.TRUE);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_CUSTOM_FIELD_VALUES_LIST);
            sc.setRequestAttribute(request, "tabView", new Tab(udf != null && (sc.canAction(Action.manageTaskUDFs, id) || sc.canAction(Action.manageTaskUDFs, id)) && sc.allowedByACL(id), false));
            sc.setRequestAttribute(request, "tabListValues", new Tab(udf != null && sc.canAction(Action.manageTaskUDFs, id) && sc.allowedByACL(id) && (udf.getType() == UdfValue.LIST || udf.getType() == UdfValue.MULTILIST), true));
            sc.setRequestAttribute(request, "tabEdit", new Tab(sc.canAction(Action.manageTaskUDFs, id) && sc.allowedByACL(id), false));
            sc.setRequestAttribute(request, "tabPermission", new Tab(udf != null && sc.canAction(Action.manageTaskUDFs, id) && sc.allowedByACL(id), false));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_CUSTOM_FIELD_VALUES_LIST));

            return actionMapping.findForward("taskUdfListValuesJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward save(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            CustomForm form = (CustomForm) actionForm;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null)
                return null;
            String id = form.getId();
            SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, form.getUdfId());
            if (udf.getType() == UdfValue.LIST || udf.getType() == UdfValue.MULTILIST) {
                if (udf.getUL() != null)
                    for (String ulist : udf.getUL().keySet()) {
                        AdapterManager.getInstance().getSecuredUDFAdapterManager().updateTaskUdflist(sc, id, ulist, form.getLists(ulist));
                    }
                if (form.getAddlist() != null && form.getAddlist().length() > 0) {
                    StringTokenizer tk = new StringTokenizer(form.getAddlist(), "\r\n");
                    while (tk.hasMoreTokens()) {
                        String token = tk.nextToken().trim();
                        if (token.length() > 0)
                            AdapterManager.getInstance().getSecuredUDFAdapterManager().addTaskUdflist(sc, id, udf.getId(), token);
                    }
                }
            }
            return actionMapping.findForward("taskUdfListValuesPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward delete(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            CustomForm form = (CustomForm) actionForm;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null)
                return null;
            SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, form.getUdfId());
            for (String key : udf.getUL().keySet()) {
                if (form.getValue(key) != null)
                    AdapterManager.getInstance().getSecuredUDFAdapterManager().deleteTaskUdflist(sc, form.getId(), key);
            }
            return actionMapping.findForward("taskUdfListValuesPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
