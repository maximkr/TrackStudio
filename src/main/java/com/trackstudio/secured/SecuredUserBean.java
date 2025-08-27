package com.trackstudio.secured;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

import com.trackstudio.app.UdfValue;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.AttachmentCacheItem;
import com.trackstudio.kernel.cache.MessageCacheItem;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UDFCacheItem;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.UdfManager;
import com.trackstudio.model.Template;
import com.trackstudio.soap.bean.UserBean;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.PasswordValidator;
import com.trackstudio.tools.PropertyContainer;
import com.trackstudio.tools.formatter.DateFormatter;

import net.jcip.annotations.ThreadSafe;

/**
 * Bean which represents user
 */
@ThreadSafe
public class SecuredUserBean extends AbstractBeanWithUdf {
    private final UserRelatedInfo user;
    protected final boolean allowedByACL;
    private final boolean onSight;
    private volatile Integer childCount = null;
    private volatile Integer totalChildrenCount = null;
    private final AtomicReference<SecuredPrstatusBean> prstatus = new AtomicReference<SecuredPrstatusBean>();
    private final AtomicReference<SecuredUserBean> manager = new AtomicReference<SecuredUserBean>();
    private final String preferences;
    private final AtomicReference<SecuredTaskBean> defaultProject = new AtomicReference<SecuredTaskBean>();
    private volatile ArrayList<SecuredUDFValueBean> udfValues = null;
    private volatile CopyOnWriteArraySet<String> allowedPrstatuses;

    public SecuredUserBean(UserRelatedInfo user, SessionContext sess) throws GranException {
        this.sc = sess;
        this.user = user;
        this.preferences = user.getPreferences();
        if (onSight = sess.userOnSight(user.getId())) {
            allowedByACL = sc.allowedByUser(user.getId());
        } else {
            allowedByACL = false;
        }
    }


    public UserRelatedInfo getUser() {
        return user;
    }

    public String getLogin() {
        return user.getLogin();
    }

    public String getPassword() {
        String pwd = user.getPassword();
        if (pwd == null) {
            return null;
        } else {
            return allowedByACL ? pwd.length() <= PasswordValidator.END_INDEX ? pwd : pwd.substring(0, PasswordValidator.END_INDEX) : null;
        }
    }

    public String getTel() throws GranException {
        if (user.getTel() != null)
            return sc.canAction(Action.viewUserPhone, getAllowedPrstatuses()) ? user.getTel() : null;
        else return null;
    }

    public String getEmail() {
        return user.getEmail();
    }

    public ArrayList<String> getEmailList() {
        return user.getEmailList();
    }

    public String getLocale() {
        return user.getLocale();
    }

    public String getTimezone() {
        return user.getTimezone();
    }

    public String getTimezoneAsString() {
        String lc = getLocale();
        if (lc == null)
            lc = Config.getInstance().getDefaultLocale();
        String timezone = getTimezone();
        if (timezone == null)
            timezone = Config.getInstance().getDefaultTimezone();
        String name = TimeZone.getTimeZone(timezone).getDisplayName(DateFormatter.toLocale(lc));
        return name + " (" + (timezone) + ")";
    }

    public String getLocaleAsString() {
        String lc = getLocale();
        if (lc == null) lc = Config.getInstance().getDefaultLocale();
        Locale l = DateFormatter.toLocale(lc);
        return l.getDisplayName(l);
    }

    public Calendar getUserExpireDate() throws GranException {
        return allowedByACL ? user.getExpireDate() : null;
    }

    public Calendar getExpireDate() throws GranException {
        return allowedByACL ? user.getExpireDate() : null;
    }

    public String getExpireDateAsString() throws GranException {
        return getSecure().getUser().getDateFormatter().parse(getExpireDate());
    }

    public boolean isExpired() throws GranException {
        return UserRelatedManager.getInstance().isExpired(user.getId());
    }

    public String getCompany() throws GranException {
        if (user.getCompany() != null)
            return sc.canAction(Action.viewUserCompany, getAllowedPrstatuses()) ? user.getCompany() : null;
        return null;
    }

