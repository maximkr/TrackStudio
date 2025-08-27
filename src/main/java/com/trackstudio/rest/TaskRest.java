package com.trackstudio.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.trackstudio.app.session.SessionManager;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.soap.bean.TaskBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Filter;
import com.trackstudio.secured.SecuredAttachmentBean;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredTaskAttachmentBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.textfilter.MacrosUtil;
import com.trackstudio.view.UDFValueViewCSV;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.trackstudio.tools.textfilter.MacrosUtil.getListTask;

public class TaskRest implements RestService  {
    private static Log log = LogFactory.getLog(TaskRest.class);
    private final Map<String, BiFunction<HttpServletRequest, HttpServletResponse, String>> actions = new HashMap<>();

    public TaskRest() {
        actions.put("task", this::task);
        actions.put("tasks", this::tasks);
        actions.put("messages", this::messages);
    }

    private String tasks(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jo = new JSONObject();
        try {
            SessionContext sc = session(req, resp);
            SecuredTaskBean task = AdapterManager.getInstance().getSecuredFindAdapterManager()
                    .findTaskById(sc, req.getParameter("taskId"));
            if (task == null) {
                throw new GranException("task not found: " + req.getParameter("taskId"));
            }
            String filterId = req.getParameter("filterId");
            TaskFValue taskFValue = AdapterManager.getInstance().getSecuredFilterAdapterManager()
                    .getTaskFValue(sc, filterId).getFValue();
            List<SecuredTaskBean> list = getListTask(task, taskFValue, filterId);
            JSONArray tasks = new JSONArray();
            for (SecuredTaskBean bean : list) {
                TaskBean tb = bean.getSOAP();
                tb.setDescription("");
                tasks.put(new JSONObject(tb));
            }
            jo.put("tasks", tasks);
        } catch (GranException e) {
            log.error("Rest auth", e);
            jo.put("error", e.getMessage());
        }
        return jo.toString();
    }

    private String task(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jo = new JSONObject();
        try {
            SessionContext sc = session(req, resp);
            SecuredTaskBean task = AdapterManager.getInstance().getSecuredFindAdapterManager()
                    .findTaskById(sc, req.getParameter("taskId"));
            jo.put("task", new JSONObject(task.getSOAP()));
        } catch (GranException e) {
            log.error("Rest auth", e);
            jo.put("error", e.getMessage());
        }
        return jo.toString();
    }

    private String messages(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jo = new JSONObject();
        try {
            SessionContext sc = session(req, resp);
            SecuredTaskBean task = AdapterManager.getInstance().getSecuredFindAdapterManager()
                    .findTaskById(sc, req.getParameter("taskId"));
            if (task == null) {
                throw new GranException("task not found: " + req.getParameter("taskId"));
            }
            JSONArray messages = new JSONArray();
            for (SecuredMessageBean bean : task.getMessages()) {
                messages.put(new JSONObject(bean.getSOAP()));
            }
            jo.put("messages", messages);
        } catch (GranException e) {
            log.error("Rest auth", e);
            jo.put("error", e.getMessage());
        }
        return jo.toString();
    }

//    private List<MessageJSONBean> getMessages(SecuredTaskBean stb) throws Exception {
//        if (stb != null) {
//            List<MessageJSONBean> messages = new ArrayList<MessageJSONBean>();
//            for (SecuredMessageBean message : stb.getMessages()) {
//                messages.add(message.getRest());
//            }
//            return messages;
//        }
//        return null;
//    }
//
//    private Map<String, String> getAttachments(SecuredTaskBean stb) throws Exception {
//        if (stb != null) {
//            Map<String, String> attachment = new HashMap<String, String>();
//            List<SecuredTaskAttachmentBean> attachmentBeans = stb.getAttachments();
//            if (attachmentBeans != null)
//                for (SecuredAttachmentBean attach : attachmentBeans) {
//                    String link = Config.getProperty("trackstudio.siteURL") + "/download/task/" + stb.getNumber() + "/" + attach.getId();
//                    attachment.put(link, attach.getName());
//                }
//            return attachment;
//        }
//        return new HashMap<String, String>();
//    }
//
//    public List getTasks(String parent, String filter, String login,
//                                       String password, String number) throws Exception {
//        try {
//            SessionContext sc = getSessionContext(login, password);
//            SecuredTaskBean stb = checkTask(sc, parent);
//            if (stb != null) {
//                String filterId = MacrosUtil.getFilterId(filter, stb.getId(), sc.getUserId(), false);
//                if (filterId != null) {
//                    TaskFValue taskFValue = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(sc, filterId).getFValue();
//                    List<SecuredTaskBean> listTasks = getListTask(stb, taskFValue, filterId);
//                    List<TaskJSONBean> taskJSONBeans = new ArrayList<TaskJSONBean>();
//                    int num;
//                    try {
//                        num = Integer.parseInt(number);
//                    } catch (NumberFormatException nfe) {
//                        num = -1;
//                    }
//                    if (num != -1 && num <= listTasks.size()) {
//                        listTasks = listTasks.subList(0, num);
//                    }
//                    for (SecuredTaskBean task : listTasks) {
//                        taskJSONBeans.add(task.getRest());
//                    }
//                    return taskJSONBeans;
//                }
//            }
//        } catch (GranException e) {
//            log.error("getTasks", e);
//            throw new Exception(e);
//        }
//        return null;
//    }
//
//    public List<String> getFilters(String parent,  String login,  String password) throws Exception {
//        try {
//            SessionContext sc = getSessionContext(login, password);
//            SecuredTaskBean stb = checkTask(sc, parent);
//            if (stb != null) {
//                List<Filter> filters = KernelManager.getFilter().getTaskFilterList(stb.getId(), sc.getUserId());
//                List<String> filtersName = new ArrayList<String>();
//                for (Filter filter : filters) {
//                    filtersName.add(filter.getName());
//                }
//                return filtersName;
//            }
//        } catch (Exception e) {
//            log.error("getFilters", e);
//            throw new Exception(e);
//        }
//        return null;
//    }
//
//    private SecuredTaskBean checkTask(SessionContext sc, String parent) {
//        return null;
//    }
//
//    private SessionContext getSessionContext(String login, String password) {
//        return null;
//    }
//
//    private Map<String, String> getUdfs(SecuredTaskBean stb) throws GranException {
//        if (stb != null) {
//            Map<String, String> udfs = new LinkedHashMap<String, String>();
//            for (SecuredUDFValueBean udf : stb.getUDFValuesList()) {
//                String value = new UDFValueViewCSV(udf).getValue(stb);
//                udfs.put(udf.getCaption(), value);
//            }
//            return udfs;
//        }
//        return null;
//    }

    @Override
    public Map<String, BiFunction<HttpServletRequest, HttpServletResponse, String>> actions() {
        return actions;
    }
}
