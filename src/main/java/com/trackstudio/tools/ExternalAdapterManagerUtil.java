package com.trackstudio.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.CategoryCacheItem;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.model.Prstatus;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredPriorityBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredResolutionBean;
import com.trackstudio.secured.SecuredStatusBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;

import net.jcip.annotations.Immutable;

/**
 * Класс адаптер для работы с внешними данными
 */
@Immutable
public class ExternalAdapterManagerUtil {

    private static final String className = "ExternalAdapterManagerUtil.";
    private static final HibernateUtil hu = new HibernateUtil();
    private static final LockManager lockManager = LockManager.getInstance();

    private static HashMap<String, String> getAssignableHandlerAclMap(SessionContext sc, String taskId) throws GranException {
        HashMap<String, String> hm = new HashMap<String, String>();
        ArrayList<SecuredUserBean> list = AdapterManager.getInstance().getSecuredAclAdapterManager().getHandlerForFilter(sc, taskId);
        for (SecuredUserBean sub : list) {
            if (!hm.containsKey(sub.getId()))
                hm.put(sub.getId(), sub.getName());
        }
        return hm;
    }

    /**
     * Заполняет доступные категории и процессы
     *
     * @param sc     сессия
     * @param taskId ID задачи
     * @param cm     карта категорий
     * @param wm     карта процессов
     * @throws GranException при необходимости
     */
    public static void fillAvailableCategoryAndWorkflowMaps(SessionContext sc, String taskId, HashMap<String, String> cm, HashMap<String, String> wm) throws GranException {
        ArrayList<CategoryCacheItem> categories = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getAllPossibleSubcategories(sc, TaskRelatedManager.getInstance().find(taskId).getCategoryId());
        for (CategoryCacheItem cci : categories) {
            if ((!cm.containsKey(cci.getId()) && cci.getName() != null) || (!wm.containsKey(cci.getWorkflowId()) && cci.getWorkflowName() != null)) {
                if (TaskRelatedManager.getInstance().hasPath(taskId, cci.getTaskId())) {
                    cm.put(cci.getId(), cci.getName());
                    wm.put(cci.getWorkflowId(), cci.getWorkflowName());
                }
            }
        }
    }

    /**
     * Возвращают карту доступных категорий
     *
     * @param sc     сессия
     * @param taskId ID задачи
     * @return карта
     * @throws GranException при необходимости
     */
    public static HashMap<String, String> getAvailableCategoryMap(SessionContext sc, String taskId) throws GranException {
        HashMap<String, String> hm = new HashMap<String, String>();
        ArrayList<CategoryCacheItem> categories = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getAllPossibleSubcategories(sc, TaskRelatedManager.getInstance().find(taskId).getCategoryId());
        for (CategoryCacheItem cci : categories) {
            if (!hm.containsKey(cci.getId()) && cci.getName() != null && TaskRelatedManager.getInstance().hasPath(taskId, cci.getTaskId())) {
                hm.put(cci.getId(), cci.getName());
            }
        }
        return hm;
    }

    /**
     * Возвращает карту доступных процессов
     *
     * @param sc     сессия
     * @param taskId ID задачи
     * @return карта
     * @throws GranException при необходимости
     */
    public static HashMap<String, String> getAvailableWorkflowMap(SessionContext sc, String taskId) throws GranException {
        HashMap<String, String> hm = new HashMap<String, String>();
        ArrayList<CategoryCacheItem> categories = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getAllPossibleSubcategories(sc, TaskRelatedManager.getInstance().find(taskId).getCategoryId());
        for (CategoryCacheItem cci : categories) {
            if (!hm.containsKey(cci.getWorkflowId()) && cci.getWorkflowName() != null && TaskRelatedManager.getInstance().hasPath(taskId, cci.getTaskId())) {
                hm.put(cci.getWorkflowId(), cci.getWorkflowName());
            }
        }
        return hm;
    }

