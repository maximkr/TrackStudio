package com.trackstudio.app.adapter.email.change;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.trackstudio.constants.FilterConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredAttachmentBean;
import com.trackstudio.secured.SecuredMessageAttachmentBean;
import com.trackstudio.secured.SecuredMessageBean;

import net.jcip.annotations.Immutable;

/**
 * Изменение, при добавлении нового сообщения с приложенными файлами
 */
@Immutable
public class NewMessageWithAttachmentChange extends NewMessageChange {

    /**
     * Конструктор.
     *
     * @param when    дата изменения
     * @param by      ID пользователя
     * @param message сообщение
     */
    // Используется в конфигурации ITIL, не удалять
    public NewMessageWithAttachmentChange(Calendar when, String by, SecuredMessageBean message, final String auditId) {
        super(when, by, message, FilterConstants.FIRE_NEW_MESSAGE_WITH_ATTACHMENT, auditId);
    }

    /**
     * Возвращает список приложенных файлов
     *
     * @return список файлов
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredMessageAttachmentBean
     */
    public List<SecuredAttachmentBean> getAttachments() throws GranException {
        List<SecuredAttachmentBean> beans = new ArrayList<SecuredAttachmentBean>();
        for (SecuredMessageAttachmentBean attachmentBean : getMessage().getAttachments()) {
            beans.add(attachmentBean);
        }
        return beans;
    }
}