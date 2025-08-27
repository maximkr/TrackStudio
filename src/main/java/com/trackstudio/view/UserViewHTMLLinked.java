package com.trackstudio.view;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.Immutable;

@Immutable
public class UserViewHTMLLinked extends UserViewHTML {

    public UserViewHTMLLinked(SecuredUserBean bean, String context) {
        super(bean, context);
    }

    public String getName() throws GranException {
        if (user != null) {
            if (user.isOnSight()) {

                return "<a href=\"" + context + "/user/" + user.getLogin() + "?thisframe=true\">" + Null.stripNullHtml(HTMLEncoder.encode(user.getName())) + "</a>";
            } else return nobr(HTMLEncoder.encode(user.getName()));
        }
        return "";
    }

    public String getLogin() throws GranException {
        if (null != user) {
            if (user.isOnSight()) {
                return "<a href=\"" + context + "/user/" + user.getLogin() + "?thisframe=true\">" + Null.stripNullHtml(HTMLEncoder.encode(user.getLogin())) + "</a>";
            } else return nobr(HTMLEncoder.encode(user.getLogin()));
        } else
            return "";
    }

    public UserView getView(SecuredUserBean t) {
        return new UserViewHTMLLinked(t, context);
    }

    public UDFValueView getUDFValueView(SecuredUDFValueBean bean) {
        return new UDFValueViewHTML(bean, context);
    }
}
