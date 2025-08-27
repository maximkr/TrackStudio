package com.trackstudio.app.adapter.email;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.concurrent.FJTask;
import org.concurrent.FJTaskRunnerGroup;

import com.trackstudio.action.TemplateServlet;
import com.trackstudio.app.CalculatedValue;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.adapter.email.change.Change;
import com.trackstudio.app.adapter.email.change.NewAttachmentChange;
import com.trackstudio.app.adapter.email.change.NewMessageChange;
import com.trackstudio.app.adapter.email.change.SubscriptionReason;
import com.trackstudio.app.adapter.email.change.TestChange;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.FilterSettings;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.filter.list.MessageFilter;
import com.trackstudio.app.filter.list.TaskFilter;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.app.session.SessionManager;
import com.trackstudio.common.FieldMap;
import com.trackstudio.constants.FilterConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.jmx.NotificationMXBeanImpl;
import com.trackstudio.jmx.SubscriptionMXBeanImpl;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Filter;
import com.trackstudio.model.Subscription;
import com.trackstudio.model.Usersource;
import com.trackstudio.secured.SecuredAttachmentBean;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.simple.Notification;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.HibernateUtil;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.TableTransform;
import com.trackstudio.tools.TemplateUtil;
import com.trackstudio.tools.formatter.DateFormatter;
import com.trackstudio.tools.textfilter.HTMLEncoder;
import com.trackstudio.tools.textfilter.Wiki;
import com.trackstudio.view.TaskViewEmailHTML;
import com.trackstudio.view.TaskViewFactory;
import com.trackstudio.view.UDFValueViewReport;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateHashModel;
import net.jcip.annotations.Immutable;

/**
 * Класс адаптер, управляет рассылкой уведомлений и подписок
 */
@Immutable
public class FilterNotifyAdapterManager {

    private static final Log log = LogFactory.getLog(FilterNotifyAdapterManager.class);
    private static final HibernateUtil hu = new HibernateUtil();
    private static final LockManager lockManager = LockManager.getInstance();
    /**
     * Конструктор по умолчанию
     */
    public FilterNotifyAdapterManager() {
    }

    /**
     * Отправляет сообщение для задачи
     *
     * @param source нотификация
     * @param fi     ID фильтра
     * @param toUser ID пользователя, которому шлем
     * @param task   ID задачи
     * @param reason изменение
     * @return TRUE - если сообщение успешно отправлено, FALSE - если нет
     * @throws GranException при необходимости
     */
    public boolean sendMsg(com.trackstudio.simple.Notification source, String fi, String toUser, String task, Change reason, boolean testMode) throws GranException {
        boolean result = false;
        try {
            SendMsgFJTask sendMsgFJTask = new SendMsgFJTask(source, fi, toUser, task, reason, null, true, null);
            sendMsgFJTask.setTestMode(testMode);
            log.debug("\n\n ************ ALERT DISTRIBUTION ***********\n");
            log.debug("\t\tDISTRIBUTION NAME: " + sendMsgFJTask.getNotification().getName());
            log.debug("\t\tFILNET NAME: " + sendMsgFJTask.getNotification().getFilter());
            log.debug("\t\tTASK NAME: " + sendMsgFJTask.getNotification().getTask());
            log.debug("\t\tUSER NAME: " + sendMsgFJTask.getNotification().getUser());
            FJTask.invoke(sendMsgFJTask);
            result = sendMsgFJTask.getResult();
        } catch (Exception e) {
            log.debug("\t\tRESULT DISTRIBUTION: EXCEPTION " + e);
            throw new GranException(e);
        }
        log.debug("\t\tRESULT DISTRIBUTION: " + result);
        log.debug("\n\n");
        return result;
    }

