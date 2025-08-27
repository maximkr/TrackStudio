package com.trackstudio.view;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;

import net.jcip.annotations.Immutable;

@Immutable
public class TaskViewFactory {

    public static TaskView inEmailText(SecuredTaskBean bean) throws GranException {
        return new TaskViewEmailText(bean);
    }

    public static TaskView inEmailHTML(SecuredTaskBean bean) throws GranException {
        return new TaskViewEmailHTML(bean);
    }

    public static TaskView inHTML(SecuredTaskBean bean, String context) throws GranException {
        return new TaskViewHTML(bean, context);
    }

    public static TaskView inHTMLSelect(SecuredTaskBean bean, String currentURL, String context) throws GranException {
        return new TaskViewHTMLSelect(bean, currentURL, context);
    }

    public static TaskView inHTMLShort(SecuredTaskBean bean, String context) throws GranException {
        return new TaskViewHTMLShort(bean, context);
    }

    public static TaskView inEmailHTMLList(SecuredTaskBean bean) throws GranException {
        return new TaskListViewEmailHTML(bean);
    }

    public static TaskView inEmailTextList(SecuredTaskBean bean) throws GranException {
        return new TaskListViewEmailText(bean);
    }
}
