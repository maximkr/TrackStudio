package com.trackstudio.securedkernel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.CantFindObjectException;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.InvalidParameterException;
import com.trackstudio.kernel.cache.AttachmentCacheItem;
import com.trackstudio.kernel.cache.MessageCacheItem;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UDFCacheItem;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.AttachmentManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Acl;
import com.trackstudio.model.Attachment;
import com.trackstudio.model.Bookmark;
import com.trackstudio.model.Category;
import com.trackstudio.model.Filter;
import com.trackstudio.model.MailImport;
import com.trackstudio.model.Mstatus;
import com.trackstudio.model.Notification;
import com.trackstudio.model.Priority;
import com.trackstudio.model.Prstatus;
import com.trackstudio.model.Registration;
import com.trackstudio.model.Report;
import com.trackstudio.model.Resolution;
import com.trackstudio.model.Status;
import com.trackstudio.model.Subscription;
import com.trackstudio.model.Template;
import com.trackstudio.model.Transition;
import com.trackstudio.model.Udflist;
import com.trackstudio.model.Workflow;
import com.trackstudio.secured.SecuredAttachmentBean;
import com.trackstudio.secured.SecuredBookmarkBean;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredMailImportBean;
import com.trackstudio.secured.SecuredMessageAttachmentBean;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredNotificationBean;
import com.trackstudio.secured.SecuredPriorityBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredRegistrationBean;
import com.trackstudio.secured.SecuredReportBean;
import com.trackstudio.secured.SecuredResolutionBean;
import com.trackstudio.secured.SecuredStatusBean;
import com.trackstudio.secured.SecuredSubscriptionBean;
import com.trackstudio.secured.SecuredTaskAclBean;
import com.trackstudio.secured.SecuredTaskAttachmentBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTaskUDFBean;
import com.trackstudio.secured.SecuredTemplateBean;
import com.trackstudio.secured.SecuredTransitionBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUdflistBean;
import com.trackstudio.secured.SecuredUserAclBean;
import com.trackstudio.secured.SecuredUserAttachmentBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.secured.SecuredUserUDFBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.secured.SecuredWorkflowUDFBean;
import com.trackstudio.tools.Pair;

import net.jcip.annotations.Immutable;

/**
 * Класс SecuredFindAdapterManager содержит методы для поиска объектов по их ID.
 */
@Immutable
public class SecuredFindAdapterManager {
    private static final LockManager lockManager = LockManager.getInstance();
    /**
     * Ищет SecuredWorkflowBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredWorkflowBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredWorkflowBean
     */
    public SecuredWorkflowBean findWorkflowById(SessionContext sc, String id) throws GranException {
        //log.trace("findWorkflowById");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findWorkflowById", "sc", sc);
        if (id == null) return null;

        Workflow w = KernelManager.getFind().findWorkflow(id);
        return new SecuredWorkflowBean(w, sc);
    }

    /**
     * Ищет SecuredCategoryBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredCategoryBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredCategoryBean
     */
    public SecuredCategoryBean findCategoryById(SessionContext sc, String id) throws GranException {
//        log.trace("findCategoryById");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findCategoryById", "sc", sc);

        if (id == null) return null;

        Category c = KernelManager.getFind().findCategory(id);
        return new SecuredCategoryBean(c, sc);
    }

    /**
     * Ищет SecuredNotificationBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredNotificationBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredNotificationBean
     */
    public SecuredNotificationBean findNotificationById(SessionContext sc, String id) throws GranException {
//        log.trace("findCategoryById");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findNotificationById", "sc", sc);
        if (id == null) return null;

        Notification obj = KernelManager.getFind().findNotification(id);
        return new SecuredNotificationBean(obj, sc);
    }

    /**
     * Ищет SecuredFilterBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredFilterBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredFilterBean
     */
    public SecuredFilterBean findFilterById(SessionContext sc, String id) throws GranException {
//        log.trace("findFilterById");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findFilterById", "sc", sc);
        if (id == null) return null;

        Filter w = KernelManager.getFind().findFilter(id);
        return new SecuredFilterBean(w, sc);
    }

    /**
     * Ищет SecuredPrstatusBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredPrstatusBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredPrstatusBean
     */
    public SecuredPrstatusBean findPrstatusById(SessionContext sc, String id) throws GranException {
//        log.trace("findPrstatusById");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findPrstatusById", "sc", sc);
        if (id == null) return null;

        Prstatus w = KernelManager.getFind().findPrstatus(id);
        return new SecuredPrstatusBean(w, sc);
    }

