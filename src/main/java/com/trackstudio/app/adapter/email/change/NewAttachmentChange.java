package com.trackstudio.app.adapter.email.change;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.constants.FilterConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredAttachmentBean;
import com.trackstudio.tools.Null;

/**
 * Изменение, при добавлении нового прилоежнного файла
 */
public class NewAttachmentChange extends Change {

    private final CopyOnWriteArrayList<String> attachments;

    /**
     * Конструктор
     *
     * @param when        лата, когда было произведено изменение
     * @param by          ID пользователя, который произвел изменение
     * @param attachments список ID прилоежнных файлов
     */
    public NewAttachmentChange(Calendar when, String by, List<String> attachments, final String auditId) {
        super(when, by, FilterConstants.FIRE_NEW_ATTACHMENT, auditId);
        this.attachments = new CopyOnWriteArrayList<String>(Null.removeNullElementsFromList(attachments));
    }

    /**
     * Возвращает список прилоежнных файлов
     *
     * @return список файлов
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredAttachmentBean
     */
    public List<SecuredAttachmentBean> getAttachments() throws GranException {
        if (getSession() != null) {
            List<SecuredAttachmentBean> list = new ArrayList<SecuredAttachmentBean>();
            for (String attach : attachments) {
                list.add(AdapterManager.getInstance().getSecuredFindAdapterManager().findAttachmentById(getSession(), attach));
            }
            return list;
        } else
            return null;
    }
}