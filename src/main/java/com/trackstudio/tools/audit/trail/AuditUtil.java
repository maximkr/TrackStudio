package com.trackstudio.tools.audit.trail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import com.trackstudio.app.csv.CSVImport;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.sman.tools.FileUtil;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.textfilter.DiffMatchPatch;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class AuditUtil {
    private final StringBuilder builder;
    private volatile boolean change;
    private final String mstatusId;
    private volatile boolean isTableClose = false;
    private final Calendar cal;
    private final Type type;

    public enum Type {
        USUAL, TASK, MESSAGE, ATTACHMENT
    }

    public AuditUtil(StringBuilder builder, String taskId, Calendar cal, Type type) throws GranException {
        this.builder = builder;
        this.change = !builder.toString().isEmpty();
        this.cal = cal;
        this.type = type;
        this.mstatusId = KernelManager.getStep().getMstatusesForAudit(taskId, CSVImport.LOG_MESSAGE);
        createHeaderTable();
    }

    private void createHeaderTable() {
        builder.append("<table class=\"general\" id=\"auditTable\" border=\"1px\" cellspacing=\"0\" cellpadding=4>");
    }

    public void buildTr(String nameTr, String oldValue, String newValue) {
        builder.append("<tr>");
        builder.append("<td align=\"right\">");
        builder.append(nameTr);
        builder.append("</td>");
        builder.append("<td><strike>");
        builder.append(oldValue != null && !oldValue.isEmpty() ? oldValue : " - ");
        builder.append("</strike></td>");
        builder.append("<td>");
        builder.append(newValue != null && !newValue.isEmpty() ? newValue : " - ");
        builder.append("</td>");
        builder.append("</tr>\n");
    }

    public void buildTrText(String nameTr, String oldValue, String newValue) {
        builder.append("<tr>");
        builder.append("<td align=\"right\">");
        builder.append(nameTr);
        builder.append("</td>");
        builder.append("<td colspan=\"2\">");
        builder.append(getDiffText(oldValue, newValue));
        builder.append("</td>");
        builder.append("</tr>\n");
    }

    public void buildTrList(String nameTr, String oldValue, String newValue, boolean taskUdf) {
        builder.append("<tr>");
        builder.append("<td align=\"right\">");
        builder.append(nameTr);
        builder.append("</td>");
        builder.append("<td colspan=\"2\">");
        builder.append(buildList(oldValue, newValue, taskUdf));
        builder.append("</td>");
        builder.append("</tr>\n");
    }

    public void buildTrDescription(String oldValue, String newValue) throws GranException {
        closeTable();
        builder.append("<br>");
        builder.append(getDiffText(oldValue, newValue));
    }

    private void closeTable() {
        if (!isTableClose) {
            builder.append("</table>");
        }
        isTableClose = true;
    }

    private String getDiffText(String oldValue, String newValue) {
        DiffMatchPatch dmp = new DiffMatchPatch();
        oldValue = oldValue == null ? "" : oldValue;
        newValue = newValue == null ? "" : newValue;
        LinkedList<DiffMatchPatch.Diff> diff = dmp.diff_main(oldValue, newValue);
        return dmp.diff_prettyHtml(diff);
    }

    public SafeString getTable() {
        closeTable();
        return SafeString.createSafeString(builder.toString());
    }

    public void addText(String text) {
        this.builder.append(text);
    }

    public <T> boolean checkSimpleValue(T value, T compareLine) {
        if (Null.isNotNull(value) && !value.equals(compareLine)) {
            change = true;
            return true;
        } else if (value == null && Null.isNotNull(compareLine)) {
            change = true;
            return true;
        }
        return false;
    }

    public String createLogMessage(SessionContext sc, String taskId, SafeString text) throws GranException {
        if (change) {
            if (mstatusId != null) {
                TaskRelatedInfo task = TaskRelatedManager.getInstance().find(taskId);
                return KernelManager.getMessage().createMessage(sc.getUserId(), taskId, mstatusId, text, 0L, null, null, null, task.getPriorityId(), task.getDeadline(), null, cal, type);
            }
        }
        return null;
    }

    public boolean isCreateMessage() {
        return mstatusId != null;
    }

    private static String buildList(String oldValue, String newValue, boolean taskUdf) {
        List<String> oldList = new ArrayList<String>(Arrays.asList(Null.stripNullText(oldValue).split(";")));
        List<String> newList = new ArrayList<String>(Arrays.asList(Null.stripNullText(newValue).split(";")));
        List<String> temp = new ArrayList<String>();
        for (String value : newList) {
            if (oldList.indexOf(value) != -1) {
                oldList.remove(oldList.indexOf(value));
                temp.add(value);
            }
        }
        for (String value : temp) {
            newList.remove(value);
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append("<strike>");
        addValue(buffer, oldList, taskUdf);
        buffer.append("</strike>");
        addValue(buffer, temp, taskUdf);
        buffer.append("<u>");
        addValue(buffer, newList, taskUdf);
        buffer.append("</u>");
        return buffer.toString();
    }

    private static void addValue(final StringBuffer buffer, List<String> list, boolean taskUdf) {
        for (String number : list) {
            if (taskUdf) {
                buffer.append("#");
            }
            buffer.append(number).append("<br>");
        }
    }

    /**
     * This method builds info about deleted task.
     * @param task Deleted task
     * @return text - time login numbers task
     * @throws GranException for necessary
     */
    private static String buildTextAboutDeletedTask(TaskRelatedInfo task, String login) throws GranException {
        StringBuilder sb = new StringBuilder(Calendar.getInstance().getTime().toString());
        sb.append(" ").append(login);
        sb.append(" parent_id=").append(task.getParentId()).append(",");
        sb.append(" task_name=").append(task.getName());
        sb.append(" [#").append(task.getNumber()).append("]\n");
        return sb.toString();
    }

    /**
     * This method add text in file about deleted task
     * @param taskId deleted task
     */
    public static void addAuditAboutDeletedTasks(String taskId, String login) throws GranException {
        TaskRelatedInfo task = TaskRelatedManager.getInstance().find(taskId);
        String text= buildTextAboutDeletedTask(task, login);
        FileUtil.addInfoAboutDeletedTasks(text);
    }
}