package com.trackstudio.app.adapter.store;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.trackstudio.kernel.manager.IndexManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.StoreAdapter;
import com.trackstudio.exception.MailImportException;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.Null;

/**
 * Класс для управления правилами импорта почты
 */
public class StoreAdapterManager {
    private static final Log log = LogFactory.getLog(StoreAdapterManager.class);
    private final CopyOnWriteArrayList<StoreAdapter> am;

    /**
     * Конструктор по умолчанию
     *
     * @param adapters список адаптеров
     */
    public StoreAdapterManager(ArrayList<StoreAdapter> adapters) {
        am = new CopyOnWriteArrayList<StoreAdapter>(Null.removeNullElementsFromList(adapters));
    }

    /**
     * Процесс импорта сообщения
     *
     * @param message сообщение
     * @throws com.trackstudio.exception.MailImportException for necessery
     */
    public void process(MimeMessage message) throws MailImportException {
        ResultImport resultImport = new ResultImport();
        resultImport.appendTxtLn("startup process import email : " + StoreAdapterManager.class.getName());
        log.debug("startup process import email : " + StoreAdapterManager.class.getName());
        resultImport.appendTxtLn("read list of skipping emails");
        String txt = Config.getProperty("trackstudio.skip.emails.from");
        txt = txt != null ? txt : "";
        String[] emailsSkip = txt.split(",");
        resultImport.appendTxtLn("value : " + txt);
        for (StoreAdapter adapter: am) {
            try {
                String from = ((InternetAddress) (message.getFrom())[0]).getAddress();
                boolean skip = false;
                for (String email : emailsSkip) {
                    if (!email.isEmpty() && from.toLowerCase().contains(email)) {
                        skip = true;
                    }
                }
                resultImport.appendTxtLn("result of checking skipping emails : " + skip + " your email : " + from);
                if (!skip) {
                    adapter.process(message, resultImport);
                    if (resultImport.getMsg() != ResultImport.Message.OK) {
                        StoreClass.forwardUnprocessed(message, resultImport);
                    }
                }
            } catch (MailImportException e) {
                StoreClass.forwardUnprocessed(e);
            } catch (Throwable e) {
                throw new MailImportException(e, adapter.toString(), message);
            }
        }
        log.debug("SHUTDOWN PROCESS IMPORT EMAIL:" + StoreAdapterManager.class.getName());
    }
}