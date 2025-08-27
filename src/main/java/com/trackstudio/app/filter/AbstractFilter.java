package com.trackstudio.app.filter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.UdfValue;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.AbstractBeanWithUdf;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.Immutable;

/**
 * Класс содержит базовые и основные методы для проверки соответствия значений пользовательских полей условиям фильтрации
 */
@Immutable
public abstract class AbstractFilter {
    /**
     * Объект для логирования текущих данных
     */
    protected static final Log log = LogFactory.getLog(AbstractFilter.class);


    public boolean passUdf(SecuredTaskBean stb, FValue fv, List<SecuredUDFValueBean> udfValues) throws GranException {
        Set<String> useForUdf = fv.getUsedUdfIds();
        if (useForUdf != null && !useForUdf.isEmpty() && udfValues != null) {
            HashMap<String, SecuredUDFValueBean> usedUdfValues = new HashMap<String, SecuredUDFValueBean>();
            for (SecuredUDFValueBean udfValueBean : udfValues) {
                if (useForUdf.contains(udfValueBean.getUdfId()))
                    usedUdfValues.put(udfValueBean.getUdfId(), udfValueBean);
            }
            for (String udfId : useForUdf) {
                if (KernelManager.getFind().findUdfNull(udfId) == null)
                    continue;
                boolean needAdd = true;
                SecuredUDFValueBean udf = usedUdfValues.get(udfId);
                // empty operand, skip
                if (udf != null && udf.getUdfType().equals(UdfValue.DATE)) {
                    if (!fv.checkValueDate(FValue.UDF + udfId)) {
                        continue;
                    }
                } else {
                    if (!fv.getPrefix(FValue.UDF + udfId).equals(FValue.EMPTY)){
                        if (emptyOperand(fv.getAsString(FValue.UDF + udfId))) {
                            continue;
                        } else {
                            // For Task and User 'All' valus saves as '0'. Fix for bug #60508
                            if (udf != null && ((udf.getUdfType().equals(UdfValue.USER) || udf.getUdfType().equals(UdfValue.TASK) ||
                                    udf.getUdfType().equals(UdfValue.LIST) || udf.getUdfType().equals(UdfValue.MULTILIST)) &&
                                    (fv.get(FValue.UDF + udfId) == null || fv.get(FValue.UDF + udfId).get(0).equals("0"))))
                                continue;
                        }
                    }
                }
                // not empty operand, udfValue is null - return false
                if (udf == null)
                    return false;
                int type = udf.getUdfType();
                Object value;
                if (type == UdfValue.LIST || type == UdfValue.MULTILIST)
                    value = udf.getList(stb);
                else
                    value = udf.getValue(stb);
                if (type == UdfValue.MEMO && needAdd)
                    needAdd = testString(fv, FValue.UDF + udfId, udf.isHtmlview() && value != null ? HTMLEncoder.stripHtmlTags((String) value) : (String) value);
                if ((type == UdfValue.STRING || type == UdfValue.URL) && needAdd)
                    needAdd = testString(fv, FValue.UDF + udfId, value != null ? value.toString() : "");
                if (type == UdfValue.INTEGER && needAdd)
                    needAdd = testNumber(fv, FValue.UDF + udfId, value != null ? Integer.valueOf(value.toString()) : null);
                if (type == UdfValue.FLOAT && needAdd)
                    needAdd = testFloat(fv, FValue.UDF + udfId, value != null ? Double.valueOf(value.toString()) : null);
                if (type == UdfValue.DATE && needAdd)
                    needAdd = testTimestamp(fv, FValue.UDF + udfId, (Calendar) value);
                if (type == UdfValue.LIST && needAdd)
                    needAdd = testList(fv, FValue.UDF + udfId, value);
                if ((type == UdfValue.MULTILIST || type == UdfValue.TASK) && needAdd) {
                    needAdd = testMultiList(fv, FValue.UDF + udfId, value, udf.getSecure().getUserId());
                }
                if ((type == UdfValue.USER) && needAdd)
                    needAdd = testUserList(fv, FValue.UDF + udfId, value, udf.getSecure().getUserId());
                if (!needAdd)
                    return false;
            }
        }
        return true;
    }

