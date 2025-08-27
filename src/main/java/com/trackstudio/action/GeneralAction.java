/* SPDX-License-Identifier: Apache-2.0
   Copyright (c) 2025 Your Name */
package com.trackstudio.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;

import com.trackstudio.app.Preferences;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.adapter.auth.SSLCerfAuthAdapter;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.app.session.SessionManager;
import com.trackstudio.containers.PrstatusListItem;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.BaseForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Prstatus;
import com.trackstudio.model.Registration;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.securedkernel.SecuredTSInfoAdapterManager;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.MD5;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.formatter.DateFormatter;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import static com.trackstudio.tools.Null.isNotNull;

public class GeneralAction {
    public enum Manage {
        TASK, USER
    }

    private static GeneralAction instance;
    private static Log log = LogFactory.getLog(GeneralAction.class);

    public static synchronized GeneralAction getInstance() {
        if (instance == null)
            instance = new GeneralAction();
        return instance;
    }

    public SessionContext imports(HttpServletRequest request, HttpServletResponse response) throws GranException {
        return imports(request, response, false);
    }

    public SessionContext imports(HttpServletRequest request, HttpServletResponse response, boolean noForward) throws GranException {
        log.trace("##########");
        if (request.getAttribute("startTime") == null)
            request.setAttribute("startTime", System.currentTimeMillis());
        request.setAttribute("tsHost", Config.getInstance().isTSHost() ? "true" : null);
        String versionPath = Config.getVersionPath();
        SessionContext sc = null;
        String sessionId;
        if (request.getParameter("autologin") != null)
            request.getSession().setAttribute("autologin", request.getParameter("autologin"));
        if (request.getParameter("autopassword") != null)
            request.getSession().setAttribute("autopassword", request.getParameter("autopassword"));
        String autologin = (String) request.getSession().getAttribute("autologin");
        String autopassword = (String) request.getSession().getAttribute("autopassword");
        if (autologin != null && autopassword != null) {
            sessionId = AdapterManager.getInstance().getSecuredUserAdapterManager().authenticate(autologin, autopassword, request);
            if (sessionId != null) {
                sc = SessionManager.getInstance().getSessionContext(sessionId);
                sc.setSessionInCookies(false);
            }
        }
        if (sc == null) {
            ArrayList<String> sessionIds = SessionContext.getCookies(request, response);

            for (String sessId : sessionIds) {
                sc = SessionManager.getInstance().getSessionContext(sessId);
                if (sc != null) break;
            }
            if (sc != null)
                sc.setSessionInCookies(true);
        }
        if (sc == null) {
            // TODO: Throw out
            sessionId = request.getParameter("session");
            sc = SessionManager.getInstance().getSessionContext(sessionId);
            if (sc != null)
                sc.setSessionInCookies(false);
        }
        if (sc == null) {
            SSLCerfAuthAdapter adapter = new SSLCerfAuthAdapter();
            if (adapter.authorizeImpl(null, null, true, request)) {
                String userScId = SessionManager.getInstance().create(adapter.getUser());
                sc = SessionManager.getInstance().getSessionContext(userScId);
                AdapterManager.getInstance().getSecuredUserAdapterManager().updateLastLogonDate(sc, sc.getUserId());
            }
        }
        if (sc == null) {
            sc = createSessionFromJOSSO(request);
        }
        if (sc == null) {
            sc = createSessionForAnonymous();
        }
        if (sc == null) {
            response.setContentType("text/html; charset=" + Config.getEncoding());
            // erase ts-session cookie
            SessionContext.resetCookies(request, response);
            try {

                response.setContentType("text/html; charset=" + Config.getEncoding());
                // erase ts-session cookie
                SessionContext.resetCookies(request, response);
                PrintWriter out = response.getWriter();
                String lastPath = "";
                if (request.getParameter("return") != null) {
                    lastPath = request.getParameter("return");
                } else {
                    lastPath = URLEncoder.encode(request.getRequestURL() + (request.getQueryString() != null ? '?' + request.getQueryString() : ""), "UTF-8");
                }
                out.println("<script type=\"text/javascript\">\n" +
                        "                document.location.replace('" + request.getContextPath() + "/LoginAction.do?method=loginPage&lastPath=" + lastPath + "');\n" +
                        "</script>");
                out.flush();
                out.close();
                return null;
            } catch (IOException io) {
                throw new GranException(io);
            }
        }

        request.setAttribute("request", request);    //this line is important!!!
        request.setAttribute("response", response);
        sc.setRequestAttribute(request, "sc", sc);
        sc.setRequestAttribute(request, "contextPath", request.getContextPath());
        sc.setRequestAttribute(request, "remoteHost", parseRemoteHost(request.getRequestURL()));
        sc.setRequestAttribute(request, "versionPath", versionPath);
        sc.setRequestAttribute(request, "versionTS", com.trackstudio.app.adapter.AdapterManager.getInstance().getSecuredTSInfoAdapterManager().getTSVersion(null));
        sc.setRequestAttribute(request, "domain", request.getRemoteHost());
        sc.setRequestAttribute(request, "true", Boolean.TRUE);
        sc.setRequestAttribute(request, "session", sc.getSession());
        boolean runStartClock = Config.getProperty("trackstudio.runClockWhenOpenTask") == null || Config.isTurnItOn("trackstudio.runClockWhenOpenTask");
        sc.setRequestAttribute(request, "runClockWhenOpenTask", runStartClock);
        sc.setRequestAttribute(request, "imageView", Config.getProperty("trackstudio.preview.image", "400"));
        // log.debug("Login as another user problem diagnostic info: user=" + sc.getUser().getLogin() + "; locale=" + sc.getLocale());
        request.getSession().setAttribute(Globals.LOCALE_KEY, new Locale(sc.getLocale()));

        sc.setRequestAttribute(request, "ImageServlet", "/ImageServlet/" + SERVLET_KEY);

        // log.debug(" decimalFormatUdfFloat" + Config.getProperty("trackstudio.decimalFormatUdfFloat", "3"));
        sc.setRequestAttribute(request, "decimalFormatUdfFloat", Config.getProperty("trackstudio.decimalFormatUdfFloat", "3"));
        sc.setRequestAttribute(request, "defaultLimit", Config.getProperty("trackstudio.default.limit", "200"));
        sc.setRequestAttribute(request, "searchDelay", Config.getProperty("trackstudio.search.delay", "0"));
        return sc;
    }

