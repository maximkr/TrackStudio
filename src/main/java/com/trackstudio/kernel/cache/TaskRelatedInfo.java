package com.trackstudio.kernel.cache;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.UdfValue;
import com.trackstudio.app.udf.GenericValue;
import com.trackstudio.app.udf.ListMultiValue;
import com.trackstudio.app.udf.ListValue;
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.tools.GZIPCompression;
import com.trackstudio.tools.Intern;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * Класс для хренения данных о задаче
 */
@ThreadSafe
public class TaskRelatedInfo implements Comparable {
    private final ReentrantReadWriteLock udfLock = new ReentrantReadWriteLock();
    private static final Log log = LogFactory.getLog(TaskRelatedInfo.class);

    private final AtomicInteger childCount;

    @GuardedBy("udfLock")
    private CopyOnWriteArrayList<UDFCacheItem> UDFs = null; // ArrayList of UDF of all udfs

    @GuardedBy("udfLock")
    private CopyOnWriteArrayList<UDFCacheItem> workflowUDFs = null; // ArrayList of UDF of all udfs

    @GuardedBy("udfLock")
    private CopyOnWriteArrayList<UdfValue> UDFValues = null; // ArrayList of UDFValue. Stores values for ALL udfs: task's and workflow's

    @GuardedBy("udfLock")
    private CopyOnWriteArrayList<UdfValue> workflowUDFValues = null;

    private final String id;
    private final String shortname; //persistent
    private final String name;
    private final Long submitdate; //persistent
    private final AtomicLong updatedate; //persistent
    private final AtomicLong closedate; //persistent

    private final AtomicReference<byte[]> description;

    private final long abudget; //persistent
    private final long budget; //persistent
    private final Long deadline; //persistent
    private final int number;
    private final String submitterId;
    private final String handlerId;
    private final String handlerUserId;
    private final String handlerGroupId;
    private final String parentId;
    private final String categoryId;
    private final String workflowId;
    private final String statusId;
    private final String resolutionId;
    private final String priorityId;
    private final String longtextId;
    private final AtomicReference<ConcurrentMap> acl;
    private final AtomicLong actualBudget;

    /**
     * Конструктор для инициализвации по id
     *
     * @param id ID задачи
     */
    public TaskRelatedInfo(String id) {
        this.id = id; // Do not wrap id with intern, short-lived objects, called often, too slow
        this.childCount = null;
        this.description = null;
        this.actualBudget = null;
        this.longtextId = null;
        this.name = null;
        this.shortname = null;
        this.submitdate = null;
        this.updatedate = null;
        this.closedate = null;
        this.abudget = 0;
        this.budget = 0;
        this.deadline = null;
        this.number = 0;
        this.submitterId = null;
        this.handlerId = null;
        this.handlerUserId = null;
        this.handlerGroupId = null;
        this.parentId = null;
        this.categoryId = null;
        this.workflowId = null;
        this.statusId = null;
        this.resolutionId = null;
        this.priorityId = null;
        this.acl = null;
    }