    /**
     * Проверяет соответствие пользовательского поля условиям фильтрации
     *
     * @param fv   параметры фильтрации
     * @param bean обхект, содержащий информацию о пользовательском поле
     * @return TRUE - соответствует, FALSE - нет
     * @throws GranException при необходимости
     */
    public boolean passUdf(FValue fv, AbstractBeanWithUdf bean) throws GranException {
        //todo winzard просьба логи не удалять и не комментарить. Их можно отключить в trackstudio.log4j.properties
        // log.trace("##########");
        Set<String> useForUdf = fv.getUsedUdfIds();
        if (useForUdf != null && !useForUdf.isEmpty()) {
            HashMap<String, SecuredUDFValueBean> usedUdfValues = new HashMap<String, SecuredUDFValueBean>();
            Set<String> usedUdfs = new HashSet<String>();
            if (bean instanceof SecuredTaskBean){
                SecuredTaskBean task = (SecuredTaskBean) bean;
                for (SecuredUDFBean udf : task.getUDFs()){
                    if (udf != null)
                        usedUdfs.add(udf.getId());
                }
            } else if (bean instanceof SecuredUserBean){
                SecuredUserBean user = (SecuredUserBean)bean;
                for (SecuredUDFBean udf : user.getUDFs()){
                    if (udf != null)
                        usedUdfs.add(udf.getId());
                }
            }
            for (SecuredUDFValueBean udfValueBean : bean.getFilteredUDFValues()) {
                if (useForUdf.contains(udfValueBean.getUdfId()))
                    usedUdfValues.put(udfValueBean.getUdfId(), udfValueBean);
            }
            for (String udfId : useForUdf) {
                if (KernelManager.getFind().findUdfNull(udfId) == null)
                    continue;
                //если для задачи нет поля udf, то задача не прозодит по фильтру
                if (!usedUdfs.contains(udfId))
                    return false;
                boolean needAdd = true;
                SecuredUDFValueBean udf = usedUdfValues.get(udfId);
                // empty operand, skip
                if (udf != null && udf.getUdfType().equals(UdfValue.DATE)) {
                    if (!fv.checkValueDate(FValue.UDF + udfId)) {
                        continue;
                    }
                } else {
                    if (!fv.getPrefix(FValue.UDF + udfId).equals(FValue.EMPTY)){
                        if (emptyOperand(fv.getAsString(FValue.UDF + udfId))) {
                            continue;
                        } else {
                            // For Task and User 'All' valus saves as '0'. Fix for bug #60508
                            if (udf != null && ((udf.getUdfType().equals(UdfValue.USER) || udf.getUdfType().equals(UdfValue.TASK) ||
                                    udf.getUdfType().equals(UdfValue.LIST) || udf.getUdfType().equals(UdfValue.MULTILIST)) &&
                                    (fv.get(FValue.UDF + udfId) == null || fv.get(FValue.UDF + udfId).get(0).equals("0"))))
                                continue;
                        }
                    }
                }
                // not empty operand, udfValue is null - return false
                if (udf == null)
                    return false;
                int type = udf.getUdfType();
                Object value;
                if (type == UdfValue.LIST || type == UdfValue.MULTILIST)
                    value = udf.getList(bean);
                else
                    value = udf.getValue(bean);
                if (type == UdfValue.MEMO && needAdd)
                    needAdd = testString(fv, FValue.UDF + udfId, udf.isHtmlview() && value != null ? HTMLEncoder.stripHtmlTags((String) value) : (String) value);
                if ((type == UdfValue.STRING || type == UdfValue.URL) && needAdd)
                    needAdd = testString(fv, FValue.UDF + udfId, value != null ? value.toString() : "");
                if (type == UdfValue.INTEGER && needAdd)
                    needAdd = testNumber(fv, FValue.UDF + udfId, value != null ? Integer.valueOf(value.toString()) : null);
                if (type == UdfValue.FLOAT && needAdd)
                    needAdd = testFloat(fv, FValue.UDF + udfId, value != null ? Double.valueOf(value.toString()) : null);
                if (type == UdfValue.DATE && needAdd)
                    needAdd = testTimestamp(fv, FValue.UDF + udfId, (Calendar) value);
                if (type == UdfValue.LIST && needAdd)
                    needAdd = testList(fv, FValue.UDF + udfId, value);
                if ((type == UdfValue.MULTILIST || type == UdfValue.TASK) && needAdd)
                    needAdd = testMultiList(fv, FValue.UDF + udfId, value, udf.getSecure().getUserId());
                if ((type == UdfValue.USER) && needAdd)
                    needAdd = testUserList(fv, FValue.UDF + udfId, value, udf.getSecure().getUserId());
                if (!needAdd)
                    return false;
            }
        }
        return true;
    }

    /**
     * Сравнивает 2 объекта текущего класса
     *
     * @param a один объект
     * @param b второй объект
     * @return +1, 0 или -1
     */
    public int compare(Object a, Object b) {
        if (a == null && b != null)
            return 1;
        if (a != null && b == null)
            return -1;
        if (a == null)
            return 0;
        if (a instanceof Calendar && b instanceof Calendar) {
            return compareDate((Calendar) a, (Calendar) b);
        } else if (a instanceof Integer && b instanceof Integer) {
            return compareInteger((Integer) a, (Integer) b);
        } else if (a instanceof Double && b instanceof Double) {
            return compareDouble((Double) a, (Double) b);
        } else if (a instanceof String && b instanceof String) {
            return compareString((String) a, (String) b);
        } else if (a instanceof TreeSet && b instanceof TreeSet) {
            return compareTreeSet((TreeSet) a, (TreeSet) b);
        } else if (a instanceof List && b instanceof List) {
            return compareList((List) a, (List) b);
        } else {
            return compareObject(a, b);
        }
    }