    /**
     * Ищет SecuredTemplateBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredTemplateBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredTemplateBean
     */
    public SecuredTemplateBean findTemplateById(SessionContext sc, String id) throws GranException {
//        log.trace("findEmailTypeById");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findTemplateById", "sc", sc);
        if (id == null) return null;
        Template w = KernelManager.getFind().findTemplate(id);
        return new SecuredTemplateBean(w, sc);
    }

    /**
     * Ищет SecuredSubscriptionBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredSubscriptionBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredSubscriptionBean
     */
    public SecuredSubscriptionBean findSubscriptionById(SessionContext sc, String id) throws GranException {
//        log.trace("findSubscriptionById");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findSubscriptionById", "sc", sc);
        if (id == null) return null;

        Subscription w = KernelManager.getFind().findSubscription(id);
        return new SecuredSubscriptionBean(w, sc);
    }

    /**
     * Ищет SecuredReportBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredReportBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredReportBean
     */
    public SecuredReportBean findReportById(SessionContext sc, String id) throws GranException {
//        log.trace("findReportById");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findReportById", "sc", sc);
        if (id == null) return null;
        Report w = KernelManager.getFind().findReport(id);
        return new SecuredReportBean(w, sc);
    }

    /**
     * Ищет SecuredMailImportBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredMailImportBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredMailImportBean
     */
    public SecuredMailImportBean findMailImportById(SessionContext sc, String id) throws GranException {
//        log.trace("findMailImportById");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findMailImportById", "sc", sc);
        if (id == null) return null;
        MailImport w = KernelManager.getFind().findMailImport(id);
        return new SecuredMailImportBean(w, sc);
    }

    /**
     * Ищет SecuredMessageBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredMessageBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredMessageBean
     */
    public SecuredMessageBean findMessageById(SessionContext sc, String id) throws GranException {
//        log.trace("findMessageById");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findMessageById", "sc", sc);
        if (id == null) return null;

        MessageCacheItem msi = TaskRelatedManager.findMessage(id);
        return new SecuredMessageBean(msi, sc);
    }

    /**
     * Ищет SecuredMstatusBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredMstatusBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredMstatusBean
     */
    public SecuredMstatusBean findMstatusById(SessionContext sc, String id) throws GranException {
//        log.trace("findMstatusById");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findMstatusById", "sc", sc);
        if (id == null) return null;

        Mstatus obj = KernelManager.getFind().findMstatus(id);
        return new SecuredMstatusBean(obj, sc);
    }

    /**
     * Ищет SecuredUserAclBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredUserAclBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredUserAclBean
     */
    public SecuredPriorityBean findPriorityById(SessionContext sc, String id) throws GranException {
//        log.trace("findPriorityById");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findPriorityById", "sc", sc);
        if (id == null) return null;

        Priority obj = KernelManager.getFind().findPriority(id);
        if (obj!=null)
            return new SecuredPriorityBean(obj, sc);
        else return null;
    }

    /**
     * Ищет SecuredTaskAclBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredTaskAclBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredTaskAclBean
     */
    public SecuredUserAclBean findUserAclById(SessionContext sc, String id) throws GranException {
        boolean w = lockManager.acquireConnection(SecuredFindAdapterManager.class.getSimpleName());
        try {
//        log.trace("findAclById");
            if (sc == null)
                throw new InvalidParameterException(this.getClass(), "findAclById", "sc", sc);
            if (id == null) return null;

            Acl obj = KernelManager.getFind().findAcl(id);
            return new SecuredUserAclBean(obj, sc);
        } finally {
            if (w) lockManager.releaseConnection(SecuredFindAdapterManager.class.getSimpleName());
        }
    }

    /**
     * Ищет SecuredResolutionBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredResolutionBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredResolutionBean
     */
    public SecuredTaskAclBean findTaskAclById(SessionContext sc, String id) throws GranException {
//        log.trace("findAclById");
        boolean w = lockManager.acquireConnection(SecuredFindAdapterManager.class.getSimpleName());
        try {
            if (sc == null)
                throw new InvalidParameterException(this.getClass(), "findAclById", "sc", sc);
            if (id == null) return null;

            Acl obj = KernelManager.getFind().findAcl(id);
            return new SecuredTaskAclBean(obj, sc);
        } finally {
            if (w) lockManager.releaseConnection(SecuredFindAdapterManager.class.getSimpleName());
        }
    }

    /**
     * Ищет SecuredStatusBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredStatusBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredStatusBean
     */
    public SecuredResolutionBean findResolutionById(SessionContext sc, String id) throws GranException {
//        log.trace("findResolutionById");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findResolutionById", "sc", sc);
        if (id == null) return null;

        Resolution obj = KernelManager.getFind().findResolution(id);
        return new SecuredResolutionBean(obj, sc);
    }

