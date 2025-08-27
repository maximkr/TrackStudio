package com.trackstudio.tools.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.TSPropertyManager;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class PropertySetTag extends BodyTagSupport {
    protected volatile String value;             // tag attribute
    protected volatile boolean valueSpecified;   // status
    protected volatile String key;                 // tag attribute


    /**
     * Конструктор
     */
    public PropertySetTag() {
        super();
        init();
    }

    private void init() {
        value = key = null;

    }

    /**
     * Возвращает значение
     *
     * @return значение
     */
    public String getValue() {
        return value;
    }

    /**
     * Устанавливает значение
     *
     * @param value значение
     */
    public void setValue(String value) {
        this.value = value;
        this.valueSpecified = true;
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
     * @throws javax.servlet.jsp.JspException при необходимости
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
        try {
            if (key != null) TSPropertyManager.getInstance().set(key, value);
        } catch (GranException e) {

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

    public void setValueSpecified(boolean valueSpecified) {
        this.valueSpecified = valueSpecified;
    }
}
