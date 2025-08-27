package com.trackstudio.kernel.cache;

import com.trackstudio.constants.TaskActions;
import com.trackstudio.constants.UserActions;

import net.jcip.annotations.Immutable;

/**
 * Класс Action описывет действия в системе, которые может делать пользователя
 */
@Immutable
public class Action implements Comparable {

    /**
     * Название действия
     */
    private final String myName; // for debug only

    /**
     * конструктор, устанавливает название действия
     *
     * @param name название действия
     */
    public Action(String name) {
        myName = name;
    }

    /**
     * Возвращает строковое представление текущего класса
     *
     * @return строковое представление текущего класса
     */
    @Override
    public String toString() {
        return myName;
    }

    /**
     * Возвращает Название действия
     *
     * @return название действия
     */
    public String getName() {
        return myName;
    }

    /**
     * Сравнивает два объекта текущего класса
     *
     * @param obj Скравниваемый обхект
     * @return TREU если равны, FALSE если нет
     */
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        return obj.toString().equals(toString());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.myName != null ? this.myName.hashCode() : 0);
        return hash;
    }

    /**
     * Сравнивает два объекта текущего класса
     *
     * @param o Сравниваемый обхект
     * @return +1, 0 или -1
     */
    public int compareTo(Object o) {
        return toString().compareTo(o.toString());
    }

    /**
     * Право на создание пользователей
     */
    public static final UserAction createUser = new UserAction(UserActions.createUser);
    /**
     * Право на удаление пользователей
     */
    public static final UserAction deleteUser = new UserAction(UserActions.deleteUser);
    /**
     * Право редактировать статусы пользователей
     */
    public static final UserAction editUserStatus = new UserAction(UserActions.editUserStatus);
    /**
     * Право просматривать телефоны пользователей
     */
    public static final UserAction viewUserPhone = new UserAction(UserActions.viewUserPhone);
    /**
     * Право редактировать телефоны пользователей
     */
    public static final UserAction editUserPhone = new UserAction(UserActions.editUserPhone);
    /**
     * Право редактировать электронную почту пользователей
     */
    public static final UserAction editUserEmail = new UserAction(UserActions.editUserEmail);
    /**
     * Право на просмотр компании пользователя
     */
    public static final UserAction viewUserCompany = new UserAction(UserActions.viewUserCompany);
    /**
     * Право редактировать компанию пользователя
     */
    public static final UserAction editUserCompany = new UserAction(UserActions.editUserCompany);
    /**
     * Право редактировать локаль пользователя
     */
    public static final UserAction editUserLocale = new UserAction(UserActions.editUserLocale);
    /**
     * Право редактировать таймзону пользователя
     */
    public static final UserAction editUserTimezone = new UserAction(UserActions.editUserTimezone);
    /**
     * Право редактировать дату истекания срока действия логина пользователя
     */
    public static final UserAction editUserExpireDate = new UserAction(UserActions.editUserExpireDate);
    /**
     * Право редактирования колличетсва лицензированных пользователей
     */
    public static final UserAction editUserLicensed = new UserAction(UserActions.editUserLicensed);
    /**
     * Право редактировать проект пользователя по умолчанию
     */
    public static final UserAction editUserDefaultProject = new UserAction(UserActions.editUserDefaultProject);
    /**
     * Право активировать/деактивировать пользователей
     */
    public static final UserAction editUserActive = new UserAction(UserActions.editUserActive);
    /**
     * Право управление правами доступа к пользователям
     */
    public static final UserAction manageUserACLs = new UserAction(UserActions.manageUserACLs);
    /**
     * Право изменять свои данных
     */
    public static final UserAction editUserHimself = new UserAction(UserActions.editUserHimself);
    /**
     * Право изменять данные подчиненных
     */
    public static final UserAction editUserChildren = new UserAction(UserActions.editUserChildren);
    /**
     * Право вырезать и вставлять пользователей
     */
    public static final UserAction cutPasteUser = new UserAction(UserActions.cutPasteUser);
    /**
     * Право менять свой пароль
     */
    public static final UserAction editUserPasswordHimself = new UserAction(UserActions.editUserPasswordHimself);
    /**
     * Право редактировать тип шаблона email
     */
    public static final UserAction editUserEmailType = new UserAction(UserActions.editUserEmailType);
    /**
     * Право менять пароли подчиненных пользователей
     */
    public static final UserAction editUserChildrenPassword = new UserAction(UserActions.editUserChildrenPassword);
    /**
     * Право управлять пользовательскими полями для пользователей
     */
    public static final UserAction manageUserUDFs = new UserAction(UserActions.manageUserUDFs);
    /**
     * Право управлять роляит пользователей
     */
    public static final UserAction manageRoles = new UserAction(UserActions.manageRoles);
    /**
     * Право на просмотр прилоежнных к пользователям файлов
     */
    public static final UserAction viewUserAttachments = new UserAction(UserActions.viewUserAttachments);
    /**
     * Право создавать приложенные к пользователям файлы
     */
    public static final UserAction createUserAttachments = new UserAction(UserActions.createUserAttachments);
    /**
     * Право редактировать и удалять приложенные к пользователям файлы
     */
    public static final UserAction manageUserAttachments = new UserAction(UserActions.manageUserAttachments);
    /**
     * Право просмотривать фильтры пользователей
     */
    public static final UserAction viewUserFilters = new UserAction(UserActions.viewUserFilters);
    /**
     * Право управлять приватными фильтрами пользователей
     */
    public static final UserAction manageUserPrivateFilters = new UserAction(UserActions.manageUserPrivateFilters);
    /**
     * Право управлять публичными фильтрами пользователей
     */
    public static final UserAction manageUserPublicFilters = new UserAction(UserActions.manageUserPublicFilters);
    /**
     * Право управлять регистрациями пользователей
     */
    public static final TaskAction manageRegistrations = new TaskAction(TaskActions.manageRegistrations);
    /**
     * Право просмотривать фильтры для задач
     */
    public static final TaskAction viewFilters = new TaskAction(TaskActions.viewFilters);
    /**
     * Право управлять приватными фильтрами для задач
     */
    public static final TaskAction manageTaskPrivateFilters = new TaskAction(TaskActions.manageTaskPrivateFilters);
    /**
     * Право управлять публичными фильтрами для задач
     */
    public static final TaskAction manageTaskPublicFilters = new TaskAction(TaskActions.manageTaskPublicFilters);
    /**
     * Право управлять почтовыми уведомлениями
     */
    public static final TaskAction manageEmailSchedules = new TaskAction(TaskActions.manageEmailSchedules);
    /**
     * Право на групповые операции с задачами
     */
    public static final TaskAction bulkProcessingTask = new TaskAction(TaskActions.bulkProcessingTask);
    /**
     * Право копировать/вырезать и вставлять азадчи
     */
    public static final TaskAction cutCopyPasteTask = new TaskAction(TaskActions.cutCopyPasteTask);
    /**
     * Право просматривать приоритеты задач
     */
    public static final TaskAction viewTaskPriority = new TaskAction(TaskActions.viewTaskPriority);
    /**
     * Право редактировать приоритеты задач
     */
    public static final TaskAction editTaskPriority = new TaskAction(TaskActions.editTaskPriority);
    /**
     * Право редактировать алиасы задач
     */
    public static final TaskAction editTaskAlias = new TaskAction(TaskActions.editTaskAlias);
    /**
     * Право просматривать дедлайн задач
     */
    public static final TaskAction viewTaskDeadline = new TaskAction(TaskActions.viewTaskDeadline);
    /**
     * Право редактировать дедлайн задач
     */
    public static final TaskAction editTaskDeadline = new TaskAction(TaskActions.editTaskDeadline);
    /**
     * Право просматривать бюджет задач
     */
    public static final TaskAction viewTaskBudget = new TaskAction(TaskActions.viewTaskBudget);
    /**
     * Право редактировать бюджет задач
     */
    public static final TaskAction editTaskBudget = new TaskAction(TaskActions.editTaskBudget);
    /**
     * Право просматривать потраченное время заадч
     */
    public static final TaskAction viewTaskActualBudget = new TaskAction(TaskActions.viewTaskActualBudget);
    /**
     * Право редактировать потраченное время задач
     */
    public static final TaskAction editTaskActualBudget = new TaskAction(TaskActions.editTaskActualBudget);
    /**
     * Право просматривать дату/время создания задач
     */
    public static final TaskAction viewTaskSubmitDate = new TaskAction(TaskActions.viewTaskSubmitDate);
    /**
     * Право просматривать дату/время последнего изменения задач
     */
    public static final TaskAction viewTaskLastUpdated = new TaskAction(TaskActions.viewTaskLastUpdated);
    /**
     * Право менять ответственного в задачах
     */
    public static final TaskAction editTaskHandler = new TaskAction(TaskActions.editTaskHandler);
    /**
     * Право смотреть даты/время закрытия задач
     */
    public static final TaskAction viewTaskCloseDate = new TaskAction(TaskActions.viewTaskCloseDate);
    /**
     * Право просматривать резолюции задач
     */
    public static final TaskAction viewTaskResolution = new TaskAction(TaskActions.viewTaskResolution);
    /**
     * Право смотреть описания задач
     */
    public static final TaskAction viewTaskDescription = new TaskAction(TaskActions.viewTaskDescription);
    /**
     * Право редактировать описание задач
     */
    public static final TaskAction editTaskDescription = new TaskAction(TaskActions.editTaskDescription);
    /**
     * Правл просматривать прилоежнные к задачам файлы
     */
    public static final TaskAction viewTaskAttachments = new TaskAction(TaskActions.viewTaskAttachments);
    /**
     * Право создавать прилоежнные к задачам файлы
     */
    public static final TaskAction createTaskAttachments = new TaskAction(TaskActions.createTaskAttachments);
    /**
     * Право управлять приложенными к задачам файлами
     */
    public static final TaskAction manageTaskAttachments = new TaskAction(TaskActions.manageTaskAttachments);
    /**
     * Право удалять свои файлы приложенные к задачам
     */
    public static final TaskAction deleteTheirTaskAttachment = new TaskAction(TaskActions.deleteTheirTaskAttachment);
    /**
     * Право создавать приложенные к сообщениям файлы
     */
    public static final TaskAction createTaskMessageAttachments = new TaskAction(TaskActions.createTaskMessageAttachments);
    /**
     * Право управлять приложенными к сообщениям файлами
     */
    public static final TaskAction manageTaskMessageAttachments = new TaskAction(TaskActions.manageTaskMessageAttachments);

    /**
     * Право удалять свои файлы проложенные к сообщениям
     */
    public static final TaskAction deleteTheirMessageAttachment = new TaskAction(TaskActions.deleteTheirMessageAttachment);

    /**
     * Право смотреть где используются скрипты
     */
    public static final TaskAction viewScriptsBrowser = new TaskAction(TaskActions.viewScriptsBrowser);

    public static final TaskAction viewTemplatesBrowser = new TaskAction(TaskActions.viewTemplatesBrowser);

    public static final TaskAction showView = new TaskAction(TaskActions.showView);
    public static final TaskAction showOtherFilterTab = new TaskAction(TaskActions.showOtherFilterTab);
    public static final TaskAction canCreateTaskByOperation = new TaskAction(TaskActions.canCreateTaskByOperation);
    public static final TaskAction canUsePostFiltration = new TaskAction(TaskActions.canUsePostFiltration);
    public static final TaskAction canArchive = new TaskAction(TaskActions.canArchive);
    public static final TaskAction canDeleteArchive = new TaskAction(TaskActions.canDeleteArchive);

    /**
     * Право управлять пользовательскими полями для задач
     */
    public static final TaskAction manageTaskUDFs = new TaskAction(TaskActions.manageTaskUDFs);
    /**
     * Право управлять правилами импорта почтовых сообщений
     */
    public static final TaskAction manageEmailImportRules = new TaskAction(TaskActions.manageEmailImportRules);
    /**
     * право управлять шаблонами для задач
     */
    public static final TaskAction manageTaskTemplates = new TaskAction(TaskActions.manageTaskTemplates);
    /**
     * Право управлять правами доступа к задачам
     */
    public static final TaskAction manageTaskACLs = new TaskAction(TaskActions.manageTaskACLs);
    /**
     * Право просматривать отчеты
     */
    public static final TaskAction viewReports = new TaskAction(TaskActions.viewReports);
    /**
     * Право управлять приватными отчетами
     */
    public static final TaskAction managePrivateReports = new TaskAction(TaskActions.managePrivateReports);
    /**
     * Право управлять публичными отчетами
     */
    public static final TaskAction managePublicReports = new TaskAction(TaskActions.managePublicReports);
    /**
     * Право управлять категориями
     */
    public static final TaskAction manageCategories = new TaskAction(TaskActions.manageCategories);
    /**
     * Право управлть процессами
     */
    public static final TaskAction manageWorkflows = new TaskAction(TaskActions.manageWorkflows);
    /**
     * Право на удаление сообщений к задачам
     */
    public static final TaskAction deleteOperations = new TaskAction(TaskActions.deleteOperations);
    /**
     * Права пользователя для задач, которые не переопределяются
     */
    public static final TaskAction[] taskUnOverridedActions = new TaskAction[]{
            viewFilters,
            manageTaskPrivateFilters,
            manageTaskPublicFilters,

            manageEmailSchedules,

            viewReports,
            managePrivateReports,
            managePublicReports
    };

    /**
     * Права пользователя для пользователей, которые не переопределяются
     */
    public static final UserAction[] userUnOverridedActions = new UserAction[]{
            viewUserFilters,
            manageUserPrivateFilters,
            manageUserPublicFilters
    };

    /**
     * Прав доступа пользователя к задачам
     */
    public static final TaskAction[] taskActions = new TaskAction[]{
            viewFilters,
            manageTaskPrivateFilters,
            manageTaskPublicFilters,

            manageEmailSchedules,

            bulkProcessingTask,
            cutCopyPasteTask,
            viewTaskPriority,
            editTaskPriority,
            editTaskAlias,
            viewTaskDeadline,
            editTaskDeadline,
            viewTaskBudget,
            editTaskBudget,
            viewTaskActualBudget,
            editTaskActualBudget,
            viewTaskSubmitDate,
            viewTaskLastUpdated,
            editTaskHandler,
            viewTaskCloseDate,
            viewTaskResolution,
            viewTaskDescription,
            editTaskDescription,
            viewTaskAttachments,
            createTaskAttachments,
            manageTaskAttachments,
            createTaskMessageAttachments,
            manageTaskMessageAttachments,
            manageTaskUDFs,
            manageEmailImportRules,
            manageTaskTemplates,
            manageTaskACLs,
            viewReports,
            managePrivateReports,
            managePublicReports,
            manageCategories,
            manageWorkflows,
            deleteOperations,
            manageRegistrations,
            deleteTheirTaskAttachment,
            deleteTheirMessageAttachment,
            viewScriptsBrowser,
            viewTemplatesBrowser,
            showView
    };
}

