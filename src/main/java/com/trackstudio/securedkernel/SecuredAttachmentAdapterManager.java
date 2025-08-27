package com.trackstudio.securedkernel;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.adapter.email.change.Change;
import com.trackstudio.app.adapter.email.change.NewAttachmentChange;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.AccessDeniedException;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.InvalidParameterException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.AttachmentCacheItem;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.manager.AttachmentArray;
import com.trackstudio.kernel.manager.IndexManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.model.Attachment;
import com.trackstudio.secured.SecuredAttachmentBean;
import com.trackstudio.secured.SecuredMessageAttachmentBean;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredSearchAttachmentItem;
import com.trackstudio.secured.SecuredTaskAttachmentBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserAttachmentBean;
import com.trackstudio.tools.audit.trail.AuditTrailAttachment;
import com.trackstudio.tools.audit.trail.AuditUtil;

import net.jcip.annotations.Immutable;

/**
 * Класс AttachmentManager содержит методы для работы с приложенными файлами.<br/>
 * Файлы могут быть приложены к задачам, сообщениям и пользователям.
 */
@Immutable
public class SecuredAttachmentAdapterManager {

    private static final Log log = LogFactory.getLog(SecuredAttachmentAdapterManager.class);
    private final Class c = this.getClass();

    /**
     * Удаляет прилоежнный файл с указанным ID
     *
     * @param sc           сессия пользователя
     * @param attachmentId ID приложенного файла
     * @throws GranException при необходимости
     */
    public void deleteAttachment(SessionContext sc, String attachmentId) throws GranException {
        log.trace("deleteAttachment");
        if (sc == null)
            throw new InvalidParameterException(c, "deleteAttachment", "sessionId", null);
        if (attachmentId == null)
            throw new InvalidParameterException(c, "deleteAttachment", "attachmentId", sc);
        SecuredAttachmentBean attachment = AdapterManager.getInstance().getSecuredFindAdapterManager().findAttachmentById(sc, attachmentId);
        if (attachment instanceof SecuredMessageAttachmentBean && ((SecuredMessageAttachmentBean) attachment).getMessageId() != null) {
            String taskId = ((SecuredMessageAttachmentBean) attachment).getMessage().getTaskId();
            if (!sc.canAction(Action.manageTaskMessageAttachments, taskId)) {
                if (!sc.canAction(Action.deleteTheirMessageAttachment, taskId)) {
                    throw new AccessDeniedException(c, "deleteAttachment", sc, "!(sc.canAction(com.trackstudio.kernel.cache.Action.manageTaskMessageAttachments, taskId))", taskId);
                }
            }
            AuditUtil builder = new AuditUtil(new StringBuilder("Attachments was deleted from a message.<br/>"), taskId, Calendar.getInstance(), AuditUtil.Type.ATTACHMENT);
            new AuditTrailAttachment(sc, attachment.getTaskId(), builder).auditAttachment(Arrays.asList(attachmentId));
        } else if (attachment instanceof SecuredTaskAttachmentBean) {
            String taskId = attachment.getTaskId();
            if (!sc.canAction(Action.manageTaskAttachments, taskId)) {
                if (!sc.canAction(Action.deleteTheirTaskAttachment, taskId)) {
                    throw new AccessDeniedException(c, "deleteAttachment", sc, "!(sc.canAction(com.trackstudio.kernel.cache.Action.manageTaskAttachments, taskId))", taskId);
                }
            }
            AuditUtil builder = new AuditUtil(new StringBuilder("Attachments was deleted a task.<br/>"), taskId, Calendar.getInstance(), AuditUtil.Type.ATTACHMENT);
            new AuditTrailAttachment(sc, attachment.getTaskId(), builder).auditAttachment(Arrays.asList(attachmentId));

        } else if (attachment instanceof SecuredUserAttachmentBean) {
            String userId = attachment.getUserId();
            if (!sc.canAction(Action.manageUserAttachments, userId)) {
                throw new AccessDeniedException(c, "deleteAttachment", sc, "!(sc.canAction(com.trackstudio.kernel.cache.Action.manageUserAttachments, userId))", userId);
            }
        }
        KernelManager.getAttachment().deleteAttachment(attachmentId);
    }

