package com.trackstudio.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;


/**
 * Describes user
 */
public class User extends Named implements Serializable {

    private String login; //persistent
    private String password; //persistent
    private String preferences;
    private String tel; //persistent
    private String email; //persistent
    private Integer active; //persistent

    private String locale; //persistent
    private String timezone; //persistent
    private String company; //persistent
    private Integer childAllowed; //persistent
    private Calendar expireDate; //persistent
    private Calendar lastLogonDate; //persistent
    private Calendar passwordChangedDate; //persistent

    private Set prstatusSet = new HashSet(); //persistent

    private Set udfsourceSet = new HashSet(); //persistent
    private Set udfValSet = new HashSet(); //persistent
    private Set filterOwnerSet = new HashSet(); //persistent
    private Set filterUserSet = new HashSet(); //persistent
    private Set mailImportSet = new HashSet(); //persistent

    private Set taskSubmitterSet = new HashSet(); //persistent
    private Set childSet = new HashSet(); //persistent
    private Set reportSet = new HashSet(); //persistent
    private Set bookmarkUserSet = new HashSet(); //persistent
    private Set bookmarkOwnerSet = new HashSet(); //persistent

    private Set messageSubmitterSet;

    private Set aclOwnerSet = new HashSet();
    private Set templateOwnerSet = new HashSet();
    private Set templateUserSet = new HashSet();
    private Set currentFilterUserSet;
    private Set currentFilterOwnerSet;

    private Set registrationSet;
    private Set usersourceSet;
    private Set aclSet;
    private Prstatus prstatus;
    private User manager;
    private String template;
    private Task defaultProject;

    public static final String delimiter = ",";

    public User(String id) {
        this.id = id;
    }

