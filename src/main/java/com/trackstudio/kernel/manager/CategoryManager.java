/*
 * @(#)CategoryManager.java
 * Copyright 2009 TrackStudio, Inc. All rights reserved.
 */
package com.trackstudio.kernel.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.Preferences;
import com.trackstudio.constants.CategoryConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.CategoryCacheManager;
import com.trackstudio.kernel.cache.CprstatusCacheItem;
import com.trackstudio.kernel.cache.CprstatusCacheManager;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.model.Category;
import com.trackstudio.model.Catrelation;
import com.trackstudio.model.Cprstatus;
import com.trackstudio.model.Longtext;
import com.trackstudio.model.Prstatus;
import com.trackstudio.model.Trigger;
import com.trackstudio.model.Usersource;
import com.trackstudio.tools.Key;

import net.jcip.annotations.Immutable;

/**
 * Класс CategoryManager содержит методы для работы с категориями.
 */
@Immutable
public class CategoryManager extends KernelManager {

    private static final String className = "CategoryManager.";
    private static final Log log = LogFactory.getLog(CategoryManager.class);
    private static final CategoryManager instance = new CategoryManager();
    //private static final int CREATE = 1;
    private static final int EDIT = 2;
    private static final int DELETE = 3;
    private static final int VIEW = 4;
    private static final int HANDLER = 5;
    public static final int CREATE_INCORRECT = 1; //0001
    public static final int EDIT_INCORRECT = 2; //0010
    public static final int DELETE_INCORRECT = 4; //0100
    public static final int BEHANDLER_INCORRECT = 8; //1000
    private final CategoryCacheManager categoryCacheManager = CategoryCacheManager.getInstance();
    private static final LockManager lockManager = LockManager.getInstance();
    /**
     * Конструктор по умолчанию
     */
    private CategoryManager() {
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return Экземпляр CategoryManager
     */
    protected static CategoryManager getInstance() {
        return instance;
    }

    /**
     * Возвращает список ID дочерних категорий для указанной категории
     *
     * @param categoryId ID категории
     * @return Список ID дочерних категорий
     * @throws GranException при необходимости
     */
    public List<String> getChildrenCategoryIdList(String categoryId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("select c.id from com.trackstudio.model.Category c, " +
                    "com.trackstudio.model.Catrelation catrel where catrel.category = ? and c.id = catrel.child", categoryId);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список дочерних категорий для указанной категории и задачи
     *
     * @param categoryId  ID категории
     * @param currentTask ID задачи
     * @return Список дочерних категорий
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Category
     */
    public List<Category> getChildrenCategoryList(String categoryId, String currentTask) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            ArrayList<Category> l = new ArrayList<Category>();
            List<Category> list = hu.getList("select c from com.trackstudio.model.Category c, " +
                    "com.trackstudio.model.Catrelation catrel where catrel.category = ? and c.id = catrel.child", categoryId);
            ArrayList<TaskRelatedInfo> chain = TaskRelatedManager.getInstance().getTaskRelatedInfoChain(null, currentTask);
            for (Category cat : list) {
                if (cat.getTask() != null) {
                    if (chain.contains(new TaskRelatedInfo(cat.getTask().getId()))) {
                        l.add(cat);
                    }
                }
            }
            return l;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список родительских категорий для указанной категории и задачи
     *
     * @param categoryId  ID категории
     * @param currentTask ID задачи
     * @return Список дочерних категорий
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Category
     */
    public List<Category> getParentCategoryList(String categoryId, String currentTask) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            ArrayList<Category> l = new ArrayList<Category>();
            List<Category> list = hu.getList("select c from com.trackstudio.model.Category c, " +
                    "com.trackstudio.model.Catrelation catrel where catrel.category = c.id and  catrel.child = ?", categoryId);
            ArrayList<TaskRelatedInfo> chain = TaskRelatedManager.getInstance().getTaskRelatedInfoChain(null, currentTask);
            for (Category cat : list) {
                if (chain.contains(new TaskRelatedInfo(cat.getTask().getId())))
                    l.add(cat);
            }

            return l;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список прав, которые есть у указанного статуса на указанную категорию
     *
     * @param prstatusId ID статуса
     * @param categoryId ID категории
     * @return Список прав, которые есть у указанного статуса на указанную категорию
     * @throws GranException при необходимости
     */
    public List<String> getCategoryRuleList(String prstatusId, String categoryId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("select cpr.type from com.trackstudio.model.Cprstatus cpr where cpr.category = ? and" +
                    " cpr.prstatus = ?", categoryId, prstatusId);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список доступных категорий для задачи
     *
     * @param taskId ID задачи
     * @return Список доступных категорий
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Category
     */
    public ArrayList<Category> getAvailableCategoryList(String taskId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            ArrayList<Category> result = new ArrayList<Category>();
            for (TaskRelatedInfo info : TaskRelatedManager.getInstance().getTaskRelatedInfoChain(null, taskId))
                result.addAll(hu.getList("select c from com.trackstudio.model.Category c where c.task=? order by c.name", info.getId()));
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список всех доступных категорий для задачи
     *
     * @param taskId ID задачи
     * @return Список всех доступных категорий
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Category
     */
    public List<Category> getAllAvailableCategoryList(String taskId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            ArrayList<Category> result = new ArrayList<Category>();
            for (Category c : (List<Category>) hu.getList("from com.trackstudio.model.Category c order by c.name")) {
                if (TaskRelatedManager.getInstance().hasPath(taskId, c.getTask().getId()))
                    result.add(c);
            }
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список всех
     *
     * @return Список всех категорий
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Category
     */
    public List getAllCategoryList() throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("from com.trackstudio.model.Category c order by c.name");
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удаляет указанную категорию и производит инвалидацию кеша
     *
     * @param categoryId ID категории
     * @throws GranException при необходимости
     */
    public void deleteCategory(String categoryId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            CprstatusCacheManager.getInstance().invalidateRemoveCategory(categoryId);
            categoryCacheManager.invalidateCategory(categoryId);
            hu.deleteObject(Category.class, categoryId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Производит редактирование категории и инвалидацию кеша
     *
     * @param categoryId          ID категории
     * @param name                Название категории
     * @param action              Название действия
     * @param description         Описание категории
     * @param handlerRequired     Необходим ли ответственный
     * @param groupHandlerAllowed Можно ли задавать группу в качестве ответственного
     * @param workflowId          ID процесса
     * @param budget              Формат бюджета
     * @param preferences         Настройки
     * @param icon                Иконка категории
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.manager.SafeString
     */
    public void editCategory(String categoryId, SafeString name, SafeString action, SafeString description, boolean handlerRequired,
                             boolean groupHandlerAllowed, String workflowId, String budget, SafeString preferences, SafeString icon) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Category obj = KernelManager.getFind().findCategory(categoryId);
            String old = obj.getWorkflow().getId();
            obj.setWorkflow(workflowId);
            obj.setName(name != null ? name.toString() : null);
            obj.setAction(action != null ? action.toString() : null);
            obj.setBudget(budget);
            obj.setDescription(description != null ? description.toString() : null);
            obj.setHandlerRequired(handlerRequired ? 1 : 0);
            obj.setGroupHandlerAllowed(groupHandlerAllowed ? 1 : 0);
            obj.setPreferences(preferences != null ? preferences.toString() : null);
            obj.setIcon(icon != null ? icon.toString() : null);
            hu.updateObject(obj);
            if (!old.equals(workflowId)) {
                TaskRelatedManager.getInstance().invalidateWFUDWhenChangeCategory(old);
                TaskRelatedManager.getInstance().invalidateWFUDWhenChangeCategory(workflowId);
            }
            categoryCacheManager.invalidateCategory(categoryId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Редактирует настройки категории
     *
     * @param categoryId      ID категории
     * @param preferences Настройки
     * @throws GranException при необходимости
     */
    public void setPreferences(String categoryId, String preferences) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Category c = KernelManager.getFind().findCategory(categoryId);
            c.setPreferences(preferences);
            hu.updateObject(c);
            categoryCacheManager.invalidateCategory(categoryId);
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает шаблон категории.
     *
     * @param categoryId ID категории, для которой возвращается шаблон.
     * @return Шаблон категории
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Category
     */
    public String getTemplate(String categoryId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            String description = "";
            if (categoryId != null) {
                Category obj = KernelManager.getFind().findCategory(categoryId);
                if (obj.getTemplate() != null)
                    description = KernelManager.getLongText().getLongtext(obj.getTemplate().getId());
            }
            if (description == null)
                description = "";
            return description;
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Устанавливает шаблон для категории.
     *
     * @param categoryId ID категории, для которой устанавливается шаблон.
     * @param template   Шаблон
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.manager.SafeString
     */
    public void setTemplate(String categoryId, SafeString template) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Category category = KernelManager.getFind().findCategory(categoryId);
            Longtext lt = category.getTemplate();
            String newLongtext = null;
            String oldLongtextId = null;
            if (lt != null) {
                String oldformula = KernelManager.getLongText().getLongtext(lt.getId());
                if (!oldformula.equals(template.toString())) {
                    oldLongtextId = category.getTemplate().getId();
                    if (template.length() > 0)
                        newLongtext = KernelManager.getLongText().createLongtext(oldLongtextId, template.toString());
                }
            } else {
                if (template.length() > 0)
                    newLongtext = KernelManager.getLongText().createLongtext(null, template.toString());
            }
            if (oldLongtextId != null) {
                category.setTemplate(KernelManager.getFind().findLongtext(newLongtext));
            } else if (lt == null && newLongtext != null)
                category.setTemplate(KernelManager.getFind().findLongtext(newLongtext));
            hu.updateObject(category);
            categoryCacheManager.invalidateCategory(categoryId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Создается категория
     *
     * @param taskId              ID  задачи, для которой создается категория
     * @param name                Название категории
     * @param workflowId          ID процесса, на основании которого создается категория
     * @param handlerRequired     Необходим ли ответственный
     * @param groupHandlerAllowed Можно ли задавать группу в качестве ответственного
     * @return ID созданной категории
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.manager.SafeString
     */
    public String createCategory(String taskId, SafeString name, String workflowId, boolean handlerRequired, boolean groupHandlerAllowed) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            return hu.createObject(new Category(name != null ? name.toString() : null, workflowId, taskId, null, handlerRequired, groupHandlerAllowed));
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Проверяется, можно ли изменять процесс для категории или нет.
     * Если есть хоть одна задача с заданной категорий, то менять процесс нельзя
     *
     * @param categoryId ID  категории, для которой произовадится проверка
     * @return TRUE - если можно менять процесс, FALSE - если нельзя
     * @throws GranException при необходимости
     */
    public boolean canChangeWorkflow(String categoryId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("select t.id from com.trackstudio.model.Task t where t.category=?", categoryId).isEmpty();
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Создается правило доступа типа type для указанного статуса и категории
     *
     * @param type       тип правила доступа
     * @param categoryId ID  категории
     * @param prstatusId ID  статуса
     * @throws GranException при необходимости
     */
    public void addPrstatusCategory(String type, String categoryId, String prstatusId) throws GranException {
        CprstatusCacheManager.getInstance().invalidateSingle(prstatusId, categoryId);
        if (type != null && type.length() != 0)
            hu.createObject(new Cprstatus(type, categoryId, prstatusId));
    }

    /**
     * Устанавливаются правила доступа для указанных категории и статуса
     *
     * @param types      типы правила доступа
     * @param categoryId ID  категории
     * @param prstatusId ID  статуса
     * @throws GranException при необходимости
     */
    public void setCategoryRule(String categoryId, String prstatusId, List<String> types) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        lockManager.getLock(prstatusId).lock();
        try {
            resetCategoryRule(categoryId, prstatusId);
            for (String type : types) {
                if (type != null) {
                    addPrstatusCategory(type, categoryId, prstatusId);
                }
            }
            categoryCacheManager.invalidateCategoryIsValid(categoryId);
        } finally {
            if (w) lockManager.releaseConnection(className);
            lockManager.getLock(prstatusId).unlock();

        }
    }

    /**
     * Для указанного статуса устанавливаются правила доступа на указанные категории
     *
     * @param map        содержит карту ключ-значение, где ключ - ID категории, значение - список назначаемых прав
     * @param prstatusId ID  статуса
     * @throws GranException при необходимости
     */
    public void setCategoryRuleMap(HashMap<String, List<String>> map, String prstatusId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        lockManager.getLock(prstatusId).lock();
        try {
            for (String categoryId : map.keySet()) {
                List list = hu.getList("select c from com.trackstudio.model.Cprstatus c where c.category.id=? and c.prstatus.id=? ", categoryId, prstatusId);
                for (Object role : list) {
                    Cprstatus cp = (Cprstatus) role;
                    hu.deleteObject(cp.getClass(), cp.getId());
                }
                CprstatusCacheManager.getInstance().invalidateSingle(prstatusId, categoryId);
                for (String type : map.get(categoryId)) {
                    CprstatusCacheManager.getInstance().invalidateSingle(prstatusId, categoryId);
                    if (type != null && type.length() != 0)
                        hu.createObject(new Cprstatus(type, categoryId, prstatusId));
                }
                categoryCacheManager.invalidateCategoryIsValid(categoryId);
            }
            hu.cleanSession();
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(className);
            lockManager.getLock(prstatusId).unlock();
        }
    }

    /**
     * Удаляет правила доступа для указанной кактегории и статуса
     *
     * @param categoryId ID  категории
     * @param prstatusId ID  статуса
     * @throws GranException при необходимости
     */
    private void resetCategoryRule(String categoryId, String prstatusId) throws GranException {
        hu.executeDML("delete from com.trackstudio.model.Cprstatus c where c.category=? and c.prstatus=?", categoryId, prstatusId);
        hu.cleanSession();
        CprstatusCacheManager.getInstance().invalidateSingle(prstatusId, categoryId);
    }

    /**
     * Создается отношение между родительской и дочерней категориями и происходит инвалидация кеша
     *
     * @param parentCategoryId ID родительской категории
     * @param related          ID дочерней категории
     * @throws GranException при необходимости
     */
    public void addRelatedCategory(String parentCategoryId, String related) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            hu.createObject(new Catrelation(parentCategoryId, related));
            hu.cleanSession();
            categoryCacheManager.invalidateCategory(parentCategoryId);
            categoryCacheManager.invalidateCategoryIsValid(related);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удаляется отношение между родительской и дочерней категориями и происходит инвалидация кеша
     *
     * @param parentCategoryId ID родительской категории
     * @param related          ID дочерней категории
     * @throws GranException при необходимости
     */
    public void removeRelatedCategory(String parentCategoryId, String related) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            hu.executeDML("delete from com.trackstudio.model.Catrelation c where c.category=? and c.child=?", parentCategoryId, related);
            hu.cleanSession();
            categoryCacheManager.invalidateCategory(parentCategoryId);
            categoryCacheManager.invalidateCategoryIsValid(related);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }



    /**
     * Возвращает список категорий, которые который указанный пользователь может создавать для текущей задачи с учетом дочерних категорий
     *
     * @param taskId         ID задачи
     * @param userId         ID пользователя
     * @param lookAtChildren Нужно ли учитывать дожерние категории
     * @return Список категорий
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Category
     */
    public List<Category> getCreatableCategoryList(String taskId, String userId, boolean lookAtChildren, boolean paste) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            Set<Category> ret = new HashSet<Category>();

            ArrayList<Category> all = new ArrayList<Category>();
            ArrayList<Category> handler = new ArrayList<Category>();
            ArrayList<Category> submitter = new ArrayList<Category>();
            ArrayList<Category> sh = new ArrayList<Category>();
            ArrayList<Category> editAllForPaste = new ArrayList<Category>();

            ArrayList<Category> availCat = KernelManager.getCategory().getAvailableCategoryList(taskId);
            String userPrstatusId = UserRelatedManager.getInstance().find(userId).getPrstatusId();
            Set<Prstatus> prstatusSet = KernelManager.getPrstatus().getAvailablePrstatusList("1");
            for (String prstatusId : TaskRelatedManager.getInstance().getAllowedPrstatuses(userId, taskId, userPrstatusId)) {
                for (Category c : availCat) {
                    if (!Preferences.isCategoryHidden(c.getPreferences()) && getCategoryIsValid(c.getId(), taskId, prstatusSet)) {
                        CprstatusCacheItem cci = CprstatusCacheManager.getInstance().find(prstatusId, c.getId());
                        for (String type : cci.getCprsList()) {

                            if (type != null) {
                                if (type.equals(CategoryConstants.CREATE_ALL))
                                    all.add(c);
                                if (type.equals(CategoryConstants.CREATE_HANDLER))
                                    handler.add(c);
                                if (type.equals(CategoryConstants.CREATE_SUBMITTER))
                                    submitter.add(c);
                                if (type.equals(CategoryConstants.CREATE_SUBMITTER_AND_HANDLER))
                                    sh.add(c);
                                if (type.equals(CategoryConstants.EDIT_ALL) && paste)
                                    editAllForPaste.add(c);
                            }
                        }
                    }
                }
            }

            TaskRelatedInfo task = TaskRelatedManager.getInstance().find(taskId);

            String catid = task.getCategoryId();
            String handlerUserId = task.getHandlerUserId();
            String handlerGroupId = task.getHandlerGroupId();
            String submitterid = task.getSubmitterId();

            submitter.addAll(sh);
            handler.addAll(sh);


            List<Category> child = null;
            if (catid != null && lookAtChildren)
                child = KernelManager.getCategory().getChildrenCategoryList(catid, taskId);
            for (Category cat : all) {
                if (child == null || child.contains(cat)) {
                    ret.add(cat);
                }
            }
            for (Category cat : handler) {
                if ((child == null || child.contains(cat)) && (userId.equals(handlerUserId) || userPrstatusId.equals(handlerGroupId))) {
                    ret.add(cat);
                }
            }
            for (Category cat : submitter) {
                if ((child == null || child.contains(cat)) && userId.equals(submitterid)) {
                    ret.add(cat);
                }
            }
            for (Category cat: editAllForPaste) {
                if (child == null || child.contains(cat)) {
                    ret.add(cat);
                }
            }
            return new ArrayList<Category>(ret);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }


    public boolean isCategoryCreatable(String taskId, String userId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            TaskRelatedInfo task = TaskRelatedManager.getInstance().find(taskId);
            String catid = task.getCategoryId();
            String userPrstatusId = UserRelatedManager.getInstance().find(userId).getPrstatusId();
            String handlerUserId = task.getHandlerUserId();
            String handlerGroupId = task.getHandlerGroupId();
            String submitterid = task.getSubmitterId();
            Set<Prstatus> prstatusSet = KernelManager.getPrstatus().getAvailablePrstatusList("1");
            for (String prstatusId : TaskRelatedManager.getInstance().getAllowedPrstatuses(userId, taskId, userPrstatusId)) {
                if (!Preferences.isCategoryHidden(KernelManager.getFind().findCategory(task.getCategoryId()).getPreferences()) && getCategoryIsValid(catid, taskId, prstatusSet)) {
                    CprstatusCacheItem cci = CprstatusCacheManager.getInstance().find(prstatusId, catid);
                    for (String type : cci.getCprsList()) {
                        if (type != null) {
                            if (type.equals(CategoryConstants.CREATE_ALL))
                                return true;
                            if (type.equals(CategoryConstants.CREATE_HANDLER))
                                if (userId.equals(handlerUserId) || userPrstatusId.equals(handlerGroupId)) return true;
                            if (type.equals(CategoryConstants.CREATE_SUBMITTER))
                                if (userId.equals(submitterid)) return true;
                            if (type.equals(CategoryConstants.CREATE_SUBMITTER_AND_HANDLER))
                                if (userId.equals(submitterid) || userId.equals(handlerUserId) || userPrstatusId.equals(handlerGroupId) ) return true;
                        }
                    }
                }

            }
            return false;

        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }
    /**
     * Проверяет доступна ли категория для пользователя
     *
     * @param taskId         ID задачи
     * @param type           ID пользователя
     * @param userId         ID пользователя
     * @param categoryId     ID пользователя
     * @param userPrstatusId ID пользователя
     * @return TRUE - если категория доступна для пользователя, FALSE - если нет
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Category
     */
    private boolean isCategoryAvailableForUser(String taskId, String type, String userId, String categoryId, String userPrstatusId) throws GranException {
        for (String prstatusId : TaskRelatedManager.getInstance().getAllowedPrstatuses(userId, taskId, userPrstatusId)) {
            if (CprstatusCacheManager.getInstance().find(prstatusId, categoryId).getCprsList().contains(type))
                return true;
        }
        return false;
    }

    /**
     * Проверяет есть ли у указанного статуса тип доступа type на указанную категорию
     *
     * @param type       тип
     * @param groupId    ID пользователя
     * @param categoryId ID пользователя
     * @return TRUE - если категория доступна для статуса, FALSE - если нет
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Category
     */
    private boolean isCategoryAvailableForGroup(String type, String groupId, String categoryId) throws GranException {
        return CprstatusCacheManager.getInstance().find(groupId, categoryId).getCprsList().contains(type);
    }

    /**
     * Проверяеет возможен ли тип операции type для указанных задачи, пользователя, категории и статуса
     *
     * @param taskId         ID задачи
     * @param userId         ID пользователя
     * @param categoryId     ID категории
     * @param userPrstatusId ID статуса
     * @param type           тип операции
     * @return TRUE - если операция возможна, FALSE - если нет
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Category
     */
    private boolean isAvailableCategoryOperation(String taskId, String userId, String categoryId, String userPrstatusId, int type) throws GranException {
        if (type == DELETE)
            if (isCategoryAvailableForUser(taskId, CategoryConstants.DELETE_ALL, userId, categoryId, userPrstatusId))
                return true;
        if (type == EDIT)
            if (isCategoryAvailableForUser(taskId, CategoryConstants.EDIT_ALL, userId, categoryId, userPrstatusId))
                return true;
        if (type == VIEW)
            if (isCategoryAvailableForUser(taskId, CategoryConstants.VIEW_ALL, userId, categoryId, userPrstatusId))
                return true;
        if (type == HANDLER)
            if (isCategoryAvailableForUser(taskId, CategoryConstants.BE_HANDLER_ALL, userId, categoryId, userPrstatusId))
                return true;

        String handlerid = TaskRelatedManager.getInstance().find(taskId).getHandlerId();
        String handlerUserId = null;
        String handlerGroupId = null;
        if (handlerid != null) {
            Usersource us = KernelManager.getFind().findUsersource(handlerid);
            handlerUserId = us.getUser() == null ? null : us.getUser().getId();
            handlerGroupId = us.getPrstatus() == null ? null : us.getPrstatus().getId();
        }

        if (handlerUserId != null && userId.equals(handlerUserId)) {
            if (type == DELETE)
                if (isCategoryAvailableForUser(taskId, CategoryConstants.DELETE_HANDLER, userId, categoryId, userPrstatusId))
                    return true;
            if (type == EDIT)
                if (isCategoryAvailableForUser(taskId, CategoryConstants.EDIT_HANDLER, userId, categoryId, userPrstatusId))
                    return true;

            if (type == HANDLER)
                if (isCategoryAvailableForUser(taskId, CategoryConstants.BE_HANDLER_HANDLER, userId, categoryId, userPrstatusId))
                    return true;
        }

        if (handlerGroupId != null && userPrstatusId.equals(handlerGroupId)) {
            if (type == DELETE)
                if (isCategoryAvailableForGroup(CategoryConstants.DELETE_HANDLER, handlerGroupId, categoryId) || isCategoryAvailableForGroup(CategoryConstants.DELETE_SUBMITTER_AND_HANDLER, handlerGroupId, categoryId))
                    return true;
            if (type == EDIT)
                if (isCategoryAvailableForGroup(CategoryConstants.EDIT_HANDLER, handlerGroupId, categoryId) || isCategoryAvailableForGroup(CategoryConstants.EDIT_SUBMITTER_AND_HANDLER, handlerGroupId, categoryId))
                    return true;
            if (type == HANDLER)
                if (isCategoryAvailableForGroup(CategoryConstants.BE_HANDLER_HANDLER, handlerGroupId, categoryId) || isCategoryAvailableForGroup(CategoryConstants.BE_HANDLER_SUBMITTER_AND_HANDLER, handlerGroupId, categoryId))
                    return true;
        }

        String submitterid = TaskRelatedManager.getInstance().find(taskId).getSubmitterId();

        if (userId.equals(submitterid)) {
            if (type == DELETE)
                if (isCategoryAvailableForUser(taskId, CategoryConstants.DELETE_SUBMITTER, userId, categoryId, userPrstatusId))
                    return true;
            if (type == EDIT)
                if (isCategoryAvailableForUser(taskId, CategoryConstants.EDIT_SUBMITTER, userId, categoryId, userPrstatusId))
                    return true;
            if (type == VIEW)
                if (isCategoryAvailableForUser(taskId, CategoryConstants.VIEW_SUBMITTER, userId, categoryId, userPrstatusId))
                    return true;
            if (type == HANDLER)
                if (isCategoryAvailableForUser(taskId, CategoryConstants.BE_HANDLER_SUBMITTER, userId, categoryId, userPrstatusId))
                    return true;
        }

        if (userId.equals(submitterid) || (handlerUserId != null && userId.equals(handlerUserId))) {
            if (type == DELETE)
                if (isCategoryAvailableForUser(taskId, CategoryConstants.DELETE_SUBMITTER_AND_HANDLER, userId, categoryId, userPrstatusId))
                    return true;
            if (type == EDIT)
                if (isCategoryAvailableForUser(taskId, CategoryConstants.EDIT_SUBMITTER_AND_HANDLER, userId, categoryId, userPrstatusId))
                    return true;
            if (type == HANDLER)
                if (isCategoryAvailableForUser(taskId, CategoryConstants.BE_HANDLER_SUBMITTER_AND_HANDLER, userId, categoryId, userPrstatusId))
                    return true;
        }
        return false;
    }

    /**
     * Проверяеет возможен ли тип операции type для указанных задачи, статуса и категории
     *
     * @param taskId     ID задачи
     * @param groupId    ID статуса
     * @param categoryId ID категории
     * @param type       тип операции
     * @return TRUE - если операция возможна, FALSE - если нет
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Category
     */
    private boolean isAvailableCategoryOperationForGroup(String taskId, String groupId, String categoryId, int type) throws GranException {
        if (type == DELETE)
            if (isCategoryAvailableForGroup(CategoryConstants.DELETE_ALL, groupId, categoryId))
                return true;
        if (type == EDIT)
            if (isCategoryAvailableForGroup(CategoryConstants.EDIT_ALL, groupId, categoryId))
                return true;
        if (type == VIEW)
            if (isCategoryAvailableForGroup(CategoryConstants.VIEW_ALL, groupId, categoryId))
                return true;
        if (type == HANDLER)
            if (isCategoryAvailableForGroup(CategoryConstants.BE_HANDLER_ALL, groupId, categoryId))
                return true;

        String handlerid = null;
        handlerid = TaskRelatedManager.getInstance().find(taskId).getHandlerId();
        String handlerGroupId = null;
        if (handlerid != null) {
            Usersource us = KernelManager.getFind().findUsersource(handlerid);
            handlerGroupId = us.getPrstatus() == null ? null : us.getPrstatus().getId();
        }

        if (handlerGroupId != null && groupId.equals(handlerGroupId)) {
            if (type == DELETE)
                if (isCategoryAvailableForGroup(CategoryConstants.DELETE_HANDLER, groupId, categoryId))
                    return true;
            if (type == EDIT)
                if (isCategoryAvailableForGroup(CategoryConstants.EDIT_HANDLER, groupId, categoryId))
                    return true;

            if (type == HANDLER)
                if (isCategoryAvailableForGroup(CategoryConstants.BE_HANDLER_HANDLER, groupId, categoryId))
                    return true;

            if (type == DELETE)
                if (isCategoryAvailableForGroup(CategoryConstants.DELETE_SUBMITTER_AND_HANDLER, groupId, categoryId))
                    return true;
            if (type == EDIT)
                if (isCategoryAvailableForGroup(CategoryConstants.EDIT_SUBMITTER_AND_HANDLER, groupId, categoryId))
                    return true;
            if (type == HANDLER)
                if (isCategoryAvailableForGroup(CategoryConstants.BE_HANDLER_SUBMITTER_AND_HANDLER, groupId, categoryId))
                    return true;
        }

        return false;
    }

    /**
     * Проверяеет есть ли право на удаление категории
     *
     * @param taskId         ID задачи
     * @param userId         ID пользователя
     * @param categoryId     ID категории
     * @param userPrstatusId ID статуса
     * @return TRUE - если есть, FALSE - если нет
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Category
     */
    public boolean isCategoryDeletable(String taskId, String userId, String categoryId, String userPrstatusId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return isAvailableCategoryOperation(taskId, userId, categoryId, userPrstatusId, DELETE);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Проверяеет есть ли право на редактирование категории
     *
     * @param taskId         ID задачи
     * @param userId         ID пользователя
     * @param categoryId     ID категории
     * @param userPrstatusId ID статуса
     * @return TRUE - если есть, FALSE - если нет
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Category
     */
    public boolean isCategoryEditable(String taskId, String userId, String categoryId, String userPrstatusId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return isAvailableCategoryOperation(taskId, userId, categoryId, userPrstatusId, EDIT);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Проверяеет есть ли право на просмотр категории
     *
     * @param taskId         ID задачи
     * @param userId         ID пользователя
     * @param categoryId     ID категории
     * @param userPrstatusId ID статуса
     * @return TRUE - если есть, FALSE - если нет
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Category
     */
    public boolean isCategoryViewable(String taskId, String userId, String categoryId, String userPrstatusId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            Key.KeyID keyItem = new Key.FourParam(taskId, userId, categoryId, userPrstatusId);
            Boolean isView = categoryCacheManager.getCategoryIsViewable(keyItem);
            if (isView == null) {
                isView = isAvailableCategoryOperation(taskId, userId, categoryId, userPrstatusId, VIEW);
                categoryCacheManager.setCategoryIsViewable(keyItem, isView);
            }
            return isView;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Проверяеет есть ли право у пользователя быть ответственным для категории
     *
     * @param taskId         ID задачи
     * @param userId         ID пользователя
     * @param categoryId     ID категории
     * @param userPrstatusId ID статуса
     * @param isNew          Новая ли задача создается?
     * @param submitter      ID автора
     * @return TRUE - если есть, FALSE - если нет
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Category
     */
    public boolean isCategoryCanBeHandler(String taskId, String userId, String categoryId, String userPrstatusId, boolean isNew, String submitter) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            if (!isNew)
                return isAvailableCategoryOperation(taskId, userId, categoryId, userPrstatusId, HANDLER);
            else {
                List<String> cprsList = CprstatusCacheManager.getInstance().find(userPrstatusId, categoryId).getCprsList();
//                String handlerStatusId = null;
                String handlerGroupId = null;
                String handlerUserId = null;

                String handlerId = TaskRelatedManager.getInstance().find(taskId).getHandlerId();
                if (handlerId != null && !handlerId.equals("nullusersource")) {
                    Usersource us = KernelManager.getFind().findUsersource(handlerId);
                    handlerGroupId = us.getPrstatus() == null ? null : us.getPrstatus().getId();
                    handlerUserId = us.getUser() == null ? null : us.getUser().getId();
                }
                if (cprsList.contains(CategoryConstants.BE_HANDLER_ALL))
                    return true;
                if ((cprsList.contains(CategoryConstants.BE_HANDLER_HANDLER) || cprsList.contains(CategoryConstants.BE_HANDLER_SUBMITTER_AND_HANDLER)) &&
                        handlerUserId != null && handlerUserId.equals(userId))
                    return true;
                if ((cprsList.contains(CategoryConstants.BE_HANDLER_SUBMITTER) || cprsList.contains(CategoryConstants.BE_HANDLER_SUBMITTER_AND_HANDLER)
                ) && submitter.equals(userId))
                    return true;
                if (handlerGroupId != null && userPrstatusId.equals(handlerGroupId)) {
                    if (cprsList.contains(CategoryConstants.BE_HANDLER_HANDLER))
                        return true;
                }
                return false;
            }
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Проверяеет есть ли право у группы на быть ответственной для категории
     *
     * @param taskId     ID задачи
     * @param groupId    ID статуса
     * @param categoryId ID категории
     * @param isNew      Новая ли задача создается?
     * @return TRUE - если есть, FALSE - если нет
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Category
     */
    public boolean isCategoryCanBeHandlerForGroup(String taskId, String groupId, String categoryId, boolean isNew) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            if (!isNew)
                return isAvailableCategoryOperationForGroup(taskId, groupId, categoryId, HANDLER);
            else {
                if (isCategoryAvailableForGroup(CategoryConstants.BE_HANDLER_ALL, groupId, categoryId))
                    return true;
                String handlerid = null;
                handlerid = TaskRelatedManager.getInstance().find(taskId).getHandlerId();
                String handlerGroupId = null;
                if (handlerid != null) {
                    Usersource us = KernelManager.getFind().findUsersource(handlerid);
                    handlerGroupId = us.getPrstatus() == null ? null : us.getPrstatus().getId();
                }
                if (handlerGroupId != null && groupId.equals(handlerGroupId)) {
                    return (isCategoryAvailableForGroup(CategoryConstants.BE_HANDLER_HANDLER, groupId, categoryId) ||
                            isCategoryAvailableForGroup(CategoryConstants.BE_HANDLER_SUBMITTER_AND_HANDLER, groupId, categoryId));
                }
                return false;
            }
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Устанавливает триггеры для категории
     *
     * @param categoryId   ID категории
     * @param before       before-триггер
     * @param insteadOf    instanseof-триггер
     * @param after        after-триггер
     * @param updBefore    update before-триггер
     * @param updInsteadOf update-instanseof-триггер
     * @param updAfter     update-after-триггер
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Category
     */
    public void setCategoryTrigger(String categoryId, SafeString before, SafeString insteadOf, SafeString after, SafeString updBefore, SafeString updInsteadOf, SafeString updAfter) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Category category = (Category) hu.getObject(Category.class, categoryId);
            Trigger crTrigger;
            Trigger updTrigger;
            if (category.getCrTrigger() == null) {
                crTrigger = new Trigger();
                hu.createObject(crTrigger);
            } else {
                crTrigger = category.getCrTrigger();
            }
            if (category.getUpdTrigger() == null) {
                updTrigger = new Trigger();
                hu.createObject(updTrigger);
            } else {
                updTrigger = category.getUpdTrigger();
            }
            crTrigger.setBefore(before != null && before.length() != 0 ? before.toString() : null);
            crTrigger.setInsteadOf(insteadOf != null && insteadOf.length() != 0 ? insteadOf.toString() : null);
            crTrigger.setAfter(after != null && after.length() != 0 ? after.toString() : null);
            category.setCrTrigger(crTrigger);
            updTrigger.setBefore(updBefore != null && updBefore.length() != 0 ? updBefore.toString() : null);
            updTrigger.setInsteadOf(updInsteadOf != null && updInsteadOf.length() != 0 ? updInsteadOf.toString() : null);
            updTrigger.setAfter(updAfter != null && updAfter.length() != 0 ? updAfter.toString() : null);
            category.setUpdTrigger(updTrigger);
            hu.updateObject(category);
            hu.cleanSession();
        } catch(Exception e) {
            e.printStackTrace();
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Проверяеет валидна категория или нет
     *
     * @param categoryId ID категории
     * @param taskId     ID задачи
     * @return TRUE - если валидна, FALSE - если нет
     * @throws GranException при необходимости
     */
    public Boolean getCategoryIsValid(String categoryId, String taskId, Set<Prstatus> prstatusSet) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Boolean isValid = categoryCacheManager.getCategoryIsValid(categoryId);
            if (isValid == null) {
                isValid = true;
                Category category = KernelManager.getFind().findCategory(categoryId);
                if (!KernelManager.getWorkflow().getWorkflowIsValid(category.getWorkflow().getId()))
                    isValid = false;
                if (isValid) {
                    if (getParentCategoryList(categoryId, taskId).isEmpty())
                        isValid = false;
                }
                if (isValid) {
                    if (!isValidPermissions(categoryId, prstatusSet))
                        isValid = false;
                }
                categoryCacheManager.setCategoryIsValid(categoryId, isValid);
            }
            return isValid;
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Проверяеет валидны ли права доступа для категории или нет
     *
     * @param categoryId ID категории
     * @return TRUE - если валидны, FALSE - если нет
     * @throws GranException при необходимости
     */
    private Boolean isValidPermissions(String categoryId,  Set<Prstatus> prstatusSet) throws GranException {
//        Set<Prstatus> prstatusSet = KernelManager.getPrstatus().getAvailablePrstatusList("1");
        for (Prstatus prstatus : prstatusSet) {
            if (checkCategoryForPrstatus(categoryId, prstatus.getId())!=0) return false;
        }
        return true;
    }

    public int checkCategoryForPrstatus(String categoryId, String prstatus) throws GranException {
        List<String> rules = getCategoryRuleList(prstatus, categoryId);
        boolean viewAll = false;
        boolean viewS = false;


        if (rules.contains(CategoryConstants.VIEW_ALL)) {
            viewAll = true;
        } else if (rules.contains(CategoryConstants.VIEW_SUBMITTER)) {
            viewS = true;
        }

        int res = 0;
        if ((rules.contains(CategoryConstants.EDIT_ALL) && !viewAll) ||
                (rules.contains(CategoryConstants.EDIT_SUBMITTER) && !viewAll && !viewS) ||
                (rules.contains(CategoryConstants.EDIT_HANDLER) && !viewAll) ||
                (rules.contains(CategoryConstants.EDIT_SUBMITTER_AND_HANDLER) && !viewAll)) {

            res+=EDIT_INCORRECT;
        }
        if ((rules.contains(CategoryConstants.CREATE_ALL) && !viewAll && !viewS ) ||
                (rules.contains(CategoryConstants.CREATE_SUBMITTER) && !viewAll && !viewS ) ||
                (rules.contains(CategoryConstants.CREATE_HANDLER) && !viewAll) ||
                (rules.contains(CategoryConstants.CREATE_SUBMITTER_AND_HANDLER) && !viewAll )) {

            res+=CREATE_INCORRECT;
        }
        if ((rules.contains(CategoryConstants.DELETE_ALL) && !viewAll) ||
                (rules.contains(CategoryConstants.DELETE_SUBMITTER) && !viewAll && !viewS) ||
                (rules.contains(CategoryConstants.DELETE_HANDLER) && !viewAll) ||
                (rules.contains(CategoryConstants.DELETE_SUBMITTER_AND_HANDLER) && !viewAll)) {

            res+=DELETE_INCORRECT;
        }
        if ((rules.contains(CategoryConstants.BE_HANDLER_ALL) && !viewAll) ||
                (rules.contains(CategoryConstants.BE_HANDLER_SUBMITTER) && !viewAll && !viewS) ||
                (rules.contains(CategoryConstants.BE_HANDLER_HANDLER) && !viewAll) ||
                (rules.contains(CategoryConstants.BE_HANDLER_SUBMITTER_AND_HANDLER) && !viewAll)) {

            res+=BEHANDLER_INCORRECT;
        }
        return res;
    }

    public List<Category> getCategoryCreate(String taskId, String triggerId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            ArrayList<Category> result = new ArrayList<Category>();
            for (Category c : (List<Category>) hu.getList("from com.trackstudio.model.Category c where c.crTrigger=?", triggerId)) {
                if (TaskRelatedManager.getInstance().hasPath(taskId, c.getTask().getId()))
                    result.add(c);
            }
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    public List<Category> getCategoryUpdate(String taskId, String triggerId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            ArrayList<Category> result = new ArrayList<Category>();
            for (Category c : (List<Category>) hu.getList("from com.trackstudio.model.Category c where c.updTrigger=?", triggerId)) {
                if (TaskRelatedManager.getInstance().hasPath(taskId, c.getTask().getId()))
                    result.add(c);
            }
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

}