package com.trackstudio.app.adapter.macros;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.adapter.MacrosTaskAdapter;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.view.TaskView;

import net.jcip.annotations.Immutable;

@Immutable
public abstract class DoubleSharp implements MacrosTaskAdapter {
    public abstract String buildLink(TaskView view, SecuredTaskBean task) throws GranException;

    @Override
    public String convert(TaskView view, String description) throws GranException {
        SessionContext sc = view.getTask().getSecure();

        StringBuffer sb = new StringBuffer();
        Pattern filterPattern = Pattern.compile("##(\\d+)(?![\\{0-9\\w])");
        Matcher matcher = filterPattern.matcher(description);
        while (matcher.find()) {
            //String name = description.substring(matcher.start(), matcher.end());
            if (checkedLink(description, matcher.start(), matcher.end()-1) || checkedInsiteTag(description, matcher.start(), matcher.end()-1)) {
                continue;
            }
            String number = matcher.group(1);
            SecuredTaskBean tci = AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskByNumber(sc, number);
            if (tci != null) {
                String link = buildLink(view, tci);
                link = link.replaceAll("\\$","\\\\\\$");
                matcher.appendReplacement(sb, link);
            }
        }
        matcher.appendTail(sb);
        if (sb.toString().isEmpty()) {
            return description;
        }  else {
            return sb.toString();
        }
    }

    private boolean checkedLink(String description, int matchStart, int matchEnd) {
        if (description.lastIndexOf("<a", matchStart) != -1 && description.indexOf("</a>", matchEnd) != -1) {
            int first = description.lastIndexOf("<a",matchStart);
            int middle = description.indexOf("</a>", first);
            int end = description.indexOf("</a>", matchEnd);
            return middle == end;
        }
        return false;
    }

    private boolean checkedInsiteTag(String description, int matchStart, int matchEnd) {
        if (description.lastIndexOf("<", matchStart) != -1 && description.indexOf(">", matchEnd) != -1) {
            int first = description.lastIndexOf("<",matchStart);
            int middle = description.indexOf(">", first);
            int end = description.indexOf(">", matchEnd);
            return middle == end;
        }
        return false;
    }


    @Override
    public boolean init() {
        return true;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
