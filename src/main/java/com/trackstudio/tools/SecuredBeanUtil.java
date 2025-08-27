package com.trackstudio.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.trackstudio.app.UdfValue;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.filter.UserFValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.AttachmentCacheItem;
import com.trackstudio.kernel.cache.MessageCacheItem;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.model.Acl;
import com.trackstudio.model.Attachment;
import com.trackstudio.model.Bookmark;
import com.trackstudio.model.Category;
import com.trackstudio.model.CurrentFilter;
import com.trackstudio.model.Filter;
import com.trackstudio.model.MailImport;
import com.trackstudio.model.Mstatus;
import com.trackstudio.model.Notification;
import com.trackstudio.model.Priority;
import com.trackstudio.model.Prstatus;
import com.trackstudio.model.Registration;
import com.trackstudio.model.Report;
import com.trackstudio.model.Resolution;
import com.trackstudio.model.Status;
import com.trackstudio.model.Subscription;
import com.trackstudio.model.Template;
import com.trackstudio.model.Transition;
import com.trackstudio.model.Udflist;
import com.trackstudio.model.Workflow;
import com.trackstudio.secured.SecuredAclBean;
import com.trackstudio.secured.SecuredBookmarkBean;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredCurrentFilterBean;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredMailImportBean;
import com.trackstudio.secured.SecuredMessageAttachmentBean;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredNotificationBean;
import com.trackstudio.secured.SecuredPriorityBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredRegistrationBean;
import com.trackstudio.secured.SecuredReportBean;
import com.trackstudio.secured.SecuredResolutionBean;
import com.trackstudio.secured.SecuredStatusBean;
import com.trackstudio.secured.SecuredSubscriptionBean;
import com.trackstudio.secured.SecuredTaskAclBean;
import com.trackstudio.secured.SecuredTaskAttachmentBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTaskFValueBean;
import com.trackstudio.secured.SecuredTemplateBean;
import com.trackstudio.secured.SecuredTransitionBean;
import com.trackstudio.secured.SecuredUdflistBean;
import com.trackstudio.secured.SecuredUserAclBean;
import com.trackstudio.secured.SecuredUserAttachmentBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.secured.SecuredUserFValueBean;
import com.trackstudio.secured.SecuredWorkflowBean;

import net.jcip.annotations.Immutable;

/**
 * Класс для преобразования объектов Model в их Secured Bean версии
 */
