package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.trackstudio.action.AttachmentEditAction;
import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.Preferences;
import com.trackstudio.app.TriggerManager;
import com.trackstudio.app.UDFFormFillHelper;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.csv.CSVImport;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.builder.TaskBuilder;
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.TriggerException;
import com.trackstudio.exception.UserException;
import com.trackstudio.exception.UserExceptionAfterTrigger;
import com.trackstudio.form.MessageForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.AttachmentCacheItem;
import com.trackstudio.kernel.cache.MessageCacheItem;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.manager.AttachmentArray;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredPriorityBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredResolutionBean;
import com.trackstudio.secured.SecuredTaskAttachmentBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.formatter.DateFormatter;
import com.trackstudio.tools.formatter.HourFormatter;
import com.trackstudio.tools.textfilter.HTMLEncoder;
import com.trackstudio.tools.textfilter.MacrosUtil;

import static com.trackstudio.tools.Null.isNotNull;
import static com.trackstudio.tools.Null.isNull;
import static com.trackstudio.tools.textfilter.MacrosUtil.cutLine;

public class MessageCreateAction extends TSDispatchAction {

    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            MessageForm mf = (MessageForm) form;
            String[] val = mf.getDeleteMessage();
            if (val != null)
                for (String aVal : val)
                    AdapterManager.getInstance().getSecuredMessageAdapterManager().deleteMessage(sc, aVal);
            return mapping.findForward("messagePage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            if (request.getParameter("DELETE") != null) return delete(mapping, form, request, response);
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(null, sc, request, true);
            String referer = request.getContextPath() + "/TaskViewAction.do?method=page&amp;id=" + id;
            sc.setRequestAttribute(request, "referer", referer);
            sc.setRequestAttribute(request, "NOT_CHOOSEN", I18n.getString(sc, "NOT_CHOOSEN"));
            MessageForm mf = (MessageForm) form;
            String mstatusId = mf.getMstatus();
            SecuredTaskBean tci = new SecuredTaskBean(id, sc);
            if (sc.getAttribute("messageForm" + id) != null) {
                MessageForm mmf = (MessageForm) sc.getAttribute("messageForm" + id);
                if (mmf.getMstatus().equals(mstatusId)) {
                    mf.restore(mmf);
                    mstatusId = mf.getMstatus();
                    mf.setMutable(false);
                    Map<String, List<SecuredTaskBean>> udfsTasks = new HashMap<String, List<SecuredTaskBean>>();
                    for (SecuredUDFValueBean sub : tci.getWorkflowUDFValues()) {
                        if (sub.getType().equals("task")) {
                            Object o = mf.getUdf(sub.getId());
                            if (o != null) {
                                List<SecuredTaskBean> selected = (List<SecuredTaskBean>) sub.getValue();
                                String[] nums = (String[]) o;
                                List<SecuredTaskBean> uTasks = new ArrayList<SecuredTaskBean>();
                                for (String number : nums) {
                                    SecuredTaskBean task = AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskByNumber(sc, number);
                                    if (selected == null || !selected.contains(task)) {
                                        uTasks.add(task);
                                    }
                                }
                                udfsTasks.put(sub.getId(), uTasks);
                            }
                        }
                    }
                    sc.setRequestAttribute(request, "udfsTasks", udfsTasks);
                }
            }
            String returnToTask = tci.getParentId();
            mf.setReturnToTask(returnToTask);
            mf.setSession(sc.getSession());
            sc.setRequestAttribute(request, "canEditTaskActualBudget", sc.canAction(Action.editTaskActualBudget, id));
            if (mstatusId == null) {
                throw new UserException("Sorry. MstatusId not found.");
            }
            ArrayList<String> handlerList = KernelManager.getStep().getHandlerList(mstatusId, id);
            ArrayList<String> handlerGroupList = KernelManager.getStep().getHandlerGroupList(mstatusId, id);

            ArrayList<SecuredUserBean> handlers = new ArrayList<SecuredUserBean>();
            for (String aHandlerColl : handlerList) {
                SecuredUserBean us = new SecuredUserBean(aHandlerColl, sc);
                handlers.add(us);
            }
            Collections.sort(handlers);
            ArrayList<SecuredPrstatusBean> handlerGroups = new ArrayList<SecuredPrstatusBean>();
            for (String aHandlerGroupList : handlerGroupList) {
                SecuredPrstatusBean us = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, aHandlerGroupList);
                handlerGroups.add(us);
            }