    public Integer getChildrenAllowed() {
        return user.getChildAllowed();
    }

    public String getName() {
        return user.getName();
    }


    public String getId() {
        return user.getId();
    }


    public Integer getChildCount() throws GranException {
        if (!onSight)
            return null;

        if (childCount == null) {
            int count = 0;
            for (Object o : user.getChildren()) {
                String id = (String) o;
                if (sc.userOnSight(id))
                    count++;
            }
            childCount = count;
        }
        return childCount;
    }

    public Integer getTotalChildrenCount() throws GranException {
        if (!onSight)
            return null;

        if (totalChildrenCount == null) {
            int count = 0;
            for (String id : user.getDescendents()) {
                if (sc.userOnSight(id))
                    count++;
            }
            totalChildrenCount = count;
        }
        return totalChildrenCount;
    }

    protected PropertyContainer getContainer() {
            PropertyContainer pc = container.get();
            if (pc != null)
                return pc; // object in cache, return it

            PropertyContainer newPC = new PropertyContainer();
            newPC.put(getName()).put(getId());

            if (container.compareAndSet(null, newPC)) // try to update
                return newPC; // we can update - return loaded value
            else
                return container.get(); // some other thread already updated it - use saved value
    }

    public boolean hasChildren() throws GranException {
        return getChildCount() != null && getChildCount() != 0;
    }