@Immutable
public class SecuredBeanUtil {
    private static final LockManager lockManager = LockManager.getInstance();
    /**
     * Тип - правило доступа
     */
    public static final int ACL = 1;
    /**
     * Тип - категория
     */
    public static final int CATEGORY = 2;
    /**
     * Тип - текущий фильтр
     */
    public static final int CURRENTFILTER = 4;
    /**
     * Тип - фильтр
     */
    public static final int FILTER = 6;
    /**
     * Тип - тип фильтра
     */
    public static final int FILTERTYPE = 7;
    /**
     * Тип - параметры фильтрации
     */
    public static final int FVALUE = 8;
    /**
     * Тип - импорт почты
     */
    public static final int MAILIMPORT = 9;
    /**
     * Тип - сообщение
     */
    public static final int MESSAGE = 10;
    /**
     * Тип - тип сообщения
     */
    public static final int MSTATUS = 11;
    /**
     * Тип - нотификация
     */
    public static final int NOTIFICATION = 12;
    /**
     * Тип - приоритет
     */
    public static final int PRIORITY = 13;
    /**
     * Тип - статус
     */
    public static final int PRSTATUS = 14;
    /**
     * Тип - правило регистрации
     */
    public static final int REGISTRATION = 15;
    /**
     * Тип - отчет
     */
    public static final int REPORT = 16;
    /**
     * Тип - резолюция
     */
    public static final int RESOLUTION = 17;
    /**
     * Тип - тип отчета
     */
    public static final int RTYPE = 18;
    /**
     * Тип - состояние
     */
    public static final int STATUS = 21;
    /**
     * Тип - подписка
     */
    public static final int SUBSCRIPTION = 22;
    /**
     * Тип - задача
     */
    public static final int TASK = 23;
    /**
     * Тап - параметры фильтрации задач
     */
    public static final int TASKFVALUE = 24;
    /**
     * Тип - пользовательское поле для задачи
     */
    public static final int TASKUDF = 25;
    /**
     * Тип - пользовательское поле
     */
    public static final int UDF = 26;
    /**
     * Тип - список пользовательских полей
     */
    public static final int UDFLIST = 27;
    /**
     * Тип - значение пользовательского поля
     */
    public static final int UDFVALUE = 28;
    /**
     * Тип - пользователь
     */
    public static final int USER = 29;
    /**
     * Тип - параметры фильтрации для пользователей
     */
    public static final int USERFVALUE = 30;
    /**
     * Тип - пользовательское поле для пользователя
     */
    public static final int USERUDF = 31;
    /**
     * Тип - процесс
     */
    public static final int WORKFLOW = 32;
    /**
     * Тип - пользовательское поля для процесса
     */
    public static final int WORKFLOWUDF = 33;
    /**
     * Тип - неизветный
     */
    public static final int UNKNOWN = 34;
    /**
     * Тип - приложенный файл
     */
    public static final int ATTACHMENT = 35;
    /**
     * Тип - переход между состояниями
     */
    public static final int TRANSITION = 37;
    /**
     * Тип - шаблон
     */
    public static final int TEMPLATE = 38;
    /**
     * Тип - закладка
     */
    public static final int BOOKMARK = 39;

