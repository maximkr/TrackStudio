package com.trackstudio.view;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.Immutable;

@Immutable
public class TaskViewEmailText extends TaskView {

    public TaskViewEmailText(SecuredTaskBean task) throws GranException {
        super(task);

    }

    public String getReferencedTasks(String udfName, Collection tasks) throws GranException {
        if (tasks == null) return "";
        String value = "";
        for (Iterator it = tasks.iterator(); it.hasNext();) {
            SecuredTaskBean t = (SecuredTaskBean) it.next();
            TaskView v = new TaskViewEmailText(t);
            if (t.isOnSight())
                value += v.getFullPath() + "[" + v.getNumber() + "]" + "\n";
            else
                value += t.getName() + "\n";
        }
        return value;
    }

    public String getReferencedUsers(Collection users) throws GranException {
        if (users == null) return "";
        String value = "";
        for (Iterator it = users.iterator(); it.hasNext();) {
            SecuredUserBean t = (SecuredUserBean) it.next();
            UserView v = new UserViewEmailText(t);
            value += v.getPath() + "\n";
        }
        return value;
    }

    public String getAliasPath() throws GranException {
        if (freeAccess) {
            HTMLEncoder sb = new HTMLEncoder(task.getTaskNameCutted());
            sb.replace("\r\n", " ");
            sb.replace("\n", " ");
            return sb.toString();
        } else
            return getNumber();
    }

    @Override
    public String getSimplePath() throws GranException {
       return "<span>" + NUMBER_SIGN + task.getNumber() + "</span>";
    }

    public String getFilterAliasPath() throws GranException {
        return "";  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getAlias() throws GranException {
        return Null.stripNullText(task.getShortname());
    }

    public String getName() throws GranException {
        //return getName() + (addNumber ? "<a href=\"" + Config.getInstance().getSiteURL() + "/TSFrameAction.do?id="+  task.getId() + "&session=" + task.getSecure().getSession() + "\">" + " [" + NUMBER_SIGN+task.getNumber() + "]"  + "</a>" : "") + "\n";
        return getEncodedName() +" [" + getNumber() + "]";// + Config.getInstance().getSiteURL() + "/task/" + task.getNumber() + (task.getSecure().getSession().equals("session") ? "" : "?session=" + task.getSecure().getSession()) ;
    }

    public TaskView getView(SecuredTaskBean t) throws GranException {
        return new TaskViewEmailText(t);
    }

    public String getNumber() throws GranException {
        return NUMBER_SIGN + task.getNumber();
    }

    public String getDeadline() throws GranException {

        return Null.stripNullText(task.getSecure().getUser().getDateFormatter().parse(task.getDeadline()));

    }


    public String getHandler() throws GranException {
        if (task.getHandlerUserId() != null)
            return getUserView(task.getHandlerUser()).getName();
        else if (task.getHandlerGroupId() != null)
            return HTMLEncoder.encode(task.getHandlerGroup().getName());
        else
            return "";
    }

    public String getSubmitter() throws GranException {
        return getUserView(task.getSubmitter()).getName();
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
        return task.getSecure().getUser().getDateFormatter().parse(task.getSubmitdate());
    }

    public String getClosedate() throws GranException {
        return task.getSecure().getUser().getDateFormatter().parse(task.getClosedate());
    }

    public String getUpdatedate() throws GranException {
        return task.getSecure().getUser().getDateFormatter().parse(task.getUpdatedate());
    }

    protected String getEncodedName() throws GranException {
        HTMLEncoder sb = new HTMLEncoder(task.getName());
        sb.replace("\r\n", " ");
        sb.replace("\n", " ");
        return sb.toString();

    }

    public UDFValueView getUDFValueView(SecuredUDFValueBean bean) throws GranException {
        return new UDFValueViewEmailText(bean);
    }

    public UserView getUserView(SecuredUserBean bean) throws GranException {
        return new UserViewEmailText(bean);
    }

    public String getSubmitterPrstatuses() throws GranException {
        StringBuffer result = new StringBuffer();
        List submitterPrstatuses = task.getSubmitterPrstatuses();
        if (submitterPrstatuses != null)
            for (Iterator it = submitterPrstatuses.iterator(); it.hasNext();) {
                result.append((String) it.next());
                if (it.hasNext())
                    result.append(", ");
            }
        return result.toString();
    }

    public String getHandlerPrstatuses() throws GranException {
        StringBuffer result = new StringBuffer();
        List handlerPrstatuses = task.getHandlerPrstatuses();
        if (handlerPrstatuses != null)
            for (Iterator it = handlerPrstatuses.iterator(); it.hasNext();) {
                result.append((String) it.next());
                if (it.hasNext())
                    result.append(", ");
            }
        return result.toString();
    }
}
