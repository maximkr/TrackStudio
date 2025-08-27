package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import com.trackstudio.form.TemplateForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTemplateBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.Tab;

public class TemplateAction extends TSDispatchAction {

    private static Log log = LogFactory.getLog(TemplateAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TemplateForm tf = (TemplateForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(tf, sc, request, true);
            SecuredTaskBean tci = new SecuredTaskBean(id, sc);
            List<SecuredTemplateBean> templateList = AdapterManager.getInstance().getSecuredTemplateAdapterManager().getAllTemplatesList(sc, id);

            ArrayList<SecuredTemplateBean> templates = new ArrayList<SecuredTemplateBean>();
            EggBasket<SecuredTaskBean, SecuredTemplateBean> parentTemplateSet = new EggBasket<SecuredTaskBean, SecuredTemplateBean>();
            EggBasket<SecuredTaskBean, SecuredTemplateBean> childrenTemplateSet = new EggBasket<SecuredTaskBean, SecuredTemplateBean>();
            ArrayList<EggBasket> seeAlso = new ArrayList<EggBasket>();
            ArrayList<SecuredTaskBean> parentTasks = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskChain(sc, null, id);

            for (SecuredTemplateBean smi : templateList) {
                SecuredTaskBean task = smi.getTask();
                if (task.canView() && sc.canAction(Action.manageTaskTemplates, task.getId())) {
                    if (task.getId().equals(id)) {
                        templates.add(smi);
                    } else if (parentTasks.contains(task)) {
                        parentTemplateSet.putItem(task, smi);
                    } else childrenTemplateSet.putItem(task, smi);
                }

            }
            Collections.sort(templates);
            sc.setRequestAttribute(request, "templates", templates);

            if (!parentTemplateSet.isEmpty()) seeAlso.add(parentTemplateSet);
            if (!childrenTemplateSet.isEmpty()) seeAlso.add(childrenTemplateSet);

            sc.setRequestAttribute(request, "seeAlso", seeAlso);
            sc.setRequestAttribute(request, "canDelete", sc.canAction(Action.manageTaskTemplates, id) && tci.canManage());
            sc.setRequestAttribute(request, "canCreateObject", sc.canAction(Action.manageTaskTemplates, id) && tci.canManage());
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_TEMPLATE_LIST);
            sc.setRequestAttribute(request, "tabTemplate", new Tab(sc.canAction(Action.manageTaskTemplates, id), true));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_TEMPLATE_LIST));
            return mapping.findForward("templateListJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TemplateForm tf = (TemplateForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String[] mis = tf.getDelete();
            if (mis != null) {
                for (String mi : mis)
                    AdapterManager.getInstance().getSecuredTemplateAdapterManager().deleteTemplate(sc, mi);
            }
            return mapping.findForward("templateListPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        log.trace("##########");
        return mapping.findForward("templateEditPage");
    }

    public ActionForward clone(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TemplateForm tf = (TemplateForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String[] mis = tf.getDelete();
            if (mis != null) {
                for (String mi : mis) {
                    SecuredTemplateBean stb = AdapterManager.getInstance().getSecuredFindAdapterManager().findTemplateById(sc, mi);
                    AdapterManager.getInstance().getSecuredTemplateAdapterManager().createTemplate(sc, stb.getName() + "_clone", stb.getDescription(), stb.getTaskId(), stb.getUserId(), stb.getFolder(), stb.getActive() ? 1 : 0);
                }
            }
            return mapping.findForward("templateListPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
