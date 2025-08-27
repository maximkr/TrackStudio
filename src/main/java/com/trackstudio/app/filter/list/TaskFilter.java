package com.trackstudio.app.filter.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.UdfValue;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.AbstractFilter;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.filter.TaskPreFilter;
import com.trackstudio.app.filter.comparator.TaskComparator;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.secured.SecuredWorkflowUDFBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Key;

/**
 * Класс содержит методы для фильтрации задач
 */
public class TaskFilter extends AbstractFilter {
    private static final Log log = LogFactory.getLog(TaskFilter.class);
    private static final LockManager lockManager = LockManager.getInstance();
    private final SecuredTaskBean task;
    private final static int processorCount = Runtime.getRuntime().availableProcessors();
    private final static ExecutorService service = Executors.newFixedThreadPool(processorCount);
    private final static int MIN_JOB_SIZE_FOR_THREAD = 100; // 300 tasks per thread

    /**
     * Конструктор
     *
     * @param task задача
     * @see com.trackstudio.secured.SecuredTaskBean
     */
    public TaskFilter(SecuredTaskBean task) {
        this.task = task;
    }


    /**
     * Проверяет соответствие пользовательских полей задачи условиям фильтрации
     *
     * @param securedTask задача
     * @param flt         параметры фильтрации
     * @return TRUE - соответствует, FALSE = нет
     * @throws GranException при необходимости
     */
    public boolean passUDFProperties(SecuredTaskBean securedTask, TaskFValue flt) throws GranException {
        return passUdf(flt, securedTask);
    }

    public boolean passUDFProperties(SecuredTaskBean securedTask, TaskFValue flt, List<SecuredUDFValueBean> udfValues) throws GranException {
        return passUdf(securedTask, flt, udfValues);
    }

    /**
     * Проверяет соответствие сообщений задачи условиям фильтрации
     *
     * @param securedTask задача
     * @param flt         параметры фильтрации
     * @return TRUE - соответствует, FALSE = нет
     * @throws GranException при необходимости
     */
    public boolean passMessageProperties(SecuredTaskBean securedTask, TaskFValue flt, List<String> useForMessages) throws GranException {
        boolean requiredMessageFound = true;
        if (!useForMessages.isEmpty() &&
                (useForMessages.contains(FieldMap.MSG_SUSER_NAME.getFilterKey())
                        || useForMessages.contains(FieldMap.MSG_SUBMITDATE.getFilterKey())
                        || useForMessages.contains(TaskFValue.MSG_TYPE)
                        || useForMessages.contains(FieldMap.MSG_HUSER_NAME.getFilterKey())
                        || useForMessages.contains(FieldMap.MSG_RESOLUTION.getFilterKey())
                        || useForMessages.contains(FieldMap.MSG_ABUDGET.getFilterKey())
                        || useForMessages.contains(TaskFValue.MSG_TEXT))) {
            requiredMessageFound = false;
            ArrayList<SecuredMessageBean> messages = securedTask.getMessages();
            for (SecuredMessageBean message : messages) {
                if ((new MessageFilter(task)).pass(message, flt)) {
                    requiredMessageFound = true;
                    break;
                }
            }
        }
        return requiredMessageFound;
    }