    /**
     * Сравнивает две даты
     *
     * @param value1 одна дата
     * @param value2 вторая дата
     * @return +1, 0 или -1
     */
    private int compareDate(Calendar value1, Calendar value2) {
        if (value1.after(value2))
            return 1;
        if (value1.before(value2)) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * Сравнивает два целых числа
     *
     * @param value1 одно число
     * @param value2 второе число
     * @return +1, 0 или -1
     */
    private int compareInteger(Integer value1, Integer value2) {
        if (value1 > value2)
            return 1;
        if (value1 < value2)
            return -1;
        if (value1.intValue() == value2.intValue())
            return 0;
        log.error("Something incorrect");
        return 0;
    }

    /**
     * Сравнивает два дробных числа
     *
     * @param value1 одно число
     * @param value2 другое число
     * @return +1, 0 или -1
     */
    private int compareDouble(Double value1, Double value2) {
        if (value1.floatValue() > value2.floatValue())
            return 1;
        if (value1.floatValue() < value2.floatValue())
            return -1;
        // todo maximkr: нельзя на равенство float-ы сравнивать
        if (value1.floatValue() == value2.floatValue())
            return 0;
        log.error("Something incorrect");
        return 0;
    }

    /**
     * Сравнивает две строки
     *
     * @param value1 одна строка
     * @param value2 вторая строка
     * @return +1, 0 или -1
     */
    protected int compareString(String value1, String value2) {
        return value1.compareToIgnoreCase(value2);
    }

    /**
     * Сравнивает два дерева
     *
     * @param value1 одно дерево
     * @param value2 второе дерево
     * @return +1, 0 или -1
     */
    public int compareTreeSet(TreeSet value1, TreeSet value2) {
        String s1 = value1.toString();
        s1 = s1.substring(1, s1.length() - 1);
        String s2 = value2.toString();
        s2 = s2.substring(1, s2.length() - 1);
        return s1.compareTo(s2);
    }


    public int compareList(List value1, List value2) {
        String s1 = value1.toString();
        s1 = s1.substring(1, s1.length() - 1);
        String s2 = value2.toString();
        s2 = s2.substring(1, s2.length() - 1);
        return s1.compareTo(s2);
    }

    /**
     * Сравнивает два объекта
     *
     * @param value1 первый объект
     * @param value2 второй объект
     * @return +1, 0 или -1
     */
    public int compareObject(Object value1, Object value2) {
        return ((Comparable) value1).compareTo(value2);
    }


    /**
     * Проверяет числовое значение на соответствие фильтру
     *
     * @param flt           параметры фильтрации
     * @param property      свойство
     * @param databaseValue значение из бд
     * @return TRUE если число удовлетворяет условиям фильтрации, FALSE если нет
     *         <p/>
     *         Условные обозначения:
     *         <=5: '5'
     *         >=5: '_5'
     *         <>5: '_ne_5'
     *         ==5: '_eq_5'
     */
    public boolean testNumber(FValue flt, String property, Number databaseValue) {
        String propertyValue = flt.getAsString(property);

        // any test with empty operand passes
        if (emptyOperand(propertyValue))
            return true;

        // any non-empty test with empty database value
        String prefix = flt.getPrefix(property);
        if (databaseValue == null)
            return prefix.equals(FValue.NE);

        if (prefix.equals(FValue.IN)) {
            if (propertyValue.length() == 0) return true;
            Character[] characters = {',',';','-'};
            for (Character c: characters)
                propertyValue = propertyValue.replaceAll(c.toString(), " ");
            StringTokenizer st = new StringTokenizer(propertyValue, " -", true);
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                if (token.compareTo(" ") == 0 || token.compareTo("-") == 0) {  // bad filter string
                    continue;
                }
                int op1 = Integer.parseInt(token);
                if (!st.hasMoreTokens())
                    return op1 == databaseValue.intValue();
                token = st.nextToken();
                if (token.compareTo(" ") == 0) { // list of values
                    if (op1 == databaseValue.intValue())
                        return true;
                } else if (token.compareTo("-") == 0) { // range of values
                    if (!st.hasMoreTokens())
                        return databaseValue.intValue() >= op1;
                    token = st.nextToken();
                    int op2 = Integer.parseInt(token);
                    if (databaseValue.intValue() >= op1 && databaseValue.intValue() <= op2)
                        return true;

                } else {  // bad filter string
                    log.error("Bad input filter string " + propertyValue);
                    return false;
                }
            }
            return false;
        } else {

            int operand = Integer.parseInt(propertyValue);

            // Not equals
            if (prefix.equals(FValue.NE)) {
                return databaseValue.intValue() != operand;
            }

            // Equals
            if (prefix.equals(FValue.EQ)) {
                return databaseValue.intValue() == operand;
            }

            // Greater or equals (>=)
            if (prefix.startsWith(FValue.SUB)) {
                return databaseValue.intValue() <= operand;
            }

            // Less or equals (<=)
            return databaseValue.intValue() >= operand;
        }
    }

