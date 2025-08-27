package com.trackstudio.tools.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.app.session.SessionManager;
import com.trackstudio.exception.GranException;

import net.jcip.annotations.ThreadSafe;

/**
 * Класс кастомного JSTL-тега, описывающий тег < ts:set >.
 * <br/>
 * Используется для помещения атрибутов в сессия пользователя
 */
@ThreadSafe
public class SetTag extends BodyTagSupport {
    protected volatile Object value;             // tag attribute
    protected volatile boolean valueSpecified;   // status
    private volatile String key;                 // tag attribute
    private volatile String session;     // tag attribute

    /**
     * Конструктор
     */
    public SetTag() {
        super();
        init();
    }

    private void init() {
        value = key = null;
        session = null;
    }

    /**
     * Возвращает значение
     *
     * @return значение
     */
    public Object getValue() {
        return value;
    }

    /**
     * Устанавливает значение
     *
     * @param value значение
     */
    public void setValue(Object value) {
        this.value = value;
        this.valueSpecified = true;
    }

    public boolean isValueSpecified() {
        return valueSpecified;
    }

    public String getKey() {
        return key;
    }

    public String getSession() {
        return session;
    }

    /**
     * Срабатывает при завершении работы с тегом
     */


    public void release() {
        super.release();
        init();
    }

    /**
     * Обрабатывает последнее вхождение тега
     *
     * @return код, что делать дальше
     * @throws JspException при необходимости
     */
    public int doEndTag() throws JspException {
        Object result;
        if (value != null) {
            result = value;
        } else if (valueSpecified) {
            result = null;
        } else {
            if (bodyContent == null || bodyContent.getString() == null)
                result = "";
            else
                result = bodyContent.getString().trim();
        }
        SessionContext sc = null;

        if (session != null) {
            try {
                sc = SessionManager.getInstance().getSessionContext(session);
            } catch (GranException e) {

            }
        }
        if (key != null && sc != null) {
            if (result != null) {
                sc.setAttribute(key, result);
            } else {
                sc.removeAttribute(key);
            }

        }
        return EVAL_PAGE;
    }

    /**
     * Устанавливает атрибут тега
     *
     * @param key атрибут
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Устанавливает сессию пользователя
     *
     * @param sess сессия пользователя
     */
    public void setSession(String sess) {
        this.session = sess;
    }
}