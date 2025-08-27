package com.trackstudio.app.filter.comparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUserBean;

import net.jcip.annotations.Immutable;

/**
 * Спенциальный компаратор, предназначен для сравнения пользователей
 */
@Immutable
public class UserComparator extends AbstractComparator {

    private static final Log log = LogFactory.getLog(UserComparator.class);

    /**
     * Конструкторп по умолчанию
     *
     * @param sortOrder порядок сортировки
     * @param udfHash   карта пользовательских полей
     */
    public UserComparator(List<String> sortOrder, ArrayList<SecuredUDFBean> udfHash) {
        super(sortOrder, udfHash, FValue.SUB + FieldMap.USER_LOGIN.getFieldKey());
    }

    /**
     * Метод для основной сортировки
     *
     * @param ids список id пользователей для сортировки
     * @param sc  сессия пользователя
     * @return Отсортированный список пользователей
     * @throws GranException при необходимости
     */
    public static ArrayList<SecuredUserBean> sort(Collection ids, SessionContext sc) throws GranException {
        if (ids.isEmpty()) return null;
        ArrayList<SecuredUserBean> list = new ArrayList<SecuredUserBean>();
        for (Object id : ids) {
            SecuredUserBean t = new SecuredUserBean(id.toString(), sc);
            list.add(t);
        }
        ArrayList<String> s = new ArrayList<String>();
        s.add(FValue.SUB + "user_login");
        Collections.sort(list, new UserComparator(s, null));
        return list;
    }

    /**
     * Метод сравнения двух объектов, если один из них null
     *
     * @param a первый обхект
     * @param b второй объект
     * @return +1,0 или -1
     */
    private int compareNulls(Object a, Object b) {
        if (a == null && b != null)
            return 1;
        if (a != null && b == null)
            return -1;
        return 0;
    }

    /**
     * Сравнивает два статуса
     *
     * @param p1 первый
     * @param p2 второй
     * @return +1, 0 или -1
     */
    private int comparePrstatus(SecuredPrstatusBean p1, SecuredPrstatusBean p2) {
        if (p1 == null || p2 == null)
            return compareNulls(p1, p2);
        return p1.getName().compareTo(p2.getName());
    }

    /**
     * Сравнивает два объекта текущего класса
     *
     * @param o1 первый объект
     * @param o2 второй объект
     * @return +1,0 или -1
     */
    public int compare(Object o1, Object o2) {
        int retVal = 0;
        SecuredUserBean tci1 = (SecuredUserBean) o1;
        SecuredUserBean tci2 = (SecuredUserBean) o2;
        try {
            for (Iterator it = sortedOrder.iterator(); it.hasNext() && retVal == 0;) {
                String field = (String) it.next();
                if (field.equals(FieldMap.USER_CHILDALLOWED.getFieldKey()))
                    retVal = super.compare(tci1.getChildrenAllowed(), tci2.getChildrenAllowed());

                if (field.equals(FieldMap.USER_CHILDCOUNT.getFieldKey()))
                    retVal = super.compare(tci1.getChildCount(), tci2.getChildCount());

                if (field.equals(FieldMap.USER_COMPANY.getFieldKey()))
                    retVal = super.compare(tci1.getCompany(), tci2.getCompany());

                if (field.equals(FieldMap.USER_EMAIL.getFieldKey()))
                    retVal = super.compare(tci1.getEmail(), tci2.getEmail());

                if (field.equals(FieldMap.FULLPATH.getFieldKey())) {
                    List list1 = UserRelatedManager.getInstance().getUserIdChain(null, tci1.getId());
                    List list2 = UserRelatedManager.getInstance().getUserIdChain(null, tci2.getId());
                    Iterator it1 = list1.iterator();
                    Iterator it2 = list2.iterator();
                    while (true) {
                        String id1 = (String) it1.next();
                        String id2 = (String) it2.next();
                        if (id1.equals(id2)) {
                            boolean hn1 = it1.hasNext();
                            boolean hn2 = it2.hasNext();
                            if (hn1 && hn2)
                                continue;
                            if (!hn1 && !hn2) {
                                retVal = 0;
                                break;
                            }
                            if (hn1) {
                                retVal = 1;
                                break;
                            } else {
                                retVal = -1;
                                break;
                            }
                        } else {
                            retVal = super.compare(UserRelatedManager.getInstance().find(id1).getName(), UserRelatedManager.getInstance().find(id2).getName());
                            break;
                        }
                    }
                }

                if (field.equals(FieldMap.USER_NAME.getFieldKey()))
                    retVal = super.compare(tci1.getName(), tci2.getName());

                if (field.equals(FieldMap.USER_EXPIREDATE.getFieldKey()))
                    retVal = super.compare(tci1.getExpireDate(), tci2.getExpireDate());

                if (field.equals(FieldMap.USER_LOCALE.getFieldKey()))
                    retVal = super.compare(tci1.getLocale(), tci2.getLocale());

                if (field.equals(FieldMap.USER_LOGIN.getFieldKey()))
                    retVal = super.compare(tci1.getLogin(), tci2.getLogin());

                if (field.equals(FieldMap.USER_STATUS.getFieldKey()))
                    retVal = comparePrstatus(tci1.getPrstatus(), tci2.getPrstatus());

                if (field.equals(FieldMap.USER_TEL.getFieldKey()))
                    retVal = super.compare(tci1.getTel(), tci2.getTel());

                if (field.equals(FieldMap.USER_ACTIVE.getFieldKey()))
                    retVal = super.compare(tci1.isEnabled(), tci2.isEnabled());

                if (field.equals(FieldMap.USER_TIMEZONE.getFieldKey()))
                    retVal = super.compare(tci1.getTimezone(), tci2.getTimezone());
                if (udfs != null && !udfs.isEmpty())
                    retVal = super.compareUdf(field, tci1, tci2, retVal);

                if (!(fieldMap.get(field)))
                    retVal = -retVal;

                if (!field.equals(sortedOrder.get(0))) {
                    if (retVal < 0)
                        retVal = -1;
                    else if (retVal > 0)
                        retVal = 1;
                }
                // log.debug("comparsion by "+ field +" "+tci1.getName() + " and "+tci2.getName()+ " : "+ retVal);
            }
        } catch (Exception e) {
            log.error("Error", e);
        }
        return retVal;
    }
}