    /**
     * Проверяет соответствие полей задачи условиям фильтрации
     *
     * @param securedTask задача
     * @param flt         параметры фильтрации
     * @return TRUE - соответствует, FALSE = нет
     * @throws GranException при необходимости
     */
    public boolean passTaskProperties(SecuredTaskBean securedTask, TaskFValue flt, List<String> useForTask) throws GranException {
        boolean needAdd = true;
        for (String u : useForTask) {
            if (u.equals(FieldMap.TASK_NUMBER.getFilterKey()))
                needAdd = testNumber(flt, FieldMap.TASK_NUMBER.getFilterKey(), new Integer(securedTask.getNumber()));
            else if (u.equals(FieldMap.TASK_NAME.getFilterKey()))
                needAdd = testString(flt, FieldMap.TASK_NAME.getFilterKey(), securedTask.getName());
            else if (u.equals(FieldMap.TASK_SHORTNAME.getFilterKey()))
                needAdd = testString(flt, FieldMap.TASK_SHORTNAME.getFilterKey(), securedTask.getProjectAlias());
            else if (u.equals(FieldMap.FULLPATH.getFilterKey()))
                needAdd = testString(flt, FieldMap.FULLPATH.getFilterKey(), securedTask.getName());
            else if (u.equals(FieldMap.TASK_UPDATEDATE.getFilterKey()))
                needAdd = testTimestamp(flt, FieldMap.TASK_UPDATEDATE.getFilterKey(), securedTask.getUpdatedate());
            else if (u.equals(FieldMap.TASK_SUBMITDATE.getFilterKey()))
                needAdd = testTimestamp(flt, FieldMap.TASK_SUBMITDATE.getFilterKey(), securedTask.getSubmitdate());
            else if (u.equals(FieldMap.TASK_CLOSEDATE.getFilterKey()))
                needAdd = testTimestamp(flt, FieldMap.TASK_CLOSEDATE.getFilterKey(), securedTask.getClosedate());
            else if (u.equals(FieldMap.TASK_DEADLINE.getFilterKey()))
                needAdd = testTimestamp(flt, FieldMap.TASK_DEADLINE.getFilterKey(), securedTask.getDeadline());
            else if (u.equals(FieldMap.TASK_BUDGET.getFilterKey()))
                needAdd = testNumber(flt, FieldMap.TASK_BUDGET.getFilterKey(), securedTask.getBudget());
            else if (u.equals(FieldMap.TASK_ABUDGET.getFilterKey()))
                needAdd = testNumber(flt, FieldMap.TASK_ABUDGET.getFilterKey(), securedTask.getActualBudget());
            else if (u.equals(FieldMap.HUSER_NAME.getFilterKey())) {
                if (securedTask.getHandlerGroupId() == null) {
                    List<String> list = new ArrayList<String>();
                    if (securedTask.getHandlerUserId() != null) list.add(securedTask.getHandlerUserId());
                    needAdd = testMultiList(flt, FieldMap.HUSER_NAME.getFilterKey(), list, securedTask.getSecure().getUserId());
                } else {
                    needAdd = testList(flt, FieldMap.HUSER_NAME.getFilterKey(), "GROUP_" + securedTask.getHandlerGroupId());
                    if (!needAdd) {
                        List<String> values = flt.get(FieldMap.HUSER_NAME.getFilterKey());
                        if (!values.isEmpty()) {
                            for (String handler : values) {
                                SecuredUserBean user = null;
                                if (handler.equals("null")) continue;
                                if (handler.equals("CurrentUserID") || handler.equals("IandSubUsers") || handler.equals("IandManager") || handler.equals("IandManagers")) {
                                    user = securedTask.getSecure().getUser();
                                    if (user != null && user.getId().equals(securedTask.getHandlerId())) {
                                        needAdd = true;
                                    }
                                } else {
                                    if (handler.startsWith("GROUP_")) {
                                        if (handler.equals("GROUP_MyActiveGroup")) {
                                            needAdd = testActiveGroupHandler(flt, FieldMap.HUSER_NAME.getFilterKey(), securedTask.getHandlerGroupId(), new ArrayList<String>(task.getSecure().getAllowedPrstatusesForTask(securedTask.getId())));
                                        }
                                    } else {
                                        user = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(securedTask.getSecure(), handler);
                                    }
                                }
                                if (user != null && user.getId().equals(securedTask.getHandlerId())) {
                                    needAdd = true;
                                }
                            }
                        }
                    }
                }
            } else if (u.equals(FieldMap.TASK_PRIORITY.getFilterKey()))
                needAdd = testList(flt, FieldMap.TASK_PRIORITY.getFilterKey(), securedTask.getPriorityId());
            else if (u.equals(FieldMap.TASK_MESSAGECOUNT.getFilterKey()))
                needAdd = testNumber(flt, FieldMap.TASK_MESSAGECOUNT.getFilterKey(), securedTask.getMessageCount());
            else if (u.equals(FieldMap.TASK_CHILDCOUNT.getFilterKey()))
                needAdd = testNumber(flt, FieldMap.TASK_CHILDCOUNT.getFilterKey(), securedTask.getTotalChildrenCount());

            else if (u.equals(FieldMap.TASK_CATEGORY.getFilterKey()))
                needAdd = testList(flt, FieldMap.TASK_CATEGORY.getFilterKey(), securedTask.getCategoryId());
            else if (u.equals(FieldMap.TASK_STATUS.getFilterKey()))
                needAdd = testList(flt, FieldMap.TASK_STATUS.getFilterKey(), securedTask.getStatusId());
            else if (u.equals(FieldMap.TASK_RESOLUTION.getFilterKey()))
                needAdd = testList(flt, FieldMap.TASK_RESOLUTION.getFilterKey(), securedTask.getResolutionId());
            else if (u.equals(FieldMap.SUSER_NAME.getFilterKey()))
                needAdd = testUser(flt, FieldMap.SUSER_NAME.getFilterKey(), securedTask.getSubmitterId(), task.getSecure().getUserId());

            else if (u.equals(FieldMap.SUSER_STATUS.getFilterKey()))
                needAdd = testMultiList(flt, FieldMap.SUSER_STATUS.getFilterKey(), securedTask.getSubmitterId() != null ? new ArrayList<String>(securedTask.getSubmitterPrstatusesId()) : null, securedTask.getSecure().getUserId());
            else if (u.equals(FieldMap.HUSER_STATUS.getFilterKey()))
                if (securedTask.getHandlerGroupId() == null)
                    needAdd = testMultiList(flt, FieldMap.HUSER_STATUS.getFilterKey(), securedTask.getHandlerUserId() != null ? new ArrayList<String>(securedTask.getHandlerPrstatusesId()) : null, securedTask.getSecure().getUserId());
                else {
                    ArrayList<String> s = new ArrayList<String>();
                    s.add(securedTask.getHandlerGroupId());
                    needAdd = testMultiList(flt, FieldMap.HUSER_STATUS.getFilterKey(), securedTask.getHandlerGroupId() != null ? s : null, securedTask.getSecure().getUserId());
                }
            else if (u.equals(FieldMap.TASK_DESCRIPTION.getFilterKey())) {
                needAdd = testString(flt, FieldMap.TASK_DESCRIPTION.getFilterKey(), securedTask.getDescription());
            } else if (u.equals(FieldMap.LAST_MSG_SUBMITDATE.getFilterKey())) {
                List<SecuredMessageBean> msgs = SecuredTaskBean.getMessageCheckedAudit(securedTask.getMessages(), false);
                needAdd = !msgs.isEmpty() && testTimestamp(flt, FieldMap.LAST_MSG_SUBMITDATE.getFilterKey(), msgs.get(msgs.size() - 1).getTime());
            }
            if (!needAdd)
                break;
        }
        return needAdd;
    }

