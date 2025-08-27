package com.trackstudio.app.report.handmade;

import com.trackstudio.app.adapter.HandMadeReport;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.AccessDeniedException;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.InvalidParameterException;
import com.trackstudio.securedkernel.SecuredReportAdapterManager;
import com.trackstudio.startup.Config;

import net.jcip.annotations.NotThreadSafe;

/**
 * Основной класс для генерации отчетов
 */
@NotThreadSafe
public class HandMadeReportManager {
    /**
     * Конструктор
     */
    public HandMadeReportManager() {
    }

    /**
     * Экспортирует данные в форматы CSV, XML, Klip, TreeXML, MS Project, RSS и т.п.
     *
     * @param sc        сессия пользователя
     * @param taskId    ID задачи
     * @param filterId  ID фильтра
     * @param filter    фильтр
     * @param format    формат вывода
     * @param delimiter разделитель
     * @param encoding  кодировка
     * @return сгенерированные данные
     * @throws GranException при необходимости
     */
    public String generate(SessionContext sc, String taskId, String filterId, TaskFValue filter, String format, String delimiter, String encoding) throws GranException {
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "generate", "sc", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "generate", "taskId", sc);
        if (encoding == null || encoding.length() == 0)
            encoding = Config.getEncoding();
        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "generate", sc, "!sc.taskOnSight(taskId)", taskId);
        HandMadeReport rep;
        if (format.equalsIgnoreCase(SecuredReportAdapterManager.RT_CSV))
            rep = new CSVHandMadeReport(delimiter);
        else if (format.equalsIgnoreCase(SecuredReportAdapterManager.RT_TREE_XML))
            rep = new TreeXMLHandMadeReport();
        else if (format.equalsIgnoreCase(SecuredReportAdapterManager.RT_RSS))
            rep = new RSSHandMadeReport();
        else
            throw new InvalidParameterException(this.getClass(), "generate", "format", sc);
        return rep.generateImpl(sc, taskId, filterId, filter, encoding);
    }

/**
 * replace invalid XML characters by regexp: http://benjchristensen.com/2008/02/07/how-to-strip-invalid-xml-characters/
 * @param in
 * @return
 */
    public static String stripNonValidXMLCharacters(String in) {
        String out = "";
        if (in == null || ("".equals(in))) {
            return "";
        }
        in = in
//                .replaceAll("&nbsp;", " ")
                .replaceAll(String.valueOf((char) 0xc2) + String.valueOf((char) 0xb7), String.valueOf((char) 0xb7));
        String invalidXmlPattern = "[^"
        		+ "\\u0009\\u000A\\u000D"
        		+ "\\u0020-\\uD7FF"
        		+ "\\uE000-\\uFFFD"
        		+ "\\u10000-\\u10FFFF"
        		+ "]+";
        		out = in.replaceAll(invalidXmlPattern, " ");
        return out;
    }
}