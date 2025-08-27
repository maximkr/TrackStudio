package com.trackstudio.secured;

import java.util.ArrayList;
import java.util.Set;

import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.UserFValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.app.session.SessionManager;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.soap.bean.UserFvalueBean;

import net.jcip.annotations.Immutable;

/**
 * Bean which represents user filtering criteria
 */
@Immutable
public class SecuredUserFValueBean extends SecuredFValueBean {

    public SecuredUserFValueBean(FValue f, SessionContext sc) {
        super(f, sc);
    }


    public boolean canManage() throws GranException {
        return true;
    }

    public SecuredUserFValueBean(UserFvalueBean f, String sessionId) throws GranException {
        this(new UserFValue(), SessionManager.getInstance().getSessionContext(sessionId));
        map.put(FieldMap.USER_CHILDALLOWED.getFilterKey(), f.getChildAllowed());
        map.put(FieldMap.USER_CHILDCOUNT.getFilterKey(), f.getChildCount());
        map.put(FieldMap.USER_COMPANY.getFilterKey(), f.getCompany());
        map.put(FValue.DEEPSEARCH, f.getDeepSearch());
        map.put(FieldMap.USER_EMAIL.getFilterKey(), f.getEmail());
        map.put(FieldMap.USER_EXPIREDATE.getFilterKey(), f.getExpiredate());
        map.put(FValue.DISPLAY, f.getDisplay());

        map.put(FieldMap.USER_LOCALE.getFilterKey(), f.getLocale());
        map.put(FieldMap.USER_LOGIN.getFilterKey(), f.getLogin());
        map.put(FieldMap.USER_NAME.getFilterKey(), f.getName());
        map.put(UserFValue.ONPAGE, f.getOnPage());
        map.put(FieldMap.USER_STATUS.getFilterKey(), f.getPrstatus());
        map.put(FValue.SORTORDER, f.getSortOrder());
        map.put(FieldMap.USER_TEL.getFilterKey(), f.getTel());
        map.put(FieldMap.USER_TIMEZONE.getFilterKey(), f.getTimezone());
        map.put(FValue.UDF, f.getUdf());
        map.put(FValue.UDF_SORT, f.getUdfSort());


    }

    public UserFValue getFValue() {
        UserFValue fv = new UserFValue();
        fv.putAll(this.map);
        return fv;
    }

    public UserFvalueBean getSOAP() {
        UserFvalueBean bean = new UserFvalueBean();
        bean.setChildAllowed(getStirngValueFromMap(FieldMap.USER_CHILDALLOWED.getFilterKey()));
        bean.setChildCount(getStirngValueFromMap(FieldMap.USER_CHILDCOUNT.getFilterKey()));
        bean.setCompany(getStirngValueFromMap(FieldMap.USER_COMPANY.getFilterKey()));
        bean.setDeepSearch(getStirngValueFromMap(FValue.DEEPSEARCH));
        bean.setEmail(getStirngValueFromMap(FieldMap.USER_EMAIL.getFilterKey()));

        bean.setExpiredate(getStirngValueFromMap(FieldMap.USER_EXPIREDATE.getFilterKey()));
        bean.setDisplay(getStirngValueFromMap(FValue.DISPLAY));
        bean.setLocale(getStirngValueFromMap(FieldMap.USER_LOCALE.getFilterKey()));
        bean.setLogin(getStirngValueFromMap(FieldMap.USER_LOGIN.getFilterKey()));
        bean.setName(getStirngValueFromMap(FieldMap.USER_NAME.getFilterKey()));
        bean.setOnPage(getStirngValueFromMap(UserFValue.ONPAGE));
        bean.setPrstatus(getStirngValueFromMap(FieldMap.USER_STATUS.getFilterKey()));
        bean.setSortOrder(getStirngValueFromMap(FValue.SORTORDER));
        bean.setTel(getStirngValueFromMap(FieldMap.USER_TEL.getFilterKey()));
        bean.setTimezone(getStirngValueFromMap(FieldMap.USER_TIMEZONE.getFilterKey()));


        ArrayList<String> udfList = new ArrayList<String>();
        ArrayList<String> udfSortList = new ArrayList<String>();

        for (String key : (Set<String>) map.keySet()) {
            if (key.startsWith(FValue.UDF))
                if (key.startsWith(FValue.UDF_SORT))
                    udfSortList.add(key);
                else
                    udfList.add(key);
        }
        bean.setUdf(udfList.toArray(new String[]{}));
        bean.setUdfSort(udfSortList.toArray(new String[]{}));
        return bean;
    }

}
