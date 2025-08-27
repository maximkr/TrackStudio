package com.trackstudio.view;

import java.util.Collection;
import java.util.Iterator;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public abstract class UserView {
    public static final String PATH_DELIMITER = " &gt ";
    protected final SecuredUserBean user;
    private volatile boolean isNew = false;
    private volatile String newUserName;

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public String getNewUserName() {
        return newUserName;
    }

    public void setNewUserName(String newUserName) {
        this.newUserName = newUserName;
    }

    public abstract String getName() throws GranException;

    public abstract String getReferencedUsers(String udfName, Collection users) throws GranException;

    public String getCutName(String context) throws GranException {
        if (user != null) {
            if (user.isOnSight()) {
                return "<a style=\"color: #999999\" href=\"" +
                        context + "/user/" + user.getLogin() + "?thisframe=true" +
                        "\">" + Null.stripNullHtml(HTMLEncoder.encode(user.getName())) + "</a>";
            } else return Null.stripNullHtml(HTMLEncoder.encode(user.getName()));
        }
        return "";
    }

    public abstract String getPath() throws GranException;

    public String getDelimiterSign() {

        return PATH_DELIMITER;
    }

    public String getFullPath() throws GranException {
        if (null != user) {
            if (user.isOnSight()) {
                StringBuffer bf = new StringBuffer();
                Iterator it = KernelManager.getUser().getUserIdChain(null, user.getId()).iterator();
                if (it.hasNext()) {
                    SecuredUserBean usr1 = new SecuredUserBean((String) it.next(), user.getSecure());
                    bf.append(getView(usr1).getPath());
                }
                while (it.hasNext()) {
                    bf.append(getDelimiterSign());
                    SecuredUserBean usr1 = new SecuredUserBean((String) it.next(), user.getSecure());
                    bf.append(getView(usr1).getPath());
                }
                if (isNew) {
                    bf.append(getDelimiterSign());
                    bf.append(newUserName);
                }
                return bf.toString();
            } else return getPath();
        } else return "";
    }

    public String getRelativePath(String from) throws GranException {
        if (null != user) {
            if (user.isOnSight()) {
                StringBuffer bf = new StringBuffer();
                Iterator it = AdapterManager.getInstance().getSecuredUserAdapterManager().getUserChain(user.getSecure(), user.getId(), from).iterator();
                if (it.hasNext()) {
                    SecuredUserBean usr1 = (SecuredUserBean) it.next();
                    bf.append(getView(usr1).getPath());
                }
                while (it.hasNext()) {
                    bf.append(getDelimiterSign());
                    SecuredUserBean usr1 = (SecuredUserBean) it.next();
                    bf.append(getView(usr1).getPath());
                }
                if (isNew) {
                    bf.append(getDelimiterSign());
                    bf.append(newUserName);
                }
                return bf.toString();
            } else return getPath();
        } else return "";
    }

    public abstract UserView getView(SecuredUserBean t);

    public abstract UDFValueView getUDFValueView(SecuredUDFValueBean bean);

    public abstract String getLogin() throws GranException;

    public abstract String getPrstatus() throws GranException;

    public abstract String getTel() throws GranException;

    public abstract String getEmail() throws GranException;

    public abstract String getCompany() throws GranException;

    public abstract String getDefaultProject() throws GranException;

    public abstract String getExpireDate() throws GranException;

    public abstract String getLocale() throws GranException;

    public abstract String getTimezone() throws GranException;

    protected UserView(SecuredUserBean user) {
        this.user = user;
    }

}
