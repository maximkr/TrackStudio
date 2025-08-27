package com.trackstudio.tools;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.email.AddressDictionary;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.MailImportException;
import com.trackstudio.exception.UserException;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.textfilter.MacrosUtil;

import net.jcip.annotations.Immutable;


/**
 * Класс используется для отправки сообщений через JavaMail<br>
 * SПоддерживает: <li>отправку text/plain и text/html сообщений
 * <li>отправку почту для получателей в полях To, CC и BCC
 * <li>отправку почты в различных кодировках
 */
@Immutable
public class MailWriter {
    /**
     * Сессия
     */
    private final Session session;
    private final static Log LOGGER = LogFactory.getLog(MailWriter.class);
    /**
     * Возвращает адрес по умолчанию
     *
     * @param name название
     * @return массив адресов
     * @throws GranException при необходимости
     */
    public InternetAddress[] getDefaultReplyTo(String name) throws GranException {
        String s = this.session.getProperty("mail.from");
        if (s != null && s.length() > 0) {
            try {
                if (name != null)
                    return new InternetAddress[]{new InternetAddress(s, name, Config.getInstance().getProperty("trackstudio.encoding"))};
                else {
                    //String sender =TSProperties.getInstance().getMailProperty(TSProperties.MAIL_SENDER_PROPERTY);//Config.getInstance().getProperty("trackstudio.sender");
                    String sender = Config.getInstance().getProperty("mail.sender");
                    //TSProperties.getInstance().getMailProperty(TSProperties.MAIL_SENDER_PROPERTY
                    if (sender != null)
                        return new InternetAddress[]{new InternetAddress(s, sender, Config.getInstance().getProperty("trackstudio.encoding"))};
                    else
                        return new InternetAddress[]{new InternetAddress(s, "TrackStudio", Config.getInstance().getProperty("trackstudio.encoding"))};
                }
            } catch (Exception e) {
                throw new GranException(e);
            }

        } else {
            return null;
        }
    }

    /**
     * Создает объект текущего класа на основании сессии
     *
     * @param s сессия
     */
    public MailWriter(Session s) {
        this.session = s;
    }

    public boolean send(AddressDictionary dictionary, int highPriority, Map<String, String> files) {
        try {
            LOGGER.trace("send");

            Message message = new MimeMessage(session);
            message.setHeader("X-Priority", String.valueOf(highPriority));
            message.setHeader("Content-Transfer-Encoding", "8bit");
            message.setHeader("precedence", "bulk");
            Map<String, String> xTrackStudio = dictionary.getHeaders();
            if (xTrackStudio != null) {
                for (String key : xTrackStudio.keySet()) {
                    message.setHeader(key, xTrackStudio.get(key));
                }
            }

            String subject = dictionary.getSubject();
            String body = dictionary.getBody();

            if (Null.isNull(subject)) {
                subject = "Subject is empty. Incorrect configuration";
                body = dictionary.getErrorInfo();
            }
            message.setSubject(MimeUtility.encodeText(subject, Config.getEncoding(), "B"));
            message.setSentDate(new Date());

            InternetAddress from = dictionary.getFrom();
            LOGGER.debug("MAIL.FROM : " + getDefaultReplyTo(null)[0]);
            LOGGER.debug("FROM : " + from.toString());
            if (from != null)
                message.setFrom(from);
            else
                message.setFrom(getDefaultReplyTo(null)[0]);

            InternetAddress reply = dictionary.getReply();
            if (reply != null)
                message.setReplyTo(new Address[]{reply});
            else
                message.setReplyTo(new Address[]{this.getDefaultReplyTo(null)[0]});

            InternetAddress[] to = dictionary.getTo();
            if (to != null && to.length > 0)
                message.setRecipients(Message.RecipientType.TO, to);

            InternetAddress[] cc = dictionary.getCc();
            if (cc != null && cc.length > 0)
                message.setRecipients(Message.RecipientType.CC, cc);

            InternetAddress[] bcc = dictionary.getBcc();
            if (bcc != null && bcc.length > 0)
                message.setRecipients(Message.RecipientType.BCC, bcc);

            MimeMultipart main = new MimeMultipart("related");
            List<Pair<byte[]>> images;
            StringBuffer sb = new StringBuffer();
            if (Config.isTurnItOn("trackstudio.email.image.base64")) {
                sb.append(body);
                images = new ArrayList<Pair<byte[]>>();
            } else {
                images = MacrosUtil.convertImagesToByte(body, sb);
            }
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(sb.toString(), dictionary.getContentType());
            mimeBodyPart.setHeader("Content-Transfer-Encoding", "8bit");
            main.addBodyPart(mimeBodyPart);

            for (Pair<byte[]> pair : images) {
                ByteArrayDataSource image = new ByteArrayDataSource(pair.getT(), "image/png");
                MimeBodyPart imgPart = new MimeBodyPart();
                imgPart.setFileName(pair.getKey() + ".png");
                imgPart.setHeader("Content-ID", "<"+pair.getKey()+">");
                imgPart.setDataHandler(new DataHandler(image));
                main.addBodyPart(imgPart);
            }
            //������� ������, ���� ��� ����
            if (files != null) {
                for (String file : files.keySet()) {
                    MimeBodyPart mimeBodyPart2 = new MimeBodyPart();
                    FileDataSource fileDataSource = new FileDataSource(file);
                    mimeBodyPart2.setDataHandler(new DataHandler(fileDataSource));
                    mimeBodyPart2.setFileName(MimeUtility.encodeText(files.get(file)));
                    main.addBodyPart(mimeBodyPart2);
                }
            }
            message.setContent(main);

            new MailWriterThread(session, message).run();
        } catch (Exception e) {
            LOGGER.error("Can't send email", e);
            return false;
        }
        return true;
    }

