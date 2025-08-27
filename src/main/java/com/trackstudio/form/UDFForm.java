package com.trackstudio.form;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.action.user.UserEditAction;

public class UDFForm extends UploadForm {
    private static Log log = LogFactory.getLog(UserEditAction.class);
    public Map udf;
    public Map url;

    public UDFForm() {
        url = new HashMap();
        udf = new HashMap();
    }

    public Object getUdf(String key) {
        return udf.get(key);
    }

    public void setUdf(String key, Object value) {
        if (mutable) udf.put(key, value);
    }

    public String[] getUdflist(String key) {
        return (String[]) udf.get(key);
    }

    public void setUdflist(String key, String[] value) {
        if (mutable) udf.put(key, value);
    }

    public Object getUrl(String key) {
        return url.get(key);
    }

    public void setUrl(String key, Object value) {
        if (mutable) url.put(key, value);
    }
}
