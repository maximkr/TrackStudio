package com.trackstudio.app.filter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;

import com.trackstudio.constants.UdfConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Udf;
import com.trackstudio.model.Udfval;

import net.jcip.annotations.Immutable;

/**
 * Класс для предварительной фильтрации
 */
@Immutable
abstract class PreFilter {

    private static final Log log = LogFactory.getLog(PreFilter.class);
    protected static final LockManager lockManager = LockManager.getInstance();
    /**
     * Максимальное число родителей
     */
    protected static final int MAX_PARENT = 50;

    /**
     * Параметры фильтрации
     */
    protected final FValue fv;

    /**
     * ID текущего пользователя
     */
    protected final String currentUserId;

    /**
     * Конструктор
     *
     * @param fv            параметры фильтрации
     * @param currentUserId ID текущего пользователя
     * @throws GranException при необходимости
     */
    public PreFilter(FValue fv, String currentUserId) throws GranException {
        this.fv = fv;
        this.currentUserId = currentUserId;
        log.debug(fv);
    }

    /**
     * Устанавливает критерии фильтрации
     *
     * @param cr      критерия
     * @param key     значение
     * @param column1 колонка 1
     * @param column2 колонка 2
     * @return TRUE - все удачно, FALSE - не все
     * @throws GranException при необходимости
     */
    protected boolean applyListCriteria(Criteria cr, String key, String column1, String column2) throws GranException {
        List<String> value = fv.get(key);
        String prefix = fv.getPrefix(key);

        boolean processed = false;
        if (!emptyOperand(value)) {

            if (value != null) {
                //dnikitiin: без "alias1" нельзя - под postgreSQL не будет работать: так не работает запрос вида select user.user_id from gr_user as user;
                //cr.createAlias(column1, "alias1" + column1);

                if (prefix.equals(FValue.SUB)) {
                    cr.add(Expression.or(Expression.not(Expression.in(/*"alias1" + */(column1 != null ? column1 + '.' : "") + column2, value)), Expression.isNull(/*"alias1" + */(column1 != null ? column1 + '.' : "") + column2)));
                } else {
                    cr.add(Expression.in(/*"alias1" + */(column1 != null ? column1 + '.' : "") + column2, value));
                }
            }
            processed = true;
        }
        return processed;
    }

    /**
     * Устанавливает критерии фильтрации для списка
     *
     * @param cr      критерия
     * @param key     значение
     * @param column1 колонка 1
     * @param column2 колонка 2
     * @return TRUE - все удачно, FALSE - не все
     * @throws GranException при необходимости
     */
    protected boolean applyMultiListCriteria(Criteria cr, String key, String column1, String column2) throws GranException {
        List<String> value = fv.get(key);
        String prefix = fv.getPrefix(key);
        boolean processed = false;
        if (!emptyOperand(value)) {

            if (value != null) {
                //dnikitiin: без "alias1" нельзя - под postgreSQL не будет работать: так не работает запрос вида select user.user_id from gr_user as user;
                cr.createAlias(column1, "alias1" + column1);

                if (prefix.equals(FValue.SUB)) {
                    // don't process "is not" criteria
                } else {
                    cr.add(Expression.in("alias1" + column1 + '.' + column2, value));
                    processed = true;
                }
            }
        }
        return processed;
    }

    /**
     * Устанавливает критерии фильтрации для дробного числа
     *
     * @param cr     критерия
     * @param key    значение
     * @param column колонка
     * @return TRUE - все удачно, FALSE - не все
     * @throws GranException при необходимости
     */
    protected boolean applyFloatCriteria(Criteria cr, String key, String column) throws GranException {
        String value = fv.getAsString(key);
        String prefix = fv.getPrefix(key);
        log.trace("value='" + value + "', column='" + column);
        boolean processed = false;
        if (!emptyOperand(value)) {
            double operand = Double.parseDouble(value);
            if (prefix.equals(FValue.NE)) {
                cr.add(Expression.or(Expression.not(Expression.between(column, operand - 0.0000001d, operand + 0.0000001d)), Expression.isNull(column)));
            } else if (prefix.equals(FValue.EQ))
                cr.add(Expression.between(column, operand - 0.0000001d, operand + 0.0000001d));
            else if (prefix.equals(FValue.SUB))
                cr.add(Expression.or(Expression.le(column, operand), Expression.isNull(column)));
            else
                cr.add(Expression.ge(column, operand));
            processed = true;
        }
        return processed;
    }

