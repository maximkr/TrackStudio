package com.trackstudio.app.adapter.email;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.action.TemplateServlet;
import com.trackstudio.app.adapter.store.ResultImport;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.app.session.SessionManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.PluginCacheManager;
import com.trackstudio.kernel.cache.PluginType;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.MailWriter;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.textfilter.MacrosUtil;

import freemarker.cache.FileTemplateLoader;
import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelIterator;
import net.jcip.annotations.Immutable;

@Immutable
public class EmailUtil {
    private static final Log log = LogFactory.getLog(EmailUtil.class);

    public static Environment buildTemplate(String name, Map<String, Object> datamodel, final Writer out, PluginType type) throws GranException, TemplateException, IOException {
        String template = PluginCacheManager.getInstance().getText(type, name);
        Configuration cfg = new Configuration();
        FileTemplateLoader wtl = new FileTemplateLoader(new File(Config.getInstance().getWebDir()));
        cfg.setTemplateLoader(wtl);
        cfg.addAutoImport("std", "std.ftl");
        StringReader sr = new StringReader(template);
        Template temp = new Template("root", sr, cfg);
        Environment env = temp.createProcessingEnvironment(datamodel, out, cfg.getObjectWrapper());
        env.process();
        out.flush();
        return env;
    }

    @Immutable
    private static class DataEmail {
        private final ConcurrentMap<String, Object> datamodel = new ConcurrentHashMap<String, Object>();

        private DataEmail() throws Exception {
            datamodel.put("charset", Config.getEncoding());
            datamodel.put("link", Config.getInstance().getSiteURL());
            datamodel.put(TemplateServlet.I18N, BeansWrapper.getDefaultInstance().wrap(I18n.getInstance()));
        }

        public void addData(String key, Object value) {
            datamodel.put(key, value);
        }

        public Map<String, Object> getDatamodel() {
            return datamodel;
        }
    }

    public static Map<String, Object> buildDataForUser(UserRelatedInfo user, String pwd) throws Exception {
        String sessionId = SessionManager.getInstance().create(user);
        SessionContext sc = SessionManager.getInstance().getSessionContext(sessionId);
        DataEmail dataEmail = new DataEmail();
        dataEmail.addData("user", new SecuredUserBean(user, sc));
        dataEmail.addData("acls", SecuredUserBean.getPrstatusForTaskId(sc, user.getId()));
        dataEmail.addData("company", user.getCompany() != null ? user.getCompany() : "");
        dataEmail.addData("session", BeansWrapper.getDefaultInstance().wrap(sc));
        dataEmail.addData("password", pwd);
        return dataEmail.getDatamodel();
    }

    public static InternetAddress[] buildTo(UserRelatedInfo userRelatedInfo) throws Exception {
        ArrayList<String> al = userRelatedInfo.getEmailList();
        InternetAddress[] to = new InternetAddress[al.size()];
        int i = 0;
        for (String s : al) {
            to[i] = new InternetAddress(s, userRelatedInfo.getName(), Config.getProperty("trackstudio.encoding"));
            i++;
        }
        return to;
    }

    private static String buildSubject(Environment env) throws Exception {
        String subject = "";
        TemplateModel subjectModel = env.getVariable("Subject");
        if (subjectModel != null) {
            subject = ((SimpleScalar) subjectModel).getAsString();
        }
        return subject;
    }

    private static HashMap<String, String> buildHeader(Environment env) throws Exception {
        HashMap<String, String> headers = new HashMap<String, String>();
        TemplateModel headersModel = env.getVariable("Headers");
        if (headersModel != null) {
            TemplateHashModelEx headersM = ((TemplateHashModelEx) headersModel);
            TemplateCollectionModel keys = headersM.keys();
            for (TemplateModelIterator it = keys.iterator(); it.hasNext(); ) {
                SimpleScalar model = (SimpleScalar) it.next();
                SimpleScalar value = (SimpleScalar) headersM.get(model.getAsString());
                headers.put(model.getAsString(), value.getAsString());
            }
        }
        return headers;
    }

    public static void sendEmail(Map<String, Object> dataEmail, InternetAddress[] to, String template, UserRelatedInfo user) throws Exception {
        StringWriter out = new StringWriter();
        Environment env = buildTemplate(template, dataEmail, out, PluginType.EMAIL);
        String msgbody = out.toString();
        Session ses = Config.getInstance().getSession();
        MailWriter mw = new MailWriter(ses);
        AddressDictionary dictionary = AddressDictionary.createDictionary(user, env, msgbody);
        String subject = buildSubject(env);
        HashMap<String, String> headers = buildHeader(env);
        Integer priorityValue = MacrosUtil.getIntegerOrNull(headers.get("X-Priority"));
        int priority = Null.isNotNull(priorityValue) ? priorityValue : 3;
        mw.send(dictionary.getFrom(), dictionary.getReply(), to, dictionary.getCc(), dictionary.getBcc(), subject, msgbody, buildContentType(), priority, headers, null);
        ses.getDebugOut().flush();
    }

    public static void forwardEmail(MimeMessage message, String template, ResultImport msg) throws Exception {
        Session ses = Config.getInstance().getSession();
        DataEmail dataEmail = new DataEmail();
        dataEmail.addData("reason", msg.getMsg().getMessage("en"));
        dataEmail.addData("details", msg.getText());
        StringBuilder sb = new StringBuilder();
        for (Address address : message.getAllRecipients()) {
            sb.append(((InternetAddress) address).getAddress());
        }
        dataEmail.addData("from", sb.toString());
        dataEmail.addData("subjectText", MimeUtility.encodeText(message.getSubject(), Config.getEncoding(), "B"));
        StringWriter out = new StringWriter();
        Environment env = buildTemplate(template, dataEmail.getDatamodel(), out, PluginType.EMAIL);
        String msgbody = out.toString();
        MailWriter mw = new MailWriter(ses);
        if (Config.getInstance().isForwardUnprocessed()) {
            mw.forward(message, buildAddress(message), msgbody, buildContentType(), buildSubject(env));
        }
        ses.getDebugOut().flush();
    }

    private static Address[] buildAddress(MimeMessage message) throws MessagingException {
        BuilderInternetAddress address = new BuilderInternetAddress();
        address.add(Config.getInstance().getForwardEmail());
        if (Config.isTurnItOn("trackstudio.forward.to.user.email")) {
            Address[] addresses = message.getFrom();
            if (addresses != null) {
                for (Address ad : addresses) {
                    address.add(((InternetAddress) ad).getAddress());
                }
            }
        }
        return address.getList().toArray(new Address[address.getList().size()]);
    }

    @Immutable
    private static class BuilderInternetAddress {
        private final CopyOnWriteArrayList<InternetAddress> list = new CopyOnWriteArrayList<InternetAddress>();

        public void add(String addr) {
            try {
                list.add(new InternetAddress(addr, addr, Config.getEncoding()));
            } catch (Exception e) {
                log.debug(" Error parse email address : " + addr);
            }
        }

        public List<InternetAddress> getList() {
            return list;
        }
    }

    private static String buildContentType() {
        return "text/html; charset=\"".concat(Config.getEncoding()).concat("\"");
    }
}
