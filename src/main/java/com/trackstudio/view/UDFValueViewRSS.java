package com.trackstudio.view;

import java.util.ArrayList;
import java.util.Calendar;
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
import com.trackstudio.tools.Null;
import com.trackstudio.tools.formatter.DateFormatter;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.Immutable;

@Immutable
public class UDFValueViewRSS extends UDFValueView {

    public UDFValueViewRSS(SecuredUDFValueBean udfValue) {
        this.udfValue = udfValue;
    }

    public String getValue(Secured o) throws GranException {
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
                    return UDFFormFillHelper.listToString((List) va, ", ");
                case UdfValue.TASK: {
                    List<String> sortedSet = (List<String>) va;
                    ArrayList<SecuredTaskBean> set = TaskComparator.sort(sortedSet, sc);
                    if (set == null)
                        return "";
                    StringBuffer sb = new StringBuffer();
                    for (Iterator<SecuredTaskBean> it = set.iterator(); it.hasNext();) {
                        SecuredTaskBean t = it.next();
                        TaskView v = new TaskViewText(t);
                        sb.append(v.getNumber());
                        if (it.hasNext())
                            sb.append(", ");
                    }
                    return sc.toString();
                }
                case UdfValue.USER: {
                    List<String> sortedSet = (List<String>) va;
                    ArrayList<SecuredUserBean> set = UserComparator.sort(sortedSet, sc);
                    if (set == null)
                        return "";
                    StringBuffer sb = new StringBuffer();
                    for (Iterator<SecuredUserBean> it = set.iterator(); it.hasNext();) {
                        SecuredUserBean u = it.next();
                        UserView v = new UserViewText(u);
                        sb.append(v.getName());
                        if (it.hasNext()) {
                            sb.append(", ");
                        }
                    }
                    return sb.toString();
                }
                case UdfValue.URL:
                    return getURLView(va);
                case UdfValue.MEMO:
                    String value = va.toString();
                    if (!udfValue.isHtmlview()) {
                        return value;
                    } else {
                        value = HTMLEncoder.stripHtmlTags(value);
                        return Null.stripNullText(value);
                    }
                default:
                    return va.toString();
            }
        } else {
            return "";
        }
    }

    protected String getURLView(Object va) {
        return urlUDFToText(va.toString());
    }

    public String getCaption() {
        return udfValue.getCaption();
    }

}
