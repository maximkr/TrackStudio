package com.trackstudio.view;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.Immutable;

@Immutable
public class PrstatusViewHTML {
    protected final SecuredPrstatusBean prstatus;
    protected final String context;

    public PrstatusViewHTML(SecuredPrstatusBean st, String contextPath) {
        this.prstatus = st;
        this.context = contextPath;

    }

    public String getDelimiter() throws GranException {
        String imageServlet = "/ImageServlet/" + GeneralAction.SERVLET_KEY;
        return "<img alt=\"\" src=\"" + context + imageServlet + "/cssimages/ico.status.gif\" border=\"0\" hspace=\"0\" vspace=\"0\"> ";
    }

    public String getName() throws GranException {
        if (prstatus.canView()) {
            return " <a class=\"user\" href=\"" +
                    context + "/UserStatusViewAction.do?method=page&amp;id=" + prstatus.getUserId() + "&amp;prstatusId=" + prstatus.getId() +
                    "\">" + getDelimiter() + HTMLEncoder.encode(prstatus.getName()) + "</a>";
        } else {
            return getDelimiter() + HTMLEncoder.encode(prstatus.getName());
        }
    }


}
