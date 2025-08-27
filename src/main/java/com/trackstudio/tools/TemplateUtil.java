package com.trackstudio.tools;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.Preferences;
import com.trackstudio.app.Slider;
import com.trackstudio.app.TriggerManager;
import com.trackstudio.app.UDFFormFillHelper;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.csv.CSVImport;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.filter.list.MessageFilter;
import com.trackstudio.app.filter.list.TaskFilter;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.AttachmentManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredPriorityBean;
import com.trackstudio.secured.SecuredSearchTaskItem;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTaskFValueBean;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.formatter.DateFormatter;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.NotThreadSafe;

/**
 * Класс содержит методы для работы с шаблонами
 */
@NotThreadSafe
public class TemplateUtil {
    private static final LockManager lockManager = LockManager.getInstance();
    private static final Log log = LogFactory.getLog(TemplateUtil.class);
    private CopyOnWriteArrayList<SecuredTaskBean> tasks = new CopyOnWriteArrayList<SecuredTaskBean>();
    /**
     * Внутренняя сессия
     */
    private final SessionContext innerSession;

    /**
     * Конструктор
     *
     * @param sc сессия пользователя
     */
    public TemplateUtil(SessionContext sc) {
        this.innerSession = sc;
    }

    /**
     * Возврашает фильтр по его названию
     *
     * @param filterName название фильтра
     * @param task       задачи
     * @return фильтр
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredFilterBean
     */
    private SecuredFilterBean findFilterById(String filterName, SecuredTaskBean task) throws GranException {
        List<SecuredFilterBean> filters = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFilterList(this.innerSession, task.getId());
        for (SecuredFilterBean b : filters) {
            if (b.canView() && (b.getId().equals(filterName) || b.getName().equals(filterName))) {
                return b;
            }
        }
        return null;
    }

    /**
     * Возвращает список задач для указанной задачи и фильтра
     *
     * @param taskObject задача
     * @param filter     фильтр
     * @return список задач
     */
    public List<SecuredTaskBean> subtasks(Object taskObject, String filter) {
        boolean lock = lockManager.acquireConnection(TemplateUtil.class.getName());
        try {
            SecuredTaskBean task = findTask(taskObject);
            if (task == null)
                return new ArrayList<SecuredTaskBean>();
            TaskFilter tf = new TaskFilter(task);
            SecuredFilterBean f;
            try {
                f = findFilterById(filter, task);
            } catch (GranException e) {
                log.error("Exception ", e);
                return new ArrayList<SecuredTaskBean>();
            }
            if (f != null) {
                SecuredTaskFValueBean settings = null;
                try {
                    settings = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(innerSession, f.getId());
                } catch (GranException e) {
                    log.error("Exception ", e);
                    return new ArrayList<SecuredTaskBean>();
                }
                TaskFValue fValue = settings.getFValue();
                try {
                    boolean notwithsub = fValue.get(FValue.SUBTASK) == null;
                    return tf.getTaskList(fValue, fValue.needFilterUDF(), notwithsub, fValue.getSortOrder());

                } catch (GranException e) {
                    log.error("Exception ", e);
                    return new ArrayList<SecuredTaskBean>();
                }
            }
            return new ArrayList<SecuredTaskBean>();
        } finally {
            if (lock) lockManager.releaseConnection(TemplateUtil.class.getName());
        }
    }

    /**
     * Возвращает список сообщений для задачи и фильтра
     *
     * @param taskObject задача
     * @param filter     фильтр
     * @return список сообщений
     */
    public List<SecuredMessageBean> messages(Object taskObject, String filter) {
        SecuredTaskBean task = findTask(taskObject);
        SecuredFilterBean f;
        try {
            f = findFilterById(filter, task);
        } catch (GranException e) {
            log.error("Exception ", e);
            return new ArrayList<SecuredMessageBean>();
        }
        if (f != null) {
            try {
                return AdapterManager.getInstance().getSecuredMessageAdapterManager().getMessageList(innerSession, task.getId(), f.getId());
            } catch (GranException e) {
                log.error("Exception ", e);
                return new ArrayList<SecuredMessageBean>();
            }
        }
        return new ArrayList<SecuredMessageBean>();
    }

