package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Collections;

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
import com.trackstudio.kernel.cache.PluginCacheManager;
import com.trackstudio.kernel.cache.PluginType;
import com.trackstudio.secured.SecuredTemplateBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;

public class TemplateEditAction extends TSDispatchAction {

    private static Log log = LogFactory.getLog(TemplateEditAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TemplateForm tf = (TemplateForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String taskId = GeneralAction.getInstance().taskHeader(tf, sc, request, true);
            String userId = sc.getUserId();
            String templateId = tf.getTemplateId();
            boolean createNewTemplate = templateId == null || templateId.length() == 0;
            SecuredUserBean currentUser = new SecuredUserBean(userId, sc);

            ArrayList users = AdapterManager.getInstance().getSecuredAclAdapterManager().getUserList(sc, taskId);
            Collections.sort(users);
            sc.setRequestAttribute(request, "users", users);

            SecuredTemplateBean record = null;
            if (!createNewTemplate) {
                record = AdapterManager.getInstance().getSecuredFindAdapterManager().findTemplateById(sc, templateId);

                tf.setActive(record.getActive());
                tf.setTemplateId(record.getId());
                tf.setName(record.getName());
                tf.setDescription(record.getDescription());
                tf.setUser(record.getUserId());
                tf.setFolder(record.getFolder());
                sc.setRequestAttribute(request, "templateOwner", record.getOwner());
                sc.setRequestAttribute(request, "template", record);
            }

            sc.setRequestAttribute(request, "currentUser", currentUser);
            sc.setRequestAttribute(request, "canEditTemplate", sc.canAction(Action.manageTaskTemplates, taskId));
            sc.setRequestAttribute(request, "templateId", templateId);
            sc.setRequestAttribute(request, "templates", PluginCacheManager.getInstance().list(PluginType.WEB).get(PluginType.WEB));
            sc.setRequestAttribute(request, "tabView", new Tab(!createNewTemplate, false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(true, true));
            sc.setRequestAttribute(request, "showViewSubtasks", true);
            sc.setRequestAttribute(request, "showViewTask", true);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_TEMPLATE_PROPERTIES);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_TEMPLATE_PROPERTIES));
            return mapping.findForward("templateEditJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TemplateForm tf = (TemplateForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = tf.getId();
            String templateId = tf.getTemplateId();

            if (templateId == null || templateId.length() == 0) {
                templateId = AdapterManager.getInstance().getSecuredTemplateAdapterManager().createTemplate(sc, tf.getName(), tf.getDescription(), id, (tf.getUser() != null && tf.getUser().length() != 0) ? tf.getUser() : null, tf.getFolder(), tf.getActive() != null && tf.getActive() ? 1 : 0);
            } else {
                AdapterManager.getInstance().getSecuredTemplateAdapterManager().updateTemplate(sc, templateId, tf.getName(), tf.getDescription(), tf.getUser(), tf.getFolder(), tf.getActive() != null && tf.getActive() ? 1 : 0);
            }
            tf.setTemplateId(templateId);
            tf.setMutable(false);
            return mapping.findForward("templateViewPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
