package com.trackstudio.securedkernel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.CategoryConstants;
import com.trackstudio.exception.AccessDeniedException;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.InvalidParameterException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.CategoryCacheItem;
import com.trackstudio.kernel.cache.CategoryCacheManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.model.Category;
import com.trackstudio.model.Prstatus;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.tools.ParameterValidator;
import com.trackstudio.tools.SecuredBeanUtil;

import net.jcip.annotations.Immutable;

/**
 * Класс CategoryManager содержит методы для работы с категориями.
 */
@Immutable
public class SecuredCategoryAdapterManager {

    private static final Log log = LogFactory.getLog(SecuredCategoryAdapterManager.class);
    private static final ParameterValidator pv = new ParameterValidator();
    private static final LockManager lockManager = LockManager.getInstance();
    /**
     * Возвращает список доступных категорий для задачи
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи
     * @return список категорий
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredCategoryBean
     */
    public ArrayList<SecuredCategoryBean> getAvailableCategoryList(SessionContext sc, String taskId) throws GranException {
        log.trace("getAvailableCategoryList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass().getName(), "getAvailableCategoryList", "sc", "1");
        if (taskId == null)
            throw new InvalidParameterException(this.getClass().getName(), "getAvailableCategoryList", "taskId", sc.getUserId());
        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "getAvailableCategoryList", sc, "!sc.taskOnSight(taskId)", taskId);
        boolean w = lockManager.acquireConnection(SecuredCategoryAdapterManager.class.getSimpleName());
        try {
            return (ArrayList<SecuredCategoryBean>) SecuredBeanUtil.toList(sc, KernelManager.getCategory().getAvailableCategoryList(taskId), SecuredBeanUtil.CATEGORY);
        } finally {
            if (w) lockManager.releaseConnection(SecuredCategoryAdapterManager.class.getSimpleName());
        }
    }

    /**
     * Возвращает список всех доступных категорий для задачи
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи
     * @return список категорий
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredCategoryBean
     */
    public ArrayList<SecuredCategoryBean> getAllAvailableCategoryList(SessionContext sc, String taskId) throws GranException {
        boolean w = lockManager.acquireConnection(SecuredCategoryAdapterManager.class.getSimpleName());
        try {
            log.trace("getAvailableCategoryList");
            if (sc == null)
                throw new InvalidParameterException(this.getClass().getName(), "getAvailableCategoryList", "sc", "1");
            if (taskId == null)
                throw new InvalidParameterException(this.getClass().getName(), "getAvailableCategoryList", "taskId", sc.getUserId());
            if (!sc.taskOnSight(taskId))
                throw new AccessDeniedException(this.getClass(), "getAvailableCategoryList", sc, "!sc.taskOnSight(taskId)", taskId);
            return (ArrayList<SecuredCategoryBean>) SecuredBeanUtil.toList(sc, KernelManager.getCategory().getAllAvailableCategoryList(taskId), SecuredBeanUtil.CATEGORY);
        } finally {
            if (w) lockManager.releaseConnection(SecuredCategoryAdapterManager.class.getSimpleName());
        }
    }

