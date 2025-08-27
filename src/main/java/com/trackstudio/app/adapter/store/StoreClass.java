package com.trackstudio.app.adapter.store;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.mail.Address;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.codec.net.QuotedPrintableCodec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.email.EmailUtil;
import com.trackstudio.constants.AttachmentConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.MailImportException;
import com.trackstudio.kernel.manager.AttachmentArray;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.MailWriter;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.textfilter.HTMLEncoder;
import com.trackstudio.tools.textfilter.MacrosUtil;

import net.jcip.annotations.Immutable;

import static com.trackstudio.tools.Null.isNotNull;

/**
 * Основной класс для работы с импортом почты
 */
@Immutable
public class StoreClass {

    private static final Log log = LogFactory.getLog(StoreClass.class);

    private static final String QUOTED = "=?UTF-8?Q?";
    private static final QuotedPrintableCodec CODEC = new QuotedPrintableCodec();

    /**
     * Возвращает заголовок письма
     *
     * @param subject текущее сообщение
     * @return заголовок письма
     * @throws MessagingException           при необходимости
     * @throws UnsupportedEncodingException при необходимости
     */
    public static String getSubject(String subject) {
        subject = subject == null ? "" : subject;
        try {
            String subj = subject;
            int pos = subj.indexOf(":");
            if (pos > 0) {
                String prefix = subj.substring(0, pos);
                String s = prefix.toUpperCase(Locale.ENGLISH);
                if (s.equals("RE") || s.equals("FW") || s.equals("FWD"))
                    subj = subj.substring(pos + 1);
            }
            subject = MimeUtility.decodeText(subj);
            if (subject.startsWith(QUOTED)) {
                subject = subject.substring(QUOTED.length()).replaceAll("= ", "");
                subject = CODEC.decode(subject, "UTF-8");
            }
        } catch (Exception e) {
            log.error(String.format("Convert email subject : %s", subject), e);
        }
        return subject;
    }

    /**
     * Возвращает тело письма
     *
     * @param currentMessage сообщение
     * @return тело письма
     * @throws GranException при необходимости
     */
    public static MimeBodyPart getMultiBodyPart(MimeMessage currentMessage) throws GranException {
        MimeBodyPart content = null;
        try {
            if (currentMessage.isMimeType("multipart/*")) {
                // log.debug("Mime type is multipart/*");
                MimeMultipart multipart = (MimeMultipart) currentMessage.getContent();
                for (int l = 0; l < multipart.getCount(); l++) {
                    MimeBodyPart p = (MimeBodyPart) multipart.getBodyPart(l);
                    content = diggBodyFromGrave(p);
                    if (content != null) return content;
                }
            }
        } catch (UnsupportedEncodingException uee) {
            log.debug("UnsupportedEncodingException");
            log.debug(uee.toString(), uee);
        } catch (Exception e) {
            log.debug("Whatever exeption");
            throw new GranException(e);
        }
        return content;
    }

    /**
     * Возвращает нужную часть пиьсма, если письмо многачастевое
     *
     * @param currentPart текущаа часть
     * @return новая нужная часть
     * @throws GranException при необходимости
     */
    public static MimeBodyPart diggBodyFromGrave(MimeBodyPart currentPart) throws GranException {
        MimeBodyPart content = null;
        try {
            if (currentPart.isMimeType("multipart/*")) {
                log.debug("Mime type is multipart/*");
                MimeMultipart multipart = (MimeMultipart) currentPart.getContent();
                for (int j = 0; j < multipart.getCount(); j++) {
                    MimeBodyPart p = (MimeBodyPart) multipart.getBodyPart(j);
                    String file_name = p.getFileName();
                    if ((p.isMimeType("text/*")) && (file_name == null || file_name.length() == 0)) { //
                        log.debug("MimeBodyPart's type is text/plain, and it is not attach file");
                        content = p;
                        return content;
                    } else {
                        log.debug("Mime type is multipart but this part is not text, but " + p.getContentType());

                        if (p.isMimeType("multipart/*")) {
                            log.debug("MimeBodyPart's type is multipart/*, lets try to find text/plain");
                            content = diggBodyFromGrave(p);
                            if (content != null)
                                return content;
                        }
                    }
                }
                //log.debug("Going to return null");
            } else if ((currentPart.isMimeType("text/*")) && (currentPart.getFileName() == null || currentPart.getFileName().length() == 0)) { //
                //log.debug("MimeBodyPart's type is text/plain, and it is not attach file");
                content = currentPart;
                return content;
            }
        } catch (UnsupportedEncodingException uee) {
            log.debug("UnsupportedEncodingException");
            log.debug(uee.toString(), uee);
        } catch (Exception e) {
            log.debug("Whatever exeption");
            throw new GranException(e);
        }
        return content;
    }

