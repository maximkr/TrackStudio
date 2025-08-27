package com.trackstudio.kernel.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.UdfValue;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Udflist;
import com.trackstudio.model.User;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.HibernateUtil;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.tree.OrderedTree;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * Класс для работы с кешем пользователей и их данных
 */
@ThreadSafe
public class UserRelatedManager {
    private static final String GROUP_PREFIX = "GROUP_";
    private static final Log log = LogFactory.getLog(UserRelatedManager.class);
    private static final AtomicReference<UserRelatedManager> instance = new AtomicReference<UserRelatedManager>(null);
    private static final HibernateUtil hu = new HibernateUtil();
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    @GuardedBy("rwl")
    private final OrderedTree<UserRelatedInfo> cache;

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return экземпляр UserRelatedManager
     * @throws GranException при необходимости
     */
    public static UserRelatedManager getInstance() {
        UserRelatedManager ins = instance.get();
        if (ins != null)
            return ins;

        try {
            if (instance.compareAndSet(null, new UserRelatedManager())) {
                log.info("Initializing user cache");
                instance.get().fillUsers();
                log.info("Initializing user ACL");
                instance.get().fillAcl();
                // should be called single thread first time, otherwise second call will see initialized, but not filled instance
            }
            return instance.get();
        } catch (GranException e) {
            throw new IllegalStateException("Cannot init UserRelatedManager", e);
        }
    }

    /**
     * Конструктор
     *
     * @throws GranException при необходимости
     */
    protected UserRelatedManager() throws GranException {
        try {
            User t = (User) hu.getObject(User.class, "1");

            UserRelatedInfo root = new UserRelatedInfo(t.getId(),
                    t.getLogin(),
                    t.getPassword(),
                    t.getName(),
                    t.getTel(),
                    t.getEmail(),

                    t.getActive(),
                    t.getLocale(),
                    t.getTimezone(),
                    t.getCompany(),
                    t.getChildAllowed(),
                    t.getExpireDate(),
                    t.getPrstatus().getId(),
                    null,
                    t.getTemplate(),
                    t.getDefaultProject() != null ? t.getDefaultProject().getId() : null,
                    t.getLastLogonDate(),
                    t.getPasswordChangedDate(),
                    t.getPreferences());
            this.cache = new OrderedTree<UserRelatedInfo>(root);
        } catch (GranException e) {
            throw new IllegalStateException("User related initialization failed",e);
        }
        log.debug("Tree Cache has been initialized.");
    }


    /**
     * Возвращает деревянный кеш
     *
     * @return деревянный кеш
     */
    public List<UserRelatedInfo> getCacheContents() {
        rwl.readLock().lock();
        try {
            return cache.getContents();
        } finally {
            rwl.readLock().unlock();
        }

    }


    private void add(UserRelatedInfo to, UserRelatedInfo o) {
        rwl.writeLock().lock();
        try {
            cache.add(to, o);
            updateInsert(o);
        } finally {
            rwl.writeLock().unlock();
        }

    }

    private void updateInsert(UserRelatedInfo o) {

        rwl.writeLock().lock();
        try {
            List<UserRelatedInfo> l = cache.getAncestors(o);
            List<UserRelatedInfo> children = cache.getChildren(o);
            int childcount = 0;

            for (UserRelatedInfo related : children) {
                childcount += related.getChildCount() + 1;
            }

            o.setChildCount(childcount);
            for (UserRelatedInfo oper : l) {
                oper.setChildCount(oper.getChildCount() + o.getChildCount() + 1);
            }
        } finally {
            rwl.writeLock().unlock();
        }

    }

    /**
     * Удаляет пользователя по ID
     *
     * @param s ID пользователя
     * @throws GranException при необходимости
     */
    protected void remove(String s) throws GranException {
        rwl.writeLock().lock();
        try {
            UserRelatedInfo o = new UserRelatedInfo(s);
            List<UserRelatedInfo> l = cache.getAncestors(o);
            UserRelatedInfo old = cache.get(o);

            for (UserRelatedInfo oper : l) {
                oper.setChildCount(oper.getChildCount() - old.getChildCount() - 1);
            }
            cache.remove(o);
        } finally {
            rwl.writeLock().unlock();
        }
    }

    /**
     * Обновляет пользователя
     *
     * @param n пользователь
     * @throws GranException при необходимости
     */
    protected void update(UserRelatedInfo n) throws GranException {
        rwl.writeLock().lock();
        try {
            if (n == null) return;
            UserRelatedInfo old = cache.get(n);
            int oldchildcount = old.getChildCount();
            List<UserRelatedInfo> l = cache.getAncestors(n);
            List<UserRelatedInfo> children = cache.getChildren(n);
            int childcount = 0;
            for (UserRelatedInfo related : children) {
                childcount += related.getChildCount() + 1;
            }
            n.setChildCount(childcount);
            n.setAcl(old.getAcl());
            cache.replaceWith(old, n);
            for (UserRelatedInfo oper : l) {
                oper.setChildCount(oper.getChildCount() - oldchildcount + n.getChildCount());
            }
        } finally {
            rwl.writeLock().unlock();
        }

    }

