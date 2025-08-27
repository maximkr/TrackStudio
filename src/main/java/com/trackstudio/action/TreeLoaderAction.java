package com.trackstudio.action;

import java.io.PrintWriter;
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
import org.codehaus.jackson.map.ObjectMapper;

import com.trackstudio.app.Preferences;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.comparator.TaskComparator;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.TreeLoaderForm;
import com.trackstudio.form.TreeNode;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.compare.FieldSort;
import com.trackstudio.tools.compare.SortUser;
import com.trackstudio.tools.textfilter.MacrosUtil;
import com.trackstudio.tools.tree.NodeMask;

public class TreeLoaderAction extends TSDispatchAction {
    private final LockManager lockManager = LockManager.getInstance();
    private static Log log = LogFactory.getLog(TreeLoaderAction.class);
    public static final int NODE_GROUPING = Null.getIntegerOrDefaultValue(Config.getProperty("trackstudio.groupItems"), 100); // 100: ����� ����� �� ������ ������ ��������, ��� ������� ���������� �����������
    public static final int MILLIS_IN_A_WEEK = 604800000;
    public static String countOfSubnode = Config.getProperty("trackstudio.maxTreeItems");

    /**
     * Field cotains the name of fields with tree nodes mask.
     */
    public static final String MASK_FIELD = Config.getProperty("trackstudio.tree.node.mask");

    public static final String TODAY_GROUP = "1";
    public static final String YESTERDAY_GROUP = "2";
    public static final String A_WEEK_AGO_GROUP = "3";
    public static final String TWO_WEEKS_AGO_GROUP = "4";
    public static final String OLD_GROUP = "5";
    public static final String USER_GROUP = "6";

    public static String imageServlet = "/ImageServlet/" + GeneralAction.SERVLET_KEY;
    private static final String HTML = "html";
    private static final String FOLDER_IMG = imageServlet + "/" + HTML + "/xtree/images/folder.png";
    private static final String USER_IMG = imageServlet + "/" + HTML + "/xtree/images/userNode.gif";
    private static final String WARNING_IMG = imageServlet + "/cssimages/warning.gif";

    public static final char NUMBER_SIGN = '#';

    private List<SecuredUserBean> getUserChildrenIsActive(SessionContext sc, String userId) {
        List<SecuredUserBean> resultBeans = new ArrayList<SecuredUserBean>();
        try {
            List<SecuredUserBean> beans = AdapterManager.getInstance().getSecuredUserAdapterManager().getChildren(sc, userId);
            for (SecuredUserBean sub : beans) {
                if (sub.isActive()) resultBeans.add(sub);
            }
            return resultBeans;
        } catch (GranException e) {
            log.error("Exception ", e);
        }
        return resultBeans;
    }

    public ActionForward init(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionContext sc = (SessionContext) request.getAttribute("sc");
        if (sc == null) {
            return null;
        }
        SecuredTaskBean root = new SecuredTaskBean("1", sc);
        String nameUserRoot = new SecuredUserBean("1", sc).getName();
        nameUserRoot = nameUserRoot.replace("\"", "&quot;");
        sc.setRequestAttribute(request, "taskTreeRootName", NodeMask.nameByMask(root, true));
        sc.setRequestAttribute(request, "userTreeRootName", nameUserRoot);
        sc.setRequestAttribute(request, "VERSION_TS", com.trackstudio.app.adapter.AdapterManager.getInstance().getSecuredTSInfoAdapterManager().getTSVersion(null));
        sc.setRequestAttribute(request, "jsAlert", "true".equals(Config.getProperty("trackstudio.show.js.error")));
        sc.setRequestAttribute(request, "sc", sc);
        sc.setRequestAttribute(request, "isCompress", GeneralAction.getInstance().compressHtml());
        sc.setRequestAttribute(request, "encoding" , Config.getEncoding());
        response.setContentType("text/xml; charset=" + Config.getEncoding());
        return mapping.findForward("treeFrameJSP");
    }

    public ActionForward taskLoadChildren(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.trace("##########");
        boolean r = lockManager.acquireConnection();
        try {
            TreeLoaderForm tlf = (TreeLoaderForm) form;
            response.setContentType("text/json; charset=" + Config.getEncoding());
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            if (sc == null) return null;
            String contextPath = request.getContextPath();
            String groupId = request.getParameter("group") != null ? request.getParameter("group") : "0";

            boolean isUdf = tlf.getUdfSelect() != null;

            String number = tlf.getTi();
            PrintWriter out = response.getWriter();
            if (number == null) {
                SecuredTaskBean task = new SecuredTaskBean("1", sc);
                List<TreeNode> children = getTaskXMLTree(sc, contextPath, groupId, isUdf, task);
                final TreeNode root = new TreeNode(task.getId(),
                        MacrosUtil.buildImageForState(task.getStatus(), contextPath + imageServlet)+
                                (children.isEmpty() ? task.getName() : String.format("%s (%s)", task.getName(), children.size())),
                        true,
                        contextPath + imageServlet + "/icons/categories/" + task.getCategory().getIcon(),
                        MacrosUtil.buildImageForState(task.getStatus(), contextPath + imageServlet),
                        null);
                out.print(new ObjectMapper().writeValueAsString(Collections.singletonList(root)));
            } else {
                out.print(new ObjectMapper().writeValueAsString(getTaskXMLTree(sc, contextPath, groupId, isUdf, AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskByNumber(sc, number))));
            }
            out.flush();
        } finally {
            if (r) lockManager.releaseConnection();
        }
        return null;
    }