    /**
     * Создает приложенный файл
     *
     * @param sc          сессия пользователя
     * @param taskId      ID задачи, к которой прикладывается файл (если файл приклатывается к пользователю, то указывается ID пользователя)
     * @param messageId   ID сообщения, к которому прикладывается файл
     * @param userId      ID пользователя, который прикладывает файл
     * @param name        Название приложенного файла
     * @param description Описание приложенного файла
     * @param data        содержание приложенного файла в виде массива байтов
     * @return список ID созданных файлов
     * @throws GranException при необходимости
     */
    public List<String> createAttachment(SessionContext sc, String taskId, String messageId, String userId, String name, String description, InputStream data) throws GranException {
        ArrayList<AttachmentArray> fake = new ArrayList<AttachmentArray>();
        fake.add(new AttachmentArray(SafeString.createSafeString(name), SafeString.createSafeString(description), data));
        return createAttachment(sc, taskId, messageId, userId, fake);
    }

    /**
     * Редактирует приложенный файл
     *
     * @param sc           сессия пользователя
     * @param attachmentId ID релактируемого приложенного файла
     * @param name         Название приложенного файла
     * @param description  Описание приложенного файла
     * @throws GranException при необходимости
     */
    public void updateAttachment(SessionContext sc, String attachmentId, String name, String description) throws GranException {
        log.trace("updateAttachment");
        if (sc == null)
            throw new InvalidParameterException(c, "updateAttachment", "sessionId", null);
        if (attachmentId == null)
            throw new InvalidParameterException(c, "updateAttachment", "attachmentId", sc);
        if (name == null || name.length() == 0 || (name.toLowerCase(Locale.ENGLISH).equals("null")))
            throw new InvalidParameterException(c, "updateAttachment", "name", sc);
        SecuredAttachmentBean attachment = AdapterManager.getInstance().getSecuredFindAdapterManager().findAttachmentById(sc, attachmentId);
        if (attachment instanceof SecuredMessageAttachmentBean) {
            String taskId = ((SecuredMessageAttachmentBean) attachment).getMessage().getTaskId();
            if (!sc.canAction(Action.manageTaskMessageAttachments, taskId))
                throw new AccessDeniedException(c, "updateAttachment", sc, "!(sc.canAction(com.trackstudio.kernel.cache.Action.manageTaskMessageAttachments, taskId))", taskId);
        } else if (attachment instanceof SecuredTaskAttachmentBean) {
            String taskId = (attachment).getTaskId();
            if (!sc.canAction(Action.manageTaskAttachments, taskId))
                throw new AccessDeniedException(c, "updateAttachment", sc, "!(sc.canAction(com.trackstudio.kernel.cache.Action.manageTaskAttachments, taskId))", taskId);
        } else if (attachment instanceof SecuredUserAttachmentBean) {
            String userId = attachment.getUserId();
            if (!sc.canAction(Action.manageUserAttachments, userId))
                throw new AccessDeniedException(c, "createAttachment", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.manageUserAttachments, userId)", userId);
            if ((sc.getUserId().equals(userId) && !sc.canAction(Action.editUserHimself, userId)) || (!sc.getUserId().equals(userId) && !sc.canAction(Action.editUserChildren, userId)))
                throw new AccessDeniedException(c, "updateAttachment", sc, "(sc.getUserId().equals(userId) && !sc.canAction(Action.editUserHimself, userId)) || (!sc.getUserId().equals(userId) && !sc.canAction(Action.editUserChildren, userId))", userId);
        }
        KernelManager.getAttachment().updateAttachment(attachmentId, SafeString.createSafeString(name), SafeString.createSafeString(description));


    }