    /**
     * Возвращает пользователя по ID
     *
     * @param id ID пользователя
     * @return пользователь
     * @throws GranException при необходимости
     */
    public UserRelatedInfo find(String id) throws GranException {
        rwl.readLock().lock();
        try {
            return cache.get(new UserRelatedInfo(id));
        } finally {
            rwl.readLock().unlock();
        }

    }

    private void putIn(TreeMap<String, UserRelatedInfo> map, String id) throws GranException {
        rwl.writeLock().lock();
        try {
            // если еще не добавлено
            if (find(id) == null) {
                UserRelatedInfo t = map.get(id);
                if (t != null) {
                    if (t.getParentId() != null) {
                        if (find(t.getParentId()) == null) {
                            putIn(map, t.getParentId());
                        }
                        add(new UserRelatedInfo(t.getParentId()), t);
                    }
                }
            }
        } finally {
            rwl.writeLock().unlock();
        }

    }

    /**
     * Заполняет данные о пользователях
     *
     * @throws GranException при необходимости
     */
    protected void fillUsers() throws GranException {
        rwl.writeLock().lock();
        try {
            TreeMap<String, UserRelatedInfo> map = new TreeMap<String, UserRelatedInfo>();
            for (Object o : hu.getList("select new com.trackstudio.kernel.cache.UserRelatedInfo(user.id, user.login, user.password, user.name, user.tel, user.email, user.active, user.locale, user.timezone, user.company, user.childAllowed, user.expireDate, user.prstatus.id, user.manager.id, user.template, user.defaultProject.id, user.lastLogonDate, user.passwordChangedDate, user.preferences) from com.trackstudio.model.User as user")) {
                UserRelatedInfo tri = (UserRelatedInfo) o;
                map.put(tri.getId(), tri);
            }

            log.debug("total users: " + map.size());
            for (String id : map.keySet()) {
                //((UserRelatedInfo) map.get(id)).getUDFValues();
                putIn(map, id);
            }
            initUDFValues(map.keySet());
            log.debug("cached users: " + cache.size());
        } finally {
            rwl.writeLock().unlock();
        }

    }

    /**
     * Проверяет активный пользователь или нет
     *
     * @param id ID пользователя
     * @return TRUE - активный, FALSE - нет
     * @throws GranException при необходимости
     */
    public boolean isActive(String id) throws GranException {
        rwl.readLock().lock();
        try {
            UserRelatedInfo uri = find(id);
            if (!uri.isEnabled() || uri.isExpired()) return false;
            for (UserRelatedInfo u : cache.getAncestors(uri)) {
                if (!u.isEnabled() || u.isExpired())
                    return false;
            }
            return true;
        } finally {
            rwl.readLock().unlock();
        }

    }

    /**
     * Проверяет дату истечения срока пользователя
     *
     * @param id ID пользователя
     * @return TRUE - истекла, FALSE - нет
     * @throws GranException при необходимости
     */
    public boolean isExpired(String id) throws GranException {
        rwl.readLock().lock();
        try {
            UserRelatedInfo uri = find(id);
            if (uri.isExpired()) return true;
            for (UserRelatedInfo u : cache.getAncestors(uri)) {
                if (u.isExpired())
                    return true;
            }
            return false;
        } finally {
            rwl.readLock().unlock();
        }

    }

    /**
     * Возвращает количество дочерних пользователей для указанного
     *
     * @param id ID пользователя
     * @return количество пользователей
     * @throws GranException при необходимости
     */
    public Integer getAllowedChildren(String id) throws GranException {
        rwl.readLock().lock();
        try {
            UserRelatedInfo uri = find(id);
            Integer minimum = uri.getChildAllowed();

            for (UserRelatedInfo u : cache.getAncestors(uri)) {
                if (minimum == null || minimum == 0 || u.getChildAllowed() != null && u.getChildAllowed() != 0 && u.getChildAllowed() < minimum)
                    minimum = u.getChildAllowed();
            }
            return minimum;
        } finally {
            rwl.readLock().unlock();
        }

    }

    /**
     * Возвращает количество дочерних пользователей для указанного
     *
     * @param id ID пользователя
     * @return количество пользователей
     */
    public int getChildrenCount(String id) {
        rwl.readLock().lock();
        try {
            return cache.getChildrenCount(new UserRelatedInfo(id));
        } finally {
            rwl.readLock().unlock();
        }

    }

