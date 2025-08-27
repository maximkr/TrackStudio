/*
 * @(#)PrstatusManager.java
 * Copyright 2009 TrackStudio, Inc. All rights reserved.
 */
package com.trackstudio.kernel.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.constants.CategoryConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.ActionCacheManager;
import com.trackstudio.kernel.cache.CprstatusCacheManager;
import com.trackstudio.kernel.cache.MprstatusCacheManager;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UprstatusCacheManager;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.model.Cprstatus;
import com.trackstudio.model.Mprstatus;
import com.trackstudio.model.Prstatus;
import com.trackstudio.model.Rolestatus;
import com.trackstudio.model.Uprstatus;
import com.trackstudio.model.User;
import com.trackstudio.startup.I18n;

import net.jcip.annotations.Immutable;

/**
 * Класс PrstatusManager содержит методы для работы со статусами пользователей
 */
@Immutable
public class PrstatusManager extends KernelManager {

    private static final String className = "PrstatusManager.";
    private static final PrstatusManager instance = new PrstatusManager();
    private static final Log log = LogFactory.getLog(PrstatusManager.class);
    private static final LockManager lockManager = LockManager.getInstance();
    /**
     * Конструктор по умолчанию
     */
    private PrstatusManager() {
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return Экземпляр PrstatusManager
     */
    protected static PrstatusManager getInstance() {
        return instance;
    }

    /**
     * Редактирует статус
     *
     * @param prstatusId  ID редактируемого статуса
     * @param name        Название статуса
     * @param preferences Настройки статуса
     * @throws GranException при необходимости
     */
    public void updatePrstatus(String prstatusId, SafeString name, String preferences) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Prstatus obj = KernelManager.getFind().findPrstatus(prstatusId);
            obj.setName(name != null ? name.toString() : null);
            obj.setPreferences(preferences);
            hu.updateObject(obj);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удаляет статус по его ID
     *
     * @param prstatusId ID удаляемого статуса
     * @throws GranException при необходимости
     */
    public void deletePrstatus(String prstatusId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        lockManager.getLock(prstatusId).lock();
        try {
            MprstatusCacheManager.getInstance().invalidateRemovePrstatus(prstatusId);
            CprstatusCacheManager.getInstance().invalidateRemovePrstatus(prstatusId);
            UprstatusCacheManager.getInstance().invalidateRemovePrstatus(prstatusId);
            /*Mprstatus � Cprstatus ���� ����� ������������� �� ����, ��� ����� ������ prstatus.
            �.�. ����� ������ prstatus ������������ �������� ��� mprstatus-� � �� �� ������ ������,
            ����� mprstatus-� ����� ������� �� ����
            */
            KernelManager.getAcl().deleteAclByPrstatus(prstatusId);
            hu.deleteObject(Prstatus.class, prstatusId);

            ActionCacheManager.getInstance().invalidateForPrstatus(prstatusId);
        } finally {
            if (w) lockManager.releaseConnection(className);
            lockManager.getLock(prstatusId).unlock();
        }
    }

    /**
     * Создается статус
     *
     * @param name   Название статуса
     * @param userId ID пользователя, который создает статус
     * @return ID созданного статуса
     * @throws GranException при необходимости
     */
    public String createPrstatus(SafeString name, String userId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            String prstatusId = hu.createObject(new Prstatus(name != null ? name.toString() : null, userId));
            List<String> roleses = hu.getList("select rolestatus.role from com.trackstudio.model.Rolestatus as rolestatus where rolestatus.prstatus=?",
                    UserRelatedManager.getInstance().find(userId).getPrstatusId());
            resetRoles(prstatusId);
            createAllowedRoles(prstatusId, roleses);
            ActionCacheManager.getInstance().invalidateForPrstatus(prstatusId);
            String categoryId = TaskRelatedManager.getInstance().find("1").getCategoryId();
            HashMap<String, List<String>> map = new HashMap<String, List<String>>();
            List<String> list = Arrays.asList(CategoryConstants.NONE, CategoryConstants.VIEW_ALL, CategoryConstants.NONE, CategoryConstants.NONE, CategoryConstants.NONE);
            map.put(categoryId, list);
            KernelManager.getCategory().setCategoryRuleMap(map, prstatusId);
            return prstatusId;
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Создается копия статуса (клонируется)
     *
     * @param prstatusId ID копируемого статуса
     * @param userId     ID пользователя
     * @param locale     Локаль
     * @return ID созданного статуса
     * @throws GranException при необходимости
     */
    public String clonePrstatus(String prstatusId, String userId, String locale) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        lockManager.getLock(prstatusId).lock();
        try {
            Prstatus prstatus = (Prstatus) hu.getObject(Prstatus.class, prstatusId);
            String name = prstatus.getName();
            Prstatus prstatusCopy = new Prstatus(name.endsWith(' ' + I18n.getString(locale, "CLONED")) ? name : name + ' ' + I18n.getString(locale, "CLONED"), userId);
            String prstatid = hu.createObject(prstatusCopy);
            List<String> roleses = hu.getList("select rolestatus.role from com.trackstudio.model.Rolestatus as rolestatus where rolestatus.prstatus=?", prstatusId);
            resetRoles(prstatid);
            createAllowedRoles(prstatid, roleses);
            //setRoles(prstatid, roleses, new ArrayList<String>());
            // clone workflow permissions
            List<Mprstatus> mpss = hu.getList("from com.trackstudio.model.Mprstatus as mprstatus where  mprstatus.prstatus=?", prstatusId);
            for (Mprstatus mps : mpss) {
                WorkflowManager.getInstance().grant(mps.getType(), prstatid, mps.getMstatus().getId());
            }
            // clone UDF permissions
            List<Uprstatus> uprlist = hu.getList("from com.trackstudio.model.Uprstatus as uprstatus where uprstatus.prstatus=?", prstatusId);
            for (Uprstatus u : uprlist) {
                String type = u.getType();
                String udfId = u.getUdf().getId();
                KernelManager.getUdf().setUDFRule(udfId, prstatid, type);
            }
            // clone category permissions
            List<Cprstatus> cpslist = hu.getList("from com.trackstudio.model.Cprstatus as cprstatus where cprstatus.prstatus=?", prstatusId);
            for (Cprstatus cps : cpslist) {
                CategoryManager.getInstance().addPrstatusCategory(cps.getType(), cps.getCategory().getId(), prstatid);
            }

            ActionCacheManager.getInstance().invalidateForPrstatus(prstatid);
            return prstatid;
        } finally {
            if (w) lockManager.releaseConnection(className);
            lockManager.getLock(prstatusId).unlock();
        }
    }

    /**
     * Устанавливает роли для пользователя
     *
     * @param prstatusId ID статуса пользователя
     * @param allowed    Устанавливаемые роли
     * @param denied     Удаляемые роли
     * @throws GranException при неободимости
     */
    public void setRoles(String prstatusId, List<String> allowed, List<String> denied) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        lockManager.getLock(prstatusId).lock();
        try {
            //resetRoles(prstatusId);
            List<String> common = new ArrayList<String>();
            common.addAll(denied);
            common.addAll(allowed);
            for (String a : common) {
                hu.executeDML("delete from com.trackstudio.model.Rolestatus r where r.prstatus=? and r.role=?", prstatusId, a);
            }
            hu.cleanSession();
            createAllowedRoles(prstatusId, allowed);
        } finally {
            if (w) lockManager.releaseConnection(className);
            lockManager.getLock(prstatusId).unlock();
        }
    }

