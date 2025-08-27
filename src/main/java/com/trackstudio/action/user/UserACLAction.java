package com.trackstudio.action.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
import com.trackstudio.action.task.ACLAction;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.ACLForm;
import com.trackstudio.form.BaseForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredUserAclBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.PropertyComparable;
import com.trackstudio.tools.PropertyContainer;

public class UserACLAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(ACLAction.class);

    // todo list item
    public static class ACLListItem extends PropertyComparable {
        public String id;
        public SecuredUserBean forUser;
        public SecuredPrstatusBean forRole;

        public SecuredUserBean connectedTo;
        public SecuredUserBean owner;
        public SecuredPrstatusBean withRole;

        public ArrayList prstatus;
        public boolean canUpdate;
        public boolean override;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public ArrayList getPrstatus() {
            return prstatus;
        }

        public void setPrstatus(ArrayList prstatus) {
            this.prstatus = prstatus;
        }


        public SecuredUserBean getConnectedTo() {
            return connectedTo;
        }

        public void setConnectedTo(SecuredUserBean connectedTo) {
            this.connectedTo = connectedTo;
        }

        public SecuredUserBean getOwner() {
            return owner;
        }

        public void setOwner(SecuredUserBean owner) {
            this.owner = owner;
        }

        public boolean isCanUpdate() {
            return canUpdate;
        }

        public void setCanUpdate(boolean canUpdate) {
            this.canUpdate = canUpdate;
        }


        public SecuredPrstatusBean getWithRole() {
            return withRole;
        }

        public void setWithRole(SecuredPrstatusBean withRole) {
            this.withRole = withRole;
        }

        public boolean isOverride() {
            return override;
        }

        public void setOverride(boolean override) {
            this.override = override;
        }


        public SecuredUserBean getForUser() {
            return forUser;
        }

        public void setForUser(SecuredUserBean forUser) {
            this.forUser = forUser;
        }

        public SecuredPrstatusBean getForRole() {
            return forRole;
        }

        public void setForRole(SecuredPrstatusBean forRole) {
            this.forRole = forRole;
        }

        protected PropertyContainer getContainer() {
            PropertyContainer pc = container.get();
            if (pc != null)
                return pc; // object in cache, return it

            PropertyContainer newPC = new PropertyContainer();
            newPC.put(connectedTo.getName()).put(owner.getName()).put(id);

            if (container.compareAndSet(null, newPC)) // try to update
                return newPC; // we can update - return loaded value
            else
                return container.get(); // some other thread already updated it - use saved value
        }

        public ACLListItem(String id) {
            this.id = id;
        }
    }

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            BaseForm tf = (BaseForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(tf, sc, request);
            if (!sc.canAction(Action.manageUserACLs, id))
                return null;

            ArrayList<SecuredUserAclBean> accessControl = AdapterManager.getInstance().getSecuredAclAdapterManager().getAllUserAclList(sc, id);

            ArrayList<ACLListItem> acls = new ArrayList<ACLListItem>();
            EggBasket<SecuredUserBean, ACLListItem> parentAclSet = new EggBasket<SecuredUserBean, ACLListItem>();
            EggBasket<SecuredUserBean, ACLListItem> childrenAclSet = new EggBasket<SecuredUserBean, ACLListItem>();
            ArrayList<EggBasket> seeAlso = new ArrayList<EggBasket>();
            ArrayList<SecuredUserBean> parentUsers = AdapterManager.getInstance().getSecuredUserAdapterManager().getUserChain(sc, null, id);
            HashMap<String, ArrayList<SecuredPrstatusBean>> prstatusLocalCache = new HashMap<String, ArrayList<SecuredPrstatusBean>>(); // winzard: this is performance issue

            for (SecuredUserAclBean aclBean : accessControl) {

                ACLListItem li = new ACLListItem(aclBean.getId());
                SecuredUserBean user = aclBean.getUser();
                if (user == null || sc.allowedByUser(user.getId())) {
                    tf.setValue("override-" + aclBean.getId(), aclBean.isOverride() ? "1" : null);
                    boolean canUpdate = aclBean.canManage();
                    li.setForUser(user);
                    li.setWithRole(aclBean.getPrstatus());
                    li.setForRole(aclBean.getGroup());

                    if (canUpdate && (user == null || !user.equals(sc.getUser()))) {
                        String managerId = user != null ? user.getParentId() : aclBean.getGroup().getUserId();
                        ArrayList<SecuredPrstatusBean> prstatus;
                        if (prstatusLocalCache.containsKey(managerId)) {
                            prstatus = prstatusLocalCache.get(managerId);
                        } else {

                            prstatus = AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getCreatablePrstatusList(sc, managerId);// imenno getParentId()!!!
                            Collections.sort(prstatus);
                            prstatusLocalCache.put(managerId, prstatus);
                        }

                        li.setPrstatus(prstatus);
                        if (aclBean.getPrstatusId() != null)
                            tf.setValue("prstatus-" + aclBean.getId(), aclBean.getPrstatusId());
                    }

                    li.setConnectedTo(aclBean.getToUser());
                    boolean homeUser = aclBean.getToUserId().equals(id);
                    li.setOverride(aclBean.getOverride());
                    li.setCanUpdate(aclBean.getCanUpdate() && aclBean.getToUserId().equals(id) && canUpdateForYourSelf(sc, aclBean));
                    li.setOwner(aclBean.getOwner());
                    if (aclBean.canManage()) {
                        if (homeUser) {
                            acls.add(li);
                        } else if (parentUsers.contains(aclBean.getToUser())) {
                            parentAclSet.putItem(aclBean.getToUser(), li);
                        } else childrenAclSet.putItem(aclBean.getToUser(), li);
                    }
                } // if

            }
            Collections.sort(acls);
            ArrayList<SecuredUserBean> userColl = AdapterManager.getInstance().getSecuredUserAdapterManager().getUserListForNewAcl(sc, sc.getUserId());
            Collections.sort(userColl);

            sc.setRequestAttribute(request, "paramCollection", userColl);
            sc.setRequestAttribute(request, "userCollection", userColl);

            TreeSet<SecuredPrstatusBean> prstatusSet = new TreeSet<SecuredPrstatusBean>(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId()));
            EggBasket<SecuredPrstatusBean, SecuredUserBean> eggBasket = new EggBasket<SecuredPrstatusBean, SecuredUserBean>();
            for (SecuredPrstatusBean bean : prstatusSet) {
                if (!eggBasket.containsKey(bean)) {
                    ArrayList<SecuredUserBean> list = AdapterManager.getInstance().getSecuredUserAdapterManager().getChildrenWithPrstatus(sc, sc.getUserId(), bean.getId());
                    eggBasket.put(bean, list);
                }
            }

            sc.setRequestAttribute(request, "statusCollection", eggBasket);

            sc.setRequestAttribute(request, "msgHowCreateObject", I18n.getString(sc.getLocale(), "ACL_ADD"));
            sc.setRequestAttribute(request, "firstParamMsg", I18n.getString(sc.getLocale(), "ADD_NEW"));
            sc.setRequestAttribute(request, "firstParamName", "aclUser");
            sc.setRequestAttribute(request, "msgAddObject", I18n.getString(sc.getLocale(), "ACL_ADD"));
            sc.setRequestAttribute(request, "createObjectAction", "/UserACLAction.do");
            sc.setRequestAttribute(request, "acls", acls);

            if (!parentAclSet.isEmpty()) seeAlso.add(parentAclSet);
            if (!childrenAclSet.isEmpty()) seeAlso.add(childrenAclSet);

            sc.setRequestAttribute(request, "seeAlso", seeAlso);
            sc.setRequestAttribute(request, "canViewAccessControl", sc.canAction(Action.manageUserACLs, id));
            sc.setRequestAttribute(request, "canDeleteAccessControl", sc.canAction(Action.manageUserACLs, id) && !id.equals(sc.getUserId()) && sc.allowedByUser(id));
            sc.setRequestAttribute(request, "canCreateAccessControl", sc.canAction(Action.manageUserACLs, id) && !id.equals(sc.getUserId()) && sc.allowedByUser(id));
            return mapping.findForward("userAclJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    //todo winzard тут нужно обыгрывать правила, по которым даем редактировать UserACL. Однако надо еще обратить внимание, какие UserACL кому можно добавлять, чтобы не получилось так, что добавить можем, а редактировать -нет
    //dnikitin на us2.getGroup().canUpdate() проверять не нужно.
    private boolean canUpdateForYourSelf(SessionContext sc, SecuredUserAclBean us2) throws GranException {
        return us2.getUserId() != null && !sc.getUserId().equals(us2.getUserId()) ||
                //если acl установлен на группу, и юзер принадлежит этой группе, то он может редактировать acl если:
                //а)он сам владелец acl-я
                //б)владелец не он, но у него есть доступ к манагеру текущего юзера с правом viewUserAccessControl
                //делается это чтобы исключить возможность удаления собственного права доступа
                us2.getGroupId() != null && (!us2.getGroupId().equals(sc.getPrstatusId()) || us2.getOwnerId().equals(sc.getUserId()) || us2.getToUser() != null && us2.getToUser().getManagerId() != null && sc.allowedByUser(us2.getToUser().getManagerId()) && sc.canAction(Action.manageUserACLs, us2.getToUser().getManagerId()));
    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");

            ACLForm tf = (ACLForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(tf, sc, request);
            ArrayList<SecuredUserAclBean> list = AdapterManager.getInstance().getSecuredAclAdapterManager().getUserAclList(sc, id);
            for (SecuredUserAclBean bean : list) {
                String prstatusid = (String) tf.getValue("prstatus-" + bean.getId());
                boolean override = false;
                if (tf.getValue("override-" + bean.getId()) != null)
                    override = true;
                if (prstatusid != null && prstatusid.length() > 0 && (sc.canAction(Action.manageUserACLs, id) &&
                        (canUpdateForYourSelf(sc, bean) && bean.canManage()))) {
                    AdapterManager.getInstance().getSecuredAclAdapterManager().updateUserAcl(sc, bean.getId(), prstatusid, override);
                }
            }
            return mapping.findForward("userAclPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            ACLForm tf = (ACLForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String[] values = tf.getDelete();
            if (values != null)
                for (String value : values)
                    if (canUpdateForYourSelf(sc, AdapterManager.getInstance().getSecuredFindAdapterManager().findUserAclById(sc, value)))
                        AdapterManager.getInstance().getSecuredAclAdapterManager().deleteUserAcl(sc, value);
            return mapping.findForward("userAclPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            ACLForm tf = (ACLForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(tf, sc, request);
            if (tf.getAclUser() != null && tf.getAclUser().length > 0) {
                for (String s : tf.getAclUser()) {
                    String userId = null;
                    String prstatusId = null;
                    if (s.startsWith("PR_"))
                        prstatusId = s.substring("PR_".length());
                    else
                        userId = s;
                    AdapterManager.getInstance().getSecuredAclAdapterManager().createAcl(sc, null, id, userId, prstatusId);
                }
            }
            return mapping.findForward("userAclPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

}