    /**
     * Возвращает список Secured-бинов, список создается на основе передаваемых в метод моделей и типа создаваемого Secured-бина.
     * <br/>
     * В список попадают только те объекты, которые удовлетворяют условиям видимости для передаваемого пользователя.
     *
     * @param sc        сессия пользователя
     * @param modelList список моделей
     * @param type      тип Secured-бина
     * @return список Secured-бинов
     * @throws GranException при необходимости
     * @see com.trackstudio.model
     */
    public static List toList(SessionContext sc, Collection modelList, int type) throws GranException {
        boolean w = lockManager.acquireConnection(SecuredBeanUtil.class.getSimpleName());
        List<Object> result = new ArrayList<Object>();
        try {
        for (Object o : modelList) {
            switch (type) {
                case ACL:
                    Acl acl = (Acl) o;
                    if (acl.getTask() != null) {
                        SecuredAclBean securedAclBean = new SecuredTaskAclBean((Acl) o, sc);
                        if (securedAclBean.canView())
                            result.add(securedAclBean);
                    } else {
                        SecuredAclBean securedAclBean = new SecuredUserAclBean((Acl) o, sc);
                        if (securedAclBean.canView())
                            result.add(securedAclBean);
                    }
                    break;
                case BOOKMARK:
                    SecuredBookmarkBean sbb = new SecuredBookmarkBean((Bookmark) o, sc);
                    result.add(sbb);
                    break;
                case CATEGORY:
                    SecuredCategoryBean securedCategoryBean = new SecuredCategoryBean((Category) o, sc);
                    if (securedCategoryBean.canView())
                        result.add(securedCategoryBean);
                    break;
                case CURRENTFILTER:
                    SecuredCurrentFilterBean filterBean = new SecuredCurrentFilterBean((CurrentFilter) o, sc);
                    if (filterBean.canView())
                        result.add(filterBean);
                    break;
                case FILTER:
                    SecuredFilterBean o1 = new SecuredFilterBean((Filter) o, sc);
                    if (o1.canView())
                        result.add(o1);
                    break;
                case MAILIMPORT:
                    SecuredMailImportBean o3 = new SecuredMailImportBean((MailImport) o, sc);
                    if (o3.canView())
                        result.add(o3);
                    break;
                case MESSAGE:
                    SecuredMessageBean securedMessageBean = new SecuredMessageBean((MessageCacheItem) o, sc);
                    if (securedMessageBean.canView())
                        result.add(securedMessageBean);
                    break;
                case MSTATUS:
                    SecuredMstatusBean mstatusBean = new SecuredMstatusBean((Mstatus) o, sc);
                    if (mstatusBean.canView())
                        result.add(mstatusBean);
                    break;
                case NOTIFICATION:
                    SecuredNotificationBean securedNotificationBean = new SecuredNotificationBean((Notification) o, sc);
                    if (securedNotificationBean.canView())
                        result.add(securedNotificationBean);
                    break;
                case PRIORITY:
                    SecuredPriorityBean priorityBean = new SecuredPriorityBean((Priority) o, sc);
                    if (priorityBean.canView())
                        result.add(priorityBean);
                    break;
                case PRSTATUS:
                    SecuredPrstatusBean prstatusBean = new SecuredPrstatusBean((Prstatus) o, sc);
                    if (prstatusBean.canView())
                        result.add(prstatusBean);
                    break;
                case REGISTRATION:
                    SecuredRegistrationBean securedRegistrationBean = new SecuredRegistrationBean((Registration) o, sc);
                    if (securedRegistrationBean.canView())
                        result.add(securedRegistrationBean);
                    break;
                case REPORT:
                    SecuredReportBean securedReportBean = new SecuredReportBean((Report) o, sc);
                    if (securedReportBean.canView())
                        result.add(securedReportBean);
                    break;
                case RESOLUTION:
                    SecuredResolutionBean resolutionBean = new SecuredResolutionBean((Resolution) o, sc);
                    if (resolutionBean.canView())
                        result.add(resolutionBean);
                    break;

                case STATUS:
                    SecuredStatusBean statusBean = new SecuredStatusBean((Status) o, sc);
                    if (statusBean.canView())
                        result.add(statusBean);
                    break;
                case SUBSCRIPTION:
                    result.add(new SecuredSubscriptionBean((Subscription) o, sc));
                    break;
                case TASK:
                    if (o instanceof TaskRelatedInfo) {
                        SecuredTaskBean securedTaskBean = new SecuredTaskBean((TaskRelatedInfo) o, sc);
                        if (securedTaskBean.canView())
                            result.add(securedTaskBean);
                    } else {
                        SecuredTaskBean securedTaskBean1 = new SecuredTaskBean((String) o, sc);
                        if (securedTaskBean1.canView())
                            result.add(securedTaskBean1);
                    }
                    break;
                case TASKFVALUE:
                    SecuredTaskFValueBean securedTaskFValueBean = new SecuredTaskFValueBean((FValue) o, sc);
                    if (securedTaskFValueBean.canView())
                        result.add(securedTaskFValueBean);
                    break;
                case TASKUDF:
                    throw new GranException("Use UdfManager instead");
                case UDFLIST:
                    SecuredUdflistBean udflistBean = new SecuredUdflistBean((Udflist) o, sc);
                    if (udflistBean.canView())
                        result.add(udflistBean);
                    break;
                case UDFVALUE:
                    throw new GranException ("UdfValue.getTaskId() and .getUserId() always return null");
/*                    UdfValue value = (UdfValue) o;
                    if (value.getTaskId() != null) {
                        SecuredUDFValueBean udfValueBean = new SecuredUDFValueBean(value, new SecuredTaskBean(value.getTaskId(), sc));
                        if (udfValueBean.canView())
                            result.add(udfValueBean);
                    } else {
                        SecuredUDFValueBean udfValueBean = new SecuredUDFValueBean(value, new SecuredUserBean(value.getUserId(), sc));
                        if (udfValueBean.canView())
                            result.add(udfValueBean);
                    }
                    break; */
                case USER:
                    if (o instanceof UserRelatedInfo) {
                        SecuredUserBean userBean = new SecuredUserBean((UserRelatedInfo) o, sc);
                        if (userBean.canView())
                            result.add(userBean);
                    } else {
                        SecuredUserBean userBean = new SecuredUserBean((String) o, sc);
                        if (userBean.canView())
                            result.add(userBean);
                    }
                    break;
                case USERFVALUE:
                    SecuredUserFValueBean securedUserFValueBean = new SecuredUserFValueBean((FValue) o, sc);
                    if (securedUserFValueBean.canView())
                        result.add(securedUserFValueBean);
                    break;
                case USERUDF:
                    throw new GranException("Use UdfManager instead");
                case WORKFLOW:
                    SecuredWorkflowBean workflowBean = new SecuredWorkflowBean((Workflow) o, sc);
                    if (workflowBean.canView())
                        result.add(workflowBean);
                    break;
                case TRANSITION:
                    SecuredTransitionBean transitionBean = new SecuredTransitionBean((Transition) o, sc);
                    if (transitionBean.canView())
                        result.add(transitionBean);
                    break;
                case WORKFLOWUDF:
                    throw new GranException("Use UdfManager instead");
                case ATTACHMENT: {
                    AttachmentCacheItem ci = (AttachmentCacheItem) o;
                    if (ci.getMessageId() != null) {
                        SecuredMessageAttachmentBean bean = new SecuredMessageAttachmentBean(ci, sc);
                        if (bean.canView())
                            result.add(bean);
                    } else if (ci.getTaskId() != null) {
                        SecuredTaskAttachmentBean bean1 = new SecuredTaskAttachmentBean(ci, sc);
                        if (bean1.canView())
                            result.add(bean1);
                    } else {
                        SecuredUserAttachmentBean securedUserAttachmentBean = new SecuredUserAttachmentBean(ci, sc);
                        if (securedUserAttachmentBean.canView())
                            result.add(securedUserAttachmentBean);
                    }
                }
                break;
                case TEMPLATE:
                    SecuredTemplateBean bean2 = new SecuredTemplateBean((Template) o, sc);
                    if (bean2.canView())
                        result.add(bean2);
                    break;
                default:
                    throw new GranException("bad type");

            }
        }
        } finally {
            if (w) lockManager.releaseConnection(SecuredBeanUtil.class.getSimpleName());
        }
        return result;
    }

