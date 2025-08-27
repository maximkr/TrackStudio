package com.trackstudio.app.adapter.email;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.Null;

import freemarker.core.Environment;
import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateSequenceModel;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class AddressDictionary {
    private volatile InternetAddress[] to;
    private volatile InternetAddress[] cc;
    private volatile InternetAddress[] bcc;
    private volatile InternetAddress from;
    private volatile InternetAddress reply;
    private final Environment env;
    private volatile UserRelatedInfo user;
    private volatile String contentType;
    private ConcurrentMap<String, String> headers = new ConcurrentHashMap<String, String>();
    private volatile String subject;
    private volatile String body;

    private AddressDictionary() {
        this.env = null;
    }

    private AddressDictionary(Environment env) {
        this.env = env;
    }

    public static AddressDictionary createDictionary(InternetAddress from, InternetAddress reply,
                                                     InternetAddress[] to, InternetAddress[] cc,
                                                     InternetAddress[] bcc, String subject,
                                                     String body, String contentType, HashMap<String, String> headers) throws GranException {
        AddressDictionary dictionary = new AddressDictionary();
        dictionary.from = from;
        dictionary.reply = reply;
        dictionary.to = to;
        dictionary.cc = cc;
        dictionary.bcc = bcc;
        dictionary.subject = subject;
        dictionary.body = body;
        dictionary.contentType = contentType;
        dictionary.headers = new ConcurrentHashMap(Null.removeNullElementsFromMap(headers));
        return dictionary;
    }

    public static AddressDictionary createDictionary(UserRelatedInfo user, Environment env, String body) throws GranException {
        AddressDictionary dictionary = new AddressDictionary(env);
        dictionary.user = user;
        dictionary.body = body;
        dictionary.init();
        return dictionary;
    }

    private void init() throws GranException {
        try {
            initTo();
            initFrom();
            initReplay();
            initCc();
            initBcc();
            initSubject();
            initContentType();
            initHeaders();
        } catch (Exception e) {
            throw new GranException(e);
        }
    }

    private void initHeaders() throws TemplateModelException {
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
    }

    private void initContentType() throws TemplateModelException {
        this.contentType = "text/plain;\n charset=\"" + Config.getEncoding() + "\"";
        TemplateModel content = env.getVariable("ContentType");
        if (content != null) {
            contentType = ((SimpleScalar) content).getAsString();
        }
    }

    private void initSubject() throws TemplateModelException {
        TemplateModel subjectModel = env.getVariable("Subject");
        if (subjectModel != null) {
            this.subject = ((SimpleScalar) subjectModel).getAsString();
        }
    }

    private List<InternetAddress> parseEmail(TemplateSequenceModel col) throws TemplateModelException, AddressException {
        List<InternetAddress> addresses = new ArrayList<InternetAddress>();
        if (col.size() > 0) {
            for (int i = 0; i < col.size(); i++) {
                String address = ((StringModel) col.get(i)).getAsString();
                addresses.add(new InternetAddress(address));
            }
        }
        return addresses;
    }

    private List<InternetAddress> parseEmail(TemplateModel toAddress) throws AddressException, TemplateModelException {
        List<InternetAddress> addresses = new ArrayList<InternetAddress>();
        if (toAddress != null) {
            if (toAddress instanceof TemplateSequenceModel) {
                addresses.addAll(parseEmail((TemplateSequenceModel) toAddress));
            } else {
                addresses.addAll(parseEmail((SimpleScalar) toAddress));
            }
        }
        return addresses;
    }

    private List<InternetAddress> parseEmail(SimpleScalar toAddress) throws AddressException {
        List<InternetAddress> addresses = new ArrayList<InternetAddress>();
        String address = toAddress.getAsString();
        if (Null.isNotNull(address)) {
            for (String adds : address.split(",")) {
                addresses.add(new InternetAddress(adds));
            }
        }
        return addresses;
    }

    private void initTo() throws UnsupportedEncodingException, TemplateModelException, AddressException {
        ArrayList<String> al = user.getEmailList();
        List<InternetAddress> addresses = parseEmail(env.getVariable("TO"));
        for (int i=0;i!=al.size();++i) {
            addresses.add(new InternetAddress(al.get(i), user.getName(), Config.getProperty("trackstudio.encoding")));
        }
        this.to = addresses.toArray(new InternetAddress[addresses.size()]);
    }

    private void initFrom() throws TemplateModelException, UnsupportedEncodingException, AddressException {
        TemplateModel fromEmail = env.getVariable("FromEmail");
        TemplateModel fromUser = env.getVariable("FromUser");
        String emailFrom = fromEmail != null ? ((SimpleScalar) fromEmail).getAsString() : Config.getProperty("mail.from");
        String userName = fromUser != null ? ((SimpleScalar) fromUser).getAsString() : emailFrom;
        from = new InternetAddress(emailFrom, userName, Config.getEncoding());
    }

    private void initReplay() throws AddressException, TemplateModelException {
        TemplateModel replyAddress = env.getVariable("ReplyTo");
        if (replyAddress != null) {
            String address = ((SimpleScalar) replyAddress).getAsString();
            reply = new InternetAddress(address);
        }
    }

    private InternetAddress[] convert2Array(List<InternetAddress> addresses) {
        return addresses.toArray(new InternetAddress[addresses.size()]);
    }

    /**
     * This method inits CC addresses
     * @throws TemplateModelException for necessary
     * @throws AddressException for necessary
     */
    private void initCc() throws TemplateModelException, AddressException {
        this.cc = convert2Array(parseEmail(env.getVariable("CC")));
    }

    private void initBcc() throws TemplateModelException, AddressException {
        this.bcc = convert2Array(parseEmail(env.getVariable("BCC")));
    }

    public InternetAddress[] getCc() {
        return cc;
    }

    public InternetAddress[] getBcc() {
        return bcc;
    }

    public InternetAddress getFrom() {
        return from;
    }

    public InternetAddress getReply() {
        return reply;
    }

    public InternetAddress[] getTo() {
        return to;
    }

    public String getContentType() {
        return contentType;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getErrorInfo() {
        StringBuilder sb = new StringBuilder("Subject is empty. Incorrect configuration");
        if (this.user != null) {
            sb.append(" User[login=").append(this.user.getLogin()).append(", emails=").append(this.user.getEmailList()).append("]");
        }
        sb.append("</br>").append(body);
        return sb.toString();
    }
}