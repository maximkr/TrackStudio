package com.trackstudio.app.adapter.macros;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.trackstudio.app.adapter.MacrosTaskAdapter;
import com.trackstudio.exception.GranException;
import com.trackstudio.view.TaskView;
import com.trackstudio.view.TaskViewEmailHTML;

import static com.trackstudio.tools.textfilter.HTMLEncoder.escape2HTML;
import static com.trackstudio.tools.textfilter.HTMLEncoder.html2Text;

public class SyntaxHighlighter implements MacrosTaskAdapter {

    @Override
    public String convert(TaskView view, String current) throws GranException {
        StringBuffer sb = new StringBuffer();
        if (current != null) {
            Matcher matcher = Pattern.compile("(\\[code=)(\\w+)(\\])([^\\[]*)(\\[/code\\])").matcher(current);
            while (matcher.find()) {
                String source = matcher.group(4);
                if (!(view instanceof TaskViewEmailHTML)) {
                    source = html2Text(escape2HTML(source));
                    source = source.replaceAll("(<pre>)|(</pre>)", "");
                }
                matcher.appendReplacement(sb, "<pre class=\"brush: " + matcher.group(2) + "\">" + Matcher.quoteReplacement(source) + "</pre>");
            }
            matcher.appendTail(sb);
            if (!sb.toString().isEmpty()) {
                current = sb.toString();
            }
        }
        return current;
    }



    @Override
    public boolean init() {
        return true;
    }

    @Override
    public String getDescription() {
        return "";
    }
}
