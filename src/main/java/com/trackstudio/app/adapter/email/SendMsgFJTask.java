package com.trackstudio.app.adapter.email;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.concurrent.FJTask;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.adapter.email.change.Change;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.app.session.SessionManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.AttachmentManager;
import com.trackstudio.secured.SecuredAttachmentBean;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredTaskAttachmentBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.simple.Notification;
import com.trackstudio.tools.HibernateUtil;
import com.trackstudio.tools.TemplateUtil;

import freemarker.ext.beans.BeansWrapper;
import net.jcip.annotations.ThreadSafe;

/**
 * Отправляет сообщение для задачи в отдельном потоке
 */
@ThreadSafe
public class SendMsgFJTask extends FJTask {
    private static final LockManager lockManager = LockManager.getInstance();

    private static final Log log = LogFactory.getLog(SendMsgFJTask.class);
    private final com.trackstudio.simple.Notification notification;
    private final String fi;
    private final String toUser;
    private final String taskId;
    private final SecuredUserBean fromUser;
    private volatile boolean result;
    private volatile Change reason;
    private final boolean notificationList;
    private final String type;
    private volatile boolean testMode = false;
    protected final HibernateUtil hu = new HibernateUtil();

    public Notification getNotification() {
        return notification;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    /**
     * Конструктор
     *
     * @param notification нотификация
     * @param fi           ID фильтра
     * @param toUser       ID пользователя
     * @param taskId       ID задачи
     * @param reason       причина отправки (изменение)
     * @param fromUser     отправитель сообщения
     * @param notificationList вызов для подписки
     */
    public SendMsgFJTask(com.trackstudio.simple.Notification notification, String fi, String toUser, String taskId, Change reason, SecuredUserBean fromUser, boolean notificationList, String type) {
        this.notification = notification;
        this.fi = fi;
        this.toUser = toUser;
        this.taskId = taskId;
        this.reason = reason;
        this.fromUser = fromUser;
        this.notificationList = notificationList;
        this.type = type;
        hu.cleanSession(); // we send messages in another threads. So we should flush current thread, otherwise that new threads will not see new msgs, etc until we complete the page

    }

    /**
     * Отправляет сообщение
     */
    public void run() {
        boolean w = lockManager.acquireConnection();
        try {
            SecuredUserBean user;
            log.trace("sendByMail");

            // Self-secured User
            String sessionId = SessionManager.getInstance().create(UserRelatedManager.getInstance().find(toUser));
            SessionContext sc = SessionManager.getInstance().getSessionContext(sessionId);
            if (sc == null) {
                this.result = false;
                return;
            }
            user = sc.getUser();

            SecuredTaskBean task = new SecuredTaskBean(taskId, sc);
            SecuredFilterBean fb = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, fi);
            log.debug("Send message to " + user.getLogin() + " " + user.getEmail());
            TemplateUtil tu = new TemplateUtil(sc);
            boolean hasSubtasks = sc.taskOnSight(taskId) && tu.subtask(task, fb.getId());
            log.debug("Notify list :  " + notificationList + " hasSubtasks :  " + hasSubtasks + " task : " + task.getNumber() + " filter : " + fb.getId());
            if (!testMode) {
                if (!hasSubtasks && notificationList) {
                    result = false;
                    return;
                }
            }

            Map data = AdapterManager.getInstance().getFilterNotifyAdapterManager().getDataMap(sc, task, notification, fi, reason, fromUser);
            data.put("Util", BeansWrapper.getDefaultInstance().wrap(tu));
            AdapterManager.getInstance().getSecuredSenderAdapterManager().process(type, user, task, data, reason, notification, toUser);
            result = true;
            return;
        } catch (Exception e) {
            log.error("E-mail notification error: ", e);
        } finally {
            if (w) lockManager.releaseConnection();
        }
        result = false;
    }

    /**
     * Возвращает результат отправки сообщения
     *
     * @return TRUE - если все верно
     */
    public boolean getResult() {
        return result;
    }

    /**
     * Возвращает карту приложенных файлов
     *


     * @param task задача
     * @return карта приложенных файлов
     * @throws GranException по вкусу
     */
    public static HashMap<String, String> getAttachmentsForTask(Change reason) throws GranException {
        HashMap<String, String> files = new HashMap<String, String>();
        List<SecuredAttachmentBean> atts = reason.getAttachments();
        if (atts != null && !atts.isEmpty()) {
            for (SecuredAttachmentBean att : atts) {
                try {
                    files.put(AttachmentManager.getAttachmentDirPath(att.getTaskId(), att.getUserId(), false) + File.separator + att.getId(), att.getName());
                } catch (Exception e) {
                    log.error("Attachment not found",e);
                }
            }
        }
        return files;
    }

    public static HashMap<String, String> getAttachmentsForTask(SecuredTaskBean task) throws GranException {
        HashMap<String, String> files = new HashMap<String, String>();
        List<SecuredTaskAttachmentBean> atts = task.getAttachments();
        if (atts != null && !atts.isEmpty()) {
            for (SecuredAttachmentBean att : atts) {
                try {
                    files.put(AttachmentManager.getAttachmentDirPath(att.getTaskId(), att.getUserId(), false) + File.separator + att.getId(), att.getName());
                } catch (Exception e) {
                    log.error("Attachment not found",e);
                }
            }
        }
        return files;
    }
}