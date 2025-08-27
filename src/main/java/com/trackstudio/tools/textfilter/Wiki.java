package com.trackstudio.tools.textfilter;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.Null;
import com.trackstudio.view.TaskView;
import com.trackstudio.view.TaskViewHTML;
import com.trackstudio.view.TaskViewText;

import net.jcip.annotations.ThreadSafe;

/**
 * Класс для разбора текста и подготовки его вывода в различные контексты, текст разбирается с учетом специфики ТС. 
 * Контексты вывода определяются классами TaskView
 */
@ThreadSafe
public class Wiki {
    private static Log log = LogFactory.getLog(Wiki.class);
    protected volatile TaskView view;
    private volatile ConcurrentHashMap<String, String[]> parameters;

    /**
     * Конструктор
     *
     * @param session сессия пользователя
     */
    @Deprecated
    public Wiki(SessionContext session) {
        try {
            view = new TaskViewText(new SecuredTaskBean("1", session));
        } catch (GranException e) {

            e.printStackTrace();
        }
        //this.url = Config.getInstance().getSiteURL();
        //this.session = session;
    }
    /**
     * @deprecated Контексты должны задаваться только через TaskView. По сути, Wiki - вспомогательный класс для TaskView
     */
    public Wiki(SessionContext session, String url) {
        try {
            view = new TaskViewText(new SecuredTaskBean("1", session));
        } catch (GranException e) {

            e.printStackTrace();
        }
        //this.url = url;
        //this.session = session;
    }
    /**
     * 	Единственный правильный конструктор. Контекст задается классом TaskView
     * @param view
     */
    public Wiki(TaskView view){
        this.view = view;
    }

    /**
     * Преобразует текст в вид, пригодный для вывода в HTML
     *
     * @param what исходная строка
     * @return выходная строка
     */
    @Deprecated
    public String toHTML(String what) {
        log.trace("toHTML");
        throw new UnsupportedOperationException("to use toMacros instead this method");
    }

    /**
     * Преобразует текст с помощью макросов, заданных в параметре macros.adapter файла trackstudio.adapter.properties
     * @param description текст с макросами
     * @param viewText параметр, управляющий форматом вывода (текст или HTML)
     * @return treated text
     * @deprecated Оставлено для совместимости со старыми почтовыми шаблонами.
     */
    public String toMacros(String description, boolean viewText) {
        log.trace("toMacros");
        try {
            view = new TaskViewHTML(new SecuredTaskBean("1", view.getTask().getSecure()), Config.getInstance().getSiteURL());
            return MacrosUtil.convertUrlToText(AdapterManager.getInstance().getSecuredMacrosAdapterManager().process(view, description, this.parameters));
        } catch (GranException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Метод преобразует текст с помощью макросов. 
     * Так как контекст задан в TaskView, никаких параметров кроме собственно текста нам не нужно.
     * Используется не только для преобразования описаний задач, поэтому текст передается параметром, а не берется из TaskView 
     * @param description исходный текст
     * @return преобразованный текст
     */
    public String toMacros(String description) {
        log.trace("toMacros");
        String result = Null.isNotNull(description) ? AdapterManager.getInstance().getSecuredMacrosAdapterManager().process(view, description, this.parameters) : description;
        return Config.isTurnItOn("trackstudio.email.image.base64") ? MacrosUtil.convertUrlToText(result) : result;
    }

    /**
     * Метод преобразует текст с помощью макросов. 
     * Контекст задается первым параметром. Это нужно, например, для обработок внутри шаблона e-mail, когда "снаружи" не известно,
     * какого типа шаблон.
     * Используется не только для преобразования описаний задач, поэтому текст передается параметром, а не берется из TaskView
     * @param v контекст 
     * @param description исходный текст
     * @return преобразованный текст
     */
    public String toMacros(TaskView v, String description) {
        log.trace("toMacros");
        return AdapterManager.getInstance().getSecuredMacrosAdapterManager().process(v, description, this.parameters);
    }

    @Deprecated
    public String toMacros(String description, boolean viewText, String url) {
        return toMacros(description, viewText);
    }

    private boolean inParseZone(StringBuffer sb, int index) {
        int i1;

        //in tag
        if ((i1 = sb.toString().lastIndexOf('<', index)) != -1) {
            int i2 = sb.toString().indexOf('>', i1);
            if (i2 == -1)
                log.error("!!! \"<\" found, but \">\" not found!");
            if (i2 >= index) {
                return false;
            }
        }

        //in link
        if ((i1 = sb.toString().toLowerCase(Locale.ENGLISH).lastIndexOf("<a", index)) != -1) {
            int i2 = sb.toString().toLowerCase(Locale.ENGLISH).indexOf("</a", i1);
            if (i2 == -1)
                log.error("!!! \"<a\" found, but \"</a\" not found!");
            if (i2 >= index) {
                return false;
            }
        }

        return true;
    }

    public void setParameters(Map<String, String[]> parameters) {
        this.parameters = new ConcurrentHashMap<String, String[]>(Null.removeNullElementsFromMap(parameters));
    }

    /**
     * This method cuts the html from string
     * @param value input html
     * @return result without html
     */
    public String cutHtml(String value) {
        return MacrosUtil.cutHtml(value);
    }
}