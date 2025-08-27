package com.trackstudio.action.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

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
import com.trackstudio.constants.WorkflowConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.PrstatusForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.ExternalAdapterManagerUtil;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.Tab;

public class UserMTypeSecurityAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(UserStatusEditAction.class);


    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PrstatusForm pf = (PrstatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(pf, sc, request);
            if (!sc.canAction(Action.manageRoles, id))
                return null;
            String prstatusId = pf.getPrstatusId();
            String workflowId = pf.getWorkflowId();

            SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
            SecuredWorkflowBean workflow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
            pf.setName(prstatus.getName());


            // Tab attributes
            sc.setRequestAttribute(request, "workflow", workflow);
            sc.setRequestAttribute(request, "workflowId", workflow.getId());
            sc.setRequestAttribute(request, "name", prstatus.getName());
            sc.setRequestAttribute(request, "prstatusId", prstatusId);
            sc.setRequestAttribute(request, "currentPrstatus", prstatus);
            sc.setRequestAttribute(request, "canView", sc.canAction(Action.manageRoles, id));


            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_MTYPE_USER_SECURITY);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_USER_MTYPE_USER_SECURITY));

            ArrayList<SecuredMstatusBean> mstatusList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getMstatusList(sc, workflow.getId());
            Collections.sort(mstatusList, new Comparator<SecuredMstatusBean>() {
                @Override
                public int compare(SecuredMstatusBean o1, SecuredMstatusBean o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            ArrayList<Pair<String>> process = new ArrayList<Pair<String>>();
            ArrayList<Pair<String>> nonProcess = new ArrayList<Pair<String>>();
            ArrayList<Pair<String>> view = new ArrayList<Pair<String>>();
            ArrayList<Pair<String>> nonView = new ArrayList<Pair<String>>();
            ArrayList<Pair<String>> beHandler = new ArrayList<Pair<String>>();
            ArrayList<Pair<String>> nonBeHandler = new ArrayList<Pair<String>>();

            for (SecuredMstatusBean mstatus : mstatusList) {
                EggBasket<String, String> rules = ExternalAdapterManagerUtil.getMprstatusMap(mstatus.getId());
                List<String> types = rules.get(prstatusId);
                if (types != null) {
                    if (types.contains(WorkflowConstants.VIEW_ALL))
                        view.add(new Pair<String>(mstatus.getId(), mstatus.getName()));
                    else if (types.contains(WorkflowConstants.VIEW_SUBMITTER))
                        view.add(new Pair<String>(mstatus.getId() + " (* " + I18n.getString(sc, "SUBMITTER") + ")", mstatus.getName() + " (* " + I18n.getString(sc, "SUBMITTER") + ")"));
                    else if (types.contains(WorkflowConstants.VIEW_HANDLER))
                        view.add(new Pair<String>(mstatus.getId() + " (* " + I18n.getString(sc, "HANDLER") + ")", mstatus.getName() + " (* " + I18n.getString(sc, "HANDLER") + ")"));
                    else if (types.contains(WorkflowConstants.VIEW_SUBMITTER_AND_HANDLER))
                        view.add(new Pair<String>(mstatus.getId() + " (* " + I18n.getString(sc, "SUBMITTER") +", " + I18n.getString(sc, "HANDLER")  + ")", mstatus.getName() + " (* " + I18n.getString(sc, "SUBMITTER") +", " + I18n.getString(sc, "HANDLER")  + ")"));
                    else {
                        nonView.add(new Pair<String>(mstatus.getId(), mstatus.getName()));
                    }

                    if (types.contains(WorkflowConstants.PROCESS_ALL))
                        process.add(new Pair<String>(mstatus.getId(), mstatus.getName()));
                    else if (types.contains(WorkflowConstants.PROCESS_SUBMITTER))
                        process.add(new Pair<String>(mstatus.getId() + " (* " + I18n.getString(sc, "SUBMITTER") + ")", mstatus.getName() + " (* " + I18n.getString(sc, "SUBMITTER") + ")"));
                    else if (types.contains(WorkflowConstants.PROCESS_HANDLER))
                        process.add(new Pair<String>(mstatus.getId() + " (* " + I18n.getString(sc, "HANDLER") + ")", mstatus.getName() + " (* " + I18n.getString(sc, "HANDLER") + ")"));
                    else if (types.contains(WorkflowConstants.PROCESS_SUBMITTER_AND_HANDLER))
                        process.add(new Pair<String>(mstatus.getId() + " (* " + I18n.getString(sc, "SUBMITTER") +", " + I18n.getString(sc, "HANDLER")  + ")", mstatus.getName() + " (* " + I18n.getString(sc, "SUBMITTER") +", " + I18n.getString(sc, "HANDLER")  + ")"));
                    else
                        nonProcess.add(new Pair<String>(mstatus.getId(), mstatus.getName()));

                    if (types.contains(WorkflowConstants.BE_HANDLER_ALL))
                        beHandler.add(new Pair<String>(mstatus.getId(), mstatus.getName()));
                    else if (types.contains(WorkflowConstants.BE_HANDLER_SUBMITTER))
                        beHandler.add(new Pair<String>(mstatus.getId() + " (* " + I18n.getString(sc, "SUBMITTER") + ")", mstatus.getName() + " (* " + I18n.getString(sc, "SUBMITTER") + ")"));
                    else if (types.contains(WorkflowConstants.BE_HANDLER_HANDLER))
                        beHandler.add(new Pair<String>(mstatus.getId() + " (* " + I18n.getString(sc, "HANDLER") + ")", mstatus.getName() + " (* " + I18n.getString(sc, "HANDLER") + ")"));
                    else if (types.contains(WorkflowConstants.BE_HANDLER_SUBMITTER_AND_HANDLER))
                        beHandler.add(new Pair<String>(mstatus.getId() + " (* " + I18n.getString(sc, "SUBMITTER") +", " + I18n.getString(sc, "HANDLER")  + ")", mstatus.getName() + " (* " + I18n.getString(sc, "SUBMITTER") +", " + I18n.getString(sc, "HANDLER")  + ")"));
                    else
                        nonBeHandler.add(new Pair<String>(mstatus.getId(), mstatus.getName()));
                } else {
                    nonView.add(new Pair<String>(mstatus.getId(), mstatus.getName()));
                    nonProcess.add(new Pair<String>(mstatus.getId(), mstatus.getName()));
                    nonBeHandler.add(new Pair<String>(mstatus.getId(), mstatus.getName()));
                }
            }
            sc.setRequestAttribute(request, "ruleProcessAll", process);
            sc.setRequestAttribute(request, "ruleProcessNone", nonProcess);
            sc.setRequestAttribute(request, "ruleViewAll", view);
            sc.setRequestAttribute(request, "ruleViewNone", nonView);
            sc.setRequestAttribute(request, "ruleBeHandlerAll", beHandler);
            sc.setRequestAttribute(request, "ruleBeHandlerNone", nonBeHandler);

            sc.setRequestAttribute(request, "tabMType", new Tab(true, true));
            sc.setRequestAttribute(request, "tabView", new Tab(true, false));
            sc.setRequestAttribute(request, "tabUdfMType", new Tab(true, false));

            return mapping.findForward("userMTypeSecurityJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }


    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward createRuleV(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PrstatusForm pf = (PrstatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String prstatusId = pf.getPrstatusId();
            if (pf.getRuleCategory() != null) {
                for (int i = 0; i < pf.getRuleCategory().length; ++i) {
                    String perm = WorkflowConstants.NONE;
                    if (pf.getSubmitterOnly() == null && pf.getHandlerOnly() == null)
                        perm = WorkflowConstants.VIEW_ALL;
                    else if (pf.getHandlerOnly() == null)
                        perm = WorkflowConstants.VIEW_SUBMITTER;
                    else if (pf.getSubmitterOnly() == null)
                        perm = WorkflowConstants.VIEW_HANDLER;
                    else
                        perm = WorkflowConstants.VIEW_SUBMITTER_AND_HANDLER;
                    AdapterManager.getInstance().getSecuredWorkflowAdapterManager().grantView(sc, perm, prstatusId, pf.getRuleCategory()[i]);
                }
            }
            sc.setRequestAttribute(request, "prstatusId", prstatusId);
            sc.setRequestAttribute(request, "workflowId", pf.getWorkflowId());
            return mapping.findForward("userMTypeSecurityPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward createRuleP(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PrstatusForm pf = (PrstatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String prstatusId = pf.getPrstatusId();
            if (pf.getRuleCategory() != null) {
                for (int i = 0; i < pf.getRuleCategory().length; ++i) {
                    String perm = WorkflowConstants.NONE;
                    if (pf.getSubmitterOnly() == null && pf.getHandlerOnly() == null)
                        perm = WorkflowConstants.PROCESS_ALL;
                    else if (pf.getHandlerOnly() == null)
                        perm = WorkflowConstants.PROCESS_SUBMITTER;
                    else if (pf.getSubmitterOnly() == null)
                        perm = WorkflowConstants.PROCESS_HANDLER;
                    else
                        perm = WorkflowConstants.PROCESS_SUBMITTER_AND_HANDLER;
                    AdapterManager.getInstance().getSecuredWorkflowAdapterManager().grantProcess(sc, perm, prstatusId, pf.getRuleCategory()[i]);
                }
            }
            sc.setRequestAttribute(request, "prstatusId", prstatusId);
            sc.setRequestAttribute(request, "workflowId", pf.getWorkflowId());
            return mapping.findForward("userMTypeSecurityPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward createRuleH(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PrstatusForm pf = (PrstatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String prstatusId = pf.getPrstatusId();
            if (pf.getRuleCategory() != null) {
                for (int i = 0; i < pf.getRuleCategory().length; ++i) {
                    String perm = WorkflowConstants.NONE;
                    if (pf.getSubmitterOnly() == null && pf.getHandlerOnly() == null)
                        perm = WorkflowConstants.BE_HANDLER_ALL;
                    else if (pf.getHandlerOnly() == null)
                        perm = WorkflowConstants.BE_HANDLER_SUBMITTER;
                    else if (pf.getSubmitterOnly() == null)
                        perm = WorkflowConstants.BE_HANDLER_HANDLER;
                    else
                        perm = WorkflowConstants.BE_HANDLER_SUBMITTER_AND_HANDLER;
                    AdapterManager.getInstance().getSecuredWorkflowAdapterManager().grantBeHandler(sc, perm, prstatusId, pf.getRuleCategory()[i]);
                }
            }
            sc.setRequestAttribute(request, "prstatusId", prstatusId);
            sc.setRequestAttribute(request, "workflowId", pf.getWorkflowId());
            return mapping.findForward("userMTypeSecurityPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward deleteRuleV(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PrstatusForm pf = (PrstatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String prstatusId = pf.getPrstatusId();
            String[] values = pf.getDelete();
            if (values != null)
                for (int i = 0; i < values.length; i++) {
                    if (!values[i].startsWith("V_")) continue;
                    StringTokenizer st = new StringTokenizer(values[i].substring(2), ",");
                    while (st.hasMoreTokens()) {
                        String cat = st.nextToken();
                        AdapterManager.getInstance().getSecuredWorkflowAdapterManager().grantView(sc, WorkflowConstants.NONE, prstatusId, cat);
                    }
                }
            return mapping.findForward("userMTypeSecurityPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward deleteRuleP(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PrstatusForm pf = (PrstatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String prstatusId = pf.getPrstatusId();
            String[] values = pf.getDelete();
            if (values != null)
                for (String value : values) {
                    if (!value.startsWith("P_")) continue;
                    StringTokenizer st = new StringTokenizer(value.substring(2), ",");
                    while (st.hasMoreTokens()) {
                        String cat = st.nextToken();
                        AdapterManager.getInstance().getSecuredWorkflowAdapterManager().grantProcess(sc, WorkflowConstants.NONE, prstatusId, cat);
                    }
                }
            return mapping.findForward("userMTypeSecurityPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward deleteRuleH(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PrstatusForm pf = (PrstatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String prstatusId = pf.getPrstatusId();
            String[] values = pf.getDelete();
            if (values != null)
                for (String value : values) {
                    if (!value.startsWith("H_")) continue;
                    StringTokenizer st = new StringTokenizer(value.substring(2), ",");
                    while (st.hasMoreTokens()) {
                        String cat = st.nextToken();
                        AdapterManager.getInstance().getSecuredWorkflowAdapterManager().grantBeHandler(sc, WorkflowConstants.NONE, prstatusId, cat);
                    }
                }
            return mapping.findForward("userMTypeSecurityPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PrstatusForm tf = (PrstatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String prstatusId = tf.getPrstatusId();
            ArrayList<SecuredMstatusBean> mstatusList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getMstatusList(sc, tf.getWorkflowId());
            String canprocess = tf.getHiddencanprocess();
            String canview = tf.getHiddencanview();
            String canhandler = tf.getHiddencanhandler();
            ArrayList<String> canViewMstatuses = new ArrayList<String>();
            ArrayList<String> canViewMstatusesHandler = new ArrayList<String>();
            ArrayList<String> canViewMstatusesSubmitter = new ArrayList<String>();
            ArrayList<String> canViewMstatusesSubmitterAndHandler = new ArrayList<String>();

            ArrayList<String> canProcessMstatuses = new ArrayList<String>();
            ArrayList<String> canProcessMstatusesHandler = new ArrayList<String>();
            ArrayList<String> canProcessMstatusesSubmitter = new ArrayList<String>();
            ArrayList<String> canProcessstatusesSubmitterAndHandler = new ArrayList<String>();

            ArrayList<String> canBeHandlerMstatuses = new ArrayList<String>();
            ArrayList<String> canBeHandlerMstatusesHandler = new ArrayList<String>();
            ArrayList<String> canBeHandlerMstatusesSubmitter = new ArrayList<String>();
            ArrayList<String> canBeHandlerMstatusesSubmitterAndHandler = new ArrayList<String>();

            parseForm(sc, canview, canViewMstatusesSubmitterAndHandler, canViewMstatusesHandler, canViewMstatusesSubmitter, canViewMstatuses);
            parseForm(sc, canprocess, canProcessstatusesSubmitterAndHandler, canProcessMstatusesHandler, canProcessMstatusesSubmitter, canProcessMstatuses);
            parseForm(sc, canhandler, canBeHandlerMstatusesSubmitterAndHandler, canBeHandlerMstatusesHandler, canBeHandlerMstatusesSubmitter, canBeHandlerMstatuses);

            for (SecuredMstatusBean pr : mstatusList) {
                String mstatusId = pr.getId();
                String view = null, process = null, handler = null;
                if (canViewMstatuses.contains(mstatusId)) {
                    view = WorkflowConstants.VIEW_ALL;
                } else if (canViewMstatusesHandler.contains(mstatusId)) {
                    view = WorkflowConstants.VIEW_HANDLER;
                } else if (canViewMstatusesSubmitter.contains(mstatusId)) {
                    view = WorkflowConstants.VIEW_SUBMITTER;
                } else if (canViewMstatusesSubmitterAndHandler.contains(mstatusId)) {
                    view = WorkflowConstants.VIEW_SUBMITTER_AND_HANDLER;
                }

                if (canProcessMstatuses.contains(mstatusId)) {
                    process = WorkflowConstants.PROCESS_ALL;
                } else if (canProcessMstatusesHandler.contains(mstatusId)) {
                    process = WorkflowConstants.PROCESS_HANDLER;
                } else if (canProcessMstatusesSubmitter.contains(mstatusId)) {
                    process = WorkflowConstants.PROCESS_SUBMITTER;
                } else if (canProcessstatusesSubmitterAndHandler.contains(mstatusId)) {
                    process = WorkflowConstants.PROCESS_SUBMITTER_AND_HANDLER;
                }


                if (canBeHandlerMstatuses.contains(mstatusId)) {
                    handler = WorkflowConstants.BE_HANDLER_ALL;
                } else if (canBeHandlerMstatusesHandler.contains(mstatusId)) {
                    handler = WorkflowConstants.BE_HANDLER_HANDLER;
                } else if (canBeHandlerMstatusesSubmitter.contains(mstatusId)) {
                    handler = WorkflowConstants.BE_HANDLER_SUBMITTER;
                } else if (canBeHandlerMstatusesSubmitterAndHandler.contains(mstatusId)) {
                    handler = WorkflowConstants.BE_HANDLER_SUBMITTER_AND_HANDLER;
                }

                AdapterManager.getInstance().getSecuredWorkflowAdapterManager().grantView(sc, view, prstatusId, mstatusId);
                AdapterManager.getInstance().getSecuredWorkflowAdapterManager().grantProcess(sc, process, prstatusId, mstatusId);
                AdapterManager.getInstance().getSecuredWorkflowAdapterManager().grantBeHandler(sc, handler, prstatusId, mstatusId);
            }
            return mapping.findForward("userMTypeSecurityPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    private void parseForm(SessionContext sc, String canview, ArrayList<String> canViewMstatusesSubmitterAndHandler, ArrayList<String> canViewMstatusesHandler, ArrayList<String> canViewMstatusesSubmitter, ArrayList<String> canViewMstatuses) throws GranException {
        StringTokenizer tk = new StringTokenizer(canview, FValue.DELIM);
        while (tk.hasMoreElements()) {
            String token = tk.nextToken();
            if (token.length() > 0) {
                int posSpec = token.indexOf("(*");
                if (posSpec > 0) {
                    int posHanlder = token.indexOf(I18n.getString(sc, "HANDLER"));
                    int posSubmitter = token.indexOf(I18n.getString(sc, "SUBMITTER"));
                    //todo Тут надо не просто пробелы вырезать, а вообще символы, которые не относятся к id. Т.е. переводы строк, например
                    String prstatusId = token.substring(0, posSpec - 1);
                    prstatusId = prstatusId.replace('\r', ' ');
                    prstatusId = prstatusId.replace('\n', ' ');
                    prstatusId = prstatusId.trim();
                    if (posHanlder > -1 && posSubmitter > -1) {
                        canViewMstatusesSubmitterAndHandler.add(prstatusId);
                    } else if (posHanlder > -1 && posSubmitter == -1) {
                        canViewMstatusesHandler.add(prstatusId);
                    } else if (posHanlder == -1 && posSubmitter > -1) {
                        canViewMstatusesSubmitter.add(prstatusId);
                    }
                } else {
                    String prstatusId = token.replace('\r', ' ');
                    prstatusId = prstatusId.replace('\n', ' ');
                    prstatusId = prstatusId.trim();
                    canViewMstatuses.add(prstatusId);
                }
            }
        }
    }
}