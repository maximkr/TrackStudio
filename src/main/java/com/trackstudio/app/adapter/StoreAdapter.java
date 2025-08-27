package com.trackstudio.app.adapter;

import javax.mail.internet.MimeMessage;

import com.trackstudio.app.adapter.store.ResultImport;
import com.trackstudio.exception.GranException;

/**
 * Интерфейм используется для реализации классов, выполняющих операции работы с почтовыми сообщениями e-mail
 */
public interface StoreAdapter extends Adapter {
    /**
     * This method treatments email. like create a new task or a new message
     *
     * @param message message
     * @param resultImport object which has description process of import
     * @throws GranException for necessary
     */
    void process(MimeMessage message, final ResultImport resultImport) throws GranException;
}