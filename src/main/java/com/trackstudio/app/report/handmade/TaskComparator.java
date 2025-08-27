package com.trackstudio.app.report.handmade;

import java.util.Comparator;

import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.secured.SecuredTaskBean;

import net.jcip.annotations.NotThreadSafe;

/**
 * Класс для сортировки задач по номерам
 */
@NotThreadSafe
class TaskComparator implements Comparator<SecuredTaskBean> {
    /**
     * Сравнивает два объекта
     *
     * @param task1 объект 1
     * @param task2 объект 2
     * @return +1,0, -1
     */

    @Override
    public int compare(SecuredTaskBean task1, SecuredTaskBean task2) {
        int level1 = countOfParent(task1.getTask());
        int level2 = countOfParent(task2.getTask());
        int number1 = Integer.parseInt(task1.getNumber());
        int number2 = Integer.parseInt(task2.getNumber());
        if (number1 != number2) {
            return number1 < number2 ? 1 : -1;
        }
        return level1 > level2 ? 1 : -1;
    }

    private static int countOfParent(TaskRelatedInfo task) {
        int total = 0;
        try {
            String taskId;
            while ((taskId = task.getParentId()) != null) {
                task = TaskRelatedManager.getInstance().find(taskId);
                total++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }
}