    /**
     * Отправляет сообщение
     *
     * @param from         отправитель
     * @param reply        куда слать ответ
     * @param to           кому шлем
     * @param cc           кому копия
     * @param bcc          кому дубликат копии
     * @param subject      Тема письма
     * @param body         Тело письма
     * @param contentType  тип контента
     * @param highPriority высокий ли приоритет
     * @param xTrackStudio тип сендера
     * @param files        прилоежнный файлы (если надо)
     * @return TRUE - если отправлено успешно, FALSE - если нет
     * @throws UserException при необходимости
     */
    public boolean send(InternetAddress from, InternetAddress reply, InternetAddress[] to, InternetAddress[] cc, InternetAddress[] bcc, String subject, String body, String contentType, int highPriority, HashMap<String, String> xTrackStudio, Map<String, String> files) throws UserException {
        try {
            AddressDictionary dictionary = AddressDictionary.createDictionary(from, reply, to, cc, bcc, subject, body, contentType, xTrackStudio);
            return send(dictionary, highPriority, files);
        } catch (Exception e) {
            LOGGER.error("Can't send email", e);
        }
        return false;
    }

    /**
     * Форвардинг почты
     *
     * @param to куда форвардить
     * @param ex что формардить
     * @throws GranException при необходимости
     */
    public void forward(Address to, MailImportException ex) throws GranException {
        try {
            LOGGER.debug("forward", new Throwable());
            Message message = new MimeMessage(session);
            message.setHeader("precedence", "bulk");
            message.setHeader("X-TrackStudio", "forwarded mail");
            message.setHeader("X-Priority", "1");

            message.setRecipient(Message.RecipientType.TO, to);

            Address[] rec = ex.getMimeMessage().getAllRecipients();
            message.setSubject("Forwarded from  " + ((InternetAddress) rec[0]).getAddress() + ": " + ex.getMimeMessage().getSubject());

            message.setSentDate(new Date());

            if (ex.getMimeMessage().getFrom() != null && ex.getMimeMessage().getFrom().length != 0)
                message.setFrom(ex.getMimeMessage().getFrom()[0]);
            else
                message.setFrom(new InternetAddress("unknown@localhost", "unknown@localhost", Config.getEncoding()));

            Multipart multipart = new MimeMultipart();
            if (ex.getMimeMessage().isMimeType("multipart/*")) {
                multipart = (Multipart) ex.getMimeMessage().getContent();
            } else {
                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                DataHandler dataHandler = new DataHandler(ex.getMimeMessage(), "message/rfc822");
                mimeBodyPart.setDataHandler(dataHandler);
                multipart.addBodyPart(mimeBodyPart);
            }
            try {
                StringWriter w = new StringWriter();
                PrintWriter p = new PrintWriter(w);
                if (ex.getMessage() != null)
                    p.write(ex.getMessage());
                p.write("\n");
                if (ex.getTSCause() != null)
                    ex.getTSCause().printStackTrace(p);
                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setContent(w.toString(), "text/plain");
                message.setText(w.toString());
                multipart.addBodyPart(mimeBodyPart);

            } catch (MessagingException me) {/*Empty*/}
            message.setContent(multipart);
            new MailWriterThread(session, message).run();
        } catch (Exception e) {
            throw new GranException(e, "ERROR_CAN_NOT_SEND_EMAIL");
        } catch (Throwable e) {
            LOGGER.error("THROWABLE ERROR FORWARD EMAIL: ", e);
        }
    }

    public void forward(MimeMessage me, Address[] address, String text, String contentType, String subject) throws GranException {
        try {
            LOGGER.debug("forward", new Throwable());
            Message message = new MimeMessage(session);
            message.setHeader("precedence", "bulk");
            message.setHeader("X-TrackStudio", "forwarded mail");
            message.setHeader("X-Priority", "1");
            message.setRecipients(Message.RecipientType.TO, address);
            message.setSubject(subject);
            message.setSentDate(new Date());
            if (me.getFrom() != null && me.getFrom().length != 0)
                message.setFrom(new InternetAddress(Config.getProperty("mail.from")));
            else
                message.setFrom(new InternetAddress("unknown@localhost", "unknown@localhost", Config.getEncoding()));
            Multipart multipart = new MimeMultipart();
            if (me.isMimeType("multipart/*")) {
                multipart = (Multipart) me.getContent();
            } else {
                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                DataHandler dataHandler = new DataHandler(me, "message/rfc822");
                mimeBodyPart.setDataHandler(dataHandler);
                multipart.addBodyPart(mimeBodyPart);
            }
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(text, contentType);
            mimeBodyPart.setHeader("Content-Transfer-Encoding", "8bit");
            multipart.addBodyPart(mimeBodyPart, 0);
            message.setContent(multipart);
            new MailWriterThread(session, message).run();
        } catch (Exception e) {
            throw new GranException(e, "ERROR_CAN_NOT_SEND_EMAIL");
        } catch (Throwable e) {
            LOGGER.error("THROWABLE ERROR FORWARD EMAIL: ", e);
        }
    }
}