    /**
     * Возвращает список потомков для пользователя
     *
     * @param id ID пользователя
     * @return список пользователей
     * @throws GranException при необходимости
     */
    public List<UserRelatedInfo> getActiveDescendents(String id) throws GranException {
        rwl.readLock().lock();
        try {
            List<UserRelatedInfo> c = cache.getChildren(new UserRelatedInfo(id));
            List<UserRelatedInfo> tmp = new ArrayList<UserRelatedInfo>();
            if (c != null && !c.isEmpty()) {
                for (UserRelatedInfo chil : c) {
                    if (isActive(chil.getId())) {
                        tmp.add(chil);
                        List<UserRelatedInfo> child = getActiveDescendents(chil.getId());
                        if (child != null) tmp.addAll(child);
                    }
                }
            }
            return tmp;
        } finally {
            rwl.readLock().unlock();
        }

    }

    private void fillAcl() throws GranException {
        rwl.writeLock().lock();
        try {
            for (Object o : hu.getList("select new com.trackstudio.kernel.cache.InternalACLIntermediate(acl.toUser.id, acl.id, acl.usersource.user.id, acl.usersource.prstatus.id, acl.prstatus.id, acl.owner.id, acl.override) from com.trackstudio.model.Acl as acl where acl.toUser is not null")) {
                InternalACLIntermediate iaclim = (InternalACLIntermediate) o;
                UserRelatedInfo tr = find(iaclim.getObjectId());
                if (tr != null) {
                    tr.addAcl(iaclim.getInternalACL());
                    cache.replaceWith(tr, tr);
                }
            }
        } finally {
            rwl.writeLock().unlock();
        }

    }

    /**
     * Очищает кеш прав доступа для пользователя
     *
     * @param id      ID пользователя
     * @param userId  ID пользователя
     * @param groupId ID статуса
     * @throws GranException при необходимости
     */
    public void invalidateAcl(String id, String userId, String groupId) throws GranException {
        rwl.writeLock().lock();
        try {
            UserRelatedInfo tr = find(id);
            ConcurrentMap acl = tr.getAcl();
            if (userId != null)
                acl.remove(userId);
            else
                acl.remove(GROUP_PREFIX + groupId);

            List l;
            if (userId != null)
                l = hu.getList("select new com.trackstudio.kernel.cache.InternalACL(acl.id, acl.usersource.user.id, acl.usersource.prstatus.id, acl.prstatus.id, acl.owner.id, acl.override) from com.trackstudio.model.Acl as acl  where acl.toUser=? and acl.usersource.user=?", id, userId);
            else
                l = hu.getList("select new com.trackstudio.kernel.cache.InternalACL(acl.id, acl.usersource.user.id, acl.usersource.prstatus.id, acl.prstatus.id, acl.owner.id, acl.override) from com.trackstudio.model.Acl as acl  where acl.toUser=? and acl.usersource.prstatus=?", id, groupId);
            for (Object o : l) {
                InternalACL iacl = (InternalACL) o;
                addAcl(acl, iacl);
            }
            tr.setAcl(acl);
            cache.replaceWith(tr, tr);
        } finally {
            rwl.writeLock().unlock();
        }

    }

    /**
     * Добавляет правило доступа
     *
     * @param acl  карта правил доступа
     * @param iacl правило доступа
     */
    public static void addAcl(Map<String, TreeSet<InternalACL>> acl, InternalACL iacl) {
            String id = iacl.getUserId() != null ? iacl.getUserId() : GROUP_PREFIX + iacl.getGroupId();
        TreeSet<InternalACL> o = acl.get(id);
        if (o == null) {
            o = new TreeSet<InternalACL>();
        }
        o.add(iacl);
        acl.put(id, o);
    }

    /**
     * Возвращает список потомков
     *
     * @param id ID пользователя
     * @return список ID пользователей
     * @throws GranException при необходимости
     */
    public ArrayList<String> getDescendents(String id) throws GranException {
        rwl.readLock().lock();
        try {
            ArrayList<String> subtasksId = new ArrayList<String>();
            for (Iterator<UserRelatedInfo> subtasksIterator = cache.getDescendents(new UserRelatedInfo(id)).iterator(); subtasksIterator.hasNext(); ) {
                UserRelatedInfo key = subtasksIterator.next();
                subtasksId.add(key.getId());
            }
            return subtasksId;
        } finally {
            rwl.readLock().unlock();
        }

    }

