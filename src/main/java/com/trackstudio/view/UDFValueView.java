package com.trackstudio.view;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.Secured;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public abstract class UDFValueView {
    protected volatile SecuredUDFValueBean udfValue;

    public abstract String getValue(Secured o) throws GranException;

    protected String urlUDFtoHTML(String strValue) {
        String url = "";
        String description = "";
        if (strValue != null) {
            int j = strValue.indexOf('\n');
            if (j > 1 && j < strValue.length() - 1) {
                url = strValue.substring(0, j);
                description = strValue.substring(j);
            } else if (j == 0) {
                url = "";
                description = strValue;
            } else {
                url = strValue;
                description = strValue;

            }
        }

        url = url.replaceAll("\\\\", "/");
        if (url.indexOf("//") == 0)
            url = "file:" + url;
        return "<a target=\"_blank\" href=\"" + url + "\">" + Null.stripNullHtml(HTMLEncoder.encode(description)) + "</a>";

    }

    public static String urlUDFToText(String strValue) {
        String url = "";
        String description = "";
        if (strValue != null) {
            int j = strValue.indexOf('\n');
            if (j > 1 && j < strValue.length() - 1) {
                url = strValue.substring(0, j);
                description = strValue.substring(j+1);
            } else if (j == 0) {
                url = "";
                description = strValue;
            } else
                url = strValue;
        }
        return "["+description+"](" + url + ")";
    }

    public abstract String getCaption();
}
