package com.trackstudio.soap.bean;

public class UserBean {

    private String id;
    private boolean enabled;


    private int childAllowed;
    private String company;
    private String email;
    private long expireDate;
    private long passwordChangedDate;
    private long lastLogonDate;
    private String locale;
    private String login;
    private String name;
    private String password;
    private String preferences;
    private String tel;
    private String timezone;
    private String prstatusId;
    private String managerId;

    private String defaultProjectId;

    public UserBean() {

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getChildAllowed() {
        return childAllowed;
    }

    public void setChildAllowed(int childAllowed) {
        this.childAllowed = childAllowed;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(long expireDate) {
        this.expireDate = expireDate;
    }


    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getPrstatusId() {
        return prstatusId;
    }

    public void setPrstatusId(String prstatusId) {
        this.prstatusId = prstatusId;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }


    public String getDefaultProjectId() {
        return defaultProjectId;
    }

    public void setDefaultProjectId(String defaultProjectId) {
        this.defaultProjectId = defaultProjectId;
    }


    public long getPasswordChangedDate() {
        return passwordChangedDate;
    }

    public void setPasswordChangedDate(long passwordChangedDate) {
        this.passwordChangedDate = passwordChangedDate;
    }

    public long getLastLogonDate() {
        return lastLogonDate;
    }

    public void setLastLogonDate(long lastLogonDate) {
        this.lastLogonDate = lastLogonDate;
    }

    public String toString() {
        return getName() + " [" + getLogin() + "]";
    }

    public String getString() {
        return getName() + " [" + getLogin() + "]";
    }
}
