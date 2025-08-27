package com.trackstudio.view;

import java.util.Iterator;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.Immutable;

@Immutable
public class UserViewHTMLSelect extends UserViewHTML {
    public UserViewHTMLSelect(SecuredUserBean task, String context) {
        super(task, context);
    }

    public UserView getView(SecuredUserBean t) {
        return new UserViewHTMLSelect(t, context);
    }

    public String getPathNoLink() throws GranException {
        return "<span class=\"user\">" + getDelimiter() + Null.stripNullHtml(HTMLEncoder.encode(user.getName())) + "</span>";

    }

    public String getPath() throws GranException {
        if (null != user)
            if (user.isOnSight()) {
                return " <a class=\"user\" href=\"" + context + "?id=" + user.getId() + "\">" + getDelimiter() + Null.stripNullHtml(HTMLEncoder.encode(user.getName())) + "</a>";
            } else
                return getPathNoLink();
        else
            return "";
    }

    public String getFullPath() throws GranException {
        if (null != user) {
            if (user.isOnSight()) {
                StringBuffer bf = new StringBuffer();
                Iterator it = KernelManager.getUser().getUserIdChain(null, user.getId()).iterator();
                if (it.hasNext()) {
                    SecuredUserBean usr1 = new SecuredUserBean((String) it.next(), user.getSecure());
                    if (usr1.hasChildren())
                        bf.append(getView(usr1).getPath());
                    else
                        bf.append(new UserViewHTMLSelect(usr1, context).getPathNoLink());
                }
                while (it.hasNext()) {
                    bf.append(getDelimiterSign());
                    SecuredUserBean usr1 = new SecuredUserBean((String) it.next(), user.getSecure());
                    if (usr1.hasChildren())
                        bf.append(getView(usr1).getPath());
                    else
                        bf.append(new UserViewHTMLSelect(usr1, context).getPathNoLink());
                }
                return "<div class=\"fullpath\">" + bf + "</div>";
            } else
                return "<div class=\"fullpath\">" + getPath() + "</div>";
        } else
            return "";
    }

    public String getDelimiter() throws GranException {
        String img = "";

        if (isUserActive())
            img = "arw.usr.a.gif";
        else
            img = "arw.usr.gif";

        String imageServlet = "/ImageServlet/" + GeneralAction.SERVLET_KEY;
        return "<img alt=\"\" src=\"" + imageServlet + "/cssimages/" + img + "\" border=\"0\" hspace=0 vspace=0>";
    }


}
