package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import com.trackstudio.form.MailImportForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredMailImportBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.PropertyComparable;
import com.trackstudio.tools.PropertyContainer;
import com.trackstudio.tools.Tab;

public class MailImportAction extends TSDispatchAction {

    private static Log log = LogFactory.getLog(MailImportAction.class);

    public static class MailImportListItem extends PropertyComparable {
        public String id;
        public String name;
        public String category;
        public SecuredUserBean owner;
        public SecuredTaskBean connectedTo;
        public Integer order;
        public String mstatus;
        public boolean active;
        public boolean importUnknown;

        public boolean canUpdate;

        protected PropertyContainer getContainer() {
            PropertyContainer pc = container.get();
            if (pc != null)
                return pc; // object in cache, return it

            PropertyContainer newPC = new PropertyContainer();
            newPC.put(order).put(name).put(connectedTo.getName()).put(id);

            if (container.compareAndSet(null, newPC)) // try to update
                return newPC; // we can update - return loaded value
            else
                return container.get(); // some other thread already updated it - use saved value
        }

        public MailImportListItem(String id, String name) {
            this.id = id;
            this.name = name;
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

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public SecuredUserBean getOwner() {
            return owner;
        }

        public void setOwner(SecuredUserBean owner) {
            this.owner = owner;
        }

        public SecuredTaskBean getConnectedTo() {
            return connectedTo;
        }

        public void setConnectedTo(SecuredTaskBean connectedTo) {
            this.connectedTo = connectedTo;
        }

        public boolean isCanUpdate() {
            return canUpdate;
        }

        public void setCanUpdate(boolean canUpdate) {
            this.canUpdate = canUpdate;
        }

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }

        public String getMstatus() {
            return mstatus;
        }

        public void setMstatus(String mstatus) {
            this.mstatus = mstatus;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public boolean getImportUnknown() {
            return importUnknown;
        }

        public void setImportUnknown(boolean importUnknown) {
            this.importUnknown = importUnknown;
        }
    }

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            MailImportForm tf = (MailImportForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(tf, sc, request, true);
            List<SecuredMailImportBean> allMailImportList = AdapterManager.getInstance().getSecuredMailImportAdapterManager().getAllAvailableMailImportList(sc, id);
            List<MailImportListItem> mailImports = new ArrayList<MailImportListItem>();
            ArrayList<SecuredTaskBean> parentTasks = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskChain(sc, null, id);
            EggBasket<SecuredTaskBean, MailImportListItem> parentMailImportSet = new EggBasket<SecuredTaskBean, MailImportListItem>();
            EggBasket<SecuredTaskBean, MailImportListItem> childrenMailImportSet = new EggBasket<SecuredTaskBean, MailImportListItem>();
            for (SecuredMailImportBean mi : allMailImportList) {
                MailImportListItem mli = new MailImportListItem(mi.getId(), mi.getName());
                mli.setCategory(mi.getCategory().getName());
                mli.setMstatus(mi.getMStatusId() != null ? mi.getMstatus().getName() : null);
                mli.setActive(mi.isActive());
                mli.setOwner(mi.getOwnerId() != null ? mi.getOwner() : null);
                mli.setCanUpdate(mi.canManage());
                mli.setConnectedTo(mi.getTask());
                mli.setOrder(mi.getOrder());
                mli.setImportUnknown(mi.getImportUnknown());
                if (mi.getTaskId().equals(id)) {
                    mailImports.add(mli);
                } else {
                    if (parentTasks.contains(mi.getTask())) {
                        parentMailImportSet.putItem(mi.getTask(), mli);
                    } else {
                        childrenMailImportSet.putItem(mi.getTask(), mli);
                    }
                }
            }


            ArrayList<EggBasket> seeAlso = new ArrayList<EggBasket>();

            if (!parentMailImportSet.isEmpty())
                seeAlso.add(parentMailImportSet);
            if (!childrenMailImportSet.isEmpty())
                seeAlso.add(childrenMailImportSet);

            Collections.sort(mailImports);
            sc.setRequestAttribute(request, "seeAlso", seeAlso);
            sc.setRequestAttribute(request, "mailImportList", mailImports);
            sc.setRequestAttribute(request, "canDelete", sc.canAction(Action.manageEmailImportRules, id));
            sc.setRequestAttribute(request, "canCreateObject", sc.canAction(Action.manageEmailImportRules, id) && !AdapterManager.getInstance().getSecuredCategoryAdapterManager().getCreatableCategoryList(sc, id).isEmpty());
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_EMAIL_IMPORT_LIST);
            sc.setRequestAttribute(request, "tabImport", new Tab(sc.canAction(Action.manageEmailImportRules, id), true));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_EMAIL_IMPORT_LIST));
            return mapping.findForward("mailImportJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            MailImportForm tf = (MailImportForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String[] mis = tf.getDelete();
            if (mis != null) {
                for (String mi : mis) {
                    AdapterManager.getInstance().getSecuredMailImportAdapterManager().deleteMailImport(sc, mi);
                }
            }
            return mapping.findForward("mailImportPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.trace("##########");
        return mapping.findForward("mailImportEditPage");
    }

    public ActionForward clone(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            MailImportForm tf = (MailImportForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String[] mis = tf.getDelete();
            if (mis != null) {
                for (String mi : mis) {
                    SecuredMailImportBean smb = AdapterManager.getInstance().getSecuredFindAdapterManager().findMailImportById(sc, mi);
                    AdapterManager.getInstance().getSecuredMailImportAdapterManager().createMailImport(sc, smb.getName() + "_clone", smb.getTaskId(), smb.getKeywords(),
                            smb.getSearchIn(), smb.getOrder(), smb.getCategoryId(),
                            smb.getMStatusId(), smb.getDomain(),
                            smb.isActive(), smb.getImportUnknown());
                }
            }
            return mapping.findForward("mailImportPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
