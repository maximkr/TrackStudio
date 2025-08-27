package com.trackstudio.tools;

import java.io.PrintStream;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Category;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.net.SMTPAppender;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;

import com.trackstudio.sman.tools.MailOutputStream;
import com.trackstudio.startup.Config;

import net.jcip.annotations.Immutable;

/**
 * Класс используется для отправки лога работы системы на почту
 */
@Immutable
public class TSSMTPAppender extends SMTPAppender {

    /**
     * Выставляет настройки класса
     */
    @Override
    public void activateOptions() {
        String hostSMTP =  Config.getProperty("log4j.host") != null ? Config.getProperty("log4j.host") : "";
        String port = Config.getProperty("log4j.port") != null ? Config.getProperty("log4j.port") : "";

        Properties props = new Properties();
        for (String e : Config.properties.stringPropertyNames()) {
            props.setProperty(e, Config.getProperty(e));
        }
        if (getTo() != null) {
            try {
                super.setSMTPHost(hostSMTP);
                props.put("mail.smtp.host", hostSMTP);
                props.put("mail.smtp.port", port);
            } catch (Exception e) {
                LogLog.error("Could not connect to "+hostSMTP+":"+port);
            }
        }
        Session session = Session.getInstance(props, null);
        PrintStream printStream = new PrintStream(new MailOutputStream(true));
        session.setDebugOut(printStream);
        msg = new MimeMessage(session);
        try {
            if (getFrom() != null && !getFrom().isEmpty()) {
                msg.setFrom(getAddress(getFrom()));
            }
            if (getTo() != null) {
                msg.setRecipients(Message.RecipientType.TO, parseAddress(getTo()));
            }
            String subject = "TS Error " + com.trackstudio.app.adapter.AdapterManager.getInstance().getSecuredTSInfoAdapterManager().getTSVersion(null) + " ";
            if (super.getSubject() != null)
                msg.setSubject(subject+super.getSubject());
            else
                msg.setSubject(subject);
        } catch (Exception e) {
            LogLog.error("Could not activate SMTPAppender options.", e);
        }
    }

    /**
     * Возвращает адрес отправителя
     *
     * @return адрес отправителя
     */
    @Override
    public String getFrom() {
        try {
            return Config.getProperty("mail.from");
        } catch (Exception ex) {
            return super.getFrom();
        }
    }

    /**
     * Возвращает адрес получателя
     *
     * @return адрес получателя
     */
    @Override
    public String getTo() {
        try {
            return Config.getProperty("trackstudio.reportBugsTo");
        } catch (Exception ex) {
            return super.getTo();
        }
    }

    /**
     * Преобразует адрес почты из строки в объект InternetAddress
     *
     * @param addressStr адрес в виде строки
     * @return адрес в виде InternetAddress
     */
    InternetAddress getAddress(String addressStr) {
        try {
            return new InternetAddress(addressStr);
        } catch (AddressException e) {
            errorHandler.error("Could not parse address [" + addressStr + "].", e, ErrorCode.ADDRESS_PARSE_FAILURE);
            return null;
        }
    }

    /**
     * Преобразует адреса почты из строки в массив объектов InternetAddress
     *
     * @param addressStr адреса в виде строки
     * @return массив объектов InternetAddress
     */
    InternetAddress[] parseAddress(String addressStr) {
        try {
            return InternetAddress.parse(addressStr, true);
        } catch (AddressException e) {
            errorHandler.error("Could not parse address [" + addressStr + "].",
                    e, ErrorCode.ADDRESS_PARSE_FAILURE);
            return null;
        }
    }

    /**
     * Добавляет текст в отправляемому контенту
     *
     * @param loggingEvent к чему добавляем текст
     */
    @Override
    public void append(org.apache.log4j.spi.LoggingEvent loggingEvent) {

        Runtime jre = Runtime.getRuntime();
        String info="System Information:\n"+
                "Java Information:\n"+
                "Java Version: " + System.getProperty("java.version") + "\n"+
                "Java Vendor: " + System.getProperty("java.vendor") + "\n"+
                "Java Specification Vendor: " + System.getProperty("java.specification.vendor") + "\n"+
                "Java Specification Version: " + System.getProperty("java.specification.version") + "\n"+
                "Java Home: " + System.getProperty("java.home") + "\n"+
                "Java&nbsp;Classpath: " + System.getProperty("java.class.path") + "\n"+
                "Virtual Machine Information:\n"+
                "VM Vendor: " + System.getProperty("java.vm.vendor") + "\n"+
                "VM: " + System.getProperty("java.vm.name") + "\n"+
                "VM Version: " + System.getProperty("java.vm.version") + "\n"+
                "Runtime Version: " + System.getProperty("java.runtime.version") + "\n"+
                "VM Specification Version: " + System.getProperty("java.vm.specification.version") + "\n"+
                "VM Info: " + System.getProperty("java.vm.info") + "\n"+
                "System information:\n"+
                "OS Name: " + System.getProperty("os.arch") + "-" + System.getProperty("os.name") + "(" + System.getProperty("os.version") + ")\n"+
                "Total Memory: " + jre.totalMemory() + "\n"+
                "Free Memory: " + jre.freeMemory() + "\n"+
                "Product Information:\n"+
                "Version: " + com.trackstudio.app.adapter.AdapterManager.getInstance().getSecuredTSInfoAdapterManager().getTSVersion(null) + "\n"+
                "Licensee: " + Config.getProperty("trackstudio.license.licensee") + "\n"+
                "License Type: " + Config.getProperty("trackstudio.license.type") + "\n"+
                "Database Information:\n"+
                "Hibernate Dialect: " + System.getProperty("hibernate.dialect") + "\n"+
                "Hibernate Driver: " + System.getProperty("hibernate.connection.driver_class") + "\n";
        Category cat = Category.getInstance(loggingEvent.categoryName);
        LoggingEvent event = new LoggingEvent(loggingEvent.getLoggerName(), cat, loggingEvent.getLevel(), loggingEvent.getMessage() + info,  loggingEvent.getThrowableInformation() != null ? loggingEvent.getThrowableInformation().getThrowable() : null);
        if (Null.isNotNull(getSMTPHost())) {
            super.append(event);
        }
    }
}