    /**
     * ВОзвращает список ID подчиненных пользователей
     *
     * @param id ID пользователя
     * @return список ID
     * @throws GranException при необходимости
     */
    public ArrayList<String> getChildren(String id) throws GranException {
        rwl.readLock().lock();
        try {
            List<UserRelatedInfo> children = cache.getChildren(new UserRelatedInfo(id));
            ArrayList<String> subtasksId = new ArrayList<String>();
            for (UserRelatedInfo c : children) {
                subtasksId.add(c.getId());
            }
            return subtasksId;
        } finally {
            rwl.readLock().unlock();
        }

    }

    /**
     * Возвращает список ID дочерних пользователей
     *
     * @param id ID пользователя
     * @return список ID пользователей
     * @throws GranException при необходимости
     */
    public ArrayList<String> getManagerChildren(String id) throws GranException {
        rwl.readLock().lock();
        try {
            ArrayList<UserRelatedInfo> children = (ArrayList<UserRelatedInfo>) cache.getChildren(new UserRelatedInfo(id));
            ArrayList<String> childsId = new ArrayList<String>();
            for (UserRelatedInfo uri : children) {
                if (uri.getChildCount() != 0) {
                    childsId.add(uri.getId());
                }
            }
            return childsId;
        } finally {
            rwl.readLock().unlock();
        }

    }

    /**
     * Проверяет существование пользователя
     *
     * @param userid ID пользователя
     * @return TRUE - пользователь существует, FALSE - нет
     * @throws GranException при необходимости
     */
    public boolean isUserExists(String userid) throws GranException {
            return getInstance().find(userid) != null;
    }

    /**
     * Возвращает список предков для пользователей
     *
     * @param users список пользователей
     * @return список пользователей
     * @throws GranException при необходимости
     */
    public List<String> getParents(Collection<String> users) throws GranException {
            List<String> result = new ArrayList<String>();
            for (String userId : users) {
                if (getChildrenCount(userId) > 0)
                    result.add(userId);
            }
            return result;
    }

    /**
     * Возвращает цепочку ID пользователей от одного пользователя до другого
     *
     * @param fromid начальный пользователь
     * @param toId   конечный пользователь
     * @return список пользователей
     * @throws GranException при необходимости
     */
    public ArrayList<String> getUserIdChain(String fromid, String toId) throws GranException {
        rwl.readLock().lock();
        try {
            ArrayList<String> ret = new ArrayList<String>();
            if (toId.equals(fromid)) {
                ret.add(fromid);
                return ret;
            }
            ArrayList<UserRelatedInfo> l = getUserChain(fromid, toId);
            if (l != null) {
                for (UserRelatedInfo o : l) {
                    // переворачиваем
                    if (o != null)
                        ret.add(o.getId());
                }
            } else {
                return null;
            }
            return ret;
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            rwl.readLock().unlock();
        }

    }

    /**
     * Возвращает цепочку пользователей от одного пользователя до другого
     *
     * @param fromid начальный пользователь
     * @param toId   конечный пользователь
     * @return список пользователей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UserRelatedInfo
     */
    public ArrayList<UserRelatedInfo> getUserChain(String fromid, String toId) throws GranException {
        rwl.readLock().lock();
        try {
            UserRelatedInfo tr = find(toId);
            // ordered
            ArrayList<UserRelatedInfo> l = cache.getAncestors(tr);
            if (tr != null) {
                l.add(tr);
            }
            if (fromid != null) {
                UserRelatedInfo tr2 = getInstance().find(fromid);
                if (!l.contains(tr2)) return null;
                ArrayList<UserRelatedInfo> l2 = cache.getAncestors(tr2);
                l.removeAll(l2);
            }
            return l;
        } finally {
            rwl.readLock().unlock();
        }

    }

    /**
     * Более быстрый метод узнать, имеет ли доступ юзер к юзеру
     *
     * @param toUserId   к кому доступ
     * @param userid     чей доступ проверяем
     * @param prstatusId ID статуса
     * @return TRUE - есть доступ, FALSE - ytn
     * @throws GranException при необходимости
     */
    public boolean hasAccess(String toUserId, String userid, String prstatusId) throws GranException {
        rwl.readLock().lock();
        try {
            ArrayList<UserRelatedInfo> l = getUserChain(null, toUserId);
            for (UserRelatedInfo tr : l) {
                Map<String, TreeSet<InternalACL>> acl = tr.getReadOnlyAcl();
                if (acl.containsKey(userid))
                    return true;
                if (acl.containsKey(GROUP_PREFIX + prstatusId)) {
                    for (InternalACL iacl : acl.get(GROUP_PREFIX + prstatusId)) {
                        if (getUserIdChain(iacl.getOwnerId(), userid) != null)
                            return true;
                    }
                }
            }
            return false;
        } finally {
            rwl.readLock().unlock();
        }

    }

