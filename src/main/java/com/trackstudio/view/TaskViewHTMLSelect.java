package com.trackstudio.view;

import java.util.Iterator;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.Immutable;

@Immutable
public class TaskViewHTMLSelect extends TaskViewHTMLShort {
    private final String currentURL;

    public TaskViewHTMLSelect(SecuredTaskBean task, String currentURL, String context) throws GranException {
        super(task, context);
        this.currentURL = currentURL;
    }

    public String getName() throws GranException {
        if (!task.canViewChildren())
            return getPathNoLink();
        if (freeAccess)
            return " <a href=\"" + currentURL + "?id=" + task.getId() + "\">" + getDelimiter() + Null.stripNullHtml(HTMLEncoder.encode(task.getName())) + " [" + NUMBER_SIGN + task.getNumber() + (task.getShortname() != null && task.getShortname().length() > 0 ? ' ' + task.getShortname() : "") + ']' + "</a>";
        else return ' ' + getDelimiter() + NUMBER_SIGN + Null.stripNullHtml(HTMLEncoder.encode(task.getNumber()));
    }

    private String getPathNoLink() throws GranException {
        if (freeAccess)
            return ' ' + getDelimiter() + Null.stripNullHtml(HTMLEncoder.encode(task.getName())) + " [" + NUMBER_SIGN + task.getNumber() + ']';
        else
            return ' ' + getDelimiter() + NUMBER_SIGN + task.getNumber();
    }


    public TaskView getView(SecuredTaskBean t) throws GranException {
        return new TaskViewHTMLSelect(t, currentURL, context);
    }

    public String getAliasPath() throws GranException {
        if (freeAccess)
            return " <a href=\"" + currentURL + "?id=" + task.getId() + "\">" + getDelimiter() + Null.stripNullHtml(HTMLEncoder.encode(task.getTaskNameCutted())) + "</a>";
        else
            return ' ' + getDelimiter() + NUMBER_SIGN + Null.stripNullHtml(HTMLEncoder.encode(task.getNumber()));
    }

    public String getRelativePath(String from) throws GranException {
        StringBuffer bf = new StringBuffer();
        Iterator i1 = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskChain(task.getSecure(), from, task.getId()).iterator();
        if (i1.hasNext()) {
            SecuredTaskBean usr1 = (SecuredTaskBean) i1.next();
            if (!usr1.canViewChildren()) bf.append(new TaskViewHTMLSelect(usr1, currentURL, context).getPathNoLink());
            else if (!usr1.getId().equals(from)) bf.append(getView(usr1).getAliasPath());
        }
        while (i1.hasNext()) {
            SecuredTaskBean usr1 = (SecuredTaskBean) i1.next();
            if (!usr1.canViewChildren()) {
                bf.append(PATH_DELIMITER);
                bf.append(new TaskViewHTMLSelect(usr1, currentURL, context).getPathNoLink());
            } else if (!usr1.getId().equals(from)) {
                bf.append(PATH_DELIMITER);
                bf.append(getView(usr1).getAliasPath());
            }
        }
        return "<div class=\"fullpath\">" + bf + "</div>";
    }


    public String getFullPath() throws GranException {
        StringBuffer bf = new StringBuffer();
        Iterator i1 = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskChain(task.getSecure(), null, task.getId()).iterator();
        if (i1.hasNext()) {
            SecuredTaskBean usr1 = (SecuredTaskBean) i1.next();
            if (usr1.canViewChildren())
                bf.append(usr1.getId().equals(task.getId()) ? getView(usr1).getName() : getView(usr1).getAliasPath());
            else
                bf.append(new TaskViewHTMLSelect(usr1, currentURL, context).getPathNoLink());
        }
        while (i1.hasNext()) {
            SecuredTaskBean usr1 = (SecuredTaskBean) i1.next();
            bf.append(PATH_DELIMITER);
            if (usr1.canViewChildren())
                bf.append(usr1.getId().equals(task.getId()) ? getView(usr1).getName() : getView(usr1).getAliasPath());
            else
                bf.append(new TaskViewHTMLSelect(usr1, currentURL, context).getPathNoLink());
        }
        return "<div class=\"fullpath\">" + bf + "</div>";
    }


}
