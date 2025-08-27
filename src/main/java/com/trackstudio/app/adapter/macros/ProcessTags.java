package com.trackstudio.app.adapter.macros;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.id.UUIDHexGenerator;

import com.trackstudio.app.adapter.MacrosTaskAdapter;
import com.trackstudio.exception.GranException;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.MD5;
import com.trackstudio.view.TaskView;

import net.jcip.annotations.Immutable;

@Immutable
public class ProcessTags implements MacrosTaskAdapter {

    @Override
    public String convert(TaskView view, String description) throws GranException {
        if (description != null){
            int stylePos = description.indexOf("/* Style Definitions */");
            if (stylePos>-1) {

                String begin = description.substring(0, stylePos);
                String end = description.substring(stylePos, description.length());
                int lastIndexOf = begin.lastIndexOf("<!--");
                int indexOf = end.indexOf("-->");
                if (lastIndexOf != -1 && indexOf != -1) {
                    description = begin.substring(0, lastIndexOf) + end.substring(indexOf+"-->".length(), end.length());
                }
            }
        }
        return cutBasket(cutEmail(cutBaseLink(description)));
    }
    final static String MAIL_REGEX = "(ftp://)?(\\w+:)?(email:)?(mailto:)?(<)?(\\s+)?([_A-Za-z0-9-]+)(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})(\\s+)?(>)?(\\n)?";
    final static String BASKET_MAIL_REGEX = "(<)([_A-Za-z0-9-]+)(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})(>)";

    public static String cutBasket(String text) {
        return cut(new Template(BASKET_MAIL_REGEX, text) {
            @Override
            String block(String value) {
                return value.substring(1, value.length()-1);
            }
        });
    }

    /**
     * This method cuts the email by reg exp
     * @param word value
     * @return convert email to br
     */
    public static String cutEmail(String word) {
        return cut(new Template(MAIL_REGEX, word) {
            @Override
            String block(String value) {
                boolean hasBr = value.endsWith("\n");
                if (!(value.startsWith("ftp") || value.startsWith("email:"))) {
                    value = value.startsWith("mailto:") ? value.substring("mailto:".length()) : value;
                    if (value.contains("<") && value.contains(">")) {
                        value = value.substring(value.indexOf("<")+1, value.indexOf(">"));
                    }
                    value = value.trim();
                    if  (value.contains("github.com")) {
                        return value + (hasBr ? "</br>" : "");
                    } else {
                        return "<a href=\"mailto:"+value+"\">"+value+"</a>" + (hasBr ? "</br>" : "");
                    }
                } else {
                    return value;
                }
            }
        });
    }

    /**
     * Cut the base tag.
     * @param text Input text
     * @return text without base tag
     */
    public static String cutBaseLink(String text) {
        return cut(
                new Template("(<base)(.*?)(href=)('|\")(.*?)('|\")(.*?)(>)", text) {
                    @Override
                    String block(String value) {
                        return "";
                    }
                }
        );
    }

    private static String cut(Template template) {
        template.init();
        StringBuffer sb = new StringBuffer();
        Matcher matcher = Pattern.compile(template.pattern).matcher(template.text);
        while (matcher.find()) {
            String email = matcher.group();
            matcher.appendReplacement(sb, template.block(email));
        }
        matcher.appendTail(sb);
        if (!sb.toString().isEmpty()) {
            template.text = sb.toString();
        }
        return template.getResult();
    }

    private static abstract class Template {
        private String pattern;
        private String text;
        private Map<String, String> links = new LinkedHashMap<String, String>();

        protected Template(String pattern, String text) {
            this.pattern = pattern;
            this.text = text;
        }

        private void init() {
            StringBuffer sb = new StringBuffer();
            Matcher matcher = Pattern.compile("(<a)(.*?)(href=)('|\")(.*?)('|\")(>)(.*?)(</a>)").matcher(text);
            while (matcher.find()) {
                String id = MD5.encode((new UUIDHexGenerator()).generate(null, null).toString());
                matcher.appendReplacement(sb, id);
                String link = addBlank(matcher);
                links.put(id, link);
            }
            matcher.appendTail(sb);
            if (!sb.toString().isEmpty()) {
                text = sb.toString();
            }
        }

        private String addBlank(Matcher matcher) {
            String link = matcher.group(5);
            String valueProtocols = Config.getProperty("trackstudio.support.protocols", "ftp;ftps;http;https;Notes;notes;mailto");
            List<String> protocols = Arrays.asList(valueProtocols.split(";"));
            link = this.startWith(protocols, link) ? link : "http://" + link;
            String url = matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4) + link
                         + matcher.group(6)  + matcher.group(7)  + matcher.group(8)  + matcher.group(9);
            if (!url.contains("target")) {
                url = url.substring(0, url.indexOf("<a")) + "<a target='_blank' " + url.substring(url.indexOf("<a") + "<a".length(), url.length());
            }
            return url;
        }

        private boolean startWith(List<String> list, String value) {
            for (String key : list) {
                if (value.startsWith(key)) {
                    return true;
                }
            }
            return false;
        }

        private String getResult() {
            for (Map.Entry<String, String> entry : links.entrySet()) {
                text = text.replace(entry.getKey(), entry.getValue());
            }
            return text;
        }


        abstract String block(String value);
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
