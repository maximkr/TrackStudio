package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.List;

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
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.Tab;

public class WorkflowUdfViewAction extends TSDispatchAction {


    public ActionForward page(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            CustomForm form = (CustomForm) actionForm;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null) return null;
            String id = GeneralAction.getInstance().taskHeader(form, sc, request, true);

            CustomEditAction.fillViewForm(sc, form, request);
            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, form.getWorkflowId());
            SecuredUDFBean udf = (SecuredUDFBean) request.getAttribute("udf");

            if (udf.getType() == UdfValue.TASK && udf.getInitial() != null) {
                SecuredTaskBean t = new SecuredTaskBean(udf.getInitial(), sc);
                sc.setRequestAttribute(request, "tuval", t);
            } else if (udf.getType() == UdfValue.USER && udf.getInitial() != null) {
                SecuredUserBean t = new SecuredUserBean(udf.getInitial(), sc);
                sc.setRequestAttribute(request, "tuval", t);
            } else if ((udf.getType() == UdfValue.LIST || udf.getType() == UdfValue.MULTILIST) && Null.isNotNull(udf.getDefaultUDF())) {
                sc.setRequestAttribute(request, "defaultList", KernelManager.getFind().findUdflist(udf.getDefaultUDF()).getVal());
            }

            boolean canEdit = udf.canManage() && sc.canAction(Action.manageWorkflows, id);
            boolean canManageRole = sc.canAction(Action.manageRoles, sc.getUserId());
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_WORKFLOW_CUSTOM_FIELD_OVERVIEW);
            selectTaskTab(sc, id, "tabWorkflows", request);
            List<String> editableIds = KernelManager.getUdf().getOperationsWhereUDFIsEditable(udf.getId());
            List<String> viewableIds = KernelManager.getUdf().getOperationsWhereUDFIsViewable(udf.getId());
            List<SecuredMstatusBean> editable = new ArrayList<SecuredMstatusBean>();
            List<SecuredMstatusBean> viewable = new ArrayList<SecuredMstatusBean>();
            for (String i : editableIds) {
                SecuredMstatusBean o = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, i);
                if (!editable.contains(o)) {
                    editable.add(o);
                }
            }
            for (String i : viewableIds) {
                SecuredMstatusBean o = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, i);
                if (!viewable.contains(o)) {
                    viewable.add(o);
                }
            }

            sc.setRequestAttribute(request, "viewable", viewable);
            sc.setRequestAttribute(request, "editable", editable);
            sc.setRequestAttribute(request, "viewPermission", canEdit || canManageRole);
            sc.setRequestAttribute(request, "tabView", new Tab(canEdit, true));
            sc.setRequestAttribute(request, "tabListValues", new Tab(canEdit && (udf.getType().intValue() == UdfValue.LIST || udf.getType().intValue() == UdfValue.MULTILIST), false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(canEdit, false));
            sc.setRequestAttribute(request, "tabPermission", new Tab(canEdit || canManageRole, false));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_CUSTOM_FIELD_PROPERTIES));
            sc.setRequestAttribute(request, "workflowId", flow.getId());

            sc.setRequestAttribute(request, "flow", flow);
            return actionMapping.findForward("workflowUdfViewJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}