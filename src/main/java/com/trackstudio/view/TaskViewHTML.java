package com.trackstudio.view;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.textfilter.HTMLEncoder;
import com.trackstudio.tools.textfilter.Wiki;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class TaskViewHTML extends TaskView {

    protected final String context;
    private volatile String wikiParsedDescription;

    public String getPermLink() {
        return Config.getInstance().getSiteURL() + "/task/" + task.getNumber();
    }

    public String getRSSLink() throws GranException {
        String filterId = AdapterManager.getInstance().getSecuredFilterAdapterManager().getCurrentTaskFilterId(task.getSecure(), task.getId());
        return Config.getInstance().getSiteURL() + "/task/" + task.getNumber() + "/rss/" + filterId;
    }

    public String getAlias() throws GranException {
        return Null.stripNullText(HTMLEncoder.encode(task.getShortname()));
    }

    public TaskViewHTML(SecuredTaskBean task, String context) throws GranException {
        super(task);
        this.context = context;
        this.freeAccess = task.canView();

    }

    public String getDelimiter() throws GranException {
        String imageServlet = "/ImageServlet/" + GeneralAction.SERVLET_KEY;
        if (task.getCategoryId() != null) {
            return "<img alt=\"\" class=\"icon\" border=\"0\" src=\"" + context + imageServlet + "/icons/categories/" + task.getCategory().getIcon() + "\">";
        } else {
            return "";
        }
    }

    public String getStateIcon() throws GranException {
        String imageServlet = "/ImageServlet/" + GeneralAction.SERVLET_KEY;
        return "<img alt=\"\" class=\"state\" border=\"0\" style=\"background-color: "+ task.getStatus().getColor() +"\" src=\"" + context + imageServlet +  task.getStatus().getImage() + "\">";
    }

    public String getFilterAliasPath(SecuredTaskBean task) throws GranException {
        String delim = new TaskViewHTML(task, context).getDelimiter();
        if (freeAccess) {
            return " <a href=\"" + context +
                    "/TaskFilterAction.do?method=page&amp;id=" + task.getId() + "\">" + delim +
                    HTMLEncoder.encode(task.getTaskNameCutted()) + "</a>";
        } else return ' ' + delim + NUMBER_SIGN + task.getNumber();
    }

    public String getFilterName() throws GranException {
        if (freeAccess) {
            return " <a href=\"" + context +
                    "/TaskFilterAction.do?method=page&amp;id=" + task.getId() + "\">" + getDelimiter() +
                    HTMLEncoder.encode(task.getName()) + " [" + NUMBER_SIGN + task.getNumber() + (task.getShortname() != null && task.getShortname().length() > 0 ? ' ' + task.getShortname() : "") + "]</a>";
        } else return "<span>" + getDelimiter() + NUMBER_SIGN + task.getNumber() + "</span>";
    }

    public String getFilterFullPath() throws GranException {
        if (null != task) {
            if (freeAccess) {
                StringBuffer bf = new StringBuffer();
                Iterator i1 = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskChain(task.getSecure(), null, task.getId()).iterator();
                if (i1.hasNext()) {
                    SecuredTaskBean stb = (SecuredTaskBean) i1.next();
                    bf.append(stb.getId().equals(task.getId()) ? getFilterName() : getFilterAliasPath(stb));
                }
                while (i1.hasNext()) {
                    SecuredTaskBean stb = (SecuredTaskBean) i1.next();
                    bf.append(pathDelimiter());
                    bf.append(stb.getId().equals(task.getId()) ? getFilterName() : getFilterAliasPath(stb));
                }
                return bf.toString();
            } else return getName();
        }
        return "";
    }

    public String getAliasPath() throws GranException {
        if (freeAccess) {
            return " <a href=\"" +
                    context + "/task/" + task.getNumber() + "?thisframe=true\">" + getDelimiter() +
                    HTMLEncoder.encode(task.getTaskNameCutted()) + "</a>";
        } else return ' ' + getDelimiter() + NUMBER_SIGN + task.getNumber();
    }

    public String getAliasPathForTaskSelect() throws GranException {
        if (freeAccess) {
            return " <a href=\"" +
                    context + "/TaskSelectAction.do?" + "id=" + task.getId() + "\">" + getDelimiter() +
                    HTMLEncoder.encode(task.getTaskNameCutted()) + "</a>";
        } else return ' ' + getDelimiter() + NUMBER_SIGN + task.getNumber();
    }

    public String getReferencedTasks(String udfName, Collection tasks) throws GranException {
        String value;
        String r = "<table class=\"udf\">";
        r += "<caption>" + udfName + "</caption>";
        for (SecuredTaskBean t : (Collection<SecuredTaskBean>) tasks) {
            TaskView v = new TaskViewHTML(t, context);
            if (t.isOnSight())
                r += "<tr><td>" + v.getFullPath() + "</td></tr>";
            else
                r += "<tr><td>" + t.getName() + "</td></tr>";
        }
        value = r + "</table>";
        return value;
    }

    public String getNumber() throws GranException {
        if (freeAccess) {
            String linkByEvent = context + "/task/" + task.getNumber() + "?thisframe=true";
            StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("<a class=\'internal\' title='");
			stringBuilder.append(HTMLEncoder.encode(task.getName()));
			stringBuilder.append("' href=\"");
			stringBuilder.append(linkByEvent);
			stringBuilder.append("\">");
			stringBuilder.append(NUMBER_SIGN);
			stringBuilder.append(Null.stripNullHtml(HTMLEncoder.encode(task.getNumber())));
			stringBuilder.append("</a>");
			return stringBuilder.toString();
        } else return NUMBER_SIGN + Null.stripNullHtml(HTMLEncoder.encode(task.getNumber()));
    }

    @Override
    public String getContext() throws GranException {
        return context;
    }

    public String getName() throws GranException {
        if (freeAccess) {
            String name = task.getName();  
            if (name.length() > 100) {
                name = name.substring(0, 100) + "...";
            }
            StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("<a class=\"internal\" href=\"");
			stringBuilder.append(context);
			stringBuilder.append("/task/");
			stringBuilder.append(task.getNumber());
			stringBuilder.append("?thisframe=true\">");
			stringBuilder.append(getDelimiter());
			stringBuilder.append(getStateIcon());
			stringBuilder.append(" ");
			stringBuilder.append(HTMLEncoder.encode(name));
			stringBuilder.append("</a> <em class=\"number\">[");
			stringBuilder.append(NUMBER_SIGN);
			stringBuilder.append(task.getNumber());
			stringBuilder.append((task.getShortname() != null && task.getShortname().length() > 0 ? ' ' + task.getShortname() : ""));
			stringBuilder.append("]</em>");
			return stringBuilder.toString();
        } else return "<span>" + getDelimiter() + NUMBER_SIGN + task.getNumber() + "</span>";
    }

    public String getSimplePath() throws GranException {
        if (freeAccess) {
            StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("&nbsp;<a href=\"");
			stringBuilder.append(context);
			stringBuilder.append("/task/");
			stringBuilder.append(task.getNumber());
			stringBuilder.append("?thisframe=true\">");
			stringBuilder.append(HTMLEncoder.encode(task.getName()));
			stringBuilder.append(" [");
			stringBuilder.append(NUMBER_SIGN);
			stringBuilder.append(task.getNumber());
			stringBuilder.append((task.getShortname() != null && task.getShortname().length() > 0 ? ' ' + task.getShortname() : ""));
			stringBuilder.append("]</a>");
			return stringBuilder.toString();
        } else return "<span>" + getDelimiter() + NUMBER_SIGN + task.getNumber() + "</span>";
    }

    public TaskView getView(SecuredTaskBean t) throws GranException {
        return new TaskViewHTML(t, context);
    }

    private String nobr(String s) {
        String s2 = Null.stripNullText(s);
        if (s2.length() > 0) {
            return s2;
        } else return "";
    }

    public String getDeadline() throws GranException {
        return nobr(task.getSecure().getUser().getDateFormatter().parse(task.getDeadline()));
    }

    public String getSubmitdate() throws GranException {
        return nobr(task.getSecure().getUser().getDateFormatter().parse(task.getSubmitdate()));
    }

    public String getClosedate() throws GranException {
        return nobr(task.getSecure().getUser().getDateFormatter().parse(task.getClosedate()));
    }

    public String getUpdatedate() throws GranException {
        return nobr(task.getSecure().getUser().getDateFormatter().parse(task.getUpdatedate()));
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

    public String getDescription() throws GranException {
        if (task.getDescription() != null) {
            if (wikiParsedDescription == null) {
                Wiki wiki = new Wiki(this);
                wikiParsedDescription = wiki.toMacros(task.getDescription());
            }
            return wikiParsedDescription;
        }
        return "";
    }

    public String getShortDescription() throws GranException {
        if (task.getTextDescription() != null) {
            String textDescription = HTMLEncoder.stripHtmlTags(task.getDescription());
            return HTMLEncoder.htmlCut(textDescription, 180);
        }
        return "";
    }

    public String getPriority() throws GranException {
        return null != task.getPriorityId() ? HTMLEncoder.encode(task.getPriority().getName()) : "";
    }

    public String getCategory() throws GranException {
        return null != task.getCategoryId() ? HTMLEncoder.encode(task.getCategory().getName()) : "";
    }

    public String getStatus() throws GranException {
        boolean isnull = null == task.getStatusId();
        if (isnull) return "";
        else return new StateViewHTML(task.getStatus(), context).getName();
    }

    public String getSubmitterPrstatuses() throws GranException {
        StringBuffer result = new StringBuffer();
        List submitterPrstatuses = task.getSubmitterPrstatuses();
        if (submitterPrstatuses != null) for (Iterator it = submitterPrstatuses.iterator(); it.hasNext();) {
            result.append(HTMLEncoder.encode((String) it.next()));
            if (it.hasNext())
                result.append(", ");
        }
        return result.toString();
    }

    public String getHandlerPrstatuses() throws GranException {
        StringBuffer result = new StringBuffer();

        List handlerPrstatuses = task.getHandlerPrstatuses();
        if (handlerPrstatuses != null) for (Iterator it = handlerPrstatuses.iterator(); it.hasNext();) {
            result.append(HTMLEncoder.encode((String) it.next()));
            if (it.hasNext())
                result.append(", ");
        }
        return result.toString();
    }


    public String getResolution() throws GranException {
        if (task.getResolution() != null) return nobr(task.getResolution().getName());
        else return "";
    }

    public String getWorkflow() throws GranException {
        return nobr(HTMLEncoder.encode(task.getWorkflow().getName()));
    }


    public UDFValueView getUDFValueView(SecuredUDFValueBean bean) throws GranException {
        return new UDFValueViewHTML(bean, context);
    }

    public UserView getUserView(SecuredUserBean bean) throws GranException {
        return new UserViewHTMLLinked(bean, context);
    }

    public String getFullPath() throws GranException {
        return "<div class=\"fullpath\">" + getPath() + "</div>";    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected String getPath() throws GranException {
        return super.getFullPath();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public String getRelativePath(String from) throws GranException {
        return "<div class=\"fullpath\">" + super.getRelativePath(from) + "</div>";    //To change body of overridden methods use File | Settings | File Templates.
    }
}
