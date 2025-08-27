package com.trackstudio.tools.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.jcip.annotations.ThreadSafe;

/**
 * Класс кастомного JSTL-тега, описывающий тег < ts:jsLink >.
 * <br/>
 * Используется для задания  пути к файлу js, используется только внутри тега < js:css >
 */
@ThreadSafe
public class JSLinkTag extends BodyTagSupport {
    /**
     * Ссылка
     */
    private volatile String link;

    /**
     * Устанавливает ссылку
     *
     * @param link ссылка
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Обрабатывает первое вхождение тега
     *
     * @return код, что делать дальше
     * @throws JspException при необходимости
     */
    public int doStartTag() throws JspException {
        JSTag parentTag = (JSTag) findAncestorWithClass(this, JSTag.class);
        if (parentTag == null) {
            throw new JspException("Tag should be nested in JSTag Tag");
        }
        parentTag.files.add(link);
        return SKIP_BODY;
    }
}