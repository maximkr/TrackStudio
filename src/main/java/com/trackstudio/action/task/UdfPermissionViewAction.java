package com.trackstudio.action.task;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.constants.WorkflowConstants;
import com.trackstudio.form.MstatusUdfPermissionForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;

/**
 * Created by IntelliJ IDEA.
 * User: Eugene
 * Date: 01.02.2007
 * Time: 23:42:02
 * To change this template use File | Settings | File Templates.
 */
public class UdfPermissionViewAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(ReportEditAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            MstatusUdfPermissionForm form = (MstatusUdfPermissionForm) actionForm;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null) return null;
            String id = GeneralAction.getInstance().taskHeader(form, sc, request, true);
            form.setId(id);
            String idUDF = form.getWorkflowId() == null ? "1" : form.getWorkflowId();
            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, idUDF);
            String mstatusId = form.getMstatusId();
            String udfId = form.getUdfId();
            SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, udfId);
            SecuredMstatusBean mstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, mstatusId);
            Set<SecuredPrstatusBean> prstatusSet = new TreeSet(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId()));
            sc.setRequestAttribute(request, "prstatusSet", prstatusSet);
            String viewRules = "", editRules = "";
            for (SecuredPrstatusBean spb : prstatusSet) {
                List types = AdapterManager.getInstance().getSecuredUDFAdapterManager().getUDFRuleList(sc, spb.getId(), udfId);
                boolean viewMstatusValues = false;
                boolean editMstatusValues = false;

                if (types.contains(UdfConstants.MSTATUS_VIEW_ALL))
                    viewMstatusValues = true;
                if (types.contains(UdfConstants.MSTATUS_EDIT_ALL))
                    editMstatusValues = true;

                if (types.contains(WorkflowConstants.MSTATUS_VIEW_PREFIX + mstatus.getId()))
                    viewMstatusValues = true;
                if (types.contains(WorkflowConstants.MSTATUS_EDIT_PREFIX + mstatus.getId()))
                    editMstatusValues = true;
                if (viewMstatusValues) {
                    if (viewRules.length() > 0) viewRules += ", ";
                    viewRules += spb.getName();
                }
                if (editMstatusValues) {
                    if (editRules.length() > 0) editRules += ", ";
                    editRules += spb.getName();
                }
            }
            sc.setRequestAttribute(request, "editRules", editRules);
            sc.setRequestAttribute(request, "viewRules", viewRules);
            sc.setRequestAttribute(request, "mstatusId", mstatusId);
            sc.setRequestAttribute(request, "mstatus", mstatus);
            sc.setRequestAttribute(request, "udfId", udfId);
            sc.setRequestAttribute(request, "udf", udf);
            sc.setRequestAttribute(request, "flow", flow);
            selectTaskTab(sc, id, "tabWorkflows", request);
            sc.setRequestAttribute(request, "tabView", new Tab(sc.canAction(Action.manageWorkflows, flow.getId()), true));
            sc.setRequestAttribute(request, "tabEdit", new Tab(flow.canManage() && sc.canAction(Action.manageWorkflows, flow.getId()), false));
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_WORKFLOW_MSTATUS_UDF_PERMISSIONS_VIEW);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_MSTATUS_UDF_PERMISSIONS_VIEW));
            return mapping.findForward("udfPermissionViewJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
