package com.trackstudio.app.adapter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.UnaryOperator;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.manager.IndexManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.sman.MailReceiver;
import com.trackstudio.sman.properties.ConfigFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.adapter.store.StoreClass;
import com.trackstudio.exception.MailImportException;
import com.trackstudio.external.IGeneralScheduler;
import com.trackstudio.jmx.MailImportMXBeanImpl;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.Null;

import net.jcip.annotations.Immutable;

import static com.trackstudio.tools.textfilter.MacrosUtil.getIntegerOrDefault;

/**
 * Класс, в котором реализованы методы работы с импортом почты
 */
@Immutable
public class BaseStoreServiceAdapter implements IGeneralScheduler {

	private static final LockManager lockManager = LockManager.getInstance();

	private static final Log log = LogFactory.getLog(BaseStoreServiceAdapter.class);

	private final List<Session> sessions = getSessions();

	private final IndexManager search = IndexManager.getInstance();

	private ExecutorService executorService = Executors.newFixedThreadPool(sessions.size());

	public BaseStoreServiceAdapter() {
		MailImportMXBeanImpl.getInstance().registrationService(this);
	}

	@Override
	public String getCronTime() {
		return Config.getProperty("trackstudio.mailimport.interval");
	}

	@Override
	public boolean isUse() {
		return Config.getInstance().isFormMailNotification();
	}

	/**
	 * Возвращает текстовое описание адаптера
	 *
	 * @return описание адаптера
	 */
	public String getName() {
		return "Base Store Service Adapter";
	}

	/**
	 * Импортируем почту
	 */
	public String execute() throws Exception {
		for (Session session: sessions) {
			executorService.execute(() -> {
				String protocol = session.getProperty("mail.store.protocol");
				String host = session.getProperty("mail.store.host");
				String login = session.getProperty("mail.store.user");
				String password = session.getProperty("mail.store.password");
				int port = Null.getIntegerOrDefaultValue(
						session.getProperty("mail.store.port"), -1
				);
				try (Store store = session.getStore(protocol))	{
					store.connect(host, port, login, password);
					Folder folder = store.getFolder("INBOX");
					folder.open(Folder.READ_WRITE);
					Message[] messages = fetch(folder);
					for (Message message : messages) {
						MimeMessage msg = (MimeMessage) message;
						if (isImported(msg)) {
							message.setFlag(Flags.Flag.DELETED, true);
						} else if (processSingleMessage(msg)) {
							message.setFlag(Flags.Flag.DELETED, true);
						}
					}
					folder.close(true);
					session.getDebugOut().flush();
				} catch (Exception e) {
					log.error("Error occurred in import email : " + e);
				}
			});
		}
		return null;
	}

	/**
	 * Check, if this message is already imported.
	 * This situation can be happened, when TS imported the mail,
	 * but mail server closes the connection and TS could not delete the mail from mail server.
	 * @param msg Message
	 * @return true is message is already imported.
	 * @throws MessagingException possible exception with mail.
	 * @throws GranException possible exception with TS
	 */
	public boolean isImported(MimeMessage msg) throws MessagingException, GranException {
		boolean result = false;
		if (msg.getMessageID()==null) {
			return false;
		}
		Map<String, String> tasks = search.searchTasks(msg.getMessageID());

		if (tasks == null) {
			return false;
		}

		for (String taskId : tasks.keySet()) {
			if (TaskRelatedManager.getInstance().find(taskId).getName().equals(msg.getSubject())) {
				result = true;
				break;
			}
		}
		return result;
	}

	public boolean processSingleMessage(MimeMessage m) {
		boolean result = false;
		try {
			MailImportMXBeanImpl.getInstance().treatementMsg(m);
			AdapterManager.getInstance().getStoreAdapterManager().process(m);
			result = true;
		} catch (MailImportException e) {
			StoreClass.forwardUnprocessed(e);
		}
		return result;
	}

	private Message[] fetch(Folder folder) throws MessagingException {
		Message[] msgs;
		int limit = getIntegerOrDefault(Config.getProperty("trackstudio.mail.import.limit"), 0);
		if (limit > 0 && limit < folder.getMessageCount()) {
			msgs = folder.getMessages(1, limit);
		} else {
			msgs = folder.getMessages();
		}
		return msgs;
	}

	@Override
	public String getClassName() {
		return this.getClass().getName();
	}

	private List<Session> getSessions() {
		List<Session> sessions = new ArrayList<>();
		for (MailReceiver receiver : Config.getInstance().getMailReceivers()) {
			sessions.add(Config.getInstance().getSession(receiver));
		}
		return sessions;
	}

	@Override
	public void shutdown() {
		log.warn(executorService.shutdownNow().size() + " tasks have been rejected");
	}

}