            Collections.sort(handlerGroups);

            ArrayList<SecuredResolutionBean> resolutionList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getResolutionList(sc, mstatusId);
            ArrayList<Pair> resolutions = new ArrayList<Pair>();
            String defResId = null;
            boolean defaultResolution = false;
            for (SecuredResolutionBean us : resolutionList) {
                if (us.isDefault()) {
                    defaultResolution = true;
                    defResId = us.getId();
                }
                resolutions.add(new Pair(us.getId(), us.getName()));
            }
            Collections.sort(resolutions);


            if (defResId != null)
                mf.setResolution(defResId);
            mf.setHandler(tci.getHandlerGroupId() == null ? tci.getHandlerUserId() : "PR_" + tci.getHandlerGroupId());
            if (mf.getHandler() == null) mf.setHandler(""); // Nobody


            TreeSet<SecuredUserBean> participantsInSortOrder = new TreeSet<SecuredUserBean>();
            ArrayList<SecuredMessageBean> listMes = AdapterManager.getInstance().getSecuredMessageAdapterManager().getMessageList(sc, tci.getId());

            // ���������� �� ������ ��������, �������� ����������
            if (listMes != null && listMes.size() > 0) {
                for (SecuredMessageBean b : listMes) {
                    SecuredUserBean ASubmitter = b.getSubmitter();
                    SecuredUserBean AHandler = b.getHandlerUserId() != null ? b.getHandlerUser() : null;

                    if (AHandler != null && !participantsInSortOrder.contains(AHandler) && handlers.contains(AHandler))
                        participantsInSortOrder.add(AHandler);
                    if (!participantsInSortOrder.contains(ASubmitter) && handlers.contains(ASubmitter))
                        participantsInSortOrder.add(ASubmitter);
                }
            }
            SecuredUserBean handler = tci.getHandlerUser();
            if (handler != null && !participantsInSortOrder.contains(handler) && handlers.contains(handler))
                participantsInSortOrder.add(handler);
            // ��������� � ������ ������ ������ ������
            SecuredUserBean submitter = tci.getSubmitter();


            if (handlers.contains(submitter) && !participantsInSortOrder.contains(submitter)) {
                participantsInSortOrder.add(submitter);

            }

