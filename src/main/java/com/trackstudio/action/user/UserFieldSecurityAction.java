package com.trackstudio.action.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.CategoryConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.RoleUserFieldSecurityForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.ActionCacheManager;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredUserUDFBean;
import com.trackstudio.securedkernel.SecuredUDFAdapterManager;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.PropertyComparable;
import com.trackstudio.tools.PropertyContainer;
import com.trackstudio.tools.Tab;


public class UserFieldSecurityAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(TaskFieldSecurityAction.class);

    public static class UdfBeanListItem extends PropertyComparable {
        private String id;
        private String caption;
        private boolean canUpdate;
        private String parent;


        public UdfBeanListItem(String id, String caption, boolean canUpdate, String parent) {
            this.id = id;
            this.caption = caption;
            this.canUpdate = canUpdate;
            this.parent = parent;
        }

        public String getId() {
            return id;
        }

        public String getCaption() {
            return caption;
        }

        public boolean getCanUpdate() {
            return canUpdate;
        }

        public String getParent() {
            return parent;
        }

        protected PropertyContainer getContainer() {
            PropertyContainer pc = container.get();
            if (pc != null)
                return pc; // object in cache, return it

            PropertyContainer newPC = new PropertyContainer();
            newPC.put(parent).put(caption).put(id);

            if (container.compareAndSet(null, newPC)) // try to update
                return newPC; // we can update - return loaded value
            else
                return container.get(); // some other thread already updated it - use saved value
        }

    }

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            RoleUserFieldSecurityForm pf = (RoleUserFieldSecurityForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(pf, sc, request);
            if (!sc.canAction(Action.manageRoles, id))
                return null;

            String prstatusId = pf.getPrstatusId();

            SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
            pf.setName(prstatus.getName());
            boolean b = prstatus.canManage();

            List<Action> choosed = ActionCacheManager.getInstance().getActions(prstatus.getId());
            List<Action> choosedCurrent = ActionCacheManager.getInstance().getActions(sc.getPrstatusId());
            pf.setViewUserCompany(choosed.contains(Action.viewUserCompany));
            pf.setEditUserCompany(choosed.contains(Action.editUserCompany));
            pf.setEditUserStatus(choosed.contains(Action.editUserStatus));
            pf.setEditUserEmail(choosed.contains(Action.editUserEmail));
            pf.setViewUserPhone(choosed.contains(Action.viewUserPhone));
            pf.setEditUserPhone(choosed.contains(Action.editUserPhone));
            pf.setEditUserLocale(choosed.contains(Action.editUserLocale));
            pf.setEditUserTimezone(choosed.contains(Action.editUserTimezone));
            pf.setEditUserExpireDate(choosed.contains(Action.editUserExpireDate));
            pf.setEditUserLicensed(choosed.contains(Action.editUserLicensed));
            pf.setEditUserEmailType(choosed.contains(Action.editUserEmailType));
            pf.setEditUserEmailType(choosed.contains(Action.editUserEmailType));
            pf.setEditUserActive(choosed.contains(Action.editUserActive));
            pf.setEditUserDefaultProject(choosed.contains(Action.editUserDefaultProject));

            sc.setRequestAttribute(request, "viewUserCompany", choosed.contains(Action.viewUserCompany));
            sc.setRequestAttribute(request, "editUserCompany", choosed.contains(Action.editUserCompany));
            sc.setRequestAttribute(request, "editUserStatus", choosed.contains(Action.editUserStatus));
            sc.setRequestAttribute(request, "editUserEmail", choosed.contains(Action.editUserEmail));
            sc.setRequestAttribute(request, "viewUserPhone", choosed.contains(Action.viewUserPhone));
            sc.setRequestAttribute(request, "editUserPhone", choosed.contains(Action.editUserPhone));
            sc.setRequestAttribute(request, "editUserLocale", choosed.contains(Action.editUserLocale));
            sc.setRequestAttribute(request, "editUserTimezone", choosed.contains(Action.editUserTimezone));
            sc.setRequestAttribute(request, "editUserExpireDate", choosed.contains(Action.editUserExpireDate));
            sc.setRequestAttribute(request, "editUserLicensed", choosed.contains(Action.editUserLicensed));
            sc.setRequestAttribute(request, "editUserEmailType", choosed.contains(Action.editUserEmailType));
            sc.setRequestAttribute(request, "editUserEmailType", choosed.contains(Action.editUserEmailType));
            sc.setRequestAttribute(request, "editUserActive", choosed.contains(Action.editUserActive));
            sc.setRequestAttribute(request, "editUserDefaultProject", choosed.contains(Action.editUserDefaultProject));

            fillStatusPermissionForm(sc, pf, request, prstatusId);

            sc.setRequestAttribute(request, "currentPrstatus", prstatus);
            sc.setRequestAttribute(request, "prstatusId", prstatusId);
            sc.setRequestAttribute(request, "canView", b);
            sc.setRequestAttribute(request, "canEdit", b);
            sc.setRequestAttribute(request, "tabEdit", new Tab(b, false));
            sc.setRequestAttribute(request, "tabTaskFieldSecurity", new Tab(b, false));
            sc.setRequestAttribute(request, "tabUserFieldSecurity", new Tab(b, true));
            sc.setRequestAttribute(request, "tabUserSecurity", new Tab(b, false));
            sc.setRequestAttribute(request, "tabTaskSecurity", new Tab(b, false));
            sc.setRequestAttribute(request, "tabCategorySecurity", new Tab(choosedCurrent.contains(Action.manageCategories), false));
            sc.setRequestAttribute(request, "tabWorkflowSecurity", new Tab(choosedCurrent.contains(Action.manageWorkflows), false));
            sc.setRequestAttribute(request, "tabView", new Tab(b, false));
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_STATUS_USER_FIELD_SECURITY);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_USER_STATUS_PROPERTIES));

            sc.setRequestAttribute(request, "canManageUserUDFs", choosedCurrent.contains(Action.manageUserUDFs));

            return mapping.findForward("userFieldSecurityJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    private void fillStatusPermissionForm(SessionContext sc, RoleUserFieldSecurityForm form, HttpServletRequest request, String prstatusId) throws GranException {

        // Init udf permissions
        Set<SecuredUserUDFBean> udfs = new TreeSet<SecuredUserUDFBean>(AdapterManager.getInstance().getSecuredUDFAdapterManager().getAllAvailableUserUdfListForStatus(sc, prstatusId));
        Set<String> view = new TreeSet<String>();
        Set<String> edit = new TreeSet<String>();
        for (SecuredUserUDFBean spb : udfs) {
            List types = AdapterManager.getInstance().getSecuredUDFAdapterManager().getUDFRuleList(sc, prstatusId, spb.getId());
            if (types.contains(CategoryConstants.VIEW_ALL))
                view.add(spb.getId());
            if (types.contains(CategoryConstants.EDIT_ALL)) {
                edit.add(spb.getId());
            }

        }

        view.addAll(edit);
        form.setView(view.toArray(new String[]{}));
        form.setEdit(edit.toArray(new String[]{}));
        sc.setRequestAttribute(request, "udfs", udfs);
    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            RoleUserFieldSecurityForm pf = (RoleUserFieldSecurityForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(pf, sc, request);
            if (!sc.canAction(Action.manageRoles, id))
                return null;

            String prstatusId = pf.getPrstatusId();

            SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
            pf.setName(prstatus.getName());

            ArrayList<String> allowed = new ArrayList<String>();
            ArrayList<String> denied = new ArrayList<String>();
            if (pf.isViewUserCompany())
                allowed.add(Action.viewUserCompany.toString());
            else
                denied.add(Action.viewUserCompany.toString());
            if (pf.isEditUserCompany())
                allowed.add(Action.editUserCompany.toString());
            else
                denied.add(Action.editUserCompany.toString());
            if (pf.isEditUserStatus())
                allowed.add(Action.editUserStatus.toString());
            else
                denied.add(Action.editUserStatus.toString());
            if (pf.isEditUserEmail())
                allowed.add(Action.editUserEmail.toString());
            else
                denied.add(Action.editUserEmail.toString());
            if (pf.isViewUserPhone())
                allowed.add(Action.viewUserPhone.toString());
            else
                denied.add(Action.viewUserPhone.toString());
            if (pf.isEditUserPhone())
                allowed.add(Action.editUserPhone.toString());
            else
                denied.add(Action.editUserPhone.toString());

            if (pf.isEditUserLocale())
                allowed.add(Action.editUserLocale.toString());
            else
                denied.add(Action.editUserLocale.toString());
            if (pf.isEditUserTimezone())
                allowed.add(Action.editUserTimezone.toString());
            else
                denied.add(Action.editUserTimezone.toString());
            if (pf.isEditUserExpireDate())
                allowed.add(Action.editUserExpireDate.toString());
            else
                denied.add(Action.editUserExpireDate.toString());
            if (pf.isEditUserLicensed())
                allowed.add(Action.editUserLicensed.toString());
            else
                denied.add(Action.editUserLicensed.toString());
            if (pf.isEditUserEmailType())
                allowed.add(Action.editUserEmailType.toString());
            else
                denied.add(Action.editUserEmailType.toString());
            if (pf.isEditUserEmailType())
                allowed.add(Action.editUserEmailType.toString());
            else
                denied.add(Action.editUserEmailType.toString());
            if (pf.isEditUserActive())
                allowed.add(Action.editUserActive.toString());
            else
                denied.add(Action.editUserActive.toString());
            if (pf.isEditUserDefaultProject())
                allowed.add(Action.editUserDefaultProject.toString());
            else
                denied.add(Action.editUserDefaultProject.toString());
            AdapterManager.getInstance().getSecuredPrstatusAdapterManager().setRoles(sc, prstatusId, allowed, denied);

            if (ActionCacheManager.getInstance().getActions(sc.getPrstatusId()).contains(Action.manageUserUDFs)) {
                // Save udf permissions
                SecuredUDFAdapterManager cam = AdapterManager.getInstance().getSecuredUDFAdapterManager();

                List<String> viewUdfsList = pf.getView() != null ? Arrays.asList(pf.getView()) : new ArrayList<String>();
                List<String> editUdfsList = pf.getEdit() != null ? Arrays.asList(pf.getEdit()) : new ArrayList<String>();
                Set<String> viewUdfsSet = new HashSet<String>();
                viewUdfsSet.addAll(viewUdfsList);
                viewUdfsSet.addAll(editUdfsList);
                Set<SecuredUserUDFBean> udfs = new TreeSet<SecuredUserUDFBean>(AdapterManager.getInstance().getSecuredUDFAdapterManager().getAllAvailableUserUdfListForStatus(sc, prstatusId));
                for (SecuredUserUDFBean s : udfs) {
                    String view = null, edit = null;
                    if (viewUdfsSet.contains(s.getId())) {
                        view = CategoryConstants.VIEW_ALL;
                    }
                    if (editUdfsList.contains(s.getId())) {
                        edit = CategoryConstants.EDIT_ALL;
                    }
                    cam.setUserUDFRule(sc, s.getId(), prstatusId, view, edit);
                }
            }
            return mapping.findForward("userFieldSecurityPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
