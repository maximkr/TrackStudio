package com.trackstudio.app.report.handmade;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.adapter.HandMadeReport;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.FilterSettings;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.filter.list.MessageFilter;
import com.trackstudio.app.filter.list.TaskFilter;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredAttachmentBean;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredMessageAttachmentBean;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredTaskAttachmentBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;

import net.jcip.annotations.NotThreadSafe;

/**
 * Класс для экспорта данных в фортам RSS
 */
@NotThreadSafe
public class RSSHandMadeReport implements HandMadeReport {

    private static Log log = LogFactory.getLog(RSSHandMadeReport.class);

    /**
     * Инициализирует класс
     *
     * @return TRUE - если успешно, FALSE - если нет
     */
    public boolean init() {
        return true;
    }

    /**
     * Возвращает описание класса
     *
     * @return описание
     */
    public String getDescription() {
        return "RSS Export Adapter";
    }

    /**
     * Генерирует данные для RSS
     *
     * @param sc       сессия пользователя
     * @param taskId   ID Задачи
     * @param filterId ID фильтра
     * @param filter   фильтр
     * @param encoding кодировка выходных данных
     * @return сгенерированный текст
     * @throws GranException при необходимости
     */
    public String generateImpl(SessionContext sc, String taskId, String filterId, TaskFValue filter, String encoding) throws GranException {
        try {
            FilterSettings flthm = new FilterSettings(filter, taskId, filterId);
            SecuredTaskBean tci = new SecuredTaskBean(taskId, sc);
            TreeMap<Calendar, Object> data = getRSSData(sc, tci, flthm);
            DOMImplementationImpl domImpl = new DOMImplementationImpl();
            Document xmlDoc = domImpl.createDocument(null, "rss", null);
            Element root = xmlDoc.getDocumentElement();
            root.setAttribute("version", "2.0");
            Element channel = xmlDoc.createElement("channel");
            root.appendChild(channel);

            String rssLink = Config.getInstance().getSiteURL() + "/task/" + tci.getNumber() + "/rss/" + filterId + "?autologin=" + sc.getUser().getLogin() + "&autopassword=PASSWORD";
            SecuredFilterBean f = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
            addGeneralInfo(xmlDoc, channel, f.getName(), rssLink);
            TaskFValue fv = (TaskFValue) flthm.getSettings();
            boolean withsub = fv.get(FValue.SUBTASK) != null;
            for (Calendar calendar : data.keySet()) {
                Object o = data.get(calendar);
                if (o instanceof SecuredTaskBean) {
                    channel.appendChild(buildTaskItem(xmlDoc, sc, (SecuredTaskBean) o, withsub));
                } else if (o instanceof SecuredMessageBean) {
                    channel.appendChild(buildMessageItem(xmlDoc, sc, (SecuredMessageBean) o, withsub));
                } else if (o instanceof SecuredTaskAttachmentBean) {
                    channel.appendChild(buildAttachmentItem(xmlDoc, sc, (SecuredTaskAttachmentBean) o, withsub));
                }
            }
            StringWriter sw = new StringWriter();
            try {
                XMLSerializer serializer = new XMLSerializer(sw, new OutputFormat(xmlDoc, Config.getEncoding(), true));
                serializer.serialize(xmlDoc);
            } catch (IOException ee) {
                log.error("Error",ee);
            } finally {
                sw.flush();
            }
            return sw.toString();
        } catch (Exception e) {
            throw new GranException(e);
        }

    }

    public String generateImpl(SessionContext sc, String taskId, String filterId, TaskFValue filter, String encoding, String linkXml) throws GranException {
        return generateImpl(sc, taskId, filterId, filter, encoding);
    }

    private final static int MAX_TASK = 50;
    private TreeMap<Calendar, Object> getRSSData(SessionContext sc, SecuredTaskBean tci, FilterSettings flthm) throws GranException {
        TaskFilter taskList = new TaskFilter(tci);
        ArrayList<SecuredUDFValueBean> ulist = tci.getFilterUDFValues();
        boolean taskUDFView = flthm.getSettings().needFilterUDF() && !ulist.isEmpty();
        TaskFValue fv = (TaskFValue) flthm.getSettings();
        boolean notwithsub = fv.get(FValue.SUBTASK) == null;
        List<SecuredTaskBean> list = taskList.getTaskList(fv, taskUDFView, notwithsub, flthm.getSortedBy());
        if (list.size() > MAX_TASK) {
            list = list.subList(0, MAX_TASK);
        }
        TreeMap<Calendar, Object> map = new TreeMap<Calendar, Object>(new CalendarComparator());
        for (SecuredTaskBean task : list) {
            map.put(task.getSubmitdate(), task);
            Set<String> msgsAttsIds = new HashSet<String>();

            MessageFilter ml = new MessageFilter(task);
            List<SecuredMessageBean> listMes = ml.getMessageList(sc, fv, true, true);

            for (SecuredMessageBean msg : listMes) {
                map.put(msg.getTime(), msg);
                List<SecuredMessageAttachmentBean> atts = msg.getAttachments();
                if (atts != null) {
                    for (SecuredMessageAttachmentBean att : atts) {
                        msgsAttsIds.add(att.getId());
                    }
                }
            }
            ArrayList<SecuredTaskAttachmentBean> atts = task.getAttachments();
            if (atts != null) {
                for (SecuredAttachmentBean att : atts) {
                    if (!msgsAttsIds.contains(att.getId())) {
                        if (!att.getDeleted()) {
                            map.put(att.getLastModified(), att);
                        }
                    }
                }
            }
        }
        return map;
    }

