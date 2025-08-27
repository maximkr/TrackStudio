package com.trackstudio.action.task;

import java.net.URLEncoder;

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
import com.trackstudio.secured.SecuredTemplateBean;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;

public class TemplateViewAction extends TSDispatchAction {

    private static Log log = LogFactory.getLog(TemplateViewAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TemplateForm tf = (TemplateForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(tf, sc, request, true);
            String templateId = tf.getTemplateId();
            SecuredTemplateBean record = AdapterManager.getInstance().getSecuredFindAdapterManager().findTemplateById(sc, templateId);
            sc.setRequestAttribute(request, "name", URLEncoder.encode(record.getName(), Config.getEncoding()));
            sc.setRequestAttribute(request, "template", record);
            sc.setRequestAttribute(request, "tabView", new Tab(true, true));
            sc.setRequestAttribute(request, "tabEdit", new Tab(record.canManage() && sc.canAction(Action.manageTaskTemplates, id), false));

            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_TEMPLATE_OVERVIEW);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_TEMPLATE_OVERVIEW));
            return mapping.findForward("templateViewJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
