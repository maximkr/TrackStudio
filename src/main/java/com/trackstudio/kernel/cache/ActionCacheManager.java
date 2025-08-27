package com.trackstudio.kernel.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.exception.GranException;
import com.trackstudio.model.Rolestatus;
import com.trackstudio.tools.EggBasket;

import net.jcip.annotations.Immutable;

/**
 * Класс для работы с кеширование действий пользователей
 */
@Immutable
public class ActionCacheManager extends CacheManager {

    private static final Log log = LogFactory.getLog(ActionCacheManager.class);
    private final static ActionCacheManager instance = new ActionCacheManager();

    // эталоны
    /**
     * Список действий для задач
     */
    private final List<TaskAction> taskSecurity = Collections.unmodifiableList(Arrays.asList(
            Action.manageRegistrations,
            Action.cutCopyPasteTask,
            Action.bulkProcessingTask,
            Action.viewTaskAttachments,
            Action.manageTaskAttachments,
            Action.createTaskAttachments,
            Action.manageTaskMessageAttachments,
            Action.createTaskMessageAttachments,

            Action.viewFilters,
            Action.manageTaskPrivateFilters,
            Action.manageTaskPublicFilters,

            Action.viewReports,
            Action.managePrivateReports,
            Action.managePublicReports,

            Action.manageEmailSchedules,

            Action.manageTaskACLs,
            Action.manageTaskUDFs,
            Action.manageEmailImportRules,
            Action.manageTaskTemplates,
            Action.manageCategories,
            Action.manageWorkflows,
            Action.deleteOperations,
            Action.deleteTheirTaskAttachment,
            Action.deleteTheirMessageAttachment,
            Action.viewScriptsBrowser,
            Action.viewTemplatesBrowser,
            Action.showView,
            Action.canCreateTaskByOperation,
            Action.showOtherFilterTab,
            Action.canUsePostFiltration,
            Action.canArchive,
            Action.canDeleteArchive));

    /**
     * Список действий для пользователей
     */
    private final List<UserAction> userSecurity = Collections.unmodifiableList(Arrays.asList(
            Action.editUserHimself,
            Action.editUserChildren,
            Action.createUser,
            Action.deleteUser,
            Action.cutPasteUser,
            Action.editUserPasswordHimself,
            Action.editUserChildrenPassword,
            Action.viewUserFilters,
            Action.manageUserPrivateFilters,
            Action.manageUserPublicFilters,
            Action.manageUserACLs,
            Action.manageUserUDFs,
            Action.manageRoles,
            Action.viewUserAttachments,
            Action.createUserAttachments,
            Action.manageUserAttachments));

    /**
     * Список действий для полей пользователей
     */
    private final List<UserAction> userFieldSecurity = Collections.unmodifiableList(Arrays.asList(
            Action.viewUserCompany,
            Action.editUserCompany,
            Action.editUserStatus,
            Action.editUserEmail,
            Action.viewUserPhone,
            Action.editUserPhone,
            Action.editUserLocale,
            Action.editUserTimezone,
            Action.editUserExpireDate,
            Action.editUserLicensed,
            Action.editUserDefaultProject,
            Action.editUserEmailType,
            Action.editUserActive));

    /**
     * Список действий для полей задач
     */
    private final List<TaskAction> taskFieldSecurity = Collections.unmodifiableList(Arrays.asList(
            Action.editTaskAlias,
            Action.viewTaskResolution,
            Action.viewTaskPriority,
            Action.editTaskPriority,
            Action.editTaskHandler,
            Action.viewTaskSubmitDate,
            Action.viewTaskLastUpdated,
            Action.viewTaskCloseDate,
            Action.viewTaskDeadline,
            Action.editTaskDeadline,
            Action.viewTaskBudget,
            Action.editTaskBudget,
            Action.viewTaskActualBudget,
            Action.editTaskActualBudget,
            Action.viewTaskDescription,
            Action.editTaskDescription));

    /**
     * Список всех действий
     */
    private final ConcurrentSkipListSet<Action> allActions;

    {
        allActions = new ConcurrentSkipListSet<Action>();
        allActions.addAll(taskSecurity);
        allActions.addAll(userSecurity);
        allActions.addAll(taskFieldSecurity);
        allActions.addAll(userFieldSecurity);
    }

    /**
     * Кеш действий
     */
    private final EggBasket<String, Action> storedActions = new EggBasket<String, Action>();

    /**
     * Инициализирут кеш действий и возвращает его
     *
     * @return кеш действий
     * @throws GranException при необходимости
     * @see com.trackstudio.tools.EggBasket
     * @see com.trackstudio.kernel.cache.Action
     */
    private EggBasket<String, Action> getStoredActions() throws GranException {
        return storedActions;
    }

    /**
     * Возвращает действия для указанного статуса
     *
     * @param prstatus ID статуса
     * @return список действий
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.Action
     */
    public List<Action> getActions(String prstatus) throws GranException {
        List<Action> list = getStoredActions().get(prstatus);
        return list == null ? new ArrayList<Action>() : list;
    }


    /**
     * Возвращает список действий для задач
     *
     * @return список действий
     * @see com.trackstudio.kernel.cache.Action
     */
    public List<TaskAction> getTaskSecurity() {
        return taskSecurity;
    }

    /**
     * Возвращает список действий для пользователей
     *
     * @return список действий
     * @see com.trackstudio.kernel.cache.Action
     */
    public List<UserAction> getUserSecurity() {
        return userSecurity;
    }

    /**
     * Возвращает список действий для полей пользователей
     *
     * @return список действий
     * @see com.trackstudio.kernel.cache.Action
     */
    public List<UserAction> getUserFieldSecurity() {
        return userFieldSecurity;
    }

    /**
     * Возвращает список действий для полей задач
     *
     * @return список действий
     * @see com.trackstudio.kernel.cache.Action
     */
    public List<TaskAction> getTaskFieldSecurity() {
        return taskFieldSecurity;
    }

    /**
     * Кнструктор по умолчанию. Производится инициализация кеша
     *
     * @throws GranException при необходимости
     */
    private ActionCacheManager() {
        initCache();
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return экземпляр класса ActionCacheManager
     * @throws GranException при необходимости
     */
    public static ActionCacheManager getInstance() throws GranException {
        return instance;
    }

    /**
     * Инициализируется кеш
     */
    //lock: don't need
    private void initCache() {
        try {
            List<Rolestatus> list = hu.getList("from com.trackstudio.model.Rolestatus r");
            for (Rolestatus role : list) {
                final Action action = new Action(role.getRole());
                if (allActions.contains(action)) {
                    storedActions.putItem(role.getPrstatus().getId(), action);
                }
            }

        } catch (Exception e) {
            log.error("Exception ", e);
        }
    }

    /**
     * Очищает кеш ля статуса пользователя
     *
     * @param prstatusId ID статуса
     * @throws GranException при необходимости
     */
    public synchronized void invalidateForPrstatus(String prstatusId) throws GranException {
        storedActions.remove(prstatusId);
        hu.cleanSession();
        List<Rolestatus> list = hu.getList("from com.trackstudio.model.Rolestatus r where r.prstatus=?", prstatusId);
        for (Rolestatus role : list) {
            final Action action = new Action(role.getRole());
            if (allActions.contains(action)) {
                storedActions.putItem(role.getPrstatus().getId(), action);
            }
        }
    }
}