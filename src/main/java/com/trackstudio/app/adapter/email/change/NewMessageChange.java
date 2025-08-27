package com.trackstudio.app.adapter.email.change;

import java.util.Calendar;
import java.util.List;

import com.trackstudio.constants.FilterConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredAttachmentBean;
import com.trackstudio.secured.SecuredMessageBean;

import net.jcip.annotations.Immutable;

/**
 * Изменение, при добавлении нового сообщения
 */
@Immutable
public class NewMessageChange extends Change {

    private final SecuredMessageBean message;
    /**
     * Конструктор
     *
     * @param when    дата изменения
     * @param by      ID пользователя
     * @param message сообщение
     */
    public NewMessageChange(Calendar when, String by, SecuredMessageBean message, final String auditId)  {
        super(when, by, FilterConstants.FIRE_NEW_MESSAGE, auditId);
        this.message = message;
    }

    public NewMessageChange(Calendar when, String by, SecuredMessageBean message, String code,  final String auditId) {
        super(when, by, code, auditId);
        this.message = message;
    }

    /**
     * Возвращает сообщение
     *
     * @return сообщение
     * @throws GranException при необходимости
     */
    public SecuredMessageBean getMessage() throws GranException {
        return this.message;
    }

    @Override
    public List<SecuredAttachmentBean> getAttachments() throws GranException {
        return null;
    }
}