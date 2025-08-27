package com.trackstudio.app.adapter;

import java.util.Map;

import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;

import freemarker.core.Environment;

public interface SenderAdapter extends Adapter {
    public boolean send(SecuredUserBean user, SecuredTaskBean task, Environment env, String text, Map<String, String> files);
}
