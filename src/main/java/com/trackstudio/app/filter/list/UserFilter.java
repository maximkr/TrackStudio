package com.trackstudio.app.filter.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.filter.AbstractFilter;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.UserFValue;
import com.trackstudio.app.filter.UserPreFilter;
import com.trackstudio.app.filter.comparator.UserComparator;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredUserBean;

import net.jcip.annotations.Immutable;

/**
 * Класс содержит методы для фильтрации пользователей
 */
@Immutable
public class UserFilter extends AbstractFilter {
    private static final Log log = LogFactory.getLog(UserFilter.class);
    private final SecuredUserBean user;

    /**
     * Конструктор
     *
     * @param user пользователь
     * @see com.trackstudio.secured.SecuredUserBean
     */
    public UserFilter(SecuredUserBean user) {
        this.user = user;
    }

    /**
     * Проверяет соответствие пользователя условиям фильтрации
     *
     * @param tci пользователь
     * @param flt параметры фильтрации
     * @return TRUE - соответствует, FALSE - нет
     * @throws GranException при необходимости
     */
    public boolean pass(SecuredUserBean tci, UserFValue flt) throws GranException {
        log.trace("##########");
        for (String u : flt.getUseForUser()) {

            boolean needAdd = true;
            if (u.equals(FieldMap.USER_CHILDALLOWED.getFilterKey()) && tci.getChildrenAllowed() != null)
                needAdd = testNumber(flt, FieldMap.USER_CHILDALLOWED.getFilterKey(), tci.getChildrenAllowed());
            if (u.equals(FieldMap.USER_CHILDCOUNT.getFilterKey()))
                needAdd = testNumber(flt, FieldMap.USER_CHILDCOUNT.getFilterKey(), tci.getChildCount());
            else if (u.equals(FieldMap.USER_COMPANY.getFilterKey())) {
                needAdd = testString(flt, FieldMap.USER_COMPANY.getFilterKey(), tci.getCompany());
            } else if (u.equals(FieldMap.USER_EMAIL.getFilterKey()))
                needAdd = testString(flt, FieldMap.USER_EMAIL.getFilterKey(), tci.getEmail());
            else if (u.equals(FieldMap.USER_EXPIREDATE.getFilterKey()))
                needAdd = testTimestamp(flt, FieldMap.USER_EXPIREDATE.getFilterKey(), tci.getExpireDate());
            else if (u.equals(FieldMap.USER_LOCALE.getFilterKey()))
                needAdd = testList(flt, FieldMap.USER_LOCALE.getFilterKey(), tci.getLocale() != null ? tci.getLocale() : null);
            else if (u.equals(FieldMap.USER_LOGIN.getFilterKey()))
                needAdd = testString(flt, FieldMap.USER_LOGIN.getFilterKey(), tci.getLogin());
            else if (u.equals(FieldMap.USER_NAME.getFilterKey()))
                needAdd = testString(flt, FieldMap.USER_NAME.getFilterKey(), tci.getName());
            else if (u.equals(FieldMap.USER_STATUS.getFilterKey()))
                needAdd = testList(flt, FieldMap.USER_STATUS.getFilterKey(), tci.getPrstatusId() != null ? tci.getPrstatusId() : null);
            else if (u.equals(FieldMap.USER_TEL.getFilterKey()))
                needAdd = testString(flt, FieldMap.USER_TEL.getFilterKey(), tci.getTel());
            else if (u.equals(FieldMap.USER_ACTIVE.getFilterKey()))
                needAdd = testList(flt, FieldMap.USER_ACTIVE.getFilterKey(), tci.isEnabled() ? "True" : "False");
            else if (u.equals(FieldMap.USER_TIMEZONE.getFilterKey()))
                needAdd = testList(flt, FieldMap.USER_TIMEZONE.getFilterKey(), tci.getTimezone() != null ? tci.getTimezone() : null);
            if (!needAdd)
                return false;
        }
        return passUdf(flt, tci);
    }

    /**
     * Возвращает список отфильтрованных пользователей
     *
     * @param flt         параметры фильтрации
     * @param withUDF     Нужно ли фильтровать
     * @param withSubUSer Искать ли вложенных пользователей
     * @param sortorder   порядок сортировки
     * @return список пользователей
     * @throws GranException при необзодимости
     */
    public ArrayList<SecuredUserBean> getList(UserFValue flt, boolean withUDF, boolean withSubUSer, List<String> sortorder) throws GranException {
        try {
            log.trace("##########");

            ArrayList udfHash = null;
            if (withUDF)
                udfHash = user.getUDFs();

            ArrayList<SecuredUserBean> list = new ArrayList<SecuredUserBean>();
            List<String> children;
            boolean notwithsub = flt.get(FValue.DEEPSEARCH) == null && !withSubUSer;
            if (notwithsub) {
                children = user.getAllowedChildrenIdOnly();
            } else {
                children = user.getAllowedUserAndChildrenListIdOnly();
                children.remove(user.getId());
            }

            UserPreFilter upf = new UserPreFilter(flt, user.getSecure().getUserId());
            Set<String> childrenSet = upf.filterUsers(new TreeSet<String>(children), user.getId());

            for (String id : childrenSet) {
                SecuredUserBean item = new SecuredUserBean(id, user.getSecure());
                if (pass(item, flt)) {
                    list.add(item);
                }
            }
            List<String> sortstring = sortorder != null && sortorder.size() != 0 ? sortorder : flt.getSortOrder();
            Collections.sort(list, new UserComparator(sortstring, udfHash));
            return list;
        } catch (Exception e) {
            throw new GranException(e);
        }
    }
}