    /**
     * Возвращает список отфильтрованных задач
     *
     * @param flt            Параметры фильтрации
     * @param withUDF        Нужна ли фильтрация пользовательских полей
     * @param withoutSubTask Нужен ли глубокий поиск
     * @param sortorder      Порядок сортировки
     * @return Список задач
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredTaskBean
     */
    public ArrayList<SecuredTaskBean> getTaskList(final TaskFValue flt, boolean withUDF, boolean withoutSubTask, List<String> sortorder) throws GranException {
        boolean w = lockManager.acquireConnection(TaskFilter.class.getSimpleName());
        try {
            //String s;
            // Get children map
            final Map<String, Boolean> childrenVisibilityMap;
            if (withoutSubTask) {
                childrenVisibilityMap = task.getAllowedChildrenMap();
            } else {
                childrenVisibilityMap = task.getAllowedChildrenWithSubtasksMap();
            }
            Set<String> childrenPassedSet = childrenVisibilityMap.keySet();
            // Searches if keywords exists
            String filterKey = flt.getAsString(FieldMap.SEARCH.getFilterKey());
            if (filterKey != null) {
                childrenPassedSet = TaskPreFilter.fastRetainAll(
                        childrenPassedSet,
                        KernelManager.getIndex().searchTasksWithHighLight(filterKey).keySet()
                );
            }

            Set<String> useForUdf = flt.getUsedUdfIds();
            final ArrayList<SecuredUDFBean> udfList;
            if (withUDF)
                udfList = task.getFilterUDFs();
            else
                udfList = null;
            final boolean initUDF = useForUdf != null && !useForUdf.isEmpty() && udfList != null && !udfList.isEmpty();

            List<String> sortstring = sortorder != null && sortorder.size() != 0 ? sortorder : flt.getSortOrder();

            final Map<Key.KeyID, Boolean> localPermissionCache = new ConcurrentHashMap<Key.KeyID, Boolean>();
            final List<Callable<ArrayList<SecuredTaskBean>>> callable = new ArrayList<>(16);

            final ItemQueue[] queueArray = splitItemSet(childrenPassedSet);

            for (int i = 0; i < queueArray.length; i++) {
                final Set<String> perCPUSet = queueArray[i].getSet();
                callable.add(() -> {
                    LockManager.getInstance().acquireConnection();
                    try {
                        final ArrayList<SecuredTaskBean> tempList = new ArrayList(10000);
                        final List<String> useForTask = flt.getUseForTask();
                        final List<String> useForMessages = flt.getUseForMessages();
                        for (final String id : perCPUSet) {
                            boolean hasAccess = childrenVisibilityMap.get(id);

                            if (!hasAccess)
                                continue;

                            SecuredTaskBean stb = new SecuredTaskBean(id, task.getSecure());
                            if (!stb.canView())
                                continue;

                            if (!stb.isOnSight())
                                continue;

                            if (!passTaskProperties(stb, flt, useForTask))
                                continue;

                            if (initUDF) {
                                ArrayList<SecuredUDFValueBean> udfValuesWithPermissions = getUDFValuesWithPermissions(
                                    stb,
                                    udfList,
                                    localPermissionCache);
                                if (!passUDFProperties(stb, flt, udfValuesWithPermissions))
                                    continue;
                            }

                            if (!passMessageProperties(stb, flt, useForMessages))
                                continue;

                            tempList.add(stb);
                        }
                        return tempList;
                    } catch (GranException e) {
                        log.error("Error", e);
                        return null;
                    } finally {
                        lockManager.releaseConnection();
                    }
                });
            }

            final ArrayList<SecuredTaskBean> topTasks = new ArrayList<SecuredTaskBean>();
            for (Future<ArrayList<SecuredTaskBean>> item : service.invokeAll(callable)) {
                topTasks.addAll(item.get());
            }

            topTasks.sort(new TaskComparator(sortstring, udfList));
            return topTasks;

            // convert to array for faster sorting
            //SecuredTaskBean[] array = topTasks.toArray(new SecuredTaskBean[topTasks.size()]);
            //Arrays.parallelSort(array, new TaskComparator(sortstring, udfList));

            //ArrayList<SecuredTaskBean> result = new ArrayList(array.length);
            //result.addAll(Arrays.asList(array));
            //return result;
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(TaskFilter.class.getSimpleName());
        }
    }