    /**
     * Возвращает карту типов сообщений
     *
     * @param sc          сессия
     * @param workflowMap карта процессов
     * @return карта типов сообщений
     * @throws GranException при небходимости
     */
    public static HashMap<String, String> getAvailableMstatusMap(SessionContext sc, HashMap<String, String> workflowMap) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            HashMap<String, String> hm = new HashMap<String, String>();
            for (Map.Entry<String, String> workflowE : workflowMap.entrySet()) {
                for (SecuredMstatusBean smb : AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getMstatusList(sc, workflowE.getKey())) {
                    hm.put(smb.getId(), workflowMap.get(workflowE.getKey()) + " / " + smb.getName() + ' ');
                }
            }
            return hm;
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает карту доступных статусов
     *
     * @param sc          сессия
     * @param workflowMap карта процессов
     * @return карта статусов
     * @throws GranException при необходимости
     */
    public static HashMap<String, String> getAvailableStatusMap(SessionContext sc, HashMap<String, String> workflowMap) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            HashMap<String, String> hm = new HashMap<String, String>();
            for (Map.Entry<String, String> workflowE : workflowMap.entrySet()) {
                for (SecuredStatusBean spb : AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getStateList(sc, workflowE.getKey())) {
                    hm.put(spb.getId(), workflowMap.get(workflowE.getKey()) + " / " + spb.getName() + ' ');
                }
            }
            return hm;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает карту доступных приоритетов
     *
     * @param sc          сессия
     * @param workflowMap карта процессов
     * @return карта приоритетов
     * @throws GranException при необходимости
     */
    public static HashMap<String, String> getAvailablePriorityMap(SessionContext sc, HashMap<String, String> workflowMap) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            HashMap<String, String> hm = new HashMap<String, String>();
            for (Map.Entry<String, String> workflowE : workflowMap.entrySet()) {
                for (SecuredPriorityBean spb : AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getPriorityList(sc, workflowE.getKey())) {
                    hm.put(spb.getId(), workflowMap.get(workflowE.getKey()) + " / " + spb.getName() + ' ');
                }
            }
            return hm;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает карту доступных резолюций
     *
     * @param sc          сессия
     * @param workflowMap карта процессов
     * @return карта резолюций
     * @throws GranException при необходимости
     */
    public static HashMap<String, String> getAvailableResolutionMap(SessionContext sc, HashMap<String, String> workflowMap) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            HashMap<String, String> hm = new HashMap<String, String>();
            for (Map.Entry<String, String> workflowE : workflowMap.entrySet()) {
                for (SecuredMstatusBean smb : AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getMstatusList(sc, workflowE.getKey())) {
                    for (SecuredResolutionBean srb : AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getResolutionList(sc, smb.getId())) {
                        hm.put(srb.getId(), workflowMap.get(workflowE.getKey()) + " / " + smb.getName() + " / " + srb.getName() + ' ');
                    }
                }
            }
            return hm;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает карту прав доступа к типу сообщений
     *
     * @param mstatusId ID типа сообщенич
     * @return карта прав доступа
     * @throws GranException при необходимости
     */
    public static EggBasket<String, String> getMprstatusMap(String mstatusId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            EggBasket<String, String> eggBasket = new EggBasket<String, String>();
            List list = hu.getList("select mp.prstatus, mp.type from com.trackstudio.model.Mprstatus mp where mp.mstatus=?", mstatusId);
            for (Object o : list) {
                Object[] rs = (Object[]) o;
                Prstatus ps = (Prstatus) rs[0];
                String type = rs[1].toString();
                eggBasket.putItem(ps.getId(), type);
            }
            return eggBasket;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает карту пользователей
     *
     * @param sc     сессия
     * @param flthm  параметры фильтрации
     * @param taskId ID задачи
     * @return карта пользователей
     * @throws GranException при необходимости
     */
    public static HashMap<String, String> makeUserMap(SessionContext sc, FValue flthm, String taskId) throws GranException {
        HashMap<String, String> usersMap = getAssignableHandlerAclMap(sc, taskId);
        List<String> defs = flthm.get(FieldMap.HUSER_NAME.getFilterKey());
        List<String> strings = flthm.get(FieldMap.SUSER_NAME.getFilterKey());
        if (defs != null && strings != null) {
            defs.addAll(strings);
        } else if (strings != null)
            defs = flthm.get(FieldMap.SUSER_NAME.getFilterKey());
        if (defs != null) for (String s : defs) {
            if (!s.equals("0") && !s.equals("null") && !s.startsWith("GROUP_") && !usersMap.containsKey(s) &&
                    UserRelatedManager.getInstance().isUserExists(s)) {
                SecuredUserBean sub = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(sc, s);
                if (sub != null)
                    usersMap.put(sub.getId(), sub.getName());
            }
        }
        usersMap.put("CurrentUserID", "-------" + I18n.getString(sc.getLocale(), "I_AM") + "-------");
        usersMap.put("IandSubUsers", "-------" + I18n.getString(sc.getLocale(), "ME_AND_SUBORDINATED") + "-------");
        usersMap.put("IandManager", "-------" + I18n.getString(sc.getLocale(), "ME_AND_MANAGER") + "-------");
        usersMap.put("IandManagers", "-------" + I18n.getString(sc.getLocale(), "ME_AND_MANAGERS") + "-------");

        return usersMap;
    }

    /**
     * Возвращает карту статусов
     *
     * @param sc     сессия
     * @param taskId ID задачи
     * @return карта статусов
     * @throws GranException при необходимости
     */
    public static HashMap<String, String> makeStatusMap(SessionContext sc, String taskId) throws GranException {
        ArrayList<SecuredPrstatusBean> groupList = AdapterManager.getInstance().getSecuredAclAdapterManager().getHandlerStatusesForFilter(sc, taskId);
        HashMap<String, String> handlerUserMap = new HashMap<String, String>();
        for (SecuredPrstatusBean key : groupList) {
            handlerUserMap.put(key.getId(), key.getName());
        }
        return handlerUserMap;
    }

    /**
     * Возвращает карту статусов и пользователей
     *
     * @param sc     сессия
     * @param flthm  параметры фильтрации
     * @param taskId ID задачи
     * @return карта
     * @throws GranException при необходимости
     */
    public static HashMap<String, String> makeStatusAndUserMap(SessionContext sc, FValue flthm, String taskId) throws GranException {
        HashMap<String, String> handlerUserMap = makeUserMap(sc, flthm, taskId);
        ArrayList<SecuredPrstatusBean> groupList = AdapterManager.getInstance().getSecuredAclAdapterManager().getHandlerStatusesForFilter(sc, taskId);
        for (SecuredPrstatusBean key : groupList) {
            handlerUserMap.put("GROUP_" + key.getId(), key.getName());
        }
        handlerUserMap.put("GROUP_MyActiveGroup", "-------" + I18n.getString(sc.getLocale(), "MSG_MY_ACTIVE_GROUP") + "-------");
        return handlerUserMap;
    }
}