    /**
     * Возвращает слайдер задач для указанной задачи, фильтра и страницы
     *
     * @param taskObject задача
     * @param filter     фильтр
     * @param page       страница
     * @return слайдер задач
     * @see com.trackstudio.app.Slider
     */
    public Slider<SecuredTaskBean> subtasks(Object taskObject, String filter, String page) {
        SecuredTaskBean task = findTask(taskObject);
        SecuredFilterBean f;
        try {
            f = findFilterById(filter, task);
            String name = f != null ? f.getName() : "not found!";
            log.debug("FILTER TEMPLATE NAME: " + filter + "\t\tFILTER FOUND: " + name);
        } catch (GranException e) {
            log.error("Exception ", e);
            return new Slider<SecuredTaskBean>();
        }
        if (f != null) {
            SecuredTaskFValueBean settings;
            try {
                settings = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(task.getSecure(), f.getId());
            } catch (GranException e) {
                return new Slider<SecuredTaskBean>();
            }
            TaskFValue fValue = settings.getFValue();
            int pageNumber = 1;
            if (page != null) {
                try {
                    pageNumber = Integer.parseInt(page);
                } catch (NumberFormatException ne) {
                }
            }
            boolean taskUDFView;
            try {
                taskUDFView = fValue.needFilterUDF() && !task.getFilterUDFs().isEmpty();
            } catch (GranException e) {
                taskUDFView = false;
            }
            try {
                return AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskList(task.getSecure(), task.getId(), fValue, taskUDFView, pageNumber, null);
            } catch (GranException e) {
                log.error("Exception ", e);
                return new Slider<SecuredTaskBean>();
            }
        }
        return new Slider<SecuredTaskBean>();
    }


    /**
     * Возвращает путь от корня до указанной задачи
     *
     * @param taskObject задача
     * @return список задач
     * @see com.trackstudio.secured.SecuredTaskBean
     */
    public ArrayList<SecuredTaskBean> fullPath(Object taskObject) {
        try {
            SecuredTaskBean task = findTask(taskObject);
            return AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskChain(task.getSecure(), null, task.getId());
        } catch (GranException e) {
            log.error("Exception ", e);
            return new ArrayList<SecuredTaskBean>();
        }
    }

    /**
     * Возвращает путь от одной задачи до другой
     *
     * @param fromObject первая задача
     * @param toObject   вторая задача
     * @return список задач
     */
    public ArrayList<SecuredTaskBean> path(Object fromObject, Object toObject) {
        try {
            String fromId = null, toId = null;

            if (fromObject != null) {
                if (fromObject instanceof SecuredTaskBean)
                    fromId = ((SecuredTaskBean) fromObject).getId();
                else
                    fromId = CSVImport.findTaskIdByNumber(fromObject.toString());
            }
            if (toObject != null) {
                if (toObject instanceof SecuredTaskBean)
                    toId = ((SecuredTaskBean) toObject).getId();
                else
                    toId = CSVImport.findTaskIdByNumber(toObject.toString());
            }
            return AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskChain(innerSession, fromId, toId);
        } catch (GranException e) {
            log.error("Exception ", e);
            return new ArrayList<SecuredTaskBean>();
        }
    }

    /**
     * Проверяет на наличие пути между двумя задачами
     *
     * @param fromObject первая задача
     * @param toObject   вторая задача
     * @return TRUE - путь есть, FALSE - нет
     */
    public boolean hasPath(Object fromObject, Object toObject) {
        try {
            String fromId = null, toId = null;
            if (fromObject != null) {
                if (fromObject instanceof SecuredTaskBean)
                    fromId = ((SecuredTaskBean) fromObject).getId();
                else
                    fromId = CSVImport.findTaskIdByNumber(fromObject.toString());
            }
            if (toObject != null) {
                if (toObject instanceof SecuredTaskBean)
                    toId = ((SecuredTaskBean) toObject).getId();
                else
                    toId = CSVImport.findTaskIdByNumber(toObject.toString());
            }
            return TaskRelatedManager.getInstance().hasPath(fromId, toId);
        } catch (GranException e) {
            log.error("Exception ", e);
            return false;
        }
    }

