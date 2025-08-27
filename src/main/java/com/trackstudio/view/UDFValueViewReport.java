package com.trackstudio.view;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.trackstudio.startup.Config;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.formatter.DateFormatter;
import com.trackstudio.tools.formatter.HourFormatter;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.Immutable;

@Immutable
public class UDFValueViewReport extends UDFValueView {

    public UDFValueViewReport(SecuredUDFValueBean udfValue) {
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
                    int decimalFormatUdfFloat = Integer.valueOf(Config.getProperty("trackstudio.decimalFormatUdfFloat", "3"));
                    return String.valueOf(new BigDecimal(HourFormatter.parseDouble(va.toString())).setScale(decimalFormatUdfFloat, RoundingMode.UP).doubleValue());
                case UdfValue.MULTILIST:
                    return UDFFormFillHelper.listToString((List) va, "\n");
                case UdfValue.TASK: {
                    List<String> sortedSet = (List<String>) va;
                    ArrayList<SecuredTaskBean> set = TaskComparator.sort(sortedSet, sc);
                    if (set == null)
                        return "";
                    String r = "";

                    for (Iterator<SecuredTaskBean> it = set.iterator(); it.hasNext();) {
                        SecuredTaskBean t = it.next();
                        TaskView v = new TaskViewEmailHTML(t);

                        if (path)
                            if (it.hasNext()) {
                                r += v.getFullPath() + "<br>";
                            } else {
                                r += v.getFullPath();
                            }
                        else {
                            r += v.getNumber();
                        }
                    }

                    return r;
                }
                case UdfValue.USER:

                {
                    List<String> sortedSet = (List<String>) va;
                    ArrayList<SecuredUserBean> set = UserComparator.sort(sortedSet, sc);
                    if (set == null)
                        return "";

                    String r = "";

                    for (Iterator<SecuredUserBean> it = set.iterator(); it.hasNext();) {
                        SecuredUserBean t = it.next();
                        UserView v = new UserViewText(t);
                        if (path) {
                            if (it.hasNext()) {
                                r += v.getPath() + v.getDelimiterSign() + '\n';
                            } else {
                                r += v.getPath();
                            }
                        } else {
                            if (it.hasNext()) {
                                r += v.getName() + "<br>";
                            } else {
                                r += v.getName();
                            }
                        }
                    }

                    return r;
                }
                case UdfValue.URL:
                    return getURLView(va);
                case UdfValue.MEMO:
                    String value = va.toString();
                    if (!udfValue.isHtmlview())
                        return value;
                    else {
                        value = HTMLEncoder.stripHtmlTags(value);
                        return Null.stripNullText(value);
                    }
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