            // ������� ���, ��� ��� �������� �������
            handlers.removeAll(participantsInSortOrder);
              /*
              if (handlers.isEmpty() && participantsInSortOrder.isEmpty() && handlerGroups.isEmpty()){
                  // ��������� � ������ ������ �������� ��������������

                  if (handler!=null && !participantsInSortOrder.contains(handler)) {
                      participantsInSortOrder.add(handler);

                  }
                  if (tci.getHandlerGroupId()!=null) {
                      // ��������� ��� � ������, ��� ��� ���� �������������
                      handlerGroups.add(tci.getHandlerGroup());
                  }
              }
              */
            Collections.reverse(listMes);
            List<SecuredMessageBean> messages = new ArrayList<SecuredMessageBean>();
            boolean showAudit = Preferences.isViewAutidTrail(sc.getUser().getPreferences());
            List<String> tasksId = new ArrayList<String>();
            for (SecuredMessageBean message : listMes) {
                if (CSVImport.LOG_MESSAGE.equals(message.getMstatus().getName())) {
                    if (showAudit) {
                        messages.add(message);
                    }
                } else {
                    messages.add(message);
                    tasksId.add(message.getTaskId());
                }
            }
            if (sc.canAction(Action.viewTaskAttachments, tci.getId())) {
                Map<String, List<AttachmentCacheItem>> attachmentMaps = AdapterManager.getInstance().getSecuredMessageAdapterManager().getCollectAttachmentsForMessage(sc, tci.getId(), tasksId);
                sc.setRequestAttribute(request, "attachmentsMsg", attachmentMaps);
            }
            sc.setRequestAttribute(request, "defaultResolution", defaultResolution);
            sc.setRequestAttribute(request, "resolutions", resolutions);
            sc.setRequestAttribute(request, "participantsInSortOrder", participantsInSortOrder);
            List<SecuredUserBean> handlerAll = new ArrayList<SecuredUserBean>(handlers);
            handlerAll.addAll(participantsInSortOrder);
            Map<String, String> handlerRole = GeneralAction.getInstance().handlerRole(sc, request, handlerAll, tci.getId());
            sc.setRequestAttribute(request, "handlerMap", handlerRole);
            String pref = tci.getCategory().getPreferences();
            sc.setRequestAttribute(request, "handlers", Preferences.isHandlerOnlyRole(pref) ? new ArrayList<SecuredUserBean>(0) : handlers);
            sc.setRequestAttribute(request, "messages", messages);
            sc.setRequestAttribute(request, "handlerGroups", handlerGroups);
            ArrayList<SecuredPriorityBean> pc = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getPriorityList(sc, tci.getWorkflowId());
            ArrayList<Pair> priorities = new ArrayList<Pair>();
            for (SecuredPriorityBean pri : pc) {
                priorities.add(new Pair(pri.getId(), pri.getName(), pri.getOrder().toString()));
            }
            Collections.reverse(priorities);
            sc.setRequestAttribute(request, "priorities", priorities);
            mf.setPriority(tci.getPriorityId());

            DateFormatter df = sc.getUser().getDateFormatter();
            if (tci.getDeadline() != null) mf.setDeadline(df.parse(tci.getDeadline()));
            sc.setRequestAttribute(request, "pattern2", df.getPattern2());