    /**
     * Split one large queue for multiple queues (one for each thread). We cannot use thread per SecuredMessageBean
     * because DBMS connect/disconnect is expensive.
     *
     * Also, we shouldn't create too small sets
     *
     * @param childrenPassedSet
     * @return
     */
    private ItemQueue[] splitItemSet(Set<String> childrenPassedSet) {

        final int queueCount = Math.max(1, Math.min(processorCount, childrenPassedSet.size() / MIN_JOB_SIZE_FOR_THREAD)); // minimum 1
        final ItemQueue[] queue = new ItemQueue[queueCount];
        int counter = 0;

        for (int i = 0; i < queueCount; i++)
            queue[i] = new ItemQueue();

        for (final String id : childrenPassedSet) {
            queue[counter].getSet().add(id);
            counter++;
            if (counter == queueCount)
                counter = 0;
        }
        return queue;
    }

    /**
     * Возвращает список значений пользовательских полей с учетом прав доступа
     *
     * @param task                 задача
     * @param udfList              список пользовательских полей
     * @param localPermissionCache права доступа
     * @return список пользовательских полей
     * @throws GranException при необходимости
     */
    private ArrayList<SecuredUDFValueBean> getUDFValuesWithPermissions(SecuredTaskBean task, ArrayList<SecuredUDFBean> udfList, Map<Key.KeyID, Boolean> localPermissionCache) throws GranException {
        final TaskRelatedInfo tci = task.getTask();
        final ArrayList<SecuredUDFValueBean> udfValuesWithPermissions = new ArrayList<SecuredUDFValueBean>();
        final List<UdfValue> udfValueList = TaskRelatedManager.getInstance().getUDFValues(tci.getId());
        final HashMap<String, UdfValue> udfValueHash = new HashMap<String, UdfValue>(20);

        for (UdfValue uvCurrent : udfValueList)
            udfValueHash.put(uvCurrent.getUdfId(), uvCurrent);

        final Set<String> prstatuses = TaskRelatedManager.getInstance().getAllowedPrstatuses(task.getSecure().getUserId(), tci.getId());

        for (SecuredUDFBean suvb : udfList) {
            //определяем права только для фильтруемых udf-ов (udfHash)
            String udfId = suvb.getUdfId();
            UdfValue uv = udfValueHash.get(udfId);

            if (uv != null) {//задача содержит фильтруемый udf

                //смотрим в кэш
                Key.KeyID key1 = new Key.ListPlusOneParam(prstatuses, udfId);
                Boolean o1 = localPermissionCache.get(key1);
                if (o1 != null) {
                    if (o1)
                        udfValuesWithPermissions.add(new SecuredUDFValueBean(uv, task));
                    continue;
                }

                Key.KeyID key2 = new Key.ListPlusTwoParam(prstatuses, tci.getStatusId(), udfId);
                Boolean o2 = localPermissionCache.get(key2);
                if (o2 != null) {
                    if (o2)
                        udfValuesWithPermissions.add(new SecuredUDFValueBean(uv, task));
                    continue;
                }

                boolean isWFUDF = suvb instanceof SecuredWorkflowUDFBean; //определяем workflow-ный этот udf или нет.
                Key.KeyID key3;
                if (isWFUDF) {
                    key3 = key2;
                } else {
                    key3 = new Key.ListPlusTwoFour(prstatuses, udfId, tci.getHandlerUserId(), tci.getHandlerGroupId(), tci.getSubmitterId());
                }
                Boolean o3 = localPermissionCache.get(key3);
                if (o3 != null) {
                    if (o3)
                        udfValuesWithPermissions.add(new SecuredUDFValueBean(uv, task));
                    continue;
                }


                Key.KeyID key = key1;
                boolean canView = false;

                if (isWFUDF) {//для workflow udf сначала определяем нет ли права доступа ко всем статусам
                    canView = KernelManager.getUdf().isUdfAvailableForUser(UdfConstants.STATUS_VIEW_ALL, suvb.getUdfId(), prstatuses);
                    if (!canView)//если есть, то statusId в ключ не добавляем
                        key = key2;
                }

                //если udf не workflow-ных либо workflow-ный, но для него установлено право STATUS_VIEW_ALL (что должно быть в большинстве случаев), то определяем наличие права VIEW_ALL
                canView = (!isWFUDF || canView) && KernelManager.getUdf().isUdfAvailableForUser(UdfConstants.VIEW_ALL, suvb.getUdfId(), prstatuses);

                if (!canView) {//Если быстрым путем права не определились, то добавляем в key submitterId/handlerId и определяем права общим способом
                    key = key3;
                    canView = KernelManager.getUdf().isTaskUdfViewableFast(prstatuses, tci.getId(), task.getSecure().getUserId(), suvb.getUdfId());
                }
                localPermissionCache.put(key, canView);

                if (canView)
                    udfValuesWithPermissions.add(new SecuredUDFValueBean(uv, task));
            }
        }
        return udfValuesWithPermissions;
    }

