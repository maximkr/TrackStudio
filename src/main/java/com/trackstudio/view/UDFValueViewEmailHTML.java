package com.trackstudio.view;

import java.util.ArrayList;
import java.util.List;

import com.trackstudio.app.filter.comparator.TaskComparator;
import com.trackstudio.app.filter.comparator.UserComparator;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;

import net.jcip.annotations.Immutable;

@Immutable
public class UDFValueViewEmailHTML extends UDFValueViewHTML {

    public UDFValueViewEmailHTML(SecuredUDFValueBean udfValue) {
    	super(udfValue, null);
    }

	protected String printUser(SessionContext sc, Object va)
			throws GranException {
		List<String> sortedSet = (List<String>) va;
		ArrayList<SecuredUserBean> set = UserComparator.sort(sortedSet, sc);
		if (set == null)
		    return "";

		StringBuilder r = new StringBuilder();

		for (SecuredUserBean t : set) {

		    UserView v = new UserViewEmailHTML(t);
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

		StringBuilder r = new StringBuilder();
		for (SecuredTaskBean t : set) {
		    SecuredTaskBean task = t;
		    TaskView v = new TaskViewEmailHTML(task);
		    r.append(v.getName());
		    if (set.size()>1) r.append("<br/>");
		}
		
		return r.toString();
	}
   

    
}