    /**
     * Возвращает приложенные файлы для письма
     *
     * @param currentMessage сообщение
     * @return список приложенных файлов
     * @throws GranException при необходимости
     */
    public static ArrayList<AttachmentArray> getAttaches(MimeMessage currentMessage) throws GranException {
        log.trace("getAttaches");
        List<Pair<InputStream>> attaches = new ArrayList<Pair<InputStream>>();
        try {
            if (currentMessage.isMimeType("multipart/*")) {
                // log.debug("Mime type is multipart/*");
                MimeMultipart multipart = (MimeMultipart) currentMessage.getContent();
                parseBodyPart(multipart, 0, attaches);
            }
            // convert hashmap to list
            ArrayList<AttachmentArray> atts = new ArrayList<AttachmentArray>();
            for (Pair<InputStream> pair : attaches) {
                String description = AttachmentConstants.DEFAULT_DESCRIPTION;
                BufferedInputStream in = new BufferedInputStream(pair.getT());
                AttachmentArray arr = new AttachmentArray(SafeString.createSafeString(HTMLEncoder.safe(pair.getKey())), SafeString.createSafeString(HTMLEncoder.safe(description)), in);
                arr.setContext(Config.getProperty("trackstudio.siteURL"));
                arr.setTinyMCEImage(pair.isBoolValue());
                arr.setContentId(pair.getValue());
                atts.add(arr);
            }

            return atts;

        } catch (Exception e) {
            log.error("Error", e);
            throw new GranException(e);
        }
    }

