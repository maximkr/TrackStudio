package com.trackstudio.view;

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.Immutable;

@Immutable
public class TaskViewText extends TaskView {
    public TaskViewText(SecuredTaskBean task) throws GranException {
        super(task);
    }

    public String getAliasPath() throws GranException {
        if (freeAccess)
            return task.getTaskNameCutted();
        else return getNumber();
    }

    public String getFilterAliasPath() throws GranException {
        return "";  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getAlias() throws GranException {
        return Null.stripNullText(task.getShortname());
    }

    public String getName() throws GranException {
        return task.getName() + "  [" + getNumber() + ']';
    }

    public TaskView getView(SecuredTaskBean t) throws GranException {
        return new TaskViewText(t);
    }

    public String getNumber() throws GranException {
        return NUMBER_SIGN + task.getNumber();
    }

    public String getDeadline() throws GranException {
        Calendar deadline = task.getDeadline();
        return null != deadline ? task.getSecure().getUser().getDateFormatter().parse(deadline) : "";
    }


    public String getHandler() throws GranException {
        if (task.getHandlerUserId() != null)
            return new UserViewText(task.getHandlerUser()).getName();
        else if (task.getHandlerGroupId() != null)
            return HTMLEncoder.encode(task.getHandlerGroup().getName());
        else return "";
    }

    public String getSubmitter() throws GranException {
        return new UserViewText(task.getSubmitter()).getName();
    }

    public String getDescription() throws GranException {
        return Null.stripNullText(task.getDescription());
    }

    public String getShortDescription() throws GranException {
        if (task.getTextDescription() != null) {
            String textDescription = task.getTextDescription();
            return HTMLEncoder.htmlCut(textDescription, 180);
        }
        return "";
    }

    public String getPriority() throws GranException {
        return null != task.getPriorityId() ? task.getPriority().getName() : "";
    }

    public String getCategory() throws GranException {
        return null != task.getCategoryId() ? task.getCategory().getName() : "";
    }

    public String getStatus() throws GranException {
        return null != task.getStatusId() ? task.getStatus().getName() : "";
    }

    public String getResolution() throws GranException {
        return null != task.getResolutionId() ? task.getResolution().getName() : "";
    }

    public String getWorkflow() throws GranException {
        return null != task.getWorkflowId() ? task.getWorkflow().getName() : "";
    }

    public String getSubmitdate() throws GranException {
        Calendar submitdate = task.getSubmitdate();
        return submitdate != null ? task.getSecure().getUser().getDateFormatter().parse(submitdate) : "";
    }

    public String getClosedate() throws GranException {
        Calendar closedate = task.getClosedate();
        return closedate != null ? task.getSecure().getUser().getDateFormatter().parse(closedate) : "";
    }

    public String getUpdatedate() throws GranException {
        Calendar updatedate = task.getUpdatedate();
        return updatedate != null ? task.getSecure().getUser().getDateFormatter().parse(updatedate) : "";
    }

    /*
    public String getName() throws GranException {
        return task.getName();
    }
    */
    public UDFValueView getUDFValueView(SecuredUDFValueBean bean) throws GranException {
        return new UDFValueViewReport(bean);
    }

    public UserViewText getUserView(SecuredUserBean bean) throws GranException {
        return new UserViewText(bean);
    }

    public String getSubmitterPrstatuses() throws GranException {
        StringBuffer result = new StringBuffer();
        List submitterPrstatuses = task.getSubmitterPrstatuses();
        if (submitterPrstatuses != null) for (Iterator it = submitterPrstatuses.iterator(); it.hasNext();) {
            result.append((String) it.next());
            if (it.hasNext())
                result.append(", ");
        }
        return result.toString();
    }

    public String getHandlerPrstatuses() throws GranException {
        StringBuffer result = new StringBuffer();
        List handlerPrstatuses = task.getHandlerPrstatuses();
        if (handlerPrstatuses != null) for (Iterator it = handlerPrstatuses.iterator(); it.hasNext();) {
            result.append((String) it.next());
            if (it.hasNext())
                result.append(", ");
        }
        return result.toString();
    }

    public String getReferencedTasks(String udfName, Collection tasks) throws GranException {
        String ret = "";
        for (Iterator it = tasks.iterator(); it.hasNext();) {
            SecuredTaskBean t = (SecuredTaskBean) it.next();
            TaskView v = new TaskViewText(t);
            if (ret.length() > 0) ret += "\n";
            ret += udfName + " = ";
            if (t.isOnSight())
                ret += v.getFullPath();
            else
                ret += v.getName();
        }
        return ret;
    }

    @Override
    public String getSimplePath() throws GranException {
       return task.getName() + " [" + NUMBER_SIGN + task.getNumber() + "]";
    }
}