    public SessionContext createSessionFromJOSSO(HttpServletRequest request) throws GranException {
        log.debug(" createSessionFromJOSSO ");
        if (Config.isTurnItOn("trackstudio.useJOSSO")) {
            String login = request.getRemoteUser();
            log.debug(" find user : " + login);
            if (login != null) {
                log.debug(" create session ");
                return buildSession(login);
            }
        }
        return null;
    }

    public static SessionContext buildSession(String login) throws GranException {
        SessionContext session = null;
        String userId = KernelManager.getUser().findByLogin(login);
        if (userId != null) {
            UserRelatedInfo uri = UserRelatedManager.getInstance().find(userId);
            if (uri.isEnabled()) {
                String sessionId = SessionManager.getInstance().create(uri);
                session = SessionManager.getInstance().getSessionContext(sessionId);
            }
        }
        return session;
    }

    public SessionContext createSessionForAnonymous() throws GranException {
        return buildSession(UserRelatedInfo.ANONYMOUS_USER);
    }

    public static final String SERVLET_KEY = MD5.encode(String.valueOf(System.currentTimeMillis()));

    /**
     * This method checks the user information sent in the Authorization
     * header against the database of users maintained in the users Hashtable.
     *
     * @param req request
     * @return session id
     * @throws IOException
     */
    protected SessionContext HTTPAuthenticate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String auth = req.getHeader("Authorization");
        if (auth == null) return null;  // no auth
        if (!auth.toUpperCase(Locale.ENGLISH).startsWith("BASIC "))
            return null;  // we only do BASIC
        // Get encoded user and password, comes after "BASIC "
        String userpassEncoded = auth.substring(6);
        // Decode it, using any base 64 decoder
        String userpassDecoded = URLDecoder.decode(userpassEncoded, Config.getEncoding());
        // Check our user list to see if that user and password are "allowed"
        int delim = userpassDecoded.indexOf(':');
        if (delim == -1) return null;
        String login = userpassDecoded.substring(0, delim);
        String pwd = userpassDecoded.substring(delim + 1);
        try {
            String sessionId = AdapterManager.getInstance().getSecuredUserAdapterManager().authenticate(login, pwd, req);
            return SessionManager.getInstance().getSessionContext(sessionId);
        } catch (GranException e) {
            System.out.println("Authentication failed for " + login + ". " + e.getMessage());
            return null;
        }
    }

    private void commonHeader(SessionContext sc, HttpServletRequest request, Manage manage) throws GranException {
        sc.setRequestAttribute(request, "NOT_CHOOSEN", I18n.getString(sc, "NOT_CHOOSEN"));
        sc.setRequestAttribute(request, "tsVersionDescription", Config.TRACKSTUDIO_VERSION);
        request.setAttribute("tsHost", Config.getInstance().isTSHost() ? "true" : null);
        String useGoogle = Config.getProperty("trackstudio.nogoogle");
        if (useGoogle == null || "true".equals(useGoogle)) {
            request.setAttribute("noGoogle", "true");
        }
        sc.setRequestAttribute(request, "decimalSepatator", new DecimalFormatSymbols(DateFormatter.toLocale(sc.getLocale())).getDecimalSeparator());
        sc.setRequestAttribute(request, "charSet", Config.getEncoding());

        sc.setRequestAttribute(request, "useAnchor", "true".equals(Config.getProperty("use.anchor")));

        String userId = (String) sc.getAttribute("userId");
        String taskId = (String) sc.getAttribute("taskId");
        if (userId == null || !sc.userOnSight(userId)) {
            userId = sc.getUserId();
        }
        if (taskId == null || !sc.taskOnSight(taskId)) {
            taskId = "1";
        }
        SecuredTaskBean tci = new SecuredTaskBean(taskId, sc);
        SecuredUserBean currentUser = new SecuredUserBean(userId, sc);
        sc.setRequestAttribute(request, "tci", tci);
        Object object = sc.getAttribute("jsEvent");
        sc.removeAttribute("jsEvent");
        sc.setRequestAttribute(request, "jsEvent", object);
        String taskNameEncoded = tci.getName();
        int j = -1;
        j = taskNameEncoded.indexOf('\\', j + 1);
        while (j > -1) {
            taskNameEncoded = taskNameEncoded.substring(0, j + 1) + taskNameEncoded.substring(j);
            j = taskNameEncoded.indexOf('\\', j + 2);
        }
        j = -1;
        j = taskNameEncoded.indexOf('\'', j + 1);
        while (j > -1) {
            taskNameEncoded = taskNameEncoded.substring(0, j) + '\\' + taskNameEncoded.substring(j);
            j = taskNameEncoded.indexOf('\'', j + 2);
        }

        sc.setRequestAttribute(request, "taskNameEncoded", taskNameEncoded);
        sc.setRequestAttribute(request, "taskNumber", "#" + tci.getTaskNumber());
        sc.setRequestAttribute(request, "taskName", tci.getName());
        sc.setRequestAttribute(request, "currentUser", currentUser);
        sc.setRequestAttribute(request, "canViewTask", tci.canManage());
        sc.setRequestAttribute(request, "canViewUser", currentUser.canManage());

        sc.setRequestAttribute(request, "canCreateUser", currentUser.canManage() && sc.canAction(Action.createUser, userId));
        sc.setRequestAttribute(request, "canEditUser", currentUser.canManage() && (sc.getUserId().equals(userId) && sc.canAction(Action.editUserHimself, userId) || sc.canAction(Action.editUserChildren, userId)));
        boolean canViewTaskChildren = tci.canViewChildren();
        if (!canViewTaskChildren) {
            canViewTaskChildren = AdapterManager.getInstance().getSecuredCategoryAdapterManager().hasSubcategories(sc, tci.getCategoryId(), taskId);
        }
        sc.setRequestAttribute(request, "canViewFilters", canViewTaskChildren && sc.canAction(Action.viewFilters, taskId));
        sc.setRequestAttribute(request, "canViewTaskFilters", canViewTaskChildren && sc.canAction(Action.viewFilters, taskId));
        sc.setRequestAttribute(request, "canManageTaskFilters", canViewTaskChildren && (sc.canAction(Action.manageTaskPrivateFilters, taskId) || sc.canAction(Action.manageTaskPublicFilters, taskId)));
        sc.setRequestAttribute(request, "canViewReports", canViewTaskChildren && sc.canAction(Action.viewReports, taskId));
        sc.setRequestAttribute(request, "canViewUserList", currentUser.canView());
        sc.setRequestAttribute(request, "canViewUserFilters", currentUser.canView() && sc.canAction(Action.viewUserFilters, userId));
        sc.setRequestAttribute(request, "canViewUserCustomization", currentUser.canView() && sc.canAction(Action.manageUserUDFs, userId));
        sc.setRequestAttribute(request, "canManageEmailSchedules", Config.getInstance().isSendMail() && sc.canAction(Action.manageEmailSchedules, taskId));
        sc.setRequestAttribute(request, "canDeleteUser", currentUser.canManage() && (!currentUser.getId().equals(sc.getUserId()) && currentUser.getManagerId() != null && sc.canAction(Action.deleteUser, userId) && (currentUser.getChildCount() == null || currentUser.getChildCount() == 0)));
        sc.setRequestAttribute(request, "canViewUserACL", currentUser.canView() && sc.canAction(Action.manageUserACLs, userId));
        sc.setRequestAttribute(request, "canViewUserAttachments", tci.canView() && sc.canAction(Action.viewTaskAttachments, taskId));

        sc.setRequestAttribute(request, "canViewStatuses", currentUser.canView() && sc.canAction(Action.manageRoles, userId));
        boolean importEnabled = Config.getInstance().isFormMailNotification();

        sc.setRequestAttribute(request, "canManageRegistrations", tci.canManage() && sc.canAction(Action.manageRegistrations, taskId));
        sc.setRequestAttribute(request, "canCreateTaskAttachments", sc.canAction(Action.createTaskAttachments, taskId));
        sc.setRequestAttribute(request, "canCreateTaskMessageAttachments", sc.canAction(Action.createTaskMessageAttachments, taskId));
        sc.setRequestAttribute(request, "canCreateUserAttachments", sc.canAction(Action.createUserAttachments, userId));

        sc.setRequestAttribute(request, "canViewACL", tci.canView() && sc.canAction(Action.manageTaskACLs, taskId));
        sc.setRequestAttribute(request, "canEditTaskACL", tci.canManage() && sc.canAction(Action.manageTaskACLs, taskId));
        sc.setRequestAttribute(request, "canWorkflow", tci.canView() && sc.canAction(Action.manageWorkflows, taskId));
        sc.setRequestAttribute(request, "canCategory", tci.canView() && sc.canAction(Action.manageCategories, taskId));
        sc.setRequestAttribute(request, "canSubscribe", tci.canView() && sc.canAction(Action.manageEmailSchedules, taskId));
        sc.setRequestAttribute(request, "canViewTaskCustomization", tci.canView() && sc.canAction(Action.manageTaskUDFs, taskId));
        sc.setRequestAttribute(request, "canMailImport", importEnabled && tci.canManage() && sc.canAction(Action.manageEmailImportRules, taskId));
        sc.setRequestAttribute(request, "canTaskTemplate", tci.canView() && sc.canAction(Action.manageTaskTemplates, taskId));
        sc.setRequestAttribute(request, "canEditActualBudget", sc.canAction(Action.editTaskActualBudget, taskId));
        sc.setRequestAttribute(request, "canViewAttachment", sc.canAction(Action.viewTaskAttachments, taskId));
        sc.setRequestAttribute(request, "canAllowedByUser", sc.allowedByUser(userId));
        boolean canDelete = tci.getParentId() != null && sc.allowedByACL(tci.getParentId()) && AdapterManager.getInstance().getSecuredCategoryAdapterManager().isCategoryDeletable(sc, taskId, tci.getCategoryId());
        sc.setRequestAttribute(request, "canDeleteTask", canDelete);
        sc.setRequestAttribute(request, "showClipboard", sc.canAction(Action.cutCopyPasteTask, taskId));
        sc.setRequestAttribute(request, "viewScriptsBrowser", sc.canAction(Action.viewScriptsBrowser, taskId));
        sc.setRequestAttribute(request, "viewTemplatesBrowser", sc.canAction(Action.viewTemplatesBrowser, taskId));
        sc.setRequestAttribute(request, "showView", sc.canAction(Action.showView, taskId));
        List<Pair> notices = getEmergyNotices(sc);
        sc.setRequestAttribute(request, "isNotices", notices.size() != 0);
        sc.setRequestAttribute(request, "notices", notices);
        setHomePageUser(sc, request);
    }