    private static void parseBodyPart(MimeMultipart multipart, int counter, List<Pair<InputStream>> attaches) throws MessagingException, IOException {
        // handleMultipart(multipart);
        for (int i = 0; i < multipart.getCount(); i++) {
            MimeBodyPart p = (MimeBodyPart) multipart.getBodyPart(i);
            String fileName = null;
            try {
                fileName = p.getFileName();
            } catch (MessagingException e) {
                log.debug("File name exception " + e.getMessage());

            }
            if ((!p.isMimeType("text/*")) || (fileName != null && fileName.length() != 0)) {

                // log.debug("Attach file name before is " + fileName);
                if (fileName != null)
                    fileName = decode(fileName);
                else {
                    counter++;
                    fileName = "file" + (counter);
                    if (p.isMimeType("text/plain"))
                        fileName += ".txt";
                    if (p.isMimeType("text/html")) {
                        fileName += ".html";
                    }
                }

                // log.debug("Attach file name after is " + fileName);
                if (!p.isMimeType("multipart/*") && fileName != null && fileName.length() > 0) {
                    log.debug("Try get all headers from email");
                    Enumeration headers = p.getAllHeaders();
                    while (headers.hasMoreElements()) {
                        Header h = (Header) headers.nextElement();
                        log.debug(String.format("%s %s", h.getName(), h.getValue()));
                    }
                    String contentId = p.getHeader("Content-ID", ";");
                    if (contentId == null) {
                        contentId = p.getHeader("X-Attachment-Id", "");
                    }

                    String ext = p.getHeader("Content-Type", ";");
                    fileName = cutAttachName(fileName, ext != null ? ext : "", 200);
                    if (isNotNull(contentId)) {
                        contentId = contentId.substring(1, contentId.length()-1);
                    }
                    attaches.add(new Pair<InputStream>(fileName, contentId, p.getInputStream(), isNotNull(contentId)));
                    log.debug("Attach file founded size " + p.getSize());
                } else {
                    try {
                        MimeMultipart multipart2 = (MimeMultipart) p.getContent();
                        parseBodyPart(multipart2, counter, attaches);
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static String cutAttachName(String fileName, String ext, int limit) {
        if (!fileName.contains(".")) {
            // Outlook does not contain valid file name
            if (ext.contains("rfc822")) {
                fileName += ".msg";
            } else if (ext.contains("msword")) {
                fileName += ".doc";
            } else {
                fileName += "." + ext.substring(ext.indexOf("/") + 1, ext.length());
            }
        }
        if (fileName.length() > limit) {
            if (fileName.contains(".")) {
                int index = fileName.lastIndexOf(".");
                fileName = fileName.substring(0, limit - (fileName.length() - index)) +
                        fileName.substring(index);
            } else
                fileName = fileName.substring(0, limit);
        }
        return fileName;
    }

    public static void handleMultipart(Multipart multipart) throws MessagingException, IOException {
        for (int i=0, n=multipart.getCount(); i<n; i++) {
            handlePart(multipart.getBodyPart(i));
        }
    }

    public static void handlePart(Part part) throws MessagingException, IOException {
        String disposition = part.getDisposition();
        String contentType = part.getContentType();
        if (disposition == null) { // When just body
            log.debug("Null: "  + contentType);
            // Check if plain
            if ((contentType.length() >= 10) &&
                    (contentType.toLowerCase().substring(
                            0, 10).equals("text/plain"))) {
                part.writeTo(System.out);
            } else { // Don't think this will happen
                System.out.println("Other body: " + contentType);
                part.writeTo(System.out);
            }
        } else if (disposition.equalsIgnoreCase(Part.ATTACHMENT)) {
            log.error("Attachment: " + part.getFileName() + " : " + contentType);
        } else if (disposition.equalsIgnoreCase(Part.INLINE)) {
            log.error("Inline: " + part.getFileName() + " : " + contentType);
        } else {  // Should never happen
            log.error("Other: " + disposition);
        }
    }

    /**
     * MIME decode
     *
     * @param input input text
     * @return decoded text
     */
    private static String decode(String input) {
        try {
            if (input.contains("\t")) {
                input = input.substring(0, input.indexOf("\t"));
            }
            if (input.indexOf("==?=") != -1) //Outgluk
                return MimeUtility.decodeText(input);
            StringBuffer result = new StringBuffer();//Mozilla
            if (input.indexOf("==") == -1) result.append(MimeUtility.decodeText(input));
            else {
                while (true) {
                    int i = input.indexOf("==");
                    if (i == -1)
                        break;
                    result.append(MimeUtility.decodeText(input.substring(0, i + 1)));
                    input = input.substring(i + 1);
                }
                result.append(MimeUtility.decodeText(input));
            }
            return result.toString();
        } catch (Exception e) {
            return "File";
        }
    }

    /**
     * Разбирает строку и ищет в ней номер задачи
     *
     * @param string исходная строка
     * @return ID задачи
     * @throws GranException при необходимости
     */
    public static String parseTaskNumber(String string) throws GranException {
        int number = MacrosUtil.parseNumberString(string);
        if (number != -1) {
            String taskId = KernelManager.getTask().findByNumber(String.valueOf(number));
            if (taskId == null) {
                log.debug("task with number " + number + " not found");
            }
            return taskId;
        } else {
            return null;
        }
    }

    /**
     * Перенаправляет необработанную почту
     *
     * @param ex ошибка импорта
     */
    public static void forwardUnprocessed(MailImportException ex) {
        log.trace("forwardUnprocessed caled");
        if (ex.getMimeMessage() == null) {
            log.error("message is null");
        }
        Session session = Config.getInstance().getSession();
        try {
            MailWriter mw = new MailWriter(session);
            if (Config.getInstance().isForwardUnprocessed()) {
                log.debug("forward current message");
                String addr = Config.getInstance().getForwardEmail();
                mw.forward(new InternetAddress(addr, addr, Config.getEncoding()), ex);
            }
        } catch (GranException e) {
            log.debug("Exception: " + e.getMessage());
            String errorFrom = "[none]";
            String subject;
            String from = errorFrom;
            try {
                errorFrom = ex.getMimeMessage().getHeader("From", null);
                Address[] aFrom = ex.getMimeMessage().getFrom();
                if (aFrom != null) {
                    from = ((InternetAddress) aFrom[0]).getAddress();
                }
            } catch (MessagingException me) {
                log.info("Invalid FROM address <" + errorFrom + ">. E-mail has been deleted.");
                return;
            }
            try {
                subject = StoreClass.getSubject(ex.getMimeMessage().getSubject());
                if (subject == null) {
                    subject = "[none]";
                }
            } catch (Exception me) {
                subject = "[none]";
            }
            log.error("Message from <" + from + "> : <" + subject + "> has been skipped.", e);
        } catch (UnsupportedEncodingException e) {
            log.error("process message failed", e);
        } finally {
            session.getDebugOut().flush();
        }
    }

    /**
     * Перенаправляет необработанную почту
     *
     * @param message Тело сообщения
     */
    public static void forwardUnprocessed(MimeMessage message, ResultImport msg) {
        log.trace("forwardUnprocessed caled");
        if (message == null) {
            log.error("message is null");
        } else {
            try {
                EmailUtil.forwardEmail(message, "forward.ftl_h", msg);
            } catch (GranException e) {
                log.debug("Exception: " + e.getMessage());
                String errorFrom = "[none]";
                String subject = "";
                String from = errorFrom;
                try {
                    errorFrom = message.getHeader("From", null);
                    Address[] aFrom = message.getFrom();
                    if (aFrom != null) {
                        from = ((InternetAddress) aFrom[0]).getAddress();
                    }
                } catch (MessagingException me) {
                    log.info("Invalid FROM address <" + errorFrom + ">. E-mail has been deleted.");
                    return;
                }
                try {
                    subject = StoreClass.getSubject(message.getSubject());
                    if (subject == null) {
                        subject = "[none]";
                    }

                } catch (Exception me) {
                    subject = "[none]";
                }
                log.error("Message from <" + from + "> : <" + subject + "> has been skipped.", e);
            } catch (Throwable e) {
                log.error("ERROR FORWARD EMAIL: ", e);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String subject = "=?UTF-8?Q?=D0=92=D0=A2=D0=91_=D0=A2=D0=95=D0=A1=D0=A2=D0=98=D0=A0=D0=9E=D0=92=D0=90= =D0=9D=D0=98=D0=95_=D0=98=D0=9A=D0=A0_0072_=D0=9C=D0=B0=D0=BB=D1=8B=D0=B9 = =D0=B1=D0=B8=D0=B7=D0=BD=D0=B5=D1=81_RF-000039306";
        System.out.println(getSubject(subject));
    }
}