    /**
     * Создает список приложенных файлов
     *
     * @param sc          сессия пользователя
     * @param taskId      ID задачи, к которй прикладывается файл
     * @param messageId   ID сообщения, к которому прикладывается файл
     * @param userId      ID пользователя, к которому прикладывается файл
     * @param attachments список объектов, содержащих данные о создаваемых файлах
     * @param sendMail    нужно ли слать почту
     * @return список ID созданных приложенных файлов
     * @throws GranException при необзодимости
     * @see com.trackstudio.kernel.manager.AttachmentArray
     */
    public List<String> createAttachment(SessionContext sc, String taskId, String messageId, String userId, final List<AttachmentArray> attachments, boolean sendMail) throws GranException {
        log.trace("createAttachment");
        if (sc == null)
            throw new InvalidParameterException(c, "createAttachment", "sessionId", null);
        if (attachments == null)
            throw new InvalidParameterException(c, "createAttachment", "getTaskAttachments", sc);
        if (messageId != null) {
            SecuredMessageBean mb = AdapterManager.getInstance().getSecuredFindAdapterManager().findMessageById(sc, messageId);
            taskId = mb.getTaskId();
        }
        if (taskId != null) {
            if (!sc.allowedByACL(taskId))
                throw new AccessDeniedException(c, "createAttachment", sc, "!sc.allowedByACL(taskId)", taskId);
            if (messageId != null) {
                if (!sc.canAction(Action.createTaskMessageAttachments, taskId))
                    throw new AccessDeniedException(c, "createAttachment", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.createTaskMessageAttachments, taskId)", taskId);
            } else {
                if (!AdapterManager.getInstance().getSecuredTaskAdapterManager().isTaskEditable(sc, taskId)) {
                    if (!sc.canAction(Action.createTaskAttachments, taskId)) {
                        throw new AccessDeniedException(c, "createAttachment", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.createTaskAttachments, taskId)", taskId);
                    }
                }
            }
        } else {
            if (!sc.canAction(Action.createUserAttachments, userId))
                throw new AccessDeniedException(c, "createAttachment", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.createUserAttachments, taskId)", userId);
            if (!sc.allowedByUser(userId))
                throw new AccessDeniedException(c, "createAttachment", sc, "!sc.allowedByACL(userId)", userId);
        }
        ArrayList<String> ret = KernelManager.getAttachment().createAttachment(taskId, messageId, taskId != null ? sc.getUserId() : userId, attachments);
        if (taskId != null && ret != null && !ret.isEmpty()) {
            TaskRelatedInfo tri = TaskRelatedManager.getInstance().find(taskId);
            KernelManager.getIndex().reIndexTask(tri);
            if (sendMail) {
                Calendar now = new GregorianCalendar();
                now.setTimeInMillis(System.currentTimeMillis());
                Change attChange = new NewAttachmentChange(now, sc.getUserId(), ret, null);
                AdapterManager.getInstance().getFilterNotifyAdapterManager().sendNotifyForTask(null, taskId, sc.getUserId(), null, attChange);
            }
            AuditUtil builder = new AuditUtil(new StringBuilder("Attachments was added to a task.<br/>"), taskId, Calendar.getInstance(), AuditUtil.Type.ATTACHMENT);
            new AuditTrailAttachment(sc, taskId, builder).auditAttachment(ret);
        }
        if (taskId == null && ret != null && !ret.isEmpty()) {
            KernelManager.getIndex().reIndexUser(userId);
        }
        return ret;
    }

    /**
     * Создает список приложенных файлов
     *
     * @param sc          сессия пользователя
     * @param taskId      ID задачи, к которй прикладывается файл
     * @param messageId   ID сообщения, к которому прикладывается файл
     * @param userId      ID пользователя, к которому прикладывается файл
     * @param attachments список объектов, содержащих данные о создаваемых файлах
     * @return список ID созданных приложенных файлов
     * @throws GranException при необзодимости
     * @see com.trackstudio.kernel.manager.AttachmentArray
     */
    public List<String> createAttachment(SessionContext sc, String taskId, String messageId, String userId, List<AttachmentArray> attachments) throws GranException {
        return createAttachment(sc, taskId, messageId, userId, attachments, true);
    }

    /**
     * Возвращает содержимое приложенного файла в виде массива байтов
     *
     * @param sc       сессия пользователя

     * @param attId ID приложенного файла
     * @return содержимое приложенного файла в виде массива байтов
     * @throws GranException при необходимости
     */
    public byte[] getAttachment(SessionContext sc, String attId) throws GranException {
        log.trace("getAttachment");
        if (sc == null)
            throw new InvalidParameterException(c, "getAttachment", "sessionId", null);
        if (attId == null)
            throw new InvalidParameterException(c, "getAttachment", "attId", sc);
        Attachment model = KernelManager.getFind().findAttachment(attId);
        if (model != null) {
            if (model.getTask() != null) {
                if (!sc.canAction(Action.viewTaskAttachments, model.getTask().getId()))
                    throw new AccessDeniedException(this.getClass(), "getAttachment", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.viewTaskAttachments, taskId)", model.getTask().getId());
                if (!sc.allowedByACL(model.getTask().getId()))
                    throw new AccessDeniedException(this.getClass(), "getAttachment", sc, "!sc.allowedByACL(taskId)", model.getTask().getId());
                return KernelManager.getAttachment().getAttachment(model.getTask().getId(), null, attId);
            }
            if (model.getUser() != null) {
                if (!sc.canAction(Action.viewUserAttachments, model.getUser().getId()))
                    throw new AccessDeniedException(this.getClass(), "getAttachment", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.viewUserAttachments, userId)", model.getUser().getId());
                if (!sc.allowedByUser(model.getUser().getId()))
                    throw new AccessDeniedException(this.getClass(), "getAttachment", sc, "!sc.allowedByUser(userId)", model.getUser().getId());
                return KernelManager.getAttachment().getAttachment(null, model.getUser().getId(), attId);
            } else return null;
        } else return null;
    }

