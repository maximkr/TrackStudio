package com.trackstudio.action.task;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.Slider;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.TaskForm;
import com.trackstudio.secured.SecuredSearchItem;
import com.trackstudio.secured.SecuredSearchTaskItem;
import com.trackstudio.secured.SecuredTaskBean;

public class SimilarAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(SimilarAction.class);
    public static final NumberFormat NF = new DecimalFormat("#0.#");

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TaskForm tf = (TaskForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(tf, sc, request, true);
            String page = request.getParameter("sliderPage");
            if (!sc.allowedByACL(id))
                return null;
            String contextPath = request.getContextPath();
            HashMap<SecuredTaskBean, Float> tasks = AdapterManager.getInstance().getSecuredTaskAdapterManager().findSimilar(sc, id);
            ArrayList<SecuredSearchItem> results = new ArrayList<SecuredSearchItem>();
            for (Map.Entry e : tasks.entrySet()) {
                SecuredSearchItem sstask = new SecuredSearchTaskItem((SecuredTaskBean) e.getKey(), "", "");
                results.add(sstask);
            }
            Slider<SecuredSearchItem> slider = new Slider<SecuredSearchItem>(results, tf.isAll() ? results.size() : 20, null, page == null ? 1 : Integer.valueOf(page));
            slider.setAll(tf.isAll(), sc.getLocale());
            sc.setRequestAttribute(request, "slider", slider.drawSlider(contextPath + "/SimilarAction.do?method=page&amp;id=" + id, "div", "slider"));
            List<SecuredSearchItem> tasksSlider = slider.getCol();
            sc.setRequestAttribute(request, "tasks", tasksSlider);
            sc.setRequestAttribute(request, "key", "");
            return mapping.findForward("similarJSP");

        } finally {
            if (w) lockManager.releaseConnection();
        }
    }
}