    /**
     * Возвращает список доступных статусов
     *
     * @param toUserId  к кому доступ
     * @param forUserId кого доступ
     * @return список статусов
     * @throws GranException при необходимости
     */
    public TreeSet<String> getAllowedPrstatuses(String toUserId, String forUserId) throws GranException {
        rwl.readLock().lock();
        try {
            TreeSet<String> ret = new TreeSet<String>();
            // add default user prstatus to collection
            String prId = find(forUserId).getPrstatusId();
            ret.add(prId);
            for (UserRelatedInfo tr : getUserChain(null, toUserId)) {
                Map<String, TreeSet<InternalACL>> acl = tr.getReadOnlyAcl();
                TreeSet<InternalACL> acls = new TreeSet<InternalACL>();
                TreeSet<InternalACL> iacl = acl.get(forUserId);
                TreeSet<InternalACL> iaclGroup = acl.get(GROUP_PREFIX + prId);
                if (iacl != null) {
                    acls.addAll(iacl);
                }
                if (iaclGroup != null) {
                    acls.addAll(iaclGroup);
                }
                for (InternalACL a : acls) {
                    if (a.getOverride())
                        ret.clear();
                    if (a.getPrstatusId() != null)
                        ret.add(a.getPrstatusId());
                }
            }
            return ret;
        } finally {
            rwl.readLock().unlock();
        }

    }

    /**
     * Возвращает ближайшее правило доступа
     *
     * @param toUserId   к кому доступ
     * @param userid     кого доступ
     * @param prstatusId ID статуса
     * @return ID правила доступа
     * @throws GranException при необходимости
     */
    public String getNearestUserACL(String toUserId, String userid, String prstatusId) throws GranException {
        rwl.readLock().lock();
        try {
            String ret = userid;
            // add default user prstatus to collection
            List<UserRelatedInfo> l = getUserChain(null, toUserId);
            for (UserRelatedInfo tr : l) {
                Map<String, TreeSet<InternalACL>> acl = tr.getReadOnlyAcl();
                //TreeSet acls = new TreeSet();
                TreeSet<InternalACL> o = acl.get(userid);
                if (o != null) {
                    ret = tr.getId();
                } else {
                    if (acl.containsKey(GROUP_PREFIX + prstatusId)) {
                        for (InternalACL iacl : acl.get(GROUP_PREFIX + prstatusId)) {
                            if (getUserIdChain(iacl.getOwnerId(), userid) != null) {
                                ret = tr.getId();
                            }
                        }
                    }
                }
            }
            return ret;
        } finally {
            rwl.readLock().unlock();
        }

    }

    /**
     * Проверяет может ли один пользователь просматривать другого
     *
     * @param toUserId   кого смотреть
     * @param userid     кто смотрит
     * @param prstatusId id cnfnecf
     * @return TRUE - может смотреть, FALSE - не может
     * @throws GranException при необходимости
     */
    public boolean onSight(String toUserId, String userid, String prstatusId) throws GranException {
        rwl.readLock().lock();
        try {
            if (toUserId.equals("1")) return true;
            if (hasAccess(toUserId, userid, prstatusId)) return true;
            for (Iterator it = cache.getDescendents(new UserRelatedInfo(toUserId)).iterator(); it.hasNext(); ) {
                UserRelatedInfo tr = (UserRelatedInfo) it.next();
                Map<String, TreeSet<InternalACL>> acls = tr.getReadOnlyAcl();
                if (acls.containsKey(userid))
                    return true;
                if (acls.containsKey(GROUP_PREFIX + prstatusId)) {
                    for (InternalACL iacl : acls.get(GROUP_PREFIX + prstatusId)) {
                        if (getUserIdChain(iacl.getOwnerId(), userid) != null)
                            return true;
                    }
                }
            }
            return false;
        } finally {
            rwl.readLock().unlock();
        }

    }

    /**
     * Возвращает список ID доступных пользователей
     *
     * @param userId ID пользователя
     * @return список пользователей
     * @throws GranException при необходимости
     */
    public TreeSet<String> getAllowedUsers(String userId) throws GranException {
        rwl.readLock().lock();
        try {
            TreeSet<String> ret = new TreeSet<String>();
            List<String> addedPrstatus = new ArrayList<String>();
            List<UserRelatedInfo> l = getUserChain(null, userId);
            for (UserRelatedInfo tr : l) {
                Map<String, TreeSet<InternalACL>> acl = tr.getReadOnlyAcl();
                for (String id : acl.keySet()) {
                    if (id.startsWith(GROUP_PREFIX)) {
                        if (!addedPrstatus.contains(id)) {
                            List<String> list = hu.getList("select u.id from com.trackstudio.model.User u where u.prstatus=? and u.active=1", id.substring(GROUP_PREFIX.length()));
                            Set<String> subordUsers = new HashSet<String>();
                            for (InternalACL iacl : acl.get(id)) {
                                subordUsers.addAll(getInstance().getDescendents(iacl.getOwnerId()));
                            }
                            list.retainAll(subordUsers);
                            ret.addAll(list);
                            addedPrstatus.add(id);
                        }
                    } else {
                        ret.add(id);
                    }
                }
            }
            return ret;
        } finally {
            rwl.readLock().unlock();
        }

    }

