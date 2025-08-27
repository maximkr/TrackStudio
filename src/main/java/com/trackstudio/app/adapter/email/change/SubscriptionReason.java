package com.trackstudio.app.adapter.email.change;

import java.util.Calendar;
import java.util.List;

import com.trackstudio.constants.FilterConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredAttachmentBean;
import com.trackstudio.simple.Notification;

import net.jcip.annotations.Immutable;

/**
 * Изменение, для рассылки подписок
 */
@Immutable
public class SubscriptionReason extends Change {
    private final Notification notification;

    /**
     * Конструктор
     *
     * @param when дата изменения
     * @param by   ID пользователя
     * @param not  нотификация
     */
    public SubscriptionReason(Calendar when, String by, Notification not) {
        super(when, by, FilterConstants.FIRE_ON_TIME, null);
        this.notification = not;
    }

    /**
     * Возвращает нотификацию
     *
     * @return нотификацию
     */
    public Notification getNotification() {
        return notification;
    }

    @Override
    public List<SecuredAttachmentBean> getAttachments() throws GranException {
        return null;
    }
}