    /**
     * Конструктор для полной инициализвации
     *
     * @param id             ID задачи
     * @param description    Описание задачи
     * @param longtextId     ID объекта longtext, в котором хранится описание задачи, если его длина превышает 2000 символов
     * @param name           Название задачи
     * @param shortname      Алиас задачи
     * @param submitdate     Дата создания задачи
     * @param updatedate     Дата последнего обновления задачи
     * @param closedate      Дата закрытия задачи
     * @param abudget        Время, потраченное на задачу
     * @param budget         Бюджет задачи
     * @param deadline       Дата дедлайна задачи
     * @param number         Номер задачи
     * @param submitterId    ID пользователя - автора задачи
     * @param handlerId      ID ответственного (usersource)
     * @param handlerUserId  ID ответственного пользователя
     * @param handlerGroupId ID ответственной группы
     * @param parentId       ID родительской задачи
     * @param categoryId     ID категории задачи
     * @param workflowId     ID процесса
     * @param statusId       ID статуса задачи
     * @param resolutionId   ID резолюции задачи
     * @param priorityId     ID проиоритета задачи
     */
    public TaskRelatedInfo(String id, String description, String longtextId, String name, String shortname, Calendar submitdate,
                           Calendar updatedate, Calendar closedate, Long abudget, Long budget, Calendar deadline, String number,
                           String submitterId, String handlerId, String handlerUserId, String handlerGroupId, String parentId,
                           String categoryId, String workflowId, String statusId, String resolutionId, String priorityId) {
        this.id = Intern.process(id);
        this.childCount = new AtomicInteger(0);
        this.description = new AtomicReference<byte[]>(GZIPCompression.compress(description));
        this.actualBudget = new AtomicLong(0);
        this.longtextId = longtextId;
        this.name = name; // also used for quick search
        this.shortname = shortname;
        if (submitdate != null) {
            this.submitdate = submitdate.getTimeInMillis();
        } else {
            this.submitdate = null;
        }
        if (updatedate != null) {
            this.updatedate = new AtomicLong(updatedate.getTimeInMillis());
        } else {
            this.updatedate = null;
        }


        if (closedate != null) {
            this.closedate = new AtomicLong(closedate.getTimeInMillis());
        } else {
            this.closedate = null;
        }

        if (abudget != null)
            this.abudget = abudget;
        else
            this.abudget = 0;
        if (budget != null)
            this.budget = budget;
        else
            this.budget = 0;
        if (deadline != null) {
            this.deadline = deadline.getTimeInMillis();
        } else {
            this.deadline = null;
        }
        this.number = Integer.valueOf(number);
        this.submitterId = Intern.process(submitterId);
        this.handlerId = Intern.process(handlerId);
        this.handlerUserId = Intern.process(handlerUserId);
        this.handlerGroupId = Intern.process(handlerGroupId);
        this.parentId = Intern.process(parentId);
        this.categoryId = Intern.process(categoryId);
        this.workflowId = Intern.process(workflowId);
        this.statusId = Intern.process(statusId);
        this.resolutionId = Intern.process(resolutionId);
        this.priorityId = Intern.process(priorityId);

        this.acl = new AtomicReference<ConcurrentMap>(new ConcurrentHashMap());
    }

    /**
     * Обрезает входящий текст до 50 символов и приписывает к нему троеточие. Изпользуется в дереве задач
     *
     * @param name Входящий текст
     * @return обработанный текст
     */
    private static String sizeTrim(String name) {
        String ret = name;
        if (name == null || name.length() == 0)
            return "NoName";
        if (name.length() > 200) {
            ret = ret.substring(0, 200);
            ret += "...";
        }
        return ret;
    }

    /**
     * Возвращает ID родительской задачи
     *
     * @return ID адачи
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * Устанавливает дату последнего обновления задачи
     *
     * @param u дата обновления задачи
     */
    public void setUpdatedate(Calendar u) {
        this.updatedate.set(u.getTimeInMillis());
    }

    /**
     * Возвращает число подзадач
     *
     * @return число подзадач
     */
    public int getChildCount() {
        return childCount.intValue();
    }

    /**
     * Устанавливает число подзадач
     *
     * @param childCount число подзадач
     */
    public void setChildCount(int childCount) {
        this.childCount.set(childCount);
    }

    /**
     * Возвращает ID категории
     *
     * @return ID категории
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * Возвращает ID ответственного
     *
     * @return ID ответственного
     */
    public String getHandlerId() {
        return handlerId;
    }

    /**
     * Возвращает ID автора
     *
     * @return ID автора
     */
    public String getSubmitterId() {
        return submitterId;
    }

    /**
     * Устанавливает потраченное время
     *
     * @param actualBudget потраченное время
     */
    public void setActualBudget(Long actualBudget) {
        if (actualBudget == null)
            this.actualBudget.set(0L);
        else
            this.actualBudget.set(actualBudget);
    }