    /**
     * Возвращает список Secured-бинов, список создается на основе передаваемых в метод моделей и типа создаваемого Secured-бина.
     * <br/>
     * В список попадают только те объекты, которые удовлетворяют условиям видимости для передаваемого пользователя.
     *
     * @param sc        сессия пользователя
     * @param modelList список моделей
     * @param type      тип Secured-бина
     * @return список Secured-бинов
     * @throws GranException при необходимости
     * @see com.trackstudio.model
     */
    public static ArrayList toArrayList(SessionContext sc, Collection modelList, int type) throws GranException {
        return (ArrayList) toList(sc, modelList, type);
    }

    /**
     * Возвращает список Secured-бинов, список создается на основе передаваемых в метод моделей. Тип создаваемого Secured-бина определяется автоматически
     * <br/>
     * В список попадают только те объекты, которые удовлетворяют условиям видимости для передаваемого пользователя.
     *
     * @param sc        сессия пользователя
     * @param modelList список моделей
     * @return список Secured-бинов
     * @throws GranException при необходимости
     * @see com.trackstudio.model
     */
    @Deprecated
    public static ArrayList toArrayList(SessionContext sc, Collection modelList) throws GranException {
        if (modelList.isEmpty())
            return new ArrayList();
        Object o = modelList.iterator().next();
        int type;
        if (o instanceof Acl)
            type = ACL;
        else if (o instanceof Category)
            type = CATEGORY;
        else if (o instanceof CurrentFilter)
            type = CURRENTFILTER;
        else if (o instanceof Filter)
            type = FILTER;
        else if (o instanceof MailImport)
            type = MAILIMPORT;
        else if (o instanceof MessageCacheItem)
            type = MESSAGE;
        else if (o instanceof Mstatus)
            type = MSTATUS;
        else if (o instanceof Notification)
            type = NOTIFICATION;
        else if (o instanceof Priority)
            type = PRIORITY;
        else if (o instanceof Prstatus)
            type = PRSTATUS;
        else if (o instanceof Registration)
            type = REGISTRATION;
        else if (o instanceof Report)
            type = REPORT;
        else if (o instanceof Resolution)
            type = RESOLUTION;
        else if (o instanceof Status)
            type = STATUS;
        else if (o instanceof Subscription)
            type = SUBSCRIPTION;
        else if (o instanceof TaskRelatedInfo)
            type = TASK;
        else if (o instanceof TaskFValue)
            type = TASKFVALUE;
        else if (o instanceof Udflist)
            type = UDFLIST;
        else if (o instanceof UdfValue)
            type = UDFVALUE;
        else if (o instanceof UserRelatedInfo)
            type = USER;
        else if (o instanceof UserFValue)
            type = USERFVALUE;
        else if (o instanceof Workflow)
            type = WORKFLOW;
        else if (o instanceof Transition)
            type = TRANSITION;
        else if (o instanceof Attachment)
            type = ATTACHMENT;
        else if (o instanceof Template)
            type = TEMPLATE;
        else
            type = UNKNOWN;
        return toArrayList(sc, modelList, type);
    }