    public static List<String> getHandlerValues(List<String> propertyValuesCollection, SessionContext sc) throws GranException {
        List<String> values = new ArrayList<String>();
        if (propertyValuesCollection != null)
            for (String pvString : propertyValuesCollection) {
                if (pvString.equals("0")) continue;
                if (pvString.equals("CurrentUserID"))
                    values.add("--" + I18n.getString(sc.getLocale(), "I_AM") + "--");
                else if (pvString.equals("IandSubUsers"))
                    values.add("--" + I18n.getString(sc.getLocale(), "ME_AND_SUBORDINATED") + "--");
                else if (pvString.equals("IandManager"))
                    values.add("--" + I18n.getString(sc.getLocale(), "ME_AND_MANAGER") + "--");
                else if (pvString.equals("IandManagers"))
                    values.add("--" + I18n.getString(sc.getLocale(), "ME_AND_MANAGERS") + "--");
                else if (pvString.equals("GROUP_MyActiveGroup"))
                    values.add("--" + I18n.getString(sc.getLocale(), "MSG_MY_ACTIVE_GROUP") + "--");
                else if (pvString.startsWith("GROUP_") && !pvString.equals("GROUP_MyActiveGroup"))
                    values.add(AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, pvString.substring("GROUP_".length())).getName());
                else if (pvString.equals("null"))
                    values.add("--" + I18n.getString(sc.getLocale(), "NONE") + "--");
                else if (UserRelatedManager.getInstance().isUserExists(pvString))
                    values.add(new SecuredUserBean(pvString, sc).getName());
            }
        return values;
    }
}
