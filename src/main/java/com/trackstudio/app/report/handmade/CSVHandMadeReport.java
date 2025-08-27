package com.trackstudio.app.report.handmade;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.adapter.HandMadeReport;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.filter.list.MessageFilter;
import com.trackstudio.app.filter.list.TaskFilter;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.tools.formatter.DateFormatter;
import com.trackstudio.tools.textfilter.HTMLEncoder;
import com.trackstudio.view.TaskViewText;
import com.trackstudio.view.UDFValueViewCSV;

import net.jcip.annotations.NotThreadSafe;

/**
 * Класс для экспорта жанных в формат CSV
 */
@NotThreadSafe
public class CSVHandMadeReport implements HandMadeReport {
    private static final LockManager lockManager = LockManager.getInstance();
    private static Log log = LogFactory.getLog(CSVHandMadeReport.class);
    private String delimiter;

    private final static String endline = "\r\n";

    //    private boolean deepExport = false;
    private ArrayList<SecuredUDFBean> filteredUdfs = null;

    /**
     * Инициализипует класс
     *
     * @return TRUE - успешно, FALSE - нет
     */
    public boolean init() {
        return true;
    }

    /**
     * Возвращает описание класса
     *
     * @return описание класса
     */
    public String getDescription() {
        return "CSV Export Adapter";
    }

    /**
     * Конструктор класса
     *
     * @param delimiter разделитель
     */
    public CSVHandMadeReport(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Производит экспорт
     *
     * @param sc       сессия пользователя
     * @param taskId   ID задачи
     * @param filterId ID фильтра
     * @param encoding кодировка выходного файла
     * @return експортируемые данные
     * @throws GranException при необходимости
     */
    public String generateImpl(SessionContext sc, String taskId, String filterId, TaskFValue filter, String encoding) throws GranException {

        log.trace("generateImpl");
        try {
            TaskFValue flthm = filter;//AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(sc, filterId).getFValue();
            List view = flthm.getView();

            StringBuffer header = new StringBuffer();
            appendHeader(view, header, FieldMap.TASK_NUMBER);
            appendHeader(view, header, FieldMap.TASK_NAME);
            appendHeader(view, header, FieldMap.TASK_SHORTNAME);
            appendHeader(view, header, FieldMap.FULLPATH);
            appendHeader(view, header, FieldMap.TASK_CATEGORY);
            appendHeader(view, header, FieldMap.TASK_STATUS);
            appendHeader(view, header, FieldMap.TASK_RESOLUTION);
            appendHeader(view, header, FieldMap.TASK_PRIORITY);
            appendHeader(view, header, FieldMap.SUSER_NAME);
            appendHeader(view, header, FieldMap.SUSER_STATUS);
            appendHeader(view, header, FieldMap.HUSER_NAME);
            appendHeader(view, header, FieldMap.HUSER_STATUS);
            appendHeader(view, header, FieldMap.TASK_SUBMITDATE);
            appendHeader(view, header, FieldMap.TASK_UPDATEDATE);
            appendHeader(view, header, FieldMap.TASK_CLOSEDATE);
            appendHeader(view, header, FieldMap.TASK_DEADLINE);
            appendHeader(view, header, FieldMap.TASK_BUDGET);
            appendHeader(view, header, FieldMap.TASK_ABUDGET);


            SecuredTaskBean filtertask = new SecuredTaskBean(AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId).getTaskId(), sc);
            filteredUdfs = filtertask.getFilterUDFs();
            for (SecuredUDFBean udfvf : filteredUdfs) {
                if (view.contains(FValue.UDF + udfvf.getUdfId())) {
                    appendCSV(header, convertToCSV(udfvf.getCaption()));
                }

            }
            appendHeader(view, header, FieldMap.TASK_DESCRIPTION);
            if (view.contains(FieldMap.MESSAGEVIEW.getFilterKey())) {
                appendCSV(header, "\"" + FieldMap.MESSAGE_TASK.getAltKey() + "\"");
                appendCSV(header, "\"" + FieldMap.MSG_SUSER_NAME.getAltKey() + "\"");
                appendCSV(header, "\"" + FieldMap.MSG_HUSER_NAME.getAltKey() + "\"");
                appendCSV(header, "\"" + FieldMap.MSG_MSTATUS.getAltKey() + "\"");
                appendCSV(header, "\"" + FieldMap.MSG_RESOLUTION.getAltKey() + "\"");
                appendCSV(header, "\"" + FieldMap.MSG_PRIORITY.getAltKey() + "\"");
                appendCSV(header, "\"" + FieldMap.MSG_BUDGET.getAltKey() + "\"");
                appendCSV(header, "\"" + FieldMap.MSG_ABUDGET.getAltKey() + "\"");
                appendCSV(header, "\"" + FieldMap.MSG_SUBMITDATE.getAltKey() + "\"");
                appendCSV(header, "\"" + FieldMap.TEXT_MSG.getAltKey() + "\"");
            }
            header.append(endline);

            StringBuffer export = buildBody(taskId, sc, flthm);

            byte[] bytes = header.append(export).toString().getBytes(encoding);
            return new String(bytes, encoding);
        } catch (Exception e) {
            throw new GranException(e);
        }
    }