            HourFormatter budgetFormatter = new HourFormatter(tci.getBudget(), tci.getBudgetFormat(), sc.getLocale());
            if (tci.getBudget() != null) {
                mf.setBudgetDoubleYears(budgetFormatter.getYears());
                mf.setBudgetIntegerYears(budgetFormatter.getYears().intValue());
                mf.setBudgetDoubleMonths(budgetFormatter.getMonths());
                mf.setBudgetIntegerMonths(budgetFormatter.getMonths().intValue());
                mf.setBudgetDoubleWeeks(budgetFormatter.getWeeks());
                mf.setBudgetIntegerWeeks(budgetFormatter.getWeeks().intValue());
                mf.setBudgetDoubleDays(budgetFormatter.getDays());
                mf.setBudgetIntegerDays(budgetFormatter.getDays().intValue());
                mf.setBudgetDoubleHours(budgetFormatter.getHours());
                mf.setBudgetIntegerHours(budgetFormatter.getHours().intValue());
                mf.setBudgetDoubleMinutes(budgetFormatter.getMinutes());
                mf.setBudgetIntegerMinutes(budgetFormatter.getMinutes().intValue());

                mf.setBudgetIntegerSeconds(budgetFormatter.getSeconds().intValue());

            }
            sc.setRequestAttribute(request, "budgetY", budgetFormatter.getFormat().indexOf("Y") > -1);
            sc.setRequestAttribute(request, "budgetM", budgetFormatter.getFormat().indexOf("M") > -1);
            sc.setRequestAttribute(request, "budgetW", budgetFormatter.getFormat().indexOf("W") > -1);
            sc.setRequestAttribute(request, "budgetD", budgetFormatter.getFormat().indexOf("D") > -1);
            sc.setRequestAttribute(request, "budgeth", budgetFormatter.getFormat().indexOf("h") > -1);
            sc.setRequestAttribute(request, "budgetm", budgetFormatter.getFormat().indexOf("m") > -1);
            sc.setRequestAttribute(request, "budgets", budgetFormatter.getFormat().indexOf("s") > -1);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_OVERVIEW);
            sc.setRequestAttribute(request, "canShowNobody", !tci.getCategory().getHandlerRequired());
            sc.setRequestAttribute(request, "canEditHandler", sc.canAction(Action.editTaskHandler, id));
            sc.setRequestAttribute(request, "canEditPriority", sc.canAction(Action.editTaskPriority, id));
            sc.setRequestAttribute(request, "canEditDeadline", sc.canAction(Action.editTaskDeadline, id));
            sc.setRequestAttribute(request, "canEditBudget", sc.canAction(Action.editTaskBudget, id));
            sc.setRequestAttribute(request, "canViewResolution", sc.canAction(Action.viewTaskResolution, id));

            sc.setRequestAttribute(request, "canCreateTaskMessageAttachments", sc.canAction(Action.createTaskMessageAttachments, id));
            sc.setRequestAttribute(request, "handler", tci.getHandler());
            sc.setRequestAttribute(request, "taskId", id);
            Object nextObject = sc.getAttribute("next");
            if (nextObject != null) {
                sc.setRequestAttribute(request, "next", nextObject);
            }
            mf.setId(id);

            List<SecuredUDFValueBean> list = tci.getUdfValuesList();
            List<SecuredUDFValueBean> listWorkflow = tci.getWorkflowUDFValues();
            if (listWorkflow != null) {
                for (SecuredUDFValueBean udf : listWorkflow) {
                    if (!list.contains(udf)) {
                        list.add(udf);
                    }
                }
            }
            Set<SecuredUDFValueBean> udfValue = new TreeSet<SecuredUDFValueBean>(list);
            UDFFormFillHelper.fillUdf(tci, tci.getId(), mf, udfValue, request, "messageForm" + id, true, true, null);

            SecuredMstatusBean mstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, mstatusId);
            sc.setRequestAttribute(request, "mstatus", mstatus);

            String[] val = mf.getDeleteMessage();
            if (val != null) {
                StringBuffer b = new StringBuffer();
                for (String aVal : val) {
                    SecuredMessageBean messageBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findMessageById(sc, aVal);
                    String descr = messageBean.getDescription();
                    String author = messageBean.getSubmitter().getName();
                    b.append("<pre>");
                    b.append(I18n.getString(sc, "QUOTE"));
                    b.append(" <b>");
                    b.append(author).append(" ");
                    b.append("</b>");
                    String host = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
                    b.append("<a href='").append(host).append(request.getContextPath()).append("/task/").append(tci.getNumber()).append("?thisframe=true#").append(aVal).append("'>").append(df.parse(messageBean.getTime())).append("</a>");
                    b.append(":</pre>");
                    b.append("<blockquote style=\"color:blue; margin-left: 40px;\">");
                    b.append(descr);
                    b.append("</blockquote>");
                    b.append("<br>");
                }
                mf.setBugnote(b.toString());
            }
            Cookie[] cook = request.getCookies();
            String selectedIds = "";
            if (cook != null) {

                for (Cookie c : cook) {
                    log.debug(c.getName() + c.getValue());
                    if (c.getName().equals("_selectedId") && c.getValue() != null && c.getValue().length() > 0) {
                        selectedIds = c.getValue();
                    }
                }
            }
            ArrayList<SecuredTaskBean> selected = new ArrayList<SecuredTaskBean>();
            for (String s : selectedIds.split(UdfConstants.SPLIT_SEPARATOR)) {
                SecuredTaskBean t = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskById(sc, s);
                if (t != null) selected.add(t);

            }
            sc.setRequestAttribute(request, "selectedIds", selected);
            sc.setRequestAttribute(request, "viewTaskUdf", true);
            ArrayList<SecuredTaskAttachmentBean> container = tci.getAttachments();
            if (container != null) {
                sc.setRequestAttribute(request, "attachments", container);
            }
            sc.setRequestAttribute(request, "canViewTaskAttachments", sc.canAction(Action.viewTaskAttachments, id));
            sc.removeAttribute("messageForm" + id);
            return mapping.findForward("messageCreateJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    private void detectErrorInScript(HttpServletRequest request, List<SecuredUDFValueBean> list) throws GranException {
        try {
            UDFFormFillHelper.isValidateScript(list);
        } catch (UserException e) {
            ActionMessages msg = new ActionMessages();
            msg.add("msg", new ActionMessage(I18n.getString("LOOKUP_SCRIPT_EXCEPTION"), false));
            saveMessages(request, msg);
        }
    }


    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            String param = request.getParameter("paramsubmit");
            if ("GOPARENT".equals(param))
                return saveGoToParent(mapping, form, request, response);
            if ("GONEXT".equals(param))
                return saveGoToNext(mapping, form, request, response);
            MessageForm mf = (MessageForm) form;
            saveMessage(request, mf);
            String id = request.getParameter("id");
            ActionForward af = new ActionForward(mapping.findForward("taskViewPage").getPath() + "&id=" + id);
            af.setRedirect(true);
            return af;
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    private void saveMessage(HttpServletRequest request, MessageForm mf) throws GranException {
        SessionContext sc = (SessionContext) request.getAttribute("sc");
        String id = request.getParameter("id");
        SecuredTaskBean tci = new SecuredTaskBean(id, sc);
        try {
            mf.setMutable(false);

            String priority = mf.getPriority();
            priority = priority != null ? (priority.equals(tci.getPriorityId()) ? tci.getPriorityId() : priority) : null;
            String deadline = mf.getDeadline();
            Calendar calendDeadline = null;
            if (deadline != null && deadline.length() > 0) {
                calendDeadline = sc.getUser().getDateFormatter().parseToCalendar(deadline);
            }
            Long budget = HourFormatter.parseInput(mf.getBudgetYears(), mf.getBudgetMonths(), mf.getBudgetWeeks(), mf.getBudgetDays(), mf.getBudgetHours(), mf.getBudgetMinutes(), mf.getBudgetSeconds());
            Long hrs = HourFormatter.parseInput(mf.getActualBudgetYears(), mf.getActualBudgetMonths(), mf.getActualBudgetWeeks(), mf.getActualBudgetDays(), mf.getActualBudgetHours(), mf.getActualBudgetMinutes(), mf.getActualBudgetSeconds());

            String handler = mf.getHandler();
            String handlerGroup = null;
            String handlerUser = null;
            if (handler != null) {
                if (handler.length() == 0) {//handler == None
                    handlerUser = "";
                    handlerGroup = "";
                } else {
                    if (handler.startsWith("PR_"))
                        handlerGroup = handler.substring("PR_".length());
                    else
                        handlerUser = handler;
                }
            }
            List<SecuredUDFValueBean> list = tci.getUdfValuesList();
            List<SecuredUDFValueBean> listWorkflow = tci.getWorkflowUDFValues();
            if (listWorkflow != null) {
                list.addAll(listWorkflow);
            }
            String context = request.getContextPath();
            Set<SecuredUDFValueBean> udfValue = new TreeSet<SecuredUDFValueBean>(list);
            HashMap<String, String> udfMap = new UDFFormFillHelper().getUdfMap(sc, id, udfValue, mf, mf.getMstatus(), null);
            String description = mf.getBugnote();
            ArrayList<AttachmentArray> atts = new ArrayList<AttachmentArray>();
            if (description.contains("src=\"data:image/png;base64,")) {
                description = MacrosUtil.parseImagesFromText(atts, description, id);
            }
            if (mf.getFile() != null && ((List) mf.getFile()).size() != 0) {
                atts.addAll(AttachmentEditAction.uploadForm(mf));
            }
            String resolution = isNotNull(mf.getResolution()) ? mf.getResolution() : null;
            String newId = TriggerManager.getInstance().createMessage(sc, id, mf.getMstatus(), description, hrs, handlerUser, handlerGroup, resolution, priority, calendDeadline, budget, udfMap, true, atts);
            if (mf.getFile() != null && ((List) mf.getFile()).size() != 0)
                AttachmentEditAction.destroyForm(mf);
            Calendar now = new GregorianCalendar();
            now.setTimeInMillis(System.currentTimeMillis());

            SecuredTaskBean stb = new SecuredTaskBean(id, sc);
            String imageServlet = "/ImageServlet/" + GeneralAction.SERVLET_KEY;
            String statusIcon = MacrosUtil.buildImageForState(stb.getStatus(), context + imageServlet);
            if (!stb.getId().equals("1"))
                sc.setAttribute("jsEvent",
                        new ChangeEvent(ChangeEvent.EVENT_TASK_UPDATED,
                                stb.getParent() != null && stb.getParent() != null ? stb.getParent().getNumber() : "1",
                                new String[]{context + imageServlet + "/icons/categories/" + stb.getCategory().getIcon()},
                                new String[]{statusIcon},
                                new String[]{"#" + stb.getNumber() + " " + HTMLEncoder.encodeTree(stb.getName())},
                                new String[]{HTMLEncoder.encodeTree(stb.getName())},
                                new String[]{"javascript:{self.top.frames[1].location = '" + context + "/task/" + stb.getNumber() + "?thisframe=true'; childs='" + stb.getChildrenCount() + "(" + AdapterManager.getInstance().getSecuredTaskAdapterManager().getTotalNotFinishChildren(sc, stb.getId()) + ")'; sortorder='" + Preferences.isSortOrgerInTree(stb.getCategory().getPreferences()) + "'; hide='" + Preferences.getHiddenInTree(stb.getCategory().getPreferences()) + "';}"},
                                new String[]{stb.getId()},
                                new String[]{}, new String[]{}, ""));
            else
                sc.setAttribute("jsEvent",
                        new ChangeEvent(ChangeEvent.EVENT_TASK_UPDATED,
                                stb.getParent() != null && stb.getParent() != null ? stb.getParent().getNumber() : "1",
                                new String[]{context + imageServlet + "/icons/categories/" + stb.getCategory().getIcon()},
                                new String[]{statusIcon},
                                new String[]{"#" + stb.getNumber() + " " + HTMLEncoder.encodeTree(stb.getName())},
                                new String[]{HTMLEncoder.encodeTree(stb.getName())},
                                new String[]{"javascript:{self.top.frames[1].location = '" + context + "/task/" + stb.getNumber() + "?thisframe=true'; childs='" + stb.getChildrenCount() + "(" + AdapterManager.getInstance().getSecuredTaskAdapterManager().getTotalNotFinishChildren(sc, stb.getId()) + ")'; sortorder='" + Preferences.isSortOrgerInTree(stb.getCategory().getPreferences()) + "'; hide='" + Preferences.getHiddenInTree(stb.getCategory().getPreferences()) + "';}"},
                                new String[]{stb.getId()},
                                new String[]{}, new String[]{}, ""));
        } catch (TriggerException te) {
            sc.setAttribute("messageForm" + id, mf);
            sc.setAttribute("defaultMstatus", mf.getMstatus());
            throw te;
        } catch (UserExceptionAfterTrigger ueat) {
            ActionMessages msg = new ActionMessages();
            msg.add("msg", new ActionMessage(ueat.getMessage(), false));
            sc.setAttribute("after_user_exception", msg);
        } catch (UserException ue) {
            sc.setAttribute("messageForm" + id, mf);
            throw ue;
        }
    }

    public ActionForward saveGoToParent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            MessageForm mf = (MessageForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            saveMessage(request, mf);

            sc.setRequestAttribute(request, "id", mf.getReturnToTask());
            mf.setId(mf.getReturnToTask());
            mf.setMutable(false);
            return mapping.findForward("taskAction");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward saveGoToNext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            MessageForm mf = (MessageForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            saveMessage(request, mf);
            Object nextObject = sc.getAttribute("next");
            if (nextObject != null) {
                String s = ((SecuredTaskBean) nextObject).getId();
                sc.setRequestAttribute(request, "id", s);

                mf.setId(s);
                ActionForward af = new ActionForward(mapping.findForward("taskViewPage").getPath() + "&id=" + s);
                af.setRedirect(true);
                return af;
            }
            mf.setMutable(false);

            return mapping.findForward("taskAction");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    /**
     * This method cuts the name for task which should be created by comments or operations
     * @param mf form
     * @return string name
     * @throws GranException  for unpredictable situation
     */
    private String cutNameForTask(MessageForm mf) throws GranException {
        String desc = mf.getSelectedText();
        String[] val = mf.getDeleteMessage();
        if (val != null && val.length > 0) {
            MessageCacheItem message = TaskRelatedManager.findMessage(val[0]);
            if (message != null && isNull(desc)) {
                desc = message.getDescription();
            }
        }
        return desc;
    }

    /**
     * This method creates the new task by comments or operations
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception for unpredictable situation
     */
    public ActionForward createTaskByOperation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        String id = request.getParameter("id");
        MessageForm mf = (MessageForm) form;
        SessionContext sc = (SessionContext) request.getAttribute("sc");
        try {
            boolean createByThisLevel = Config.isTurnItOn("trackstudio.create.by.operation.this.level", "true");
            String desc = cutNameForTask(mf);
            if (isNotNull(desc)) {
                String name = cutLine(desc);
                name = name.length() > 200 ? name.substring(0, 200) : name;
                SecuredTaskBean tci = new SecuredTaskBean(id, sc);
                TaskBuilder taskBuilder = new TaskBuilder();
                taskBuilder.setUpdatedate(Calendar.getInstance());
                taskBuilder.setSc(sc);
                taskBuilder.setCategoryId(tci.getCategoryId());
                taskBuilder.setShortname(tci.getShortname());
                taskBuilder.setName(name);
                taskBuilder.setDescription(desc);
                taskBuilder.setBudget(tci.getBudget());
                taskBuilder.setDeadline(tci.getDeadline());
                taskBuilder.setPriorityId(tci.getPriorityId());
                taskBuilder.setParentId(createByThisLevel ? tci.getParentId() : tci.getId());
                taskBuilder.setHandlerUserId(tci.getHandlerUserId());
                taskBuilder.setHandlerGroupId(tci.getHandlerGroupId());
                taskBuilder.setUdfValues(UDFFormFillHelper.simplifyUdf(tci));
                taskBuilder.setStatusId(KernelManager.getWorkflow().getStartStateId(tci.getWorkflowId()));
                taskBuilder.setSubmitterId(sc.getUserId());
                taskBuilder.setCopyOrMoveOpr(false);
                taskBuilder.setNeedSend(true);
                id = TriggerManager.getInstance().createTask(SecuredTaskTriggerBean.build(taskBuilder, TaskBuilder.Action.CREATE));
                SecuredTaskBean stb = new SecuredTaskBean(id, sc);
                String context = request.getContextPath();
                String imageServlet = "/ImageServlet/" + GeneralAction.SERVLET_KEY;
                String statusIcon = MacrosUtil.buildImageForState(stb.getStatus(), context + imageServlet);
                sc.setAttribute("jsEvent",
                        new ChangeEvent(ChangeEvent.EVENT_TASK_ADDED,
                                stb.getParent() != null && stb.getParent().getParent() != null ? stb.getParent().getParent().getNumber() : "1",
                                new String[]{context + imageServlet + "/icons/categories/" + stb.getCategory().getIcon()},
                                new String[]{statusIcon},
                                new String[]{stb.getNumber()},
                                new String[]{HTMLEncoder.encodeTree(stb.getName())},
                                new String[]{"javascript:{self.top.frames[1].location = '" + context + "/task/" + stb.getNumber() + "?thisframe=true';  childs='" + stb.getChildrenCount() + "(" + 0 + ")'; sortorder='" + Preferences.isSortOrgerInTree(stb.getCategory().getPreferences()) + "'; hide='" + Preferences.getHiddenInTree(stb.getCategory().getPreferences()) + "';}"},
                                new String[]{stb.getId()},
                                new String[]{},
                                new String[]{},
                                stb.getParentId()));
            }
        } catch (TriggerException ueat) {
            ActionMessages msg = new ActionMessages();
            msg.add("msg", new ActionMessage(ueat.getMessage(), false));
            sc.setAttribute("after_user_exception", msg);
        } finally {
            if (w) lockManager.releaseConnection();
        }
        ActionForward af = new ActionForward(mapping.findForward("taskViewPage").getPath() + "&id=" + id);
        af.setRedirect(true);
        return af;
    }
}
