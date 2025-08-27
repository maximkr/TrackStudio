package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.CategoryConstants;
import com.trackstudio.form.MstatusUdfPermissionForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;

public class WorkflowUdfOperationPermissionAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(ReportEditAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            MstatusUdfPermissionForm form = (MstatusUdfPermissionForm) actionForm;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null)
                return null;
            String id = GeneralAction.getInstance().taskHeader(form, sc, request, true);
            form.setId(id);
            String workflowId = form.getWorkflowId() == null ? "1" : form.getWorkflowId();
            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
            ArrayList<SecuredMstatusBean> mstatusList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getMstatusList(sc, flow.getId());
            String udfId = form.getUdfId();
            SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, udfId);
            List<String> editableIds = KernelManager.getUdf().getOperationsWhereUDFIsEditable(udfId);
            List<String> viewableIds = KernelManager.getUdf().getOperationsWhereUDFIsViewable(udfId);
            List<SecuredMstatusBean> editable = new ArrayList<SecuredMstatusBean>();
            List<SecuredMstatusBean> viewable = new ArrayList<SecuredMstatusBean>();
            StringBuffer hiddenViewable = new StringBuffer();
            StringBuffer hiddenEditable = new StringBuffer();

            for (String i : editableIds) {
                SecuredMstatusBean o = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, i);
                if (!editable.contains(o)) {
                    editable.add(o);
                    hiddenEditable.append(i).append(FValue.DELIM);
                }
            }
            for (String i : viewableIds) {
                SecuredMstatusBean o = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, i);
                if (!viewable.contains(o)) {
                    viewable.add(o);
                    hiddenViewable.append(i).append(FValue.DELIM);
                }
            }
            List<SecuredMstatusBean> noteditable = new ArrayList<SecuredMstatusBean>(mstatusList);
            List<SecuredMstatusBean> notviewable = new ArrayList<SecuredMstatusBean>(mstatusList);
            noteditable.removeAll(editable);
            notviewable.removeAll(viewable);

            form.setHiddencanedit(hiddenEditable.toString());
            form.setHiddencanview(hiddenViewable.toString());
            sc.setRequestAttribute(request, "viewable", viewable);
            sc.setRequestAttribute(request, "notviewable", notviewable);
            sc.setRequestAttribute(request, "editable", editable);
            sc.setRequestAttribute(request, "noteditable", noteditable);
            sc.setRequestAttribute(request, "udfId", udfId);
            sc.setRequestAttribute(request, "udf", udf);
            sc.setRequestAttribute(request, "flow", flow);
            selectTaskTab(sc, id, "tabWorkflows", request);
            sc.setRequestAttribute(request, "tabView", new Tab(sc.canAction(Action.manageWorkflows, flow.getId()), false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(flow.canManage(), true));
            sc.setRequestAttribute(request, "cancelAction", "/WorkflowUdfViewAction.do");
            sc.setRequestAttribute(request, "canEdit", flow.canManage() && sc.canAction(Action.manageWorkflows, flow.getId()));
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_WORKFLOW_MSTATUS_UDF_PERMISSIONS_EDIT);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_MSTATUS_UDF_PERMISSIONS_EDIT));
            return mapping.findForward("udfPermissionEditJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            MstatusUdfPermissionForm tf = (MstatusUdfPermissionForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");

            String udfId = tf.getUdfId();
            String id = GeneralAction.getInstance().taskHeader(tf, sc, request, false);

            if (sc.canAction(Action.manageWorkflows, id)) {
                String canview = tf.getHiddencanview();
                StringTokenizer tk = new StringTokenizer(canview, FValue.DELIM);
                ArrayList<String> mstatusViewIds = new ArrayList<String>();
                while (tk.hasMoreElements()) {
                    String token = tk.nextToken();
                    mstatusViewIds.add(token);
                }
                String canedit = tf.getHiddencanedit();
                tk = new StringTokenizer(canedit, FValue.DELIM);
                ArrayList<String> mstatusEditIds = new ArrayList<String>();
                while (tk.hasMoreElements()) {
                    String token = tk.nextToken();
                    mstatusEditIds.add(token);
                }
                String workflowId = tf.getWorkflowId() == null ? "1" : tf.getWorkflowId();
                ArrayList<SecuredMstatusBean> mstatusList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getMstatusList(sc, workflowId);
                for (SecuredMstatusBean bean : mstatusList) {
                    String view = null, edit = null;
                    if (mstatusViewIds.contains(bean.getId())) {
                        view = CategoryConstants.VIEW_ALL;
                    }

                    if (mstatusEditIds.contains(bean.getId())) {
                        edit = CategoryConstants.EDIT_ALL;
                    }
                    AdapterManager.getInstance().getSecuredUDFAdapterManager().setMstatusUDFRule(sc, udfId, bean.getId(), view, edit);
                }
            }


            return mapping.findForward("udfPermissionEditPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
