package com.trackstudio.exception;

import javax.mail.internet.MimeMessage;

public class MailImportException extends GranException {

    protected MimeMessage mimeMessage;

    public MailImportException(Throwable e, String s, MimeMessage message) {
        super(e, s);
        this.mimeMessage = message;
    }

    public MimeMessage getMimeMessage() {
        return mimeMessage;
    }

    public void setMimeMessage(MimeMessage mimemessage) {
        this.mimeMessage = mimemessage;
    }
}
