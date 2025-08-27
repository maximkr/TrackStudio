package com.trackstudio.view;

import java.util.ArrayList;
import java.util.Calendar;
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
public class UDFValueViewEmailText extends UDFValueView {

    public UDFValueViewEmailText(SecuredUDFValueBean udfValue) {
        this.udfValue = udfValue;
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
                case UdfValue.FLOAT:
                    return String.valueOf(va);
                case UdfValue.MULTILIST:
                    value = UDFFormFillHelper.listToString((List) va, ", ");
                    return value;
                case UdfValue.TASK: {
                    List<String> sortedSet = (List<String>) va;
                    ArrayList<SecuredTaskBean> set = TaskComparator.sort(sortedSet, sc);
                    if (set == null)
                        return "";
                    StringBuilder r = new StringBuilder();
            		for (SecuredTaskBean t : set) {
            		    SecuredTaskBean task = t;
            		    TaskView v = new TaskViewEmailText(task);
            		    r.append(v.getName());
            		    if (set.size()>1) r.append(", ");
            		}
            		
            		return r.toString();
                }
                case UdfValue.USER:

                {
                    List<String> sortedSet = (List<String>) va;
                    ArrayList<SecuredUserBean> set = UserComparator.sort(sortedSet, sc);
                    if (set == null)
                        return "";
                    StringBuilder r = new StringBuilder();
                    for (SecuredUserBean t : set) {
            		    UserView v = new UserViewEmailText(t);
            		    r.append(v.getPath());
            		    if (set.size()>1) r.append(", ");
            		}
            		return r.toString();
                    
                }
                case UdfValue.URL:
                    return urlUDFToText(va.toString());
                default:
                    value = va.toString();
                    return Null.stripNullText(HTMLEncoder.stripHtmlTags(value));
            }
        }
        return "";
    }

    public String getCaption() {
        return udfValue.getCaption();
    }

}
