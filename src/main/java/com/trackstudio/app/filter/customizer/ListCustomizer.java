package com.trackstudio.app.filter.customizer;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.CantFindObjectException;
import com.trackstudio.exception.GranException;
import com.trackstudio.model.Named;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredPriorityBean;
import com.trackstudio.secured.SecuredResolutionBean;
import com.trackstudio.secured.SecuredStatusBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Null;

import net.jcip.annotations.NotThreadSafe;

/**
 * Кастомный вывод списка (для вывода в фильтрах)
 */
@NotThreadSafe
public class ListCustomizer extends Customizer implements Serializable{

    private static Log log = LogFactory.getLog(ListCustomizer.class);
    /**
     * Константа равенства списка
     */
    public static int LIST_EQUAL = 0;
    /**
     * Константа неравенства списка
     */
    public static int LIST_UNEQUAL = 1;
    /**
     * Константа сортировки списка
     */
    public static int LIST_ASC = 2;
    /**
     * Константа наличия списка
     */
    public static int LIST_CONTAINS = 3;

    private boolean multiple = false;
    private Map<String, String> items = null;
    private int theType = 0;
    private FValue originFilter = null;

    /**
     * Возвращает оригинал фильтра
     *
     * @return фильтр
     */
    public FValue getOriginFilter() {
        return originFilter;
    }

    /**
     * Устанавливает оригинал фильтра
     *
     * @param originFilter оригинал фильтра
     */
    public void setOriginFilter(FValue originFilter) {
        this.originFilter = originFilter;
    }

    /**
     * Конструктор
     *
     * @param col      Коллекция
     * @param type     тип
     * @param map      карта полей
     * @param multiple можественный ли список
     */
    public ListCustomizer(Map<String, String> col, int type, FieldMap map, boolean multiple) {
        super(map);
        this.items = col;
        this.theType = type;
        this.multiple = multiple;
    }

    /**
     * Возвращает итератор имен
     *
     * @return итератор
     */
    public Iterator byNameIterator() {
        TreeMap<Entry, String> internal = new TreeMap<Entry, String>();
        for (String key : this.items.keySet()) {
            String value = this.items.get(key);
            if (value != null)
                internal.put(new Entry(key, value), key);
        }
        return internal.values().iterator();
    }