    /**
     * Ищет SecuredTransitionBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredTransitionBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredTransitionBean
     */
    public SecuredStatusBean findStatusById(SessionContext sc, String id) throws GranException {
//        log.trace("findStatusById");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findStatusById", "sc", sc);
        if (id == null) return null;

        Status obj = KernelManager.getFind().findStatus(id);
        return new SecuredStatusBean(obj, sc);
    }

    /**
     * Ищет SecuredTaskBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredTaskBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredTaskBean
     */
    public SecuredTransitionBean findTransitionById(SessionContext sc, String id) throws GranException {
//        log.trace("findStatusById");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findTransitionById", "sc", sc);
        if (id == null) return null;

        Transition obj = KernelManager.getFind().findTransition(id);
        return new SecuredTransitionBean(obj, sc);
    }

    /**
     * Ищет SecuredTaskUDFBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredTaskUDFBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredTaskUDFBean
     */
    public SecuredTaskBean findTaskById(SessionContext sc, String id) throws GranException {
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findTaskById", "sc", sc);
        if (id == null) return null;
        if (TaskRelatedManager.getInstance().isTaskExists(id))
            return new SecuredTaskBean(id, sc);
        else return null;
    }

    /**
     * Ищет SecuredUserBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredUserBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredUserBean
     */
    public SecuredTaskUDFBean findTaskUDFById(SessionContext sc, String id) throws GranException {
//        log.trace("findTaskUDFById");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findTaskUDFById", "sc", sc);
        if (id == null) return null;

        UDFCacheItem obj = KernelManager.getUdf().getUDFCacheItem(id);
        return new SecuredTaskUDFBean(obj, sc);
    }

    /**
     * Ищет SecuredUserUDFBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredUserUDFBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredUserUDFBean
     */
    public SecuredUdflistBean findUdflistById(SessionContext sc, String id) throws GranException {
//        log.trace("findUdflistById");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findUdflistById", "sc", sc);
        if (id == null) return null;

        Udflist obj = KernelManager.getFind().findUdflist(id);
        return new SecuredUdflistBean(obj, sc);
    }

    /**
     * Ищет SecuredWorkflowUDFBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredWorkflowUDFBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredWorkflowUDFBean
     */
    public SecuredUserBean findUserById(SessionContext sc, String id) throws GranException {
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findUserById", "sc", sc);
        if (id == null) return null;
        if (UserRelatedManager.getInstance().isUserExists(id))
            return new SecuredUserBean(id, sc);
        else return null;
    }

    /**
     * Ищет SecuredRegistrationBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredRegistrationBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredRegistrationBean
     */
    public SecuredUserUDFBean findUserUDFById(SessionContext sc, String id) throws GranException {
//        log.trace("findUserUDFById");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findUserUDFById", "sc", sc);
        if (id == null) return null;

        UDFCacheItem obj = KernelManager.getUdf().getUDFCacheItem(id);
        return new SecuredUserUDFBean(obj, sc);
    }

    /**
     * Ищет SecuredAttachmentBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredAttachmentBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredAttachmentBean
     */
    public SecuredWorkflowUDFBean findWorkflowUDFById(SessionContext sc, String id) throws GranException {
//        log.trace("findWorkflowUDFById");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findWorkflowUDFById", "sc", sc);
        if (id == null) return null;
        UDFCacheItem obj = KernelManager.getUdf().getUDFCacheItem(id);
        return new SecuredWorkflowUDFBean(obj, sc);
    }

    /**
     * Ищет SecuredAttachmentBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredAttachmentBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredAttachmentBean
     */
    public SecuredRegistrationBean findRegistrationById(SessionContext sc, String id) throws GranException {
//        log.trace("findRegistrationById");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findRegistrationById", "sc", sc);
        if (id == null) return null;

        Registration obj = KernelManager.getFind().findRegistration(id);
        return new SecuredRegistrationBean(obj, sc);
    }

