package com.trackstudio.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.concurrent.ConcurrentReaderHashMap;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.UserFValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.formatter.DateFormatter;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class UserListView {
    private static Log log = LogFactory.getLog(UserListView.class);
    protected final String context;
    protected String scheme;
    protected final SecuredUserBean user;
    protected volatile boolean userDelete;
    protected volatile Locale locale;
    protected volatile boolean userUDFView;
    protected volatile boolean subusersView = true;
    protected volatile UserFValue flthm;
    protected volatile String filterId;
    protected volatile CopyOnWriteArrayList udfcol = null;

    public static class UserListItem {
        private volatile String id;
        private volatile String name;
        private volatile String fullPath;
        private volatile String childrenAllowed;
        private volatile String childrenCount;
        private volatile String company;
        private volatile String login;
        private volatile String expireDate;
        private volatile String active;
        private volatile String tel;
        private volatile String email;
        private volatile String link;
        private volatile String locale;
        private volatile String timezone;
        private volatile String status;
        private volatile String parent;
        private volatile boolean parentActive;
        private volatile String template;
        private volatile Boolean allowed;
        public ConcurrentReaderHashMap udfs = new ConcurrentReaderHashMap();

        public UserListItem(String id) {
            this.id = id;
        }

        public boolean isParentActive() {
            return parentActive;
        }

        public void setParentActive(boolean parentActive) {
            this.parentActive = parentActive;
        }

        public String getParent() {
            return parent;
        }

        public void setParent(String parent) {
            this.parent = parent;
        }

        public String getTemplate() {
            return template;
        }

        public void setTemplate(String template) {
            this.template = template;
        }

        public String getUdf(String id) {
            Object o = udfs.get(id);
            if (o != null) return o.toString();
            else return null;
        }

        public void setUdf(String id, String value) {
            this.udfs.put(id, value);
        }

        public String getFullPath() {
            return fullPath;
        }

        public void setFullPath(String fullPath) {
            this.fullPath = fullPath;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public Boolean isAllowed() {
            return allowed;
        }

        public void setAllowed(Boolean allowed) {
            this.allowed = allowed;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getExpireDate() {
            return expireDate;
        }

        public void setExpireDate(String expireDate) {
            this.expireDate = expireDate;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }


        public String getLocale() {
            return locale;
        }

        public void setLocale(String locale) {
            this.locale = locale;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getActive() {
            return active;
        }

        public void setActive(String active) {
            this.active = active;
        }

        public String getChildrenAllowed() {
            return childrenAllowed;
        }

        public void setChildrenAllowed(String childrenAllowed) {
            this.childrenAllowed = childrenAllowed;
        }

        public String getChildrenCount() {
            return childrenCount;
        }

        public void setChildrenCount(String childrenCount) {
            this.childrenCount = childrenCount;
        }

        public Boolean getAllowed() {
            return allowed;
        }

        public Map getUdfs() {
            return udfs;
        }

        public void setUdfs(Map udfs) {
            this.udfs = new ConcurrentReaderHashMap(udfs);
        }


        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }

    class UserViewInner extends UserViewHTMLLinked {

        public UserViewInner(SecuredUserBean bean, String context, String scheme) {
            super(bean, context);
        }

        public UserView getView(SecuredUserBean t) {
            return new UserViewInner(t, context, scheme);
        }

        public String getName() throws GranException {
            if (user != null) {
                if (user.isOnSight()) {
                    return "<a href=\"" +
                            context + "/user/" + user.getLogin() + "?thisframe=true" +
                            "\">" + Null.stripNullHtml(HTMLEncoder.encode(user.getName())) + "</a>";
                } else return Null.stripNullHtml(HTMLEncoder.encode(user.getName()));
            }
            return "";
        }

        public String getLogin() throws GranException {
            if (null != user) {
                if (user.isOnSight())
                    return "<a href=\"" +
                            context + "/user/" + user.getLogin() + "?thisframe=true" +
                            "\">" + Null.stripNullHtml(HTMLEncoder.encode(user.getLogin())) + "</a>";
                else return Null.stripNullHtml(HTMLEncoder.encode(user.getLogin()));
            } else
                return "";
        }

        public String getPath() throws GranException {
            if (user != null) {
                if (user.isOnSight()) {
                    return "<a class=\"user\" href=\"" +
                            context + "/user/" + user.getLogin() + "?thisframe=true" +
                            "\">" + getDelimiter() + Null.stripNullHtml(HTMLEncoder.encode(user.getName())) + "</a>";
                } else {
                    return "<span class=\"user\">" + getDelimiter() + Null.stripNullHtml(HTMLEncoder.encode(user.getName())) + "</span>";
                }
            }
            return "";
        }

        private String getPathForCutted() throws GranException {
            return " <a style=\"color: #999999\" class=\"user\" href=\"" +
                    context + "/user/" + user.getLogin() + "?thisframe=true\">" + getDelimiter() +
                    HTMLEncoder.encode(user.getName()) + "</a>";

        }

    }

    protected SessionContext getSecure() {
        return user.getSecure();
    }

    //public boolean isShowDeleteFlag() {
    //    return showDeleteFlag;
    //}

    protected void initParams(String filterId) throws GranException {
        try {
            this.filterId = filterId;
            this.flthm = AdapterManager.getInstance().getSecuredFilterAdapterManager().getUserFValue(user.getSecure(), filterId).getFValue();

            this.locale = DateFormatter.toLocale(user.getSecure().getLocale());
            this.userDelete = getSecure().canAction(Action.deleteUser, user.getId());
            this.userUDFView = flthm.needFilterUDF() && !user.getUDFValuesList().isEmpty();
            this.subusersView = true;
        } catch (Exception e) {
            throw new GranException(e);
        }
    }

    public UserListView(SecuredUserBean user, String filter, String context) throws GranException {
        this.user = user;
        this.context = context;

        initParams(filter);
    }

    public UserListView(SecuredUserBean user, String filter, ArrayList list, String context) throws GranException {
        this(user, filter, context);
        initParams(filter);
    }

    private List getUdfcol() throws GranException {
        if (udfcol == null)
            udfcol = new CopyOnWriteArrayList(Null.removeNullElementsFromList(user.getUDFValuesList()));
        return udfcol;
    }


    private String th(String s) {
        return "<th>" + s + "</th>";
    }

    private String shortTh(String s) {
        return "<th width='2%'>" + s + "</th>";
    }


    private String tdNoWrap(Object s) {
        return "<td style='white-space: nowrap'>" + s + "</td>";
    }

    public String getHeaderLine(String contextPath, String order) throws GranException {
        StringBuffer ret = null;
        try {
            ret = new StringBuffer(1024);
            ret.append("<tr class=\"wide\">");
            List view = flthm.getView();
            ret.append(addDeleteHeader());
            if (view.contains(FieldMap.USER_LOGIN.getFilterKey()))
                ret.append(shortTh(getHeaderLink(I18n.getString(getSecure().getLocale(), "LOGIN"), FieldMap.USER_LOGIN.getFieldKey(), order)));

            if (view.contains(FieldMap.USER_NAME.getFilterKey()))
                ret.append(th(getHeaderLink(I18n.getString(getSecure().getLocale(), "NAME"), FieldMap.USER_NAME.getFieldKey(), order)));
            if (view.contains(FieldMap.FULLPATH.getFilterKey()))
                ret.append(th(getHeaderLink(I18n.getString(getSecure().getLocale(), "FULL_PATH"), FieldMap.FULLPATH.getFieldKey(), order)));
            if (view.contains(FieldMap.USER_COMPANY.getFilterKey()))
                ret.append(shortTh(getHeaderLink(I18n.getString(getSecure().getLocale(), "COMPANY"), FieldMap.USER_COMPANY.getFieldKey(), order)));
            if (view.contains(FieldMap.USER_STATUS.getFilterKey()))
                ret.append(shortTh(getHeaderLink(I18n.getString(getSecure().getLocale(), "PRSTATUS"), FieldMap.USER_STATUS.getFieldKey(), order)));
            if (view.contains(FieldMap.USER_EMAIL.getFilterKey()))
                ret.append(shortTh(getHeaderLink(I18n.getString(getSecure().getLocale(), "EMAIL"), FieldMap.USER_EMAIL.getFieldKey(), order)));
            if (view.contains(FieldMap.USER_TEL.getFilterKey()))
                ret.append(shortTh(getHeaderLink(I18n.getString(getSecure().getLocale(), "PHONE"), FieldMap.USER_TEL.getFieldKey(), order)));
            if (view.contains(FieldMap.USER_LOCALE.getFilterKey()))
                ret.append(shortTh(getHeaderLink(I18n.getString(getSecure().getLocale(), "LOCALE"), FieldMap.USER_LOCALE.getFieldKey(), order)));
            if (view.contains(FieldMap.USER_TIMEZONE.getFilterKey()))
                ret.append(shortTh(getHeaderLink(I18n.getString(getSecure().getLocale(), "TIME_ZONE"), FieldMap.USER_TIMEZONE.getFieldKey(), order)));
            if (view.contains(FieldMap.USER_EXPIREDATE.getFilterKey()))
                ret.append(shortTh(getHeaderLink(I18n.getString(getSecure().getLocale(), "EXPIRE_DATE"), FieldMap.USER_EXPIREDATE.getFieldKey(), order)));
            if (view.contains(FieldMap.USER_ACTIVE.getFilterKey()))
                ret.append(shortTh(getHeaderLink(I18n.getString(getSecure().getLocale(), "ACTIVE"), FieldMap.USER_ACTIVE.getFieldKey(), order)));
            if (view.contains(FieldMap.USER_CHILDALLOWED.getFilterKey()))
                ret.append(shortTh(getHeaderLink(I18n.getString(getSecure().getLocale(), "USERS_ALLOWED"), FieldMap.USER_CHILDALLOWED.getFieldKey(), order)));
            if (view.contains(FieldMap.USER_CHILDCOUNT.getFilterKey()))
                ret.append(shortTh(getHeaderLink(I18n.getString(getSecure().getLocale(), "SUBORDINATED_USERS_AMOUNT"), FieldMap.USER_CHILDCOUNT.getFieldKey(), order)));
            if (view.contains(FieldMap.USER_PARENT.getFilterKey()))
                ret.append(th(getHeaderLink(I18n.getString(getSecure().getLocale(), "USER_PARENT"), FieldMap.USER_PARENT.getFieldKey(), order)));
            if (view.contains(FieldMap.USER_TEMPLATE.getFilterKey()))
                ret.append(th(getHeaderLink(I18n.getString(getSecure().getLocale(), "USER_TEMPLATE"), FieldMap.USER_TEMPLATE.getFieldKey(), order)));
            if (userUDFView) {
                for (Object o : getUdfcol()) {
                    SecuredUDFValueBean udf = (SecuredUDFValueBean) o;
                    if (view.contains(FValue.UDF + udf.getId()))
                        ret.append(shortTh(getHeaderLink(HTMLEncoder.encode(udf.getCaption()), FValue.UDF_SORT + udf.getId(), order)));
                }
            }

            ret.append("</tr>\n");

        } catch (Exception e) {
            throw new GranException(e);
        }

        return ret.toString();
    }


    /**
     * returns Task Line for subtasks page and filter mailing
     */
    public UserListItem getLine(SecuredUserBean sub) throws GranException {
        try {

            UserViewInner userView = new UserViewInner(sub, context, scheme);
            UserListItem li = new UserListItem(sub.getId());
            li.setAllowed(sub.canManage());
            li.setLink(sub.getLogin());
            List view = flthm.getView();


            if (view.contains(FieldMap.USER_LOGIN.getFilterKey()))
                li.setLogin(userView.getLogin());
            if (view.contains(FieldMap.USER_NAME.getFilterKey())) {
                String ids = (String) getSecure().getAttribute("USERS");
                boolean userCutted = false;
                if (ids != null)
                    for (String sid : ids.split(UdfConstants.SPLIT_SEPARATOR)) 
                        if (sub.getId().equals(sid)) {
                            userCutted = true;
                            break;
                        }
                if (userCutted)
                    li.setName(userView.getPathForCutted());
                else
                    li.setName(userView.getPath());
            }
            if (view.contains(FieldMap.FULLPATH.getFilterKey())) {
                li.setFullPath(userView.getRelativePath(user.getId()));
            }

            if (view.contains(FieldMap.USER_COMPANY.getFilterKey()))
                li.setCompany(userView.getCompany());
            if (view.contains(FieldMap.USER_STATUS.getFilterKey()))
                li.setStatus(userView.getPrstatus());
            if (view.contains(FieldMap.USER_EMAIL.getFilterKey()))
                li.setEmail(userView.getEmail());
            if (view.contains(FieldMap.USER_TEL.getFilterKey()))
                li.setTel(userView.getTel());
            if (view.contains(FieldMap.USER_LOCALE.getFilterKey()))
                li.setLocale(userView.getLocale());
            if (view.contains(FieldMap.USER_TIMEZONE.getFilterKey()))
                li.setTimezone(userView.getTimezone());
            if (view.contains(FieldMap.USER_EXPIREDATE.getFilterKey()))
                li.setExpireDate(userView.getExpireDate());
            if (view.contains(FieldMap.USER_ACTIVE.getFilterKey()))
                li.setActive(userView.getActive());
            if (view.contains(FieldMap.USER_CHILDALLOWED.getFilterKey()))
                li.setChildrenAllowed(Null.stripNullHtml(sub.getChildrenAllowed()));
            if (view.contains(FieldMap.USER_CHILDCOUNT.getFilterKey()))
                li.setChildrenCount(Null.stripNullHtml(sub.getChildCount()));

            li.setParentActive(sub.getParent().isActive());
            if (view.contains(FieldMap.USER_PARENT.getFilterKey()))
                li.setParent(new UserViewInner(sub.getParent(), context, scheme).getLogin());
            if (view.contains(FieldMap.USER_LOGIN.getFilterKey()))
                li.setTemplate(sub.getTemplate());
            if (userUDFView) {
                for (Object o : getUdfcol()) {
                    SecuredUDFValueBean udf = (SecuredUDFValueBean) o;
                    ArrayList udfValues = sub.getUDFValuesList();
                    if (view.contains(FValue.UDF + udf.getId())) {
                        int pos = udfValues.indexOf(udf);
                        log.debug("POS = " + pos);

                        if (pos > -1) {
                            SecuredUDFValueBean uval = (SecuredUDFValueBean) udfValues.get(pos);
                            li.setUdf(udf.getId(), userView.getUDFValueView(uval).getValue(sub));
                        }

                    }
                }
            }
            return li;
        } catch (Exception e) {
            throw new GranException(e);
        }
    }

    private String getHeaderLink(String name, String order, String curorder) throws GranException {
        return "<a class=\"header\" href=\"" +
                context + "/UserListAction.do?method=page&amp;id=" + user.getId() + "" +
                "&sliderOrder=" + (curorder != null && ('_' + order).equals(curorder) ? "" : "_") +
                order + "\">" + name + "</a>";
    }

    private String addDeleteHeader() throws GranException {
        return "<th width='1%' style=\"white-space:nowrap\">" + " <input type=\"checkbox\" onClick=\"selectAllCheckboxes(this, 'delete1')\">" + "</th>";
    }


}