    /**
     * Возвращает итератор чисел
     *
     * @return итератор
     */
    public Iterator byNumberIterator() {
        TreeMap<Integer, String> internal = new TreeMap<Integer, String>();
        for (String key : this.items.keySet()) {
            Integer value = new Integer(key);
            internal.put(value, key);
        }
        return internal.values().iterator();
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
        StringBuffer outp = new StringBuffer(500);

        if (items == null /*|| this.items.isEmpty()*/)
            return outp.toString();
        outp.append("<td style=\"vertical-align: top\"><label for=\"").append(Null.stripNullText(map.getFilterKey())).append("\">").append(I18n.getString(sc, map.getAltKey())).append("</label></td>");
        outp.append(drawInput(sc, filter, contextPath));
        return outp.toString();
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
        String[] values = request.getParameterValues("udflist("+map.getFilterKey()+")");
        String[] avalues = request.getParameterValues("udflist("+FValue.SUB + map.getFilterKey()+")");
        filter.remove(map.getFilterKey());
        filter.remove(FValue.SUB + map.getFilterKey());
        if (values != null) for (String value : values) {
            if (value != null && !value.equals("0")) {
                if (avalues != null && avalues.length == 1 && avalues[0].equals(FValue.SUB)) {
                    filter.putItem(map.getFilterKey(), FValue.SUB + value);
                } else {
                    filter.putItem(map.getFilterKey(), value);
                }
            }
        }

        if (avalues != null && avalues.length > 1) {
            for (String avalue : avalues)
                filter.putItem(FValue.SUB + map.getFilterKey(), avalue);
        }
        if (values == null && avalues == null) {
            filter.remove(map.getFilterKey());
            filter.remove(FValue.SUB + map.getFilterKey());
        }
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
        List<String> defs2 = originFilter == null ? filter.get(map.getFilterKey()) : originFilter.get(map.getFilterKey());
        this.buildIterator(defs2, sc);
        String locale = sc.getLocale();
        StringBuffer outp = new StringBuffer(2000);
        outp.append("<td style=\"vertical-align: top; white-space: nowrap;width:90%;\">\n");
        outp.append("<table>\n");
        outp.append("<tr>\n");
        outp.append("<td style=\"vertical-align: top; white-space: nowrap;\">\n");
        List<String> defs = filter.get(map.getFilterKey());
        String prefix = filter.getPrefix(map.getFilterKey());

        boolean not = (defs != null && !defs.isEmpty() && prefix.equals(FValue.SUB));

        outp.append("<select valign=middle ").
                append(" name=\"udflist(_").append(map.getFilterKey()).append(")\"").
                append(">").
                append("<option ").append(!not ? "" : "selected='true'").append(" value=\"\">");

        if (theType == LIST_EQUAL) {
            outp.append(I18n.getString(locale, "IS"));
        } else if (theType == LIST_CONTAINS) {
            outp.append(I18n.getString(locale, "CONTAINS"));
        } else if (theType == LIST_UNEQUAL) {
            outp.append("=&gt;");
        } else
            outp.append(I18n.getString(locale, "ASC"));

        outp.append("<option ").append(!not ? "" : "selected='true'").append(" value=\"_\">");

        if (theType == LIST_EQUAL) {
            outp.append(I18n.getString(locale, "ISNOT"));
        } else if (theType == LIST_CONTAINS) {
            outp.append(I18n.getString(locale, "ISNOTCONTAINS"));
        } else if (theType == LIST_UNEQUAL) {
            outp.append("&lt;=");
        } else
            outp.append(I18n.getString(locale, "SORT_DESC"));

        outp.append("</select>");
        outp.append("</td>\n");
        outp.append("<td style=\"width:100%;\">\n");
        outp.append("<input type=\"text\" class=\"form-autocomplete\" name=\"searchudflist(").append(map.getFilterKey()).append(")\" size=\"50\" onkeyup=\"__localsearch(this);\">");
        outp.append("<input type=\"checkbox\" onclick=\"checkedListCheckBoxAll('_searchudflist(").append(map.getFilterKey()).append(")', this.checked);\">").append(I18n.getString(sc.getLocale(), "SELECT_ALL")).append("\n");
        outp.append("<div class=\"selectbox\" name='udflist(").append(map.getFilterKey()).append(")' style=\"width:100%;\" id='_searchudflist(").append(map.getFilterKey()).append(")'>\n");
        boolean marker = true;
        StringBuilder selectedGroup = new StringBuilder(1000);
        StringBuilder unselectedGroup = new StringBuilder(1000);
        StringBuilder selectedUser = new StringBuilder(1000);
        StringBuilder unselectedUser = new StringBuilder(1000);
        boolean withUserRole = map.getFilterKey().equals(FieldMap.HUSER_NAME.getFilterKey());
        for (Iterator it = theType == LIST_UNEQUAL ? this.byNumberIterator() : this.byNameIterator(); it.hasNext();) {
            int classStyle = marker ? 1 : 0;
            marker = !marker;
            String key = (String) it.next();
            String value = this.items.get(key);
            if (key.equals("null")) {
                //outp.append("<label class=\"sel").append(classStyle).append("\">").append("</label>");
            } else if (key.startsWith("GROUP_")) {
                String id = key.substring("GROUP_".length());
                String checked = defs != null && defs.contains(key) ? " checked=\"checked\"" : "";
                if (checked.isEmpty()) {
                    unselectedGroup.append("<label class=\"sel").append(classStyle).append("\">").append("&nbsp;&nbsp;&nbsp;&nbsp;<input alt=\"listcheckbox\" " +
                            " onclick=\"moveToParticipate(this.id, this.checked, '" + (withUserRole ? "participants_prstatus_" : "participants_") + "');\"" +
                            "type=\"checkbox\"").append(checked).append(" value=\"").append(key).append("\" name=\"udflist(").append(map.getFilterKey()).append(")\" id='").append(map.getFilterKey()).append("_").append(id).append("'>").append(value).append("").append("</label>");
                } else {
                    selectedGroup.append("<label class=\"sel").append(classStyle).append("\">").append("&nbsp;&nbsp;&nbsp;&nbsp;<input alt=\"listcheckbox\" " +
                            " onclick=\"moveToParticipate(this.id, this.checked, '" + (withUserRole ? "participants_prstatus_" : "participants_") + "');\"" +
                            "type=\"checkbox\"").append(checked).append(" value=\"").append(key).append("\" name=\"udflist(").append(map.getFilterKey()).append(")\" id='").append(map.getFilterKey()).append("_").append(id).append("'>").append(value).append("").append("</label>");
                }
            } else {
                String checked = defs != null && defs.contains(key) ? " checked=\"checked\"" : "";
                if (checked.isEmpty()) {
                    unselectedUser.append("<label class=\"sel").append(classStyle).append("\">")
                            .append("&nbsp;&nbsp;&nbsp;&nbsp;<input alt=\"listcheckbox\" type=\"checkbox\" onclick=\"moveToParticipate(this.id, this.checked, '" + (withUserRole ? "participants_user_" : "participants_") + "');\"").append(checked).append(" value=\"").append(key).append("\" name=\"udflist(").append(map.getFilterKey()).append(")\" id='").append(map.getFilterKey()).append("_").append(key).append("'>").append(value).append("").append("</label>");
                } else {
                    selectedUser.append("<label class=\"sel").append(classStyle).append("\">")
                            .append("&nbsp;&nbsp;&nbsp;&nbsp;<input alt=\"listcheckbox\" type=\"checkbox\" onclick=\"moveToParticipate(this.id, this.checked, '"+(withUserRole ? "participants_user_" : "participants_")+"');\"").append(checked).append(" value=\"").append(key).append("\" name=\"udflist(").append(map.getFilterKey()).append(")\" id='").append(map.getFilterKey()).append("_").append(key).append("'>").append(value).append("").append("</label>");
                }
            }
        }

        outp.append("<label id='participants_").append(map.getFilterKey()).append("'>").append(I18n.getString(sc, "SELECTED")).append("</label>");
        String groupPartStr = selectedGroup.toString();
        if (!groupPartStr.isEmpty()) {
            String label = "<label id='participants_prstatus_"+map.getFilterKey()+"' class=\"sel0\" for=\""+map.getFilterKey()+"\">";
            outp.append(label).append("&nbsp;&nbsp;");
            outp.append(I18n.getString(sc.getLocale(), "PRSTATUS"));
            outp.append("</label>");
            outp.append(groupPartStr);
            label = "<label id='participants_user_"+map.getFilterKey()+"' class=\"sel1\" for=\""+map.getFilterKey()+"\">";
            outp.append(label).append("&nbsp;&nbsp;");
            outp.append(I18n.getString(sc.getLocale(), "USER"));
            outp.append("</label>");
        }
        outp.append(selectedUser);
        outp.append("<label id='users_list_").append(map.getFilterKey()).append("'>").append(I18n.getString(sc, "UNSELECTED")).append("</label>");
        String unselectedGroupStr = unselectedGroup.toString();
        if (!unselectedGroupStr.isEmpty()) {
            String label = "<label class=\"sel0\" for=\""+map.getFilterKey()+"\">";
            outp.append(label).append("&nbsp;&nbsp;");
            outp.append(I18n.getString(sc.getLocale(), "PRSTATUS"));
            outp.append("</label>");
            outp.append(unselectedGroupStr);
            label = "<label class=\"sel1\" for=\""+map.getFilterKey()+"\">";
            outp.append(label).append("&nbsp;&nbsp;");
            outp.append(I18n.getString(sc.getLocale(), "USER"));
            outp.append("</label>");
        }
        outp.append(unselectedUser);
        outp.append("</div>\n");
        outp.append("</td>\n");
        outp.append("</tr>\n");
        outp.append("</table>\n");
        outp.append("</td>\n");
        return outp.toString();
    }

