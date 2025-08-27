package com.trackstudio.app.adapter.email.change;

import java.util.Calendar;
import java.util.List;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredAttachmentBean;
import com.trackstudio.secured.SecuredUserBean;

import net.jcip.annotations.Immutable;

/**
 * Абстрактный класс, на основании которого реализуются классы, описывающие изменения в системе,
 * при которых нужно отсылать уведомления на e-mail
 */
@Immutable
public abstract class Change {
    private final Calendar when;
    private final String by;
    private final String code;
    private final ThreadLocal<SessionContext> session = new ThreadLocal<SessionContext>();

    /**
     * Audit message id
     */
    private final String auditId;

    public abstract List<SecuredAttachmentBean> getAttachments() throws GranException;

    /**
     * Конструктор
     *
     * @param when дата, когда было произведено изменение
     * @param by   ID пользователя, который произвел изменение
     * @param code Код изменения
     */
    public Change(final Calendar when, final String by, final String code, final String auditId) {
        this.when = when;
        this.by = by;
        this.code = code;
        this.auditId = auditId;
    }

    /**
     * Возвращает сессию пользователя
     *
     * @return сессия пользователя
     */
    public SessionContext getSession() {
        return session.get();
    }

    /**
     * Устанавливает сессию пользователя
     *
     * @param session сессия пользователя
     */
    public void setSession(SessionContext session) {
        this.session.set(session);
    }

    /**
     * Возвращает пользователя, произвевшего изменения
     *
     * @return пользователь
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredUserBean
     */
    public SecuredUserBean getBy() throws GranException {
        if (getSession() != null) {
            return new SecuredUserBean(by, getSession());
        } else {
            return null;
        }
    }

    /**
     * Возвращает код изменения
     *
     * @return код изменения
     */
    public String getCode() {
        return code;
    }

    /**
     * Возвращает дату, когда было произведено изменение
     *
     * @return дата
     */
    public Calendar getWhen() {
        return when;
    }

    /**
     * Get audit message which connects to specific change
     * @return Audit ID
     */
    public String getAuditId() {
        return this.auditId;
    }
}