    /**
     * Возвращает список пользовательских полей для процесса
     *
     * @return список пользовательских полей
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    public List<UDFCacheItem> getWorkflowUDFs() {
        udfLock.readLock().lock();
        try {
            return workflowUDFs;
        } finally {
            udfLock.readLock().unlock();
        }

    }

    /**
     * Возвращает алиас
     *
     * @return алиас
     */
    public String getShortname() {
        return shortname;
    }

    /**
     * Возвращшает название задачи
     *
     * @return название задачи
     */
    public String getName() {
        return name;
    }

    public Calendar getSubmitdate() {
        if (submitdate == null)
            return null;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(submitdate);
        return cal;
    }

    /**
     * Возвращает даты обновления задачи
     *
     * @return дата обновления задачи
     */
    public Calendar getUpdatedate() {
        if (updatedate == null)
            return null;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(updatedate.get());
        return cal;
    }

    /**
     * Возвращает дату закрытия задачи
     *
     * @return дата закрытия задачи
     */
    public Calendar getClosedate() {
        if (closedate == null)
            return null;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(closedate.get());
        return cal;
    }

    /**
     * Возвращает потраченное время
     *
     * @return потраченное время
     */
    public Long getAbudget() {
        return abudget;
    }

    /**
     * Возвращает бюджет задачи
     *
     * @return бюджет задачи
     */
    public Long getBudget() {
        return budget;
    }

    /**
     * Возвращает дату дедлайна задачи
     *
     * @return дата дедлайно задачи
     */
    public Calendar getDeadline() {
        if (deadline == null)
            return null;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(deadline);
        return cal;
    }

    /**
     * Возвращает номер задачи
     *
     * @return номер задачи
     */
    public String getNumber() {
        return String.valueOf(number);
    }

    public int getNumberInt() {
        return number;
    }

    /**
     * Возвращает ID ответственного пользователя
     *
     * @return ID пользователя
     */
    public String getHandlerUserId() {
        return handlerUserId;
    }

    /**
     * Возвращает ID ответственной группы
     *
     * @return ID группы
     */
    public String getHandlerGroupId() {
        return handlerGroupId;
    }

    /**
     * Возвращает ID процесса
     *
     * @return ID процесса
     */
    public String getWorkflowId() {
        return workflowId;
    }

    /**
     * Возвращает ID состояния задачи
     *
     * @return ID состояния
     */
    public String getStatusId() {
        return statusId;
    }

    /**
     * Возвращает ID резолюции
     *
     * @return ID резолюции
     */
    public String getResolutionId() {
        return resolutionId;
    }

    /**
     * Возвращает ID резолюции
     *
     * @return ID резолюции
     */
    public String getPriorityId() {
        return priorityId;
    }

    /**
     * Возвращает ID объекта longtext
     *
     * @return ID объекта longtext
     */
    public String getLongtextId() {
        return longtextId;
    }

    /**
     * Возвращает ID задачи
     *
     * @return ID задачи
     */
    public String getId() {
        return id;
    }