    private void buildIterator(List<String> defs2, SessionContext sc) {
        if (defs2 != null) {
            for (String str : defs2) {
                if (!str.equals("0") && !str.equals("null") && !items.containsKey(str)) {
                    try {
                        if (map.getFilterKey().equals(FieldMap.SUSER_NAME.getFilterKey()) || map.getFilterKey().equals(FieldMap.HUSER_NAME.getFilterKey()) || map.getFilterKey().equals(FieldMap.MSG_SUSER_NAME.getFilterKey()) || map.getFilterKey().equals(FieldMap.MSG_HUSER_NAME.getFilterKey())) {
                            SecuredUserBean sub = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(sc, str);
                            if (sub != null) items.put(sub.getId(), sub.getName());
                        }
                        if (map.getFilterKey().equals(FieldMap.TASK_CATEGORY.getFilterKey())) {
                            SecuredCategoryBean sub = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, str);
                            items.put(sub.getId(), sub.getName());
                        }
                        if (map.getFilterKey().equals(FieldMap.TASK_STATUS.getFilterKey())) {
                            SecuredStatusBean sub = AdapterManager.getInstance().getSecuredFindAdapterManager().findStatusById(sc, str);
                            items.put(sub.getId(), sub.getWorkflow().getName() + '/' + sub.getName() + ' ');
                        }
                        if (map.getFilterKey().equals(FieldMap.TASK_PRIORITY.getFilterKey())) {
                            SecuredPriorityBean sub = AdapterManager.getInstance().getSecuredFindAdapterManager().findPriorityById(sc, str);
                            items.put(sub.getId(), sub.getWorkflow().getName() + " / " + sub.getName() + ' ');
                        }
                        if (map.getFilterKey().equals(FieldMap.TASK_RESOLUTION.getFilterKey()) || map.getFilterKey().equals(FieldMap.MSG_RESOLUTION.getFilterKey())) {
                            SecuredResolutionBean sub = AdapterManager.getInstance().getSecuredFindAdapterManager().findResolutionById(sc, str);
                            items.put(sub.getId(), sub.getMstatus().getWorkflow().getName() + " / " + sub.getMstatus().getName() + " / " + sub.getName() + ' ');
                        }
                        if (map.getFilterKey().equals(TaskFValue.MSG_TYPE)) {
                            SecuredMstatusBean sub = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, str);
                            items.put(sub.getId(), sub.getWorkflow().getName() + " / " + sub.getName() + ' ');
                        }
                    } catch (CantFindObjectException ex) {
                        // Object not found
                        log.debug("Object not found");
                    } catch (GranException e) {
                        log.error("Error", e);
                    }
                }
            }
        }
    }

//todo интересно, зачем это ? PopUpCustomizer и без этого работает и все сортирует
    /**
     * Вспомогательный класс для сортировки
     */
    private static class Entry extends Named implements Comparable {
        /**
         * Конструктор
         *
         * @param key   ключ
         * @param value значение
         */
        public Entry(String key, String value) {
            this.id = key;
            this.name = value;
        }

        /**
         * Сравнивает два объекта текущего класса
         *
         * @param o объект
         * @return +1, 0 или -1
         */
        public int compareTo(Object o) {
            Named m = (Named) o;
            if (this.name == null) {
                return 1;
            }
            String other = m.getName();
            if (other == null) {
                return -1;
            }
            int res = this.getName().compareTo(other);
            if (res == 0) {
                res = this.getCodeName().compareTo(m.getCodeName());
            }
            return res;
        }
    }
}