    /**
     * Возвращает список Secured-бинов, список создается на основе передаваемых в метод моделей. Тип создаваемого Secured-бина определяется автоматически
     * <br/>
     * Прав видимости не производится
     * <br/>
     * Когда мы составляем списки handlerList и handlerGroupList,
     * то нам НЕ нужно проверять CanView(), т.к. список ответстенных в любом случае
     * должен быть полным (см комментарии в таске #63831])
     * Соответственно все типы нам тут не нужны, а нужны только user и prstatus
     *
     * @param sc        сессия пользователя
     * @param modelList список моделей
     * @return список Secured-бинов
     * @throws GranException при необходимости
     * @see com.trackstudio.model
     */
    public static ArrayList toArrayListWithoutCanView(SessionContext sc, Collection modelList) throws GranException {
        if (modelList.isEmpty())
            return new ArrayList();
        Object o = modelList.iterator().next();
        int type;
        if (o instanceof Prstatus)
            type = PRSTATUS;
        else if (o instanceof UserRelatedInfo)
            type = USER;
        else
            type = UNKNOWN;
        return toArrayListWithoutCanView(sc, modelList, type);
    }

    /**
     * Возвращает список Secured-бинов, список создается на основе передаваемых в метод моделей и типа создаваемого Secured-бина.
     * <br/>
     * Прав видимости не производится
     *
     * @param sc        сессия пользователя
     * @param modelList список моделей
     * @param type      тип Secured-бина
     * @return список Secured-бинов
     * @throws GranException при необходимости
     * @see com.trackstudio.model
     */
    public static ArrayList toArrayListWithoutCanView(SessionContext sc, Collection modelList, int type) throws GranException {
        ArrayList<Object> result = new ArrayList<Object>();
        for (Object o : modelList) {
            switch (type) {
                case USER:
                    if (o instanceof UserRelatedInfo) {
                        result.add(new SecuredUserBean((UserRelatedInfo) o, sc));
                    } else {
                        result.add(new SecuredUserBean((String) o, sc));
                    }
                    break;
                case PRSTATUS:
                    result.add(new SecuredPrstatusBean((Prstatus) o, sc));
                    break;
                default:
                    throw new GranException("bad type");
            }
        }
        return result;
    }
}