    /**
     * Сравнивает указанный экземпляр объекта с текущим
     *
     * @param obj сравниваемый объект
     * @return TRUE - равны, FALSE - не равны
     */
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        return obj instanceof TaskRelatedInfo ? id.equals(((TaskRelatedInfo) obj).getId()) : id.equals(obj.toString());
    }

    /**
     * Возвращает текстовое представление объекта
     *
     * @return текстовое представление объекта
     */
    @Override
    public String toString() {
        return "TaskRelatedInfo{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", number='" + number + '\'' +
                '}';
    }

    /**
     * Сравнивает два объекта текущего класса
     *
     * @param o Сравниваемый обхект
     * @return +1, 0 или -1
     */
    public int compareTo(Object o) {
        return id.compareTo(((TaskRelatedInfo) o).id);
    }

    /**
     * Обновляет дату закрытия задачи
     *
     * @param t дата закрытия задачи
     */
    public void updateUpdateDate(Calendar t) {
        if (t == null)
            return;

        if (updatedate.get() < t.getTimeInMillis())
            updatedate.set(t.getTimeInMillis());
    }

    /**
     * Возвращает карту ACL для задачи
     *
     * @return карта ACL
     */
    public ConcurrentHashMap getAcl() {
        return new ConcurrentHashMap(Null.removeNullElementsFromMap(acl.get()));
    }

    /**
     * Возвращает карту ACL для задачи только для чтения
     *
     * @return карта ACL
     */
    public Map<String, TreeSet<InternalACL>> getReadOnlyAcl() {
        return Collections.unmodifiableMap(acl.get());
    }

    /**
     * Возвращает карту ACL как есть, без оберток для защиты от модификации. Этот метод можно использовать ТОЛЬКО
     * если возаращаемая коллекция не будет изменена вышестоящим кодом.
     * <p>
     * Оптимально использовать данный метод в ограниченном числе мест, где важна производительность.
     * Если не уверены - лучше использовать getReadOnlyAcl()
     *
     * @return карта ACL
     */
    protected Map<String, TreeSet<InternalACL>> getFastUnsafeAcl() {
        return acl.get();
    }

    /**
     * Устанавливает карту ACL для задачи
     *
     * @param acl карта ACL
     */
    public void setAcl(ConcurrentHashMap acl) {
        this.acl.set(new ConcurrentHashMap(Null.removeNullElementsFromMap(acl)));
    }

    /**
     * К существующему набору ACL добавляется новый. Если этот ACL задан
     * через группу, к его ID добавляется префикс
     *
     * @param iacl ACL
     * @see com.trackstudio.kernel.cache.InternalACL
     */
    public void addAcl(InternalACL iacl) {
        TaskRelatedManager.addAcl(acl.get(), iacl);
    }


    /**
     * Возвращает список сообщений для задачи
     *
     * @return список сообщений
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.MessageCacheItem
     */
    public List<MessageCacheItem> getMessages() throws GranException {
        return Null.removeNullElementsFromList(TaskRelatedManager.getInstance().getMessages(id));
    }

    /**
     * Возвращает список пользовательских полей для задачи
     *
     * @return список полей
     * @throws GranException при необзодимости
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    public ArrayList<UDFCacheItem> getUDFCacheItems() throws GranException {
        ArrayList<UDFCacheItem> list = getHierarchicalUDFCacheItems();
        list.addAll(getWorkflowUDFCacheItems(getWorkflowId(), true));
        return list;
    }

    public List<UDFCacheItem> getUDFCacheItemsWithoutCache() throws GranException {
        List<UDFCacheItem> udfCacheItems = new ArrayList<UDFCacheItem>(100);
        List<String> tasksId = new ArrayList<String>();
        for (TaskRelatedInfo tri : TaskRelatedManager.getInstance().getTaskRelatedInfoChain(null, id)) {
            tasksId.add(tri.getId());
        }
        udfCacheItems.addAll(KernelManager.getUdf().getUdfCacheItemTasks(tasksId, getWorkflowId()));
        return udfCacheItems;
    }

    /**
     * Возвращается список пользовательских полей для новой задачи и процесса
     *
     * @param workflowId ID процесса
     * @return список пользовательских полей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    public ArrayList<UDFCacheItem> getUDFCacheItemsForNewTask(String workflowId) throws GranException {
        ArrayList<UDFCacheItem> list = getHierarchicalUDFCacheItems();
        list.addAll(getWorkflowUDFCacheItems(workflowId, false));
        return list;
    }

    /**
     * Возвращается список пользовательских полей для задачи и процесса
     *
     * @param workflowId ID процесса
     * @param cache      надо ли кешировать
     * @return список пользовательских полей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    protected List<UDFCacheItem> getWorkflowUDFCacheItems(String workflowId, boolean cache) throws GranException {
        udfLock.writeLock().lock();
        try {
            if (!cache || workflowUDFs == null) {
                log.trace("########" + id);
                List<UDFCacheItem> local = KernelManager.getUdf().getListWorkflowUDFCacheItem(workflowId);
                if (cache)
                    workflowUDFs = new CopyOnWriteArrayList(Null.removeNullElementsFromList(local));
                else
                    return local;
            }
            return workflowUDFs;
        } finally {
            udfLock.writeLock().unlock();
        }

    }

    /**
     * Устанавливает пользовательские поля для процесса
     *
     * @param udfList список пользовательских полей
     * @throws GranException при необходимости
     */
    protected void setWorkflowUDFs(List<UDFCacheItem> udfList) throws GranException {
        Collections.sort(udfList);
        udfLock.writeLock().lock();
        try {
            workflowUDFs = new CopyOnWriteArrayList(Null.removeNullElementsFromList(udfList));
        } finally {
            udfLock.writeLock().unlock();
        }

    }

    /**
     * Возвращает состояния полей UDF для процесса
     *
     * @return TRUE - поля инициализированы, FALSE - нет
     */
    protected boolean isWorkflowUDFInitialized() {
        udfLock.readLock().lock();
        try {
            return workflowUDFs != null;
        } finally {
            udfLock.readLock().unlock();
        }

    }

    /**
     * Возвращает список пользовательских полей для задачи
     *
     * @return список полей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    public ArrayList<UDFCacheItem> getHierarchicalUDFCacheItems() throws GranException {
        ArrayList<UDFCacheItem> list = new ArrayList<UDFCacheItem>();
        for (TaskRelatedInfo tri : TaskRelatedManager.getInstance().getTaskRelatedInfoChain(null, id)) {
            list.addAll(tri.getUDFs());
        }
        return list;
    }

    /**
     * Возвращает список пользовательских полей
     *
     * @return список полей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    public List<UDFCacheItem> getUDFs() throws GranException {
        udfLock.writeLock().lock();
        try {
            if (UDFs == null) {
                log.trace("########" + id);
                UDFs = new CopyOnWriteArrayList(Null.removeNullElementsFromList(KernelManager.getUdf().getListTaskUDFCacheItem(id)));
            }
            return UDFs;
        } finally {
            udfLock.writeLock().unlock();
        }

    }

    /**
     * Устанавливает список пользовательских полей
     *
     * @param udfList список пользовательских полей
     * @throws GranException при необъодимости
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    protected void setUDFs(List<UDFCacheItem> udfList) throws GranException {
        Collections.sort(udfList);
        udfLock.writeLock().lock();
        try {
            UDFs = new CopyOnWriteArrayList(Null.removeNullElementsFromList(udfList));
        } finally {
            udfLock.writeLock().unlock();
        }

    }

    /**
     * Возвращает состояния полей UDF
     *
     * @return TRUE - поля инициализированы, FALSE - нет
     */
    protected boolean isTaskUDFInitialized() {
        udfLock.readLock().lock();
        try {
            return UDFs != null;
        } finally {
            udfLock.readLock().unlock();
        }

    }

    /**
     * Возвращает список отфильтрованных значений пользовательских полей
     *
     * @return список значение пользовательских полей
     * @throws GranException при необзодимости
     * @see com.trackstudio.app.UdfValue
     */
    public ArrayList<UdfValue> getFilterUDFValues() throws GranException {
        ArrayList<UDFCacheItem> fUDFs = getFilterUDFs(); // open call

        // inner code shouldn't set writeLock
        udfLock.readLock().lock();
        try {
            ArrayList<UdfValue> retUdfvalList = new ArrayList<UdfValue>();
            for (UDFCacheItem udf : fUDFs) {
                UdfValue udfval = new UdfValue(udf);
                if (UDFValues == null || !UDFValues.contains(udfval))
                    retUdfvalList.add(udfval);
            }
            if (UDFValues != null)
                retUdfvalList.addAll(UDFValues);
            return retUdfvalList;
        } finally {
            udfLock.readLock().unlock();
        }

    }

    /**
     * Возвращает список отфильтрованных пользовательских полей
     *
     * @return список пользовательских полей
     * @throws GranException при необзодимости
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    public ArrayList<UDFCacheItem> getFilterUDFs() throws GranException {
        ArrayList<UDFCacheItem> udfs = new ArrayList<UDFCacheItem>();
        udfs.addAll(getHierarchicalUDFCacheItems());
        ArrayList<String> categories = CategoryCacheManager.getInstance().getAllPossibleSubcategories(categoryId);
        ArrayList<String> usedWorkflow = new ArrayList<String>();
        for (String categoryId : categories) {
            CategoryCacheItem categoryCacheItem = CategoryCacheManager.getInstance().find(categoryId);
            String workflowId = categoryCacheItem.getWorkflowId();
            if (!usedWorkflow.contains(workflowId) && TaskRelatedManager.getInstance().hasPath(id, categoryCacheItem.getTaskId())) {
                usedWorkflow.add(workflowId);
                udfs.addAll(KernelManager.getUdf().getListWorkflowUDFCacheItem(workflowId));
            }
        }
        return udfs;
    }

    /**
     * Возвращает список значений пользовательских полей
     *
     * @return список значение пользовательских полей
     * @throws GranException при необзодимости
     * @see com.trackstudio.app.UdfValue
     */
    List<UdfValue> getUDFValues() throws GranException {
        udfLock.writeLock().lock();
        try {
            if (UDFValues == null) {
                UDFValues = new CopyOnWriteArrayList(Null.removeNullElementsFromList(KernelManager.getUdf().getUDFValues(id, UdfConstants.TASK_ALL, getUDFCacheItems())));
            }
            return UDFValues;
        } finally {
            udfLock.writeLock().unlock();
        }

    }

    /**
     * Устанавливает список значений пользовательских полей
     *
     * @param udfValues список значение пользовательских полей
     * @throws GranException при необзодимости
     * @see com.trackstudio.app.UdfValue
     */
    protected void setUDFValues(List<UdfValue> udfValues) throws GranException {
        udfLock.writeLock().lock();
        try {
            this.UDFValues = new CopyOnWriteArrayList<UdfValue>(Null.removeNullElementsFromList(udfValues));
        } finally {
            udfLock.writeLock().unlock();
        }

    }

    /**
     * Возвращает состояния значений полей UDF
     *
     * @return TRUE - поля инициализированы, FALSE - нет
     */
    protected boolean isUDFValuesInitialized() {
        udfLock.readLock().lock();
        try {
            return UDFValues != null;
        } finally {
            udfLock.readLock().unlock();
        }

    }

    /**
     * Возвращает список значений пользовательских полей для процесса
     *
     * @return список значение пользовательских полей
     * @throws GranException при необзодимости
     * @see com.trackstudio.app.UdfValue
     */
    public List<UdfValue> getWorkflowUDFValues() throws GranException {
        udfLock.writeLock().lock();
        try {
            if (workflowUDFValues == null) {
                workflowUDFValues = new CopyOnWriteArrayList(Null.removeNullElementsFromList(KernelManager.getUdf().getUDFValues(id, UdfConstants.TASK_ALL, new ArrayList<UDFCacheItem>(getWorkflowUDFCacheItems(getWorkflowId(), true)))));
            }
            return workflowUDFValues;
        } finally {
            udfLock.writeLock().unlock();
        }

    }

    /**
     * Возвращает список дочерних задач
     *
     * @return список задач
     * @throws GranException при необходимости
     */
    public List<TaskRelatedInfo> getChildren() throws GranException {
        return TaskRelatedManager.getInstance().getChildren(this);

    }

    /**
     * Возвращает количество дочерних задач
     *
     * @return количество подзадач
     * @throws GranException при необзодимости
     */
    public List<AttachmentCacheItem> getAttachments() throws GranException {
        return KernelManager.getAttachment().getAttachmentList(id, null, null);
    }

    /**
     * Возвращает количество дочерних задач
     *
     * @return количество подзадач
     * @throws GranException при необзодимости
     */
    public int getChildrenCount() throws GranException {
        return getChildren().size();
    }

    /**
     * Возвращает дату последнего обновления задачи
     *
     * @return дата последнего обновления задачи
     * @throws GranException при необзодимости
     */
    public Calendar getLastUpdateDate() {
        return getUpdatedate();
    }

    public long getLastUpdateDateMsec() {
        return updatedate.get();
    }

    /**
     * Возвращает потраченное на задачу время
     *
     * @return потраченное время
     * @throws GranException при необходимости
     */
    public Long getActualBudget() {
        return actualBudget.get();
    }

    /**
     * Обрезанное название задачи
     *
     * @return название задачи
     */
    public String getTaskNameCutted() {
        return sizeTrim(name);
    }

    /**
     * Возвращает колличество сообщений
     *
     * @return колличество сообщений
     * @throws GranException при необходимости
     */
    public Integer getMessageCount() throws GranException {
        return KernelManager.getTask().getMessageCount(id);
    }

    /**
     * Возвращает описание задачи
     *
     * @return описание задачи
     * @throws GranException при необходимости
     */
    public String getDescription() throws GranException {
        byte[] current = description.get();
        if (current != null)
            return GZIPCompression.decompress(current);

        String newValue = KernelManager.getLongText().getLongtext(longtextId);
        if (description.compareAndSet(null, GZIPCompression.compress(newValue)))
            return newValue;
        else
            return GZIPCompression.decompress(description.get());
    }

    /**
     * Возвращает описание задачи с убранными HTML-тегами
     *
     * @return текстовое описание
     * @throws GranException при необходимости
     */
    public String getTextDescription() throws GranException {
        // we have empty text, nothing to convert
        String desc = getDescription();

        if (desc == null)
            return null;

        return HTMLEncoder.stripHtmlTags(HTMLEncoder.br2nl(desc));
    }

     /**
     * Инвалидация пользовательских полей
     */
    void invalidateUDF() {
        udfLock.writeLock().lock();
        try {
            UDFs = null;
        } finally {
            udfLock.writeLock().unlock();
        }

        invalidateWorkflowUDF();
        invalidateUDFValues();

    }

    /**
     * Инвалидация полей процесса
     */
    void invalidateWorkflowUDF() {
        udfLock.writeLock().lock();
        try {
            workflowUDFs = null;
            workflowUDFValues = null;
        } finally {
            udfLock.writeLock().unlock();
        }

    }

    /**
     * Инвалидация значений пользовательских полей
     */
    void invalidateUDFValues() {
        udfLock.writeLock().lock();
        try {
            UDFValues = null;
            workflowUDFValues = null;
        } finally {
            udfLock.writeLock().unlock();
        }

    }

    /**
     * Инвалидация указанного пользовательского поля
     *
     * @param udfId ID пользовательского поля
     * @param uci   пользовательское поле
     * @throws GranException при необходимости
     */
    void invalidateUDF(String udfId, UDFCacheItem uci) throws GranException {
        udfLock.writeLock().lock();
        try {
            invalidateUDFCommon(udfId, uci, UDFs, UDFValues, false);
        } finally {
            udfLock.writeLock().unlock();
        }

    }

    /**
     * Инвалидация указанного пользовательского поля для процесса
     *
     * @param udfId ID пользовательского поля
     * @param uci   пользовательское поле
     * @throws GranException при необходимости
     */
    void invalidateWFUDF(String udfId, UDFCacheItem uci) throws GranException {
        udfLock.writeLock().lock();
        try {
            invalidateUDFCommon(udfId, uci, workflowUDFs, getWorkflowUDFValues(), true);
        } finally {
            udfLock.writeLock().unlock();
        }

    }

    /**
     * Инвалидация пользовательского поля
     *
     * @param udfId  ID поля
     * @param uci    новое значение поля
     * @param udfs   значения полей
     * @param values значения полей
     * @param wf     процесс или нет
     * @throws GranException при необходимости
     */
    private void invalidateUDFCommon(String udfId, UDFCacheItem uci, Collection<UDFCacheItem> udfs, Collection<UdfValue> values, boolean wf) throws GranException {
        udfLock.writeLock().lock();
        try {
            // Remove udf, if it's already exists
            if (udfs != null)
                for (UDFCacheItem udf : udfs) {
                    if (udf.getId().equals(udfId)) {
                        udfs.remove(udf);
                        break;
                    }
                }
            // Remove value
            GenericValue cv = null;
            if (values != null) {
                for (UdfValue udf : values) {
                    if (udf.getUdfId().equals(udfId)) {
                        if (uci != null)
                            cv = udf.getValueContainer();
                        values.remove(udf);
                        break;
                    }
                }
            }
            if (wf && UDFValues != null) {  // UDFValues stores all udfs (both task and worflow). We've to update it even for worklow
                for (UdfValue udf : UDFValues) {
                    if (udf.getUdfId().equals(udfId)) {
                        if (uci != null)
                            cv = udf.getValueContainer();
                        if (wf && UDFValues != null && UDFValues.contains(udf))
                            if (uci != null)
                                cv = udf.getValueContainer();
                        UDFValues.remove(udf);
                        break;
                    }
                }
            }
            // Remove filter value
            if (uci != null) {
                if (values == null)
                    values = new ArrayList<UdfValue>(5);
                UdfValue value = new UdfValue(uci);
                if (cv != null) {
                    value.setValueContainer(cv);
                }
                values.add(value);
                if (wf) {    // UDFValues stores all udfs (both task and worflow). We've to update even for worklow
                    if (UDFValues == null)
                        UDFValues = new CopyOnWriteArrayList<UdfValue>();
                    UDFValues.add(value);
                }
            }
        } finally {
            udfLock.writeLock().unlock();
        }

    }

    /**
     * Инвалидация списка изщмененных полей
     *
     * @param udfId  ID полей
     * @param value  значение
     * @param listId id списка
     * @throws GranException при необходимости
     */
    public void invalidateUDFWhenChangeList(String udfId, String value, String listId) throws GranException {
        udfLock.writeLock().lock();
        try {
            invalidateUDFWhenChangeListImpl(udfId, value, listId, UDFValues);
        } finally {
            udfLock.writeLock().unlock();
        }

    }

    /**
     * Инвалидация списка изщмененных полей для процесса
     *
     * @param udfId  ID полей
     * @param value  значение
     * @param listId id списка
     * @throws GranException при необходимости
     */
    public void invalidateWFUDFWhenChangeList(String udfId, String value, String listId) throws GranException {
        udfLock.writeLock().lock();
        try {
            if (invalidateUDFWhenChangeListImpl(udfId, value, listId, workflowUDFValues))  // We need to update UDFValues only if value for workflowUDFValues was changed
                invalidateUDFWhenChangeListImpl(udfId, value, listId, UDFValues); // UDFValues stores all udfs (both task and worflow). We've to update even for worklow
        } finally {
            udfLock.writeLock().unlock();
        }

    }

    /**
     * Инвалидация поля при изменении значения
     *
     * @param udfId  ID поля
     * @param value  значение
     * @param listId id списка
     * @param values значения
     * @return TRUE - если изменилось, FALSE - если нет
     * @throws GranException при необходимости
     */
    private boolean invalidateUDFWhenChangeListImpl(String udfId, String value, String listId, Collection<UdfValue> values) throws GranException {
        UdfvalCacheItem cv;
        if (values == null)
            return false; // Nothing to update
        // Update or delete an existng value
        for (UdfValue udf : values) {
            if (udfId.equals(udf.getUdfId())) {
                if (udf.getType() == UdfValue.MULTILIST) {
                    List<Pair> pairs = ((ListMultiValue) udf.getValueContainer()).getValue(null);
                    if (pairs != null && pairs.contains(new Pair(listId, value))) {
                        cv = new UdfvalCacheItem(null, getId(), value, null, null, listId, value, null, null, null);
                        udf.setValue(cv);
                    }
                } else {
                    Pair pair = ((ListValue) udf.getValueContainer()).getValue(null);
                    if (pair != null && pair.getKey().equals(listId)) {
                        if (value != null && value.length() > 2000) {
                            String newLongtextId = KernelManager.getLongText().createLongtext(null, value);
                            cv = new UdfvalCacheItem(null, getId(), null, null, null, listId, value, newLongtextId, null, null);
                        } else {
                            cv = new UdfvalCacheItem(null, getId(), value, null, null, listId, value, null, null, null);
                        }
                        udf.setValue(cv);
                    }
                }
            }
        }
        return true;
    }

}