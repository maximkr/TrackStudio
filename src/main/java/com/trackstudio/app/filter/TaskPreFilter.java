package com.trackstudio.app.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.exception.GranException;

import net.jcip.annotations.Immutable;

/**
 * Класс для работы с предварительной фильтрацией задач
 */
@Immutable
public class TaskPreFilter extends PreFilter {

    private static final Log log = LogFactory.getLog(TaskPreFilter.class);

    /**
     * Констркуктор
     *
     * @param fv            параметры фильтрации задач
     * @param currentUserId текущий пользователь
     * @throws GranException при необзодимости
     */
    public TaskPreFilter(TaskFValue fv, String currentUserId) throws GranException {
        super(fv, currentUserId);
    }

//    public Set<String> filterTasks(Set<String> sourceTasksSet, String taskId) throws GranException {
//        List<String> parentObjects = TaskRelatedManager.getInstance().getParents(sourceTasksSet);
//        parentObjects.add(taskId);
//        return fastRetainAll(fastRetainAll(fastRetainAll(sourceTasksSet, processTasks(parentObjects)), processMessages(parentObjects)), processUdfs(parentObjects, sourceTasksSet));
//    }
//
//    private Set<String> processMessages(List parentObjects) throws GranException {
//        log.trace("processMessages");
//        List<String> messageConditions = ((TaskFValue) fv).getUseForMessages();
//        Criteria cr = sess.createCriteria(Message.class, "messagePrj");
//        cr.setProjection(Projections.projectionList().add(Projections.property("messagePrj.task.id"), "id"));
//
//        boolean processed = false;
//        for (String c : messageConditions) {
//            if (c.equals(FieldMap.MSG_SUSER_NAME.getFilterKey()))
//                processed = applyListCriteria(cr, FieldMap.MSG_SUSER_NAME.getFilterKey(), "submitter", "id") || processed;
//            else if (c.equals(FieldMap.MSG_HUSER_NAME.getFilterKey()))
//                processed = applyHandlerListCriteria(cr, FieldMap.MSG_HUSER_NAME.getFilterKey()) || processed;
//            else if (c.equals(FieldMap.MSG_SUBMITDATE.getFilterKey()))
//                processed = applyDateCriteria(cr, FieldMap.MSG_SUBMITDATE.getFilterKey(), "time") || processed;
//            else if (c.equals(TaskFValue.MSG_TYPE))
//                processed = applyListCriteria(cr, TaskFValue.MSG_TYPE, "mstatus", "id") || processed;
//            else if (c.equals(FieldMap.MSG_RESOLUTION.getFilterKey()))
//                processed = applyListCriteria(cr, FieldMap.MSG_RESOLUTION.getFilterKey(), "resolution", "id") || processed;
//            else if (c.equals(FieldMap.MSG_ABUDGET.getFilterKey()))
//                processed = applyFloatCriteria(cr, FieldMap.MSG_ABUDGET.getFilterKey(), "hrs") || processed;
//        }
//
//        if (parentObjects.size() < MAX_PARENT) {
//            cr.createAlias("task", "task").add(Expression.in("task.parent.id", parentObjects));
//        }
//        if (processed)
//            return getResult(cr);
//        else
//            return null;
//    }

//    private Set<String> processTasks(List parentObjects) throws GranException {
//        log.trace("*****");
//        List<String> taskConditions = ((TaskFValue) fv).getUseForTask();
//        Criteria cr = sess.createCriteria(Task.class, "taskPrj");
//        cr.setProjection(Projections.projectionList().add(Projections.property("taskPrj.id"), "id"));
//        boolean processed = false;
//        for (Object taskCondition : taskConditions) {
//            String c = taskCondition.toString();
//            if (c.equals(FieldMap.TASK_CATEGORY.getFilterKey()))
//                processed = applyListCriteria(cr, FieldMap.TASK_CATEGORY.getFilterKey(), "category", "id") || processed;
//            else if (c.equals(FieldMap.TASK_STATUS.getFilterKey()))
//                processed = applyListCriteria(cr, FieldMap.TASK_STATUS.getFilterKey(), "status", "id") || processed;
//            else if (c.equals(FieldMap.TASK_RESOLUTION.getFilterKey()))
//                processed = applyListCriteria(cr, FieldMap.TASK_RESOLUTION.getFilterKey(), "resolution", "id") || processed;
//            else if (c.equals(FieldMap.SUSER_NAME.getFilterKey()))
//                processed = applyListCriteria(cr, FieldMap.SUSER_NAME.getFilterKey(), "submitter", "id") || processed;
//            else if (c.equals(FieldMap.HUSER_NAME.getFilterKey()))
//                processed = applyHandlerListCriteria(cr, FieldMap.HUSER_NAME.getFilterKey()) || processed;
//            else if (c.equals(FieldMap.TASK_PRIORITY.getFilterKey()))
//                processed = applyListCriteria(cr, FieldMap.TASK_PRIORITY.getFilterKey(), "priority", "id") || processed;
//            else if (c.equals(FieldMap.TASK_DEADLINE.getFilterKey()))
//                processed = applyDateCriteria(cr, FieldMap.TASK_DEADLINE.getFilterKey(), "deadline") || processed;
//            else if (c.equals(FieldMap.TASK_SUBMITDATE.getFilterKey()))
//                processed = applyDateCriteria(cr, FieldMap.TASK_SUBMITDATE.getFilterKey(), "submitdate") || processed; // don't filter by update date - calculated dynamically
//            else if (c.equals(FieldMap.TASK_CLOSEDATE.getFilterKey()))
//                processed = applyDateCriteria(cr, FieldMap.TASK_CLOSEDATE.getFilterKey(), "closedate") || processed;
//            else if (c.equals(FieldMap.TASK_BUDGET.getFilterKey()))
//                processed = applyFloatCriteria(cr, FieldMap.TASK_BUDGET.getFilterKey(), "budget") || processed;
//        }
//        if (parentObjects.size() < MAX_PARENT) {
//            cr.createAlias("parent", "parent").add(Expression.in("parent.id", parentObjects));
//        }
//        if (processed)
//            return getResult(cr);
//        else
//            return null;
//    }
}