    /**
     * Возвращает текстовое описание причины отсылки уведомления
     *
     * @param sc       сессия пользователя
     * @param reason   причина отсыфлки
     * @param category категория
     * @return Строковое описание причины
     * @throws GranException при необходимости
     */
    public String getReasonForSubject(SessionContext sc, Change reason, String category) throws GranException {
        if (reason.getCode().equals(FilterConstants.FIRE_NEW_TASK) || reason.getCode().equals(FilterConstants.FIRE_NEW_TASK_WITH_ATTACHMENT)) {
            return I18n.getString(sc.getLocale(), "CHANGE_ADD_TASK_TERM") + " - " + category;
        } else if (reason.getCode().equals(FilterConstants.FIRE_UPDATED_TASK)) {
            return I18n.getString(sc.getLocale(), "CHANGE_UPDATE_TASK_TERM");
        } else if (reason.getCode().equals(FilterConstants.FIRE_NEW_ATTACHMENT)) {
            List<SecuredAttachmentBean> list = ((NewAttachmentChange) reason).getAttachments();
            return I18n.getString(sc.getLocale(), "CHANGE_ADD_ATTACHMENT_TERM", new Object[]{list.isEmpty() ? "noname" : list.get(0).getName()});
        } else if (reason.getCode().equals(FilterConstants.FIRE_NEW_MESSAGE) || reason.getCode().equals(FilterConstants.FIRE_NEW_MESSAGE_WITH_ATTACHMENT)) {
            SecuredMessageBean smb = ((NewMessageChange) reason).getMessage();
            String name = (smb == null) ? null : smb.getMstatus().getName();
            return I18n.getString(sc.getLocale(), "CHANGE_ADD_MESSAGE_TERM") + " - " + name;
        } else if (reason.getCode().equals(FilterConstants.FIRE_ON_TEST)) {
            return I18n.getString(sc.getLocale(), "CHANGE_TEST_TERM");
        } else if (reason.getCode().equals(FilterConstants.FIRE_ON_TIME)) {
            return I18n.getString(sc.getLocale(), "SUBSCRIPTION_TERM");
        } else {
            return "";
        }
    }