/*    public int getSizeHeader(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                BufferedImage image = ImageIO.read(file.toURI().toURL());
                int heigth = (int) (image.getHeight() * 1.2);
                if (heigth > 30) {
                    return heigth;
                }
            }
        } catch (Exception ioe) {
            log.error(ioe);
        }
        return 30;
    }
*/

    public boolean checkedRoleRegistration() throws GranException {
        List<Registration> registrationList = KernelManager.getRegistration().getPublicRegistrationList();
        return registrationList != null && !registrationList.isEmpty() && Config.getInstance().isSendMail();
    }

    private void setHomePageUser(SessionContext sc, HttpServletRequest request) throws GranException {
        SecuredTaskBean defaultProject = sc.getUser().getDefaultProject();
        if (defaultProject != null) {
            sc.setRequestAttribute(request, "homePageNumber", defaultProject.getNumber());
        } else {
            sc.setRequestAttribute(request, "homePageNumber", "1");
        }
    }

    private List<Pair> getEmergyNotices(SessionContext sc) throws GranException {
        List<Pair> notices = new ArrayList<Pair>();
        for (SecuredUserBean sub : AdapterManager.getInstance().getSecuredUserAdapterManager().getManagerUser(sc)) {
            if (!sub.getId().equals(sc.getUserId())) {
                StringBuilder date = new StringBuilder();
                date.append(sub.getUser().getName());
                date.append(" ").append(Null.stripNullText(sc.getUser().getDateFormatter().parse(sub.getUser().getEmergencyNoticeDate())));
                String notice = sub.getUser().getEmergencyNotice();
                if (notice != null && notice.length() != 0) {
                    notices.add(new Pair(date.toString(), notice));
                }
            }
        }
        return notices;
    }

    /**
     * Метод раскрывает дерево до уровня фокусной задачи определяем из base
     *
     * @param sc      сессия пользователя
     * @param request входной поток
     * @param id      задачи id
     * @throws GranException при необходимости
     */
    public void createPathForTree(SessionContext sc, HttpServletRequest request, String id) throws GranException {
        StringBuilder sb = new StringBuilder("[");
        Iterator<SecuredTaskBean> it = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskChain(sc, null, id).iterator();
        while (it.hasNext()) {
            String number = it.next().getNumber();
            sb.append("'").append(number).append("'");
            if (it.hasNext()) {
                sb.append(",");
            }
        }
        sb.append("]");
        sc.setRequestAttribute(request, "jsTaskPath", sb);
    }

    public String taskHeader(BaseForm base, SessionContext sc, HttpServletRequest request, boolean isView) throws GranException {
        GeneralAction.log.trace("##########");

        String id = getId(request, base);
        SecuredTaskBean tci = new SecuredTaskBean(id, sc);
        if (!tci.isOnSight()) {
            tci = new SecuredTaskBean("1", sc);
            id = tci.getId();
        }
        sc.setAttribute("taskId", id);
        if (isView) {
            commonHeader(sc, request, Manage.TASK);
        }
        sc.setRequestAttribute(request, "id", id);
        sc.setRequestAttribute(request, "taskId", id);
        request.setAttribute("tsHost", Config.getInstance().isTSHost() ? "true" : null);

        String prstatuses = getNameRole(sc, id, sc.getUserId());
        sc.setRequestAttribute(request, "activeMenuItem", "2");
        sc.setRequestAttribute(request, "prstatuses", prstatuses);
        sc.setRequestAttribute(request, "title", "[#" + tci.getNumber() + "] " + HTMLEncoder.encodeTree(tci.getName()));
        sc.setRequestAttribute(request, "originTitle", "[#" + tci.getNumber() + "] " + tci.getName().replaceAll("\"", "'"));
        if (base != null) {
            if (id != null) {
                base.setId(id);
            }
            base.setSession(sc.getSession());
        }

        if (isView && sc.allowedByACL(id)) {
            ArrayList<SecuredCategoryBean> availColl = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getAvailableCategoryList(sc, id);
            ArrayList<SecuredCategoryBean> categoryColl = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getCreatableCategoryList(sc, id);
            categoryColl.retainAll(availColl);

            sc.setRequestAttribute(request, "categories", categoryColl);

            sc.setRequestAttribute(request, "showViewSubtasks", true);
            sc.setRequestAttribute(request, "showViewTask", true);
        }
        if (isView) {
            createPathForTree(sc, request, id);
        }
        return id;

    }

    public String getId(HttpServletRequest request, BaseForm base) {
        Object attributeId = request.getAttribute("id");
        String formId = base != null ? base.getId() : null;
        String requestId = request.getParameter("id");
        String id = formId;
        if (id == null || id.length() == 0) {
            if (attributeId == null) {
                id = requestId;
                if (id == null)
                    id = "1";
            } else id = attributeId.toString();
        }
        return id;
    }

    /**
     * This method returns task id. First it checks the url anchor then default project config and at last the request
     * @param sc SessionContext
     * @param request Request
     * @return task id
     * @throws GranException for unpredictable situation
     */
    public String getTaskIdByProperties(SessionContext sc, HttpServletRequest request) throws GranException {
        Object taskIdo = null;
        if (isNotNull(request.getParameter("anchor"))) {
            taskIdo = KernelManager.getTask().findByNumber(request.getParameter("anchor"));
        }
        String taskId;
        if (taskIdo == null) taskIdo = request.getAttribute("id");
        if (taskIdo == null) taskIdo = request.getParameter("id");
        if (taskIdo == null) {
            taskId = sc.getUser().getDefaultProjectId();
            if (taskId != null) {
                if (!sc.taskOnSight(taskId)) {
                    taskId = "1";
                }
            }
            if (taskId == null || taskId.length() == 0) taskId = "1";
        } else {
            taskId = taskIdo.toString();
        }
        return taskId;
    }

    /**
     * Return user id
     *
     * @param base
     * @param sc
     * @param request
     * @return user id
     * @throws GranException
     */
    public String userHeader(BaseForm base, SessionContext sc, HttpServletRequest request) throws GranException {
        log.trace("##########");
        String id = getUserId(request, base, sc);
        SecuredUserBean uc = new SecuredUserBean(id, sc);
        if (!uc.isOnSight()) {
            id = sc.getUserId();
            uc = new SecuredUserBean(id, sc);
        }
        sc.setAttribute("userId", id);

        commonHeader(sc, request, Manage.USER);

        TreeSet<SecuredPrstatusBean> allowedPrstatuses = new TreeSet<SecuredPrstatusBean>();
        allowedPrstatuses.addAll(AdapterManager.getInstance().getSecuredAclAdapterManager().getUserAllowedPrstatusList(sc, id, sc.getUserId()));
        StringBuffer prstatuses = new StringBuffer();
        for (Iterator pi = allowedPrstatuses.iterator(); pi.hasNext(); ) {
            prstatuses.append(((SecuredPrstatusBean) pi.next()).getName());
            if (pi.hasNext())
                prstatuses.append(", ");
        }

        String userNameEncoded = uc.getName();

        int j = -1;
        j = userNameEncoded.indexOf('\\', j + 1);
        while (j > -1) {
            userNameEncoded = userNameEncoded.substring(0, j + 1) + userNameEncoded.substring(j);
            j = userNameEncoded.indexOf('\\', j + 2);
        }
        j = -1;
        j = userNameEncoded.indexOf('\'', j + 1);
        while (j > -1) {
            userNameEncoded = userNameEncoded.substring(0, j) + '\\' + userNameEncoded.substring(j);
            j = userNameEncoded.indexOf('\'', j + 2);
        }

        sc.setRequestAttribute(request, "userNameEncoded", userNameEncoded);
        sc.setRequestAttribute(request, "activeMenuItem", "1");
        sc.setRequestAttribute(request, "id", id);
        sc.setRequestAttribute(request, "prstatuses", prstatuses);
        sc.setRequestAttribute(request, "currentUser", uc);

        request.setAttribute("tsHost", Config.getInstance().isTSHost() ? "true" : null);

        String jsUserPath = "";
        for (String s : UserRelatedManager.getInstance().getUserIdChain(null, id)) {
            SecuredUserBean sub = new SecuredUserBean(s, sc);
            if (!sub.getId().equals("1")) {
                String userGrouping = getUserGroup(sc, sub.getId());
                if (!userGrouping.equals("")) jsUserPath += "taskPath[taskPath.length]=\"" + userGrouping + "\";\n";
                jsUserPath += "taskPath[taskPath.length]=\"" + sub.getId() + "\";\n";
            }
        }
        sc.setRequestAttribute(request, "jsUserPath", jsUserPath);
        sc.setRequestAttribute(request, "title",  HTMLEncoder.encodeTree(uc.getName()));
        if (sc.allowedByUser(id)) {
            ArrayList<SecuredPrstatusBean> availablePrstatusList = AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getCreatablePrstatusList(sc, id);
            ArrayList<PrstatusListItem> listPr = new ArrayList<PrstatusListItem>();
            ArrayList<PrstatusListItem> listAdd = new ArrayList<PrstatusListItem>();
            for (SecuredPrstatusBean n : availablePrstatusList) {
                if (Preferences.showInToolbar(n.getPreferences()))
                    listPr.add(new PrstatusListItem(n.getId(), n.getName()));
                else
                    listAdd.add(new PrstatusListItem(n.getId(), n.getName()));
            }
            Collections.sort(listPr);
            sc.setRequestAttribute(request, "prstatusesCreate", listPr);
            Collections.sort(listAdd);
            sc.setRequestAttribute(request, "additionalPrstatuses", listAdd);
        }
        return id;
    }

    public String getUserId(HttpServletRequest request, BaseForm base, SessionContext sc) {
        Object attributeId = request.getAttribute("id");
        String formId = base != null ? base.getId() : null;
        String requestId = request.getParameter("id");
        String id = formId;
        if (id == null) {
            if (attributeId == null) {
                id = requestId;
                if (id == null)
                    id = "1";
            } else id = attributeId.toString();
        }
        id = id == null || id.trim().length() == 0 ? sc.getUserId() : id;
        return id;
    }

    /**
     * Parse URL to "protocol://host" template
     *
     * @param str
     * @return
     */
    private String parseRemoteHost(StringBuffer str) {
        String inputStr = str.toString();
        StringBuffer outputStr = new StringBuffer();
        int count = 0;
        for (int i = 0; i < inputStr.length(); i++) {
            if (inputStr.charAt(i) == '\\' || inputStr.charAt(i) == '/') count++;
            if (count == 3)
                return outputStr.toString();
            outputStr.append(inputStr.charAt(i));
        }
        return outputStr.toString();
    }

    private String getTaskGroup(SecuredTaskBean stb) throws GranException {

        SessionContext sc = stb.getSecure();
        List<SecuredTaskBean> taskBeans = AdapterManager.getInstance().getSecuredTaskAdapterManager().getNotFinishChildren(stb.getSecure(), stb.getParent().getId());
        HashMap<String, List<SecuredTaskBean>> countGroupTasks = GeneralAction.getInstance().getCountTaskGroup(stb.getSecure(), taskBeans);
        List<SecuredTaskBean> todayTasks = countGroupTasks.get("todayTasks");
        List<SecuredTaskBean> yesterdayTasks = countGroupTasks.get("yesterdayTasks");
        List<SecuredTaskBean> weekAgoTasks = countGroupTasks.get("weekAgoTasks");
        List<SecuredTaskBean> twoWeeksAgoTasks = countGroupTasks.get("twoWeeksAgoTasks");
        List<SecuredTaskBean> oldTasks = countGroupTasks.get("oldTasks");
        int childrenCount = taskBeans.size();
        if (childrenCount > TreeLoaderAction.NODE_GROUPING) {
            Calendar currentDate = new GregorianCalendar(new Locale(sc.getLocale()));
            Calendar taskDate = stb.getUpdatedate();
            if (taskDate != null) {
                if ((currentDate.get(Calendar.YEAR) == taskDate.get(Calendar.YEAR)) && (currentDate.get(Calendar.DAY_OF_YEAR) == taskDate.get(Calendar.DAY_OF_YEAR)))
                    return "{ti=" + stb.getParent().getNumber() + ";group=" + TreeLoaderAction.TODAY_GROUP + "; childs='" + todayTasks.size() + "(" + todayTasks.size() + ")';}";
                taskDate.add(Calendar.DAY_OF_YEAR, 1);
                if ((currentDate.get(Calendar.YEAR) == taskDate.get(Calendar.YEAR)) && (currentDate.get(Calendar.DAY_OF_YEAR) == taskDate.get(Calendar.DAY_OF_YEAR)))
                    return "{ti=" + stb.getParent().getNumber() + ";group=" + TreeLoaderAction.YESTERDAY_GROUP + "; childs='" + yesterdayTasks.size() + "(" + yesterdayTasks.size() + ")';}";
                taskDate.add(Calendar.DAY_OF_YEAR, -1);
                if ((currentDate.getTimeInMillis() - TreeLoaderAction.MILLIS_IN_A_WEEK < taskDate.getTimeInMillis()))
                    return "{ti=" + stb.getParent().getNumber() + ";group=" + TreeLoaderAction.A_WEEK_AGO_GROUP + "; childs='" + weekAgoTasks.size() + "(" + weekAgoTasks.size() + ")';}";
                if ((currentDate.getTimeInMillis() - 2 * TreeLoaderAction.MILLIS_IN_A_WEEK < taskDate.getTimeInMillis()))
                    return "{ti=" + stb.getParent().getNumber() + ";group=" + TreeLoaderAction.TWO_WEEKS_AGO_GROUP + "; childs='" + twoWeeksAgoTasks.size() + "(" + twoWeeksAgoTasks.size() + ")';}";
            }
            return "{ti=" + stb.getParent().getNumber() + ";group=" + TreeLoaderAction.OLD_GROUP + "; childs='" + oldTasks.size() + "(" + oldTasks.size() + ")';}";
        }
        return "";
    }

    private String getUserGroup(SessionContext sc, String userId) throws GranException {
        SecuredUserBean sub = new SecuredUserBean(userId, sc);
        int childrenCount = AdapterManager.getInstance().getSecuredUserAdapterManager().getNotDeactivChildren(sc, sub.getParent().getId()).size();
        if (childrenCount > TreeLoaderAction.NODE_GROUPING) {
            return "{ti=" + sub.getUser().getName().substring(0, 1).toUpperCase() + ";group=" + TreeLoaderAction.USER_GROUP + ";}";
        }
        return "";
    }

    /**
     * This method splits the tasks for time folder: older the week, now and etc
     * @param sc SessionContext
     * @param taskBeans tasks
     * @return Map, key - folder, value - lists tasks
     * @throws GranException for unpredictable situation
     */
    public HashMap<String, List<SecuredTaskBean>> getCountTaskGroup(SessionContext sc, List<SecuredTaskBean> taskBeans) throws GranException {
        HashMap<String, List<SecuredTaskBean>> map = new HashMap<String, List<SecuredTaskBean>>();
        List<SecuredTaskBean> todayTasks = new ArrayList<SecuredTaskBean>();
        List<SecuredTaskBean> yesterdayTasks = new ArrayList<SecuredTaskBean>();
        List<SecuredTaskBean> weekAgoTasks = new ArrayList<SecuredTaskBean>();
        List<SecuredTaskBean> twoWeeksAgoTasks = new ArrayList<SecuredTaskBean>();
        List<SecuredTaskBean> oldTasks = new ArrayList<SecuredTaskBean>();
        if (taskBeans.size() > TreeLoaderAction.NODE_GROUPING) {
            Calendar currentDate = new GregorianCalendar(new Locale(sc.getLocale()));
            for (SecuredTaskBean stb : taskBeans) {
                Calendar taskDate = stb.getUpdatedate();
                if (taskDate != null) {
                    if ((currentDate.get(Calendar.YEAR) == taskDate.get(Calendar.YEAR)) && (currentDate.get(Calendar.DAY_OF_YEAR) == taskDate.get(Calendar.DAY_OF_YEAR))) {
                        todayTasks.add(stb);
                        continue;
                    }
                    taskDate.add(Calendar.DAY_OF_YEAR, 1);
                    if ((currentDate.get(Calendar.YEAR) == taskDate.get(Calendar.YEAR)) && (currentDate.get(Calendar.DAY_OF_YEAR) == taskDate.get(Calendar.DAY_OF_YEAR))) {
                        yesterdayTasks.add(stb);
                        continue;
                    }
                    taskDate.add(Calendar.DAY_OF_YEAR, -1);
                    if ((currentDate.getTimeInMillis() - TreeLoaderAction.MILLIS_IN_A_WEEK < taskDate.getTimeInMillis())) {
                        weekAgoTasks.add(stb);
                        continue;
                    }
                    if ((currentDate.getTimeInMillis() - 2 * TreeLoaderAction.MILLIS_IN_A_WEEK < taskDate.getTimeInMillis())) {
                        twoWeeksAgoTasks.add(stb);
                        continue;
                    }
                }
                oldTasks.add(stb);
            }
        }
        map.put("todayTasks", todayTasks);
        map.put("yesterdayTasks", yesterdayTasks);
        map.put("weekAgoTasks", weekAgoTasks);
        map.put("twoWeeksAgoTasks", twoWeeksAgoTasks);
        map.put("oldTasks", oldTasks);
        return map;
    }

    private String getNameRole(SessionContext sc, String taskId, String userId) throws GranException {
        List<Prstatus> allowedPrstatuses = KernelManager.getAcl().getAllowedPrstatusList(taskId, userId);
        StringBuilder prstatuses = new StringBuilder();
        for (Iterator<Prstatus> pi = allowedPrstatuses.iterator(); pi.hasNext(); ) {
            prstatuses.append(pi.next().getName());
            if (pi.hasNext())
                prstatuses.append(", ");
        }
        return prstatuses.toString();
    }

    public Map<String, String> handlerRole(SessionContext sc, HttpServletRequest request, List<SecuredUserBean> handlers, String taskId) throws GranException {
        Map<String, String> handlerRole = new HashMap<String, String>();
        for (SecuredUserBean sub : handlers) {
            handlerRole.put(sub.getId(), getNameRole(sc, taskId, sub.getId()));
        }
        return handlerRole;
    }

    public String compressHtml() {
        return "html";
    }
}
