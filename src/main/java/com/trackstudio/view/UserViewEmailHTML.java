package com.trackstudio.view;

import java.util.Locale;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.formatter.DateFormatter;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.Immutable;

@Immutable
public class UserViewEmailHTML extends UserViewHTML {


    public UserViewEmailHTML(SecuredUserBean task) {

        super(task, Config.getInstance().getSiteURL());
    }

//    public String getName () {
//        if (user != null){
//              if (user.isAllowed() &&Null.isNotNull(user.getEmail()))
//                      return "<a href='mailto:" + user.getEmail() + "'>" + HTMLEncoder.encode(user.getName()) + "</a>";
//              else
//                      return HTMLEncoder.encode(user.getName());
//        } else
//            return "";
//    }

    public String getName() {
        if (user != null) {
            return Null.isNotNull(user.getEmail()) ? "<a href='mailto:" + user.getEmail() + "'>" + HTMLEncoder.encode(user.getName()) + "</a>" : HTMLEncoder.encode(user.getName());
        } else
            return "";
    }


    public String getPath() throws GranException {

        if (user != null) {
            if (user.isAllowedByACL() && Null.isNotNull(user.getEmail())) {
                return "<a href='mailto:" + user.getEmail() + "'>" + Null.stripNullHtml(HTMLEncoder.encode(user.getName())) + "</a> ";
            } else
                return  Null.stripNullHtml(HTMLEncoder.encode(user.getName()));
        }

        return "";
    }

    public UserView getView(SecuredUserBean t) {
        return new UserViewEmailHTML(t);
    }

    public String getLogin() throws GranException {
        if (null != user) {
            if (Null.isNotNull(user.getEmail()))
                return "<a href='mailto:" + user.getEmail() + "'>" + Null.stripNullHtml(HTMLEncoder.encode(user.getLogin())) + "</a>";
            else {
                return Null.stripNullHtml(HTMLEncoder.encode(user.getLogin()));
            }
        } else
            return "";
    }

    public UDFValueView getUDFValueView(SecuredUDFValueBean bean) {
        return new UDFValueViewEmailHTML(bean);
    }

    public String getLocale() throws GranException {
        Locale locale = DateFormatter.toLocale(user.getLocale());
        return locale != null ? locale.getDisplayName(DateFormatter.toLocale(user.getSecure().getLocale())) : "";
    }
}
