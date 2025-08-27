package com.trackstudio.app;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.action.user.UserEditAction;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.containers.Link;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.form.MessageForm;
import com.trackstudio.form.TaskForm;
import com.trackstudio.form.UDFForm;
import com.trackstudio.form.UserForm;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Udf;
import com.trackstudio.secured.Secured;
import com.trackstudio.secured.SecuredStatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.textfilter.MacrosUtil;

import net.jcip.annotations.Immutable;

import static com.trackstudio.tools.Null.isNotNull;

/**
 * Класс, используемый для заполнения пользовательских полей
 */
@Immutable
public class UDFFormFillHelper {
    private static final LockManager lockManager = LockManager.getInstance();
    private static final Log log = LogFactory.getLog(UserEditAction.class);

    /**
     * ЗАполняет пользовательское поле
     *
     * @param uc            секурность
     * @param objectId      ID объекта
     * @param form          форма
     * @param udfValues     значения поля
     * @param request       запрос
     * @param formName      имя формы
     * @param isTaskUdf     тасковое ли поле
     * @param message       сообщение
     * @param newTaskStatus новая ли задача
     * @throws GranException при необходимости
     */
    public static void fillUdf(Secured uc, String objectId, UDFForm form, Collection<SecuredUDFValueBean> udfValues, HttpServletRequest request, String formName, boolean isTaskUdf, boolean message, SecuredStatusBean newTaskStatus) throws GranException {
        boolean dependRuleUdf = Config.isTurnItOn("trackstudio.mode.depend.access.rule");
        boolean editableTasksUdfsInOperation = Config.isTurnItOn("trackstudio.editable.tasks.udf.in.operation");
        SessionContext sc = uc.getSecure();
        boolean throwTriggerExp = isNotNull(sc.getAttribute(formName));
        List<Pair<SecuredUDFValueBean>> list = new ArrayList<Pair<SecuredUDFValueBean>>();
        String statusId = null;
        List<String> editable = new ArrayList<String>();
        List<String> viewable = new ArrayList<String>();
        boolean isUser = (form instanceof UserForm);
        if (form instanceof MessageForm) {
            editable = KernelManager.getUdf().getEditableUDFId(((MessageForm) form).getMstatus());
            viewable = KernelManager.getUdf().getViewableUDFId(((MessageForm) form).getMstatus());
        }
        if (isTaskUdf && newTaskStatus != null) {
            statusId = newTaskStatus.getId();
        } else if (isTaskUdf) {
            statusId = ((SecuredTaskBean) uc).getStatusId();
        }
        boolean isNew = false;
        String handler = null, submiter = null;
        if (form instanceof TaskForm) {
            TaskForm tf = (TaskForm) form;
            if (tf.getNewTask() != null && !tf.getNewTask().equalsIgnoreCase("false"))
                isNew = true;
            handler = tf.getHandler();
            submiter = sc.getUserId();
        }
        for (SecuredUDFValueBean securedUDFValueBean : udfValues) {
            SecuredUDFBean udfBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, securedUDFValueBean.getUdfId());
            boolean msgEdit = message && editable.contains(securedUDFValueBean.getId());
            if (msgEdit && dependRuleUdf) {
                msgEdit = securedUDFValueBean.isTaskUdfEditable(objectId, statusId);
            }
            boolean taskEdit = !isUser && !message && (isNew ? securedUDFValueBean.isNewTaskUdfEditable(objectId, statusId, submiter, handler) : securedUDFValueBean.isTaskUdfEditable(objectId, statusId));
            boolean userEdit = !isTaskUdf && securedUDFValueBean.isUserUdfEditable(objectId);
            boolean tasksUdfs = Null.isNull(udfBean.getWorkflowId()) && editableTasksUdfsInOperation && securedUDFValueBean.isTaskUdfEditable(objectId, statusId);
            boolean canEdit = msgEdit || taskEdit || userEdit || tasksUdfs;
            if (!securedUDFValueBean.isCalculated() && canEdit) {
                switch (securedUDFValueBean.getUdfType()) {
                    case UdfValue.URL: {
                        Link val = (Link) securedUDFValueBean.getValue();
                        if (!throwTriggerExp) {
                            form.setUdf(securedUDFValueBean.getId(), val != null && val.getLink() != null ? val.getLink() : udfBean.getDefaultUDF());
                            form.setUrl(securedUDFValueBean.getId(), val != null && val.getDescription() != null ? val.getDescription() : "");
                        }
                    }
                    break;
                    case UdfValue.TASK: {
                        List<SecuredTaskBean> ts = (List) securedUDFValueBean.getValue();
                        List<String> res = new ArrayList<String>();
                        if (ts != null){
                            Collections.sort(ts);
                            for (SecuredTaskBean t : ts) {
                                res.add(t.getNumber());
                            }
                        }
                        if (!throwTriggerExp) form.setUdflist(securedUDFValueBean.getId(), res != null ? (res).toArray(new String[]{}) : new String[]{});
                    }
                    break;
                    case UdfValue.USER: {
                        Object ts = securedUDFValueBean.getValue();
                        List<String> res = new ArrayList<String>();
                        if (ts != null) {
                            List<SecuredUserBean> subs = (List<SecuredUserBean>) ts;
                            securedUDFValueBean.setParticipant(subs);
                            for (SecuredUserBean u : subs) {
                                if (u.isActive()) {
                                    res.add(u.getLogin());
                                }
                            }
                        }
                        if (!throwTriggerExp) {
                            form.setUdflist(securedUDFValueBean.getId(), res != null ? (res).toArray(new String[]{}) : new String[]{});
                        } else {
                            String[] participated = (String[]) form.getUdf(securedUDFValueBean.getId());
                            if (participated != null) {
                                List<SecuredUserBean> users = new ArrayList<SecuredUserBean>();
                                for (String userId : participated) {
                                    users.add(new SecuredUserBean(userId, sc));
                                }
                                securedUDFValueBean.setParticipant(users);
                            }
                        }
                    }
                    break;
                    case UdfValue.MULTILIST: {
                        Object val = securedUDFValueBean.getValue();
                        String[] multiList = new String[]{securedUDFValueBean.getUdf().getDefaultUDF()};
                        if (val != null) {
                            List<Pair> pairList = (List<Pair>) val;
                            multiList = new String[pairList.size()];
                            for (int i = 0; i < pairList.size(); i++)
                                multiList[i] = pairList.get(i).getKey();
                        }
                        if (!throwTriggerExp) form.setUdflist(securedUDFValueBean.getId(), multiList);
                    }
                    break;
                    case UdfValue.LIST: {
                        Object val = securedUDFValueBean.getValue();
                        if (!throwTriggerExp) {
                            if (val != null) {
                                form.setUdf(securedUDFValueBean.getId(), ((Pair) val).getKey());
                            } else {
                                form.setUdf(securedUDFValueBean.getId(), securedUDFValueBean.getUdf().getDefaultUDF());
                            }
                        }
                    }
                    break;
                    case UdfValue.DATE: {
                        Object o = securedUDFValueBean.getValue();
                        Calendar val = null;
                        if (o != null) {
                            val = (Calendar) o;
                        }
                        if (!throwTriggerExp) form.setUdf(securedUDFValueBean.getId(), val != null ? uc.getSecure().getUser().getDateFormatter().parse(val) : udfBean.getDefaultUDF());

                    }
                    break;
                    case UdfValue.FLOAT: {
                        String val = securedUDFValueBean.getValue() != null ? securedUDFValueBean.getValue().toString() : "";
                        String valueString = udfBean.getDefaultUDF();
                        if (isNotNull(val)) {
                            valueString = val;
                        }
                        if (!throwTriggerExp) form.setUdf(securedUDFValueBean.getId(), valueString);
                    }
                    break;
                    case UdfValue.INTEGER: {
                        Integer i = (Integer) securedUDFValueBean.getValue();
                        if (!throwTriggerExp) form.setUdf(securedUDFValueBean.getId(), i != null ? i : udfBean.getDefaultUDF());
                    }
                    break;
                    default:
                        Object val = securedUDFValueBean.getValue();
                        if (!throwTriggerExp) form.setUdf(securedUDFValueBean.getId(), val != null ? val : udfBean.getDefaultUDF());
                        break;
                }
                list.add(new Pair<SecuredUDFValueBean>(securedUDFValueBean, true));
            } else {
                if (form instanceof TaskForm && "true".equals(((TaskForm) form).getNewTask()) && securedUDFValueBean.isCalculated()) {
                    continue;
                }
                boolean isUdfWorkflow = securedUDFValueBean.getUdf().getWorkflowId() != null;
                if (isUdfWorkflow) {
                    Set<String> prstatuses = TaskRelatedManager.getInstance().getAllowedPrstatuses(uc.getSecure().getUserId(), objectId);
                    boolean isViewable = KernelManager.getUdf().isTaskUdfViewableFast(prstatuses, objectId, uc.getSecure().getUserId(), securedUDFValueBean.getId());
                    if (viewable.contains(securedUDFValueBean.getId())) {
                        if (dependRuleUdf) {
                            if (isViewable) {
                                list.add(new Pair<SecuredUDFValueBean>(securedUDFValueBean, false));
                            }
                        } else {
                            list.add(new Pair<SecuredUDFValueBean>(securedUDFValueBean, false));
                        }
                    } else if (isViewable && form instanceof TaskForm) {
                        list.add(new Pair<SecuredUDFValueBean>(securedUDFValueBean, false));
                    }
                } else {
                    list.add(new Pair<SecuredUDFValueBean>(securedUDFValueBean, false));
                }
            }
        }
        Comparator<Pair<SecuredUDFValueBean>> cmpPair = new Comparator<Pair<SecuredUDFValueBean>>() {
            @Override
            public int compare(Pair<SecuredUDFValueBean> o1, Pair<SecuredUDFValueBean> o2) {
                SecuredUDFValueBean value1 = o1.getT();
                SecuredUDFValueBean value2 = o2.getT();
                return value1.compareTo(value2);
            }
        };
        MacrosUtil.Block<SecuredUDFValueBean, Boolean, SecuredUDFValueBean> block = new MacrosUtil.Block<SecuredUDFValueBean, Boolean, SecuredUDFValueBean>() {
            @Override
            public void make(Map<SecuredUDFValueBean, Boolean> map, Pair<SecuredUDFValueBean> pair) {
                map.put(pair.getT(), pair.isBoolValue());
            }
        };
        Map<SecuredUDFValueBean, Boolean> udfMap = MacrosUtil.<SecuredUDFValueBean, Boolean, SecuredUDFValueBean>sortMap(list, cmpPair, block);
        sc.setRequestAttribute(request, "udfMap", udfMap);
        sc.setRequestAttribute(request, "formname", formName);
    }

    /**
     * Преобразует список значений в строку
     *
     * @param va  список значений
     * @param div разделитель
     * @return строка
     */
    public static String listToString(List<String> va, String div) {
        if (va == null)
            return null;
        StringBuffer ret = new StringBuffer();
        Collections.sort(va);
        for (Iterator<String> it = va.iterator(); it.hasNext(); ) {
            String o = it.next();
            ret.append(o);
            if (it.hasNext())
                ret.append(div);
        }
        return isNotNull(ret.toString()) ? ret.toString() : null;
    }

    /**
     * Возвращает карту пользовательских полей
     *
     * @param sc           сессия
     * @param id           ID
     * @param udfColl      список полей
     * @param sf           форма
     * @param mstatusId    ID типа сообщения
     * @param newTaskState новая ли задача
     * @return карта полей
     * @throws GranException при необходимости
     */
    public HashMap<String, String> getUdfMap(SessionContext sc, String id, Collection udfColl, UDFForm sf, String mstatusId, String newTaskState) throws GranException {
        HashMap<String, String> udfMap = new HashMap<String, String>();
        boolean lock = lockManager.acquireConnection(UDFFormFillHelper.class.getName());
        try {
            boolean dependRuleUdf = Config.isTurnItOn("trackstudio.mode.depend.access.rule");
            boolean editableTasksUdfsInOperation = Config.isTurnItOn("trackstudio.editable.tasks.udf.in.operation");
            boolean message = mstatusId != null;
            SecuredTaskBean stb = new SecuredTaskBean(id, sc);
            List<String> editable = new ArrayList<String>();
            if (message)
                editable = KernelManager.getUdf().getEditableUDFId(mstatusId);
            for (Iterator ulit = udfColl.iterator(); ulit.hasNext(); ) {
                int type;
                String uitemCaption;
                String uitemId;
                if (message) {
                    SecuredUDFValueBean uitem = (SecuredUDFValueBean) ulit.next();
                    SecuredUDFBean udfBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, uitem.getUdfId());
                    boolean tasksUdfEditable = KernelManager.getUdf().isTaskUdfEditable(id, sc.getUserId(), uitem.getUdfId(), newTaskState != null ? newTaskState : stb.getStatusId());
                    boolean tasksUdfsWithProperties = Null.isNull(udfBean.getWorkflowId()) && editableTasksUdfsInOperation && tasksUdfEditable;
                    if (!tasksUdfsWithProperties && !editable.contains(uitem.getUdfId()))
                        continue;
                    if (!tasksUdfsWithProperties && dependRuleUdf) {
                        if (!uitem.isTaskUdfEditable(id, stb.getStatusId()))
                            continue;
                        if (!tasksUdfEditable && !KernelManager.getUdf().getEditableUDFId(mstatusId).contains(uitem.getUdfId()))
                            continue;
                    }
                    type = uitem.getUdfType();
                    uitemCaption = uitem.getCaption();
                    uitemId = uitem.getId();
                } else {
                    SecuredUDFBean uitem = (SecuredUDFBean) ulit.next();
                    boolean isNew = false;
                    String handler = null, submiter = null;
                    if (sf instanceof TaskForm) {
                        TaskForm tf = (TaskForm) sf;
                        if (tf.getNewTask() != null && !tf.getNewTask().equalsIgnoreCase("false"))
                            isNew = true;
                        handler = tf.getHandler();
                        submiter = sc.getUserId();
                    }
                    if (isNew) {
                        if (!KernelManager.getUdf().isNewTaskUdfEditable(id, sc.getUserId(), uitem.getUdfId(), newTaskState != null ? newTaskState : stb.getStatusId(), submiter, handler))
                            continue;
                    } else if (!KernelManager.getUdf().isTaskUdfEditable(id, sc.getUserId(), uitem.getUdfId(), newTaskState != null ? newTaskState : stb.getStatusId()))
                        continue;
                    type = uitem.getType();
                    uitemCaption = uitem.getCaption();
                    uitemId = uitem.getId();
                }
                String valueUDF = "";
                Object uval = sf.getUdf(uitemId);
                switch (type) {
                    case UdfValue.DATE:
                        if (uval != null) {
                            //      DateFormatter df = new DateFormatter(sc.getTimezone(), sc.getLocale());
                            valueUDF = uval.toString();
                        }
                        break;
                    case UdfValue.FLOAT:
                        if (uval != null) {
                            if (isNotNull(uval)) {
                                uval = uval.toString().replace(",", ".");
                                double d = Double.parseDouble(uval.toString());
                                if (d > 1.0E+14) {
                                    throw new UserException("DOUBLE_TOO_LONG", new Object[] {uitemCaption});
                                }
                                valueUDF = uval.toString();
                            }
                        }
                        break;
                    case UdfValue.URL: {
                        if (uval != null && uval.toString().length() > 0)
                            valueUDF = sf.getUdf(uitemId) + "\n" + sf.getUrl(uitemId);
                    }
                    break;
                    case UdfValue.LIST: {
                        if (uval != null && !"NotChoosen".equals(uval)) {
                            Udf udf = KernelManager.getFind().findUdf(uitemId);
                            Map<String, String> hm = KernelManager.getUdf().getUdflist(uitemId);
                            String listUdfval = sf.getUdf(uitemId).toString();
                            for (Map.Entry e : hm.entrySet()) {
                                if (e.getKey().equals(listUdfval)) {
                                    valueUDF += e.getValue();
                                    break;
                                }
                            }
                        }
                    }
                    break;
                    case UdfValue.MULTILIST: {
                        String[] values = sf.getUdflist(uitemId);
                        if (values != null) {
                            Map<String, String> hm = KernelManager.getUdf().getUdflist(uitemId);
                            Collection<String> listUdf = hm.keySet();
                            List<String> vas = new ArrayList<String>();
                            for (String value : values) {
                                for (String o : listUdf) {
                                    if (o.equals(value)) {
                                        vas.add(hm.get(o));
                                    }
                                }
                            }
                            valueUDF = UDFFormFillHelper.listToString(vas, ";\n");
                        }
                    }
                    break;
                    case UdfValue.TASK:
                        if (uval != null) {

                            List<String> sel = new ArrayList<String>();

                            String[] values = sf.getUdflist(uitemId);
                            if (values != null) {
                                for (String v : values) {
                                    SecuredTaskBean stbeab = AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskByNumber(sc, v.replace('#', ' ').trim());
                                    if (stbeab != null) sel.add('#' + stbeab.getNumber());
                                }
                            }
                            valueUDF = UDFFormFillHelper.listToString(sel, ";");
                        }
                        break;
                    case UdfValue.USER:
                        if (uval != null) {
                            List<String> sel = new ArrayList<String>();
                            Object[] values = sf.getUdflist(uitemId);
                            if (values != null) {
                                for (Object value : values) {
                                    SecuredUserBean bean1 = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(sc, value.toString().trim());
                                    if (bean1 != null) sel.add('@' + bean1.getLogin());
                                }
                            }
                            valueUDF = UDFFormFillHelper.listToString(sel, ";");
                        }
                        break;
                    default:
                        if (uval != null) {
                            if (type == UdfValue.INTEGER || type == UdfValue.FLOAT) {
                                valueUDF = uval.toString().trim();
                            } else {
                                valueUDF = uval.toString().replaceAll("&nbsp;", " ");
                            }
                        }
                }

                udfMap.put(uitemCaption, valueUDF);
            }
        } finally {
            if (lock) lockManager.releaseConnection(UDFFormFillHelper.class.getName());
        }
        return udfMap;
    }

    /**
     * Возвращает карту значений пользовательских полей для задачи
     *
     * @param task задача
     * @return карта значений
     * @throws GranException при необходимости
     */
    public static HashMap<String, String> simplifyUdf(SecuredTaskBean task) throws GranException {
        ArrayList<SecuredUDFValueBean> udfValues = task.getUDFValuesList();
        HashMap<String, String> values = new HashMap<String, String>();
        for (SecuredUDFValueBean udf : udfValues) {
            values.put(udf.getCaption(), convert(udf));
        }
        return values;
    }

    public static String convert(SecuredUDFValueBean udf) throws GranException {
        Object val = udf.getValue();
        if (val != null) {
            if (udf.getUdfType() == UdfValue.URL) {
                return String.valueOf(val);
            } else if (udf.getUdfType() == UdfValue.TASK) {
                List<SecuredTaskBean> list = (List<SecuredTaskBean>) val;
                List<String> res = new ArrayList<String>();
                for (SecuredTaskBean t : list) {
                    res.add(t.getNumber());
                }
                return listToString(res, "; #");
            } else if (udf.getUdfType() == UdfValue.USER) {
                List<SecuredUserBean> list = (List<SecuredUserBean>) val;
                List<String> res = new ArrayList<String>();
                for (SecuredUserBean u : list) {
                    res.add(u.getLogin());
                }
                return listToString(res, "; ");
            } else if (udf.getUdfType() == UdfValue.MULTILIST) {
                List<Pair> list = (List<Pair>) val;
                List<String> res = new ArrayList<String>();
                for (Pair u : list) {
                    res.add(u.getValue());
                }
                return listToString(res, ";");
            } else if (udf.getUdfType() == UdfValue.LIST) {
                Pair p = (Pair) val;
                return p.getValue();
            } else if (udf.getUdfType() == UdfValue.DATE) {
                Calendar valDat = (Calendar) val;
                return  udf.getSecure().getUser().getDateFormatter().parse(valDat);
            } else if (udf.getUdfType() == UdfValue.FLOAT) {
                return String.valueOf(val);
            } else {
                return String.valueOf(val);
            }
        }
        return null;
    }

    public static HashMap<String, String> getWorkflowUDFValues(SecuredTaskBean task) throws GranException {
        ArrayList<SecuredUDFValueBean> udfValues = task.getWorkflowUDFValues();
        HashMap<String, String> values = new HashMap<String, String>();
        for (SecuredUDFValueBean udf : udfValues) {
            values.put(udf.getCaption(), convert(udf));
        }
        return values;
    }

    public static void isValidateScript(List<SecuredUDFValueBean> udfs) throws GranException {
        UserException ue = null;
        for (SecuredUDFValueBean udf : udfs) {
            udf.setScriptException(false);
            if (udf.isLookup()) {
                String message = "Script " + udf.getUdf().getLookupscript() + ", field name: " + udf.getCaption() + "<br>";
                try {
                    udf.getList();
                } catch (Exception e) {
                    log.error("Error", e);
                    udf.setScriptException(true);
                    if (ue == null) {
                        ue = new UserException(e.getMessage(), false);
                    } else {
                        ue.addActionMessages(new UserException(message + e.getMessage(), false).getActionMessages());
                    }
                }
            }
        }
        if (ue != null) {
            throw ue;
        }
    }
}
