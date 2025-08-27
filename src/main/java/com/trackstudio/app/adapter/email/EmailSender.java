package com.trackstudio.app.adapter.email;

import java.util.Map;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.SenderAdapter;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.MailWriter;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.textfilter.MacrosUtil;

import freemarker.core.Environment;
import net.jcip.annotations.Immutable;

@Immutable
public class EmailSender implements SenderAdapter {
    private static final Log log = LogFactory.getLog(EmailSender.class);

    @Override
    public boolean send(SecuredUserBean user, SecuredTaskBean task, Environment env, String text, Map<String, String> files) {
        try {
            Session session = Config.getInstance().getSession();
            MailWriter mw = new MailWriter(session);

            AddressDictionary dictionary = AddressDictionary.createDictionary(user.getUser(), env, text);

            InternetAddress[] to = dictionary.getTo();
            if (to.length == 0) {
                return false;
            }

            String subject = dictionary.getSubject();
            Map<String, String> headers = dictionary.getHeaders();
            if (text != null) {
                headers.put("X-TrackStudio", "taskId: " + task.getId());

                Integer priorityValue = MacrosUtil.getIntegerOrNull(headers.get("X-Priority"));
                log.debug("Send e-mail subject : " + subject + " X-Priority : " + priorityValue);
                int priority = Null.isNotNull(priorityValue) ? priorityValue : 3;
                return mw.send(dictionary, priority, files);
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error", e);
        }
        return false;
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Email sender";
    }
}