    /**
     * Ищет SecuredAttachmentBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredAttachmentBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredAttachmentBean
     */
    public SecuredTaskAttachmentBean findTaskAttachmentById(SessionContext sc, String id) throws GranException {
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findAttachmentById", "sc", sc);
        if (id == null)
            return null;
        Attachment obj = KernelManager.getFind().findAttachment(id);
        AttachmentCacheItem attachment = new AttachmentCacheItem(obj.getId(), obj.getTask() != null ? obj.getTask().getId() : null, obj.getMessage() != null ? obj.getMessage().getId() : null, obj.getUser() != null ? obj.getUser().getId() : null, obj.getName(), obj.getDescription());
        if (obj.getTask() == null) {
            return null;
        } else {
            attachment.setFile(AttachmentManager.getInstance().getAttachmentFile(obj.getTask().getId(), null, id, false));

            return new SecuredMessageAttachmentBean(attachment, sc);

        }
    }
    /**
     * Ищет SecuredAttachmentBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredAttachmentBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredAttachmentBean
     */
    public SecuredUserAttachmentBean findUserAttachmentById(SessionContext sc, String id) throws GranException {
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findAttachmentById", "sc", sc);
        if (id == null)
            return null;
        Attachment obj = KernelManager.getFind().findAttachment(id);
        AttachmentCacheItem attachment = new AttachmentCacheItem(obj.getId(), obj.getTask() != null ? obj.getTask().getId() : null, obj.getMessage() != null ? obj.getMessage().getId() : null, obj.getUser() != null ? obj.getUser().getId() : null, obj.getName(), obj.getDescription());
        if (obj.getTask() == null) {
            attachment.setFile(AttachmentManager.getInstance().getAttachmentFile(null, obj.getUser().getId(), id, false));
            return new SecuredUserAttachmentBean(attachment, sc);
        } else {
            return null;
        }
    }
    public SecuredAttachmentBean findAttachmentById(SessionContext sc, String id) throws GranException {
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findAttachmentById", "sc", sc);
        if (id == null)
            return null;
        Attachment obj = null;
        try {
            obj = KernelManager.getFind().findAttachment(id);
        } catch (CantFindObjectException e) {
            return null; //testing mode
        }
        if (obj == null) {
            return null;
        }
        AttachmentCacheItem attachment = new AttachmentCacheItem(obj.getId(), obj.getTask() != null ? obj.getTask().getId() : null, obj.getMessage() != null ? obj.getMessage().getId() : null, obj.getUser() != null ? obj.getUser().getId() : null, obj.getName(), obj.getDescription());
        if (obj.getTask() == null) {
            attachment.setFile(AttachmentManager.getInstance().getAttachmentFile(null, obj.getUser().getId(), id, false));
            return new SecuredUserAttachmentBean(attachment, sc);
        } else {
            if (obj.getMessage() == null) {
                String taskId = obj.getTask() != null ? obj.getTask().getId() : null;
                String userId = obj.getUser() != null ? obj.getUser().getId() : null;
                File file = AttachmentManager.getInstance().getAttachmentFile(taskId, userId, id, false);
                attachment.setFile(file);
                SecuredTaskAttachmentBean att = new SecuredTaskAttachmentBean(attachment, sc);
                att.init();
                return att;
            } else {
                attachment.setFile(AttachmentManager.getInstance().getAttachmentFile(obj.getTask().getId(), null, id, false));
                return new SecuredMessageAttachmentBean(attachment, sc);
            }
        }
    }
    /**
     * Ищет SecuredAttachmentBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredAttachmentBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredAttachmentBean
     */
    public SecuredMessageAttachmentBean findMessageAttachmentById(SessionContext sc, String id) throws GranException {
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findAttachmentById", "sc", sc);
        if (id == null)
            return null;
        Attachment obj = KernelManager.getFind().findAttachment(id);
        AttachmentCacheItem attachment = new AttachmentCacheItem(obj.getId(), obj.getTask() != null ? obj.getTask().getId() : null, obj.getMessage() != null ? obj.getMessage().getId() : null, obj.getUser() != null ? obj.getUser().getId() : null, obj.getName(), obj.getDescription());
        if (obj.getTask() == null) {
            return null;
        } else {
            attachment.setFile(AttachmentManager.getInstance().getAttachmentFile(obj.getTask().getId(), null, id, false));
            if (obj.getMessage() == null) {
                return null;
            } else {
                return new SecuredMessageAttachmentBean(attachment, sc);
            }
        }
    }
    /**
     * Ищет SecuredBookmarkBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredBookmarkBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredBookmarkBean
     */
    public SecuredBookmarkBean findBookmarkById(SessionContext sc, String id) throws GranException {
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findBookmarkById", "sc", sc);
        if (id == null)
            return null;
        Bookmark b = KernelManager.getFind().findBookmark(id);
        return new SecuredBookmarkBean(b, sc);
    }