    /**
     * Устанавливает критерии фильтрации для даты
     *
     * @param cr     критерия
     * @param key    значение
     * @param column колонка
     * @return TRUE - все удачно, FALSE - не все
     * @throws GranException при необходимости
     */
    protected boolean applyDateCriteria(Criteria cr, String key, String column) throws GranException {
        log.trace("key='" + key + "', column='" + column);
        boolean processed = false;
        String amountValue = fv.getAsString(FValue.AMNT + key);
        if (amountValue != null) {
            String intervalValue = fv.getAsString(FValue.INTERVAL + key);
            String beforeAfterValue = fv.getAsString(FValue.BA + key);
            String earlyLaterValue = fv.getAsString(FValue.EL + key);
            if (intervalValue != null && beforeAfterValue != null && earlyLaterValue != null) {
                int amount = 0;
                try {
                    amount = Integer.parseInt(amountValue);
                } catch (NumberFormatException nfe) {
                }
                GregorianCalendar now = new GregorianCalendar();
                if (beforeAfterValue.equals("0")) // before
                    amount = -amount;
                if (intervalValue.equals("0")) // minutes
                    now.add(Calendar.MINUTE, amount);
                if (intervalValue.equals("1")) // hours
                    now.add(Calendar.HOUR, amount);
                if (intervalValue.equals("2")) // day
                    now.add(Calendar.DAY_OF_YEAR, amount);
                if (intervalValue.equals("3")) // month
                    now.add(Calendar.MONTH, amount);
                if (earlyLaterValue.equals("0"))//early
                    cr.add(Expression.le(column, now));
                else
                    cr.add(Expression.ge(column, now));
                processed = true;
            }
        } else {
            String propertyFromValue = fv.getAsString(FValue.SUB + key);
            String propertyToValue = fv.getAsString(key);
            if (propertyFromValue != null && propertyFromValue.length() != 0) {
                Calendar from = Calendar.getInstance();
                from.setTimeInMillis((new Long(propertyFromValue)));
                cr.add(Expression.ge(column, from));
                processed = true;
            }

            if (propertyToValue != null && propertyToValue.length() != 0) {
                Calendar to = Calendar.getInstance();
                to.setTimeInMillis((new Long(propertyToValue)));
                cr.add(Expression.le(column, to));
                processed = true;
            }
        }
        return processed;
    }

    /**
     * Устанавливает критерии фильтрации для целого числа
     *
     * @param cr     критерия
     * @param key    значение
     * @param column колонка
     * @return TRUE - все удачно, FALSE - не все
     * @throws GranException при необходимости
     */
    protected boolean applyIntegerCriteria(Criteria cr, String key, String column) throws GranException {
        String value = fv.getAsString(key);
        String prefix = fv.getPrefix(key);
        log.trace("value='" + value + "', column='" + column);
        boolean processed = false;
        if (!emptyOperand(value)) {
            String operand = value.toLowerCase(Locale.ENGLISH);
            if (prefix.equals(FValue.NE))
                cr.add(Expression.or(Expression.ne(column, new Double(operand)), Expression.isNull(column)));
            else if (prefix.equals(FValue.IN))
                cr.add(Expression.in(column, ParseInExpression(operand, prefix)));
            else if (prefix.equals(FValue.EQ))
                cr.add(Expression.eq(column, new Double(operand)));
            else if (prefix.equals(FValue.SUB))
                cr.add(Expression.le(column, new Double(operand)));
            else
                cr.add(Expression.ge(column, new Double(operand)));
            processed = true;
        }
        return processed;
    }

