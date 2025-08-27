package com.trackstudio.view;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.Secured;
import com.trackstudio.secured.SecuredUDFValueBean;

import net.jcip.annotations.Immutable;

@Immutable
public class UDFValueViewListReport extends UDFValueViewReport {
    public UDFValueViewListReport(SecuredUDFValueBean udfValue) {
        super(udfValue);
    }

    public String getValue(Secured o) throws GranException {
        return getValue(o, false);
    }

    protected String getURLView(Object va) {
        String url = "";
        if (va != null) {
            String strValue = va.toString();
            int j = strValue.indexOf('\n');
            if (j > 1 && j < strValue.length() - 1) {
                url = strValue.substring(0, j);
            } else if (j == 0) {
                url = "";
            } else
                url = strValue;
        }
        return url;
    }
}