    /**
     * Возвращает карту параметров шаблона по его ссылке
     *
     * @param requestUrl ссылка
     * @return карта параметров
     */
    public static HashMap<String, String> parseTemplateURL(String requestUrl) {
        HashMap<String, String> templateParameters = new HashMap<String, String>();
        String[] param = requestUrl.split("/");
        if (param.length > 0) {
            for (int j = 0; j < param.length; j++) {
                if (param[j].equals("template")) {
                    j++;
                    try {
                        templateParameters.put("template", URLDecoder.decode(param[j], Config.getEncoding()));

                    } catch (UnsupportedEncodingException uee) {
                        templateParameters.put("template", URLDecoder.decode(param[j]));
                    }
                    StringBuffer buf = new StringBuffer();

                    if (!param[j + 1].equals("task")) do {
                        j++;
                        buf.append("/").append(param[j]);
                    }
                    while (j + 1 < param.length && !param[j + 1].equals("task"));
                    try {
                        templateParameters.put("file", URLDecoder.decode(buf.toString(), Config.getEncoding()));
                    } catch (UnsupportedEncodingException uee) {
                        templateParameters.put("file", URLDecoder.decode(buf.toString()));
                    }

                } else if (param[j].equals("task")) {
                    j++;
                    templateParameters.put("task", param[j]);
                } else if (param[j].equals("filter")) {
                    if (j < param.length - 1) {
                        j++;

                        try {
                            templateParameters.put("filter", URLDecoder.decode(param[j], Config.getEncoding()));
                        } catch (UnsupportedEncodingException uee) {
                            templateParameters.put("filter", URLDecoder.decode(param[j]));
                        }
                    }
                } else if (param[j].equals("attachment")) {
                    if (j < param.length - 1) {
                        j++;
                        try {
                            templateParameters.put("attachment", URLDecoder.decode(param[j], Config.getEncoding()));
                        } catch (UnsupportedEncodingException uee) {
                            templateParameters.put("attachment", URLDecoder.decode(param[j]));
                        }

                    }
                }
            }
        }
        return templateParameters;
    }

    /**
     * Проверяет существование приложенного файла
     *
     * @param url ссылка на файл
     * @return = TRUE - файл существует, FALSE - нет
     */
    public boolean attachmentExists(String url) {
        HashMap<String, String> params = parseTemplateURL(url);
        if (params.containsKey("task") && params.containsKey("attachment")) {
            String taskId;
            try {
                taskId = CSVImport.findTaskIdByNumber(params.get("task"));
            } catch (GranException e) {
                return false;
            }
            File f = AttachmentManager.getInstance().getAttachmentFile(taskId, null, params.get("attachment"), false);
            return f.exists();
        }
        return false;
    }

    /**
     * Возвращает список локалей, в виде объектов типа Pair
     *
     * @return список локалей
     * @see com.trackstudio.tools.Pair
     */
    public List<Pair> locales() {
        List<Locale> lc = DateFormatter.getAvailableLocales();
        ArrayList<Pair> localeNames = new ArrayList<Pair>();
        for (Locale aLc : lc) {
            localeNames.add(new Pair(aLc.toString(), aLc.getDisplayName(DateFormatter.getLocaleFromString(Config.getInstance().getDefaultLocale()))));
        }
        Collections.sort(localeNames);
        return localeNames;
    }

    /**
     * Возвращает список таймзон
     *
     * @return список таймзон
     */
    public List<String> timezones() {
        List<String> timezones = Arrays.asList(SimpleTimeZone.getAvailableIDs());
        Collections.sort(timezones);
        return timezones;
    }

    /**
     * Регистрирует нового пользователя, в качестве логина и имени пользователя используется email
     *
     * @param email        E-mail
     * @param registration Правило регистрации
     * @param task         Задача
     * @param locale       Локаль
     * @param timezone     Таймзона
     * @return пароль
     * @throws GranException при необходимости
     */
    public String register(String email, String registration, String task, String locale, String timezone) throws GranException {
        String login = email;
        String name = email;
        String registrationId = KernelManager.getRegistration().getRegistrationByName(registration, task);
        String pwd = AdapterManager.getInstance().getSecuredRegistrationAdapterManager().register(null, login, name, email, locale, timezone, null, registrationId);
        return pwd;
    }

    /**
     * Произвожит полнотекстовый поиск задачи
     *
     * @param fromObject стартовая задача, с которой начинается поиск
     * @param key        что ищем
     * @param page       страница
     * @return слайдер с задачами
     * @throws Exception при необходимости
     */
    public Slider<SecuredTaskBean> search(Object fromObject, String key, String page) throws Exception {
        SecuredTaskBean from = findTask(fromObject);
        ArrayList<SecuredSearchTaskItem> tasks = AdapterManager.getInstance().getSecuredTaskAdapterManager().fullTextSearch(from, key);
        ArrayList<SecuredTaskBean> taskList = new ArrayList<SecuredTaskBean>();
        for (SecuredSearchTaskItem t : tasks) {
            if (TaskRelatedManager.getInstance().hasPath(from.getId(), t.getTask().getId())) {
	            taskList.add(t.getTask());
            }
        }
        return new Slider<SecuredTaskBean>(taskList, 20, null, page == null ? 1 : Integer.parseInt(page));
    }