    /**
     * Проверяет строковое значение на соответствие фильтру
     *
     * @param flt           параметры фильтрации
     * @param property      свойство
     * @param databaseValue значение из бд
     * @return TRUE если строка удовлетворяет условиям фильтрации, FALSE если нет
     *         <p/>
     *         Условные обозначения:
     *         contains TEST: 'TEST'
     *         start with TEST: '_TEST'
     *         equals TEST: '_eq_TEST'
     *         not equals TEST: '_ne_TEST'
     */
    public boolean testString(FValue flt, String property, String databaseValue) {
        String propertyValue = flt.getAsString(property);
        String prefix = flt.getPrefix(property);
        // any test with empty operand passes
        if (emptyOperand(propertyValue) && !prefix.equals(FValue.EMPTY))
            return true;
        // any non-empty test with empty database value
        if ((databaseValue == null || "".equals(databaseValue)) && !prefix.equals(FValue.RE)){
            if (prefix.equals(FValue.EMPTY))
                return true;
            return prefix.equals(FValue.NE);
        } else {
            if (prefix.equals(FValue.EMPTY))
                return false;
        }

        String operand = propertyValue.toLowerCase(Locale.ENGLISH);

        // Not equals
        if (prefix.equals(FValue.NE)) {
            return !databaseValue.toLowerCase(Locale.ENGLISH).equals(operand);
        }

        // Equals
        if (prefix.equals(FValue.EQ)) {
            return databaseValue.toLowerCase(Locale.ENGLISH).equals(operand);
        }

        // Regular expression
        if (prefix.equals(FValue.RE)) {
            try {
                if (databaseValue == null)
                    databaseValue = "";
                Pattern p = Pattern.compile(propertyValue);
                Matcher matcher = p.matcher(databaseValue);
                return matcher.matches();

            } catch (PatternSyntaxException pse) {
                return false;
            }
        }

        // Start with
        if (prefix.equals(FValue.SUB)) {
            return databaseValue.toLowerCase(Locale.ENGLISH).startsWith(operand);
        }

        // Contains
        return databaseValue.toLowerCase(Locale.ENGLISH).indexOf(operand) != -1;
    }

    /**
     * Проверяет значение URL на соответствие фильтру
     *
     * @param flt      параметры фильтрации
     * @param property свойство
     * @param strValue значение из бд
     * @return TRUE если URL удовлетворяет условиям фильтрации, FALSE если нет
     */
    // todo Технически здесь можно сделать нормальную обработку URL и сравнивать не по equals и contains, а по доменам и страницам
    public boolean testURL(FValue flt, String property, String strValue) {
        String propertyValue = flt.getAsString(property);

        // any test with empty operand passes
        if (emptyOperand(propertyValue))
            return true;

        String url = "";
        String prefix = flt.getPrefix(property);
        if (strValue != null) {
            int j = strValue.indexOf('\n');
            if (j > 1 && j < strValue.length() - 1) {
                url = strValue.substring(0, j);

            } else if (j == 0)
                url = "";
            else
                url = strValue;
        }

        // any non-empty test with empty database value
        if (url == null || "".equals(url))
            return prefix.equals(FValue.NE);

        String operand = propertyValue.toLowerCase(Locale.ENGLISH);

        // Not equals
        if (prefix.equals(FValue.NE)) {
            return !url.toLowerCase(Locale.ENGLISH).equals(operand);
        }

        // Equals
        if (prefix.equals(FValue.EQ)) {
            return url.toLowerCase(Locale.ENGLISH).equals(operand);
        }

        // Start with
        if (prefix.equals(FValue.SUB)) {
            return url.toLowerCase(Locale.ENGLISH).startsWith(operand);
        }

        // Contains
        return url.toLowerCase(Locale.ENGLISH).indexOf(operand) != -1;
    }

