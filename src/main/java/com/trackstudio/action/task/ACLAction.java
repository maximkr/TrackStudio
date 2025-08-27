package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import com.trackstudio.constants.CommonConstants;
import com.trackstudio.form.ACLForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredTaskAclBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.PropertyComparable;
import com.trackstudio.tools.PropertyContainer;
import com.trackstudio.tools.Tab;

public class ACLAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(ACLAction.class);

    public static class ACLListItem extends PropertyComparable {
        public String id;
        public SecuredUserBean forUser;
        public SecuredPrstatusBean forPrstatus;
        public SecuredTaskBean toTask;
        public SecuredUserBean owner;
        public SecuredPrstatusBean withPrstatus;
        public ArrayList<SecuredPrstatusBean> prstatus;
        public boolean canManage, override;

        public boolean isCanManage() {
            return canManage;
        }

        public void setCanManage(boolean canManage) {
            this.canManage = canManage;
        }

        public String getId() {
            return id;
        }


        public void setId(String id) {
            this.id = id;
        }


        public boolean isOverride() {
            return override;
        }

        public void setOverride(boolean override) {
            this.override = override;
        }

        public ArrayList<SecuredPrstatusBean> getPrstatus() {
            return prstatus;
        }

        public void setPrstatus(ArrayList<SecuredPrstatusBean> prstatus) {
            this.prstatus = prstatus;
        }


        public SecuredUserBean getForUser() {
            return forUser;
        }

        public void setForUser(SecuredUserBean forUser) {
            this.forUser = forUser;
        }

        public SecuredPrstatusBean getForPrstatus() {
            return forPrstatus;
        }

        public void setForPrstatus(SecuredPrstatusBean forPrstatus) {
            this.forPrstatus = forPrstatus;
        }

        public SecuredTaskBean getToTask() {
            return toTask;
        }

        public void setToTask(SecuredTaskBean toTask) {
            this.toTask = toTask;
        }

        public SecuredUserBean getOwner() {
            return owner;
        }

        public void setOwner(SecuredUserBean owner) {
            this.owner = owner;
        }

        public SecuredPrstatusBean getWithPrstatus() {
            return withPrstatus;
        }

        public void setWithPrstatus(SecuredPrstatusBean withPrstatus) {
            this.withPrstatus = withPrstatus;
        }

        protected PropertyContainer getContainer() {
            PropertyContainer pc = container.get();
            if (pc != null)
                return pc; // object in cache, return it

            PropertyContainer newPC = new PropertyContainer();
            if (forUser != null || forPrstatus != null) {
                newPC.put(forUser != null ? forUser.getName() : forPrstatus.getName()).put(toTask.getName()).put(owner.getName()).put(id);
            }

            if (container.compareAndSet(null, newPC)) // try to update
                return newPC; // we can update - return loaded value
            else
                return container.get(); // some other thread already updated it - use saved value

        }

        public ACLListItem(String id) {
            this.id = id;
        }
    }

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            ACLForm tf = (ACLForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(tf, sc, request, true);
            if (!sc.canAction(Action.manageTaskACLs, id))
                return null;
            List<SecuredTaskAclBean> sourceAcl = AdapterManager.getInstance().getSecuredAclAdapterManager().getAllTaskAclList(sc, id);

            ArrayList<ACLListItem> acls = new ArrayList<ACLListItem>();
            EggBasket<SecuredTaskBean, ACLListItem> parentAclSet = new EggBasket<SecuredTaskBean, ACLListItem>();
            EggBasket<SecuredTaskBean, ACLListItem> childrenAclSet = new EggBasket<SecuredTaskBean, ACLListItem>();
            ArrayList<EggBasket> seeAlso = new ArrayList<EggBasket>();
            ArrayList<SecuredTaskBean> parentTasks = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskChain(sc, null, id);
            //Map userNameMap = new HashMap();
            //Map prstatusMap = new HashMap();

            HashMap<String, ArrayList<SecuredPrstatusBean>> prstatusLocalCache = new HashMap<String, ArrayList<SecuredPrstatusBean>>(); // winzard: this is performance issue
            String[] cuttedAcls = (String[]) sc.getAttribute("ACLS");
            sc.setRequestAttribute(request, "clipBoardEmpty", !(cuttedAcls != null && cuttedAcls.length > 0));
            for (SecuredTaskAclBean us2 : sourceAcl) {
                SecuredUserBean user = us2.getUser();

                SecuredPrstatusBean groupBean = us2.getGroup();
                SecuredPrstatusBean prstatusBean = us2.getPrstatus();

                if (user == null || sc.allowedByUser(us2.getUserId())) {
                    SecuredTaskBean task = us2.getTask();

                    ACLListItem li = new ACLListItem(us2.getId());
                    if (task.canManage() && sc.canAction(Action.manageTaskACLs, task.getId())) {
                        boolean homeTask = us2.getTaskId().equals(id);
                        boolean canUpdate = us2.canManage();
                        li.setCanManage(canUpdate);

                        if (user != null) {
                            li.setForUser(us2.getUser());
                            if (prstatusBean != null) {
                                li.setWithPrstatus(prstatusBean);
                            } else {
                                li.setWithPrstatus(user.getPrstatus());
                            }

                        } else {
                            li.setForPrstatus(groupBean);
                            li.setWithPrstatus(prstatusBean == null ? groupBean : prstatusBean);
                        }

                        //boolean himselfOrParent = user != null && (sc.getUserId().equals(user.getId()) || AdapterManager.getInstance().getSecuredUserAdapterManager().isParentOf(sc, sc.getUserId(), user.getId()));
                        if (canUpdate && homeTask) {
                            String userId;
                            if (user != null) {
                                userId = user.getParentId() != null ? user.getParentId() : user.getId();
                            } else {
                                userId = groupBean.getUserId();
                            }

                            ArrayList<SecuredPrstatusBean> prstatus;
                            if (prstatusLocalCache.containsKey(userId)) {
                                prstatus = prstatusLocalCache.get(userId);
                            } else {
                                prstatus = AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getCreatablePrstatusList(sc, userId);
                                Collections.sort(prstatus);
                                prstatusLocalCache.put(userId, prstatus);
                            }

                            li.setPrstatus(prstatus);
                            if (us2.getPrstatusId() != null) {
                                tf.setValue("prstatus-" + us2.getId(), us2.getPrstatusId());
                                if (!li.prstatus.contains(prstatusBean))
                                    li.setCanManage(false);
                            } else {
                                tf.setValue("prstatus-" + us2.getId(), us2.getGroupId());
                                if (!li.prstatus.contains(groupBean))
                                    li.setCanManage(false);
                            }
                        }
                        li.setToTask(task);
                        li.setOverride(us2.getOverride());
                        tf.setValue("override-" + us2.getId(), us2.isOverride() ? "1" : null);
                        li.setOwner(us2.getOwner());

                        if (homeTask) {
                            acls.add(li);
                        } else if (parentTasks.contains(task)) {
                            parentAclSet.putItem(task, li);
                        } else childrenAclSet.putItem(task, li);
                    }
                } // for
            }

            ArrayList<SecuredUserBean> userColl = AdapterManager.getInstance().getSecuredUserAdapterManager().getUserListForNewAcl(sc, sc.getUserId());
            Collections.sort(userColl);
            Collections.sort(acls);
            if (!parentAclSet.isEmpty()) seeAlso.add(parentAclSet);
            if (!childrenAclSet.isEmpty()) seeAlso.add(childrenAclSet);
            ArrayList<SecuredPrstatusBean> prstatusSet = AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId());
            Collections.sort(prstatusSet);
            EggBasket<SecuredPrstatusBean, SecuredUserBean> eggBasket = new EggBasket<SecuredPrstatusBean, SecuredUserBean>();
            for (SecuredPrstatusBean bean : prstatusSet) {
                if (!eggBasket.containsKey(bean)) {
                    ArrayList<SecuredUserBean> list = AdapterManager.getInstance().getSecuredUserAdapterManager().getChildrenWithPrstatus(sc, sc.getUserId(), bean.getId());
                    eggBasket.put(bean, list);
                }
            }

            sc.setRequestAttribute(request, "handlerColl", userColl);
            sc.setRequestAttribute(request, "userCollection", userColl);
            sc.setRequestAttribute(request, "statusCollection", eggBasket);
            sc.setRequestAttribute(request, "msgHowCreateObject", I18n.getString(sc.getLocale(), "ACL_ADD"));
            sc.setRequestAttribute(request, "firstParamMsg", I18n.getString(sc.getLocale(), "ADD_NEW"));
            sc.setRequestAttribute(request, "firstParamName", "aclUser");
            sc.setRequestAttribute(request, "msgAddObject", I18n.getString(sc.getLocale(), "ACL_ADD"));
            sc.setRequestAttribute(request, "createObjectAction", "/ACLAction.do");
            sc.setRequestAttribute(request, "aclList", acls);
            sc.setRequestAttribute(request, "seeAlso", seeAlso);
            sc.setRequestAttribute(request, "canManage", sc.canAction(Action.manageTaskACLs, id));

            sc.setRequestAttribute(request, "tabView", new Tab(false, false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(true, true));
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_ASSIGNED_STATUSES);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_ASSIGNED_STATUSES));
            return mapping.findForward("taskAclJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");

            ACLForm tf = (ACLForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = tf.getId();
            ArrayList<SecuredTaskAclBean> list = AdapterManager.getInstance().getSecuredAclAdapterManager().getTaskAclList(sc, id);
            for (SecuredTaskAclBean bean : list) {
                boolean override = false;
                if (tf.getValue("override-" + bean.getId()) != null)
                    override = true;
                String prstatusid = (String) tf.getValue("prstatus-" + bean.getId());
                if (Null.isNotNull(prstatusid)) {
                    if ((bean.canManage() && (bean.getUserId() == null || !sc.getUserId().equals(bean.getUserId())))) {
                        AdapterManager.getInstance().getSecuredAclAdapterManager().updateTaskAcl(sc, bean.getId(), prstatusid, override);
                    }
                }
            }

            return mapping.findForward("taskAclPage");
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
            if (values != null) {
                for (String v : values) {
                    AdapterManager.getInstance().getSecuredAclAdapterManager().deleteTaskAcl(sc, v);
                }
            }
            sc.removeAttribute("ACL_OPERATION");
            sc.removeAttribute("ACLS");
            return mapping.findForward("taskAclPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward clipboardOperation(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                            HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            ACLForm tf = (ACLForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = tf.getId();
            String[] ids = tf.getDelete();
            if (tf.getALL_COPY() != null) {
                List<String> acl = new ArrayList<String>();

                TreeSet<SecuredPrstatusBean> prstatusSet = new TreeSet<SecuredPrstatusBean>(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getCreatablePrstatusList(sc, sc.getUserId()));
                for (SecuredTaskAclBean us2 : AdapterManager.getInstance().getSecuredAclAdapterManager().getTaskAclList(sc, id)) {
                    if (us2.canManage() && (us2.getUserId() != null ? sc.allowedByUser(us2.getUser().getId()) && us2.getUser().isActive() && !us2.getUserId().equals(sc.getUserId()) : prstatusSet.contains(us2.getGroup()))) {
                        acl.add(us2.getId());
                    }
                }
                ids = acl.toArray(new String[]{});
            }
            if (ids != null) {
                sc.setAttribute("ACLS", ids);
                String operation = tf.getCUT() != null ? CommonConstants.CUT : tf.getSINGLE_COPY() != null || tf.getALL_COPY() != null ? CommonConstants.COPY : CommonConstants.COPY_RECURSIVELY;
                sc.setAttribute("ACL_OPERATION", operation);
            }
            return mapping.findForward("taskAclPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }


    public ActionForward paste(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            ACLForm tf = (ACLForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = tf.getId();
            if (sc.getAttribute("ACLS") != null && sc.getAttribute("ACL_OPERATION") != null) {
                AdapterManager.getInstance().getSecuredAclAdapterManager().pasteAcls(sc, id, (String[]) sc.getAttribute("ACLS"), (String) sc.getAttribute("ACL_OPERATION"));
            }
            sc.removeAttribute("ACL_OPERATION");
            sc.removeAttribute("ACLS");
            log.debug("CLOSE SESSION ACLAction");
            return mapping.findForward("taskAclPage");

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
            String id = tf.getId();
            if (tf.getAclUser() != null && tf.getAclUser().length > 0) {
                for (int i = 0; i < tf.getAclUser().length; ++i) {
                    String userId = null;
                    String groupId = null;
                    if (tf.getAclUser()[i].startsWith("PR_"))
                        groupId = tf.getAclUser()[i].substring("PR_".length());
                    else
                        userId = tf.getAclUser()[i];
                    AdapterManager.getInstance().getSecuredAclAdapterManager().createAcl(sc, id, null, userId, groupId);
                }
            }
            return mapping.findForward("taskAclPage");

        } finally {
            if (w) lockManager.releaseConnection();
        }
    }

}