    /**
     * Разбирает входной текст для вывода в поля
     *
     * @param text входной текст
     * @return карта значений
     */
    public HashMap<String, String> parseFields(String text) {
        HashMap<String, String> fields = new HashMap<String, String>();
        if (text != null) {
            StringBuffer parsed = new StringBuffer(text.length());
            StringTokenizer tk = new StringTokenizer(text, "\r\n");
            while (tk.hasMoreTokens()) {
                String s = tk.nextToken();
                int i = s.indexOf(":");
                if (i > -1 && i < s.length()) {
                    // it's field
                    String fieldName = s.substring(0, i);
                    String fieldValue = s.substring(i + 1);
                    fields.put(fieldName, fieldValue);
                } else {
                    parsed.append(s).append("\n");
                }
            }
            fields.put("text", parsed.toString());
        }
        return fields;
    }

    public String description(String text) {
        return HTMLEncoder.text2HTML(text);
    }

    /**
     * Возвращает список приоритетов
     *
     * @param taskObject задача
     * @return список приоритетов
     * @see com.trackstudio.secured.SecuredPriorityBean
     */
    public ArrayList<SecuredPriorityBean> priorities(Object taskObject) {
        try {
            SecuredTaskBean task = findTask(taskObject);
            return AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getPriorityList(task.getSecure(), task.getWorkflowId());
        } catch (GranException e) {
            log.error("Exception ", e);
        }
        return new ArrayList<SecuredPriorityBean>();
    }

    /**
     * Возвращает список типов сообщений для задачи
     *
     * @param taskObject задача
     * @return список типов сообщений
     * @see com.trackstudio.secured.SecuredMstatusBean
     */
    public ArrayList<SecuredMstatusBean> steps(Object taskObject) {
        try {
            SecuredTaskBean task = findTask(taskObject);
            return AdapterManager.getInstance().getSecuredStepAdapterManager().getAvailableMstatusList(task.getSecure(), task.getId());
        } catch (GranException e) {
            log.error("Exception ", e);
        }
        return new ArrayList<SecuredMstatusBean>();
    }

    /**
     * Форматирует строку по указанному шаблону
     *
     * @param pattern шаблон
     * @param arg1    строка
     * @return новая строка
     */
    public String format(String pattern, String arg1) {
        return String.format(pattern, new String[]{arg1});
    }

    /**
     * Форматирует строку по указанному шаблону
     *
     * @param pattern шаблон
     * @param arg1    строка 1
     * @param arg2    строка 2
     * @return новая строка
     */
    public String format(String pattern, String arg1, String arg2) {
        return String.format(pattern, new String[]{arg1, arg2});
    }

    /**
     * Форматирует строку по указанному шаблону
     *
     * @param pattern шаблон
     * @param arg1    строка 1
     * @param arg2    строка 2
     * @param arg3    строка 3
     * @return новая строка
     */
    public String format(String pattern, String arg1, String arg2, String arg3) {
        return String.format(pattern, new String[]{arg1, arg2, arg3});
    }

    /**
     * Форматирует строку по указанному шаблону
     *
     * @param pattern шаблон
     * @param arg1    строка 1
     * @param arg2    строка 2
     * @param arg3    строка 3
     * @param arg4    строка 4
     * @return новая строка
     */
    public String format(String pattern, String arg1, String arg2, String arg3, String arg4) {
        return String.format(pattern, new String[]{arg1, arg2, arg3, arg4});
    }

    /**
     * Форматирует строку по указанному шаблону
     *
     * @param pattern шаблон
     * @param arg1    строка 1
     * @param arg2    строка 2
     * @param arg3    строка 3
     * @param arg4    строка 4
     * @param arg5    строка 5
     * @return новая строка
     */
    public String format(String pattern, String arg1, String arg2, String arg3, String arg4, String arg5) {
        return String.format(pattern, new String[]{arg1, arg2, arg3, arg4, arg5});
    }

    public SecuredTaskTriggerBean simplify(SecuredTaskBean task) throws GranException {
        return new SecuredTaskTriggerBean(task, UDFFormFillHelper.simplifyUdf(task));
    }

