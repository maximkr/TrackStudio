package com.trackstudio.securedkernel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.AccessDeniedException;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.InvalidParameterException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.model.Template;
import com.trackstudio.secured.SecuredTemplateBean;
import com.trackstudio.tools.ParameterValidator;
import com.trackstudio.tools.SecuredBeanUtil;

import net.jcip.annotations.Immutable;

/**
 * Класс SecuredTemplateAdapterManager содержит методы для работы c шаблонами
 */
@Immutable
public class SecuredTemplateAdapterManager {

    private static final Log log = LogFactory.getLog(SecuredTemplateAdapterManager.class);
    private static final ParameterValidator pv = new ParameterValidator();

    /**
     * Удаляет шаблон
     *
     * @param sc         сессия пользователя
     * @param templateId ID удаляемого шаблона
     * @throws GranException при необходимости
     */
    public void deleteTemplate(SessionContext sc, String templateId) throws GranException {
        log.trace("deleteTemplate");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "deleteTemplate", "sc", sc);
        if (templateId == null)
            throw new InvalidParameterException(this.getClass(), "deleteTemplate", "taskId", sc);
        SecuredTemplateBean smi = AdapterManager.getInstance().getSecuredFindAdapterManager().findTemplateById(sc, templateId);
        if (!sc.canAction(Action.manageTaskTemplates, smi.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "deleteTemplate", sc, "!sc.canAction(Action.deleteTaskTemplate, smi.getTaskId())", templateId);
        if (!smi.canUpdate())
            throw new AccessDeniedException(this.getClass(), "deleteTemplate", sc, "!canUpdate", templateId);
        KernelManager.getTemplate().deleteTemplate(templateId);
    }

    /**
     * Редактируется шаблон
     *
     * @param sc          сессия пользователя
     * @param templateId  ID шаблона
     * @param name        Название шаблона
     * @param description Описание шаблона
     * @param userId      ID пользователя
     * @param folder      Каталог шаблона
     * @param active      Активен шаблон или нет
     * @throws GranException при необходимости
     */
    public void updateTemplate(SessionContext sc, String templateId, String name, String description, String userId, String folder, Integer active) throws GranException {
        log.trace("updateTemplate");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "updateTemplate", "sc", sc);
        if (templateId == null)
            throw new InvalidParameterException(this.getClass(), "updateTemplate", "templateId", sc);
        if (name == null || pv.badSmallDesc(name))
            throw new InvalidParameterException(this.getClass(), "updateTemplate", "name", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "updateTemplate", "userId", sc);
        SecuredTemplateBean template = AdapterManager.getInstance().getSecuredFindAdapterManager().findTemplateById(sc, templateId);
        if (!sc.canAction(Action.manageTaskTemplates, template.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "updateTemplate", sc, "!sc.canAction(Action.editTaskTemplate, template.getTaskId())", templateId);
        if (!template.canManage())
            throw new AccessDeniedException(this.getClass(), "updateTemplate", sc, "!template.canUpdate()", templateId);
        KernelManager.getTemplate().updateTemplate(templateId, SafeString.createSafeString(name), SafeString.createSafeString(description), userId, SafeString.createSafeString(folder), active);
    }

    /**
     * Создает шаблон
     *
     * @param sc          сессия пользователя
     * @param name        Название шаблона
     * @param taskId      ID адачи, к которой приявязывается шаблон
     * @param userId      ID пользователя, который создает шаблон
     * @param description описание шаблона
     * @param code        Каталог шаблона
     * @param active      Активный шаблон или нет
     * @return ID созданного шаблона
     * @throws GranException пи еобходимости
     */
    public String createTemplate(SessionContext sc, String name, String description, String taskId, String userId, String code, Integer active) throws GranException {
        log.trace("createTemplate");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "createTemplate", "sc", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "createTemplate", "taskId", sc);

        if (name == null || name.length() == 0)
            throw new InvalidParameterException(this.getClass(), "createTemplate", "name", sc);

        if (!sc.canAction(Action.manageTaskTemplates, taskId))
            throw new AccessDeniedException(this.getClass(), "createTemplate", sc, "!sc.canAction(Action.createTaskTemplate, taskId)", taskId);
        if (!sc.allowedByACL(taskId))
            throw new AccessDeniedException(this.getClass(), "createTemplate", sc, "!sc.allowedByACL(taskId)", taskId);

        String id = KernelManager.getTemplate().createTemplate(SafeString.createSafeString(name), SafeString.createSafeString(description), sc.getUserId(), userId, taskId);
        KernelManager.getTemplate().updateTemplate(id, SafeString.createSafeString(name), SafeString.createSafeString(description), userId, SafeString.createSafeString(code), active);
        return id;
    }

    /**
     * Возвращает список шаблонов
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи, для которой возвращается вписок шаблонов
     * @return список шаблонов
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredTemplateBean
     */
    public List<SecuredTemplateBean> getTemplateList(SessionContext sc, String taskId) throws GranException {
        log.trace("getTemplateList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getTemplateList", "sc", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getTemplateList", "taskId", sc);
        if (!sc.allowedByACL(taskId))
            throw new AccessDeniedException(this.getClass(), "getTemplateList", sc, "!sc.allowedByACL(taskId)", taskId);
        return SecuredBeanUtil.toList(sc, KernelManager.getTemplate().getTemplateList(taskId), SecuredBeanUtil.TEMPLATE);
    }

    /**
     * Возвращает список всех шаблонов
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи
     * @return список шиблогов
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Template
     */
    public List<SecuredTemplateBean> getAllTemplatesList(SessionContext sc, String taskId) throws GranException {
        log.trace("getAllTemplatesList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getAllTemplatesList", "sc", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getAllTemplatesList", "taskId", sc);
        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "getAllTemplatesList", sc, "!sc.taskOnSight(taskId)", taskId);
        return new ArrayList<SecuredTemplateBean>(KernelManager.getTemplate().getTemplatesList(sc, taskId));
    }

     public List<Template> getTemplateOwnerList(SessionContext sc, String userId) throws GranException {
        log.trace("getTemplateOwnerList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getTemplateOwnerList", "sc", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getTemplateOwnerList", "userId", sc);

        return KernelManager.getTemplate().getTemplateOwnerList(userId);
    }
}