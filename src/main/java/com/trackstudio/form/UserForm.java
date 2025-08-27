package com.trackstudio.form;

public class UserForm extends UDFForm {
    private String session;
    private String login;
    private String confirmation;
    private String email;
    private String locale;
    private String timezone;
    private String company;
    private String password;
    private String prstatus;
    private String oldManager;
    private String manager;
    private String template;
    private String expireDate;
    private String licensedUsers;
    private boolean enabled;


    private String tel;
    private String newUser;

    private String registration;
    private String project;
    private String filterId;
    private String field;
    private String lastPath;
    private boolean rememberMe;
    private String emergencyNotice;
    private String emergencyNoticeDate;

    public String getEmergencyNoticeDate() {
        return emergencyNoticeDate;
    }

    public void setEmergencyNoticeDate(String emergencyNoticeDate) {
        this.emergencyNoticeDate = emergencyNoticeDate;
    }

    public String getEmergencyNotice() {
        return emergencyNotice;
    }

    public void setEmergencyNotice(String emergencyNotice) {
        this.emergencyNotice = emergencyNotice;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public String getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(String confirmation) {
        this.confirmation = confirmation;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getPrstatus() {
        return prstatus;
    }

    public void setPrstatus(String prstatus) {
        this.prstatus = prstatus;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getOldManager() {
        return oldManager;
    }

    public void setOldManager(String oldManager) {
        this.oldManager = oldManager;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getLicensedUsers() {
        return licensedUsers;
    }

    public void setLicensedUsers(String licensedUsers) {
        this.licensedUsers = licensedUsers;
    }


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public UserForm() {
    }

    public String getFilterId() {
        return filterId;
    }

    public void setFilterId(String filterId) {
        if (isMutable())
            this.filterId = filterId;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getLastPath() {
        return lastPath;
    }

    public void setLastPath(String lastPath) {
        this.lastPath = lastPath;
    }

    public String getNewUser() {
        return newUser;
    }

    public void setNewUser(String newUser) {
        if (isMutable())
            this.newUser = newUser;
    }
}
