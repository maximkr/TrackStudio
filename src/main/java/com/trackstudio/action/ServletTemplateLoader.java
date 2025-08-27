package com.trackstudio.action;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;

import com.trackstudio.app.csv.CSVImport;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.AbstractPluginCacheItem;
import com.trackstudio.kernel.cache.PluginCacheItem;
import com.trackstudio.kernel.cache.PluginCacheManager;
import com.trackstudio.kernel.cache.PluginType;
import com.trackstudio.kernel.manager.FindManager;
import com.trackstudio.model.Template;
import com.trackstudio.tools.TemplateUtil;

public class ServletTemplateLoader implements freemarker.cache.TemplateLoader {
    String contextPath;

    public ServletTemplateLoader(String contextPath) {
        this.contextPath = contextPath;
    }

    public Object findTemplateSource(String string) throws IOException {

        String s = getTemplateSourceString(string);
        if (s == null) return "";
        else return s;
    }

    private String getTemplateSourceString(String string) {
        String requestUrl = contextPath + "/" + string;
        try {
            HashMap<String, String> templateParameters = TemplateUtil.parseTemplateURL(requestUrl);
            if (templateParameters.containsKey("template") && templateParameters.containsKey("task")) {
                String taskId = CSVImport.findTaskIdByNumber(templateParameters.get("task"));
                if (taskId != null) {
                    Template found = null;
                    for (Template t : FindManager.getTemplate().getTemplateList(taskId)) {
                        if (t.getName().equals(templateParameters.get("template")) &&
                                t.getActive() != null && t.getActive() == 1 && t.getUser() != null) {
                            found = t;
                            break;

                        }
                    }
                    if (found != null) {
                        String templateFolder = found.getFolder();
                        String template = null;

                        if (templateParameters.get("file") != null) {
                            String fileName = templateParameters.get("file");
                            if (fileName.length() > 0) template = templateFolder + fileName;
                            else template = templateFolder + "/index.ftl";
                        }
                        AbstractPluginCacheItem pci = PluginCacheManager.getInstance().find(PluginType.WEB, template);
                        return pci != null && ((PluginCacheItem) pci).getText() != null ? ((PluginCacheItem) pci).getText() : "";
                    }
                    return "";
                }
            }
        } catch (GranException e) {
        }

        return "";
    }

    public long getLastModified(Object object) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Reader getReader(Object object, String string) throws IOException {
        return new StringReader(object.toString());
    }

    public void closeTemplateSource(Object object) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
