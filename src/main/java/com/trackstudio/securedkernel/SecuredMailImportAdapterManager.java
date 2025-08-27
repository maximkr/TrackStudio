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
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredMailImportBean;
import com.trackstudio.tools.ParameterValidator;
import com.trackstudio.tools.SecuredBeanUtil;

import net.jcip.annotations.Immutable;

/**
 * Класс SecuredMailImportAdapterManager предназначен для работы с правилами импорта почтовых сообщений
 */
@Immutable
public class SecuredMailImportAdapterManager {

    private static final Log log = LogFactory.getLog(SecuredMailImportAdapterManager.class);
    private static final ParameterValidator pv = new ParameterValidator();

    /**
     * Удаляет правило импорта
     *
     * @param sc           сессия пользователя
     * @param mailImportId ID правила, которое удаляем
     * @throws GranException при необходимости
     */
    public void deleteMailImport(SessionContext sc, String mailImportId) throws GranException {
        log.trace("deleteMailImport");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "deleteMailImport", "sc", sc);
        if (mailImportId == null)
            throw new InvalidParameterException(this.getClass(), "deleteMailImport", "taskId", sc);
        SecuredMailImportBean smi = AdapterManager.getInstance().getSecuredFindAdapterManager().findMailImportById(sc, mailImportId);
        //if (!(sc.canAction(Action.deleteImportTaskRule, smi.getTaskId()) && sc.allowedByACL(smi.getTaskId())))
        //    throw new AccessDeniedException(this.getClass(), "deleteMailImport", sc);
        if (!sc.canAction(Action.manageEmailImportRules, smi.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "deleteMailImport", sc, "!sc.canAction(Action.deleteImportTaskRule, smi.getTaskId())", mailImportId);
        if (!sc.allowedByACL(smi.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "deleteMailImport", sc, "!sc.allowedByACL(smi.getTaskId())", mailImportId);
        KernelManager.getMailImport().deleteMailImport(mailImportId);
    }

    /**
     * Редактирование правила импорта
     *
     * @param sc            сессия пользователя
     * @param mailImportId  ID правила, которое редактируем
     * @param name          Название правила
     * @param keywords      Ключевые слова
     * @param searchIn      Где искать ключавые слова
     * @param order         Порядок
     * @param categoryId    ID категории, которая используется для создания задачи
     * @param mStatus       ID типа сообщений с использованием которого создаются испортируемые сообщения
     * @param domain        Домен, с которого принимается почта
     * @param active        Активно правило или нет
     * @param importUnknown Испортировать ли почту с неизвесных адресов
     * @throws GranException при необходимости
     */
    public void updateMailImport(SessionContext sc, String mailImportId, String name, String keywords, int searchIn, int order, String categoryId, String mStatus, String domain, boolean active, boolean importUnknown) throws GranException {
        log.trace("updateMailImport");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "updateMailImport", "sc", sc);
        if (mailImportId == null)
            throw new InvalidParameterException(this.getClass(), "updateMailImport", "mailImportId", sc);
        if (keywords == null || pv.badSmallDesc(keywords))
            throw new InvalidParameterException(this.getClass(), "updateMailImport", "keywords", sc);
        if (categoryId == null)
            throw new InvalidParameterException(this.getClass(), "updateMailImport", "categoryId", sc);
        SecuredMailImportBean mailImport = AdapterManager.getInstance().getSecuredFindAdapterManager().findMailImportById(sc, mailImportId);
        SecuredCategoryBean category = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, categoryId);
        if (!sc.canAction(Action.manageEmailImportRules, mailImport.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "updateMailImport", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.editImportTaskRule, mailImport.getTaskId())", mailImportId);
        if (!mailImport.canManage())
            throw new AccessDeniedException(this.getClass(), "updateMailImport", sc, "!mailImport.canUpdate()", mailImportId);
        if (!category.canView())
            throw new AccessDeniedException(this.getClass(), "updateMailImport", sc, "!category.canView()", categoryId);
        KernelManager.getMailImport().updateMailImport(mailImportId, SafeString.createSafeString(name), SafeString.createSafeString(keywords), searchIn, order, categoryId, sc.getUserId(), mStatus, SafeString.createSafeString(domain), active, importUnknown);
    }

