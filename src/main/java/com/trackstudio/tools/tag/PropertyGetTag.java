package com.trackstudio.tools.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.TSPropertyManager;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class PropertyGetTag extends TagSupport {
    protected volatile String key = null;

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
        try {
            pageContext.getOut().print(TSPropertyManager.getInstance().get(key));
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
        super.release();
    }
}
