package com.trackstudio.app.adapter.macros;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.trackstudio.app.adapter.MacrosTaskAdapter;
import com.trackstudio.exception.GranException;
import com.trackstudio.view.TaskView;

public class LotusConverter implements MacrosTaskAdapter {
    @Override
    public String convert(TaskView view, String text) throws GranException {
        StringBuffer sb = new StringBuffer();
        Matcher matcher = Pattern.compile("(<a)(.*?)(href=)('|\")(Notes://Lotus/)(.*?)('|\")(>)(.*?)(</a>)").matcher(text);
        while (matcher.find()) {
            String link = "notes://Lotus/" + matcher.group(6);
            String linkText = matcher.group(9);
            matcher.appendReplacement(sb, "<a href='"+link+"' target='_blank'>" + linkText + "</a>");
        }
        matcher.appendTail(sb);
        String result = sb.toString();
        return result.isEmpty() ? text : result;
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Lotus converter";
    }
}
