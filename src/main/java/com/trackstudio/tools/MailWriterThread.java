package com.trackstudio.tools;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.jcip.annotations.Immutable;


/**
 * Класс для отправки почты в отдельном потоке
 */
@Immutable
class MailWriterThread {

    private final static Log log = LogFactory.getLog(MailWriterThread.class);

    private final Session session;
    /**
     * Сообщение
     */
    private final Message message;

    /**
     * Отправляет почту
     */
    public void run() {
        try {
            final String host = session.getProperty("mail.smtp.host");
            final String login = session.getProperty("mail.smtp.user");
            final String password = session.getProperty("mail.smtp.password");
            final String port = session.getProperty("mail.smtp.port");

            final int iPort;
            if (port != null)
                iPort = Integer.parseInt(port);
            else
                iPort = 25;

	        final Session sess = Session.getInstance(this.session.getProperties(), new Authenticator() {

		        @Override
		        protected PasswordAuthentication getPasswordAuthentication() {
			        return new PasswordAuthentication(login, password);
		        }

	        });
	        Transport tr = sess.getTransport();
	        tr.connect(host, iPort, login, password);
	        tr.sendMessage(message, this.message.getAllRecipients());
	        tr.close();
        } catch (MessagingException e) {
            log.error("MessagingException", e);
        } catch (Exception e) {
            log.error("Cannot send e-mail", e);
        }
    }

    /**
     * Создает объект отправки почты в отдельном потоке
     *
     * @param sess сессия
     * @param mess сообщение
     */
    public MailWriterThread(Session sess, Message mess) {
        this.session = sess;
        this.message = mess;
    }
}