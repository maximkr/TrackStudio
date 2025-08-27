package com.trackstudio.tools.tag;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.jcip.annotations.ThreadSafe;

/**
 * Класс кастомного JSTL-тега, описывающий тег < ts:js >.
 * <br/>
 * Используется для объединения нескольких js-файлов в один. Объединяемые файлы задаются через дочерние теги < ts:jsLink >
 */
@ThreadSafe
public class JSTag extends BodyTagSupport {

    private static Log log = LogFactory.getLog(JSTag.class);
    protected final CopyOnWriteArrayList<String> files = new CopyOnWriteArrayList<String>();
    /**
     * HTTP-запрос
     */
    HttpServletRequest request;

    HttpServletResponse response;

    /**
     * Устанавливает HTTP-запрос
     *
     * @param request HTTP-запрос
     */
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
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
            getBodyContent().getEnclosingWriter().println("<script type=\"text/javascript\" src=\"" + request.getContextPath() + "/JSServlet/" + key + "\"></script>");
        } catch (Exception ioe) {
            //ioe.printStackTrace();
            log.error("Exception ", ioe);
        }
        return SKIP_BODY;
    }

    /**
     * Срабатывает при завершении работы с тегом
     */
    public void release() {
        files.clear();
        request = null;
        response = null;
        super.release();
    }
}