    /**
     * Проверяет дробное значение на соответствие фильтру
     *
     * @param flt           параметры фильтрации
     * @param property      свойство
     * @param databaseValue значение из бд
     * @return TRUE если число удовлетворяет условиям фильтрации, FALSE если нет
     *         <p/>
     *         Условные обозначения:
     *         <=5: '5'
     *         >=5: '_5'
     *         <>5: '_ne_5'
     *         ==5: '_eq_5'
     */
    public boolean testFloat(FValue flt, String property, Double databaseValue) {
        String propertyValue = flt.getAsString(property);

        // any test with empty operand passes
        if (emptyOperand(propertyValue))
            return true;

        // any non-empty test with empty database value
        String prefix = flt.getPrefix(property);
        if (databaseValue == null)
            return prefix.equals(FValue.NE);

        if (prefix.equals(FValue.IN)){
            List<Float> list = new ArrayList<Float>();
            Character[] characters = {' ', '-', ';'};
            for (Character c: characters)
                propertyValue = propertyValue.replaceAll(c.toString(), " ");
            StringTokenizer st = new StringTokenizer(propertyValue, " ", false);
            while (st.hasMoreTokens())
                list.add(Float.parseFloat((st.nextToken().replace(',','.'))));
            for (Float operand: list)
                if (StrictMath.abs(databaseValue.floatValue() - operand) < (float) 0.0000001)
                    return true;
            return false;
        }

        float operand = Float.parseFloat(propertyValue);

        // Not equals
        if (prefix.equals(FValue.NE)) {
            return StrictMath.abs(databaseValue.floatValue() - operand) > (float) 0.0000001;
        }

        // Equals
        if (prefix.equals(FValue.EQ)) {
            return StrictMath.abs(databaseValue.floatValue() - operand) < (float) 0.0000001;
        }

        // Greater or equals (>=)
        if (prefix.equals(FValue.SUB)) {
            return StrictMath.abs(databaseValue.floatValue() - operand) < (float) 0.0000001 || databaseValue.floatValue() < operand;
        }

        // Less or equals (<=)
        return StrictMath.abs(databaseValue.floatValue() - operand) < (float) 0.0000001 || databaseValue.floatValue() > operand;
    }

    /**
     * Проверяет значение checkbox  на соответствие фильтру
     *
     * @param flt           параметры фильтрации
     * @param property      свойство
     * @param databaseValue значение из бд
     * @return TRUE если checkbox удовлетворяет условиям фильтрации, FALSE если нет
     */
    public boolean testCheckBox(FValue flt, String property, boolean databaseValue) {
        String propertyValue = flt.getAsString(property);

        if (propertyValue == null)
            return true;

        return databaseValue;
    }


