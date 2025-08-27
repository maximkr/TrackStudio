package com.trackstudio.tools.audit.trail;

import com.trackstudio.app.UdfValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.view.UDFValueViewText;

import net.jcip.annotations.Immutable;

@Immutable
public class AuditTrailTask {
    private final AuditUtil builder;
    private final SessionContext sc;
    private final SecuredTaskBean currentTask;

    public AuditTrailTask(SessionContext sc, String taskId, AuditUtil builder) throws GranException {
        this.sc = sc;
        this.currentTask = new SecuredTaskBean(taskId, sc);
        this.builder = builder;
    }

    public String auditTask(SecuredTaskTriggerBean newTask, boolean task) throws GranException {
        String nameTr, oldValue, newValue;
        if (builder.isCreateMessage()) {
            if (builder.checkSimpleValue(currentTask.getName(), newTask.getName()) && task) {
                nameTr = I18n.getString(sc, "NAME");
                builder.buildTrText(nameTr, currentTask.getName(), newTask.getName());
            }
            if (builder.checkSimpleValue(newTask.getShortname(), currentTask.getShortname()) && task) {
                nameTr = I18n.getString(sc, "ALIAS");
                builder.buildTrText(nameTr, currentTask.getShortname(), newTask.getShortname());
            }
            if (sc.canAction(Action.editTaskBudget, currentTask.getId()) && builder.checkSimpleValue(currentTask.getBudget(), newTask.getBudget()) && task) {
                nameTr = I18n.getString(sc, "BUDGET");
                builder.buildTr(nameTr, currentTask.getBudgetAsString(), newTask.getBudgetAsString());
            }
            if (sc.canAction(Action.editTaskDeadline, currentTask.getId()) &&  builder.checkSimpleValue(currentTask.getDeadline(), newTask.getDeadline()) && task) {
                nameTr = I18n.getString(sc, "DEADLINE");
                builder.buildTr(nameTr, currentTask.getDeadlineAsString(), newTask.getDeadlineAsString());
            }
            if (sc.canAction(Action.editTaskPriority, currentTask.getId()) && builder.checkSimpleValue(currentTask.getPriorityId(), newTask.getPriorityId()) && task) {
                nameTr = I18n.getString(sc, "PRIORITY");
                oldValue = currentTask.getPriority() != null ? currentTask.getPriority().getName() : null;
                newValue = newTask.getPriority() != null ? newTask.getPriority().getName() : null;
                builder.buildTr(nameTr, oldValue, newValue);
            }
            if (!newTask.isCopyOrMoveOpr()) {
                for (String udfId : currentTask.getUDFValues().keySet()) {
                    SecuredUDFValueBean oldUdf = currentTask.getUDFValues().get(udfId);
                    if (oldUdf.isCalculated() || !KernelManager.getUdf().isTaskUdfEditable(currentTask.getId(), sc.getUserId(), udfId, currentTask.getStatusId())) {
                        continue;
                    }
                    oldValue = new UDFValueViewText(oldUdf).getValue(currentTask);
                    newValue = newTask.getUdfValues().get(oldUdf.getCaption());
                    if (builder.checkSimpleValue(oldValue, newValue) && task) {
                        nameTr = oldUdf.getCaption();
                        if (oldUdf.getUdfType().equals(UdfValue.INTEGER) || oldUdf.getUdfType().equals(UdfValue.FLOAT) || oldUdf.getUdfType().equals(UdfValue.DATE) || oldUdf.getUdfType().equals(UdfValue.LIST)) {
                            builder.buildTr(nameTr, oldValue, newValue);
                        } else if (oldUdf.getUdfType().equals(UdfValue.TASK) || oldUdf.getUdfType().equals(UdfValue.USER) || oldUdf.getUdfType().equals(UdfValue.MULTILIST)) {
                            builder.buildTrList(nameTr, oldValue, newValue, oldUdf.getUdfType().equals(UdfValue.TASK));
                        } else {
                            builder.buildTrText(nameTr, oldValue, newValue);
                        }
                    }
                }
            }
            String desc = currentTask.getDescription();
            desc = desc != null && desc.isEmpty() ? null : desc;
            if (!newTask.isCopyOrMoveOpr() && builder.checkSimpleValue(desc, newTask.getDescription())) {
                builder.buildTrDescription(currentTask.getDescription(), newTask.getDescription());
            }
            return builder.createLogMessage(sc, currentTask.getId(), builder.getTable());
        }
        return null;
    }
}