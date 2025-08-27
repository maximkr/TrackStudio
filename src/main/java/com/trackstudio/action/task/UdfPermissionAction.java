package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
import com.trackstudio.constants.CategoryConstants;
import com.trackstudio.form.MstatusPermissionForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.secured.SecuredWorkflowUDFBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;

public class UdfPermissionAction extends TSDispatchAction {

    public ActionForward page(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            MstatusPermissionForm form = (MstatusPermissionForm) actionForm;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);

            if (sc == null) return null;

            String id = GeneralAction.getInstance().taskHeader(form, sc, request, true);
            form.setId(id);
            String idUDF = form.getWorkflowId() == null ? "1" : form.getWorkflowId();
            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, idUDF);
            String mstatusId = form.getMstatusId();
            SecuredMstatusBean mstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, mstatusId);
            List<SecuredWorkflowUDFBean> udfs = flow.getWorkflowUDFs();
            List<String> editable = KernelManager.getUdf().getEditableUDFId(mstatus.getId());
            List<String> viewable = KernelManager.getUdf().getViewableUDFId(mstatus.getId());
            Set<String> view = new TreeSet<String>();
            Set<String> edit = new TreeSet<String>();

            for (SecuredWorkflowUDFBean bean : udfs) {
                if (editable.contains(bean.getId())) {
                    edit.add(bean.getId());
                }
                if (viewable.contains(bean.getId())) {
                    view.add(bean.getId());
                }
            }

            view.addAll(edit);
            form.setView(view.toArray(new String[]{}));
            form.setEdit(edit.toArray(new String[]{}));

            HashMap<Integer, String> type = new HashMap<Integer, String>();

            type.put(UdfValue.STRING, I18n.getString(sc.getLocale(), "UDF_STRING"));
            type.put(UdfValue.MEMO, I18n.getString(sc.getLocale(), "UDF_MEMO"));
            type.put(UdfValue.FLOAT, I18n.getString(sc.getLocale(), "UDF_FLOAT"));
            type.put(UdfValue.INTEGER, I18n.getString(sc.getLocale(), "UDF_INTEGER"));
            type.put(UdfValue.DATE, I18n.getString(sc.getLocale(), "UDF_DATE"));
            type.put(UdfValue.LIST, I18n.getString(sc.getLocale(), "UDF_LIST"));
            type.put(UdfValue.MULTILIST, I18n.getString(sc.getLocale(), "UDF_MULTILIST"));
            type.put(UdfValue.TASK, I18n.getString(sc.getLocale(), "UDF_TASK"));
            type.put(UdfValue.USER, I18n.getString(sc.getLocale(), "UDF_USER"));
            type.put(UdfValue.URL, I18n.getString(sc.getLocale(), "UDF_URL"));


            ArrayList<CustomEditAction.UdfBeanListItem> udfList = new ArrayList<CustomEditAction.UdfBeanListItem>();
            for (SecuredWorkflowUDFBean ri : udfs) {
                CustomEditAction.UdfBeanListItem fli = new CustomEditAction.UdfBeanListItem(ri.getId(), ri.getCaption(), type.get(ri.getType()), ri.getOrder(), ri.canManage());
                udfList.add(fli);
            }
            Collections.sort(udfList);
            sc.setRequestAttribute(request, "udfs", udfList);
            sc.setRequestAttribute(request, "mstatusId", mstatusId);
            sc.setRequestAttribute(request, "mstatus", mstatus);
            sc.setRequestAttribute(request, "flow", flow);
            selectTaskTab(sc, id, "tabWorkflows", request);
            sc.setRequestAttribute(request, "canEdit", flow.canManage() && sc.canAction(Action.manageWorkflows, flow.getId()));
            sc.setRequestAttribute(request, "tabView", new Tab(sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()), false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(flow.canManage() && sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()), false));

            sc.setRequestAttribute(request, "tabResolutions", new Tab(flow.canManage() && sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()), false));
            sc.setRequestAttribute(request, "tabTransitions", new Tab(flow.canManage() && sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()), false));
            sc.setRequestAttribute(request, "tabPermissions", new Tab(sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()), false));
            sc.setRequestAttribute(request, "tabTriggers", new Tab(flow.canManage() && sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()), false));
            sc.setRequestAttribute(request, "tabUdfPermissions", new Tab(flow.canView() && sc.canAction(Action.manageWorkflows, flow.getTaskId()), true));
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_WORKFLOW_MSTATUS_UDF_PERMISSIONS);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_MSTATUS_UDF_PERMISSIONS));
            sc.setRequestAttribute(request, "tabScheduler", new Tab(flow.canManage(), false));
            return mapping.findForward("udfPermissionJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward save(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            MstatusPermissionForm tf = (MstatusPermissionForm) actionForm;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null) return null;
            String id = GeneralAction.getInstance().taskHeader(tf, sc, request, false);
            tf.setId(id);
            String idUDF = tf.getWorkflowId() == null ? "1" : tf.getWorkflowId();
            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, idUDF);
            String mstatusId = tf.getMstatusId();
            List<SecuredWorkflowUDFBean> udfs = flow.getWorkflowUDFs();

            String[] canedit = tf.getEdit();
            String[] canview = tf.getView();

            List<String> canViewUdfs = canview != null ? Arrays.asList(canview) : new ArrayList<String>();
            List<String> canEditUdfs = canedit != null ? Arrays.asList(canedit) : new ArrayList<String>();

            for (SecuredWorkflowUDFBean bean : udfs) {
                String view = null, edit = null;
                if (canViewUdfs.contains(bean.getId())) {
                    view = CategoryConstants.VIEW_ALL;
                }

                if (canEditUdfs.contains(bean.getId())) {
                    edit = CategoryConstants.EDIT_ALL;
                }
                AdapterManager.getInstance().getSecuredUDFAdapterManager().setMstatusUDFRule(sc, bean.getId(), mstatusId, view, edit);
            }

            return mapping.findForward("udfPermissionPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
