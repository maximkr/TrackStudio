package com.trackstudio.external;

import com.trackstudio.exception.GranException;
import com.trackstudio.model.MailImport;
import com.trackstudio.secured.SecuredTaskTriggerBean;

import javax.mail.internet.MimeMessage;


/**
 * This interface should be implemented for customers triggers for mail import rule.
 */
public interface MailImportTrigger {
    /**
     * This method is executed when mail import executes
     * @param mail E-mail.
     * @return rule {@link MailImport}
     * @throws GranException for necessary
     */
    MimeMessage execute(MimeMessage mail, MailImport rule) throws GranException;
}
