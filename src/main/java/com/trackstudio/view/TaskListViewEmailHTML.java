package com.trackstudio.view;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.textfilter.HTMLEncoder;

public class TaskListViewEmailHTML extends TaskViewEmailHTML {
    public TaskListViewEmailHTML(SecuredTaskBean task) throws GranException {
        super(task);
    }

    public UDFValueView getUDFValueView(SecuredUDFValueBean bean) throws GranException {
        return new UDFValueViewEmailHTMLShort(bean);
    }

    public String getName() throws GranException {
        if (freeAccess)
            return "<a href=\"" + Config.getInstance().getSiteURL() + "/task/" + task.getNumber() + "\">" + HTMLEncoder.encode(task.getName()) + "</a> ";
        else
            return NUMBER_SIGN + task.getNumber();

    }


}
