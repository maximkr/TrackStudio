package com.trackstudio.action.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
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
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.RoleTaskFieldSecurityForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.ActionCacheManager;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredTaskUDFBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.securedkernel.SecuredUDFAdapterManager;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.PropertyComparable;
import com.trackstudio.tools.PropertyContainer;
import com.trackstudio.tools.Tab;


public class TaskFieldSecurityAction extends TSDispatchAction {
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

    private static Log log = LogFactory.getLog(TaskFieldSecurityAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            RoleTaskFieldSecurityForm pf = (RoleTaskFieldSecurityForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(pf, sc, request);
            if (!sc.canAction(Action.manageRoles, id))
                return null;

            String prstatusId = pf.getPrstatusId();

            SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
            pf.setName(prstatus.getName());

            boolean editable = prstatus.canManage();

            List<Action> choosed = ActionCacheManager.getInstance().getActions(prstatus.getId());
            List<Action> choosedCurrent = ActionCacheManager.getInstance().getActions(sc.getPrstatusId());
            pf.setEditTaskAlias(choosed.contains(Action.editTaskAlias));
            pf.setViewTaskResolution(choosed.contains(Action.viewTaskResolution));
            pf.setViewTaskPriority(choosed.contains(Action.viewTaskPriority));
            pf.setEditTaskPriority(choosed.contains(Action.editTaskPriority));
            pf.setEditTaskHandler(choosed.contains(Action.editTaskHandler));
            pf.setViewTaskSubmitDate(choosed.contains(Action.viewTaskSubmitDate));
            pf.setViewTaskLastUpdated(choosed.contains(Action.viewTaskLastUpdated));
            pf.setViewTaskCloseDate(choosed.contains(Action.viewTaskCloseDate));
            pf.setViewTaskDeadline(choosed.contains(Action.viewTaskDeadline));
            pf.setEditTaskDeadline(choosed.contains(Action.editTaskDeadline));
            pf.setViewTaskBudget(choosed.contains(Action.viewTaskBudget));
            pf.setEditTaskBudget(choosed.contains(Action.editTaskBudget));
            pf.setViewTaskActualBudget(choosed.contains(Action.viewTaskActualBudget));
            pf.setEditTaskActualBudget(choosed.contains(Action.editTaskActualBudget));
            pf.setViewTaskDescription(choosed.contains(Action.viewTaskDescription));
            pf.setEditTaskDescription(choosed.contains(Action.editTaskDescription));

            fillStatusPermissionForm(sc, pf, request, prstatusId);

            sc.setRequestAttribute(request, "currentPrstatus", prstatus);
            sc.setRequestAttribute(request, "prstatusId", prstatusId);
            sc.setRequestAttribute(request, "canEdit", editable);
            sc.setRequestAttribute(request, "canView", editable);
            sc.setRequestAttribute(request, "tabEdit", new Tab(editable, false));
            sc.setRequestAttribute(request, "tabTaskFieldSecurity", new Tab(editable, true));
            sc.setRequestAttribute(request, "tabUserFieldSecurity", new Tab(editable, false));
            sc.setRequestAttribute(request, "tabUserSecurity", new Tab(editable, false));
            sc.setRequestAttribute(request, "tabTaskSecurity", new Tab(editable, false));
            sc.setRequestAttribute(request, "tabCategorySecurity", new Tab(choosedCurrent.contains(Action.manageCategories), false));
            sc.setRequestAttribute(request, "tabWorkflowSecurity", new Tab(choosedCurrent.contains(Action.manageWorkflows), false));
            sc.setRequestAttribute(request, "tabView", new Tab(editable, false));
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_STATUS_TASK_FIELD_SECURITY);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_USER_STATUS_PROPERTIES));

            sc.setRequestAttribute(request, "canManageTaskUDFs", choosedCurrent.contains(Action.manageTaskUDFs));

            return mapping.findForward("taskFieldSecurityJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    private void fillStatusPermissionForm(SessionContext sc, RoleTaskFieldSecurityForm form, HttpServletRequest request, String prstatusId) throws GranException {
        Set<SecuredUDFBean> udfs = new TreeSet<SecuredUDFBean>(AdapterManager.getInstance().getSecuredUDFAdapterManager().getAllAvailableTaskUdfListForStatus(sc, prstatusId));
        ArrayList<UdfBeanListItem> cannotviewUdfs = new ArrayList<UdfBeanListItem>();
        ArrayList<UdfBeanListItem> canviewUdfs = new ArrayList<UdfBeanListItem>();
        ArrayList<UdfBeanListItem> cannoteditUdfs = new ArrayList<UdfBeanListItem>();
        ArrayList<UdfBeanListItem> caneditUdfs = new ArrayList<UdfBeanListItem>();
        StringBuilder canview = new StringBuilder(), canedit = new StringBuilder();

        for (SecuredUDFBean spb : udfs) {
            List types = AdapterManager.getInstance().getSecuredUDFAdapterManager().getUDFRuleList(sc, prstatusId, spb.getId());

            if (!(types.contains(UdfConstants.VIEW_ALL) || types.contains(UdfConstants.VIEW_SUBMITTER) || types.contains(UdfConstants.VIEW_HANDLER) || types.contains(UdfConstants.VIEW_SUBMITTER_AND_HANDLER))) {
                cannotviewUdfs.add(new UdfBeanListItem(spb.getId(), spb.getCaptionEx(), spb.getCanManage(), null));
            }
            if (types.contains(UdfConstants.VIEW_ALL)) {
                canviewUdfs.add(new UdfBeanListItem(spb.getId(), spb.getCaptionEx(), spb.getCanManage(), null));
                canview.append(spb.getId()).append(FValue.DELIM);
            }
            if (types.contains(UdfConstants.VIEW_SUBMITTER)) {
                String id1 = " (* " + I18n.getString(sc, "SUBMITTER") + ")";
                canviewUdfs.add(new UdfBeanListItem(spb.getId() + id1, spb.getCaptionEx() + id1, spb.getCanManage(), null));
                canview.append(spb.getId()).append(id1).append(FValue.DELIM);
            }
            if (types.contains(UdfConstants.VIEW_HANDLER)) {
                String id1 = " (* " + I18n.getString(sc, "HANDLER") + ")";
                canviewUdfs.add(new UdfBeanListItem(spb.getId() + id1, spb.getCaptionEx() + id1, spb.getCanManage(), null));
                canview.append(spb.getId()).append(id1).append(FValue.DELIM);
            }
            if (types.contains(UdfConstants.VIEW_SUBMITTER_AND_HANDLER)) {
                String id1 = " (* " + I18n.getString(sc, "HANDLER") + ", " + I18n.getString(sc, "SUBMITTER") + ")";

                canviewUdfs.add(new UdfBeanListItem(spb.getId() + id1, spb.getCaptionEx() + id1, spb.getCanManage(), null));
                canview.append(spb.getId()).append(id1).append(FValue.DELIM);
            }

            if (!(types.contains(UdfConstants.EDIT_ALL) || types.contains(UdfConstants.EDIT_HANDLER) || types.contains(UdfConstants.EDIT_SUBMITTER) || types.contains(UdfConstants.EDIT_SUBMITTER_AND_HANDLER)))
                cannoteditUdfs.add(new UdfBeanListItem(spb.getId(), spb.getCaptionEx(), spb.getCanManage(), null));
            if (types.contains(UdfConstants.EDIT_ALL)) {
                caneditUdfs.add(new UdfBeanListItem(spb.getId(), spb.getCaptionEx(), spb.getCanManage(), null));
                canedit.append(spb.getId()).append(FValue.DELIM);
            }
            if (types.contains(UdfConstants.EDIT_SUBMITTER)) {
                String id1 = " (* " + I18n.getString(sc, "SUBMITTER") + ")";
                caneditUdfs.add(new UdfBeanListItem(spb.getId() + id1, spb.getCaptionEx() + id1, spb.getCanManage(), null));
                canedit.append(spb.getId()).append(id1).append(FValue.DELIM);
            }
            if (types.contains(UdfConstants.EDIT_HANDLER)) {
                String id1 = " (* " + I18n.getString(sc, "HANDLER") + ")";
                caneditUdfs.add(new UdfBeanListItem(spb.getId() + id1, spb.getCaptionEx() + id1, spb.getCanManage(), null));
                canedit.append(spb.getId()).append(id1).append(FValue.DELIM);
            }
            if (types.contains(UdfConstants.EDIT_SUBMITTER_AND_HANDLER)) {
                String id1 = " (* " + I18n.getString(sc, "HANDLER") + ", " + I18n.getString(sc, "SUBMITTER") + ")";
                caneditUdfs.add(new UdfBeanListItem(spb.getId() + id1, spb.getCaptionEx() + id1, spb.getCanManage(), null));
                canedit.append(spb.getId()).append(id1).append(FValue.DELIM);
            }
        }
        form.setHiddencanedit(canedit.toString());
        form.setHiddencanview(canview.toString());
        sc.setRequestAttribute(request, "cannotviewUdfs", cannotviewUdfs);
        sc.setRequestAttribute(request, "canviewUdfs", canviewUdfs);
        sc.setRequestAttribute(request, "caneditUdfs", caneditUdfs);
        sc.setRequestAttribute(request, "cannoteditUdfs", cannoteditUdfs);
        sc.setRequestAttribute(request, "prstatusForm", form);
    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            RoleTaskFieldSecurityForm pf = (RoleTaskFieldSecurityForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(pf, sc, request);
            if (!sc.canAction(Action.manageRoles, id))
                return null;

            String prstatusId = pf.getPrstatusId();

            SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
            pf.setName(prstatus.getName());
            //boolean canEdit = prstatus.canManage();
            ArrayList<String> allowed = new ArrayList<String>();
            ArrayList<String> denied = new ArrayList<String>();
            if (pf.isEditTaskAlias())
                allowed.add(Action.editTaskAlias.toString());
            else
                denied.add(Action.editTaskAlias.toString());
            if (pf.isViewTaskResolution())
                allowed.add(Action.viewTaskResolution.toString());
            else
                denied.add(Action.viewTaskResolution.toString());
            if (pf.isViewTaskPriority())
                allowed.add(Action.viewTaskPriority.toString());
            else
                denied.add(Action.viewTaskPriority.toString());
            if (pf.isEditTaskPriority())
                allowed.add(Action.editTaskPriority.toString());
            else
                denied.add(Action.editTaskPriority.toString());
            if (pf.isEditTaskHandler())
                allowed.add(Action.editTaskHandler.toString());
            else
                denied.add(Action.editTaskHandler.toString());
            if (pf.isViewTaskSubmitDate())
                allowed.add(Action.viewTaskSubmitDate.toString());
            else
                denied.add(Action.viewTaskSubmitDate.toString());
            if (pf.isViewTaskLastUpdated())
                allowed.add(Action.viewTaskLastUpdated.toString());
            else
                denied.add(Action.viewTaskLastUpdated.toString());
            if (pf.isViewTaskCloseDate())
                allowed.add(Action.viewTaskCloseDate.toString());
            else
                denied.add(Action.viewTaskCloseDate.toString());
            if (pf.isViewTaskDeadline())
                allowed.add(Action.viewTaskDeadline.toString());
            else
                denied.add(Action.viewTaskDeadline.toString());
            if (pf.isEditTaskDeadline())
                allowed.add(Action.editTaskDeadline.toString());
            else
                denied.add(Action.editTaskDeadline.toString());
            if (pf.isViewTaskBudget())
                allowed.add(Action.viewTaskBudget.toString());
            else
                denied.add(Action.viewTaskBudget.toString());
            if (pf.isEditTaskBudget())
                allowed.add(Action.editTaskBudget.toString());
            else
                denied.add(Action.editTaskBudget.toString());
            if (pf.isViewTaskActualBudget())
                allowed.add(Action.viewTaskActualBudget.toString());
            else
                denied.add(Action.viewTaskActualBudget.toString());
            if (pf.isEditTaskActualBudget())
                allowed.add(Action.editTaskActualBudget.toString());
            else
                denied.add(Action.editTaskActualBudget.toString());
            if (pf.isViewTaskDescription())
                allowed.add(Action.viewTaskDescription.toString());
            else
                denied.add(Action.viewTaskDescription.toString());
            if (pf.isEditTaskDescription())
                allowed.add(Action.editTaskDescription.toString());
            else
                denied.add(Action.editTaskDescription.toString());
            AdapterManager.getInstance().getSecuredPrstatusAdapterManager().setRoles(sc, prstatusId, allowed, denied);

            if (ActionCacheManager.getInstance().getActions(sc.getPrstatusId()).contains(Action.manageTaskUDFs)) {
                // Save udf permissions
                SecuredUDFAdapterManager cam = AdapterManager.getInstance().getSecuredUDFAdapterManager();
                Set<SecuredTaskUDFBean> udfs = new TreeSet<SecuredTaskUDFBean>(AdapterManager.getInstance().getSecuredUDFAdapterManager().getAllAvailableTaskUdfListForStatus(sc, prstatusId));
                String canedit = pf.getHiddencanedit();
                String canview = pf.getHiddencanview();
                Set<String> canViewUdfs = new HashSet<String>();
                Set<String> canViewUdfsHandler = new HashSet<String>();
                Set<String> canViewUdfsSubmitter = new HashSet<String>();
                Set<String> canViewUdfsSubmitterAndHandler = new HashSet<String>();
                Set<String> canEditUdfs = new HashSet<String>();
                Set<String> canEditUdfsHandler = new HashSet<String>();
                Set<String> canEditUdfsSubmitter = new HashSet<String>();
                Set<String> canEditUdfsSubmitterAndHandler = new HashSet<String>();

                StringTokenizer tk = new StringTokenizer(canview, FValue.DELIM);
                while (tk.hasMoreElements()) {
                    String token = tk.nextToken();
                    if (token.length() > 0) {
                        int posSpec = token.indexOf("(*");
                        if (posSpec > 0) {
                            int posHanlder = token.indexOf(I18n.getString(sc, "HANDLER"));
                            int posSubmitter = token.indexOf(I18n.getString(sc, "SUBMITTER"));
                            String udfId = token.substring(0, posSpec - 1).trim();
                            if (posHanlder > -1 && posSubmitter > -1) {
                                canViewUdfsSubmitterAndHandler.add(udfId);
                            } else if (posHanlder > -1 && posSubmitter == -1) {
                                canViewUdfsHandler.add(udfId);
                            } else if (posHanlder == -1 && posSubmitter > -1) {
                                canViewUdfsSubmitter.add(udfId);
                            }
                        } else {
                            canViewUdfs.add(token);
                        }
                    }
                }

                tk = new StringTokenizer(canedit, FValue.DELIM);
                while (tk.hasMoreElements()) {
                    String token = tk.nextToken();
                    if (token.length() > 0) {
                        int posSpec = token.indexOf("(*");
                        if (posSpec > 0) {
                            int posHanlder = token.indexOf(I18n.getString(sc, "HANDLER"));
                            int posSubmitter = token.indexOf(I18n.getString(sc, "SUBMITTER"));
                            String udfId = token.substring(0, posSpec - 1).trim();
                            if (posHanlder > -1 && posSubmitter > -1) {
                                canEditUdfsSubmitterAndHandler.add(udfId);
                            } else if (posHanlder > -1 && posSubmitter == -1) {
                                canEditUdfsHandler.add(udfId);
                            } else if (posHanlder == -1 && posSubmitter > -1) {
                                canEditUdfsSubmitter.add(udfId);
                            }
                        } else {
                            canEditUdfs.add(token);
                        }
                    }
                }

                canViewUdfs.addAll(canEditUdfs);
                canViewUdfsHandler.addAll(canEditUdfsHandler);
                canViewUdfsSubmitter.addAll(canEditUdfsSubmitter);
                canViewUdfsSubmitterAndHandler.addAll(canEditUdfsSubmitterAndHandler);

                for (SecuredTaskUDFBean udf : udfs) {
                    String udfId = udf.getId();
                    String view = null, edit = null;
                    if (canViewUdfs.contains(udfId)) {
                        view = UdfConstants.VIEW_ALL;
                    } else if (canViewUdfsHandler.contains(udfId)) {
                        view = UdfConstants.VIEW_HANDLER;
                    } else if (canViewUdfsSubmitter.contains(udfId)) {
                        view = UdfConstants.VIEW_SUBMITTER;
                    } else if (canViewUdfsSubmitterAndHandler.contains(udfId)) {
                        view = UdfConstants.VIEW_SUBMITTER_AND_HANDLER;
                    }

                    if (canEditUdfs.contains(udfId)) {
                        edit = UdfConstants.EDIT_ALL;
                    } else if (canEditUdfsHandler.contains(udfId)) {
                        edit = UdfConstants.EDIT_HANDLER;
                    } else if (canEditUdfsSubmitter.contains(udfId)) {
                        edit = UdfConstants.EDIT_SUBMITTER;
                    } else if (canEditUdfsSubmitterAndHandler.contains(udfId)) {
                        edit = UdfConstants.EDIT_SUBMITTER_AND_HANDLER;
                    }
                    cam.setTaskUDFRule(sc, udfId, prstatusId, view, edit);
                }

            }
            return mapping.findForward("taskFieldSecurityPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
