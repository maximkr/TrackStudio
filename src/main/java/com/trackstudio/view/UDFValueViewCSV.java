package com.trackstudio.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.trackstudio.app.UDFFormFillHelper;
import com.trackstudio.app.UdfValue;
import com.trackstudio.app.filter.comparator.TaskComparator;
import com.trackstudio.app.filter.comparator.UserComparator;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.Secured;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.tools.compare.FieldSort;
import com.trackstudio.tools.compare.SortTask;
import com.trackstudio.tools.formatter.DateFormatter;

import net.jcip.annotations.Immutable;

@Immutable
public class UDFValueViewCSV extends UDFValueView {
    public UDFValueViewCSV(SecuredUDFValueBean udfValue) {
        this.udfValue = udfValue;
    }

    protected String getValue(Secured o, boolean path) throws GranException {
        Object va = udfValue.getValue(o);
        SessionContext sc = o.getSecure();
        if (null != va) {
            switch (udfValue.getUdfType()) {
                case UdfValue.DATE:
                    DateFormatter df = new DateFormatter(sc.getTimezone(), sc.getLocale());
                    return df.parse((Calendar) va);
                case UdfValue.FLOAT:
                    return String.valueOf(va);
                case UdfValue.MULTILIST:
                    return UDFFormFillHelper.listToString((List) va, ";");
                case UdfValue.TASK: {
                    List<String> sortedSet = (List<String>) va;
                    List<SecuredTaskBean> set = TaskComparator.sort(sortedSet, sc);
                    if (set == null)
                        return "";
                    Collections.sort(set, new SortTask(FieldSort.NUMBER, true));
                    StringBuffer r = new StringBuffer();
                    for (Iterator<SecuredTaskBean> it = set.iterator(); it.hasNext();) {
                        SecuredTaskBean t = it.next();
                        r.append('#').append(t.getNumber());
                        if (it.hasNext()) r.append(";");
                    }
                    return r.toString();
                }
                case UdfValue.USER:

                {
                    List<String> sortedSet = (List<String>) va;
                    ArrayList<SecuredUserBean> set = UserComparator.sort(sortedSet, sc);
                    if (set == null)
                        return "";
                    StringBuffer r = new StringBuffer();
                    for (Iterator<SecuredUserBean> it = set.iterator(); it.hasNext();) {
                        SecuredUserBean t = it.next();
                        r.append("@").append(t.getLogin());
                        if (it.hasNext()) r.append(";");

                    }

                    return r.toString();
                }
                case UdfValue.URL:
                    return getURLView(va);
                default:
                    return va.toString();
            }
        } else
            return "";
    }

    public String getValue(Secured o) throws GranException {
        return getValue(o, true);
    }

    protected String getURLView(Object va) {
        return urlUDFToText(va.toString());
    }

    public String getCaption() {
        return udfValue.getCaption();
    }

}
