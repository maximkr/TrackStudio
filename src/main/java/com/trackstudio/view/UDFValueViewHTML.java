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
public class UDFValueViewHTML extends UDFValueView {
    protected final String context;

    public UDFValueViewHTML(SecuredUDFValueBean udfValue, String context) {
        this.udfValue = udfValue;
        this.context = context;
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
                case UdfValue.LIST:
                    value = va.toString();
                    return value;    
                case UdfValue.STRING:
                    value = va.toString();
                    return value;        
                case UdfValue.MULTILIST:
                    value = UDFFormFillHelper.listToString((List) va, ", ");
                    return value;
                case UdfValue.TASK: {
                    return printTask(sc, va);
                }
                case UdfValue.USER: {
                    return printUser(sc, va);
                }
                case UdfValue.MEMO:
                    return udfValue.isHtmlview() ? va.toString() : "<pre>" + Null.stripNullHtml(HTMLEncoder.text2HTML(va.toString())) + "</pre>";
                case UdfValue.URL:
                    return urlUDFtoHTML(va.toString());
                default:
                    value = va.toString();
                    return udfValue.isHtmlview() ? value : "<pre>" + Null.stripNullHtml(HTMLEncoder.text2HTML((value))) + "</pre>";
            }
        }
        return "";
    }


	protected String printUser(SessionContext sc, Object va)
			throws GranException {
		List<String> sortedSet = (List<String>) va;
		ArrayList<SecuredUserBean> set = UserComparator.sort(sortedSet, sc);
		if (set == null)
		    return "";

		StringBuilder r = new StringBuilder();

		for (SecuredUserBean t : set) {

		    UserView v = new UserViewHTML(t, context);
		    r.append(v.getPath());
		    if (set.size()>1) r.append("<br/>");
		}
		
		return r.toString();
	}


	protected String printTask(SessionContext sc, Object va)
			throws GranException {
		List<String> sortedSet = (List<String>) va;
		ArrayList<SecuredTaskBean> set = TaskComparator.sort(sortedSet, sc);
		if (set == null)
		    return "";

		StringBuffer r = new StringBuffer();
		for (SecuredTaskBean t : set) {
		    SecuredTaskBean task = t;
		    TaskView v = new TaskViewHTML(task, context);
		    r.append(v.getName());
		    if (set.size()>1) r.append("<br/>");
		}
		
		return r.toString();
	}

    public String getCaption() {
        return HTMLEncoder.encode(udfValue.getCaption());
    }
}