    /**
     * Проверяет значение даты на соответствие фильтру
     *
     * @param flt           параметры фильтрации
     * @param property      свойство
     * @param databaseValue значение из бд
     * @return TRUE если дата удовлетворяет условиям фильтрации, FALSE если нет
     */
    public boolean testTimestamp(FValue flt, String property, Calendar databaseValue) {
        // period
        String periodValue = flt.getAsString(FValue.PERIOD + property);
        // amount of days, hours, minutes, etc
        String amountValue = flt.getAsString(FValue.AMNT + property);

        if (periodValue != null && !"0".equals(periodValue)) {
            if (databaseValue == null) {
                return false;
            }
            GregorianCalendar now = new GregorianCalendar();
            now.setTime(new Date());
            //  week
            if ("1".equals(periodValue)) {
                return now.get(Calendar.YEAR) == databaseValue.get(Calendar.YEAR)
                        && now.get(Calendar.WEEK_OF_YEAR) == databaseValue.get(Calendar.WEEK_OF_YEAR);
            }
            //  month
            if ("2".equals(periodValue)) {
                return now.get(Calendar.YEAR) == databaseValue.get(Calendar.YEAR)
                        && now.get(Calendar.MONTH) == databaseValue.get(Calendar.MONTH);
            }
            //  year
            if ("3".equals(periodValue)) {
                return now.get(Calendar.YEAR) == databaseValue.get(Calendar.YEAR);
            }
            //  quarter
            if ("4".equals(periodValue)) {
                return now.get(Calendar.YEAR) == databaseValue.get(Calendar.YEAR)
                        && Math.abs(now.get(Calendar.MONTH)/3) == Math.abs(databaseValue.get(Calendar.MONTH)/3);
            }
            //  prev week
            if ("5".equals(periodValue)) {
                now.set(Calendar.WEEK_OF_YEAR, now.get(Calendar.WEEK_OF_YEAR) - 1);
                return now.get(Calendar.YEAR) == databaseValue.get(Calendar.YEAR)
                        && now.get(Calendar.WEEK_OF_YEAR) == databaseValue.get(Calendar.WEEK_OF_YEAR);
            }
            //  prev month
            if ("6".equals(periodValue)) {
                now.set(Calendar.MONTH, now.get(Calendar.MONTH) - 1);
                return now.get(Calendar.YEAR) == databaseValue.get(Calendar.YEAR)
                        && now.get(Calendar.MONTH) == databaseValue.get(Calendar.MONTH);
            }
            //  prev year
            if ("7".equals(periodValue)) {
                now.set(Calendar.YEAR, now.get(Calendar.YEAR) - 1);
                return now.get(Calendar.YEAR) == databaseValue.get(Calendar.YEAR);
            }
            //  prev quarter
            if ("8".equals(periodValue)) {
                    return now.get(Calendar.YEAR) == databaseValue.get(Calendar.YEAR)
                            && Math.abs(now.get(Calendar.MONTH)/3) - 1 == Math.abs(databaseValue.get(Calendar.MONTH)/3);
            }
            //  next week
            if ("9".equals(periodValue)) {
                now.set(Calendar.WEEK_OF_YEAR, now.get(Calendar.WEEK_OF_YEAR) + 1);
                return now.get(Calendar.YEAR) == databaseValue.get(Calendar.YEAR)
                        && now.get(Calendar.WEEK_OF_YEAR) == databaseValue.get(Calendar.WEEK_OF_YEAR);
            }
            //  next month
            if ("10".equals(periodValue)) {
                now.set(Calendar.MONTH, now.get(Calendar.MONTH) + 1);
                return now.get(Calendar.YEAR) == databaseValue.get(Calendar.YEAR)
                        && now.get(Calendar.MONTH) == databaseValue.get(Calendar.MONTH);
            }
            //  next year
            if ("11".equals(periodValue)) {
                now.set(Calendar.YEAR, now.get(Calendar.YEAR) + 1);
                return now.get(Calendar.YEAR) == databaseValue.get(Calendar.YEAR);
            }
            //  next quarter
            if ("12".equals(periodValue)) {
                    return now.get(Calendar.YEAR) == databaseValue.get(Calendar.YEAR)
                            && Math.abs(now.get(Calendar.MONTH)/3) + 1 == Math.abs(databaseValue.get(Calendar.MONTH)/3);
            }
        } else {
            // заполнено количество дней для before/after - это имеет приоритет
            if (amountValue != null) {
                // в базе данных null - элемент не проходит в любом случае
                if (databaseValue == null) {
                    return false;
                }
                // 0 - hours, 1 - days, 2 - months
                String intervalValue = flt.getAsString(FValue.INTERVAL + property);
                // 0 - before
                String beforeAfterValue = flt.getAsString(FValue.BA + property);
                // 0 - early, 1 - later
                String earlyLaterValue = flt.getAsString(FValue.EL + property);
                // количество в before/after заполнено
                if (intervalValue != null && beforeAfterValue != null && earlyLaterValue != null) {
                    int amount = Integer.parseInt(amountValue);
                    GregorianCalendar now = new GregorianCalendar();
                    now.setTime(new Date());
                    GregorianCalendar tmpDate = new GregorianCalendar();
                    tmpDate.setTime(databaseValue.getTime());

                    if ("0".equals(beforeAfterValue)) { // before
                        amount = -amount;
                    }
                    if ("0".equals(intervalValue)) { // hours
                        now.add(Calendar.MINUTE, amount);
                    }
                    if ("1".equals(intervalValue)) { // days
                        now.add(Calendar.HOUR, amount);
                    }
                    if ("2".equals(intervalValue)) { // month
                        now.add(Calendar.DAY_OF_YEAR, amount);
                    }
                    if ("3".equals(intervalValue)) { // month
                        now.add(Calendar.MONTH, amount);
                    }
                    if ("0".equals(earlyLaterValue)) { // or earlier
                        return tmpDate.before(now) || tmpDate.equals(now);
                    }
                    if ("1".equals(earlyLaterValue)) { // or later
                        return tmpDate.after(now) || tmpDate.equals(now);
                    }
                    log.error("Should never reach");
                    return true;
                }
            } else {    // from/to заполнены
                // if propertyValue start with with _ "from date", else "to date";
                String propertyFromValue = flt.getAsString(FValue.SUB + property);
                String propertyToValue = flt.getAsString(property);
                // we has not empty filter and empty data - hide data
                if (propertyToValue != null && !"".equals(propertyToValue) && databaseValue == null)
                    return false;
                if (propertyFromValue != null && !"".equals(propertyFromValue) && databaseValue == null)
                    return false;
                boolean toPassed = true;
                if (propertyToValue != null && !"".equals(propertyToValue) && databaseValue != null) { // to date
                    toPassed = databaseValue.getTimeInMillis() <= new Timestamp(Long.valueOf(propertyToValue)).getTime();
                }
                boolean fromPassed = true;
                if (propertyFromValue != null && !"".equals(propertyFromValue) && databaseValue != null) {  // from date
                    fromPassed = databaseValue.getTimeInMillis() >= new Timestamp(Long.valueOf(propertyFromValue)).getTime();
                }
                return fromPassed && toPassed;
            }
        }
        log.error("Should never reach - 2");
        return true;
    }