    /**
     * Ищет AttachmentCacheItem по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return AttachmentCacheItem
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.AttachmentCacheItem
     */
    public AttachmentCacheItem searchAttachmentById(SessionContext sc, String id) throws GranException {
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findAttachmentById", "sc", sc);
        if (id == null)
            return null;
        Attachment obj = KernelManager.getFind().findAttachment(id);
        AttachmentCacheItem attachment = new AttachmentCacheItem(obj.getId(), obj.getTask() != null ? obj.getTask().getId() : null, obj.getMessage() != null ? obj.getMessage().getId() : null, obj.getUser() != null ? obj.getUser().getId() : null, obj.getName(), obj.getDescription());
        if (obj.getTask() == null) {
            attachment.setFile(AttachmentManager.getInstance().getAttachmentFile(null, obj.getUser().getId(), id, false));
            return attachment;
        } else {
            attachment.setFile(AttachmentManager.getInstance().getAttachmentFile(obj.getTask().getId(), null, id, false));
            return attachment;
        }
    }

    /**
     * Ищет SecuredUDFBean по ID
     *
     * @param sc сессия пользователя
     * @param id ID искомого объекта
     * @return SecuredUDFBean
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredUDFBean
     */
    public SecuredUDFBean findUDFById(SessionContext sc, String id) throws GranException {
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findUDFById", "sc", sc);
        if (id == null) return null;

        UDFCacheItem obj = KernelManager.getUdf().getUDFCacheItem(id);
        if (obj!=null){
            if (obj.getTaskId() != null)
                return new SecuredTaskUDFBean(obj, sc);
            else if (obj.getWorkflowId() != null)
                return new SecuredWorkflowUDFBean(obj, sc);
            else
                return new SecuredUserUDFBean(obj, sc);
        } else return null;
    }

    public List<Pair<String>> findUdfField(SessionContext sc, List<String> ids, String caption) throws GranException {
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findUdfField", "sc", sc);
        if (ids == null) {
            return null;
        }
        return KernelManager.getUdf().getUdfField(ids, caption);
    }


    /**
     * Ищет ID задачи по строке поиска
     *
     * @param sc  сессия пользователя
     * @param key строка поиска
     * @return ID пользователя
     * @throws GranException при необходимости
     */
    public String quickGoHandler(SessionContext sc, String key) throws GranException {
        SecuredTaskBean task = searchTaskByQuickGo(sc, key);
        String taskId;
        if (task == null || !sc.taskOnSight(task.getId()))
            taskId = sc.getUser().getDefaultProjectId();
        else {
            taskId = task.getId();
        }
        if (taskId == null)
            taskId = "1";
        return taskId;
    }

    /**
     * Ищет ID задачи по строке поиска
     *
     * @param sc  сессия пользователя
     * @param key строка поиска
     * @return ID пользователя
     * @throws GranException при необходимости
     */
    public SecuredTaskBean searchTaskByQuickGo(SessionContext sc, String key) throws GranException {
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "quickGoHandler", "sc", sc);
        key = key != null && key.indexOf('#') > -1 ? key.substring(1) : key;
        String taskId = AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskIdByQuickGo(sc, key);
        if (taskId != null && !sc.taskOnSight(taskId)) {
            return null;
        }
        if (taskId != null)
            return new SecuredTaskBean(taskId, sc);
        else return null;
    }

    public List<TaskRelatedInfo> searchTaskByQuickGoIndexOf(SessionContext sc, int limit, String ... keys) throws GranException {
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "quickGoHandler", "sc", sc);
        List<TaskRelatedInfo> tasks = new ArrayList<TaskRelatedInfo>();
        for (String key: keys) {
            key = key != null && key.indexOf('#') > -1 ? key.substring(1) : key;
            for (String taskId : AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskIdByQuickGoIndexOf(sc, key, limit)) {
                if (taskId != null && sc.taskOnSight(taskId)) {
                    tasks.add(new SecuredTaskBean(taskId, sc).getTask());
                }
            }

        }
        return tasks;
    }

    /**
     * Ищет ID пользователя по строке поиска
     *
     * @param sc  сессия пользователя
     * @param keys строка поиска
     * @return ID пользователя
     * @throws GranException при необходимости
     */
    public SecuredUserBean searchUserByQuickGo(SessionContext sc, String ... keys) throws GranException {
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "searchUserByQuickGo", "sc", sc);
        String userId = null;
        for (String key : keys) {
            userId = AdapterManager.getInstance().getSecuredUserAdapterManager().findUserIdByQuickGo(sc, key);
            if (userId != null) {
                break;
            }
        }
        if (userId != null && !sc.userOnSight(userId)) {
            return null;
        }
        if (userId != null)
            return new SecuredUserBean(userId, sc);
        else return null;
    }
}