    /**
     * Возвращает список прав доступа для пользователя
     *
     * @param userId ID пользователя
     * @return список ID прав
     * @throws GranException при необходимости
     */
    public ArrayList<String> getAclList(String userId) throws GranException {
            ArrayList<String> list = new ArrayList<String>();
            for (TreeSet<InternalACL> re : find(userId).getReadOnlyAcl().values()) {
                for (InternalACL it2 : re) {
                    list.add(it2.getAclId());
                }
            }
            return list;
    }

    /**
     * Загружает пользователя по ID
     *
     * @param id ID пользователя
     * @return пользователь
     * @throws GranException при необходимости
     */
    private UserRelatedInfo loadItem(String id) throws GranException {
        rwl.readLock().lock();
        try {
            UserRelatedInfo vObj = null;
            int count = getChildrenCount(id);
            for (Object o : hu.getList("select new com.trackstudio.kernel.cache.UserRelatedInfo" +
                    "(user.id, user.login, user.password, user.name, user.tel, user.email, user.active, user.locale, user.timezone, user.company, user.childAllowed, user.expireDate, user.prstatus.id, user.manager.id, user.template, user.defaultProject.id, user.lastLogonDate, user.passwordChangedDate, user.preferences) from com.trackstudio.model.User as user where user.id=?", id)) {
                vObj = (UserRelatedInfo) o;
                UserRelatedInfo oldUser = find(vObj.getId());
                if (oldUser != null && oldUser.getEmergencyNotice() != null && oldUser.getEmergencyNotice().length() != 0) {
                    vObj.setEmergencyNoticeDate(oldUser.getEmergencyNoticeDate());
                    vObj.setEmergencyNotice(oldUser.getEmergencyNotice());
                }
                vObj.setChildCount(count);
            }
            return vObj;
        } finally {
            rwl.readLock().unlock();
        }

    }

    /**
     * Удаляет пользователя из кеша
     *
     * @param userId ID пользователя
     * @param reindex to make reindex
     * @throws GranException при необходимости
     */
    public void invalidateUser(String userId, boolean reindex) throws GranException {
        rwl.writeLock().lock();
        try {
            update(loadItem(userId));
        } finally {
            rwl.writeLock().unlock();
        }

        if (reindex) {
            KernelManager.getIndex().reIndexUser(userId);
        }

    }

    /**
     * Метод для обновления в памяти срочный сообщения для пользователя
     *
     * @param user пользователь
     * @throws GranException если надо
     */
    public void invalidateUser(UserRelatedInfo user) throws GranException {
        rwl.writeLock().lock();
        try {
            update(user);
        } finally {
            rwl.writeLock().unlock();
        }

    }

    /**
     * Проверяет наличие пути от одного пользователя до другого
     *
     * @param fromid начальный пользователь
     * @param id     конечный пользователь
     * @return TRUE - путь есть, FALSE - путя нет
     * @throws GranException при необходимости
     */
    public boolean hasPath(String fromid, String id) throws GranException {
        return getUserChain(fromid, id) != null || getUserChain(id, fromid) != null;
    }

