package com.trackstudio.action.task;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.trackstudio.kernel.manager.IndexManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.PredictorServlet;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.Slider;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.TaskForm;
import com.trackstudio.secured.SecuredSearchAttachmentItem;
import com.trackstudio.secured.SecuredSearchItem;
import com.trackstudio.secured.SecuredSearchTaskItem;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.Null;

public class TaskSearchAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(TaskSearchAction.class);
    public static final NumberFormat NF = new DecimalFormat("#0.#");

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TaskForm tf = (TaskForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(tf, sc, request, true);
            request.setCharacterEncoding(Config.getEncoding());
            String contextPath = request.getContextPath();
            String key = sc.getKey().trim();
            String pageNumber = tf.getSliderPage();
            int pn = 1;
            if (Null.isNotNull(pageNumber)) {
                try {
                    pn = Integer.parseInt(pageNumber);
                } catch (Exception exc) {
                    // just skip
                }
            }
            //получаем поисковый аргумент
            String searchIn = request.getParameter("searchIn");
            if (searchIn == null) {
                searchIn = "tasks";
            }

            sc.setRequestAttribute(request, "founduser", AdapterManager.getInstance().getSecuredFindAdapterManager().searchUserByQuickGo(sc, key));
            sc.setRequestAttribute(request, "foundtask", AdapterManager.getInstance().getSecuredFindAdapterManager().searchTaskByQuickGo(sc, key));
            ArrayList<SecuredSearchTaskItem> tasks = AdapterManager.getInstance().getSecuredTaskAdapterManager().fullTextSearch(new SecuredTaskBean(id, sc), key);
            ArrayList<SecuredTaskBean> tasksByNumber = getTasksForPattern(sc, key);
            sc.setRequestAttribute(request, "tasksByNumber", tasksByNumber);
            sc.setRequestAttribute(request, "size", tasksByNumber != null ? tasksByNumber.size() + tasks.size() : tasks.size());
            // Search attachments by name
            ArrayList<SecuredSearchAttachmentItem> attachments = AdapterManager.getInstance().getSecuredAttachmentAdapterManager().fullTextSearch(sc, key);
            Slider<SecuredSearchTaskItem> taskSlider = new Slider<SecuredSearchTaskItem>(tasks, 20, null, searchIn.equals("tasks") ? pn : 1);
            Slider<SecuredSearchAttachmentItem> attachmentSlider = new Slider<SecuredSearchAttachmentItem>(attachments, 20, null, searchIn.equals("attachments") ? pn : 1);
            taskSlider.setAll(tf.isAll(), sc.getLocale());
            attachmentSlider.setAll(tf.isAll(), sc.getLocale());
            sc.setRequestAttribute(request, "key", key);
            sc.setRequestAttribute(request, "tasks", taskSlider);
            sc.setRequestAttribute(request, "taskSlider", taskSlider.drawSlider(contextPath + "/TaskSearchAction.do?method=page&amp;id=" + id + "&amp;searchIn=tasks&amp;autosearch=" + key, "div", "slider"));
            sc.setRequestAttribute(request, "attachments", attachmentSlider);
            sc.setRequestAttribute(request, "attachmentSlider", attachmentSlider.drawSlider(contextPath + "/TaskSearchAction.do?method=page&amp;id=" + id + "&amp;searchIn=attachments&amp;autosearch=" + key, "div", "slider"));
            return mapping.findForward("taskSearchJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public static ArrayList<SecuredTaskBean> getTasksForPattern(SessionContext sc, String key) throws Exception {
        String operands = key;
        ArrayList<SecuredTaskBean> ret = new ArrayList<SecuredTaskBean>();
        if (operands.length() == 0) return ret;
        operands = operands.replaceAll(",", " ");
        operands = operands.replaceAll(";", " ");
        StringTokenizer st = new StringTokenizer(operands, " -", true);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.compareTo(" ") == 0 || token.compareTo("-") == 0) {  // bad filter string
                continue;
            }
            if (token.indexOf('#') == 0) token = token.substring(1);
            try {
                int op1 = Integer.parseInt(token);
                if (!st.hasMoreTokens()) {
                    SecuredTaskBean stb = AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskByNumber(sc, Integer.toString(op1));
                    if (stb != null && sc.taskOnSight(stb.getId()))
                        ret.add(stb);
                }
                if (!st.hasMoreTokens())
                    break;
                token = st.nextToken();
                if (token.compareTo(" ") == 0) { // list of values
                    SecuredTaskBean stb = AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskByNumber(sc, Integer.toString(op1));
                    if (stb != null && sc.taskOnSight(stb.getId()))
                        ret.add(stb);
                } else if (token.compareTo("-") == 0) { // range of values
                    if (!st.hasMoreTokens())
                        return ret;
                    token = st.nextToken();
                    if (token.indexOf('#') == 0) token = token.substring(1);
                    int op2 = Integer.parseInt(token);
                    int count = 0;
                    for (int i = op1; i <= op2; ++i) {
                        SecuredTaskBean stb = AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskByNumber(sc, Integer.toString(i));
                        if (stb != null && sc.taskOnSight(stb.getId())) {
                            ret.add(stb);
                            count = 0;
                        } else if (++count > 5000) i += (op2 - op1) / 10;
                    }
                } else {  // bad filter string
                    return ret;
                }
            } catch (NumberFormatException nfe) {
                /*empty*/
            }
        }
        return ret;
    }
}