    /**
     * Редактирование правила импорта
     *
     * @param sc            Сессия пользователя
     * @param name          Название правила
     * @param taskId        ID задачи, которая будет родительской для импортируемых
     * @param keywords      Ключевые слова
     * @param searchIn      Где искать ключавые слова
     * @param order         Порядок
     * @param categoryId    ID категории, которая используется для создания задачи
     * @param mStatus       ID типа сообщений с использованием которого создаются испортируемые сообщения
     * @param domain        Домен, с которого принимается почта
     * @param active        Активно правило или нет
     * @param importUnknown Испортировать ли почту с неизвесных адресов
     * @return ID созданного правила
     * @throws GranException при необходимости
     */
    public String createMailImport(SessionContext sc, String name, String taskId, String keywords, int searchIn, int order, String categoryId, String mStatus, String domain, boolean active, boolean importUnknown) throws GranException {
        log.trace("createMailImport");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "createMailImport", "sc", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "createMailImport", "taskId", sc);
        if (keywords == null || pv.badSmallDesc(keywords))
            throw new InvalidParameterException(this.getClass(), "createMailImport", "keywords", sc);
        if (categoryId == null)
            throw new InvalidParameterException(this.getClass(), "createMailImport", "categoryId", sc);
        SecuredCategoryBean category = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, categoryId);
        if (!sc.canAction(Action.manageEmailImportRules, taskId))
            throw new AccessDeniedException(this.getClass(), "createMailImport", sc, "!sc.canAction(Action.createImportTaskRule, taskId)", taskId);
        if (!sc.allowedByACL(taskId))
            throw new AccessDeniedException(this.getClass(), "createMailImport", sc, "!sc.allowedByACL(taskId)", taskId);
        if (!category.canView())
            throw new AccessDeniedException(this.getClass(), "createMailImport", sc, "!category.canView()", categoryId);
        return KernelManager.getMailImport().createMailImport(SafeString.createSafeString(name), taskId, SafeString.createSafeString(keywords), searchIn, order, categoryId, mStatus, sc.getUserId(), SafeString.createSafeString(domain), active, importUnknown);
    }

    /**
     * Возвращает список доступных правил импорта почтовых сообщений для задачи
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи, для которой получаем правила имопрта
     * @return список правил импорта
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredMailImportBean
     */
    public List<SecuredMailImportBean> getAllAvailableMailImportList(SessionContext sc, String taskId) throws GranException {
        log.trace("getMailImportList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getMailImportList", "sc", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getMailImportList", "taskId", sc);
        if (!sc.allowedByACL(taskId))
            throw new AccessDeniedException(this.getClass(), "getMailImportList", sc, "!sc.allowedByACL(taskId)", taskId);
        return SecuredBeanUtil.toArrayList(sc, KernelManager.getMailImport().getAllAvailableMailImportList(sc), SecuredBeanUtil.MAILIMPORT);
    }

    /**
     * Возвращает все правила импорта почтовых сообщений для всех задач.
     *
     * @param sc сессия пользователя
     * @return список всех правил импорта
     * @throws GranException при необходимости
     * @see com.trackstudio.model.MailImport
     */
    public List<SecuredMailImportBean> getAllMailImportList(SessionContext sc) throws GranException {
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getAllMailImportList", "sc", sc);

        List<SecuredMailImportBean> retList = new ArrayList<SecuredMailImportBean>();
        List<SecuredMailImportBean> subList = SecuredBeanUtil.toArrayList(sc, KernelManager.getMailImport().getAllMailImports(), SecuredBeanUtil.MAILIMPORT);
        for (SecuredMailImportBean ssb : subList) {
            if (ssb.canView())
                retList.add(ssb);
        }
        return retList;
    }
}
