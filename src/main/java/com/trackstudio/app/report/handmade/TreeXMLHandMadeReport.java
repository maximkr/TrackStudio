package com.trackstudio.app.report.handmade;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.adapter.HandMadeReport;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.AbstractPluginCacheItem;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.PluginCacheItem;
import com.trackstudio.kernel.cache.PluginCacheManager;
import com.trackstudio.kernel.cache.PluginType;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.formatter.DateFormatter;
import com.trackstudio.tools.formatter.HourFormatter;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.NotThreadSafe;

/**
 * Класс для экспорта данных в фортам Tree XML
 */
@NotThreadSafe
public class TreeXMLHandMadeReport implements HandMadeReport {

    private static Log log = LogFactory.getLog(TreeXMLHandMadeReport.class);
    private Document xmlDoc = null;
    private Element root = null;

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
        return "TreeXML Export Adapter";
    }

    /**
     * Возвращает отфильтрованный список подзадач
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи
     * @throws GranException при необходимости
     */
    private void buildTree(SessionContext sc, String taskId, Element rootNode) throws GranException {
        SecuredTaskBean parent = new SecuredTaskBean(taskId, sc);
        if (!parent.getChildren().isEmpty()) {
            for (SecuredTaskBean stb : parent.getChildren()) {
                Element element = this.exportTask(sc, stb.getId(), rootNode);
                this.buildTree(sc, stb.getId(), element);
            }
        }
    }

    /**
     * Генерирует данные для XML
     *
     * @param sc       сессия пользователя
     * @param taskId   ID Задачи
     * @param filterId ID фильтра
     * @param filter   фильтр
     * @param encoding кодировка выходных данных
     * @return сгенерированный текст
     * @throws GranException при необходимости
     */
    public String generateImpl(SessionContext sc, String taskId, String filterId, TaskFValue filter, String encoding, String linkXsl) throws GranException {
        try {
            DOMImplementationImpl domImpl = new DOMImplementationImpl();
            xmlDoc = domImpl.createDocument(null, "trackstudio-task", null);
            root = xmlDoc.getDocumentElement();
            this.buildTree(sc, taskId, root);
            StringWriter sw = new StringWriter();
            try {
                XMLSerializer serializer = new XMLSerializer(sw, new OutputFormat(xmlDoc, encoding, true));
                serializer.serialize(xmlDoc);
            } catch (IOException ee) {
                log.error("Error",ee);
            } finally {
                sw.flush();
            }
            byte[] bytes = sw.toString().getBytes(encoding);
            AbstractPluginCacheItem xslt = PluginCacheManager.getInstance().find(PluginType.XSLT, linkXsl);
            String result = new String(bytes, encoding);
            if (xslt != null) {
                result = UtilReport.formatXsl(result,((PluginCacheItem) xslt).getText());
            }
            return result;
        } catch (Exception e) {
            throw new GranException(e);
        }
    }

    public String generateImpl(SessionContext sc, String taskId, String filterId, TaskFValue filter, String encoding) throws GranException {
        return generateImpl(sc, taskId, filterId, filter, encoding, null);
    }


    private Element exportTask(SessionContext sc, String taskid, Element parent) throws GranException {
        DateFormatter df = sc.getUser().getDateFormatter();
        SecuredTaskBean stb = new SecuredTaskBean(taskid, sc);

        // Find parent
        Element task = xmlDoc.createElement("task");
        task.setAttribute("url", Config.getInstance().getSiteURL() + "/task/" + stb.getNumber());
        if (stb.getId() != null) {
            Element data = xmlDoc.createElement("id");
            data.appendChild(xmlDoc.createTextNode(stb.getId()));
            task.appendChild(data);
        }

        if (stb.getParent() != null) {
            Element data = xmlDoc.createElement("parent");
            data.appendChild(xmlDoc.createTextNode(HandMadeReportManager.stripNonValidXMLCharacters(stb.getParent().getName())));
            task.appendChild(data);
        }

        if (stb.getShortname() != null) {
            Element data = xmlDoc.createElement("shortname");
            data.appendChild(xmlDoc.createTextNode(HandMadeReportManager.stripNonValidXMLCharacters(stb.getShortname())));
            task.appendChild(data);
        }

        {
            Element data = xmlDoc.createElement("name");
            data.appendChild(xmlDoc.createTextNode(HandMadeReportManager.stripNonValidXMLCharacters(stb.getName())));
            task.appendChild(data);
        }

        if (stb.getBudget() != null && stb.getBudget().floatValue() > 0.00001) {
            Element data = xmlDoc.createElement("budget");
            String formatted = "";
            if (stb.getBudget() != null) {
                log.debug("BUDGET");
                HourFormatter hf = new HourFormatter(stb.getBudget(), stb.getBudgetFormat(), sc.getLocale());
                formatted = hf.getString();

                /*HourFormatter hf =   new HourFormatter(stb.getBudget(), stb.getBudgetFormat());
     formatted = I18n.getString(sc.getLocale(), "MSG_BUDGET_FORMAT", new Object[]{hf.getYears(), hf.getMonths(), hf.getWeeks(), hf.getDays(), hf.getHours(), hf.getMinutes(), hf.getSeconds()});*/
            }
            data.appendChild(xmlDoc.createTextNode(formatted));
            task.appendChild(data);
        }

        if (stb.getActualBudget() != null && stb.getActualBudget().floatValue() > 0.00001) {
            Element data = xmlDoc.createElement("abudget");
            String s = "";
            if (stb.getActualBudget() != null) {
                log.debug("ABUDGET");
                HourFormatter hf = new HourFormatter(stb.getActualBudget(), stb.getBudgetFormat(), sc.getLocale());
                s = hf.getString();

            }
            data.appendChild(xmlDoc.createTextNode(s));
            task.appendChild(data);
        }

        if (stb.getSubmitdate() != null) {
            Element data = xmlDoc.createElement("submitdate");
            data.appendChild(xmlDoc.createTextNode(df.parse(stb.getSubmitdate())));
            task.appendChild(data);
        }

        if (stb.getUpdatedate() != null) {
            Element data = xmlDoc.createElement("updatedate");
            data.appendChild(xmlDoc.createTextNode(df.parse(stb.getUpdatedate())));
            task.appendChild(data);
        }

        if (stb.getClosedate() != null) {
            Element data = xmlDoc.createElement("closedate");
            data.appendChild(xmlDoc.createTextNode(df.parse(stb.getClosedate())));
            task.appendChild(data);
        }

        if (stb.getDeadline() != null) {
            Element data = xmlDoc.createElement("deadline");
            data.appendChild(xmlDoc.createTextNode(df.parse(stb.getDeadline())));
            task.appendChild(data);
        }


        if (stb.getPriority() != null) {
            Element data = xmlDoc.createElement("priority");
            data.appendChild(xmlDoc.createTextNode(HandMadeReportManager.stripNonValidXMLCharacters(stb.getPriority().getName())));
            task.appendChild(data);
        }

        if (stb.getCategory() != null) {
            Element data = xmlDoc.createElement("category");
            data.appendChild(xmlDoc.createTextNode(HandMadeReportManager.stripNonValidXMLCharacters(stb.getCategory().getName())));
            task.appendChild(data);
        }

        if (stb.getStatus() != null) {
            Element data = xmlDoc.createElement("status");
            data.appendChild(xmlDoc.createTextNode(HandMadeReportManager.stripNonValidXMLCharacters(stb.getStatus().getName())));
            task.appendChild(data);
        }

        if (stb.getResolution() != null) {
            Element data = xmlDoc.createElement("resolution");
            data.appendChild(xmlDoc.createTextNode(HandMadeReportManager.stripNonValidXMLCharacters(stb.getResolution().getName())));
            task.appendChild(data);
        }

        if (stb.getSubmitter() != null) {
            Element data = xmlDoc.createElement("submitter");
            data.appendChild(xmlDoc.createTextNode(HandMadeReportManager.stripNonValidXMLCharacters(stb.getSubmitter().getName())));
            task.appendChild(data);
        }

        if (stb.getHandler() != null) {
            Element data = xmlDoc.createElement("handler");
            data.appendChild(xmlDoc.createTextNode(HandMadeReportManager.stripNonValidXMLCharacters(stb.getHandler().getName())));
            task.appendChild(data);
        }

        if (stb.getDescription() != null) {
            Element data = xmlDoc.createElement("description");
            String desc = stb.getDescription();
            if (desc.indexOf("]]>") != -1) {
                HTMLEncoder sb = new HTMLEncoder(desc);
                sb.replace("]]>", "]] >");
                desc = sb.toString();
            }
            data.appendChild(xmlDoc.createTextNode(HandMadeReportManager.stripNonValidXMLCharacters(desc)));
            task.appendChild(data);
        }

        if (stb.getNumber() != null) {
            Element data = xmlDoc.createElement("number");
            data.appendChild(xmlDoc.createTextNode(stb.getNumber()));
            task.appendChild(data);
        }

        if (!stb.getUDFValues().isEmpty()) {
            Element udfs = xmlDoc.createElement("udfs");
            ArrayList<SecuredUDFValueBean> udfList = stb.getUDFValuesList();
            for (SecuredUDFValueBean udf : udfList) {
                int type = udf.getUdfType();
                Element eudf = xmlDoc.createElement("udf");
                eudf.setAttribute("type", getType(type));
                eudf.setAttribute("name", HandMadeReportManager.stripNonValidXMLCharacters(udf.getCaption()));
                Object value = udf.getValue();
                if (value != null) {

                    if (type == UdfConstants.MLIST) {
                        Element el = xmlDoc.createElement("element");
                        List<Pair> pairs = (List<Pair>) value;

                        for (Pair p : pairs) {
                            el.appendChild(xmlDoc.createTextNode(p.getValue()));
                        }

                        eudf.appendChild(el);
                    } else if (type == UdfConstants.TASK) {
                        Element el = xmlDoc.createElement("element");
                        List<SecuredTaskBean> tasks = (List<SecuredTaskBean>) value;
                        StringBuffer val = new StringBuffer();
                        for (SecuredTaskBean p : tasks) {
                            el.appendChild(xmlDoc.createTextNode("#" + p.getNumber()));
                        }

                        eudf.appendChild(el);
                    } else if (type == UdfConstants.USER) {
                        Element el = xmlDoc.createElement("element");
                        List<SecuredUserBean> users = (List<SecuredUserBean>) value;
                        StringBuffer val = new StringBuffer();
                        for (SecuredUserBean p : users) {
                            el.appendChild(xmlDoc.createTextNode(p.getName()));
                        }

                        eudf.appendChild(el);
                    } else if (type == UdfConstants.DATE){
                        if (value.toString().length() != 0) {
                            Calendar calendar = (Calendar) value;
                            value = df.parse(calendar);
                        }
                        eudf.setAttribute("value", value.toString());
                    } else {
                        eudf.setAttribute("value", value.toString());
                    }


                }

                udfs.appendChild(eudf);

            }
            task.appendChild(udfs);
        } // if udf

/*
        ArrayList TAs = stb.getAttachments();
        if (TAs != null && !TAs.isEmpty()) {
            Element files = xmlDoc.createElement("files");
            for (Iterator i = TAs.iterator(); i.hasNext();) {
                Attachment a = (Attachment) i.next();
                Element file = xmlDoc.createElement("file");
                Element name = xmlDoc.createElement("name");
                name.appendChild(xmlDoc.createTextNode(a.getName()));
                file.appendChild(name);
                files.appendChild(file);
            }
            task.appendChild(files);
        } // if Task Attachments
*/
        // references
        EggBasket<SecuredUDFValueBean, SecuredTaskBean> refs = AdapterManager.getInstance().getSecuredIndexAdapterManager().getReferencedTasksForTask(new SecuredTaskBean(taskid, sc));
        if (!refs.isEmpty()) {
            Element taskrefs = xmlDoc.createElement("taskrefs");

            for (SecuredUDFValueBean udf : refs.keySet()) {

                int type = udf.getUdfType();
                Element taskref = xmlDoc.createElement("udf");
                taskref.setAttribute("type", getType(type));
                taskref.setAttribute("name", udf.getCaption());

                for (SecuredTaskBean t : refs.get(udf)) {

                    Element el = xmlDoc.createElement("element");
                    el.appendChild(xmlDoc.createTextNode('#' + t.getNumber()));
                    taskref.appendChild(el);
                }
                taskrefs.appendChild(taskref);
            }
            task.appendChild(taskrefs);
        }
        EggBasket<SecuredUDFValueBean, SecuredUserBean> urefs = AdapterManager.getInstance().getSecuredIndexAdapterManager().getReferencedUsersForTask(new SecuredTaskBean(taskid, sc));
        if (!urefs.keySet().isEmpty()) {
            Element userrefs = xmlDoc.createElement("userrefs");
            for (SecuredUDFValueBean udf : urefs.keySet()) {

                int type = udf.getUdfType();
                Element userref = xmlDoc.createElement("udf");
                userref.setAttribute("type", getType(type));
                userref.setAttribute("name", udf.getCaption());

                for (SecuredUserBean u : urefs.get(udf)) {

                    Element el = xmlDoc.createElement("element");
                    el.appendChild(xmlDoc.createTextNode(u.getName()));
                    userref.appendChild(el);
                }
                userrefs.appendChild(userref);
            }
            task.appendChild(userrefs);
        }

        Element messages = xmlDoc.createElement("messages");
        ArrayList<SecuredMessageBean> col = stb.getMessages();
        String hrsFormat = stb.getBudgetFormat();
        //logger.assertVal(col != null, "listMessages() return null");
        if (!col.isEmpty()) {
            for (SecuredMessageBean mes : col) {
                Element message = xmlDoc.createElement("message");
                Element status = xmlDoc.createElement("status");
                status.appendChild(xmlDoc.createTextNode(HandMadeReportManager.stripNonValidXMLCharacters(mes.getMstatus().getName())));
                message.appendChild(status);

                Element timestamp = xmlDoc.createElement("timestamp");
                timestamp.appendChild(xmlDoc.createTextNode(df.parse(mes.getTime())));
                message.appendChild(timestamp);

                Element user = xmlDoc.createElement("user");
                user.appendChild(xmlDoc.createTextNode(mes.getSubmitter().getName()));
                message.appendChild(user);

                if (mes.getHrs() != null) {
                    Element time = xmlDoc.createElement("time");
                    log.debug("time");

                    HourFormatter hf = new HourFormatter(mes.getHrs(), hrsFormat, sc.getLocale());
                    time.appendChild(xmlDoc.createTextNode(hf.getString()));

                    /*HourFormatter hf =   new HourFormatter(mes.getHrs(), hrsFormat);
           time.appendChild(xmlDoc.createTextNode(I18n.getString(sc.getLocale(), "MSG_BUDGET_FORMAT", new Object[]{hf.getYears(), hf.getMonths(), hf.getWeeks(), hf.getDays(), hf.getHours(), hf.getMinutes(), hf.getSeconds()})));*/
                    message.appendChild(time);
                }

                if (mes.getHandlerUser() != null) {
                    Element handler = xmlDoc.createElement("handler");
                    handler.appendChild(xmlDoc.createTextNode(mes.getHandlerUser().getName()));
                    message.appendChild(handler);
                }

                if (mes.getResolution() != null) {
                    Element resolution = xmlDoc.createElement("resolution");
                    resolution.appendChild(xmlDoc.createTextNode(mes.getResolution().getName()));
                    message.appendChild(resolution);
                }

                if (mes.getPriority() != null) {
                    Element priority = xmlDoc.createElement("priority");
                    priority.appendChild(xmlDoc.createTextNode(mes.getPriority().getName()));
                    message.appendChild(priority);
                }

                if (mes.getDeadline() != null) {
                    Element deadline = xmlDoc.createElement("deadline");
                    deadline.appendChild(xmlDoc.createTextNode(df.parse(mes.getDeadline())));
                    message.appendChild(deadline);
                }

                if (mes.getBudget() != null) {
                    Element budget = xmlDoc.createElement("budget");
                    log.debug("budget");
                    HourFormatter hf = new HourFormatter(mes.getBudget(), hrsFormat, sc.getLocale());
                    budget.appendChild(xmlDoc.createTextNode(hf.getString()));
                    /*HourFormatter hf =   new HourFormatter(mes.getBudget(), hrsFormat);
                    budget.appendChild(xmlDoc.createTextNode(I18n.getString(sc.getLocale(), "MSG_BUDGET_FORMAT", new Object[]{hf.getYears(), hf.getMonths(), hf.getWeeks(), hf.getDays(), hf.getHours(), hf.getMinutes(), hf.getSeconds()})));*/
                    message.appendChild(budget);
                }


                String ldesc = mes.getDescription();

                if (ldesc != null) {
                    Element description = xmlDoc.createElement("description");
                    description.appendChild(xmlDoc.createTextNode(HandMadeReportManager.stripNonValidXMLCharacters(ldesc)));
                    message.appendChild(description);
                }

                messages.appendChild(message);

            }
            task.appendChild(messages);
        }
        parent.appendChild(task);
        return task;
    }

    private static String getType(int type) {
        if (type == UdfConstants.DATE)
            return "date";
        else if (type == UdfConstants.FLOAT)
            return "float";
        else if (type == UdfConstants.INTEGER)
            return "integer";
        else if (type == UdfConstants.LIST)
            return "list";
        else if (type == UdfConstants.MEMO)
            return "memo";
        else if (type == UdfConstants.MLIST)
            return "mlist";
        else if (type == UdfConstants.STRING)
            return "string";
        else if (type == UdfConstants.TASK)
            return "task";
        else if (type == UdfConstants.USER)
            return "user";
        else if (type == UdfConstants.URL)
            return "url";
        else
            return null;
    }
}