    public User(String login, String password, String name, String tel, String email, Integer active, Prstatus prstatus, String locale, String timezone, Integer childAllowed, User manager, String template) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.tel = tel;
        this.email = email;
        this.active = active;
        this.prstatus = prstatus;
        this.locale = locale;
        this.timezone = timezone;
        this.childAllowed = childAllowed;
        this.manager = manager;
        this.template = template;

    }

    public User() {
    }


    public User(String login, String name, Prstatus prstatus, User manager) {
        this.login = login;
        this.name = name;
        this.prstatus = prstatus;
        this.manager = manager;
    }

    public User(String login, String name, String prstatusId, String managerId) {
        this(login, name, prstatusId != null ? new Prstatus(prstatusId) : null, managerId != null ? new User(managerId) : null);
    }


    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getTel() {
        return this.tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmailOne() {
        ArrayList<String> al = prepareEmailList();
        if (al != null) {
            return al.get(0);

        } else return this.email;
        //return this.email;
    }

    //public String getEmailListString()
    public String getEmail() {

        return this.email;
    }

    public ArrayList<String> getEmailList() {

        return prepareEmailList();
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getActive() {
        return this.active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Prstatus getPrstatus() {
        return this.prstatus;
    }

    public void setPrstatus(String prstatusId) {
        this.prstatus = new Prstatus(prstatusId);
    }


    public void setPrstatus(Prstatus prstatus) {
        this.prstatus = prstatus;
    }

    public String getLocale() {
        return this.locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTimezone() {
        return this.timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getCompany() {
        return this.company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Integer getChildAllowed() {
        return this.childAllowed;
    }

    public void setChildAllowed(Integer childAllowed) {
        this.childAllowed = childAllowed;
    }

    public User getManager() {
        return this.manager;
    }

    public void setManager(String managerId) {
        this.manager = managerId != null ? new User(managerId) : null;
    }


    public void setManager(User manager) {
        this.manager = manager;
    }

    public String getTemplate() {
        return this.template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }


    public Calendar getExpireDate() {
        return this.expireDate;
    }

    public void setExpireDate(Calendar expireDate) {
        this.expireDate = expireDate;
    }

    public Task getDefaultProject() {
        return this.defaultProject;
    }

    public void setNullDefaultProject() {
        this.defaultProject = null;
    }

    public void setDefaultProject(Task defaultProject) {
        this.defaultProject = defaultProject;
    }

    public void setDefaultProject(String defaultProjectId) {
        this.defaultProject = defaultProjectId != null ? new Task(defaultProjectId) : null;
    }

    public Set getPrstatusSet() {
        return this.prstatusSet;
    }

    public void setPrstatusSet(Set prstatusSet) {
        this.prstatusSet = prstatusSet;
    }


    public Set getUdfsourceSet() {
        return this.udfsourceSet;
    }

    public void setUdfsourceSet(Set udfsourceSet) {
        this.udfsourceSet = udfsourceSet;
    }

    public Set getFilterOwnerSet() {
        return this.filterOwnerSet;
    }

    public void setFilterOwnerSet(Set filterOwnerSet) {
        this.filterOwnerSet = filterOwnerSet;
    }

    public Set getMessageSubmitterSet() {
        return messageSubmitterSet;
    }

    public void setMessageSubmitterSet(Set messageSubmitterSet) {
        this.messageSubmitterSet = messageSubmitterSet;
    }

    public Set getCurrentFilterUserSet() {
        return currentFilterUserSet;
    }

    public void setCurrentFilterUserSet(Set currentFilterSet) {
        this.currentFilterUserSet = currentFilterSet;
    }

    public Set getCurrentFilterOwnerSet() {
        return currentFilterOwnerSet;
    }

    public void setCurrentFilterOwnerSet(Set currentFilterOwnerSet) {
        this.currentFilterOwnerSet = currentFilterOwnerSet;
    }

    public Set getTaskSubmitterSet() {
        return this.taskSubmitterSet;
    }

    public void setTaskSubmitterSet(Set taskSubmitterSet) {
        this.taskSubmitterSet = taskSubmitterSet;
    }

    public Set getChildSet() {
        return childSet;
    }

    public void setChildSet(Set childSet) {
        this.childSet = childSet;
    }

    public Set getReportSet() {
        return reportSet;
    }

    public void setReportSet(Set reportSet) {
        this.reportSet = reportSet;
    }

    public Set getRegistrationSet() {
        return registrationSet;
    }

    public void setRegistrationSet(Set registrationSet) {
        this.registrationSet = registrationSet;
    }

    public String toString() {
        return this.getName();
    }


    public Set getAclOwnerSet() {
        return aclOwnerSet;
    }

    public void setAclOwnerSet(Set aclOwnerSet) {
        this.aclOwnerSet = aclOwnerSet;
    }

    public boolean equals(Object obj) {
        return obj instanceof User && ((User) obj).getId().equals(this.id);
    }

    public Calendar getLastLogonDate() {
        return lastLogonDate;
    }

    public void setLastLogonDate(Calendar lastLogonDate) {
        this.lastLogonDate = lastLogonDate;
    }

    public Calendar getPasswordChangedDate() {
        return passwordChangedDate;
    }

    public void setPasswordChangedDate(Calendar passwordChangedDate) {
        this.passwordChangedDate = passwordChangedDate;
    }

    public Set getUdfValSet() {
        return udfValSet;
    }

    public void setUdfValSet(Set udfValSet) {
        this.udfValSet = udfValSet;
    }


    public Set getMailImportSet() {
        return mailImportSet;
    }

    public void setMailImportSet(Set mailImportSet) {
        this.mailImportSet = mailImportSet;
    }

    public Set getUsersourceSet() {
        return usersourceSet;
    }

    public void setUsersourceSet(Set usersourceSet) {
        this.usersourceSet = usersourceSet;
    }

    public Set getAclSet() {
        return aclSet;
    }

    public void setAclSet(Set aclSet) {
        this.aclSet = aclSet;
    }

    public Set getTemplateOwnerSet() {
        return templateOwnerSet;
    }

    public void setTemplateOwnerSet(Set templateOwnerSet) {
        this.templateOwnerSet = templateOwnerSet;
    }

    public Set getTemplateUserSet() {
        return templateUserSet;
    }

    public void setTemplateUserSet(Set templateUserSet) {
        this.templateUserSet = templateUserSet;
    }


    public Set getFilterUserSet() {
        return filterUserSet;
    }

    public void setFilterUserSet(Set filterUserSet) {
        this.filterUserSet = filterUserSet;
    }

    public ArrayList<String> prepareEmailList() {
        if ((this.email != null) && (this.email.length() != 0)) {
            StringTokenizer tk = new StringTokenizer(this.email, delimiter);

            ArrayList<String> al = new ArrayList<String>();

            while (tk.hasMoreElements()) {
                String token = tk.nextToken();
                al.add(token);

            }
            return al;
        } else
            return null;
    }

    public Set getBookmarkUserSet() {
        return bookmarkUserSet;
    }

    public void setBookmarkUserSet(Set bookmarkUserSet) {
        this.bookmarkUserSet = bookmarkUserSet;
    }

    public Set getBookmarkOwnerSet() {
        return bookmarkOwnerSet;
    }

    public void setBookmarkOwnerSet(Set bookmarkOwnerSet) {
        this.bookmarkOwnerSet = bookmarkOwnerSet;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
