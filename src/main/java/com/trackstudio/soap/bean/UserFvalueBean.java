package com.trackstudio.soap.bean;

public class UserFvalueBean  {

    private String childAllowed;
    private String company;
    private String email;
    private String expiredate;
    private String isExpired;
    private String isUnactive;
    private String deepSearch;
    private String locale;
    private String login;
    private String prstatus;
    private String tel;
    private String timezone;

    public UserFvalueBean() {
    }

    public String getChildAllowed() {
        return childAllowed;
    }

    public void setChildAllowed(String childAllowed) {
        this.childAllowed = childAllowed;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getExpiredate() {
        return expiredate;
    }

    public void setExpiredate(String expiredate) {
        this.expiredate = expiredate;
    }

    public String getExpired() {
        return isExpired;
    }

    public void setExpired(String expired) {
        isExpired = expired;
    }

    public String getUnactive() {
        return isUnactive;
    }

    public void setUnactive(String unactive) {
        isUnactive = unactive;
    }

    public String getDeepSearch() {
        return deepSearch;
    }

    public void setDeepSearch(String deepSearch) {
        this.deepSearch = deepSearch;
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

    public String getPrstatus() {
        return prstatus;
    }

    public void setPrstatus(String prstatus) {
        this.prstatus = prstatus;
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

   protected String sortOrder;

    private String onPage;
    private String[] udf;
    private String[] udfSort;
    private String childCount;
    private String name;

    private String display;

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }


    public String getOnPage() {
        return onPage;
    }

    public void setOnPage(String onPage) {
        this.onPage = onPage;
    }

    public String[] getUdf() {
        return udf;
    }

    public void setUdf(String[] udf) {
        this.udf = udf;
    }

    public String[] getUdfSort() {
        return udfSort;
    }

    public void setUdfSort(String[] udfSort) {
        this.udfSort = udfSort;
    }

    public String getChildCount() {
        return childCount;
    }

    public void setChildCount(String childCount) {
        this.childCount = childCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }
}