    public ActionForward userLoadChildren(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.trace("##########");
        boolean r = lockManager.acquireConnection();
        try {
            TreeLoaderForm tlf = (TreeLoaderForm) form;
            response.setContentType("text/json; charset=" + Config.getEncoding());
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            if (sc == null) return null;
            String contextPath = request.getContextPath();
            String login = tlf.getTi();
            PrintWriter out = response.getWriter();
            if (login == null) {
                SecuredUserBean user = new SecuredUserBean("1", sc);
                List<TreeNode> children = getUserXMLTree(sc, contextPath, user);
                final TreeNode root = new TreeNode(user.getLogin(),
                        children.isEmpty() ? user.getName() : String.format("%s (%s)", user.getName(), children.size()),
                        true,
                        contextPath + USER_IMG,
                        null,
                        null);
                out.print(new ObjectMapper().writeValueAsString(Collections.singletonList(root)));
            } else {
                out.print(new ObjectMapper().writeValueAsString(getUserXMLTree(sc, contextPath, AdapterManager.getInstance().getSecuredUserAdapterManager().findByLogin(sc, login))));
            }
            out.flush();
        } finally {
            if (r) lockManager.releaseConnection();
        }
        return null;
    }

    private List<TreeNode> getUserXMLTree(SessionContext sc, String contextPath, SecuredUserBean root) throws GranException {
        boolean lock = lockManager.acquireConnection(TreeLoaderAction.class.getName());
        try {
            List<SecuredUserBean> userBeansAll = AdapterManager.getInstance().getSecuredUserAdapterManager().getChildren(sc, root.getId());
            List<SecuredUserBean> userBeans = new ArrayList<SecuredUserBean>();
            for (SecuredUserBean user : userBeansAll) {
                if (user.isActive()) {
                    userBeans.add(user);
                }
            }
            Collections.sort(userBeans, new SortUser(FieldSort.NAME));
            final List<TreeNode> nodes = new ArrayList<TreeNode>();
            for (SecuredUserBean ub : userBeans) {
                nodes.add(
                        new TreeNode(
                                ub.getLogin(), ub.getName(),
                                ub.getChildCount() > 0, contextPath + USER_IMG,
                                null,
                                ub.getLogin()
                        )
                );
            }
            return nodes;
        } finally {
            if (lock) lockManager.releaseConnection(TreeLoaderAction.class.getName());
        }
    }

    private List<TreeNode> getTaskXMLTree(final SessionContext sc, final String contextPath, String groupId, final boolean udf, SecuredTaskBean task) throws GranException {
        List<TreeNode> nodes = new ArrayList<TreeNode>();
        List<SecuredTaskBean> taskBeans = AdapterManager.getInstance().getSecuredTaskAdapterManager().getNotFinishChildren(sc, task.getId());
        List<SecuredTaskBean> taskBeansProjects = new ArrayList<SecuredTaskBean>();
        List<SecuredTaskBean> taskBeansProjectsNotTask = new ArrayList<SecuredTaskBean>();
        for (SecuredTaskBean taskBean : taskBeans) {
            boolean sortOrder = Preferences.isSortOrgerInTree(taskBean.getCategory().getPreferences());
            boolean hide = !Preferences.getHiddenInTree(taskBean.getCategory().getPreferences()).equals("N");
            if (sc.taskOnSight(taskBean.getId()) && hide) {
                if (!sortOrder) {
                    taskBeansProjectsNotTask.add(taskBean);
                } else {
                    taskBeansProjects.add(taskBean);
                }
            }
        }
        TaskComparator.sort(Collections.singletonList(FValue.SUB + FieldMap.TASK_NAME.getFieldKey()), null,  taskBeansProjects);
        TaskComparator.sort(Collections.singletonList(FieldMap.TASK_UPDATEDATE.getFieldKey()), null,  taskBeansProjectsNotTask);

        taskBeans.clear();
        for (SecuredTaskBean taskBean : taskBeansProjects)
            taskBeans.add(taskBean);
        for (SecuredTaskBean taskBean : taskBeansProjectsNotTask)
            taskBeans.add(taskBean);

        if (countOfSubnode != null) {
            int num = Integer.parseInt(countOfSubnode);
            if (taskBeans.size() > num) {
                taskBeans = taskBeans.subList(0, num);
            }
        }
        final List<SecuredTaskBean> temp = new ArrayList<SecuredTaskBean>(taskBeans);
        for (SecuredTaskBean stb : temp) {
            nodes.add(buildTaskNode(sc, contextPath, udf, stb));
        }
        return nodes;
    }

    private TreeNode buildTaskNode(SessionContext sc, String contextPath, boolean udf, SecuredTaskBean stb) throws GranException {
        int total = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTotalNotFinishChildren(sc, stb.getId());
        return new TreeNode(stb.getNumber(),
                MacrosUtil.buildImageForState(stb.getStatus(), contextPath + imageServlet) + NodeMask.nameByMask(stb, true), total > 0,
                contextPath + imageServlet + "/icons/categories/" + stb.getCategory().getIcon(),
                "",
                String.format("#%s %s", stb.getNumber(), stb.getName()));
    }


}