    /**
     * Возвращает список приложенных файлов для сообщения
     *
     * @param sc        сессия пользователя
     * @param messageId ID сообщения
     * @return список прилоежнных файлов
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredMessageAttachmentBean
     */
    public ArrayList<SecuredMessageAttachmentBean> getMessageAttachments(SessionContext sc, String messageId) throws GranException {
        log.trace("getMessageAttachments");
        if (sc == null)
            throw new InvalidParameterException(c, "getMessageAttachments", "sessionId", null);
        if (messageId == null)
            throw new InvalidParameterException(c, "getMessageAttachments", "messageId", sc);

        SecuredMessageBean m = AdapterManager.getInstance().getSecuredFindAdapterManager().findMessageById(sc, messageId);

        if (!sc.canAction(Action.viewTaskAttachments, m.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "getMessageAttachments", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.viewTaskAttachments, taskId)", m.getTaskId());
        if (!sc.allowedByACL(m.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "getMessageAttachments", sc, "!sc.allowedByACL(taskId)", m.getTaskId());
        ArrayList<SecuredMessageAttachmentBean> list = new ArrayList<SecuredMessageAttachmentBean>();
        List<AttachmentCacheItem> l = KernelManager.getAttachment().getAttachmentList(m.getTaskId(), m.getId(), null);
        for (AttachmentCacheItem aca : l)
            list.add(new SecuredMessageAttachmentBean(aca, sc));
        return list;

    }

    /**
     * Ищет приложенные файлы по их названиям. Полнотекстовый поиск
     *
     * @param sc           сессия пользователя
     * @param searchString что ищем
     * @return возвращает результаты поиска
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredSearchAttachmentItem
     */
    public ArrayList<SecuredSearchAttachmentItem> fullTextSearch(SessionContext sc, String searchString) throws GranException {
        log.trace("fullTextSearch for attachments");
        HashMap<String, String> t = IndexManager.getIndex().searchAttachments(searchString);
        ArrayList<SecuredSearchAttachmentItem> v = new ArrayList<SecuredSearchAttachmentItem>();
        if (t == null)
            return v;
        for (String anAttachmentId : new TreeSet<String>(t.keySet())) {
            SecuredAttachmentBean b = AdapterManager.getInstance().getSecuredFindAdapterManager().findAttachmentById(sc, anAttachmentId);
            if (b != null) {
                SecuredTaskBean tc = new SecuredTaskBean(b.getTaskId(), sc);
                if (tc.canView() && b.canView()) {
                    v.add(new SecuredSearchAttachmentItem(b, t.get(anAttachmentId), searchString));
                }
            }
        }
        return v;
    }

    /**
     * Возвращает наличие файла на диске
     *
     * @param sc           сессия пользователя
     * @param attachmentId ID приложенного файла
     * @return TRUE - файл с диска удален, FALSE - файл на диске присутствует
     * @throws GranException при необходимости
     */
    public Boolean getAttachmentIsDeleted(SessionContext sc, String attachmentId) throws GranException {
        log.trace("getCategoryIsValid");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getCategoryIsValid", "sc", sc);
        if (attachmentId == null)
            throw new InvalidParameterException(this.getClass(), "getCategoryIsValid", "attachmentId", sc);
        return KernelManager.getAttachment().getAttachmentIsDeleted(attachmentId);
    }

    /**
     * Создает арзих из списка файлов.
     * @param sc Сессия пользователя
     * @param outputStream выходной поток
     * @param attachmentId скисок id аттачей
     * @exception GranException при необходмисти
     */
    public void createZip(SessionContext sc, OutputStream outputStream, List<String> attachmentId) throws GranException {
        byte[] buf = new byte[1024];
        try {
            ZipOutputStream out = new ZipOutputStream(outputStream);
            for (String id : attachmentId) {
                SecuredAttachmentBean att = AdapterManager.getInstance().getSecuredFindAdapterManager().findAttachmentById(sc, id);
                if (att.getFile() != null) {
                    byte[] file = getAttachment(sc, id);
                    ByteArrayInputStream sourceStream = new ByteArrayInputStream(file);
                    out.putNextEntry(new ZipEntry(att.getName()));
                    int len;
                    while ((len = sourceStream.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }
                    out.closeEntry();
                }
            }
            out.close();
        } catch (Exception e) {
            throw new GranException(e);
        }
    }

}