    private StringBuffer buildBody(String taskId, SessionContext sc, TaskFValue flthm) throws Exception {
        boolean lock = lockManager.acquireConnection(CSVHandMadeReport.class.getName());
        try {
            StringBuffer export = new StringBuffer();
            TaskFilter taskList = new TaskFilter(new SecuredTaskBean(taskId, sc));
            boolean notwithsub = flthm.get(FValue.SUBTASK) == null;
            ArrayList<SecuredTaskBean> taskCol = taskList.getTaskList(flthm, true, notwithsub, flthm.getSortOrder());
            for (SecuredTaskBean task : taskCol) {
                exportTask(sc, task.getId(), export, flthm);
            }
            return export;
        } finally {
            if (lock) lockManager.releaseConnection(CSVHandMadeReport.class.getName());
        }
    }

    private void appendHeader(List view, StringBuffer header, FieldMap map) {
        if (view.contains(map.getFilterKey()))
            appendCSV(header, "\"" + map.getAltKey() + "\"");
    }

    private void appendCSV(StringBuffer sb, String s) {

        if (sb.length() != 0)
            sb.append(delimiter); // not first param
        if (s != null && s.length() > 0)
            sb.append(s);
    }

    private String convertToCSV(String s) {
        HTMLEncoder r = new HTMLEncoder(s);
        r.replace("\"", "\"");
        s = r.toString();
        if (s.indexOf(delimiter) > -1 || s.indexOf("\n") > -1) {
            return "\"" + s + "\"";
        } else return s;
    }

    /**
     * Преобразует список в строку, разделяя значения разделителем
     *
     * @param va  список
     * @param div разделитель
     * @return строка
     */
    protected String listToString(List va, String div) {
        String ret = "";
        for (Iterator it = va.iterator(); it.hasNext();) {
            Object o = it.next();
            if (it.hasNext())
                ret += o.toString() + div;
            else
                ret += o.toString();
        }
        return ret;
    }

