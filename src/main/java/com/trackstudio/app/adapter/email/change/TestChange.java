package com.trackstudio.app.adapter.email.change;

import java.util.Calendar;
import java.util.List;

import com.trackstudio.constants.FilterConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredAttachmentBean;
import com.trackstudio.simple.Notification;

import net.jcip.annotations.Immutable;

/**
 * Изменение для тестирования
 */
@Immutable
public class TestChange extends Change {

    private final Notification notification;

    /**
     * Конструктор
     *
     * @param when дата изменения
     * @param by   ID пользователя
     * @param not  нотификация
     */
    public TestChange(Calendar when, String by, Notification not) {
        super(when, by, FilterConstants.FIRE_ON_TEST, null);
        this.notification = not;
    }

    /**
     * Возвращает нотификацию
     *
     * @return нотификация
     */
    public Notification getNotification() {
        return notification;
    }

    @Override
    public List<SecuredAttachmentBean> getAttachments() throws GranException {
        return null;
    }
}