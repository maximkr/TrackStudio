package com.trackstudio.view;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.secured.SecuredStatusBean;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.Immutable;

@Immutable
public class StateViewHTML extends StateView {
    private final String contextPath;

    public StateViewHTML(SecuredStatusBean st, String contextPath) {
        this.status = st;
        this.contextPath = contextPath;

    }

    public String getName() {
        if (status != null) return getColorPictureOnly() + "&nbsp;" + HTMLEncoder.encode(status.getName());
        else return null;
    }

    public String getColorPictureOnly() {
        String imageServlet = "/ImageServlet/" + GeneralAction.SERVLET_KEY;
        if (status != null)
            return "<div style=\"display:inline;white-space:nowrap;\"><img alt=\"\" style=\"background-color: "+ status.getColor() +"\" border=\"0\" src=\"" + contextPath + imageServlet + status.getImage()+"\">";
        else return "";
    }


}