    private class CalendarComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            if (o1 == null || o2 == null)
                return 0;
            try {
                return ((Calendar) o2).compareTo(((Calendar) o1));
            } catch (Exception e) {
                return 0;
            }
        }
    }

    private Element buildTaskItem(Document xmlDoc, SessionContext sc, SecuredTaskBean task, boolean withsub) throws GranException {
        Element item = xmlDoc.createElement("item");
        if (withsub)
            item.appendChild(appendCDATAChild(xmlDoc, "title", task.getProjectAlias() + " | T - " + task.getName() + " [#" + task.getNumber() + "]"));
        else
            item.appendChild(appendCDATAChild(xmlDoc, "title", "T - " + task.getName() + " [#" + task.getNumber() + "]"));
        item.appendChild(appendTextChild(xmlDoc, "link", Config.getInstance().getSiteURL() + "/task/" + task.getNumber() + "?thisframe=true"));
        item.appendChild(appendTextChild(xmlDoc, "pubDate", new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH).format(new Timestamp(task.getSubmitdate().getTimeInMillis()))));

        StringBuffer description = new StringBuffer();
        description.append(I18n.getString(sc.getLocale(), "RSS_ADD_TASK", new Object[]{"<b>" + task.getSubmitter().getName() + "</b>", "<b>" + task.getCategory().getName() + "</b>"}));
        if (task.getDescription() != null && task.getDescription().trim().length() != 0) {
            description.append("<br>\n");
            description.append(task.getDescription());
        }
        String desc = HandMadeReportManager.stripNonValidXMLCharacters(description.toString());
        item.appendChild(appendCDATAChild(xmlDoc, "description", desc));
        return item;
    }

    private Element buildMessageItem(Document xmlDoc, SessionContext sc, SecuredMessageBean msg, boolean withsub) throws GranException {
        SecuredTaskBean task = msg.getTask();
        Element item = xmlDoc.createElement("item");
        if (withsub)
            item.appendChild(appendCDATAChild(xmlDoc, "title", task.getProjectAlias() + " | M - " + task.getName() + " [#" + task.getNumber() + "]"));
        else
            item.appendChild(appendCDATAChild(xmlDoc, "title", "M - " + task.getName() + " [#" + task.getNumber() + "]"));
        item.appendChild(appendTextChild(xmlDoc, "link", Config.getInstance().getSiteURL() + "/task/" + task.getNumber() + "?thisframe=true"));
        item.appendChild(appendTextChild(xmlDoc, "pubDate", new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH).format(new Timestamp(msg.getTime().getTimeInMillis()))));

        StringBuffer description = new StringBuffer();

        if (msg.getHandlerUserId() != null || msg.getHandlerGroupId() != null) {
            description.append(I18n.getString(sc.getLocale(), "RSS_ADD_MESSAGE_FOR", new Object[]{"<b>" + msg.getSubmitter().getName() + "</b>", "<b>" + msg.getMstatus().getName() + (msg.getResolution() != null ? " (" + msg.getResolution().getName() + ")" : "") + "</b>", "<b>" + (msg.getHandlerUserId() != null ? msg.getHandlerUser().getName() : msg.getHandlerGroup().getName()) + "</b>"}));
        } else {
            description.append(I18n.getString(sc.getLocale(), "RSS_ADD_MESSAGE", new Object[]{"<b>" + msg.getSubmitter().getName() + "</b>", "<b>" + msg.getMstatus().getName() + (msg.getResolution() != null ? " (" + msg.getResolution().getName() + ")" : "") + "</b>"}));
        }

        if (msg.getDescription() != null && msg.getDescription().trim().length() != 0) {
            description.append("<br>\n");
            description.append(HandMadeReportManager.stripNonValidXMLCharacters(msg.getDescription()));
        }
        ArrayList<SecuredMessageAttachmentBean> atts = new ArrayList<SecuredMessageAttachmentBean>();

        if (sc.canAction(Action.createTaskMessageAttachments, task.getId())) {
            for (SecuredMessageAttachmentBean att : msg.getAttachments()) {
                if (!att.getDeleted()) {
                    atts.add(att);
                }
            }
            if (atts.size() > 0) {
                description.append("<br>\n");
                for (SecuredMessageAttachmentBean att : atts) {
                    String size = att.getSize() < 1024 ? ">&lt;1 kB" : String.valueOf((int) (att.getSize() / 1024)) + " kB";
                    description.append("<a href=\"").append(Config.getInstance().getSiteURL()).append("/download/task/").append(att.getTask().getNumber()).append("/").append(att.getId()).append("\">").append(att.getName()).append(" (").append(size).append(")</a><br>");
                }
            }
        }
        String desc = HandMadeReportManager.stripNonValidXMLCharacters(description.toString());
        item.appendChild(appendCDATAChild(xmlDoc, "description", desc));
        return item;
    }

    private Element buildAttachmentItem(Document xmlDoc, SessionContext sc, SecuredTaskAttachmentBean att, boolean withsub) throws GranException {
        SecuredTaskBean task = att.getTask();
        Element item = xmlDoc.createElement("item");
        if (withsub)
            item.appendChild(appendCDATAChild(xmlDoc, "title", task.getProjectAlias() + " | F - " + att.getName() + " | " + task.getName() + " [#" + task.getNumber() + "]"));
        else
            item.appendChild(appendCDATAChild(xmlDoc, "title", "F - " + att.getName() + " | " + task.getName() + " [#" + task.getNumber() + "]"));
        item.appendChild(appendTextChild(xmlDoc, "link", Config.getInstance().getSiteURL() + "/task/" + task.getNumber() + "?thisframe=true"));
        item.appendChild(appendTextChild(xmlDoc, "pubDate", new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH).format(new Timestamp(att.getLastModified().getTimeInMillis()))));

        StringBuffer description = new StringBuffer();
        description.append(I18n.getString(sc.getLocale(), "RSS_ADD_ATTACHMENT", new Object[]{"<b>" + (att.getUser() != null ? att.getUser().getName() : new SecuredUserBean("1", sc).getName()) + "</b>"}));
        description.append("<br>");
        String size = att.getSize() < 1024 ? ">&lt;1 kB" : String.valueOf((int) (att.getSize() / 1024)) + " kB";
        description.append("<a href=\"").append(Config.getInstance().getSiteURL()).append("/download/task/").append(att.getTask().getNumber()).append("/").append(att.getId()).append("\">").append(att.getName()).append(" (").append(size).append(")</a>");

        String desc = HandMadeReportManager.stripNonValidXMLCharacters(description.toString());
        item.appendChild(appendCDATAChild(xmlDoc, "description", desc));
        return item;
    }

    private void addGeneralInfo(Document xmlDoc, Element channel, String name, String rssLink) {
        channel.appendChild(appendTextChild(xmlDoc, "title", name));
        channel.appendChild(appendTextChild(xmlDoc, "link", "http://www.trackstudio.com"));
        channel.appendChild(appendTextChild(xmlDoc, "description", rssLink));
        Element image = xmlDoc.createElement("image");
        image.appendChild(appendTextChild(xmlDoc, "url", Config.getInstance().getSiteURL()+"/ImageServlet/TrackStudio/favicon.png"));
        channel.appendChild(image);

    }

    private Element appendTextChild(Document xmlDoc, String name, String value) {
        Element ret = xmlDoc.createElement(name);
        ret.appendChild(xmlDoc.createTextNode(value));
        return ret;
    }

    private Element appendCDATAChild(Document xmlDoc, String name, String value) {
        Element ret = xmlDoc.createElement(name);
        ret.appendChild(xmlDoc.createCDATASection(value));
        return ret;
    }

    /**
     * ГЕнерирует данные для RSS Discovery
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи
     * @return данные
     * @throws GranException при необходимости
     */
    public String generateDiscovery(SessionContext sc, String taskId) throws GranException {
        List<SecuredFilterBean> filters = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFilterList(sc, taskId);
        SecuredTaskBean task = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskById(sc, taskId);
        StringBuffer sb = new StringBuffer();

        sb.append("<html>\n<head>\n<title>RSS Autodiscovery for ").append(task.getName()).append("[#").append(task.getNumber()).append("]").append("</title>\n");
        for (SecuredFilterBean filter : filters) {
            String rssLink = Config.getInstance().getSiteURL() + "/task/" + task.getNumber() + "/rss/" + filter.getId();
            sb.append("<link rel=\"alternate\" type=\"application/rss+xml\" title=\"").append(filter.getName()).append("\" href=\"").append(rssLink).append("\">\n");
        }

        sb.append("</head>\n<body>\n<h2>").append(I18n.getString(sc.getLocale(), "RSS_ACTIVE_CHANNELS", new String[]{task.getName() + " [#" + task.getNumber() + "]"})).append("</h2>\n");
        for (SecuredFilterBean filter : filters) {
            String rssLink = Config.getInstance().getSiteURL() + "/task/" + task.getNumber() + "/rss/" + filter.getId();
            sb.append("<a href=\"").append(rssLink).append("\">").append(filter.getName()).append("</a><br>\n");
        }
        sb.append("</body>\n</html>");
        return sb.toString();
    }
}