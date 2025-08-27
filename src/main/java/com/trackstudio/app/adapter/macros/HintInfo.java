package com.trackstudio.app.adapter.macros;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.app.adapter.MacrosTaskAdapter;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.tools.textfilter.HTMLEncoder;
import com.trackstudio.view.TaskView;
import com.trackstudio.view.TaskViewHTML;
import com.trackstudio.view.TaskViewText;

import net.jcip.annotations.Immutable;

@Immutable
public class HintInfo extends DoubleSharp implements MacrosTaskAdapter {

    @Override
    public String buildLink(TaskView view, SecuredTaskBean task) throws GranException {
        TaskViewHTML viewHTML = (TaskViewHTML) view;
        TaskViewText viewText = new TaskViewer(task);
        String linkByEvent = viewHTML.getContext() + "/task/" + task.getNumber() + "?thisframe=true";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<a class=\'internal\' title='");
        stringBuilder.append("[#").append(task.getNumber()).append("] ");
        stringBuilder.append(viewText.getFullPath());
        stringBuilder.append(" (").append(task.getCategory().getName()).append(")");
        stringBuilder.append("' href=\"");
        stringBuilder.append(linkByEvent);
        stringBuilder.append("\">");
        stringBuilder.append(buildIcoCategory(task, viewHTML.getContext()));
        stringBuilder.append(" ");
        stringBuilder.append(HTMLEncoder.encode(task.getName()));
        stringBuilder.append("</a>");
        return stringBuilder.toString();
    }

    public String buildIcoCategory(SecuredTaskBean task, String context) throws GranException {
        String imageServlet = "/ImageServlet/" + GeneralAction.SERVLET_KEY;
        return "<img alt=\"\" class=\"icon\" border=\"0\" src=\"" + context + imageServlet + "/icons/categories/" + task.getCategory().getIcon() + "\">";
    }

    private static class TaskViewer extends TaskViewText {
        private TaskViewer(SecuredTaskBean task) throws GranException {
            super(task);
        }

        @Override
        public TaskView getView(SecuredTaskBean task) throws GranException {
            return new TaskViewer(task);
        }

        @Override
        public String getName() throws GranException {
            return task.getName();
        }

        @Override
        public String getSimplePath() throws GranException {
            return task.getName();
        }

        @Override
        public String pathDelimiter() {
            return " / ";
        }
    }
}