    /**
     * Устанавливает роли для пользователя
     *
     * @param prstatusId ID статуса
     * @param allowed    Устанавливаемые роли
     * @throws GranException при необходимости
     */
    private void createAllowedRoles(String prstatusId, List<String> allowed) throws GranException {
        List<Object> objs = new ArrayList<Object>();
        for (String role : allowed) {
            objs.add(new Rolestatus(prstatusId, role));
        }
        if (!objs.isEmpty()) {
            hu.createObjects(objs);
        }
        hu.cleanSession();
        ActionCacheManager.getInstance().invalidateForPrstatus(prstatusId);
    }

    /**
     * Удаляет роли для статуса
     *
     * @param prstatusId ID статуса
     * @throws GranException при необходимости
     */
    private void resetRoles(String prstatusId) throws GranException {
        hu.executeDML("delete from com.trackstudio.model.Rolestatus r where r.prstatus=?", prstatusId, null);
        hu.cleanSession();
    }

    /**
     * Для пользователя, который создает ACL и его парентов достается
     * список статусов, созданных ими, затем достаются все подчиненные статусы
     * от собственного статуса пользователя плюс сам этот статус. Возвращается
     * сумма этих двух множеств
     *
     * @param userId ID пользователя
     * @return Список статусов
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Prstatus
     */
    public Set<Prstatus> getAvailablePrstatusList(String userId) throws GranException {
        log.trace("getAvailablePrstatusList(" + userId + ')');
        boolean r = lockManager.acquireConnection(className);
        try {
            Set<Prstatus> result = new HashSet<Prstatus>();
            List<Prstatus> list = hu.getList("from com.trackstudio.model.Prstatus p order by p.name");
            for (Prstatus p : list) {
                String uId = p.getUser().getId();
                if (UserRelatedManager.getInstance().hasPath(userId, uId)) {
                    boolean active = KernelManager.getUser().getActive(uId);
                    long expire = KernelManager.getUser().getUserExpireDate(uId);
                    if (active && (expire == 0 || expire > System.currentTimeMillis()))
                        result.add(p);
                }
            }
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }



    /**
     * Возвращает список статусов, которые может создавать указанный пользователя
     *
     * @param userId ID пользователя
     * @return список статусов
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Prstatus
     */
    public Set<Prstatus> getCreatablePrstatusList(String userId) throws GranException {
        log.trace("getAvailablePrstatusList(" + userId + ')');
        boolean r = lockManager.acquireConnection(className);
        try {
            Set<Prstatus> result = new HashSet<Prstatus>();
            List<String> list = KernelManager.getUser().getUserIdChain(null, userId);
            for (String id : list) {
                List<Prstatus> prlist = hu.getList("select p from com.trackstudio.model.Prstatus p, com.trackstudio.model.User u where p.user=? and u.id = p.user order by p.name", id);
                for (Prstatus array : prlist) {
                    result.add(array);
                }
            }
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }

    }

    /**
     * Выводит список prstatus для пользователя, которые тот может смотреть.
     * Для этого берем список prstatus, созданных пользователем, и к ним добавляем getAvailablePrstatusList
     *
     * @param userId ID пользоватлея
     * @return список статусов
     * @throws GranException при необходимости
     * @see #getAvailablePrstatusList(String)
     * @see com.trackstudio.model.Prstatus
     * @deprecated В сущности, выполняет то же самое, что и getAvailablePrstatusList
     */
    public ArrayList<Prstatus> getViewablePrstatusList(String userId) throws GranException {
        log.trace("getViewablePrstatusList(" + userId + ')');
        boolean r = lockManager.acquireConnection(className);
        try {
            Set<Prstatus> result = new HashSet(hu.getList("select p from com.trackstudio.model.Prstatus p, com.trackstudio.model.User u " +
                    "where p.user=? and u.id = p.user order by p.name", userId));
            result.addAll(this.getAvailablePrstatusList(userId));
            return new ArrayList<Prstatus>(result);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Проверяем, может ли один пользователь быть менеджером для другого.
     * <br>
     * Для userId и managerId достаем соответствующие prstatus и сравниваем. Если они не одинаковы, то берем parent-статус
     * от пользовательского и снова сравниваем со статусом менеджера. Так продолжаем, пока не дойдем до верха, либо не
     * найдем совпадающие статусы. Если статусы находятся в одной ветви, и статус managerId выше, чем статус userId,
     * проверяем, находятся ли managerId и владелец статуса userId в одной ветке, причем managerId должен являться parent-ом
     * по отношению к владельцу статуса. Т.е. иметь права редактирования на этот статус, фактически.
     * Если такие условия выполняются, то возвращаем true.
     *
     * @param userId    Пользователь, для которого проверяем.
     * @param managerId Потенциальный менеджер
     * @return true - если может
     * @throws GranException при необходимости
     */
    public boolean isManagerAvailable(String userId, String managerId) throws GranException {
        log.trace("isManagerAvailable(" + userId + ", " + managerId + ')');
        boolean r = lockManager.acquireConnection(className);
        try {
            User user = KernelManager.getFind().findUser(userId);
            User manager = KernelManager.getFind().findUser(managerId);
            Prstatus up = KernelManager.getFind().findPrstatus(user.getPrstatus().getId());
            String prstatOwner = up.getUser().getId();
            //Prstatus mp = KernelManager.getFind().findPrstatus(manager.getPrstatus().getId());
            //boolean check = up.equals(mp);
            //while (up.getParent() != null && !check) {
            //    up = KernelManager.getFind().findPrstatus(up.getParent().getId());
            //    check = up.equals(mp);
            //}
            List<UserRelatedInfo> chain = KernelManager.getUser().getUserChain(prstatOwner, manager.getId());
            return chain != null && !chain.isEmpty();
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Достаем все статуса из базы
     *
     * @return список статусов
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Prstatus
     */
    public List<Prstatus> getPrstatusList() throws GranException {
        log.trace("getPrstatusList()");
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("select p from com.trackstudio.model.Prstatus p");
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }
}