    private void exportTask(SessionContext sc, String taskid, StringBuffer fullExport, TaskFValue flthm) throws Exception {
        DateFormatter df = sc.getUser().getDateFormatter();
        SecuredTaskBean stb = new SecuredTaskBean(taskid, sc);

        List view = flthm.getView();
        StringBuffer export = new StringBuffer();
        int fieldsCount = 0;
        if (view.contains(FieldMap.TASK_NUMBER.getFilterKey())) {
            fieldsCount++;
            if (stb.getNumber() != null) {
                appendCSV(export, convertToCSV('#' + stb.getNumber()));
            } else {
                appendCSV(export, null);
            }
        }

        if (view.contains(FieldMap.TASK_NAME.getFilterKey())) {
            fieldsCount++;
            HTMLEncoder sb = new HTMLEncoder(stb.getName());
            sb.replace("\r\n", " ");
            sb.replace("\n", " ");
            appendCSV(export, convertToCSV(sb.toString()));
        }

        if (view.contains(FieldMap.TASK_SHORTNAME.getFilterKey())) {
            fieldsCount++;
            if (stb.getShortname() != null) {
                appendCSV(export, convertToCSV(stb.getShortname()));
            } else {
                appendCSV(export, null);
            }
        }
        if (view.contains(FieldMap.FULLPATH.getFilterKey())) {
            fieldsCount++;
            appendCSV(export, convertToCSV(new TaskViewText(stb).getFullPath()));
        }

        if (view.contains(FieldMap.TASK_CATEGORY.getFilterKey())) {
            fieldsCount++;
            if (stb.getCategory() != null) {
                appendCSV(export, convertToCSV(stb.getCategory().getName()));
            } else {
                appendCSV(export, null);
            }
        }

        if (view.contains(FieldMap.TASK_STATUS.getFilterKey())) {
            fieldsCount++;
            if (stb.getStatus() != null) {
                appendCSV(export, convertToCSV(stb.getStatus().getName()));
            } else {
                appendCSV(export, null);
            }
        }

        if (view.contains(FieldMap.TASK_RESOLUTION.getFilterKey())) {
            fieldsCount++;
            if (stb.getResolution() != null) {
                appendCSV(export, convertToCSV(stb.getResolution().getName()));
            } else {
                appendCSV(export, null);
            }
        }

        if (view.contains(FieldMap.TASK_PRIORITY.getFilterKey())) {
            fieldsCount++;
            if (stb.getPriority() != null) {
                appendCSV(export, convertToCSV(stb.getPriority().getName()));
            } else {
                appendCSV(export, null);
            }
        }

        if (view.contains(FieldMap.SUSER_NAME.getFilterKey())) {
            fieldsCount++;
            if (stb.getSubmitter() != null) {
                appendCSV(export, convertToCSV(stb.getSubmitter().getName()));
            } else {
                appendCSV(export, null);
            }
        }
        if (view.contains(FieldMap.SUSER_STATUS.getFilterKey())) {
            fieldsCount++;

            if (stb.getSubmitterPrstatuses() != null) {
                String val = listToString(stb.getSubmitterPrstatuses(), ";");

                appendCSV(export, convertToCSV(val));
            } else {
                appendCSV(export, null);
            }
        }

        if (view.contains(FieldMap.HUSER_NAME.getFilterKey())) {
            fieldsCount++;
            if (stb.getHandlerUserId() != null || stb.getHandlerGroupId() != null) {
                appendCSV(export, convertToCSV(stb.getHandlerUserId() != null ? stb.getHandlerUser().getName() : stb.getHandlerGroup().getName()));
            } else {
                appendCSV(export, null);
            }
        }

        if (view.contains(FieldMap.HUSER_STATUS.getFilterKey())) {
            fieldsCount++;

            if (stb.getHandlerPrstatuses() != null) {
                String val = listToString(stb.getHandlerPrstatuses(), ";");
                appendCSV(export, convertToCSV(val));
            } else {
                appendCSV(export, null);
            }
        }

        if (view.contains(FieldMap.TASK_SUBMITDATE.getFilterKey())) {
            fieldsCount++;
            if (stb.getSubmitdate() != null) {
                appendCSV(export, convertToCSV(df.parse(stb.getSubmitdate())));
            } else {
                appendCSV(export, null);
            }
        }

        if (view.contains(FieldMap.TASK_UPDATEDATE.getFilterKey())) {
            fieldsCount++;
            if (stb.getUpdatedate() != null) {
                appendCSV(export, convertToCSV(df.parse(stb.getUpdatedate())));
            } else {
                appendCSV(export, null);
            }
        }

        if (view.contains(FieldMap.TASK_CLOSEDATE.getFilterKey())) {
            fieldsCount++;
            if (stb.getClosedate() != null) {
                appendCSV(export, convertToCSV(df.parse(stb.getClosedate())));
            } else {
                appendCSV(export, null);
            }
        }

        if (view.contains(FieldMap.TASK_DEADLINE.getFilterKey())) {
            fieldsCount++;
            if (stb.getDeadline() != null) {
                appendCSV(export, convertToCSV(df.parse(stb.getDeadline())));
            } else {
                appendCSV(export, null);
            }
        }

        if (view.contains(FieldMap.TASK_BUDGET.getFilterKey())) {
            fieldsCount++;
            if (stb.getBudget() != null && stb.getBudget() > 0) {
                String formatted = "";
                if (stb.getBudget() != null) {
                    formatted = stb.getBudget().toString(); // в секундах
                }
                appendCSV(export, convertToCSV(formatted));
            } else {
                appendCSV(export, null);
            }
        }

        if (view.contains(FieldMap.TASK_ABUDGET.getFilterKey())) {
            fieldsCount++;
            if (stb.getActualBudget() != null && stb.getActualBudget() > 0) {
                String s = stb.getActualBudget().toString();
                appendCSV(export, convertToCSV(s));
            } else {
                appendCSV(export, null);
            }
        }

        ArrayList<SecuredUDFValueBean> filteredUDFValues = stb.getFilteredUDFValues();
        List<String> useUdfId = flthm.getView();
        if (!useUdfId.isEmpty() && !filteredUDFValues.isEmpty()) {
            for (SecuredUDFBean filtUdf : filteredUdfs) {
                if (view.contains(FValue.UDF + filtUdf.getUdfId())) {
                    SecuredUDFValueBean udf = findValueByKey(filteredUDFValues, filtUdf.getId());
                    String val = null;
                    if (udf != null) {
                        val = new UDFValueViewCSV(udf).getValue(stb);
                        if (udf.getUdfType() == 9) {
                            val = val.replaceAll("\\n", "");
                        }
                    }
                    fieldsCount++;
                    appendCSV(export, val != null ? convertToCSV(val) : "");
                }
            }
        } // if udf

        if (view.contains(FieldMap.TASK_DESCRIPTION.getFilterKey())) {
            fieldsCount++;
            if (stb.getDescription() != null) {
                appendCSV(export, convertToCSV(HTMLEncoder.br2nl(stb.getDescription())));
            } else {
                appendCSV(export, null);
            }
        }
        if (view.contains(FieldMap.MESSAGEVIEW.getFilterKey())) {
            for (int i = 0; i < 10; i++)
                appendCSV(export, null);
        }
        export.append(endline);

        if (view.contains(FieldMap.MESSAGEVIEW.getFilterKey())) {
            MessageFilter msgFilter = new MessageFilter(stb);
            for (SecuredMessageBean msg : stb.getMessages()) {
                if (msgFilter.pass(msg, flthm)) {
                    exportMessage(export, view, msg, fieldsCount);
                }
            }
        }
        fullExport.append(export);
    }