    /**
     * Проверяет список значений на соответствие фильтру
     *
     * @param flt           параметры фильтрации
     * @param property      свойство
     * @param databaseValue значение из бд
     * @return TRUE если список значений удовлетворяет условиям фильтрации, FALSE если нет
     *         <p/>
     *         Условные обозначения
     *         is All: '0'
     *         is not All: 0
     *         is new: '1'
     *         is not new: '_1'
     *         new&processed: '1&2'
     */
    public boolean testList(FValue flt, String property, Object databaseValue) {
        List<String> propertyValue = flt.get(property);

        // is All
        if (propertyValue == null || propertyValue.isEmpty() || (propertyValue.size() == 1 && "0".equals(propertyValue.get(0))))
            return true;

        String prefix = flt.getPrefix(property);

        if (prefix.equals(FValue.SUB)) {
            return !valueInList(propertyValue, databaseValue);  // Test for not is (NOT AND) condition (is not bug and is not task)
        } else {
            return valueInList(propertyValue, databaseValue);  // Test for is (OR) condition (bug or task)
        }
    }

    /**
     * Проверяет множественный список значений на соответствие фильтру
     *
     * @param flt           параметры фильтрации
     * @param property      свойство
     * @param databaseValue значение из бд
     * @return TRUE если множественный список значений удовлетворяет условиям фильтрации, FALSE если нет
     */
    public boolean testMultiList(FValue flt, String property, Object databaseValue, String userId) throws GranException {

        List<String> propertyValue = flt.get(property);
        if (propertyValue == null || propertyValue.size() == 0 || "0".equals(propertyValue.get(0)))
            return true;

        String prefix = flt.getPrefix(property);
        ArrayList<String> list = new ArrayList<String>();

        if (databaseValue instanceof Collection)  {
            Collection database = (Collection) databaseValue;
            list.addAll(database);
        }

        if (prefix.equals(FValue.SUB)) {
            //Test for not is condition (not (bug AND task) = not bug OR not task)
            return !valuesInList(propertyValue, list, userId);
        } else {
            // Test for is AND condition (bug AND task)
            return valuesInList(propertyValue, list, userId);
        }
    }

    /**
     * Проверяет активного ответственного группы на соответствие фильтру
     *
     * @param flt           параметры фильтрации
     * @param property      свойство
     * @param handlerGroup  ответственный группы
     * @param databaseValue значение из бд
     * @return TRUE если ответственный удовлетворяет условиям фильтрации, FALSE если нет
     */
    public boolean testActiveGroupHandler(FValue flt, String property, String handlerGroup, ArrayList<String> databaseValue) {

        List<String> propertyValue = flt.get(property);
        if (propertyValue == null || propertyValue.size() == 0 || (propertyValue.size() == 1 && "0".equals(propertyValue.get(0))))
            return true;
        ArrayList<String> propColl = new ArrayList<String>();
        propColl.add(handlerGroup);
        String prefix = flt.getPrefix(property);

        if (prefix.equals(FValue.SUB)) {
            return !valuesInList(propColl, databaseValue);
        } else {
            return valuesInList(propColl, databaseValue);
        }
    }

    /**
     * Проверяет список пользователей на соответствие фильтру
     *
     * @param flt           параметры фильтрации
     * @param property      свойство
     * @param databaseValue значение из бд
     * @param currentUser   текущий пользователь
     * @return TRUE если список пользователей удовлетворяет условиям фильтрации, FALSE если нет
     * @throws GranException при необходимости
     */
    public boolean testUserList(FValue flt, String property, Object databaseValue, String currentUser) throws GranException {
        List<String> propertyValue = flt.get(property);
        // is All
        if (propertyValue == null || propertyValue.size() == 0 || (propertyValue.size() == 1 && "0".equals(propertyValue.get(0))))
            return true;

        String prefix = flt.getPrefix(property);

        if (prefix.equals(FValue.SUB)) {
            if (databaseValue == null) return true;
            ArrayList<String> list = (ArrayList<String>) databaseValue;
            return valuesOutList(propertyValue, list, currentUser);
        } else {
            if (databaseValue == null) return false;
            List<String> list = (List<String>) databaseValue;
            for (String value : list) {
                if (testUser(flt, property, value, currentUser))
                    return true;
            }
            return valuesInList(propertyValue, list);
        }
    }

    /**
     * Проверяет пользователя на соответствие фильтру
     *
     * @param flt           параметры фильтрации
     * @param property      свойство
     * @param databaseValue значение из бд
     * @param userid        пользователь
     * @return TRUE если пользователь удовлетворяет условиям фильтрации, FALSE если нет
     * @throws GranException при необходимости
     */
    public boolean testUser(FValue flt, String property, String databaseValue, String userid) throws GranException {
        List<String> propertyValue = flt.get(property);
        // is All
        if (propertyValue == null || propertyValue.size() == 0 || (propertyValue.size() == 1 && propertyValue.contains("0")))
            return true;
        String prefix = flt.getPrefix(property);
        if (databaseValue == null && propertyValue.size() >= 1) {
            if (propertyValue.contains("null")) {
                return !prefix.equals(FValue.SUB);
            } else {
                if (prefix.equals(FValue.SUB)) {
                    return true;
                }
            }
        }
        if (databaseValue == null)
            return false;
        if (!prefix.equals(FValue.SUB)) {
            return testUserParams(propertyValue, databaseValue, userid);
        } else {
            return !testUserParams(propertyValue, databaseValue, userid);
        }
    }

