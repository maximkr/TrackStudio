package com.trackstudio.app.filter.comparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredResolutionBean;
import com.trackstudio.secured.SecuredStatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUserBean;

import net.jcip.annotations.Immutable;

/**
 * Спенциальный компаратор, предназначен для сравнения задач
 */
@Immutable
public class TaskComparator extends AbstractComparator {
    private static final LockManager lockManager = LockManager.getInstance();
    private static final Log log = LogFactory.getLog(TaskComparator.class);

    /**
     * Конструкторп по умолчанию
     *
     * @param sortOrder порядок сортировки
     * @param udfHash   карта пользовательских полей
     */
    public TaskComparator(List<String> sortOrder, ArrayList<SecuredUDFBean> udfHash) {
        super(sortOrder, udfHash, FieldMap.TASK_UPDATEDATE.getFieldKey());
    }

    /**
     * Метод для основной сортировки
     *
     * @param ids список id задач для сортировки
     * @param sc  сессия пользователя
     * @return Отсортированный список задач
     * @throws GranException при необходимости
     */
    public static ArrayList<SecuredTaskBean> sort(Collection<String> ids, SessionContext sc) throws GranException {
        if (ids.isEmpty())
            return null;
        ArrayList<SecuredTaskBean> list = new ArrayList<SecuredTaskBean>();
        for (String id : ids) {
            if (TaskRelatedManager.getInstance().isTaskExists(id)) {
                list.add(new SecuredTaskBean(id, sc));
            }
        }
        ArrayList<String> s = new ArrayList<String>();
        s.add(FValue.SUB + FieldMap.TASK_NUMBER.getFieldKey());
        Collections.sort(list, new TaskComparator(s, null));
        return list;
    }

    public static void sort(List<String> sortFields, ArrayList<SecuredUDFBean> udfs, final List<?> collection) throws GranException {
        boolean lock = lockManager.acquireConnection(TaskComparator.class.getName());
        try {
            TaskComparator taskComparator = new TaskComparator(sortFields, udfs);
            Collections.sort(collection, taskComparator);
        } finally {
            if (lock) lockManager.releaseConnection(TaskComparator.class.getName());
        }
    }

    /**
     * Метод сравнения двух объектов, если один из них null
     *
     * @param a первый обхект
     * @param b второй объект
     * @return +1,0 или -1
     */
    private int compareNulls(Object a, Object b) {
        if (a == null && b != null)
            return 1;
        if (a != null && b == null)
            return -1;
        return 0;
    }


    /**
     * Сравнивает две категории
     *
     * @param p1 первая
     * @param p2 вторая
     * @return +1,0 или -1
     */
    private int compareCategory(SecuredCategoryBean p1, SecuredCategoryBean p2) {
        if (p1 == null || p2 == null)
            return compareNulls(p1, p2);
        return p1.getName().compareTo(p2.getName());
    }

    /**
     * Сравнивает два статуса
     *
     * @param p1 первый
     * @param p2 второй
     * @return +1,0 или -1
     */
    private int compareStatus(SecuredStatusBean p1, SecuredStatusBean p2) {
        if (p1 == null || p2 == null)
            return compareNulls(p1, p2);
        return p1.getName().compareTo(p2.getName());
    }

    /**
     * Сравнивает две резолюции
     *
     * @param p1 первая
     * @param p2 вторая
     * @return +1,0 или -1
     */
    private int compareResolution(SecuredResolutionBean p1, SecuredResolutionBean p2) {
        if (p1 == null || p2 == null)
            return compareNulls(p1, p2);
        return p1.getName().compareTo(p2.getName());
    }

    /**
     * Сравнивает два пользователя
     *
     * @param p1 первый
     * @param p2 второй
     * @return +1,0 или -1
     */
    private int compareUser(SecuredUserBean p1, SecuredUserBean p2) {
        if (p1 == null || p2 == null)
            return compareNulls(p1, p2);
        return p1.getName().compareTo(p2.getName());
    }

