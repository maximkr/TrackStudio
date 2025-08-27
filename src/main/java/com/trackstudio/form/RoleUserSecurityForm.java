package com.trackstudio.form;

public class RoleUserSecurityForm extends PrstatusForm {
    public boolean editUserHimself;
    public boolean editUserChildren;
    public boolean createUser;
    public boolean deleteUser;
    public boolean cutPasteUser;
    public boolean editUserPasswordHimself;
    public boolean editUserChildrenPassword;
    public boolean viewUserFilters;
    public boolean manageUserPrivateFilters;
    public boolean manageUserPublicFilters;
    public boolean manageUserACLs;
    public boolean manageUserUDFs;
    public boolean manageRoles;
    public boolean viewUserAttachments;
    public boolean createUserAttachments;
    public boolean manageUserAttachments;

    public boolean isEditUserHimself() {
        return editUserHimself;
    }

    public void setEditUserHimself(boolean editUserHimself) {
        this.editUserHimself = editUserHimself;
    }

    public boolean isEditUserChildren() {
        return editUserChildren;
    }

    public void setEditUserChildren(boolean editUserChildren) {
        this.editUserChildren = editUserChildren;
    }

    public boolean isCreateUser() {
        return createUser;
    }

    public void setCreateUser(boolean createUser) {
        this.createUser = createUser;
    }

    public boolean isDeleteUser() {
        return deleteUser;
    }

    public void setDeleteUser(boolean deleteUser) {
        this.deleteUser = deleteUser;
    }

    public boolean isCutPasteUser() {
        return cutPasteUser;
    }

    public void setCutPasteUser(boolean cutPasteUser) {
        this.cutPasteUser = cutPasteUser;
    }

    public boolean isEditUserPasswordHimself() {
        return editUserPasswordHimself;
    }

    public void setEditUserPasswordHimself(boolean editUserPasswordHimself) {
        this.editUserPasswordHimself = editUserPasswordHimself;
    }

    public boolean isEditUserChildrenPassword() {
        return editUserChildrenPassword;
    }

    public void setEditUserChildrenPassword(boolean editUserChildrenPassword) {
        this.editUserChildrenPassword = editUserChildrenPassword;
    }

    public boolean isViewUserFilters() {
        return viewUserFilters;
    }

    public void setViewUserFilters(boolean viewUserFilters) {
        this.viewUserFilters = viewUserFilters;
    }

    public boolean isManageUserPrivateFilters() {
        return manageUserPrivateFilters;
    }

    public void setManageUserPrivateFilters(boolean manageUserPrivateFilters) {
        this.manageUserPrivateFilters = manageUserPrivateFilters;
    }

    public boolean isManageUserPublicFilters() {
        return manageUserPublicFilters;
    }

    public void setManageUserPublicFilters(boolean manageUserPublicFilters) {
        this.manageUserPublicFilters = manageUserPublicFilters;
    }

    public boolean isManageUserACLs() {
        return manageUserACLs;
    }

    public void setManageUserACLs(boolean manageUserACLs) {
        this.manageUserACLs = manageUserACLs;
    }

    public boolean isManageUserUDFs() {
        return manageUserUDFs;
    }

    public void setManageUserUDFs(boolean manageUserUDFs) {
        this.manageUserUDFs = manageUserUDFs;
    }

    public boolean isManageRoles() {
        return manageRoles;
    }

    public void setManageRoles(boolean manageRoles) {
        this.manageRoles = manageRoles;
    }

    public boolean isViewUserAttachments() {
        return viewUserAttachments;
    }

    public void setViewUserAttachments(boolean viewUserAttachments) {
        this.viewUserAttachments = viewUserAttachments;
    }

    public boolean isCreateUserAttachments() {
        return createUserAttachments;
    }

    public void setCreateUserAttachments(boolean createUserAttachments) {
        this.createUserAttachments = createUserAttachments;
    }

    public boolean isManageUserAttachments() {
        return manageUserAttachments;
    }

    public void setManageUserAttachments(boolean manageUserAttachments) {
        this.manageUserAttachments = manageUserAttachments;
    }
}