    /**
     * Возвращает карту с данными для заполнения шаблона
     *
     * @param sc       сессия пользователя
     * @param task     задача
     * @param source   нотификация
     * @param filterId source filter identifier
     * @param reason   причина
     * @param fromUser ID пользователя
     * @return Карта с данными
     * @throws GranException при необзодимости
     */
    public Map<String, Object> getDataMap(SessionContext sc, SecuredTaskBean task, Notification source, String filterId, Change reason, SecuredUserBean fromUser) throws GranException {
        log.trace("getDataMapImpl");
        Map<String, Object> root = new HashMap<String, Object>();
        try {
            root.put("table", new TableTransform());
            root.put("source", BeansWrapper.getDefaultInstance().wrap(source));
            root.put("charset", Config.getEncoding());
            root.put(TemplateServlet.DATE_FORMATTER, BeansWrapper.getDefaultInstance().wrap(new DateFormatter(sc.getTimezone(), sc.getLocale())));
            if (reason != null) {
                reason.setSession(sc);
                root.put("reason", reason);
                root.put("reasonForSubject", getReasonForSubject(sc, reason, task.getCategory().getName()));
            }
            TemplateHashModel staticModels = BeansWrapper.getDefaultInstance().getStaticModels();
            TemplateHashModel hf = (TemplateHashModel) staticModels.get("com.trackstudio.tools.formatter.HourFormatter");
            root.put(TemplateServlet.HOURS_FORMATTER, hf);
            if (Config.getInstance().isFormMailNotification() && task.canManage()) {
                root.put("mailFrom", Config.getInstance().getProperty("mail.from"));
                log.debug("mailFrom is " + Config.getInstance().getSession().getProperty("mail.from"));
            }
            //
            TaskFValue fv = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(sc, filterId).getFValue();
            List view = fv.getView();
            String v = fv.getAsString(FieldMap.MESSAGEVIEW.getFilterKey());
            String prefix = fv.getPrefix(FieldMap.MESSAGEVIEW.getFilterKey());
            Integer msgMax = 0;
            //todo winzard 13.12.2007
            if (!(v == null || v.length() == 0)) {
                msgMax = prefix.equals("_") ? -Integer.parseInt(v) : Integer.parseInt(v);
            }
            root.put("showhistory", msgMax);
            Map<String, Boolean> filter = new HashMap<String, Boolean>();
            filter.put("TASKNUMBER", view.contains(FieldMap.TASK_NUMBER.getFilterKey()));
            filter.put("CATEGORY", view.contains(FieldMap.TASK_CATEGORY.getFilterKey()));
            filter.put("STATUS", view.contains(FieldMap.TASK_STATUS.getFilterKey()));
            filter.put("RESOLUTION", view.contains(FieldMap.TASK_RESOLUTION.getFilterKey()));
            filter.put("SUBMITTER", view.contains(FieldMap.SUSER_NAME.getFilterKey()));
            filter.put("SUBMITTERSTATUS", view.contains(FieldMap.SUSER_STATUS.getFilterKey()));
            filter.put("HANDLER", view.contains(FieldMap.HUSER_NAME.getFilterKey()));
            filter.put("HANDLERSTATUS", view.contains(FieldMap.HUSER_STATUS.getFilterKey()));
            filter.put("DEADLINE", view.contains(FieldMap.TASK_DEADLINE.getFilterKey()));
            filter.put("SUBMITDATE", view.contains(FieldMap.TASK_SUBMITDATE.getFilterKey()));
            filter.put("UPDATEDATE", view.contains(FieldMap.TASK_UPDATEDATE.getFilterKey()));
            filter.put("CLOSEDATE", view.contains(FieldMap.TASK_CLOSEDATE.getFilterKey()));
            filter.put("BUDGET", view.contains(FieldMap.TASK_BUDGET.getFilterKey()));
            filter.put("ABUDGET", view.contains(FieldMap.TASK_ABUDGET.getFilterKey()));
            filter.put("CHILDCOUNT", view.contains(FieldMap.TASK_CHILDCOUNT.getFilterKey()));
            filter.put("MESSAGECOUNT", view.contains(FieldMap.TASK_MESSAGECOUNT.getFilterKey()));
            filter.put("PRIORITY", view.contains(FieldMap.TASK_PRIORITY.getFilterKey()));
            filter.put("ALIAS", view.contains(FieldMap.TASK_SHORTNAME.getFilterKey()));
            filter.put("NAME", view.contains(FieldMap.TASK_NAME.getFilterKey()));
            filter.put("FULLPATH", view.contains(FieldMap.FULLPATH.getFilterKey()));
            filter.put("DESCRIPTION", view.contains(FieldMap.TASK_DESCRIPTION.getFilterKey()));
            filter.put("MESSAGEVIEW", view.contains(FieldMap.MESSAGEVIEW.getFilterKey()));

            root.put("filter", BeansWrapper.getDefaultInstance().wrap(filter));
            root.put("link", Config.getInstance().getSiteURL());
            root.put("fields", BeansWrapper.getDefaultInstance().wrap(view));
            root.put("task", BeansWrapper.getDefaultInstance().wrap(task));
            root.put("user", BeansWrapper.getDefaultInstance().wrap(sc.getUser()));
            root.put("serverEmail", Config.getProperty("mail.from"));
            if (fromUser != null) {
                ArrayList<String> list = fromUser.getEmailList();
                root.put("fromUserEmail", list != null && list.size() > 0 ? list.get(0) : null);
                root.put("fromUserName", fromUser.getName());
            }
            root.put("filterId", filterId);
            root.put("session", BeansWrapper.getDefaultInstance().wrap(sc));
            root.put("Null", new Null());
            root.put(TemplateServlet.I18N, BeansWrapper.getDefaultInstance().wrap(I18n.getInstance()));
            root.put("Util", BeansWrapper.getDefaultInstance().wrap(new TemplateUtil(sc)));
            root.put(TemplateServlet.BSH, BeansWrapper.getDefaultInstance().wrap(CalculatedValue.getInstance()));
            root.put("viewUdfList", task.getUdfValuesList());
            ArrayList<String> sortedUDFHeaderCaptionIds = new ArrayList<String>();
            FilterSettings fs = new FilterSettings(fv, task.getId(), filterId);
            boolean taskUDFView = fs.getSettings().needFilterUDF() && !task.getFilterUDFs().isEmpty();
            HashMap<String, String> udfs = new HashMap<String, String>();
            HashMap<String, String> udfHeaderCaption = new HashMap<String, String>();
            if (taskUDFView) {
                for (SecuredUDFValueBean udf : task.getFilterUDFValues()) {
                    if (fs.getSettings().getView().contains(FValue.UDF + udf.getId())) {
                        udfHeaderCaption.put(udf.getId(), Null.stripNullHtml(HTMLEncoder.encode(udf.getCaption())));
                        udfs.put(udf.getId(), new UDFValueViewReport(udf).getValue(task));
                        sortedUDFHeaderCaptionIds.add(udf.getId());
                    }
                }
            }
            int totalCols = udfHeaderCaption.size();
            for (Map.Entry<String, Boolean> entity : filter.entrySet()) {
                if (entity.getValue()) {
                    totalCols++;
                }
            }
            root.put("totalCols", totalCols - 2);
            root.put("udfs", udfs);
            root.put("udfHeaderCaption", udfHeaderCaption);
            root.put("sortedUdfHeaderCaptionIds", sortedUDFHeaderCaptionIds); // fill-in id of UDFs in right (sorted) order. UdfHeaderCaptions keys can be in any order, they cannot be used
            Wiki wiki = new Wiki(new TaskViewEmailHTML(task));
            root.put(TemplateServlet.WIKI, BeansWrapper.getDefaultInstance().wrap(wiki));
            root.put("ViewFactory", BeansWrapper.getDefaultInstance().wrap(new TaskViewFactory()));

        } catch (Exception ex) {
            throw new GranException(ex);
        }
        return root;
    }

