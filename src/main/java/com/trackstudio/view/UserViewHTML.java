package com.trackstudio.view;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Locale;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.formatter.DateFormatter;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class UserViewHTML extends UserView {
    protected final String context;
    protected volatile Boolean userActive;

    protected String nobr(String s) {
        String s2 = Null.stripNullText(s);
        if (s2.length() > 0) {
            return s2;
        } else return "";
    }

    public UserViewHTML(SecuredUserBean bean, String context) {
        super(bean);
        this.context = context;

    }

    public String getReferencedUsers(String udfName, Collection users) throws GranException {
        String value;
        String r = "<table class=\"udf\">";
        r += "<caption>" + udfName + "</caption>";
        for (SecuredUserBean t : (Collection<SecuredUserBean>) users) {
            UserView v = new UserViewHTML(t, context);
            r += "<tr><td>" + v.getPath() + "</td></tr>";
        }
        value = r + "</table>";
        return value;
    }

    public String getPermLink() throws GranException {
        String name = user.getLogin();
        try {
            name = URLEncoder.encode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {/*empty*/}

        return Config.getInstance().getSiteURL() + "/user/" + name;
    }

    public String getName() throws GranException {
        if (null != user) {
            return nobr(HTMLEncoder.encode(user.getName()));
        } else return "";
    }

    protected boolean isUserActive() throws GranException {
        if (userActive == null && user != null) {
            userActive = user.isActive() && KernelManager.getUser().getUserExpireDate(user.getId()) > System.currentTimeMillis();
        }
        return userActive != null && userActive;
    }

    public String getDelimiter() throws GranException {
        String img;
        if (isUserActive())
            img = "arw.usr.a.gif";
        else
            img = "arw.usr.gif";
        String imageServlet = "/ImageServlet/" + GeneralAction.SERVLET_KEY;
        return "<img alt=\"\" class=\"icon\" src=\"" + context + imageServlet + "/cssimages/" + img + "\" border=\"0\" hspace=\"0\" vspace=\"0\">";
    }

    public String getStatusDelimiter() throws GranException {
        String imageServlet = "/ImageServlet/" + GeneralAction.SERVLET_KEY;
        return "<img alt=\"\" src=\"" + context + imageServlet + "/cssimages/ico.status.gif\" border=\"0\" hspace=\"0\" vspace=\"0\">";
    }

    public String getPath() throws GranException {
        if (user != null) {
            if (user.isOnSight()) {
                return " <a class=\"internal user\" title=\""+user.getLogin()+"\" href=\"" +
                        context + "/user/" + user.getLogin() + "?thisframe=true" +
                        "\">" + getDelimiter() + Null.stripNullHtml(HTMLEncoder.encode(user.getName())) + "</a>";
            } else {
                return "<span class=\"user\">" + getDelimiter() + Null.stripNullHtml(HTMLEncoder.encode(user.getName())) + "</span>";
            }
        }
        return "";
    }

    public UserView getView(SecuredUserBean t) {
        return new UserViewHTML(t, context);
    }

    public UDFValueView getUDFValueView(SecuredUDFValueBean bean) {
        return new UDFValueViewHTML(bean, context);
    }

    public String getLogin() throws GranException {
        if (null != user) {
            return nobr(HTMLEncoder.encode(user.getLogin()));
        } else return "";
    }


    public String getPrstatus() throws GranException {
        return getStatusView().getName();
    }

    public PrstatusViewHTML getStatusView() throws GranException {
        return new PrstatusViewHTML(user.getPrstatus(), context);
    }

    public String getTel() throws GranException {
        return nobr(HTMLEncoder.encode(user.getTel()));
    }

    public String getEmail() throws GranException {
        if (null == user.getEmail() || 0 == user.getEmail().length())
            return "";
        else
            return "<a href=\"mailto:" + user.getEmail() + "\">" + user.getEmail() + "</a>";
    }


    public String getCompany() throws GranException {
        return nobr(HTMLEncoder.encode(user.getCompany()));
    }

    public String getDefaultProject() throws GranException {
        return user.getDefaultProjectId() != null && user.getSecure().taskOnSight(user.getDefaultProjectId()) ? new TaskViewHTML(user.getDefaultProject(), context).getFullPath() : "";
    }

    public String getExpireDate() throws GranException {
        return nobr(user.getSecure().getUser().getDateFormatter().parse(user.getExpireDate()));
    }

    public String getActive() throws GranException {
        String imageServlet = "/ImageServlet/" + GeneralAction.SERVLET_KEY;
        return user.isActive() ? "<img alt=\"\" src=\"" + context + imageServlet + "/cssimages/ico.checked.gif\" border=\"0\" hspace=0 vspace=0>&nbsp;" : "<img src=\"" + context + imageServlet + "/cssimages/ico.unchecked.gif\" border=\"0\" hspace=0 vspace=0>&nbsp;";
    }

    public String getLocale() throws GranException {
        Locale locale = DateFormatter.toLocale(user.getLocale());
        String flag = locale != null ? locale.getCountry().toLowerCase(Locale.ENGLISH) : "";
        if (flag.length() == 0)
            flag = "-";
        String imageServlet = "/ImageServlet/" + GeneralAction.SERVLET_KEY;
        return locale != null ? "<img alt=\"\" src=\"" + context + imageServlet + "/style/flags/" + flag + ".gif\">&nbsp;" + locale.getDisplayName(DateFormatter.toLocale(user.getSecure().getLocale())) : "";
    }

    public String getTimezone() throws GranException {
        return nobr(user.getTimezone());
    }

    public String getFullPath() throws GranException {
        return "<div class=\"fullpath\">" + super.getFullPath() + "</div>";
    }

    public String getFullPathNoDiv() throws GranException {
        return super.getFullPath();
    }

    public String getgetRelativePath(String from) throws GranException {
        return "<div class=\"fullpath\">" + super.getRelativePath(from) + "</div>";
    }

}