    /**
     * Возвращает список всех категорий для статуса
     *
     * @param sc       сессия пользователя
     * @param statusId ID статуса
     * @return список категорий для статуса
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredCategoryBean
     */
    public ArrayList<SecuredCategoryBean> getAllCategoryList(SessionContext sc, String statusId) throws GranException {
        boolean w = lockManager.acquireConnection(SecuredCategoryAdapterManager.class.getSimpleName());
        try {
            log.trace("getAvailableCategoryList");
            if (sc == null)
                throw new InvalidParameterException(this.getClass().getName(), "getAllCategoryList", "sc", "1");
            if (statusId == null)
                throw new InvalidParameterException(this.getClass().getName(), "getAllCategoryList", "statusId", sc.getUserId());
            ArrayList<SecuredCategoryBean> list = SecuredBeanUtil.toArrayList(sc, KernelManager.getCategory().getAllCategoryList(), SecuredBeanUtil.CATEGORY);
            ArrayList<SecuredCategoryBean> ret = new ArrayList<SecuredCategoryBean>();
            SecuredPrstatusBean pr = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, statusId);
            if (pr == null)
                throw new InvalidParameterException(this.getClass().getName(), "getAllCategoryList", "statusId", sc.getUserId());
            ArrayList<SecuredPrstatusBean> availabePrs = AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId());
            // Check if we can edit current prstatus
            boolean canEdit = false;
            for (SecuredPrstatusBean spb : availabePrs) {
                if (spb.getId().equals(statusId)) {
                    canEdit = true;
                    break;
                }
            }
            for (SecuredCategoryBean scb : list) {
                if (sc.canAction(Action.manageCategories, scb.getTaskId()) && scb.canView()) {
                    if (scb.getTaskId() == null) {
                        continue;
                    }
                    if (canEdit) {
                        if (sc.taskOnSight(scb.getTaskId())) {
                            ret.add(scb);
                        }
                    } else {
                        if (scb.canManage()) {
                            ret.add(scb);
                        }
                    }
                }
            }
            return ret;
        } finally {
            if (w) lockManager.releaseConnection(SecuredCategoryAdapterManager.class.getSimpleName());
        }
    }

    /**
     * Создает новую категорию
     *
     * @param sc                  сессия пользователя
     * @param taskId              ID родительской задачи
     * @param name                Название категории
     * @param workflowId          ID процесса
     * @param handlerRequired     Обязательность ответственного
     * @param groupHandlerAllowed Можно ли задавать группу в качестве ответственного
     * @return ID созданной категории
     * @throws GranException при необходимости
     */
    public String createCategory(SessionContext sc, String taskId, String name, String workflowId, boolean handlerRequired, boolean groupHandlerAllowed) throws GranException {
        log.trace("createCategory");
        if (sc == null)
            throw new InvalidParameterException(this.getClass().getName(), "createCategory", "sc", "1");
        if (taskId == null)
            throw new InvalidParameterException(this.getClass().getName(), "createCategory", "taskId", sc.getUserId());
        if (name == null || pv.badSmallDesc(name))
            throw new InvalidParameterException(this.getClass().getName(), "createCategory", "name", sc.getUserId());
        if (workflowId == null)
            throw new InvalidParameterException(this.getClass().getName(), "createCategory", "workflowId", sc.getUserId());

        SecuredWorkflowBean workflow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);

        if (!sc.canAction(Action.manageCategories, taskId))
            throw new AccessDeniedException(this.getClass(), "createCategory", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.createCategory, taskId)", taskId);
        if (!sc.allowedByACL(taskId))
            throw new AccessDeniedException(this.getClass(), "createCategory", sc, "!sc.allowedByACL(taskId)", taskId);
        if (!workflow.canView())
            throw new AccessDeniedException(this.getClass(), "createCategory", sc, "!workflow.canView()", workflowId);
        String id = KernelManager.getCategory().createCategory(taskId, SafeString.createSafeString(name), workflowId, handlerRequired, groupHandlerAllowed);
        if (sc.canAction(Action.manageCategories, taskId)) {
            for (SecuredPrstatusBean prsI : new TreeSet<SecuredPrstatusBean>(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId()))) {
                AdapterManager.getInstance().getSecuredCategoryAdapterManager().setCategoryRule(sc, id, prsI.getId(), CategoryConstants.CREATE_ALL, CategoryConstants.VIEW_ALL, CategoryConstants.EDIT_ALL, CategoryConstants.BE_HANDLER_ALL, CategoryConstants.DELETE_ALL);
            }
        }
        return id;
    }

    /**
     * Удаляет категорию по ее ID
     *
     * @param sc         сессия пользователя
     * @param categoryId ID категории
     * @throws GranException при необходимости
     */
    public void deleteCategory(SessionContext sc, String categoryId) throws GranException {
        log.trace("deleteCategory");
        if (sc == null)
            throw new InvalidParameterException(this.getClass().getName(), "deleteCategory", "sc", "1");
        if (categoryId == null)
            throw new InvalidParameterException(this.getClass().getName(), "deleteCategory", "categoryId", sc.getUserId());
        SecuredCategoryBean category = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, categoryId);
        //if (!(sc.canAction(com.trackstudio.kernel.cache.Action.deleteCategory, category.getTaskId()) && category.canUpdate()))
        //    throw new AccessDeniedException(this.getClass().getName(), "deleteCategory", sc.getUserId());

        if (!sc.canAction(Action.manageCategories, category.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "deleteCategory", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.deleteCategory, category.getTaskId())", categoryId);
        if (!category.canManage())
            throw new AccessDeniedException(this.getClass(), "deleteCategory", sc, "!category.canUpdate()", categoryId);
        KernelManager.getCategory().deleteCategory(categoryId);
    }

    /**
     * Возвращает шаблон описания для категории
     *
     * @param sc         сессия пользователя
     * @param categoryId ID категории
     * @return текст шаблона
     * @throws GranException при неоходимости
     */
    public String getTemplate(SessionContext sc, String categoryId) throws GranException {
        log.trace("getTemplate(categoryId='" + categoryId + "')");
        if (sc == null)
            throw new InvalidParameterException(this.getClass().getName(), "getTemplate", "sc", "1");
        if (categoryId == null)
            throw new InvalidParameterException(this.getClass().getName(), "getTemplate", "categoryId", sc.getUserId());
        return KernelManager.getCategory().getTemplate(categoryId);
    }

    /**
     * Устанавливает для категории текст шаблона описания
     *
     * @param sc         сессия пользователя
     * @param categoryId ID категории
     * @param template   текст шаблона
     * @throws GranException при необходимости
     */
    public void setTemplate(SessionContext sc, String categoryId, String template) throws GranException {
        log.trace("setTemplate(categoryId='" + categoryId + "')");

        if (sc == null)
            throw new InvalidParameterException(this.getClass().getName(), "setTemplate", "sc", "1");
        if (categoryId == null)
            throw new InvalidParameterException(this.getClass().getName(), "setTemplate", "categoryId", sc.getUserId());
        SecuredCategoryBean category = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, categoryId);
        //if (!(sc.canAction(com.trackstudio.kernel.cache.Action.editCategoryTemplate, category.getTaskId()) && category.canUpdate()))
        //    throw new AccessDeniedException(this.getClass(), "setTemplate", sc);
        if (!sc.canAction(Action.manageCategories, category.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "setTemplate", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.editCategoryTemplate, category.getTaskId())", categoryId);
        if (!category.canManage())
            throw new AccessDeniedException(this.getClass(), "setTemplate", sc, "!category.canUpdate()", categoryId);
        KernelManager.getCategory().setTemplate(categoryId, SafeString.createSafeString(template));
    }

    /**
     * Редактирует категорию
     *
     * @param sc                  сессия пользователя
     * @param categoryId          ID категории
     * @param name                Название категории
     * @param action              Название действия
     * @param description         Описание категории
     * @param handlerRequired     Обязательность ответственного
     * @param groupHandlerAllowed Можно ли задавать группу в качестве ответственного
     * @param workflowId          ID процесса
     * @param budget              ФОрмат бюджета
     * @param preferences         настройки категории
     * @param icon                иконка категории
     * @throws GranException при необходимости
     */
    public void editCategory(SessionContext sc, String categoryId, String name, String action, String description, boolean handlerRequired, boolean groupHandlerAllowed, String workflowId, String budget, String preferences, String icon) throws GranException {
        log.trace("editCategory(categoryId='" + categoryId + "', workflowId='" + workflowId + "')");
        if (sc == null)
            throw new InvalidParameterException(this.getClass().getName(), "editCategory", "sc", "1");
        if (categoryId == null)
            throw new InvalidParameterException(this.getClass().getName(), "editCategory", "categoryId", sc.getUserId());

        if (workflowId == null)
            throw new InvalidParameterException(this.getClass().getName(), "editCategory", "workflowId", sc.getUserId());
        SecuredCategoryBean category = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, categoryId);
        SecuredWorkflowBean workflow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
        //if (!(sc.canAction(com.trackstudio.kernel.cache.Action.editCategory, category.getTaskId()) && category.canUpdate() && workflow.canView() && KernelManager.getCategory().canChangeWorkflow(categoryId)))
        //    throw new AccessDeniedException(this.getClass(), "editCategory", sc);
        //if (budget.length()>0 && pv.badBudgetFormat(budget))
        //throw new InvalidParameterException(this.getClass().getName(), "editCategory", "budget", sc.getUserId());
        if (!category.canManage())
            throw new AccessDeniedException(this.getClass(), "editCategory", sc, "!category.canUpdate()", categoryId);
        if (!workflow.canView() || !KernelManager.getCategory().canChangeWorkflow(categoryId)) {
            workflowId = category.getWorkflowId();
        }
        KernelManager.getCategory().editCategory(categoryId, SafeString.createSafeString(name), SafeString.createSafeString(action), SafeString.createSafeString(description), handlerRequired, groupHandlerAllowed, workflowId, budget, SafeString.createSafeString(preferences), SafeString.createSafeString(icon));
    }

    /**
     * Возвращает список дочерних категорий для указанной
     *
     * @param sc         сессия пользователя
     * @param categoryId ID категории
     * @param taskId     ID задачи
     * @return список категорий
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredCategoryBean
     */
    public ArrayList<SecuredCategoryBean> getChildrenCategoryList(SessionContext sc, String categoryId, String taskId) throws GranException {
        log.trace("getChildrenCategoryList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getChildrenCategoryList", "sc", sc);
        if (categoryId == null)
            throw new InvalidParameterException(this.getClass(), "getChildrenCategoryList", "categoryId", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getChildrenCategoryList", "taskId", sc);
        SecuredCategoryBean category = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, categoryId);
        //if (!(sc.canAction(com.trackstudio.kernel.cache.Action.viewCategory, category.getTaskId()) && category.canView()))
        //    throw new AccessDeniedException(this.getClass(), "getChildrenCategoryList", sc);
        if (!sc.canAction(Action.manageCategories, taskId))
            throw new AccessDeniedException(this.getClass(), "getChildrenCategoryList", sc, "!sc.canAction(Action.viewCategory, c.canActi)", taskId);
        if (!category.canView())
            throw new AccessDeniedException(this.getClass(), "getChildrenCategoryList", sc, "!category.canView()", categoryId);

        ArrayList<SecuredCategoryBean> ret = new ArrayList<SecuredCategoryBean>();
        for (Object o : SecuredBeanUtil.toArrayList(sc, KernelManager.getCategory().getChildrenCategoryList(categoryId, taskId), SecuredBeanUtil.CATEGORY)) {
            SecuredCategoryBean scb = (SecuredCategoryBean) o;
            if (sc.canAction(Action.manageCategories, scb.getTaskId()))
                ret.add(scb);
        }
        return ret;
    }

    /**
     * Возвращает список родителских категорий для указанной
     *
     * @param sc         сессия пользователя
     * @param categoryId ID категории
     * @param taskId     ID задачи
     * @return список категорий
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredCategoryBean
     */
    public ArrayList<SecuredCategoryBean> getParentCategoryList(SessionContext sc, String categoryId, String taskId) throws GranException {
        boolean w = lockManager.acquireConnection(SecuredCategoryAdapterManager.class.getSimpleName());
        try {
            log.trace("getChildrenCategoryList");
            if (sc == null)
                throw new InvalidParameterException(this.getClass(), "getChildrenCategoryList", "sc", sc);
            if (categoryId == null)
                throw new InvalidParameterException(this.getClass(), "getChildrenCategoryList", "categoryId", sc);
            if (taskId == null)
                throw new InvalidParameterException(this.getClass(), "getChildrenCategoryList", "taskId", sc);
            SecuredCategoryBean category = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, categoryId);
            if (!sc.canAction(Action.manageCategories, category.getTaskId()))
                throw new AccessDeniedException(this.getClass(), "getChildrenCategoryList", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.viewCategory, category.getTaskId())", categoryId);
            if (!category.canView())
                throw new AccessDeniedException(this.getClass(), "getChildrenCategoryList", sc, "!category.canView()", categoryId);
            return SecuredBeanUtil.toArrayList(sc, KernelManager.getCategory().getParentCategoryList(categoryId, taskId), SecuredBeanUtil.CATEGORY);
        } finally {
            if (w) lockManager.releaseConnection(SecuredCategoryAdapterManager.class.getSimpleName());
        }
    }

    /**
     * Проверяет на наличие подкатегорий у указанной
     *
     * @param sc         сессия пользователя
     * @param categoryId ID категории
     * @param taskId     ID задачи
     * @return TRUE - подкатегории есть, FALSE - нет
     * @throws GranException при необходимости
     */
    public boolean hasSubcategories(SessionContext sc, String categoryId, String taskId) throws GranException {
        log.trace("hasSubcategories");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getChildrenCategoryList", "sc", sc);
        if (categoryId == null)
            throw new InvalidParameterException(this.getClass(), "getChildrenCategoryList", "categoryId", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getChildrenCategoryList", "taskId", sc);

        return !KernelManager.getCategory().getChildrenCategoryList(categoryId, taskId).isEmpty();
    }

    /**
     * Возвращает список доступных подкатегорий для указаннолй
     *
     * @param sc         сессия пользователя
     * @param categoryId ID категории
     * @return список категорий
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.CategoryCacheItem
     */
    public ArrayList<CategoryCacheItem> getAllPossibleSubcategories(SessionContext sc, String categoryId) throws GranException {
        log.trace("getAllPossibleSubcategories");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getAllPossibleSubcategories", "sc", sc);
        if (categoryId == null)
            throw new InvalidParameterException(this.getClass(), "getAllPossibleSubcategories", "categoryId", sc);
        SecuredCategoryBean category = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, categoryId);
        if (!category.canView())
            throw new AccessDeniedException(this.getClass(), "getAllPossibleSubcategories", sc, "!category.canView()", categoryId);
        ArrayList<CategoryCacheItem> result = new ArrayList<CategoryCacheItem>();
        ArrayList<String> categories = CategoryCacheManager.getInstance().getAllPossibleSubcategories(categoryId);
        for (String catid : categories) {
            CategoryCacheItem cat = CategoryCacheManager.getInstance().find(catid);
            if (sc.taskOnSight(cat.getTaskId())) {
                result.add(cat);
            }
        }
        return result;
    }

    /**
     * Устанавливает правила доступа для категории
     *
     * @param sc          сессия пользователя
     * @param categoryId  ID категории
     * @param prstatusId  ID статуса, для которого выставляются права
     * @param createType  CREATE_ALL, CREATE_HANDLER, CREATE_SUBMITTER, CREATE_SUBMITTER_AND_HANDLER или CREATE_NONE
     * @param viewType    VIEW_ALL, VIEW_HANDLER, VIEW_SUBMITTER, VIEW_SUBMITTER_AND_HANDLER или VIEW_NONE
     * @param modifyType  EDIT_ALL, EDIT_HANDLER, EDIT_SUBMITTER, EDIT_SUBMITTER_AND_HANDLER или EDIT_NONE
     * @param handlerType BE_HANDLER_ALL, BE_HANDLER_HANDLER, BE_HANDLER_SUBMITTER, BE_HANDLER_SUBMITTER_AND_HANDLER или BE_HANDLER_NONE
     * @param deleteType  DELETE_ALL, DELETE_HANDLER, DELETE_SUBMITTER, DELETE_SUBMITTER_AND_HANDLER или DELETE_NONE
     * @throws GranException при необходимости
     * @see com.trackstudio.constants.CategoryConstants
     */
    public void setCategoryRule(SessionContext sc, String categoryId, String prstatusId, String createType, String viewType, String modifyType, String handlerType, String deleteType) throws GranException {
        log.trace("setCategoryRule");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "setCategoryRule", "sc", sc);
        if (categoryId == null)
            throw new InvalidParameterException(this.getClass(), "setCategoryRule", "categoryId", sc);
        if (prstatusId == null)
            throw new InvalidParameterException(this.getClass(), "setCategoryRule", "prstatusId", sc);
        SecuredCategoryBean categoryBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, categoryId);
        SecuredPrstatusBean prstatusBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
        //if (!(sc.canAction(com.trackstudio.kernel.cache.Action.editCategoryPermission, categoryBean.getTaskId()) &&
        //        (sc.allowedByACL(categoryBean.getTaskId()) || sc.allowedByACL(prstatusBean.getUserId()))))
        //    throw new AccessDeniedException(this.getClass(), "setCategoryRule", sc);
        if (!sc.canAction(Action.manageCategories, categoryBean.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "setCategoryRule", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.editCategoryPermission, categoryBean.getTaskId())", categoryId);
        if (!(sc.allowedByACL(categoryBean.getTaskId()) || sc.allowedByUser(prstatusBean.getUserId())))
            throw new AccessDeniedException(this.getClass(), "setCategoryRule", sc, "!(sc.allowedByACL(categoryBean.getTaskId()) || sc.allowedByACL(prstatusBean.getUserId()))", categoryId, " * " + prstatusId);
        KernelManager.getCategory().setCategoryRule(categoryId, prstatusId, Arrays.asList(createType, viewType, modifyType, handlerType, deleteType));
    }

    /**
     * Добавляет подкатегорию для указанной
     *
     * @param sc       сессия пользователя
     * @param parentId ID родительской категории
     * @param related  ID дочерней категории
     * @throws GranException при необходимости
     */
    public void addRelatedCategory(SessionContext sc, String parentId, String related) throws GranException {
        log.trace("addRelatedCategory");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "addRelatedCategory", "sc", sc);
        if (parentId == null)
            throw new InvalidParameterException(this.getClass(), "addRelatedCategory", "parentId", sc);
        SecuredCategoryBean parentBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, parentId);
        if (!sc.canAction(Action.manageCategories, parentBean.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "addRelatedCategory", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.editCategory, parentBean.getTaskId())", parentId);
        if (related != null) {
            SecuredCategoryBean childBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, related);
            if (!parentBean.canView() || !childBean.canManage())
                throw new AccessDeniedException(this.getClass(), "addRelatedCategory", sc, "!(parentBean.canView() || childBean.canUpdate())", related);
        }
        KernelManager.getCategory().addRelatedCategory(parentId, related);
    }

    /**
     * Удаляет подкатегорию для укащанной
     *
     * @param sc       сессия пользователя
     * @param parentId ID родительской категории
     * @param related  ID дочерней категории
     * @throws GranException при необходимости
     */
    public void removeRelatedCategory(SessionContext sc, String parentId, String related) throws GranException {
        log.trace("removeRelatedCategory");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "removeRelatedCategory", "sc", sc);
        if (parentId == null)
            throw new InvalidParameterException(this.getClass(), "removeRelatedCategory", "parentId", sc);
        SecuredCategoryBean parentBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, parentId);
        if (!sc.canAction(Action.manageCategories, parentBean.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "removeRelatedCategory", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.editCategory, parentBean.getTaskId())", parentId);
        if (related != null) {
            SecuredCategoryBean childBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, related);
            if (!parentBean.canView() || !childBean.canUpdate())
                throw new AccessDeniedException(this.getClass(), "removeRelatedCategory", sc, "!(parentBean.canView() || childBean.canUpdate())", parentId + " * " + related);
        }
        KernelManager.getCategory().removeRelatedCategory(parentId, related);
    }

    /**
     * Возвращает список доступных категорий для задачи
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи
     * @return список категорий
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredCategoryBean
     */
    public ArrayList<SecuredCategoryBean> getCreatableCategoryList(SessionContext sc, String taskId, boolean paste) throws GranException {
        log.trace("getCreatableCategoryList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getCreatableCategoryList", "sc", null);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getCreatableCategoryList", "taskId", sc);
        if (!sc.allowedByACL(taskId))
            throw new AccessDeniedException(this.getClass(), "getCreatableCategoryList", sc, "!sc.allowedByACL(taskId)", taskId);
        boolean w = lockManager.acquireConnection(SecuredCategoryAdapterManager.class.getSimpleName());
        try {
            return new ArrayList<SecuredCategoryBean>(new TreeSet<SecuredCategoryBean>(SecuredBeanUtil.toArrayList(sc, KernelManager.getCategory().getCreatableCategoryList(taskId, sc.getUserId(), true, paste), SecuredBeanUtil.CATEGORY)));
        } finally {
            if (w) lockManager.releaseConnection(SecuredCategoryAdapterManager.class.getSimpleName());
        }

    }

    public ArrayList<SecuredCategoryBean> getCreatableCategoryList(SessionContext sc, String taskId) throws GranException {
        return getCreatableCategoryList(sc, taskId, false);
    }

    /**
     * Возвращает список прав, которые есть у указанного статуса на указанную категорию
     *
     * @param sc         сессия пользователя
     * @param prstatusId ID статуса
     * @param categoryId ID категории
     * @return Список прав, которые есть у указанного статуса на указанную категорию
     * @throws GranException при необходимости
     */
    public ArrayList<String> getCategoryRuleList(SessionContext sc, String prstatusId, String categoryId) throws GranException {
        log.trace("getCategoryRuleList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getCategoryRuleList", "sc", sc);
        if (prstatusId == null)
            throw new InvalidParameterException(this.getClass(), "getCategoryRuleList", "prstatusId", sc);
        if (categoryId == null)
            throw new InvalidParameterException(this.getClass(), "getCategoryRuleList", "categoryId", sc);
        SecuredCategoryBean category = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, categoryId);
        SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
        //if (!(sc.canAction(com.trackstudio.kernel.cache.Action.viewCategoryPermission, category.getTaskId()) && category.canView() && prstatus.canView()))
        //    throw new AccessDeniedException(this.getClass(), "getCategoryRuleList", sc);

        if (!sc.canAction(Action.manageCategories, category.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "getCategoryRuleList", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.viewCategoryPermission, category.getTaskId())", categoryId);

        if (!category.canView())
            throw new AccessDeniedException(this.getClass(), "getCategoryRuleList", sc, "!category.canView()", categoryId);

        if (!prstatus.canView())
            throw new AccessDeniedException(this.getClass(), "getCategoryRuleList", sc, "!prstatus.canView()", prstatusId);
        return (ArrayList<String>) KernelManager.getCategory().getCategoryRuleList(prstatusId, categoryId);
    }

    /**
     * Проверяется, можно ли изменять процесс для категории или нет.
     * Если есть хоть одна задача с заданной категорий, то менять процесс нельзя
     *
     * @param sc         сессия пользователя
     * @param categoryId ID  категории, для которой произовадится проверка
     * @return TRUE - можно менять процесс, FALSE - нельзя
     * @throws GranException при необходимости
     */
    public boolean canChangeWorkflow(SessionContext sc, String categoryId) throws GranException {
        log.trace("canChangeWorkflow");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "canChangeWorkflow", "sc", sc);
        if (categoryId == null)
            throw new InvalidParameterException(this.getClass(), "canChangeWorkflow", "categoryId", sc);
        SecuredCategoryBean category = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, categoryId);
        //if (!(sc.canAction(com.trackstudio.kernel.cache.Action.viewCategory, category.getTaskId()) && category.canView()))
        //    throw new AccessDeniedException(this.getClass(), "canChangeWorkflow", sc);
        if (!sc.canAction(Action.manageCategories, category.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "canChangeWorkflow", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.viewCategory, category.getTaskId())", categoryId);
        if (!category.canView())
            throw new AccessDeniedException(this.getClass(), "canChangeWorkflow", sc, "!category.canView()", categoryId);
        return KernelManager.getCategory().canChangeWorkflow(categoryId);
    }

    /**
     * Проверяеет есть ли право на редактирование категории
     *
     * @param sc         сессия пользователя
     * @param taskId     ID задачи
     * @param categoryId ID категории
     * @return TRUE - если есть, FALSE - если нет
     * @throws GranException при необходимости
     */
    public boolean isCategoryEditable(SessionContext sc, String taskId, String categoryId) throws GranException {
        log.trace("isCategoryEditable");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "isCategoryEditable", "sc", sc);
        if (categoryId == null)
            throw new InvalidParameterException(this.getClass(), "isCategoryEditable", "categoryId", sc);
        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "isCategoryEditable", sc, "!sc.taskOnSight(taskId)", taskId);
        return KernelManager.getCategory().isCategoryEditable(taskId, sc.getUserId(), categoryId, sc.getPrstatusId());
    }

    /**
     * Проверяеет есть ли право у пользователя быть ответственным для категории
     *
     * @param sc         сессия пользователя
     * @param taskId     ID задачи
     * @param categoryId ID категории
     * @param isNew      Новая ли задача создается?
     * @param submitter  ID автора
     * @return TRUE - если есть, FALSE - если нет
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Category
     */
    public boolean isCategoryCanBeHandler(SessionContext sc, String taskId, String categoryId, boolean isNew, String submitter) throws GranException {
        log.trace("isCategoryCanBeHandler");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "isCategoryEditable", "sc", sc);
        if (categoryId == null)
            throw new InvalidParameterException(this.getClass(), "isCategoryCanBeHandler", "categoryId", sc);
        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "isCategoryCanBeHandler", sc, "!sc.taskOnSight(taskId)", taskId);
        return KernelManager.getCategory().isCategoryCanBeHandler(taskId, sc.getUserId(), categoryId, sc.getPrstatusId(), isNew, submitter);
    }

    /**
     * Проверяеет есть ли право на удаление категории
     *
     * @param sc         сессия пользователя
     * @param taskId     ID задачи
     * @param categoryId ID категории
     * @return TRUE - если есть, FALSE - если нет
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Category
     */
    public boolean isCategoryDeletable(SessionContext sc, String taskId, String categoryId) throws GranException {
        log.trace("isCategoryDeletable");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "isCategoryDeletable", "sc", sc);
        if (categoryId == null)
            throw new InvalidParameterException(this.getClass(), "isCategoryDeletable", "categoryId", sc);
        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "isCategoryDeletable", sc, "!sc.taskOnSight(taskId)", taskId);
        return KernelManager.getCategory().isCategoryDeletable(taskId, sc.getUserId(), categoryId, sc.getPrstatusId());
    }

    /**
     * Проверяеет есть ли право на просмотр категории
     *
     * @param sc         сессия пользователя
     * @param taskId     ID задачи
     * @param categoryId ID категории
     * @return TRUE - если есть, FALSE - если нет
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Category
     */
    public boolean isCategoryViewable(SessionContext sc, String taskId, String categoryId) throws GranException {
        log.trace("isCategoryDeletable");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "isCategoryViewable", "sc", sc);
        if (categoryId == null)
            throw new InvalidParameterException(this.getClass(), "isCategoryViewable", "categoryId", sc);
        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "isCategoryViewable", sc, "!sc.taskOnSight(taskId)", taskId);
        return KernelManager.getCategory().isCategoryViewable(taskId, sc.getUserId(), categoryId, sc.getPrstatusId());
    }

    /**
     * Устанавливает триггеры для категории
     *
     * @param sc           сессия пользователя
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
    public void setCategoryTrigger(SessionContext sc, String categoryId, String before, String insteadOf, String after, String updBefore, String updInsteadOf, String updAfter) throws GranException {
        log.trace("setCategoryTrigger");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "setCategoryTrigger", "sc", sc);
        if (categoryId == null)
            throw new InvalidParameterException(this.getClass(), "setCategoryTrigger", "categoryId", sc);
        SecuredCategoryBean category = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, categoryId);
        //if (!(sc.canAction(Action.editCategoryTrigger, category.getTaskId()) && category.canUpdate()))
        //    throw new AccessDeniedException(this.getClass(), "setCategoryTrigger", sc);

        if (!sc.canAction(Action.manageCategories, category.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "setCategoryTrigger", sc, "!sc.canAction(Action.editCategoryTrigger, category.getTaskId())", categoryId);
        if (!category.canManage())
            throw new AccessDeniedException(this.getClass(), "setCategoryTrigger", sc, "!category.canUpdate()", categoryId);
        KernelManager.getCategory().setCategoryTrigger(categoryId, SafeString.createSafeString(before), SafeString.createSafeString(insteadOf), SafeString.createSafeString(after), SafeString.createSafeString(updBefore), SafeString.createSafeString(updInsteadOf), SafeString.createSafeString(updAfter));
    }

    /**
     * Проверяеет валидна категория или нет
     *
     * @param sc         сессия пользователя
     * @param categoryId ID категории
     * @param taskId     ID задачи
     * @return TRUE - если валидна, FALSE - если нет
     * @throws GranException при необходимости
     */
    public Boolean getCategoryIsValid(SessionContext sc, String categoryId, String taskId) throws GranException {
        log.trace("getCategoryIsValid");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getCategoryIsValid", "sc", sc);
        if (categoryId == null)
            throw new InvalidParameterException(this.getClass(), "getCategoryIsValid", "categoryId", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getCategoryIsValid", "taskId", sc);
        Set<Prstatus> prstatusSet = KernelManager.getPrstatus().getAvailablePrstatusList("1");
        return KernelManager.getCategory().getCategoryIsValid(categoryId, taskId, prstatusSet);
    }

    public void getTaskCategory(SessionContext sc, String categoryId) throws GranException {
        log.trace("getTaskCategory");
        if (sc == null)
            throw new InvalidParameterException(this.getClass().getName(), "getTaskCategory", "sc", "1");
        if (categoryId == null)
            throw new InvalidParameterException(this.getClass().getName(), "getTaskCategory", "categoryId", sc.getUserId());
        KernelManager.getTask().getTaskCategoryList(categoryId);
    }

    public List<Category> getListCreateTrigger(SessionContext sc, String taskId, String triggerId) throws GranException {
        log.trace("isCategoryDeletable");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "isCategoryViewable", "sc", sc);
        if (triggerId == null)
            throw new InvalidParameterException(this.getClass(), "isCategoryViewable", "categoryId", sc);
        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "isCategoryViewable", sc, "!sc.taskOnSight(taskId)", taskId);
        return KernelManager.getCategory().getCategoryCreate(taskId, triggerId);
    }

    public List<Category> getListUpdateTrigger(SessionContext sc, String taskId, String triggerId) throws GranException {
        log.trace("isCategoryDeletable");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "isCategoryViewable", "sc", sc);
        if (triggerId == null)
            throw new InvalidParameterException(this.getClass(), "isCategoryViewable", "categoryId", sc);
        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "isCategoryViewable", sc, "!sc.taskOnSight(taskId)", taskId);
        return KernelManager.getCategory().getCategoryCreate(taskId, triggerId);
    }
}
