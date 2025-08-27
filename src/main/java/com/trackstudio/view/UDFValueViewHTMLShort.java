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
public class UDFValueViewHTMLShort extends UDFValueViewHTML {
    public UDFValueViewHTMLShort(SecuredUDFValueBean udfValue, String context) {
        super(udfValue, context);
    }

    public String getValue(Secured o) throws GranException {
        Object va = udfValue.getValue(o);
        SessionContext sc = o.getSecure();
        if (null != va) {
            String value;
            switch (udfValue.getUdfType()) {
                case UdfValue.DATE:
                    DateFormatter df = new DateFormatter(sc.getTimezone(), sc.getLocale());
                    value = df.parse((Calendar) va);
                    return value;
                case UdfValue.INTEGER:
                    return va.toString();
                case UdfValue.FLOAT:
                    return String.valueOf(va);
                case UdfValue.MULTILIST:
                    value = UDFFormFillHelper.listToString((List) va, "<br>");
                    return value;
                case UdfValue.TASK: {
                    List<String> sortedSet = (List<String>) va;
                    ArrayList<SecuredTaskBean> set = TaskComparator.sort(sortedSet, sc);
                    if (set == null)
                        return "";
                    String r = "";

                    for (Iterator<SecuredTaskBean> it = set.iterator(); it.hasNext();) {
                        SecuredTaskBean t = it.next();
                        TaskView v = new TaskViewHTML(t, context);
                        if (it.hasNext()) {
                            r += v.getNumber() + "<br>";
                        } else {
                            r += v.getNumber();
                        }

                    }
                    value = r;
                    return value;
                }
                case UdfValue.USER: {
                    List<String> sortedSet = (List<String>) va;
                    ArrayList<SecuredUserBean> set = UserComparator.sort(sortedSet, sc);
                    if (set == null)
                        return "";

                    String r = "";
                    for (Iterator<SecuredUserBean> it = set.iterator(); it.hasNext();) {
                        SecuredUserBean t = it.next();
                        UserView v = new UserViewHTMLLinked(t, context);
                        if (it.hasNext()) {
                            r += v.getName() + "<br>";
                        } else {
                            r += v.getName();
                        }

                    }
                    value = r;
                    return value;
                }

                case UdfValue.MEMO:
                    return udfValue.isHtmlview() ? va.toString() : "<pre>" + Null.stripNullHtml(HTMLEncoder.text2HTML(va.toString())) + "</pre>";
                case UdfValue.URL:
                    return urlUDFtoHTML(va.toString());

                default:
                    value = va.toString();
                    return udfValue.isHtmlview() ? value : "<pre>" + Null.stripNullHtml(HTMLEncoder.encode(value)) + "</pre>";
            }
        }
        return "";
    }
}
