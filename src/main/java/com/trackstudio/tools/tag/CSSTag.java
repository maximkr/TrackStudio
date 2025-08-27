package com.trackstudio.tools.tag;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.jcip.annotations.ThreadSafe;

/**
 * Класс кастомного JSTL-тега, описывающий тег < ts:css >.
 * <br/>
 * Используется для объединения нескольких css-файлов в один. Объединяемые файлы задаются через дочерние теги < ts:cssLink >
 */
@ThreadSafe
public class CSSTag extends BodyTagSupport {

    private static Log log = LogFactory.getLog(CSSTag.class);

    // Список ссылок на объединяемые файлы
    protected final CopyOnWriteArrayList<String> files = new CopyOnWriteArrayList<String>();
    /**
     * HTTP-запрос
     */
    protected volatile HttpServletRequest request;

    /**
     * Устанавливает HTTP-запрос
     *
     * @param request HTTP-запрос
     */
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Обрабатывает первое вхождение тега
     *
     * @return код, что делать дальше
     * @throws JspException при необходимости
     */
    public int doStartTag() throws JspException {
        files.clear();
        return EVAL_BODY_BUFFERED;
    }

    /**
     * Обрабатывает последнее вхождение тега
     *
     * @return код, что делать дальше
     * @throws JspException при необходимости
     */
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    /**
     * Обрабатывает тело тега
     *
     * @return код, что делать дальше
     * @throws JspTagException при необходимости
     */
    public int doAfterBody() throws JspTagException {
        try {
            String key = StoreCssJs.getInstance().appendFile(files);
            BodyContent bodyContent = getBodyContent();
            if (bodyContent != null) {
                JspWriter writer = bodyContent.getEnclosingWriter();
                if (writer != null) {
                    writer.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + request.getContextPath() + "/CSSServlet/" + key + "\">");
                }
            }
        } catch (Exception ioe) {
            log.error("doAfterBody", ioe);
        }
        return SKIP_BODY;
    }

    /**
     * Срабатывает при завершении работы с тегом
     */
    public void release() {
        files.clear();
        request = null;
        super.release();
    }
}