    /**
     * Сравнивает два объекта текущего класса
     *
     * @param o1 первый объект
     * @param o2 второй объект
     * @return +1,0 или -1
     */
    public int compare(Object o1, Object o2) {
        int retVal = 0;
        try {
            SecuredTaskBean tci1 = (SecuredTaskBean) o1;
            SecuredTaskBean tci2 = (SecuredTaskBean) o2;

            if (tci1 == null)
                log.error("tci1 is null");

            if (tci2 == null)
                log.error("tci2 is null");

            for (Iterator it = sortedOrder.iterator(); it.hasNext() && retVal == 0;) {
                String field = (String) it.next();
                if (field.equals(FieldMap.TASK_UPDATEDATE.getFieldKey())) {
                    retVal = super.compare(tci1.getUpdatedateMsec(), tci2.getUpdatedateMsec());
                }
                if (field.equals(FieldMap.TASK_CLOSEDATE.getFieldKey())) {
                    retVal = super.compare(tci1.getClosedate(), tci2.getClosedate());
                }
                if (field.equals(FieldMap.TASK_SUBMITDATE.getFieldKey())) {
                    retVal = super.compare(tci1.getSubmitdate(), tci2.getSubmitdate());
                }
                if (field.equals(FieldMap.TASK_DEADLINE.getFieldKey())) {
                    retVal = super.compare(tci1.getDeadline(), tci2.getDeadline());
                }
                if (field.equals(FieldMap.FULLPATH.getFieldKey())) {
                    ArrayList<TaskRelatedInfo> list1 = TaskRelatedManager.getInstance().getTaskRelatedInfoChain(null, tci1.getId());
                    ArrayList<TaskRelatedInfo> list2 = TaskRelatedManager.getInstance().getTaskRelatedInfoChain(null, tci2.getId());
                    Iterator<TaskRelatedInfo> it1 = list1.iterator();
                    Iterator<TaskRelatedInfo> it2 = list2.iterator();
                    while (true) {
                        String id1 = it1.next().getId();
                        String id2 = it2.next().getId();
                        if (id1.equals(id2)) {
                            boolean hn1 = it1.hasNext();
                            boolean hn2 = it2.hasNext();
                            if (hn1 && hn2)
                                continue;
                            if (!hn1 && !hn2) {
                                retVal = 0;
                                break;
                            }
                            if (hn1) {
                                retVal = 1;
                                break;
                            } else {
                                retVal = -1;
                                break;
                            }
                        } else {
                            retVal = super.compare(TaskRelatedManager.getInstance().find(id1).getName(), TaskRelatedManager.getInstance().find(id2).getName());
                            break;
                        }
                    }
                }

                if (field.equals(FieldMap.TASK_NAME.getFieldKey())) {
                    retVal = super.compare(tci1.getName(), tci2.getName());
                }

                if (field.equals(FieldMap.TASK_PARENT.getFieldKey())) {
                    retVal = super.compare(tci1.getParent().getName(), tci2.getParent().getName());
                }

                if (field.equals(FieldMap.TASK_SHORTNAME.getFieldKey())) {
                    retVal = super.compare(tci1.getShortname(), tci2.getShortname());
                }

                if (field.equals(FieldMap.TASK_DESCRIPTION.getFieldKey()))
                    retVal = super.compare(tci1.getDescription(), tci2.getDescription());

                if (field.equals(FieldMap.TASK_CATEGORY.getFieldKey()))
                    retVal = compareCategory(tci1.getCategory(), tci2.getCategory());

                if (field.equals(FieldMap.TASK_STATUS.getFieldKey()))
                    retVal = compareStatus(tci1.getStatus(), tci2.getStatus());

                if (field.equals(FieldMap.TASK_RESOLUTION.getFieldKey()))
                    retVal = compareResolution(tci1.getResolution(), tci2.getResolution());

                if (field.equals(FieldMap.HUSER_NAME.getFieldKey())) {
                    boolean b1 = tci1.getHandlerUserId() == null && tci1.getHandlerGroupId() == null;
                    boolean b2 = tci2.getHandlerUserId() == null && tci2.getHandlerGroupId() == null;
                    if (b1 || b2)
                        retVal = compareNulls(b1 ? null : "", b2 ? null : "");
                    else if (tci1.getHandlerGroupId() != null && tci2.getHandlerGroupId() == null)
                        retVal = 1;
                    else if (tci2.getHandlerGroupId() != null && tci1.getHandlerGroupId() == null)
                        retVal = -1;
                    else if (tci1.getHandlerGroupId() != null && tci2.getHandlerGroupId() != null)
                        retVal = comparePrstatus(tci1.getHandlerGroup(), tci2.getHandlerGroup());
                    else
                        retVal = compareUser(tci1.getHandlerUser(), tci2.getHandlerUser());
                }

                if (field.equals(FieldMap.HUSER_STATUS.getFieldKey()))
                    retVal = super.compare(tci1.getHandlerPrstatuses() == null ? null : new TreeSet<String>(tci1.getHandlerPrstatuses()), tci2.getHandlerPrstatuses() == null ? null : new TreeSet<String>(tci2.getHandlerPrstatuses()));

                if (field.equals(FieldMap.SUSER_NAME.getFieldKey()))
                    retVal = compareUser(tci1.getSubmitter(), tci2.getSubmitter());

                if (field.equals(FieldMap.SUSER_STATUS.getFieldKey()))
                    retVal = super.compare(new TreeSet<String>(tci1.getSubmitterPrstatuses()), new TreeSet<String>(tci2.getSubmitterPrstatuses()));

                if (field.equals(FieldMap.TASK_BUDGET.getFieldKey()))
                    retVal = super.compare(tci1.getBudget(), tci2.getBudget());

                if (field.equals(FieldMap.TASK_ABUDGET.getFieldKey()))
                    retVal = super.compare(tci1.getActualBudget(), tci2.getActualBudget());

                if (field.equals(FieldMap.TASK_CHILDCOUNT.getFieldKey()))
                    retVal = super.compare(tci1.getTotalChildrenCount(), tci2.getTotalChildrenCount());

                if (field.equals(FieldMap.TASK_MESSAGECOUNT.getFieldKey()))
                    retVal = super.compare(tci1.getMessageCount(), tci2.getMessageCount());

                if (field.equals(FieldMap.TASK_PRIORITY.getFieldKey()))
                    retVal = super.compare(tci1.getPriority(), tci2.getPriority());

                if (field.equals(FieldMap.TASK_NUMBER.getFieldKey()))
                    retVal = super.compare(new Integer(tci1.getNumber()), new Integer(tci2.getNumber()));

                if (udfs != null && !udfs.isEmpty())
                    retVal = super.compareUdf(field, tci1, tci2, retVal);

                if (!((Boolean) fieldMap.get(field)))
                    retVal = -retVal;

                if (retVal == 0 && sortedOrder.size() == 1) {
                    retVal = super.compareString(tci1.getId(), tci2.getId());
                }

                if (!field.equals(sortedOrder.get(0))) {
                    if (retVal < 0)
                        retVal = -1;
                    else if (retVal > 0) retVal = 1;
                }

            }
            if (retVal==0) retVal = super.compare(new Integer(tci1.getNumber()), new Integer(tci2.getNumber()));
        } catch (Exception e) {
            log.error("Error", e);
        }

        return retVal;
    }

    /**
     * Сравнивает два статуса
     *
     * @param p1 первый
     * @param p2 второй
     * @return +1, 0 или -1
     */
    private int comparePrstatus(SecuredPrstatusBean p1, SecuredPrstatusBean p2) {
        if (p1 == null || p2 == null)
            return compareNulls(p1, p2);
        return p1.getName().compareTo(p2.getName());
    }
}