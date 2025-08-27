package com.trackstudio.view;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.Immutable;


@Immutable
public class TaskViewEmailHTML extends TaskView {

    public static final String PATH_DELIMITER = " &gt ";
    public static final String RETURN = "\n";

    public TaskViewEmailHTML(SecuredTaskBean task) throws GranException {
        super(task);
    }

    public String getReferencedTasks(String udfName, Collection taskIds) throws GranException {
        if (taskIds == null) return "";
        String value;
        String r = "<table class=\"udf\">";
        r += "<caption>" + udfName + "</caption>";
        for (Iterator it = taskIds.iterator(); it.hasNext();) {
            SecuredTaskBean t = (SecuredTaskBean) it.next();
            TaskView v = new TaskViewEmailHTML(t);
            if (t.isOnSight())
                r += "<tr><td>" + v.getFullPath() + "</td></tr>";
            else
                r += "<tr><td>" + t.getName() + "</td></tr>";
        }
        value = r + "</table>";
        return value;
    }

    public String getReferencedUsers(Collection userIds) throws GranException {
        if (userIds == null) return "";
        String value;
        String r = "<table class=\"udf\">";

        for (Iterator it = userIds.iterator(); it.hasNext();) {
            SecuredUserBean t = (SecuredUserBean) it.next();
            UserView v = new UserViewEmailHTML(t);
            r += "<tr><td>" + v.getPath() + "</td></tr>";
        }
        value = r + "</table>";
        return value;
    }

    public String pathDelimiter() {

        return PATH_DELIMITER;
    }

    public String getAlias() throws GranException {
        return Null.stripNullHtml(HTMLEncoder.encode(task.getShortname()));
    }

    public String getAliasPath() throws GranException {
        if (freeAccess)
            return "<a href=\"" + Config.getInstance().getSiteURL() + "/task/" + task.getNumber() + "\">"
                    + HTMLEncoder.encode(task.getTaskNameCutted()) + "</a> " + RETURN;
        else
            return NUMBER_SIGN + task.getNumber() + RETURN;
    }

    @Override
    public String getSimplePath() throws GranException {
        if (freeAccess) {
            return "&nbsp;<a href=\"" +
                    Config.getInstance().getSiteURL() + "/task/" + task.getNumber() + "?thisframe=true\">" +
                    HTMLEncoder.encode(task.getName()) + " [" + NUMBER_SIGN + task.getNumber() + (task.getShortname() != null && task.getShortname().length() > 0 ? ' ' + task.getShortname() : "") + "]</a>";
        } else return "<span>" + NUMBER_SIGN + task.getNumber() + "</span>";
    }

    public String getFilterAliasPath() throws GranException {
        return "";  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected String getEncodedName() throws GranException {
        return HTMLEncoder.encode(task.getName());
    }

    public String getName() throws GranException {
        if (freeAccess)
            return "&nbsp;<a href=\"" + Config.getInstance().getSiteURL() + "/task/" + task.getNumber() + "\">" + getEncodedName() + " [" + NUMBER_SIGN + task.getNumber() + ']' + "</a> \n";
        else
            return NUMBER_SIGN + task.getNumber() + '\n';
    }

    public TaskView getView(SecuredTaskBean t) throws GranException {
        return new TaskViewEmailHTML(t);
    }

    public String getNumber() throws GranException {
        if (freeAccess)
            return "<a href=\"" + Config.getInstance().getSiteURL() + "/task/" + task.getNumber() + "\">" + NUMBER_SIGN + Null.stripNullHtml(HTMLEncoder.encode(task.getNumber())) + "</a> ";
        else
            return NUMBER_SIGN + task.getNumber();
    }

    public String getDeadline() throws GranException {
        return "<nobr>" + Null.stripNullHtml(task.getSecure().getUser().getDateFormatter().parse(task.getDeadline())) + "</nobr>";
    }


    public String getSubmitdate() throws GranException {
        return "<nobr>" + task.getSecure().getUser().getDateFormatter().parse(task.getSubmitdate()) + "</nobr>";
    }

    public String getClosedate() throws GranException {
        return "<nobr>" + task.getSecure().getUser().getDateFormatter().parse(task.getClosedate()) + "</nobr>";
    }

    public String getUpdatedate() throws GranException {
        return "<nobr>" + task.getSecure().getUser().getDateFormatter().parse(task.getUpdatedate()) + "</nobr>";
    }

    public String getHandler() throws GranException {
        if (task.getHandlerUserId() != null)
            return "<nobr>" + getUserView(task.getHandlerUser()).getName() + "</nobr>";
        else if (task.getHandlerGroupId() != null)
            return "<nobr>" + HTMLEncoder.encode(task.getHandlerGroup().getName()) + "</nobr>";
        else return "";
    }

    public String getSubmitter() throws GranException {
        return "<nobr>" + getUserView(task.getSubmitter()).getName() + "</nobr>";
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


    public UDFValueView getUDFValueView(SecuredUDFValueBean bean) throws GranException {
        return new UDFValueViewEmailHTML(bean);
    }

    public UserView getUserView(SecuredUserBean bean) throws GranException {
        return new UserViewEmailHTML(bean);
    }

    public String getSubmitterPrstatuses() throws GranException {
        StringBuffer result = new StringBuffer();
        List submitterPrstatuses = task.getSubmitterPrstatuses();
        if (submitterPrstatuses != null)
            for (Iterator it = submitterPrstatuses.iterator(); it.hasNext();) {
                result.append(HTMLEncoder.encode((String) it.next()));
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
                result.append(HTMLEncoder.encode((String) it.next()));
                if (it.hasNext())
                    result.append(", ");
            }
        return result.toString();
    }

    /*
    public String getFullPath() throws GranException {
        if (null != task) {
            StringBuffer bf = new StringBuffer();
            Iterator i1 = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskChain(task.getSecure(), null, task.getId()).iterator();
            if (i1.hasNext()) {
                bf.append(getView((SecuredTaskBean) i1.next()).getAliasPath());
            }
            while (i1.hasNext()) {
                bf.append(PATH_DELIMITER);
                bf.append(getView((SecuredTaskBean) i1.next()).getAliasPath());
            }
            return bf.toString();
        }
        return "";
    } */
}