    public SecuredPrstatusBean getPrstatus() throws GranException {
        SecuredPrstatusBean s = prstatus.get();
        if (s!=null)
            return s;

        SecuredPrstatusBean s1 = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, user.getPrstatusId());
        if (prstatus.compareAndSet(null, s1))
            return s1;
        else
            return prstatus.get();
    }

    public SecuredUserBean getManager() throws GranException {
        SecuredUserBean s = manager.get();
        if (s!=null)
            return s;

        if (user.getParentId() == null)
            return null;

        SecuredUserBean s1 = new SecuredUserBean(getManagerId(), sc);
        if (manager.compareAndSet(null, s1))
            return s1;
        else
            return manager.get();
    }

    public SecuredUserBean getParent() throws GranException {
        return getManager();
    }

    public String getTemplate() throws GranException {
        return user.getTemplate();

    }

    public String getPreferences() {
        return preferences;
    }

    public SecuredTaskBean getDefaultProject() throws GranException {
        SecuredTaskBean s = defaultProject.get();
        if (s!=null)
            return s;

        if (getDefaultProjectId()==null)
            return null;

        TaskRelatedInfo tri = TaskRelatedManager.getInstance().find(getDefaultProjectId());
        if (tri == null)
            return null;

        SecuredTaskBean s1 = new SecuredTaskBean(tri, sc);
        if (defaultProject.compareAndSet(null, s1))
            return s1;
        else
            return defaultProject.get();
    }

    public SecuredUserBean(String userId, SessionContext sess) throws GranException {
        this(UserRelatedManager.getInstance().find(userId), sess);
    }

    public Calendar getLastLogonDate() throws GranException {
        if (!allowedByACL) return null;
        return user.getLastLogonDate();
    }

    public Calendar getPasswordChangedDate() throws GranException {
        if (!allowedByACL) return null;
        return user.getPasswordChangedDate();
    }

    public ArrayList<SecuredUserBean> getChildren() throws GranException {
        if (!allowedByACL)
            return null;
        ArrayList<UserRelatedInfo> itemCollection = new ArrayList<UserRelatedInfo>();
        itemCollection.addAll(UserRelatedManager.getInstance().getItemCollection(user.getChildren()));
        ArrayList<SecuredUserBean> l = new ArrayList<SecuredUserBean>();
        for (UserRelatedInfo anItemCollection : itemCollection) {
            l.add(new SecuredUserBean(anItemCollection, getSecure()));
        }
        return l;
    }


    public ArrayList getChildrenIdOnly() throws GranException {
        if (!allowedByACL) return null;
        return user.getChildren();
    }

    public List<String> getAllowedChildrenIdOnly() throws GranException {
        List<String> result = new ArrayList<String>();
        if (this.canManage()) {
            result.addAll(user.getChildren());
        } else {
            List<String> allowed = user.getChildren();
            for (String key : allowed) {
                if (sc.userOnSight(key)) {
                    result.add(key);
                }
            }
        }
        return result;
    }

    public List<String> getAllowedUserAndChildrenListIdOnly() throws GranException {
        List<String> result = new ArrayList<String>();
        List<String> allowed = KernelManager.getUser().getUserAndChildrenListIdOnly(user.getId());
        for (String key : allowed) {
            if (sc.userOnSight(key)) {
                result.add(key);
            }
        }
        return result;
    }

    public ArrayList<SecuredUDFBean> getUDFs() throws GranException {
        ArrayList<UDFCacheItem> a = user.getUDFCacheItems();
        ArrayList<SecuredUDFBean> c = new ArrayList<SecuredUDFBean>();
        if (a != null) {
            for (UDFCacheItem u : a) {
                c.add(new SecuredUserUDFBean(u, sc));
            }
        }
        return c;
    }

    public ArrayList<SecuredUDFValueBean> getUDFValuesForNewUser() throws GranException {
        ArrayList<SecuredUDFValueBean> udfValuesList = new ArrayList<SecuredUDFValueBean>();
        if (allowedByACL) {
            ArrayList<UDFCacheItem> old = user.getUDFCacheItems();
            ArrayList<UdfValue> map = new ArrayList<UdfValue>();
            for (UDFCacheItem udf : old) {
                map.add(new UdfValue(udf));
            }
            for (UdfValue u : map) {
                if (KernelManager.getUdf().isUserUdfViewable(user.getId(), sc.getUserId(), u.getUdfId()))
                    udfValuesList.add(new SecuredUDFValueBean(u, this));
            }
        }
        return udfValuesList;
    }

    public HashMap<String, SecuredUDFValueBean> getUDFValues() throws GranException {
        HashMap<String, SecuredUDFValueBean> ret = new HashMap<String, SecuredUDFValueBean>();
        for (SecuredUDFValueBean securedUDFValueBean : getUDFValuesList()) {
            ret.put(securedUDFValueBean.getUdfId(), securedUDFValueBean);
        }
        return ret;
    }

    public ArrayList<SecuredUDFValueBean> getUDFValuesList() throws GranException {
        if (udfValues == null) {
            ArrayList<SecuredUDFValueBean> localUdfValues = new ArrayList<SecuredUDFValueBean>();
            if (allowedByACL) {
                List<UdfValue> h = user.getUDFValues();
                for (UdfValue aH : h) {
                    SecuredUDFValueBean sb = new SecuredUDFValueBean(aH, this);
                    if (KernelManager.getUdf().isUserUdfViewable(user.getId(), sc.getUserId(), sb.getUdfId()))
                        localUdfValues.add(sb);
                }
                Collections.sort(localUdfValues);
            }
            this.udfValues = localUdfValues;
        }
        return (ArrayList<SecuredUDFValueBean>)udfValues.clone();
    }

    public ArrayList<SecuredUDFValueBean> getFilteredUDFValues() throws GranException {
        return getUDFValuesList();
    }


    public String toString() {
        return getId() + ' ' + getName();
    }

    public ArrayList<SecuredUserUDFBean> getCurrentUserUDFs() throws GranException {
        if (!allowedByACL || !sc.canAction(Action.manageUserUDFs, getAllowedPrstatuses()))
            return null;
        List<UDFCacheItem> u = user.getUDFs();
        ArrayList<SecuredUserUDFBean> c = new ArrayList<SecuredUserUDFBean>();
        if (u != null) {
            for (UDFCacheItem i : u) {
                c.add(new SecuredUserUDFBean(i, sc));
            }
        }
        return c;
    }

    public DateFormatter getDateFormatter() throws GranException {
        return user.getDateFormatter();
    }


    public Integer getCountUDF() throws GranException {
        return user.getCountUDF();
    }


    public boolean isAllowedByACL() throws GranException {
        return allowedByACL;
    }


    public boolean canView() throws GranException {
        return onSight;
    }

    public boolean getCanView() throws GranException {
        return canView();
    }

    public UserBean getSOAP() throws GranException {
        UserBean bean = new UserBean();
        bean.setEnabled(isEnabled());
        bean.setChildAllowed(getChildrenAllowed() != null ? getChildrenAllowed() : 0);
        bean.setCompany(getCompany());
        bean.setDefaultProjectId(getDefaultProjectId());
        bean.setEmail(getEmail());
        bean.setExpireDate(getExpireDate() != null ? getExpireDate().getTimeInMillis() : -1L);
        bean.setPasswordChangedDate(getPasswordChangedDate() != null ? getPasswordChangedDate().getTimeInMillis() : -1L);
        bean.setLastLogonDate(getLastLogonDate() != null ? getLastLogonDate().getTimeInMillis() : -1L);
        bean.setId(getId());
        bean.setLocale(getLocale());
        bean.setLogin(getLogin());
        bean.setManagerId(getManagerId());
        bean.setPreferences(getPreferences());
        bean.setName(getName());
        bean.setPrstatusId(getPrstatusId());
        bean.setTel(getTel());
        bean.setTimezone(getTimezone());
        return bean;
    }

    public boolean isActive() throws GranException {
        return UserRelatedManager.getInstance().isActive(user.getId());
    }

    public boolean isEnabled() throws GranException {
        return user.isEnabled();
    }


    public String getPrstatusId() {
        return user.getPrstatusId();
    }

    public String getPasswordHistory() {
        return allowedByACL ? user.getPassword() : null;
    }

    public String getManagerId() {
        return user.getParentId();
    }

    public String getParentId() {
        return user.getParentId();
    }

    public String getDefaultProjectId() {
        return allowedByACL ? user.getDefaultProjectId() : null;
    }


    public boolean isOnSight() {
        return onSight;
    }

    private Set<String> getAllowedPrstatuses() throws GranException {
        if (allowedPrstatuses == null)
            allowedPrstatuses = new CopyOnWriteArraySet(sc.getAllowedPrstatusesForUser(user.getId()));
        return allowedPrstatuses;
    }

    public ArrayList<SecuredUserAttachmentBean> getAttachments() throws GranException {
        if (allowedByACL && user != null) {
            ArrayList<SecuredUserAttachmentBean> list = new ArrayList<SecuredUserAttachmentBean>();
            for (AttachmentCacheItem attachment : KernelManager.getAttachment().getAttachmentList(null, null, user.getId()))
                list.add(new SecuredUserAttachmentBean(attachment, sc));
            return list;
        }
        return null;
    }

    public ArrayList<SecuredUDFValueBean> getFilterUDFValues() throws GranException {
        ArrayList<SecuredUDFValueBean> r = new ArrayList<SecuredUDFValueBean>();
        HashMap<String, UdfValue> map = new HashMap<String, UdfValue>();
        if (isOnSight() && getUser() != null) {
            List<UdfValue> h = getUser().getFilterUDFValues();
            ArrayList<UDFCacheItem> filterUDFs = getUser().getFilterUDFs();
            UdfManager udfManager = KernelManager.getUdf();
            for (UdfValue k : h) {
                map.put(k.getUdfId(), k);
            }
            for (UDFCacheItem udfci : filterUDFs) {
                if (udfci == null)       // Can occure when the task-owner has just been deleted. (#43284)
                    continue;
                if (udfManager.isUserUdfViewable(getUser().getId(), sc.getUserId(), udfci.getId())) {
                    UdfValue o = map.get(udfci.getId());
                    if (o != null) r.add(new SecuredUDFValueBean(o, this));
                }
            }
        }
        return r;
    }


    public ArrayList<SecuredUserBean> getAncestors() throws GranException {
        if (getParentId() != null) return
                AdapterManager.getInstance().getSecuredUserAdapterManager().getUserChain(this.getSecure(), this.getParentId());
        else return new ArrayList<SecuredUserBean>();
    }

    public boolean canManage() throws GranException {
        return isAllowedByACL();
    }

    public boolean isDelete() throws GranException {
        List<String[]> alert = new ArrayList<String[]>();
        List<MessageCacheItem> messages = AdapterManager.getInstance().getSecuredMessageAdapterManager().getMessageUserList(sc, this.getId());
        if (messages.size() > 0) {
            Set<String> tasks = new TreeSet<String>();
            for (MessageCacheItem message : messages) {
                SecuredMessageBean smb = AdapterManager.getInstance().getSecuredFindAdapterManager().findMessageById(sc, message.getId());
                tasks.add("#" + smb.getTask().getNumber());
            }
            String taskAlert = "";
            for (String taskName : tasks) {
                taskAlert += taskName + "; ";
            }
            alert.add(new String[]{"USER_EXCEPTION_CONSTRAINT_MESSAGES", taskAlert});
        }
        List<TaskRelatedInfo> tasks = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskUseUserList(sc, this.getId());
        if (tasks.size() > 0) {
            Set<String> taskNames = new TreeSet<String>();
            for (TaskRelatedInfo task : tasks) {
                taskNames.add("#" + task.getNumber());
            }
            String handlerAndSubmitterAlert = "";
            for (String name : taskNames) {
                handlerAndSubmitterAlert += name + "; ";
            }
            alert.add(new String[]{"USER_EXCEPTION_CONSTRAINT_TASK", handlerAndSubmitterAlert});
        }
        if (AdapterManager.getInstance().getSecuredUserAdapterManager().getManagerUserList(sc, this.getId()).size() > 0) {
            alert.add(new String[]{"USER_EXCEPTION_CONSTRAINT_MANAGER", ""});
        }
        if (AdapterManager.getInstance().getSecuredAclAdapterManager().getAclUserList(sc, this.getId()).size() > 0) {
            alert.add(new String[]{"USER_EXCEPTION_CONSTRAINT_ACL", ""});
        }
        List<Template> templates = AdapterManager.getInstance().getSecuredTemplateAdapterManager().getTemplateOwnerList(sc, this.getId());
        if (templates.size() > 0) {
            String templateAlert = "";
            for (Template template : templates) {
                templateAlert += template.getName() + "; ";
            }
            alert.add(new String[]{"USER_EXCEPTION_CONSTRAINT_TEMPLATE", templateAlert});
        }
        if (alert.size() > 0) {
            String allException = I18n.getString("USER_EXCEPTION_CANNOT_DELETE", new Object[]{this.getName() + " (" + this.getLogin() + ")"}) + "<br>";
            for (String[] message : alert) {
                allException += I18n.getString(message[0], new Object[]{message[1]}) + "<br>";
            }
            throw new UserException(allException, false);
        }
        return true;
    }

    public static EggBasket<SecuredTaskBean, SecuredPrstatusBean> getPrstatusForTaskId(SessionContext sc, String userId) throws GranException {
        ArrayList<SecuredTaskBean> tasks = AdapterManager.getInstance().getSecuredAclAdapterManager().getTaskList(sc, userId);
        EggBasket<SecuredTaskBean, SecuredPrstatusBean> acls = new EggBasket<SecuredTaskBean, SecuredPrstatusBean>();
        for (SecuredTaskBean tb : tasks) {
            if (tb.isOnSight()) {
                for (SecuredPrstatusBean b : AdapterManager.getInstance().getSecuredAclAdapterManager().getAllowedPrstatusList(sc, tb.getId(), userId)) {
                    acls.putItem(tb, b);
                }

            }
        }
        return acls;
    }

    /**
     * This method gets list of prstatus for user for special taskid.
     * @param userId user id
     * @return list pstatus
     * @throws GranException for necessery
     */
    public static boolean isExistPrstatus(String userId, String defaultPrstatusId, String groupId) throws GranException {
        return KernelManager.getAcl().isExistPrstatus(userId, defaultPrstatusId, groupId);
    }
}