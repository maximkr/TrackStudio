package com.trackstudio.app.filter.customizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import com.trackstudio.app.UDFFormFillHelper;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Udf;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;

import net.jcip.annotations.NotThreadSafe;

/**
 * Кастомный вывод всплывающего окна (для вывода в фильтрах)
 */
@NotThreadSafe
public class PopUpCustomizer extends Customizer {

    /**
     * Задача
     */
    public static final int TASK = 0;
    /**
     * Пользователь
     */
    public static final int USER = 1;
    /**
     * Тип
     */
    public int type;

    /**
     * Возвращает тип
     *
     * @return тип
     */
    public int getType() {
        return type;
    }

    /**
     * Конструктор
     *
     * @param map  карта полей
     * @param type тип
     * @param dis  активность поля
     */
    public PopUpCustomizer(FieldMap map, int type, boolean dis) {
        super(map);
        this.disabled = dis;
        this.type = type;
    }


    /**
     * Метод для вывода информативного блока о полях
     *
     * @param sc          сессия пользователя
     * @param filter      фильтр
     * @param contextPath относительный путь контекста
     * @return строка вывода
     * @throws GranException при необзодимости
     */
    public String draw(SessionContext sc, FValue filter, String contextPath) throws GranException {
        return drawForDateOrPopUp(sc, "hideProperty", filter, contextPath);
    }

    /**
     * Метод для вывода блока ввода для полей
     *
     * @param sc          сессия пользователя
     * @param filter      фильтр
     * @param contextPath относительный путь контекста
     * @return строка вывода
     * @throws GranException при необзодимости
     */
    public String drawInput(SessionContext sc, FValue filter, String contextPath) throws GranException {
        String locale = sc.getLocale();
        StringBuffer outp = new StringBuffer(200);

        outp.append("<td style=\"vertical-align: middle; white-space: nowrap\">\n");
        List<String> def = filter.get(map.getFilterKey());
        List<String> origin = filter.getOriginValues(map.getFilterKey());
        String singleValue = origin != null && !origin.isEmpty() ? origin.get(0) : null;
        Udf udf = KernelManager.getFind().findUdf(map.getFilterKey().substring(3));
        String initialTask = null;
        if (udf != null && udf.getInitialtask() != null) {
            initialTask = udf.getInitialtask().getId();
        }
        boolean not = singleValue != null && singleValue.startsWith("_");
        String script = "<script>" +
                "new AjaxJspTag.Autocomplete(" +
                "\""+contextPath+"/predictor/\", {" +
                "progressStyle: \"throbbing\"," +
                "source: \"searchudflist("+map.getFilterKey()+")\"," +
                "target: \"autosearch("+map.getFilterKey()+")\"," +
                "minimumCharacters: \"1\"," +
                "delay: \""+ Config.getProperty("trackstudio.search.delay", "0")+"\"," +
                "className: \"autocomplete\"," +
                "parameters: \"key={searchudflist("+map.getFilterKey()+")},session=session,byTask=true,defaultTask="+initialTask+"\"," +
                "postFunction: filterTaskUdf" +
                "});" +
                "</script>";
        outp.append("<input type=\"text\" class=\"form-autocomplete\" name=\"searchudflist("+map.getFilterKey()+")\"" +
                "                size=\"50\" maxlength=\"1800\" id=\"searchudflist("+map.getFilterKey()+")\"/><input\n" +
                "                            type=\"hidden\" name=\"autosearch("+map.getFilterKey()+")\" id=\"autosearch("+map.getFilterKey()+")\">" +
                "  <input type=\"hidden\" name=\"autosearch("+map.getFilterKey()+")\" id=\"autosearch("+map.getFilterKey()+")\"></br>");
        outp.append(script);
        outp.append("<select style=\"vertical-align: middle\"  name=\"_").append(map.getFilterKey()).append("\">");
        outp.append("<option ").append(not ? "" : "selected").append(" value=\"\">");
        outp.append(I18n.getString(locale, "CONTAINS"));
        outp.append("<option ").append(not ? "selected" : "").append(" value=\"_\">");
        outp.append(I18n.getString(locale, "ISNOTCONTAINS"));
        outp.append("</select>&nbsp;");
        String ot = null;

        if (def != null && def.size() > 0) {
            List<String> selected = new ArrayList<String>();

            for (String tid : def) {
                if (getType() == TASK) {
                    String taskId = not ? tid.substring(1) : tid;

                    SecuredTaskBean task = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskById(sc, taskId);
                    if (task != null) selected.add('#' + task.getNumber());

                } else {
                    String userId = not ? tid.substring(1) : tid;
                    if (UserRelatedManager.getInstance().isUserExists(userId))
                        selected.add(new SecuredUserBean(userId, sc).getLogin());
                }
            }
            ot = UDFFormFillHelper.listToString(selected, "; ");
        }
        outp.append("<input type=\"text\" size=\"50\" id=\"").append(map.getFilterKey()).append("\" name=\"").append(map.getFilterKey()).append("\"")
                .append(" value=\"").append(buildViewValue(def, sc)).append("\"").append("\" >");
        outp.append("</td>\n");
        return outp.toString();
    }

    private String buildViewValue(List<String> def, SessionContext sc) throws GranException {
        StringBuilder viewValue = new StringBuilder();
        if (def != null) {
            for (Iterator<String> it = def.iterator();it.hasNext();) {
                String taskId = it.next();
                if (TaskRelatedManager.getInstance().isTaskExists(taskId)) {
                    SecuredTaskBean task = new SecuredTaskBean(taskId, sc);
                    viewValue.append("#").append(task.getNumber());
                    if (it.hasNext()) viewValue.append(";");
                }
            }
        }
        return viewValue.toString();
    }

    /**
     * Устанавливает фильтр
     *
     * @param sc      сессия пользователя
     * @param request запрос
     * @param filter  фильтр
     * @throws GranException при необходимости
     */
    public void setFilter(SessionContext sc, HttpServletRequest request, FValue filter) throws GranException {
        String value = request.getParameter(map.getFilterKey()); // list
        ArrayList<String> sel = new ArrayList<String>();
        if (value != null && value.length() != 0) {

            StringTokenizer tk = new StringTokenizer(value.trim(), ";");
            while (tk.hasMoreTokens()) {
                try {
                    if (getType() == TASK) {
                        SecuredTaskBean task = AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskByNumber(sc, tk.nextToken().trim());
                        if (task != null) {
                            sel.add(task.getId());
                        }
                    } else {
                        sel.add(AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(sc, tk.nextToken().trim()).getId());
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

        }
        String avalue = request.getParameter('_' + map.getFilterKey()); // is/is not
        avalue = avalue == null ? "" : avalue;
        if (value != null) {
            filter.remove(map.getFilterKey());
            filter.remove(FValue.SUB + map.getFilterKey());
            for (String v : sel)
                filter.putItem(map.getFilterKey(), avalue + v);
        }

        if (value == null && avalue == null) {
            filter.remove(map.getFilterKey());
            filter.remove(FValue.SUB + map.getFilterKey());
        }
    }
}
