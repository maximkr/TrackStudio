package com.trackstudio.tools.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.jcip.annotations.ThreadSafe;

/**
 * Класс кастомного JSTL-тега, описывающий тег < ts:cssLink >.
 * <br/>
 * Используется для задания  пути к файлу css, используется только внутри тега < ts:css >
 */
@ThreadSafe
public class CSSLinkTag extends BodyTagSupport {
    /**
     * Ссылка
     */
    volatile String link;

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
        CSSTag parentTag = (CSSTag) findAncestorWithClass(this, CSSTag.class);
        if (parentTag == null) {
            throw new JspException("Tag should be nested in CSSTag Tag");
        }
        parentTag.files.add(link);
        return SKIP_BODY;
    }
}
