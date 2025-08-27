package com.trackstudio.tools;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.StringTokenizer;

import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trackstudio.constants.CategoryConstants;
import com.trackstudio.constants.ScriptConstants;
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.constants.WorkflowConstants;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.formatter.DateFormatter;

import net.jcip.annotations.Immutable;

import static com.trackstudio.tools.Null.isNotNull;

/**
 * Класс для валидации параметров
 */
@Immutable
public class ParameterValidator {

    private static final Logger log = LoggerFactory.getLogger(ParameterValidator.class);

    /**
     * Проверяет корректность описания
     *
     * @param in входная строка
     * @return TRUE - некорректное, FALSE - корректное
     */
    public boolean badDesc(String in) {
        return in != null && in.length() > 2000;
    }

    /**
     * Проверяет корректность заголовка
     *
     * @param in входная строка
     * @return TRUE - некорректное, FALSE - корректное
     */
    public static boolean badSmallDesc(String in) {
        return in != null && in.length() > Integer.valueOf(Config.getProperty("trackstudio.default.limit", "200"));
    }

    /**
     * This method cuts a name
     * @param in input name
     * @param defaultName default name
     * @return valid name
     */
    public static String cutToValidName(String in, String defaultName) {
        String result = defaultName;
        if (isNotNull(in)) {
            if (badTaskName(in)) {
                int limit = Integer.valueOf(Config.getProperty("trackstudio.default.limit", "200"));
                if (limit < in.length()) {
                    result = in.substring(0, limit);
                }
            }
        }
        return result;
    }

    /**
     * Проверяет корректность ключа
     *
     * @param in ключ
     * @return TRUE - некорректное, FALSE - корректное
     */
    public boolean badKey(String in) {
        return in != null && in.length() > 32;
    }

    /**
     * Проверяет корректность названия задачи
     *
     * @param name название задачи
     * @return TRUE - некорректное, FALSE - корректное
     */
    public static boolean badTaskName(String name) {
        return name == null || name.trim().length() == 0 || badSmallDesc(name);
    }

