package com.trackstudio.jmx;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicLong;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.service.BaseStoreServiceAdapter;
import com.trackstudio.app.adapter.store.StoreClass;
import com.trackstudio.exception.MailImportException;
import com.trackstudio.jmx.beans.AbstractNotification;
import com.trackstudio.jmx.beans.IMailImportMXBean;
import com.trackstudio.model.MailImport;

import net.jcip.annotations.Immutable;

@Immutable
public class MailImportMXBeanImpl extends AbstractNotification implements IMailImportMXBean {
    private final AtomicLong sequenceNumber = new AtomicLong(0);
    private static final Log log = LogFactory.getLog(MailImportMXBeanImpl.class);
    private static final MailImportMXBeanImpl instance = new MailImportMXBeanImpl();
	private volatile BaseStoreServiceAdapter service;

    private MailImportMXBeanImpl() {}

    public static MailImportMXBeanImpl getInstance() {
        return instance;
    }

    public void treatementMsg(MimeMessage message) throws MailImportException {
        StringBuilder sb = new StringBuilder();
        try {
            String from = ((InternetAddress) (message.getFrom())[0]).getAddress();
            String name = ((InternetAddress) (message.getFrom())[0]).getPersonal();
            String subject = StoreClass.getSubject(message.getSubject());
            sb.append("from=").append(from).append(",");
            sb.append("name=").append(name).append(",");
            sb.append("subject=").append(subject).append(",");
            super.send(this, sequenceNumber.incrementAndGet(), "Import Mail", "Import Mail get MimeMessage : " + sb.toString());
        } catch (Exception e) {
            throw new MailImportException(e, "JMX", message);
        }
    }

    public void createTask(MimeMessage message, MailImport mailImport, String taskId) throws MessagingException, UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        String from = ((InternetAddress) (message.getFrom())[0]).getAddress();
        String name = ((InternetAddress) (message.getFrom())[0]).getPersonal();
        String subject = StoreClass.getSubject(message.getSubject());
        sb.append("from=").append(from).append(";");
        sb.append("name=").append(name).append(";");
        sb.append("subject=").append(subject).append(";");
        sb.append("mailImportId=").append(mailImport.getId()).append(";");
        sb.append("mailImportName=").append(mailImport.getName()).append(";");
        sb.append("taskId=").append(taskId).append(";");
        super.send(this, sequenceNumber.incrementAndGet(), "Import Mail", "Import Mail create task : " + sb.toString());
    }

    public void createMsg(MimeMessage message, MailImport mailImport, String taskId, String msgId) throws MessagingException, UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        String from = ((InternetAddress) (message.getFrom())[0]).getAddress();
        String name = ((InternetAddress) (message.getFrom())[0]).getPersonal();
        String subject = StoreClass.getSubject(message.getSubject());
        sb.append("from=").append(from).append(";");
        sb.append("name=").append(name).append(";");
        sb.append("subject=").append(subject).append(";");
        sb.append("mailImportId=").append(mailImport.getId()).append(";");
        sb.append("mailImportName=").append(mailImport.getName()).append(";");
        sb.append("taskId=").append(taskId).append(";");
        sb.append("msgId=").append(msgId).append(";");
        super.send(this, sequenceNumber.incrementAndGet(), "Import Mail", "Import Mail create msg : " + sb.toString());
    }

	public void registrationService(BaseStoreServiceAdapter service) {
		this.service = service;
	}

	public void startService() throws Exception {
		if (this.service != null) {
			this.service.execute();
		}
	}
}
