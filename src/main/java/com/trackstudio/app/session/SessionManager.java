package com.trackstudio.app.session;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.id.UUIDHexGenerator;

import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.tools.MD5;

import net.jcip.annotations.Immutable;

import static com.trackstudio.tools.Null.isNotNull;

/**
 * Класс для работы с сессиями пользователей
 */
@Immutable
public class SessionManager extends com.trackstudio.kernel.cache.CacheManager {

    private final ConcurrentMap<String, SessionContext> sessions = new ConcurrentHashMap<String, SessionContext>(10);
    private static final SessionManager instance = new SessionManager();
    private static final Log log = LogFactory.getLog(SessionManager.class);

    //lock: don't need
    private SessionManager() {
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return экземпляр SessionManager
     * @throws GranException при необходимости
     */
    public static SessionManager getInstance() {
        return instance;
    }


    /**
     * Возвращает сессию пользователя по ее ID
     *
     * @param sessionid ID сессии
     * @return сессия
     * @throws GranException при необходимости
     */
    public SessionContext getSessionContext(String sessionid) throws GranException {
        if (sessionid == null)
            return null;
        if (sessions.get(sessionid) != null) {
            SessionContext sc = sessions.get(sessionid);
            if (sc.getLoggedUser() == null || !sc.getUser().isActive())
                return null;
            return sc;
        } else {
            return null;//throw new GranException("Invalid sessionid:"+sessionid);
        }
    }

    /**
     * Удаляет сессию пользователя
     *
     * @param sessionid ID сессии
     * @throws GranException при необходимости
     */
    public void remove(String sessionid) throws GranException {
        if (sessionid != null) {
            sessions.remove(sessionid);
        }
    }

    /**
     * Создает сессию пользователя и возвращает ее ID
     *
     * @param uci пользователь
     * @return ID сессии
     * @throws GranException при необходимости
     */
    public String create(UserRelatedInfo uci, boolean update) throws GranException {
        for (String sessId : sessions.keySet()) {
            SessionContext sess = sessions.get(sessId);
            if (sess != null && sess.getUserId().equals(uci.getId())) {
                if (update) {
                    log.debug(KernelManager.getFind().findPrstatus(uci.getPrstatusId()).getName());
                    sess.setPrstatusId(uci.getPrstatusId());
                }
                return sessId;
            }
        }
        SessionContext sc = new SessionContext(uci);
        String id = MD5.encode((new UUIDHexGenerator()).generate(null, null).toString());
        sc.setId(id);
        log.trace("put session " + id);
        sessions.put(id, sc);
        return id;

    }

    public String create(UserRelatedInfo uci) throws GranException {
        return create(uci, false);
    }

    /**
     * Возвращает колличество сессий пользователя в системе
     *
     * @return колличество сессий
     */
    public int getSessionsSize() {
        return sessions.size();
    }

    /**
     * Возвращает список сессий пользователя
     *
     * @return список сессий
     */
    public List<SessionContext> getSessions() {
        List<SessionContext> list = new ArrayList<SessionContext>();
        for (String key : sessions.keySet()) {
            SessionContext sess = sessions.get(key);
            if (sess != null)
                list.add(sess);
        }
        return list;
    }

    /**
     * Проверяет существование сессии пользователя
     *
     * @param uri пользователь
     * @return TRUE - сессия создана, FALSE - нет
     */
    public boolean existUserSession(UserRelatedInfo uri) throws GranException {
        return isNotNull(getSessionIdByUserId(uri.getId()));
    }

    public String getSessionIdByUserId(String userId) {
        for (String key : sessions.keySet()) {
            SessionContext session = sessions.get(key);
            if (session != null && session.getUserId().equals(userId)) {
                return session.getId();
            }
        }
        return null;
    }

    public void removeByUserId(String userId) throws GranException {
        String sessionId = getSessionIdByUserId(userId);
        if (isNotNull(sessionId)) {
            remove(sessionId);
        }
    }
}