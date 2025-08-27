package com.trackstudio.tools.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.app.session.SessionManager;
import com.trackstudio.exception.GranException;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class GetTag extends TagSupport {
     // Ключ
    protected volatile String key = null;
    protected volatile String session = null;

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Обрабатывает первое вхождение тега
     *
     * @return код, что делать дальше
     * @throws javax.servlet.jsp.JspException при необходимости
     */
    public int doStartTag() throws JspException {
        Object result = null;
        try {
            if (session != null) {
                SessionContext sc = SessionManager.getInstance().getSessionContext(session);
                if (key != null && sc != null) {
                    result = sc.getAttribute(key);
                    pageContext.getOut().print(result);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (GranException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return SKIP_BODY;
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
     * Срабатывает при завершении работы с тегом
     */
    public void release() {

        key = null;
        session = null;
        super.release();
    }
}
