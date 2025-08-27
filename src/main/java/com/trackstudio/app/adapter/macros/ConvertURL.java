package com.trackstudio.app.adapter.macros;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.MacrosTaskAdapter;
import com.trackstudio.exception.GranException;
import com.trackstudio.view.TaskView;

import net.jcip.annotations.Immutable;

@Immutable
public class ConvertURL implements MacrosTaskAdapter {
    private static final Log log = LogFactory.getLog(ConvertURL.class);

    private static final Pattern nodeReg = Pattern.compile("(<a)(.*?)(id=\"webfx-tree-object)(.*?)(>)(.*?)(</a>)");

    private static final String regex = "(^|[\\s(<br\\s*/?>)(&nbsp;).,(&lt;)<])        # boundary\n" +
            "(                          # capture to $2\n"
            + "(https|http|telnet|gopher|file|wais|ftp) : \n"
            + "                            # resource and colon\n"
            + "[\\w/\\#~:;.,?+=&%@!\\-] +?   # one or more valid\n"
            + "                            # characters\n"
            + "                            # but take as little\n"
            + "                            # as possible\n"
            + ")\n"
            + "(?=                         # lookahead\n"
            + "[,.:;?\\->] *                  # for possible punc\n"
            + "((<a(.*)>)(</a>)?: [^\\w/\\#~:;.,?+=&%@!\\-(&gt;)>] # invalid character\n"
            + "| $ )                       # or end of string\n"
            + ")";

    private static final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE + Pattern.COMMENTS);

    @Override
    public String convert(TaskView view, String description) throws GranException {
        log.trace("parseURL");
        description = convertNodeFromTree(description);
        Matcher m = pattern.matcher(description);
        return m.replaceAll("$1<a target=\"_blank\" href=\"$2\">$2</a>");
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public String getDescription() {
        return null;
    }

    public String convertNodeFromTree(String description) {
        StringBuffer sb = new StringBuffer();
        Matcher matcher = nodeReg.matcher(description);
        while (matcher.find()) {
            String node = matcher.group();
            String title = node.substring(node.indexOf("title=\"") + "title=\"".length());
            title = title.substring(0, title.indexOf(" "));
            title = "##" + title.replaceAll("[^\\d+]", "");
            matcher.appendReplacement(sb, title);
        }
        matcher.appendTail(sb);
        if (sb.toString().isEmpty()) {
            return description;
        }  else {
            return sb.toString();
        }
    }
}