    private SecuredUDFValueBean findValueByKey(List<SecuredUDFValueBean> values, String key) {
        SecuredUDFValueBean result = null;
        for (SecuredUDFValueBean value : values) {
            if (value.getUdfId().equals(key)) {
                result = value;
                break;
            }
        }
        return result;
    }

    private void exportMessage(StringBuffer out, List view, SecuredMessageBean msg, int fieldsCnt) throws GranException {
        StringBuffer export = new StringBuffer();
        export.append(delimiter);
        for (int i = 2; i < fieldsCnt; i++)
            appendCSV(export, null); // на два меньше, потому что первый мы выше ставим, а последний не нужен, т.к. при выводе первого поля опять запятая будет
        SessionContext sc = msg.getSecure();
        DateFormatter df = sc.getUser().getDateFormatter();

        appendCSV(export, convertToCSV("#" + msg.getTask().getNumber()));

        if (msg.getSubmitter() != null) {
            appendCSV(export, convertToCSV(msg.getSubmitter().getName()));
        } else {
            appendCSV(export, null);
        }
        if (msg.getHandler() != null) {
            appendCSV(export, convertToCSV(msg.getHandler().getName()));
        } else {
            appendCSV(export, null);
        }

        appendCSV(export, convertToCSV(msg.getMstatus().getName()));

        if (msg.getResolution() != null) {
            appendCSV(export, convertToCSV(msg.getResolution().getName()));
        } else {
            appendCSV(export, null);
        }
        if (msg.getPriority() != null) {
            appendCSV(export, convertToCSV(msg.getPriority().getName()));
        } else {
            appendCSV(export, null);
        }
        if (msg.getBudget() != null && msg.getBudget() > 0) {
            String formatted = "";


            formatted = msg.getBudget().toString();
            appendCSV(export, convertToCSV(formatted));
        } else {
            appendCSV(export, null);
        }
        if (msg.getHrs() != null && msg.getHrs() > 0) {
            String formatted = "";


            formatted = msg.getHrs().toString();
            appendCSV(export, convertToCSV(formatted));
        } else {
            appendCSV(export, null);
        }
        if (msg.getTime() != null) {
            appendCSV(export, convertToCSV(df.parse(msg.getTime())));
        } else {
            appendCSV(export, null);
        }
        if (msg.getDescription() != null) {
            appendCSV(export, convertToCSV(HTMLEncoder.nl2br(msg.getDescription())));
        } else {
            appendCSV(export, null);
        }
        export.append(endline);
        out.append(export);
    }

    public String generateImpl(SessionContext sc, String taskId, String filterId, TaskFValue filter, String encoding, String linkXml) throws GranException {
        return generateImpl(sc, taskId, filterId, filter, encoding);
    }
}