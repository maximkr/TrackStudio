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
public class SimpleNameLink implements MacrosTaskAdapter {

    @Override
    public String convert(TaskView view, String description) throws GranException {
        SessionContext sc = view.getTask().getSecure();
        StringBuffer sb = new StringBuffer();
        Pattern filterPattern = Pattern.compile("(?<!#)#(\\d+)(?![\\{0-9\\w])");
        Matcher matcher = filterPattern.matcher(description);
        while (matcher.find()) {
            if (hasLink(description, matcher.start())) {
                continue;
            }

            String number = matcher.group(1);
            SecuredTaskBean tci = AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskByNumber(sc, number);
            if (tci != null) {
                String link = view.getView(tci).getNumber();
                matcher.appendReplacement(sb, Matcher.quoteReplacement(link));
            }
        }
        matcher.appendTail(sb);
        String result = sb.toString();
        return result.isEmpty() ? description : result;
    }

    private boolean hasLink(String description, int start) {
        if (description.lastIndexOf("<a", start) != -1 && description.indexOf("</a>", start) != -1) {
            int first = description.lastIndexOf("<a", start);
            int middle = description.indexOf("</a>", first);
            int end = description.indexOf("</a>", start);
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