    /**
     * Возвращает задачу по ее номеру
     *
     * @param someTask номер задачи
     * @return задача
     * @see com.trackstudio.secured.SecuredTaskBean
     */
    public SecuredTaskBean findTask(Object someTask) {
        try {
            if (someTask != null) {
                if (someTask instanceof SecuredTaskBean)
                    return simplify((SecuredTaskBean) someTask);
                else
                    return AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskByNumber(innerSession, someTask.toString());
            }
        } catch (GranException e) {
            log.error("Exception ", e);
        }
        return null;
    }

    /**
     * Возвращает список сообщений, отсортированных в соответствии с настройками пользователя
     *
     * @param taskObject задача
     * @return список сообщений
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredMessageBean
     */
    public List<SecuredMessageBean> getSortedMessages(Object taskObject) throws GranException {
        SecuredTaskBean task = findTask(taskObject);
        if (task != null) {
            ArrayList<SecuredMessageBean> listMes = AdapterManager.getInstance().getSecuredMessageAdapterManager().getMessageList(task.getSecure(), task.getId());
            if (listMes != null) {
                if (!Preferences.isAscMessageSortOrder(innerSession.getUser().getPreferences()))
                    Collections.reverse(listMes);
            }
            return listMes;
        } else {
            return new ArrayList<SecuredMessageBean>(0);
        }
    }

    public List<SecuredMessageBean> getMessageByFilter(SecuredTaskBean task, String filterId) throws GranException {
        MessageFilter msgList = new MessageFilter(task);
        TaskFValue fv = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(task.getSecure(), filterId).getFValue();
        List<SecuredMessageBean> listMes = msgList.getMessageList(task.getSecure(), fv, false, true);
        if (!Preferences.isAscMessageSortOrder(innerSession.getUser().getPreferences()))
            Collections.reverse(listMes);
        return listMes;
    }

    /**
     * Вощвращает последнее добавленное сообщение для задачи
     *
     * @param taskObject задача
     * @return сообщение
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredMessageBean
     */
    public SecuredMessageBean getLastMessage(Object taskObject) throws GranException {
        SecuredTaskBean task = findTask(taskObject);
        ArrayList<SecuredMessageBean> securedMessageBeans = task.getMessages();
        if (securedMessageBeans != null) {
            ListIterator<SecuredMessageBean> li = securedMessageBeans.listIterator(securedMessageBeans.size());
            while (li.hasPrevious()) {
                SecuredMessageBean msg = li.previous();
                if (!CSVImport.LOG_MESSAGE.equals(msg.getMstatus().getName())) {
                    return msg;
                }
            }
        }
        return null;
    }

    public String createTask(String parentNumber, String category, String name, String description) {
        return createtask(parentNumber, category, name, description);
    }

    public String createtask(String parentNumber, String category, String name, String description) {
        try {
            String parentTaskId = CSVImport.findTaskIdByNumber(parentNumber);
            String categoryId = CSVImport.findCategoryIdByName(innerSession, category, parentTaskId);

            return TriggerManager.getInstance().createTask(innerSession, categoryId, null, HTMLEncoder.safe(name), HTMLEncoder.safe(description),
                    null, null, null,
                    parentTaskId, null, null, true, null);
        } catch (GranException ge) {
            log.error("Error", ge);
            return null;
        }
    }

    public String parseDeadline(SecuredTaskBean task) throws GranException {
        DateFormatter df = new DateFormatter(task.getSecure().getTimezone(), task.getSecure().getLocale());
        return Null.stripNullText(df.parse(task.getDeadline()));
    }

    public boolean subtask(Object taskObject, String filter) {
        tasks = new CopyOnWriteArrayList(subtasks(taskObject, filter));
        return !tasks.isEmpty();
    }

    // used in e-mail templates to get subtasks
    public List<SecuredTaskBean> currentSubtask() {
        return tasks;
    }

    public boolean validateCreateCategory(SecuredTaskBean task, String nameCategory) {
        try {
            String categoryId = CSVImport.findCategoryIdByName(nameCategory);
            SecuredCategoryBean category = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(task.getSecure(), categoryId);
            ArrayList availableCategoryList = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getAvailableCategoryList(task.getSecure(), task.getId());
            ArrayList creatableCategoryList = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getCreatableCategoryList(task.getSecure(), task.getId());
            boolean b = availableCategoryList.contains(category);
            boolean c = creatableCategoryList.contains(category);
            return b && c;
        } catch (GranException ge) {
            return false;
        }
    }
}