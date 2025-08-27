package com.trackstudio.app.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.ActionCacheManager;
import com.trackstudio.kernel.cache.TaskAction;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserAction;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.ProgressManager;

import net.jcip.annotations.ThreadSafe;

/**
 * Контекст пользователя
 */
@ThreadSafe
public class SessionContext implements Serializable, SessionContextInterface {
    private static final Log log = LogFactory.getLog(SessionContext.class);

//    public int progress;

    private volatile String id;
    private final String loggedUserId;
    private volatile String prstatusId;
    private static final String TS_SESSION = "ts-session";

    private volatile boolean sessionInCookies = false;
    private volatile String currentSpace;
    private volatile Calendar prevLogonDate; // previous logon date
    private final ConcurrentMap<Object, Object> session = new ConcurrentHashMap<Object, Object>();
    private volatile String key;
    private final TaskRelatedManager taskManager;
    private volatile static ServletConfig servletConfig;

    /**
     * Возвращает настройки сервлета
     *
     * @return настройки сервлета
     */
    // used in SCRUM config
    public static ServletConfig getServletConfig() {
        return servletConfig;
    }

    /**
     * Устанавливает настройки сервлета
     *
     * @param servletConfig1 настройки сервлета
     */
    public static void setServletConfig(ServletConfig servletConfig1) {
        servletConfig = servletConfig1;
    }


    /**
     * Устанавливает флаг того, записан ли пользовательский контекст в куки
     *
     * @param sessionInCookies значение влага
     */
    public void setSessionInCookies(boolean sessionInCookies) {
        this.sessionInCookies = sessionInCookies;
    }

    /**
     * Возвращает установлен ли флаг наличия контекста пользователя в куках
     *
     * @return TRUE - установлен, FALSE - нет
     */
    public boolean isSessionInCookeies() {
        return sessionInCookies;
    }

    /**
     * Возвращает ID сессии пользователя
     *
     * @return ID сессии
     */
    public String getSession() {
        return isSessionInCookeies() ? "session" : getId();
    }

    /**
     * Конструктор
     *
     * @param user пользователь
     */
    public SessionContext(UserRelatedInfo user) throws GranException {
        this.loggedUserId = user.getId();
        this.prstatusId = user.getPrstatusId();
        taskManager = TaskRelatedManager.getInstance();
    }

    public void setPrstatusId(String prstatusId) {
        this.prstatusId = prstatusId;
    }

    /**
     * Возвращает пользователя
     *
     * @return пользователь
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredUserBean
     */
    public SecuredUserBean getUser() throws GranException {
        return new SecuredUserBean(loggedUserId, this);
    }

    /**
     * Возвращает пользователя по его ID
     *
     * @param id ID пользователя
     * @return пользователь
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredUserBean
     */
    public SecuredUserBean getUser(String id) throws GranException {
        return new SecuredUserBean(getUserId(id), this);
    }

    /**
     * Возвращает ID залогиненного пользователя
     *
     * @return ID пользователя
     */
    public String getUserId() {
        return loggedUserId;
    }

    /**
     * Возвращает ID пользователя
     * если toUserId - child loggedUser-a, то возвращаем loggedUserId, иначе - toUserId
     * используется для создания объектов (script/status/registration/etc), для юзеров
     * находящихся в другой ветке дерева, но на которых есть права.
     *
     * @param toUserId ID пользователя
     * @return ID пользователя
     * @throws GranException при необходимости
     */
    public String getUserId(String toUserId) throws GranException {
        if (toUserId == null)
            return loggedUserId;
        if (KernelManager.getUser().getUserIdChain(loggedUserId, toUserId) != null)
            return loggedUserId;
        else {
            return UserRelatedManager.getInstance().getNearestUserACL(toUserId, loggedUserId, prstatusId);
        }
    }

    /**
     * Устанавливает ID сессии
     *
     * @param id ID сессии
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Возвращает ID сессии
     *
     * @return ID сессии
     */
    public String getId() {
        return this.id;
    }