    /**
     * Проверяет корректность электронной почты
     *
     * @param email адрес почты
     * @return TRUE - некорректное, FALSE - корректное
     */
    public boolean badEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        if (badSmallDesc(email))
            return true;
        try {
            StringTokenizer tk = new StringTokenizer(email, com.trackstudio.model.User.delimiter);
            while (tk.hasMoreElements()) {
                String token = tk.nextToken();
                new InternetAddress(token);
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Проверяет корректность карты
     *
     * @param map входная карта
     * @return TRUE - некорректное, FALSE - корректное
     */
    public boolean badFValueField(Map map) {
        if (map == null)
            return false;
        for (Object key : map.keySet()) {
            log.debug("key=" + key);
            if (key.toString().length() > 200)
                return true;
            Object o = map.get(key);
            if (o != null && o.toString().length() > 2000)
                return true;
        }
        return false;
    }

    /**
     * Проверяет корректность значения пользовательского поля
     *
     * @param udfId  ID поля
     * @param value  Значение
     * @param locale Локаль
     * @param tz     ТАймзона
     * @return TRUE - некорректное, FALSE - корректное
     */
    public boolean badUdfValue(String udfId, String value, String locale, String tz) {
        log.trace("badUdfValue(udfId='" + udfId + "', value='" + value + "', locale='" + locale + "', tz='" + tz + "')");
        try {
            if (value == null || value.trim().length() == 0)
                return false;
            int type = KernelManager.getFind().findUdf(udfId).getType();

            switch (type) {
                case UdfConstants.DATE:
                    new DateFormatter(tz, locale).parseToCalendar(value);
                    break;
                case UdfConstants.FLOAT:
                    new Double(NumberFormat.getNumberInstance(DateFormatter.toLocale(locale)).parse(value).doubleValue());
                    break;
                case UdfConstants.INTEGER:
                    new Integer(value);
                    break;
                case UdfConstants.LIST:
                    if (KernelManager.getFind().findUdflist(value) == null)
                        return true;
                    break;
                case UdfConstants.MLIST: {
                    StringTokenizer tk = new StringTokenizer(value, "\n");
                    while (tk.hasMoreTokens()) {
                        if (KernelManager.getFind().findUdflist(tk.nextToken()) == null)
                            return true;
                    }
                }
                break;
                case UdfConstants.USER: {
                    StringTokenizer tk = new StringTokenizer(value, ";");
                    while (tk.hasMoreTokens()) {
                        if (!UserRelatedManager.getInstance().isUserExists(tk.nextToken()))
                            return true;
                    }
                }
                break;
                case UdfConstants.TASK: {
                    StringTokenizer tk = new StringTokenizer(value, ";");
                    while (tk.hasMoreTokens()) {
                        if (!TaskRelatedManager.getInstance().isTaskExists(tk.nextToken()))
                            return true;
                    }
                }
                break;
                case UdfConstants.MEMO:
                    break;
                case UdfConstants.STRING:
                    if (badDesc(value))
                        return true;
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    /**
     * Проверяет корректность локали
     *
     * @param locale локаль
     * @return TRUE - некорректное, FALSE - корректное
     */
    public boolean badLocale(String locale) {
        for (Pair pair : Config.getInstance().getAvailableLocales(Locale.ENGLISH))
            if (pair.getKey().equals(locale))
                return false;
        return true;
    }

    /**
     * Проверяет корректность таймзоны
     *
     * @param tz таймзона
     * @return TRUE - некорректное, FALSE - корректное
     */
    public static boolean badTimeZone(String tz) {
        String[] available = SimpleTimeZone.getAvailableIDs();
        for (String anAvailable : available) {
            if (anAvailable.equals(tz)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Проверяет корректность типа роли для категории
     *
     * @param in тип роли для категории
     * @return TRUE - некорректное, FALSE - корректное
     */
    public boolean badCategoryRoleType(String in) {
        return !(in.equals(CategoryConstants.NONE) ||
                in.equals(CategoryConstants.CREATE_ALL) ||
                in.equals(CategoryConstants.CREATE_HANDLER) ||
                in.equals(CategoryConstants.CREATE_SUBMITTER) ||
                in.equals(CategoryConstants.CREATE_SUBMITTER_AND_HANDLER) ||
                in.equals(CategoryConstants.DELETE_ALL) ||
                in.equals(CategoryConstants.DELETE_HANDLER) ||
                in.equals(CategoryConstants.DELETE_SUBMITTER) ||
                in.equals(CategoryConstants.DELETE_SUBMITTER_AND_HANDLER) ||
                in.equals(CategoryConstants.EDIT_ALL) ||
                in.equals(CategoryConstants.EDIT_HANDLER) ||
                in.equals(CategoryConstants.EDIT_SUBMITTER) ||
                in.equals(CategoryConstants.EDIT_SUBMITTER_AND_HANDLER) ||
                in.equals(CategoryConstants.BE_HANDLER_ALL) ||
                in.equals(CategoryConstants.BE_HANDLER_HANDLER) ||
                in.equals(CategoryConstants.BE_HANDLER_SUBMITTER) ||
                in.equals(CategoryConstants.BE_HANDLER_SUBMITTER_AND_HANDLER) ||
                in.equals(CategoryConstants.VIEW_ALL) ||
                in.equals(CategoryConstants.VIEW_SUBMITTER)
        );
    }

    /**
     * Проверяет корректность типа правила дотупа к полю
     *
     * @param in тип правила дотупа к полю
     * @return TRUE - некорректное, FALSE - корректное
     */
    public boolean badUdfRuleType(String in) {
        return !(in.equals(UdfConstants.VIEW_ALL) ||
                in.equals(UdfConstants.VIEW_HANDLER) ||
                in.equals(UdfConstants.VIEW_SUBMITTER) ||
                in.equals(UdfConstants.VIEW_SUBMITTER_AND_HANDLER) ||
                in.equals(UdfConstants.EDIT_ALL) ||
                in.equals(UdfConstants.EDIT_HANDLER) ||
                in.equals(UdfConstants.EDIT_SUBMITTER) ||
                in.equals(UdfConstants.EDIT_SUBMITTER_AND_HANDLER)
        );
    }

    /**
     * Проверяет корректность типа скрипта
     *
     * @param in тип скрипта
     * @return TRUE - некорректное, FALSE - корректное
     */
    public boolean badScriptType(String in) {
        return !(in.equals(ScriptConstants.TASK_CUSTOM_FIELD_VALUE) ||
                in.equals(ScriptConstants.USER_CUSTOM_FIELD_VALUE) ||
                in.equals(ScriptConstants.TASK_CUSTOM_FIELD_LOCKUP) ||
                in.equals(ScriptConstants.USER_CUSTOM_FIELD_LOCKUP) ||
                in.equals(ScriptConstants.MSTATUS_AFTER_TRIGGER) ||
                in.equals(ScriptConstants.MSTATUS_BEFORE_TRIGGER) ||
                in.equals(ScriptConstants.MSTATUS_INSTEAD_OF_TRIGGER) ||
                in.equals(ScriptConstants.CATEGORY_CREATE_AFTER_TRIGGER) ||
                in.equals(ScriptConstants.CATEGORY_CREATE_BEFORE_TRIGGER) ||
                in.equals(ScriptConstants.CATEGORY_CREATE_INSTEAD_OF_TRIGGER) ||
                in.equals(ScriptConstants.CATEGORY_UPDATE_AFTER_TRIGGER) ||
                in.equals(ScriptConstants.CATEGORY_UPDATE_BEFORE_TRIGGER) ||
                in.equals(ScriptConstants.CSV_IMPORT) ||
                in.equals(ScriptConstants.CATEGORY_UPDATE_INSTEAD_OF_TRIGGER));
    }

    /**
     * Проверяет корректность типа права доступа к сообщениям
     *
     * @param in тип права доступа
     * @return TRUE - некорректное, FALSE - корректное
     */
    public boolean badMprstatusType(String in) {
        return !(in.equals(WorkflowConstants.NONE) ||
                in.equals(WorkflowConstants.PROCESS_ALL) ||
                in.equals(WorkflowConstants.PROCESS_HANDLER) ||
                in.equals(WorkflowConstants.PROCESS_SUBMITTER) ||
                in.equals(WorkflowConstants.PROCESS_SUBMITTER_AND_HANDLER) ||
                in.equals(WorkflowConstants.BE_HANDLER_ALL) ||
                in.equals(WorkflowConstants.BE_HANDLER_HANDLER) ||
                in.equals(WorkflowConstants.BE_HANDLER_SUBMITTER) ||
                in.equals(WorkflowConstants.BE_HANDLER_SUBMITTER_AND_HANDLER) ||
                in.equals(WorkflowConstants.VIEW_ALL) ||
                in.equals(WorkflowConstants.VIEW_HANDLER) ||
                in.equals(WorkflowConstants.VIEW_SUBMITTER) ||
                in.equals(WorkflowConstants.VIEW_SUBMITTER_AND_HANDLER));
    }

    /**
     * Проверяет корректность формата бюджета
     *
     * @param in формат бюджета
     * @return TRUE - некорректное, FALSE - корректное
     */
    public boolean badBudgetFormat(String in) {
        return in.matches("Y*M*W*D*h*m*s*");
    }
}