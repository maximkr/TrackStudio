package com.trackstudio.tools.audit.trail;

import java.util.List;
import java.util.Map;

import com.trackstudio.app.UdfValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Resolution;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredStatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTransitionBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.view.UDFValueViewText;

import net.jcip.annotations.Immutable;

@Immutable
public class AuditTrailMessage {
    private final AuditUtil builder;
    private final SessionContext sc;
    private final SecuredTaskBean currentTask;

    public AuditTrailMessage(SessionContext sc, String taskId, AuditUtil auditUtil) throws GranException {
        this.sc = sc;
        this.currentTask = new SecuredTaskBean(taskId, sc);
        this.builder = auditUtil;
    }

    public String auditMessage(SecuredMessageTriggerBean message) throws GranException {
        String nameTr, oldValue, newValue;
        if (builder.isCreateMessage()) {
            if (sc.canAction(Action.editTaskBudget, currentTask.getId()) && builder.checkSimpleValue(currentTask.getBudget(), message.getBudget())) {
                nameTr = I18n.getString(sc, "BUDGET");
                builder.buildTr(nameTr, currentTask.getBudgetAsString(), message.getBudgetAsString());
            }
            if (sc.canAction(Action.editTaskDeadline, currentTask.getId()) && builder.checkSimpleValue(currentTask.getDeadline(), message.getDeadline())) {
                nameTr = I18n.getString(sc, "DEADLINE");
                builder.buildTr(nameTr, currentTask.getDeadlineAsString(), message.getDeadlineAsString());
            }
            if (sc.canAction(Action.editTaskPriority, currentTask.getId()) && builder.checkSimpleValue(currentTask.getPriorityId(), message.getPriorityId())) {
                nameTr = I18n.getString(sc, "PRIORITY");
                oldValue = currentTask.getPriority() != null ? currentTask.getPriority().getName() : null;
                newValue = message.getPriority() != null ? message.getPriority().getName() : null;
                builder.buildTr(nameTr, oldValue, newValue);
            }
            List<Resolution> resolutions = KernelManager.getWorkflow().getResolutionList(message.getMstatusId());
            if (!resolutions.isEmpty() && builder.checkSimpleValue(currentTask.getResolutionId(), message.getResolutionId())) {

                nameTr = I18n.getString(sc, "RESOLUTION");
                oldValue = currentTask.getResolutionId() != null ? currentTask.getResolution().getName() : null;
                newValue = message.getResolutionId() != null ? message.getResolution().getName() : null;
                builder.buildTr(nameTr, oldValue, newValue);
            }
            if (sc.canAction(Action.editTaskHandler, currentTask.getId()) && builder.checkSimpleValue(currentTask.getHandlerUserId(), message.getHandlerUserId())) {
                nameTr = I18n.getString(sc, "HANDLER");
                oldValue = currentTask.getHandlerUserId() != null ? currentTask.getHandlerUser().getName() : null;
                newValue = message.getHandlerUser() != null ? message.getHandlerUser().getName() : null;
                builder.buildTr(nameTr, oldValue, newValue);
            }
            if (sc.canAction(Action.editTaskHandler, currentTask.getId()) && builder.checkSimpleValue(currentTask.getHandlerGroupId(), message.getHandlerGroupId())) {
                nameTr = I18n.getString(sc, "HANDLER");
                oldValue = currentTask.getHandlerGroupId() != null ? currentTask.getHandlerGroup().getName() : null;
                newValue = message.getHandlerGroupId() != null && !message.getHandlerGroupId().isEmpty() ? message.getHandlerGroup().getName() : null;
                builder.buildTr(nameTr, oldValue, newValue);
            }
            SecuredStatusBean newState = null;
            if (message.getMstatus() != null) {
                for (SecuredTransitionBean transitionBean : message.getMstatus().getTransitions()) {
                    if (transitionBean.getStart().equals(currentTask.getStatus())) {
                        newState = transitionBean.getFinish();
                        break;
                    }
                }
            }
            if (builder.checkSimpleValue(currentTask.getStatus(), newState)) {
                nameTr = I18n.getString(sc, "TASK_STATE");
                oldValue = currentTask.getStatus() != null ? currentTask.getStatus().getName() : null;
                newValue = newState != null ? newState.getName() : null;
                builder.buildTr(nameTr, oldValue, newValue);
            }
            if (message.getUdfValues() != null) {
                Map<String, SecuredUDFValueBean> udfs = currentTask.getUDFValues();
                for (String caption : message.getUdfValues().keySet()) {
                    oldValue = null;
                    SecuredUDFValueBean oldUdf = getSecuredUDFValueBean(udfs, caption);
                    if (oldUdf != null && oldUdf.isCalculated()) {
                        continue;
                    }
                    if (oldUdf != null) {
                        UDFValueViewText viewCSV = new UDFValueViewText(oldUdf);
                        oldValue = viewCSV.getValue(currentTask);
                    }
                    newValue = message.getUdfValues().get(caption);
                    Integer type = oldUdf != null ? oldUdf.getUdfType() : -1;
                    if (builder.checkSimpleValue(oldValue, newValue)) {
                        nameTr = caption;
                        if (type.equals(UdfValue.INTEGER) || type.equals(UdfValue.FLOAT) || type.equals(UdfValue.DATE) || type.equals(UdfValue.LIST)) {
                            builder.buildTr(nameTr, oldValue, newValue);
                        } else if (type.equals(UdfValue.TASK) || type.equals(UdfValue.USER) || type.equals(UdfValue.MULTILIST)) {
                            builder.buildTrList(nameTr, oldValue, newValue, type.equals(UdfValue.TASK));
                        } else {
                            builder.buildTrText(nameTr, oldValue, newValue);
                        }
                    }
                }
            }
            return builder.createLogMessage(sc, currentTask.getId(), builder.getTable());
        }
        return null;
    }

    private static SecuredUDFValueBean getSecuredUDFValueBean(Map<String, SecuredUDFValueBean> udfs, String caption) {
        for (Map.Entry<String, SecuredUDFValueBean> entry : udfs.entrySet()) {
            if (entry.getValue().getCaption().equals(caption)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