    /**
     * Разбирает значение для выражения
     *
     * @param propertyValue значение
     * @param prefix        префикс
     * @return список значений
     */
    protected ArrayList<Integer> ParseInExpression(String propertyValue, String prefix) {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        if (prefix.equals(FValue.IN)) {
            String operands = propertyValue;
            if (operands.length() == 0) return ret;
            operands = operands.replaceAll(",", " ");
            operands = operands.replaceAll(";", " ");
            StringTokenizer st = new StringTokenizer(operands, " -", true);
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                if (token.compareTo(" ") == 0 || token.compareTo("-") == 0) {  // bad filter string
                    continue;
                }
                int op1 = Integer.parseInt(token);
                if (!st.hasMoreTokens()) {
                    ret.add(op1);
                    return ret;
                }
                token = st.nextToken();
                if (token.compareTo(" ") == 0) { // list of values
                    ret.add(op1);
                } else if (token.compareTo("-") == 0) { // range of values
                    token = st.nextToken();
                    int op2 = Integer.parseInt(token);
                    for (int i = op1; i < op2; ++i)
                        ret.add(i);
                } else {  // bad filter string
                    log.error("Bad input filter string " + propertyValue);
                    return ret;
                }
            }
            return ret;
        }
        return ret;
    }

    /**
     * Возвращает результат фильтрации для критерия
     *
     * @param cr критерия
     * @return список результатов фильтрации
     * @throws GranException при необходимости
     */
    protected Set<String> getResult(Criteria cr) throws GranException {
        log.trace("*****");
        Set<String> tmpResult = new TreeSet<String>();
        cr.setCacheable(false);
        ScrollableResults sr = cr.scroll();
        while (sr.next()) {
            String id = (String) sr.get(0);
            tmpResult.add(id);
        }
        return tmpResult;
    }

    /**
     * Процесс фильтрации пользовательских полей
     *
     * @param parentObjects родительские поля
     * @param allObjects    все поля
     * @return список полей
     * @throws GranException при необходимости
     */
    protected Set<String> processUdfs(List parentObjects, Set allObjects) throws GranException {
        boolean w = lockManager.acquireConnection(PreFilter.class.getSimpleName());
        try {
            Set<String> udfList = fv.getUsedUdfIds();
            log.debug("udfList(" + udfList + ')');
            Set result = null;
            for (String key : udfList) {
                log.debug("Processing UDF, key: " + key);
                int type;
                if (KernelManager.getFind().isUdfExists(key)) {
                    Udf udf = KernelManager.getFind().findUdf(key);
                    type = udf.getType();
                    if (udf.getScript() != null)//калькулируемые udf-ы не обрабатываем
                        continue;
                } else
                    continue;
                //TODO need to rewrite it on HU without use session
                Session sess = lockManager.getDBSession().getSession();
                Criteria cr = sess.createCriteria(Udfval.class, "udfPrj");

                cr.add(Expression.eq("udf.id", key));

                if (fv instanceof TaskFValue) {
                    cr.createAlias("udfsource", "udfsource").createAlias("udfsource.task", "taskzz");
                    cr.setProjection(Projections.projectionList().add(Projections.property("taskzz.id"), "id"));
                    if (parentObjects.size() < MAX_PARENT)
                        cr.add(Expression.in("taskzz.parent.id", parentObjects));
                }

                if (fv instanceof UserFValue) {
                    cr.createAlias("udfsource", "udfsource").createAlias("udfsource.user", "userzz");
                    cr.setProjection(Projections.projectionList().add(Projections.property("userzz.id"), "id"));
                    if (parentObjects.size() < MAX_PARENT)
                        cr.add(Expression.in("userzz.manager.id", parentObjects));
                }
                boolean remove = false;
                boolean processed = false;


                if (type == UdfConstants.INTEGER) {
                    processed = applyIntegerCriteria(cr, key, "num");
                } else if (type == UdfConstants.FLOAT) {
                    processed = applyFloatCriteria(cr, key, "num");
                } else if (type == UdfConstants.LIST) {
                    if (fv.getOriginValues(key) != null && fv.getPrefix(key).equals(FValue.SUB)) {
                        remove = true;
                    }
                    processed = applyListCriteria(cr, key, "udflist", "id");
                } else if (type == UdfConstants.MLIST) {
                    processed = applyMultiListCriteria(cr, key, "udflist", "id");
                } else if (type == UdfConstants.TASK) {
                    processed = applyMultiListCriteria(cr, key, "task", "id");
                } else if (type == UdfConstants.USER) {
                    processed = applyMultiListCriteria(cr, key, "user", "id");
                } else if (type == UdfConstants.DATE) {
                    processed = applyDateCriteria(cr, key, "dat");
                }

                if (processed) {
                    Set<String> s = getResult(cr);
                    if (remove) {
                        allObjects.removeAll(s);
                    }
                    result = fastRetainAll(result, remove ? allObjects : s);
                }
            }
            return result;
        } finally {
            if (w) lockManager.releaseConnection(PreFilter.class.getSimpleName());
        }
    }

    /**
     * Проверяет пустое значение или нет
     *
     * @param value значение
     * @return пустое или нет
     */
    protected boolean emptyOperand(String value) {
        return value == null || value.trim().length() == 0 || value.trim().equals(FValue.SUB) || value.trim().equals(FValue.EQ) || value.trim().equals(FValue.NE);
    }

    /**
     * Проверяет пустой список значений или нет
     *
     * @param value список значений
     * @return пустое или нет
     */
    protected boolean emptyOperand(List<String> value) {
        return value == null || value.size() == 0 || (value.size() == 1 && (value.get(0).equals("0") || value.get(0).equals("")));
    }

    /**
     * Разбирает список значений
     *
     * @param s список значений
     * @return список значений
     * @throws GranException при необходимости
     */
    protected List<String> parseFilterValue(List<String> s) throws GranException {
        List<String> result = new ArrayList<String>();

        for (String str : s) {
            if (str.equals("null")) //none query
                return null;
            if (str.length() != 0) {
                if (str.equals("CurrentUserID") || str.equals("_CurrentUserID")) {
                    result.add(currentUserId);
                } else if (str.equals("IandSubUsers") || str.equals("_IandSubUsers")) {
                    for (String s1 : KernelManager.getUser().getUserAndChildrenListIdOnly(currentUserId))
                        result.add(s1);
                } else if (str.equals("IandManager") || str.equals("_IandManager")) {
                    result.add(currentUserId);
                    if (!currentUserId.equals("1"))
                        result.add(KernelManager.getFind().findUser(currentUserId).getManager().getId());
                } else if (str.equals("IandManagers") || str.equals("_IandManagers")) {
                    for (UserRelatedInfo it : KernelManager.getUser().getUserChain("1", currentUserId))
                        result.add(it.getId());
                } else {
                    result.add(str);
                }
            }
        }
        log.debug("parseFilterValue result(" + result + "), size=" + result.size());
        return result;
    }

    /**
     * Аккумулирует два списка
     *
     * @param s1 один список
     * @param s2 другой список
     * @return новый список
     */
    public static Set<String> fastRetainAll(Set<String> s1, Set<String> s2) {
        if (s1 == null && s2 == null)
            return null;

        if (s1 == null)
            return s2;

        if (s2 == null)
            return s1;

        if (s1.size() < s2.size()) {
            s1.retainAll(s2);
            return s1;
        } else {
            s2.retainAll(s1);
            return s2;
        }
    }


}
