package com.trackstudio.view;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.formatter.DateFormatter;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.Immutable;

@Immutable
public class UserViewText extends UserView {

    public static final String PATH_DELIMITER = " > ";

    public String getDelimiterSign() {

        return PATH_DELIMITER;
    }

    public UserViewText(SecuredUserBean task) {
        super(task);
    }


    public String getName() {
        if (null != user)
            return user.getName();
        else return "";
    }

    public String getPath() throws GranException {

        return getName();
    }

    public UserView getView(SecuredUserBean t) {
        return new UserViewText(t);
    }

    public String getLogin() throws GranException {
        if (null != user)
            return user.getLogin();
        else return "";
    }

    public String getPrstatus() throws GranException {
        return user.getPrstatus().getName();
    }

    public String getTel() throws GranException {
        return Null.stripNullText(user.getTel());
    }

    public String getEmail() throws GranException {
        return Null.stripNullText(user.getEmail());
    }

    public String getCompany() throws GranException {
        return Null.stripNullText(user.getCompany());
    }

    protected String getEncodedTaskName() throws GranException {
        HTMLEncoder sb = new HTMLEncoder(user.getDefaultProject().getName());
        sb.replace("\r\n", " ");
        sb.replace("\n", " ");
        return sb.toString();

    }

    public String getDefaultProject() throws GranException {
        return user.getDefaultProjectId() != null && user.getSecure().taskOnSight(user.getDefaultProjectId()) ? getEncodedTaskName() : "";
    }

    public String getExpireDate() throws GranException {
        return Null.stripNullText(user.getSecure().getUser().getDateFormatter().parse(user.getExpireDate()));
    }

    public String getLocale() throws GranException {
        Locale locale = DateFormatter.toLocale(user.getLocale());
        return locale != null ? locale.getDisplayName(DateFormatter.toLocale(user.getSecure().getLocale())) : "";
    }

    public UDFValueView getUDFValueView(SecuredUDFValueBean bean) {
        return new UDFValueViewReport(bean);
    }

    public String getTimezone() throws GranException {
        return Null.stripNullText(user.getTimezone());

    }

    public String getReferencedUsers(String udfName, Collection users) throws GranException {
        String ret = "";

        for (Iterator it = users.iterator(); it.hasNext();) {
            SecuredUserBean t = (SecuredUserBean) it.next();
            UserView v = new UserViewText(t);
            if (ret.length() > 0)
                ret += "\n";
            ret += udfName + " = ";
            if (t.isOnSight())
                ret += v.getFullPath();
            else
                ret += v.getName();
        }
        return ret;
    }
}