    private void initUDFValues(Collection<String> idColl) throws GranException {
        log.trace("########");
        rwl.writeLock().lock();
        try {
            if (idColl.isEmpty())
                return;
            ArrayList<String> users = getUsersNotInitializedUDFValues(idColl);
            if (users.isEmpty())
                return;
            Map<String, String> listValueMap = getUdflistValuesMap();
            // Будем в hashmap values хранить список UdfvalCacheItem для каждой задачи
            HashMap<String, ArrayList<UdfvalCacheItem>> values = new HashMap<String, ArrayList<UdfvalCacheItem>>(users.size());
            for (String id : users) {
                values.put(id, new ArrayList<UdfvalCacheItem>(5));
            }
            // todo тоже переделать winzard
            List<UdfvalCacheItem> udfvalCacheItems = hu.getList("select new com.trackstudio.kernel.cache.UdfvalCacheItem (udfval.id, udfval.udfsource.user.id, udfval.udf.id, udfval.str, udfval.num, udfval.dat, udfval.udflist.id, udfval.longtext.id, udfval.task.id, udfval.user.id) from com.trackstudio.model.Udfval as udfval where udfval.udfsource.user.id is not null");
            for (UdfvalCacheItem udfvalCacheItem : udfvalCacheItems) {
                if (udfvalCacheItem.getUdflistId() != null)
                    udfvalCacheItem.setUdflistVal(listValueMap.get(udfvalCacheItem.getUdflistId()));
                ArrayList<UdfvalCacheItem> vals = values.get(udfvalCacheItem.getSourceId());
                vals.add(udfvalCacheItem);
            }
            initUDFs(users);
            for (String user : users) {
                UserRelatedInfo uri = find(user);
                ArrayList<UDFCacheItem> udfs = uri.getUDFCacheItems();
                ArrayList<UdfvalCacheItem> vals = values.get(uri.getId());

                ArrayList<UdfValue> retUdfvalList = new ArrayList<UdfValue>(); // тут храним UdfValue с установленными значениями (если есть)
                for (UDFCacheItem udfCacheItem : udfs) {
                    UdfValue udfValue = new UdfValue(udfCacheItem);
                    int type = udfCacheItem.getType();

                    // ищем в hashmap значение для текущей задачи и udf
                    for (UdfvalCacheItem uvci : vals) {
                        if (uvci.getUdfId().equals(udfCacheItem.getId())) { // нашли значение!
                            udfValue.setValue(uvci);
                            if (type == UdfValue.STRING || type == UdfValue.DATE || type == UdfValue.INTEGER || type == UdfValue.MEMO || type == UdfValue.URL
                                    || type == UdfValue.FLOAT || type == UdfValue.LIST)
                                break; // поля этих типов имеют максимум одно значение, дальше продолжать нет смысла.
                        }
                    }
                    retUdfvalList.add(udfValue);
                }
                uri.setUDFValues(retUdfvalList);  // указываем юзеру его список UdfValue
            }

        } finally {
            rwl.writeLock().unlock();
        }

    }

    private static Map<String, String> getUdflistValuesMap() throws GranException {
        List<Udflist> listValue = hu.getList("from com.trackstudio.model.Udflist");
        Map<String, String> listValueMap = new HashMap<String, String>();
        for (Udflist ul : listValue) {
            listValueMap.put(ul.getId(), ul.getVal());
        }
        return listValueMap;
    }

    private ArrayList<String> getUsersNotInitializedUDFValues(Collection<String> idColl) throws GranException {
        ArrayList<String> users = new ArrayList<String>();
        for (String objId : idColl) {
            UserRelatedInfo uci = find(objId);
            if (!uci.isUserUDFInitialized() || !uci.isUDFValuesInitialized())
                users.add(objId);
        }
        return users;
    }

    private void initUDFs(Collection idColl) throws GranException {
        rwl.writeLock().lock();
        try {
            if (idColl.isEmpty())
                return;

            List<UDFCacheItem> allUserUDFs = hu.getList("select new com.trackstudio.kernel.cache.UDFCacheItem (udf.id, udf.udfsource.id, udf.udfsource.workflow.id, udf.udfsource.task.id, udf.udfsource.user.id, udf.caption, udf.referencedbycaption, udf.type, udf.order, udf.def, udf.initialtask.id, udf.initialuser.id, udf.required, udf.htmlview, udf.script, udf.cachevalues, udf.lookupscript, udf.lookuponly, udf.udfsource.user.id) from com.trackstudio.model.Udf as udf where udf.udfsource.user.id !=null order by udf.udfsource.user.id");

            // UDFCacheItem отсортированы по userId
            EggBasket<String, UDFCacheItem> udfcache = new EggBasket<String, UDFCacheItem>();
            for (UDFCacheItem uval : allUserUDFs) {
                udfcache.putItem(uval.getSourceId(), uval);
            }
            for (String userId : udfcache.keySet()) {
                find(userId).setUDFs(udfcache.get(userId));
            }
            for (Object anIdColl : idColl) {
                String id = (String) anIdColl;
                UserRelatedInfo userRelatedInfo = find(id);
                if (!userRelatedInfo.isUserUDFInitialized())
                    userRelatedInfo.setUDFs(new ArrayList<UDFCacheItem>());
            }
        } finally {
            rwl.writeLock().unlock();
        }


    }

    /**
     * Загружает список пользователей по их ID
     *
     * @param idColl список ID пользователей
     * @return спиок пользователей
     * @throws GranException при необходимости
     */
    public ArrayList<UserRelatedInfo> getItemCollection(Collection<String> idColl) throws GranException {
            ArrayList<UserRelatedInfo> ret = new ArrayList<UserRelatedInfo>();
            for (String id : idColl) {
                UserRelatedInfo ri = find(id);
                if (ri != null) {
                    ret.add(ri);
                }
            }
            return ret;
    }