    /**
     * Проверяет параметры пользователя на соответствие фильтру
     *
     * @param propertyValue параметры пользователя
     * @param userid        пользователь
     * @param databaseValue значение из бд
     * @return TRUE если параметры пользователя удовлетворяет условиям фильтрации, FALSE если нет
     * @throws GranException при необходимости
     */
    private boolean testUserParams(List<String> propertyValue, String databaseValue, String userid) throws GranException {
        if (propertyValue.indexOf("CurrentUserID") != -1 && userid.equals(databaseValue))
            return true;
        if (propertyValue.indexOf("IandSubUsers") != -1 && (userid.equals(databaseValue) || KernelManager.getUser().isParentOf(userid, databaseValue)))
            return true;
        if (propertyValue.indexOf("IandManager") != -1) {
            if (userid.equals(databaseValue) || !userid.equals("1") && KernelManager.getFind().findUser(userid).getManager().getId().equals(databaseValue))
                return true;
        }
        if (propertyValue.indexOf("IandManagers") != -1 && (userid.equals(databaseValue) || KernelManager.getUser().getUserChain(databaseValue, userid) != null))
            return true;
        return valueInList(propertyValue, databaseValue);
    }

    /**
     * Проверяет наличие значение в списке
     *
     * @param propColl      список значений
     * @param databaseValue значение
     * @return TRUE - если в списке, FALSE - нет
     */
    private boolean valueInList(List<String> propColl, Object databaseValue) {
        for (String it : propColl) {
            if ("0".equals(it)) {
                return true; // if we select "All"
            }
            // None has been selected in the filter (available for resolutions, for ex).
            // or value in filter equals value in database
            if ("null".equals(it) && databaseValue == null || it.equals(databaseValue))
                return true;
        }
        return false;
    }

    /**
     * Проверяет наличие значени1 в списке
     *
     * @param propColl      список значений
     * @param databaseValue значениz
     * @return TRUE - если в списке, FALSE - нет
     */
    private boolean valuesInList(List<String> propColl, ArrayList<String> databaseValue, String userId) throws GranException {
        if (databaseValue == null && propColl.contains("0"))
            return true;

        for (String param : propColl) {
            if ("null".equals(param) && (databaseValue == null || databaseValue.isEmpty()))
                return true;
            if (param.equals("CurrentUserID") && databaseValue.contains(userId))
                return true;
            for(String val: databaseValue) {
                if (param.equals("IandSubUsers") && (databaseValue.contains(userId) || KernelManager.getUser().isParentOf(userId, val)))
                    return true;
                if (param.equals("IandManager")) {
                    if (databaseValue.contains(userId) || !userId.equals("1") && databaseValue.contains(KernelManager.getFind().findUser(userId).getManager().getId()))
                        return true;
                }
                if (param.equals("IandManagers") && (databaseValue.contains(userId) || KernelManager.getUser().getUserChain(val, userId) != null))
                    return true;
                /*if (param.equals("GROUP_MyActiveGroup") && KernelManager.getFind().findUser(userId).getPrstatus().getId().equals(KernelManager.getFind().findUser(val).getPrstatus().getId()))
                    return true;
                if (param.indexOf("GROUP_") != -1) {
                    param = param.substring("GROUP_".length(),param.length());
                    if (KernelManager.getFind().findUser(val).getPrstatus().getId().equals(param))
                        return true;
                }
                */
            }
            if (databaseValue.contains(param)) {
                return true;
            }
        }
        return false;
    }

    private boolean valuesInList(List<String> propColl, List<String> databaseValue) {
        // If we search for All items and found null in database - pass the filter
        if (databaseValue == null && propColl.contains("0"))
            return true;

        // If we search for any except All and found null in database - skip the item

        // if we search A or B, but database contains only A - we should return true;
        // OK when at least one required value find in the database
        // 0 was processed before
        for (String param : propColl) {
            if ("null".equals(param) && (databaseValue == null || databaseValue.isEmpty()))
                return true;
            if (databaseValue != null && databaseValue.contains(param)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Проверяет пустое значение или нет
     *
     * @param propertyValue значение
     * @return TRUE - пустое, FALSE - нет
     */
    private boolean emptyOperand(String propertyValue) {
        return propertyValue == null || "".equals(propertyValue) || FValue.SUB.equals(propertyValue) || FValue.NE.equals(propertyValue) || FValue.EQ.equals(propertyValue) || FValue.RE.equals(propertyValue);
    }

    private boolean valuesOutList(List<String> propColl, ArrayList<String> databaseValue, String currentUser) {
        for (String param: propColl) {
            if (param.indexOf("CurrentUserID") != -1) param = currentUser;
            if (databaseValue.contains(param)) {
                return false;
            }
        }
        return true;
    }
}
