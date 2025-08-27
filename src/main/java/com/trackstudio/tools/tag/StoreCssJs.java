package com.trackstudio.tools.tag;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.tools.MD5;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class StoreCssJs {
    private static final Log log = LogFactory.getLog(StoreCssJs.class);
    private final String webappPath;
    private static StoreCssJs instance;
    private final ConcurrentMap<String, String> files = new ConcurrentHashMap<String, String>();

    private StoreCssJs(String path) {
        this.webappPath = path;
    }

    public static StoreCssJs getInstance() {
        return instance;
    }

    public static void init(String path) {
        instance = new StoreCssJs(path);
    }

    public String appendFile(List<String> list) {
        String key = generateKey(list);
        if (files.containsKey(key))
            return key;

        log.debug("Generate key " + key + " for file list " + Arrays.toString(list.toArray()));
        try {
            StringBuilder sb = new StringBuilder();
            for (String file : list) {
                sb.append(FileUtils.readFileToString(new File(webappPath + file))).append("\n");
            }
            files.putIfAbsent(key, sb.toString());
        } catch (Exception e) {
            log.error("Error in appendFile", e);
        }
        return key;
    }

    public String getData(String id) {
        String ret = files.get(id);
        if (ret != null) {
            return ret;
        } else {
            log.error("Cannot find data for key:" + id);
            return null;
        }
    }

    private static String generateKey(List<String> list) {
        List<String> temp = new ArrayList<String>(list);
        Collections.sort(temp);
        StringBuilder sb = new StringBuilder();
        for (String value : temp) {
            sb.append(value);
        }
        return MD5.encode(sb.toString() + GeneralAction.SERVLET_KEY);
    }
}
