package com.trackstudio.view;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.Immutable;

@Immutable
public class TaskViewHTMLShort extends TaskViewHTML {
    public static final String PERM_LINK = "[Perm Link]";

    public TaskViewHTMLShort(SecuredTaskBean t, String context) throws GranException {
        super(t, context);
    }

    public String getName() throws GranException {
        if (freeAccess)
            return " <a href=\"" + context + "/task/" + task.getNumber() + "?thisframe=true\">"
                    + getDelimiter() + Null.stripNullHtml(HTMLEncoder.encode(task.getName())) + "</a>";
        else
            return ' ' + getDelimiter() + Null.stripNullHtml(HTMLEncoder.encode(task.getNumber()));
    }

    public String getNameForTaskSelect() throws GranException {
        if (freeAccess)
            return " <a href=\"" + context + "/TaskSelectAction.do?" + "id=" + task.getId() + "\">"
                    + getDelimiter() + Null.stripNullHtml(HTMLEncoder.encode(task.getName())) + "</a>";
        else
            return ' ' + getDelimiter() + Null.stripNullHtml(HTMLEncoder.encode(task.getNumber()));
    }

    public TaskView getView(SecuredTaskBean t) throws GranException {
        return new TaskViewHTMLShort(t, context);
    }

    public String getNumber() throws GranException {
        return NUMBER_SIGN + Null.stripNullHtml(HTMLEncoder.encode(task.getNumber()));
    }

    public UDFValueView getUDFValueView(SecuredUDFValueBean bean) throws GranException {
        return new UDFValueViewHTML(bean, context);
    }

    public String getHandler() throws GranException {
        if (task.getHandlerUserId() != null)
            return getUserView(task.getHandlerUser()).getPath();
        else if (task.getHandlerGroupId() != null)
            return new PrstatusViewHTML(task.getHandlerGroup(), context).getName();
        else return "";
    }

    public String getSubmitter() throws GranException {
        return getUserView(task.getSubmitter()).getPath();
    }
}
