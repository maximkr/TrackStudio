package com.trackstudio.tools.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.trackstudio.secured.SecuredUserBean;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class HtmlUser extends BodyTagSupport {
    private volatile SecuredUserBean user;
    private volatile Boolean showRole = false;

    public void setShowRole(Boolean showRole) {
        this.showRole = showRole;
    }

    public void setUser(SecuredUserBean user) {
        this.user = user;
    }

    public int doStartTag() throws JspException {
        return EVAL_BODY_BUFFERED;
    }

    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    public int doAfterBody() throws JspTagException {
        StringBuffer sb = new StringBuffer();
        JspWriter out = getBodyContent().getEnclosingWriter();
        try {
            sb.append(user.getName());
            if (showRole) {
                sb.append(" [").append(user.getPrstatus().getName()).append("]");
            }
            out.print(sb.toString());
        } catch (Exception e) {
            throw new JspTagException(e.getMessage());
        }
        return SKIP_BODY;
    }
}