    /**
     * Возвращает залогиненного пользователя
     *
     * @return пользователь
     * @see com.trackstudio.kernel.cache.UserRelatedInfo
     */
    protected UserRelatedInfo getLoggedUser() {
        try {
            return UserRelatedManager.getInstance().find(loggedUserId);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Проверяет возможность статуса выполнять указанное действие
     *
     * @param r          действие
     * @param prstatusid ID статуса
     * @return TRUE - действие разрешено, FALSE - нет
     * @throws GranException при необходимости
     */
    private boolean can(Action r, String prstatusid) throws GranException {
        List<Action> actions = ActionCacheManager.getInstance().getActions(prstatusid);
        return actions.contains(r);
    }

    /**
     * Проверяет возможность статуса выполнять указанное действие над задачей
     *
     * @param r          действие
     * @param prstatusid ID статуса
     * @return TRUE - действие разрешено, FALSE - нет
     * @throws GranException при необходимости
     */
    public boolean canActionPrstatus(TaskAction r, String prstatusid) throws GranException {
        return can(r, prstatusid);
    }

    /**
     * Возврашает список допустимых статусов для задачи. С учетом прав доступа
     *
     * @param taskId ID задачи
     * @return список ID статусов
     * @throws GranException при необходимости
     */
    public TreeSet<String> getAllowedPrstatusesForTask(String taskId) throws GranException {
        return taskManager.getAllowedPrstatuses(loggedUserId, taskId, getPrstatusId());
    }

    /**
     * Возврашает список допустимых статусов для пользователя. С учетом прав доступа
     *
     * @param toUserId ID пользователя
     * @return список ID статусов
     * @throws GranException при необходимости
     */
    public TreeSet<String> getAllowedPrstatusesForUser(String toUserId) throws GranException {
        return UserRelatedManager.getInstance().getAllowedPrstatuses(toUserId, loggedUserId);
    }

    /**
     * Проверяет возможность действия для списка статусов
     *
     * @param action действие
     * @param list   список статусов
     * @param taskid ID задачи
     * @return TRUE - возможно, FALSE - нет
     * @throws GranException при необходимости
     */
    public boolean canAction(TaskAction action, String taskid, Set<String> list) throws GranException {
        try {
            for (TaskAction taskUnOverridedAction : Action.taskUnOverridedActions)
                if (action.equals(taskUnOverridedAction) && !list.contains(prstatusId)) {
                    list.add(prstatusId);
                }
            for (String prst : list) {
                if (can(action, prst))
                    return true;
            }
            return false;
        } catch (Exception e) {
            throw new GranException(e);
        }
    }

    /**
     * Проверяет возможность действия над задачей
     *
     * @param action действие
     * @param taskid ID задачи
     * @return TRUE - возможно, FALSE - нет
     * @throws GranException при необходимости
     */
    public boolean canAction(TaskAction action, String taskid) throws GranException {
        return canAction(action, taskid, getAllowedPrstatusesForTask(taskid));
    }

    /**
     * Проверяет возможность действия над пользователем для списка статусов
     *
     * @param action действие
     * @param list   список статусов
     * @return TRUE - возможно, FALSE - нет
     * @throws GranException при необходимости
     */
    public boolean canAction(UserAction action, Set<String> list) throws GranException {
        try {
            for (UserAction userUnOverridedAction : Action.userUnOverridedActions)
                if (action.equals(userUnOverridedAction) && !list.contains(prstatusId)) {
                    list.add(prstatusId);
                }
            for (String prst : list) {
                if (can(action, prst))
                    return true;
            }
            return false;
        } catch (Exception e) {
            throw new GranException(e);
        }
    }

    /**
     * Проверяет возможность действия над пользователем для списка статусов
     *
     * @param action действие
     * @param userId ID пользователя
     * @return TRUE - возможно, FALSE - нет
     * @throws GranException при необходимости
     */
    public boolean canAction(UserAction action, String userId) throws GranException {
        return canAction(action, getAllowedPrstatusesForUser(userId));
    }

    //lock: don't need
    /**
     * Проверяет есть ли доступ залогиненного пользователя к указанной задаче посредством прав доступа
     *
     * @param taskid ID задачи
     * @return TRUE - если доступ есть, FALSE - если нет
     * @throws GranException при необходимости
     */
    public boolean allowedByACL(String taskid) throws GranException {
        return taskManager.hasAccess(loggedUserId, taskid, getPrstatusId());
    }

    /**
     * Проверяет есть ли доступ залогиненного пользователя к указанномупользователю посредством прав доступа
     *
     * @param userid ID пользователя
     * @return TRUE - если доступ есть, FALSE - если нет
     * @throws GranException при необходимости
     */
    public boolean allowedByUser(String userid) throws GranException {
        if (KernelManager.getUser().getUserIdChain(loggedUserId, userid) != null)
            return true;
        return UserRelatedManager.getInstance().hasAccess(userid, loggedUserId, getPrstatusId());
    }

    /**
     * Воззвращает локаль пользователя
     *
     * @return локаль пользователя
     * @throws GranException при необходимости
     */
    public String getLocale() throws GranException {
        String tmp = getLoggedUser().getLocale();
        if (tmp == null) tmp = Config.getInstance().getDefaultLocale();
        return tmp;
    }

    /**
     * Возвращает таймзону пользователя
     *
     * @return таймзона
     * @throws GranException при необходимости
     */
    public String getTimezone() throws GranException {
        String tmp = getLoggedUser().getTimezone();
        if (tmp == null) tmp = Config.getInstance().getDefaultTimezone();
        return tmp;
    }

    /**
     * Проверяет может ли залогиненный пользователь просматривать указанную задачу
     *
     * @param taskid ID задачи
     * @return TRUE - может, FALSE - нет
     * @throws GranException при необходимости
     */
    public boolean taskOnSight(String taskid) throws GranException {
        //log.call("taskOnSight ("+taskid+")");
        return taskManager.onSight(loggedUserId, taskid, getPrstatusId(), true);
    }

    /**
     * Проверяет может ли залогиненный пользователь просматривать указанного пользователя
     *
     * @param userid ID пользователя
     * @return TRUE - может, FALSE - нет
     * @throws GranException при необходимости
     */
    public boolean userOnSight(String userid) throws GranException {
        if (KernelManager.getUser().getUserIdChain(loggedUserId, userid) != null)
            return true;
        if (KernelManager.getUser().getUserIdChain(userid, loggedUserId) != null)
            return true;
        return UserRelatedManager.getInstance().onSight(userid, loggedUserId, getPrstatusId());
    }

    /**
     * Устанавливает атрибут в сессию пользователя
     *
     * @param key   ключ
     * @param value значение
     */
    public void setAttribute(String key, Object value) {
        if (value!=null)
            session.put(key, value);
        else
            session.remove(key);
    }

    /**
     * Устанавливает атрибут запроса для пользователя
     *
     * @param request запрос
     * @param key     ключ
     * @param value   значение
     */
    public void setRequestAttribute(HttpServletRequest request, String key, Object value) {
        request.setAttribute(key, value);
    }

    /**
     * Возвращает атрибут из сессии пользователя
     *
     * @param key ключ
     * @return значение
     */
    public Object getAttribute(String key) {
        return session.get(key);
    }

    /**
     * Удаляет атрибут из сессии пользователя
     *
     * @param key ключ
     */
    public void removeAttribute(String key) {
        session.remove(key);
    }

    public void removeAttributes(List<String> keys) {
        for (String key : keys) {
            session.remove(key);
        }
    }

    /**
     * Обновляет статус залогиненного пользователя
     */
    void refresh() {
        this.prstatusId = getLoggedUser().getPrstatusId();
    }

    /**
     * Возвращает ID статуса пользователя
     *
     * @return ID статуса
     */
    public String getPrstatusId() {
        return prstatusId;
    }

    /**
     * Возвращает текущее пространство пользователя
     *
     * @return пространство
     */
    public String getCurrentSpace() {
        return currentSpace;
    }

    private String defaultTab;

    /**
     * Возвращает закладку по умолчанию
     *
     * @return закладка по умолчанию
     */
    public String getDefaultTab() {
        return defaultTab;
    }

    /**
     * Устанавливает закладку по умолчанию
     *
     * @param defaultTab закладка по умолчанию
     */
    public void setDefaultTab(String defaultTab) {
        this.defaultTab = defaultTab;
    }

    /**
     * Устанавливает текущее пространство пользователя
     *
     * @param currentSpace пространство пользователя
     * @param request      запрос
     */
    public void setCurrentSpace(String currentSpace, HttpServletRequest request) {
        this.currentSpace = currentSpace;
        this.defaultTab = "";
        if (request.getCookies() != null) {
            for (ListIterator i1 = Arrays.asList(request.getCookies()).listIterator(); i1.hasNext();) {
                Cookie c = (Cookie) i1.next();
                //System.out.println("cookie: "+c.getName()+"; "+c.getDomain()+ "; "+c.getParents()+"; "+c.getValue()+ "; "+c.getMaxAge());
                if (c.getName().equals(currentSpace)) {
                    this.defaultTab = c.getValue();
                }
            }
        }
    }

    private ProgressManager progressManager;

    /**
     * Возвращает экземпляр класса ProgressManager
     *
     * @return экземпляр ProgressManager
     */
    public ProgressManager getProgressManager() {
        return progressManager;
    }

    /**
     * Устанавливает прогресс бар
     *
     * @param progressManager прогресс бар
     */
    public void setProgressManager(ProgressManager progressManager) {
        this.progressManager = progressManager;
    }

    /**
     * Возвращает значение прогресса
     *
     * @return значение прогресса
     */
    public int getProgress() {
        return progressManager == null ? -1 : getProgressManager().getProgress();
    }

    /**
     * Устанавливает хначение прогресса
     *
     * @param progress значение прогресса
     */
    public void setProgress(int progress) {
        getProgressManager().setProgress(progress);
    }

    /**
     * Устанавливает сессию в куки
     *
     * @param req  запрос
     * @param resp ответ
     */
    public void setCookies(HttpServletRequest req, HttpServletResponse resp, boolean rememberMe) {
//        log.debug("SAVE COOKIES!");
        Cookie c = new Cookie(TS_SESSION + req.getServerPort(), getId());
//        log.debug("Cook debug : " + c.getName() + c.getValue());
        if (rememberMe) {
            c.setMaxAge(60 * 60 * 24 * 365);
        }
        //     c.setDomain(req.getServerName());
        //�����, ���� ������� ������ path �� � IE7 ����� ���� �� ����������, ������ ��������� ������ ���������������
//        c.setPath("");
        resp.addCookie(c);
        resp.addCookie(TSDispatchAction.removeCookie("_selectedId"));

    }

    /**
     * На случай нескольких cookie с одним именем (а такое бывает) возвращаем все
     *
     * @param req  запрос
     * @param resp ответ
     * @return Список ID сессий из куки
     * @throws GranException при необходимости
     */
    public static ArrayList<String> getCookies(HttpServletRequest req, HttpServletResponse resp) throws GranException {
        Cookie[] cook = req.getCookies();
        ArrayList<String> sessionIds = new ArrayList<String>();
        if (cook != null) {
            for (Cookie c : cook) {
                if (c.getName().equals(TS_SESSION + req.getServerPort()) && c.getValue() != null && c.getValue().length() > 0) {
                    if ((c.getDomain() == null || c.getDomain().equals(req.getServerName()))) {
                        sessionIds.add(c.getValue());
                    }
                }
            }
        }
        return sessionIds;
    }

    /**
     * Сбрасывает куки
     *
     * @param req  запрос
     * @param resp ответ
     */
    public static void resetCookies(HttpServletRequest req, HttpServletResponse resp) {
        Cookie[] cook = req.getCookies();
        //ArrayList<String> sessionIds = new ArrayList<String>();
        if (cook != null) {
            for (Cookie c : cook) {
//                log.debug("Cook debug : " + c.getName() + c.getValue());
                if (c.getName().equals(TS_SESSION + req.getServerPort()) && c.getValue() != null && c.getValue().length() > 0) {
                    if ((c.getDomain() == null || c.getDomain().equals(req.getServerName()))) {
                        c.setMaxAge(0);
                        c.setPath("/");
                        resp.addCookie(c);
                    }
                }
                if (c.getName().equals("SubtasksAction")) {
                    c.setValue("none");
                }
            }
        }
    }


    /**
     * Возвращает дату последнего логина пользователя
     *
     * @return дата
     * @throws GranException при необходимости
     */
    public Calendar getPrevLogonDate() throws GranException {
        if (prevLogonDate == null)
            return getUser().getLastLogonDate();
        return prevLogonDate;
    }

    /**
     * Возвращает дату последнего логина пользователя
     *
     * @return дата
     * @throws GranException при необходимости
     */
    public Calendar getLastLogonDate() throws GranException {
        return getUser().getLastLogonDate();
    }

    /**
     * Устанавливает дату последнего логина пользователя
     *
     * @throws GranException при необходимости
     */
    public void savePrevLogonDate() throws GranException {
        this.prevLogonDate = getUser().getLastLogonDate();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> filter(String search) {
        List<String> keys = new ArrayList<String>();
        for (Object key : session.keySet()) {
            if (String.valueOf(key).contains(search)) {
                keys.add(String.valueOf(key));
            }
        }
        return keys;
    }
}