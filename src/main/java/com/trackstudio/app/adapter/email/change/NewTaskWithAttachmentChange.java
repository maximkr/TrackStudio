package com.trackstudio.app.adapter.email.change;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.constants.FilterConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.AttachmentCacheItem;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.secured.SecuredAttachmentBean;

/**
 * Изменение, при добавлении новой задачи c приложенными файлами
 */
public class NewTaskWithAttachmentChange extends NewTaskChange {
    private final String task;

    /**
     * Конструктор
     *
     * @param when        дата изменения
     * @param by          ID пользователя
     * @param task        ID задачи
     */
    public NewTaskWithAttachmentChange(Calendar when, String by, String task) {
        super(when, by, task, FilterConstants.FIRE_NEW_TASK_WITH_ATTACHMENT);
        this.task = task;
    }

    /**
     * Возвращает список прилоежнных файлов
     *
     * @return список прилоежнных файлов
     * @throws GranException при необходимости
     */
    public List<SecuredAttachmentBean> getAttachments() throws GranException {
        if (getSession() != null) {
            List<SecuredAttachmentBean> list = new ArrayList<SecuredAttachmentBean>();
            for (AttachmentCacheItem attach :TaskRelatedManager.getInstance().find(task).getAttachments()) {
                list.add(AdapterManager.getInstance().getSecuredFindAdapterManager().findAttachmentById(getSession(), attach.getId()));
            }
            return list;
        } else {
            return null;
        }
    }
}