    /**
     * Очищает кеш пользователей при добавлении пользователя
     *
     * @param id ID добавленного пользователя
     * @throws GranException при необходимости
     */
    public void invalidateWhenAdd(String id) throws GranException {
        UserRelatedInfo t = null;
        rwl.writeLock().lock();
        try {
            //  на этапе добавления задачи ACL для нее еще не выставили
            t = loadItem(id);
            add(new UserRelatedInfo(t.getParentId()), t);
        } finally {
            rwl.writeLock().unlock();
        }

        KernelManager.getIndex().reIndexUser(id);
    }

    /**
     * Очищает кеш пользователей при удалении пользователя
     *
     * @param id ID удаленного пользователя
     * @throws GranException при необходимости
     */
    public void invalidateWhenRemove(String id) throws GranException {
            remove(id);
            KernelManager.getIndex().deleteUser(id);
    }

    /**
     * Очищает кеш пользователей при обновлении пользователя
     *
     * @param userId ID обновленного пользователя
     * @throws GranException при необходимости
     */
    public void invalidateWhenUpdate(String userId) throws GranException {
        log.trace("invalidateWhenUpdate");
            log.trace("invalidateUser");
            invalidateUser(userId, true);
    }

    /**
     * Очищает кеш пользователей при перемещении пользователя
     *
     * @param userid ID обновленного пользователя
     * @param from   ID пользователя, откуда перемещаем
     * @param to     ID пользователя, куда перемещаем
     * @throws GranException при необходимости
     */
    public void invalidateWhenMove(String userid, String from, String to) throws GranException {
        rwl.writeLock().lock();
        try {
            log.trace("invalidateUser");
            UserRelatedInfo u = loadItem(userid);
            cache.moveTree(u, new UserRelatedInfo(to));
            updateInsert(u);
            invalidateWhenUpdate(from);
            while (u != null) {
                invalidateWhenUpdate(u.getId());
                if (u.getParentId() != null) {
                    u = loadItem(u.getParentId());
                } else {
                    u = null;
                }
            }
        } finally {
            rwl.writeLock().unlock();
        }

    }

    /**
     * Очищает кеш пользовательских полей для пользователя, при изменении списка
     *
     * @param user     пользователь
     * @param udfId    id поля
     * @param newValue новое значение
     * @param listId   ID списка
     * @throws GranException при необходимости
     */
    public void invalidateUDFWhenChangeList(String user, String udfId, String newValue, String listId) throws GranException {
        rwl.writeLock().lock();
        try {
            log.trace("invalidateUDFs");
            for (UserRelatedInfo uri : getCacheContents()) {
                uri.invalidateUDFWhenChangeList(udfId, newValue, listId);
            }
        } finally {
            rwl.writeLock().unlock();
        }

    }

    /**
     * Очищает кеш значений поля
     *
     * @param user пользователь
     * @throws GranException при необходимости
     */
    public void invalidateUDFsValues(String user) throws GranException {
        rwl.writeLock().lock();
        try {
            find(user).invalidateUDFsValues();
        } finally {
            rwl.writeLock().unlock();
        }

    }

    /**
     * Очищает кеш полей для пользователя
     *
     * @param udfId id поля
     * @throws GranException при необходимости
     */
    public void invalidateUDFs(String udfId) throws GranException {
        rwl.writeLock().lock();
        try {
            UDFCacheItem uci = KernelManager.getUdf().getUDFCacheItem(udfId);

            List<String> allUsers = new ArrayList<String>(1000);
            for (UserRelatedInfo uci1 : getCacheContents()) {
                allUsers.add(uci1.getId());
            }

            for (String s : allUsers) {
                UserRelatedInfo tci = find(s);
                tci.invalidateUDF(udfId, uci);
            }
        } finally {
            rwl.writeLock().unlock();
        }

    }



    /**
     * Возвращает список активных пользователей для статуса
     *
     * @param prstatusId ID статуса
     * @param active     активный пользователь или нет
     * @return список пользователей
     * @throws GranException при необходимости
     */
    public static List<String> getUsersForPrstatus(String prstatusId, boolean active) throws GranException {
        try {
            Integer activeValue = active ? 1 : 0;
            return hu.getList("select u.id from com.trackstudio.model.User u where u.prstatus=? and u.active=?", prstatusId, activeValue);
        } catch (Exception e) {
            throw new GranException(e);
        }
    }

    public static List<Pair> getUsersForPrstatus(String prstatusId) throws GranException {
        try {
            return hu.getList("select new com.trackstudio.tools.Pair(u.id, u.login, u.name) from com.trackstudio.model.User u where u.prstatus=?", prstatusId);
        } catch (Exception e) {
            throw new GranException(e);
        }
    }
}