    /**
     * Отсылает уведомление для задачи
     *
     * @param messageId        ID сообщеиия
     * @param taskId           ID задачи
     * @param userId           ID пользователя
     * @param mstatusId        ID типа сообщения
     * @param notificationType тип уведомления
     * @return TRUE - если все успешно
     * @throws GranException при необходмости
     */
    public boolean sendNotifyForTask(final String messageId, final String taskId, final String userId, final  String mstatusId, final Change notificationType) throws GranException {
        if (!Config.getInstance().isSendMail())
            return false;
        log.debug("Send a notifications");
        final boolean testMode = notificationType instanceof TestChange;
        if (testMode) {
            return processNotify(messageId, taskId, userId, mstatusId, notificationType, testMode);
        } else {
            new Thread(){
                public void run() {
                    try {
                        processNotify(messageId, taskId, userId, mstatusId, notificationType, testMode);
                    } catch (GranException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            return true;
        }

    }

    private boolean processNotify(String messageId, String taskId, String userId, String mstatusId, Change notificationType, boolean testMode) throws GranException {
        boolean result = false;
        boolean lock = lockManager.acquireConnection(FilterNotifyAdapterManager.class.getName());
        try {
            HashMap<String, TaskFValue> fvMap = new HashMap<String, TaskFValue>(); // caches FValues for each filter
            LinkedHashMap<String, List<com.trackstudio.model.Notification>> notificationMap = new LinkedHashMap<String, List<com.trackstudio.model.Notification>>();
            String nCode = notificationType.getCode();
            if (nCode.equals(FilterConstants.FIRE_NEW_TASK_WITH_ATTACHMENT)) {
                nCode = FilterConstants.FIRE_NEW_TASK;
            } else if (nCode.equals(FilterConstants.FIRE_NEW_MESSAGE_WITH_ATTACHMENT)) {
                nCode = FilterConstants.FIRE_NEW_MESSAGE;
            }
            List<String> alreadySendedToEmail = new ArrayList<String>();
            List<String> alreadySendedToJabber = new ArrayList<String>();
            if (testMode) {
                TestChange change = (TestChange) notificationType;
                Notification notification = change.getNotification();
                com.trackstudio.model.Notification n = KernelManager.getFind().findNotification(notification.getId());
                List<com.trackstudio.model.Notification> list = new ArrayList<com.trackstudio.model.Notification>();
                list.add(n);
                notificationMap.put(n.getTask().getId(), list);
            } else {
                ArrayList<TaskRelatedInfo> taskTree = TaskRelatedManager.getInstance().getTaskRelatedInfoChain(null, taskId);
                log.debug("Tasks: " + taskTree.size());
                Collections.reverse(taskTree);
                for (TaskRelatedInfo aTaskTree : taskTree) {
                    List<com.trackstudio.model.Notification> list = hu.getList("select n from com.trackstudio.model.Notification as n where n.task=? order by n.filter.name", aTaskTree.getId());
                    if (!list.isEmpty()) {
                        notificationMap.put(aTaskTree.getId(), list);
                    }
                }
            }
            log.debug("Notifications: " + notificationMap.size());
            List<UserRelatedInfo> recepients = KernelManager.getStep().getAllowedMsgRecepientList(taskId);
            log.debug("Recepients: " + recepients.size());
            FJTaskRunnerGroup runnerGroup = Config.getInstance().getFJTaskRunnerGroup();
            // for every single recipient

            SessionContext sessionContext = SessionManager.getInstance().getSessionContext(SessionManager.getInstance().create(UserRelatedManager.getInstance().find(userId)));
            if (sessionContext==null) return false;
            SecuredUserBean fromUser = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(sessionContext, userId);

            for (String ntaskId : notificationMap.keySet()) {
                List<com.trackstudio.model.Notification> notifications = notificationMap.get(ntaskId);
                for (com.trackstudio.model.Notification notification : notifications) {
                    if (testMode) {
                        result = testCase(userId, taskId, notification, fvMap, runnerGroup, notificationType, fromUser);
                    } else {
                        result = productionCase(userId, taskId, notification, fvMap, runnerGroup, recepients, alreadySendedToEmail, alreadySendedToJabber, mstatusId, messageId, notificationType, nCode, fromUser);
                    }
                }
            }
        } finally {
            if (lock) lockManager.releaseConnection(FilterNotifyAdapterManager.class.getName());
        }
        return result;
    }

    private boolean testCase(String userId, String taskId, com.trackstudio.model.Notification notification, HashMap<String, TaskFValue> fvMap, final FJTaskRunnerGroup runnerGroup, Change notificationType, SecuredUserBean fromUser) throws GranException {
        boolean result = false;
        String sessionId = SessionManager.getInstance().create(UserRelatedManager.getInstance().find(userId));
        SessionContext sc = SessionManager.getInstance().getSessionContext(sessionId);
        if (sc == null)//!user.isEnabled() || user.isExpire()
            return false;
        SecuredTaskBean taskBean = new SecuredTaskBean(taskId, sc);

        TaskFilter taskList = new TaskFilter(taskBean);
        Set<String> templates = new HashSet<String>();

        SecuredUserBean user = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(sc, userId);
        if (!user.getEmailList().isEmpty()) {
            String template;
            if (notification.getTemplate() != null && !notification.getTemplate().isEmpty())
                template = notification.getTemplate();
            else template = user.getTemplate();
            if (templates.contains(template))
                return false;
            Filter fb = notification.getFilter();
            TaskFValue fv = fvMap.get(fb.getId());
            if (fv == null) {
                fv = KernelManager.getFilter().getTaskFValue(fb.getId());
                fvMap.put(fb.getId(), fv);
            }


            boolean passTaskMessage = taskList.passTaskProperties(taskBean, fv, fv.getUseForTask())
                    && taskList.passUDFProperties(taskBean, fv); // ��� ���� ����� ���������, ��������� �� �����.

            if (passTaskMessage) {

                TaskRelatedInfo tci = TaskRelatedManager.getInstance().find(notification.getTask().getId());

                HTMLEncoder sb = new HTMLEncoder(tci.getName());
                sb.replace("\r\n", " ");
                sb.replace("\n", " ");

                //��� ������ ����������� notification ������ ������� � ��������� ���������
                //����� notification, � ����� ����� ���������� ��� ����� � ������������ �������.
                com.trackstudio.simple.Notification simpleNotification = buildSimpleNotification(notification);
                simpleNotification.setTemplate(notification.getTemplate() != null ? notification.getTemplate() : UserRelatedManager.getInstance().find(userId).getTemplate());
                if (simpleNotification.getTemplate() != null) {
                    Usersource usersource = notification.getUser();
                    if (usersource.getUser() != null)
                        simpleNotification.setUser(usersource.getUser().getName());
                    else
                        simpleNotification.setUser(usersource.getPrstatus().getName());

                    runnerGroup.executeTask(new SendMsgFJTask(simpleNotification, fb.getId(), userId, taskId, notificationType, fromUser, false, notification.getCondition()));
                    result = true;
                }
                templates.add(template);
            }
        }
        return result;
    }

    private boolean productionCase(String userId, String taskId, com.trackstudio.model.Notification notification, HashMap<String, TaskFValue> fvMap, final FJTaskRunnerGroup runnerGroup, List<UserRelatedInfo> recepients, List<String> alreadySendedToEmail, List<String> alreadySendedToJabber, String mstatusId, String messageId, Change notificationType, String nCode, SecuredUserBean fromUser) throws GranException  {
        boolean result = false;
        com.trackstudio.simple.Notification simpleNotification = buildSimpleNotification(notification);
        for (UserRelatedInfo recipient : recepients) {
            if (recipient.isEnabled()) {
                String sessionId = SessionManager.getInstance().create(recipient);
                SessionContext sc = SessionManager.getInstance().getSessionContext(sessionId);
                if (sc == null)
                    continue;
                SecuredTaskBean taskBean = new SecuredTaskBean(taskId, sc);
                MessageFilter messageList = new MessageFilter(taskBean);
                TaskFilter taskList = new TaskFilter(taskBean);

                if (userId != null && mstatusId != null) {
                    if (!KernelManager.getStep().isMessageViewable(recipient.getId(), taskId, mstatusId))
                        continue;
                }

                // skip notifications for other users
                Usersource usource = KernelManager.getFind().findUsersource(notification.getUser().getId());
                boolean notificationForCurrentUser = usource.getUser() != null && usource.getUser().getId().equals(recipient.getId());
                boolean notificationForCurrentPrstatus = usource.getPrstatus() != null && TaskRelatedManager.getInstance().getAllowedPrstatuses(recipient.getId(), taskId).contains(usource.getPrstatus().getId());

                if (!notificationForCurrentUser && !notificationForCurrentPrstatus)
                    continue;


                Filter fb = notification.getFilter();
                // try to get fv from cache, update cache if required
                TaskFValue fv = fvMap.get(fb.getId());
                if (fv == null) {
                    fv = KernelManager.getFilter().getTaskFValue(fb.getId());
                    fvMap.put(fb.getId(), fv);
                }

                String condition = notification.getCondition();
                boolean notI = condition != null && condition.toUpperCase(Locale.ENGLISH).contains(FilterConstants.NOT_I);

                if (!(notI && sc.getUserId().equals(userId))) {
                    boolean passTaskMessage = taskList.passTaskProperties(taskBean, fv, fv.getUseForTask())
                            && (messageId == null || messageList.pass(new SecuredMessageBean(TaskRelatedManager.findMessage(messageId), sc), fv)) // ��������� ���� ���������� �������
                            && taskList.passUDFProperties(taskBean, fv); // ��� ���� ����� ���������, ��������� �� �����.

                    if (passTaskMessage) {
                        if (notification.getCondition() == null || (notification.getCondition() != null && !notification.getCondition().toUpperCase(Locale.ENGLISH).contains(nCode)))
                            continue;
                        if (notification.getCondition() != null && notification.getCondition().contains("W")) {
                            if (alreadySendedToJabber.contains(recipient.getId()) || alreadySendedToEmail.contains(recipient.getId())) {
                                continue;
                            } else {
                                alreadySendedToJabber.add(recipient.getId());
                                alreadySendedToEmail.add(recipient.getId());
                            }
                        } else if (notification.getCondition() != null && notification.getCondition().contains("J")) {
                            if (alreadySendedToJabber.contains(recipient.getId())) {
                                continue;
                            } else {
                                alreadySendedToJabber.add(recipient.getId());
                            }
                        } else {
                            if (alreadySendedToEmail.contains(recipient.getId())) {
                                continue;
                            } else {
                                alreadySendedToEmail.add(recipient.getId());
                            }
                        }
                        simpleNotification.setTemplate(Null.isNotNull(simpleNotification.getTemplate()) ? simpleNotification.getTemplate() : recipient.getTemplate());
                        if (simpleNotification.getTemplate() != null) {
                            runnerGroup.executeTask(new SendMsgFJTask(simpleNotification, fb.getId(), recipient.getId(), taskId, notificationType, fromUser, false, notification.getCondition()));
                            result = true;
                            String login = recipient != null ? recipient.getLogin() : "null";
                            String notificationLog = simpleNotification != null ? simpleNotification.getName() : "null";
                            String notificationFilter = simpleNotification != null ? simpleNotification.getFilter() : "null";
                            log.debug("SEND EMAIL: login: " + login + " notification: " + notificationLog + " filter: " + notificationFilter);
                            NotificationMXBeanImpl.getInstance().sendUser(simpleNotification, login, nCode, taskId, messageId);
                        }
                    }
                }
            }
        }
        return result;
    }

    // TODO fill-in directly, without findXXX
    private com.trackstudio.simple.Notification buildSimpleNotification(final com.trackstudio.model.Notification notification) throws GranException {
        com.trackstudio.simple.Notification simpleNotification = new com.trackstudio.simple.Notification(notification.getId(), notification.getName());
        simpleNotification.setFilter(KernelManager.getFind().findFilter(notification.getFilter().getId()).getName());
        simpleNotification.setName(notification.getName());
        simpleNotification.setTask(KernelManager.getFind().findTask(notification.getTask().getId()).getName());
        simpleNotification.setTemplate(notification.getTemplate());
        if (simpleNotification.getTemplate() != null) {
            Usersource usersource = KernelManager.getFind().findUsersource(notification.getUser().getId());
            if (usersource.getUser() != null)
                simpleNotification.setUser(KernelManager.getFind().findUser(usersource.getUser().getId()).getName());
            else
                simpleNotification.setUser(KernelManager.getFind().findPrstatus(usersource.getPrstatus().getId()).getName());
        }
        return simpleNotification;
    }

    /**
     * Рассылает подписку на задачу
     *
     * @param subId  ID подписки
     * @param taskId ID задачи
     * @return TRUE - если успешно, FALSE - если нет
     * @throws GranException при необходимости
     */
    public boolean processSubscription(String subId, String taskId, boolean testMode) throws GranException {
        log.debug("START SEND subscription");
        boolean result = false;
        Subscription sub = KernelManager.getFind().findSubscription(subId);
        taskId = taskId != null ? taskId : sub.getTask().getId();
        if (sub.getUser().getUser() != null) {
            // for single user
            if (sub.getTask().getChildSet().size() > 0) {
                if (KernelManager.getUser().getActive(sub.getUser().getUser().getId()) && KernelManager.getUser().getUserExpireDate(sub.getUser().getUser().getId()) > System.currentTimeMillis()) {
                    com.trackstudio.simple.Notification source = new com.trackstudio.simple.Notification(sub.getId(), sub.getName());
                    source.setFilter(sub.getFilter().getName());
                    source.setTask(sub.getTask().getName());
                    source.setTemplate(sub.getTemplate());
                    source.setUser(sub.getUser().getUser().getName());
                    Calendar now = new GregorianCalendar();
                    now.setTimeInMillis(System.currentTimeMillis());
                    Change reason = new SubscriptionReason(now, sub.getUser().getUser().getId(), source);
                    result = AdapterManager.getInstance().getFilterNotifyAdapterManager().sendMsg(source, sub.getFilter().getId(), sub.getUser().getUser().getId(), taskId, reason, testMode);
                }
            }
        } else {
            // for user group
            String groupId = sub.getUser().getPrstatus().getId();
            /* ������� ������������� � ���� ����� */
            List<String> userGroup = UserRelatedManager.getInstance().getUsersForPrstatus(groupId, true);
            List<UserRelatedInfo> allowedUsers = UserRelatedManager.getInstance().getCacheContents();
            UserRelatedManager manager = UserRelatedManager.getInstance();
            /* ���������� ���� ������������� */
            for (UserRelatedInfo uri : allowedUsers) {
                /* ��������� � ������ ����, ���� ������ ���������� ����� ACL */
                // todo � ��� ����� ����� ������ SQL ���� ���������? ����� � ���� ����� ���?

                if (manager.isActive(uri.getId()) && SecuredUserBean.isExistPrstatus(uri.getId(), uri.getPrstatusId(), groupId)) {
                    userGroup.add(uri.getId());
                }
            }
            Set<String> users = new TreeSet<String>(userGroup);
            for (String uId : users) {
                if (TaskRelatedManager.getInstance().onSight(uId, taskId, groupId, true) && UserRelatedManager.getInstance().isActive(uId)) {
                    com.trackstudio.simple.Notification source = new com.trackstudio.simple.Notification(sub.getId(), sub.getName());
                    source.setFilter(sub.getFilter().getName());
                    source.setTask(sub.getTask().getName());
                    source.setTemplate(sub.getTemplate());
                    source.setUser(sub.getUser().getPrstatus().getName());
                    Calendar now = new GregorianCalendar();
                    now.setTimeInMillis(System.currentTimeMillis());
                    Change reason = new SubscriptionReason(now, uId, source);
                    SubscriptionMXBeanImpl.getInstance().sendUser(sub, sub.getUser().getPrstatus().getName(), uId);
                    result = AdapterManager.getInstance().getFilterNotifyAdapterManager().sendMsg(source, sub.getFilter().getId(), uId, taskId, reason, testMode) || result;
                }
            }
        }
        log.debug("FINISHED SEND subscription");
        return result;
    }
}
