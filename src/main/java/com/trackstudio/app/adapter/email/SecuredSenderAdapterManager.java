package com.trackstudio.app.adapter.email;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.SenderAdapter;
import com.trackstudio.app.adapter.email.change.Change;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.AbstractPluginCacheItem;
import com.trackstudio.kernel.cache.PluginCacheItem;
import com.trackstudio.kernel.cache.PluginCacheManager;
import com.trackstudio.kernel.cache.PluginType;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.simple.Notification;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.Null;

import freemarker.cache.FileTemplateLoader;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import net.jcip.annotations.Immutable;

@Immutable
public class SecuredSenderAdapterManager {
    private static Log log = LogFactory.getLog(SecuredSenderAdapterManager.class);
    private final CopyOnWriteArrayList<SenderAdapter> am = new CopyOnWriteArrayList<SenderAdapter>();
    private final EmailSender emailSender = new EmailSender();
    /**
     * Constructor
     *
     * @param adapters adapters list
     */
    public SecuredSenderAdapterManager(List<SenderAdapter> adapters) {
        am.clear();
        am.addAll(adapters);
    }

    /**
     * This service is used for send email
     * @param type
     * @param user
     * @param task
     * @param data
     * @param reason
     * @param notification
     * @param toUser
     * @throws GranException
     */
    public void process(String type, SecuredUserBean user, SecuredTaskBean task, Map data, Change reason, Notification notification, String toUser) throws GranException {
        String emailTemplate = notification.getTemplate();
        StringWriter emailSb = new StringWriter();
        HashMap<String, String> emailFiles = new HashMap<String, String>();
        Environment emailEnv = initialTemplate(data, reason, notification, emailTemplate, emailSb, task, user, toUser, emailFiles);
        emailSender.send(user, task, emailEnv, emailSb.toString(), emailFiles);
    }

    private static Environment initialTemplate(Map data, Change reason, Notification notification, String nameTemplate, final StringWriter out, SecuredTaskBean task, SecuredUserBean user, String toUser, final HashMap<String, String> files) throws GranException {
        Environment env = null;
        try {
            String template = null;
            AbstractPluginCacheItem item = PluginCacheManager.getInstance().find(PluginType.EMAIL, Null.isNotNull(nameTemplate) ? nameTemplate : user.getTemplate());
            if (item != null) {
                template = ((PluginCacheItem) item).getText();
            }
            Configuration cfg = new Configuration();
            FileTemplateLoader wtl = new FileTemplateLoader(new File(Config.getInstance().getWebDir()));
            cfg.setTemplateLoader(wtl);
            cfg.addAutoImport("std", "std.ftl");
            String templateText = template != null ? template : "";
            if (templateText.contains("<#-- includeAttachments-->")) {
                files.putAll(SendMsgFJTask.getAttachmentsForTask(reason));
                templateText = templateText.replaceAll("<#-- includeAttachments-->", "");
            } else if (templateText.contains("<#-- includeAllAttachments-->")) {
                files.putAll(SendMsgFJTask.getAttachmentsForTask(task));
                templateText = templateText.replaceAll("<#-- includeAllAttachments-->", "");
            }
            StringReader sr = new StringReader(templateText);
            freemarker.template.Template temp = new freemarker.template.Template(toUser, sr, cfg);
            env = temp.createProcessingEnvironment(data, out, cfg.getObjectWrapper());
            env.process();
        } catch (Exception e) {
            log.error("E-mail notification error: " + notification.getTemplate(), e);
            throw new GranException(e);
        }
        out.flush();
        return env;
    }
}
