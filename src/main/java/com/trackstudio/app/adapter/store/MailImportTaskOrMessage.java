package com.trackstudio.app.adapter.store;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.trackstudio.app.TriggerExecute;
import com.trackstudio.kernel.cache.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.TriggerManager;
import com.trackstudio.app.UDFFormFillHelper;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.adapter.StoreAdapter;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.app.session.SessionManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.MailImportException;
import com.trackstudio.exception.UserException;
import com.trackstudio.jmx.MailImportMXBeanImpl;
import com.trackstudio.kernel.manager.AttachmentArray;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.MailImport;
import com.trackstudio.model.Mstatus;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.ParameterValidator;

import net.jcip.annotations.Immutable;

import static com.trackstudio.tools.textfilter.MacrosUtil.convertContentImg;

/**
 * Класс для импорта электронной почты и создания на ее основе задач, сообщений и приложенных файлов
 */
@Immutable
public class MailImportTaskOrMessage implements StoreAdapter {
    private static final Log log = LogFactory.getLog(MailImportTaskOrMessage.class);
    private static final String BOTTOM_BORDER = "*!=====!*";
    private static final String TOP_BORDER = "*#=====#*";
    private final String preTag = Null.isNotNull(Config.getProperty("wrap.msg.pre")) ? Config.getProperty("wrap.msg.pre") : "<pre width='90'>";
    private final String postTag = Null.isNotNull(Config.getProperty("wrap.msg.post")) ? Config.getProperty("wrap.msg.post") : "</pre>";

    private final TriggerExecute triggers = new TriggerExecute();

    /**
     * Конструктор по умолчанию
     */
    public MailImportTaskOrMessage() {
    }

    /**
     * Инициализирует класс
     *
     * @return TRUE - если инициализация прошла успешно, FALSE - если нет
     */
    public boolean init() {
        return true;
    }

    /**
     * ВОзвращает описание адаптера
     *
     * @return описание адаптера
     */
    public String getDescription() {
        return "Mail Import Task Or Message";
    }

    /**
     * Проверяет корректность домена сообщения email
     *
     * @param mi   правило импорта
     * @param from отправитель
     * @return TRUE - если соответствует, FALSE - если нет
     */
    private boolean checkingDomains(MailImport mi, String from) {
        // log.debug("=== Checking rule " + mi.getName() + " domains " + mi.getDomain());
        String domain = mi.getDomain();
        if (domain != null && domain.trim().length() != 0) {
            Set<String> domains = new HashSet<String>();
            StringTokenizer st = new StringTokenizer(domain, ";, ");
            while (st.hasMoreTokens()) {
                String dm = st.nextToken().trim();
                domains.add(dm);
            }
            int atIndex = from.indexOf('@');
            if (atIndex != -1) {
                String userDom = from.substring(atIndex + 1);
                if (!domains.contains(userDom)) {
                    log.debug("Skipping (domain match: " + userDom + ") " + mi.getName());
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Проверяет соответствие письма ключевым словам
     *
     * @param mi      правило импорта
     * @param message сообщение
     * @param body    тело письма
     * @param subject тема письма
     * @return TRUE - соответствует, FALSE - нет
     */
    private boolean matchKeywords(MailImport mi, MimeMessage message, String body, String subject) throws MessagingException {
        // log.debug("=== Checking rule " + mi.getName() + " keywords " + mi.getKeywords());
        boolean keywordMatch = mi.getKeywords() == null || mi.getKeywords().length() == 0;
        if (!keywordMatch && mi.getSearchIn() != null) {
            String match = "";
            if (mi.getSearchIn() == 3) {
                if (subject != null)
                    match = subject.toLowerCase(Locale.ENGLISH);
                if (body != null)
                    match += " " + body.toLowerCase(Locale.ENGLISH);
            }
            if (mi.getSearchIn() == 1 && subject != null)
                match = subject.toLowerCase(Locale.ENGLISH);
            if (mi.getSearchIn() == 0 && body != null)
                match = body.toLowerCase(Locale.ENGLISH);
            if (mi.getSearchIn() == 2) {
                try {
                    Enumeration en = message.getAllHeaderLines();
                    while (en.hasMoreElements()) {
                        String header = (String) en.nextElement();
                        if (header != null)
                            match += ' ' + header.toLowerCase(Locale.ENGLISH);
                    }
                } catch (MessagingException e) {
                    log.error("Exception", e);
                }
            }
            if (mi.getSearchIn() == 4) {
                String[] tos = message.getHeader("To");
                for (String to : tos) {
                    match += to + " ";
                }
            }
            // log.debug("Pattern = " + mi.getKeywords() + " match = " + match);
            keywordMatch = checkRegExp(mi.getKeywords(), match);
        }
        if (!keywordMatch) {
            // log.debug("Skipping (keyword match: " + keywordMatch + ") " + mi.getName());
            return false;
        }
        return true;
    }

    public static boolean checkRegExp(String keyword, String match) {
        boolean result = false;
        try {
            Pattern p = Pattern.compile(keyword.toLowerCase(Locale.ENGLISH), Pattern.MULTILINE + Pattern.DOTALL);
            Matcher m = p.matcher(match);
            result = m.matches();
            if (!result) {
                result = match.toLowerCase(Locale.ENGLISH).contains(keyword.toLowerCase(Locale.ENGLISH));
            }
        } catch (PatternSyntaxException e) {
            log.debug("Pattern syntax exception" + e.getMessage());
            result = false;
        }
        return result;
    }

    /**
     * Проверяет соответствие типа сообщения
     *
     * @param sc      сессия пользователя
     * @param mstatus тип сообщения
     * @param taskId  ID задачи
     * @return TRUE - соответствует, FALSE - нет
     * @throws GranException при необходимости
     */
    private boolean checkMstatus(SessionContext sc, Mstatus mstatus, String taskId) throws GranException {
        if (mstatus == null)
            return false;
        SecuredTaskBean task = new SecuredTaskBean(taskId, sc);
        ArrayList<SecuredMstatusBean> list = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getMstatusList(sc, task.getWorkflowId());
        for (SecuredMstatusBean m : list) {
            if (m.getId().equals(mstatus.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Процесс импорта сообщения
     *
     * @param message сообщение
     * @return TRUE - соответствует, FALSE - нет
     * @throws MailImportException при необходимости
     */
    public void process(MimeMessage message, final ResultImport resultImport) throws MailImportException {
        log.trace("process e-mail message called");
        resultImport.appendTxtLn("process e-mail message called : " + MailImportTaskOrMessage.class.getName());
        try {
            if (message == null) {
                log.debug("currentMessage=null");
                resultImport.appendTxtLn("currentMessage=null");
                resultImport.setMsg(ResultImport.Message.CURRENT_MESSAGE_NULL);
                return;
            } else if (message.getFrom() == null || message.getFrom().length == 0) {
                log.debug("currentMessage.getFrom()=null");
                resultImport.appendTxtLn("currentMessage.getFrom()=null");
                resultImport.setMsg(ResultImport.Message.CURRENT_MESSAGE_FROM_NULL);
                return;
            } else if (message.getFrom()[0] == null) {
                log.debug("currentMessage.getFrom()[0]=null");
                resultImport.appendTxtLn("currentMessage.getFrom()[0]=null");
                resultImport.setMsg(ResultImport.Message.CURRENT_MESSAGE_FROM_USER_NULL);
                return;
            }
            String from = ((InternetAddress) (message.getFrom())[0]).getAddress();
            String name = ((InternetAddress) (message.getFrom())[0]).getPersonal();
            String subject = StoreClass.getSubject(message.getSubject());
            String body = getText(message);
            log.debug("From = " + from);
            log.debug("Name = " + name);
            log.debug("Subject = " + subject);
            log.debug("Body = " + body);

            resultImport.appendTxtLn("Message subject : " + subject);
            resultImport.appendTxtLn("Message body : " + body);
            final String bodyLow = body.toLowerCase();
            if (bodyLow.contains("<body") && bodyLow.contains("</body>")) {
                final int start = bodyLow.indexOf("<body");
                final int finish = bodyLow.indexOf("</body>");
                final int size = bodyLow.length();
                if (size > start && size < finish) {
                    body = body.substring(start, finish);
                    body = body.substring(body.indexOf(">") + 1);
                }
            }
            // body = message.getMessageID() + " " + body; TODO likely buggy

            List<MailImport> mailImportsAll = KernelManager.getMailImport().getAllMailImports();

            if (mailImportsAll == null || mailImportsAll.isEmpty()) {
                log.debug("not exist mail import for anything task");
                resultImport.appendTxtLn("not exist mail import for anything task");
                resultImport.setMsg(ResultImport.Message.DOES_EXIST_MAIN_IMPORT);
                return;
            }
            ArrayList<MailImport> mailImportsWithKeywords = new ArrayList<MailImport>();
            ArrayList<MailImport> mailImportsWithoutKeywords = new ArrayList<MailImport>();
            for (MailImport mi : mailImportsAll) {
                if (mi.getKeywords() == null || mi.getKeywords().length() == 0) {
                    mailImportsWithoutKeywords.add(mi);
                } else {
                    mailImportsWithKeywords.add(mi);
                }
            }
            Collections.sort(mailImportsWithKeywords);
            Collections.sort(mailImportsWithoutKeywords);

            final String taskId = StoreClass.parseTaskNumber(subject);

            processMailImport(resultImport, mailImportsWithKeywords, from, message, body, subject, taskId, name);
            if (!(ResultImport.Message.OK == resultImport.getMsg())) {
                processMailImport(resultImport, mailImportsWithoutKeywords, from, message, body, subject, taskId, name);
            }
        } catch (Exception e) {
            throw new MailImportException(e, e.toString(), message);
        } finally {
            log.debug("SHUTDOWN PROCESS EMAIL IMPORT CALLED:");
        }
    }

    private static String getText(Part p) throws MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            Object value = p.getContent();
            if (value instanceof InputStream) {
                return convertStreamToString((InputStream) value);
            } else {
                return value.toString();
            }
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart) p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getText(bp);
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getText( bp);
                    if (s != null)
                        return s;
                } else {
                    return getText( bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText( mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }
        return null;
    }

    private boolean validateCondition(final ResultImport resultImport, final MailImport mi,
                                      final String from, final MimeMessage message, final String body, final String subject) throws MessagingException {
        // log.debug("Try to do MailImport : " + mi);
        resultImport.appendTxtLn("Try to do MailImport : " + mi);
        if (mi == null) {
            // log.debug("Try to do MailImport :  error : empty rule");
            resultImport.appendTxtLn("Try to do MailImport :  error : empty rule");
            return false;
        }
        if (mi.getActive() == 0) {
            // log.debug("Try to do MailImport : " + mi + " error : the mail import rule is not active");
            resultImport.appendTxtLn("Try to do MailImport : " + mi + " error : the mail import rule is not active");
            return false;
        }
        if (!checkingDomains(mi, from)) {
            // log.debug("Try to do MailImport : " + mi + " error : the mail import rule is not checkingDomains");
            resultImport.appendTxtLn("Try to do MailImport : " + mi + " error : the mail import rule is not checkingDomains");
            return false;
        }

        if (!matchKeywords(mi, message, body, subject)) {
            // log.debug("Try to do MailImport : " + mi + " error : the mail import rule is not matchKeywords");
            resultImport.appendTxtLn("Try to do MailImport : " + mi + " error : the mail import rule is not matchKeywords");
            return false;
        }
        return true;
    }

    private void processMailImport(final ResultImport resultImport, ArrayList<MailImport> mailImports, String from, MimeMessage message, String body, String subject, final String taskId, String name) {
        log.debug("STARTUP BEGIN MAIL IMPORT:");
        try {
            for (MailImport mi : mailImports) {
                final boolean forcedCreateTask = mi.getMstatus() == null;
                if (!this.validateCondition(resultImport, mi, from, message, body, subject)) {
                    continue;
                }
                boolean hasBody = false;
                if (body != null) {
                    if (body.contains("<body") && body.contains("</body>")) {
                        int begin = body.indexOf("<body") + body.substring(body.indexOf("<body")).indexOf(">") + ">".length();
                        body = body.substring(begin, body.indexOf("</body>"));
                        hasBody = true;
                    }
                }
                if (subject == null || subject.length() == 0) {
                    subject = mi.getName();
                }

                log.debug("findUserIdByEmailNameProject: from=" + from + "; name=" + name + "; taskId=" + mi.getTask().getId());
                resultImport.appendTxtLn("findUserIdByEmailNameProject: from=" + from + "; name=" + name + "; taskId=" + mi.getTask().getId());
                String userId = KernelManager.getUser().findUserByEmailIgnoreCaseForImportEmail(from, mi.getTask().getId());
                if (userId == null) {
                    if (mi.getImportUnknown() == 1) {
                        InternetAddress internetAddress = (InternetAddress) (message.getFrom())[0];
                        body = "e-from: " + internetAddress.getPersonal() + " &lt;" + internetAddress.getAddress() + "&gt;" + "\n\n" + body;
                        if (mi.getOwner() != null) {
                            userId = mi.getOwner().getId();
                        } else {
                            userId = "1";
                        }
                    } else {
                        log.debug("Skipping (user id is null) " + mi.getName());
                        resultImport.appendTxtLn("Skipping (user id is null) " + mi.getName());
                        continue;
                    }
                }
//                log.debug("body = " + body);
                resultImport.appendTxtLn("userid = " + userId);

                String sessionId = SessionManager.getInstance().create(UserRelatedManager.getInstance().find(userId));
                SessionContext sc = SessionManager.getInstance().getSessionContext(sessionId);
                if (sc == null) {
                    log.debug("user account is not active userId : " + userId);
                    resultImport.appendTxtLn("user account is not active userId : " + userId);
                    throw new UserException("User account with id " + userId + " is not active");
                }
                if (!forcedCreateTask && taskId != null && !checkMstatus(sc, mi.getMstatus(), taskId)) {
                    log.debug("Try to do MailImport : " + mi + " error : the mail import rule is not checkMstatus");
                    resultImport.appendTxtLn("Try to do MailImport : " + mi + " error : the mail import rule is not checkMstatus");
                    continue;
                }

                String parentTaskId = mi.getTask().getId();
                String categoryId = mi.getCategory().getId();

                //log.debug("Processing UDF");
                //resultImport.appendTxtLn("Processing UDF");

                SecuredTaskBean stb = new SecuredTaskBean(parentTaskId, sc);
                if (hasBody) {
                    body = "<span width=\"90\">" + body + "</span>";
                } else if (usePre(body)) {
                    body = this.preTag + body + this.postTag;
                }
                List<AttachmentArray> atts = null;
                Map<PluginType, List<AbstractPluginCacheItem>> scripts = PluginCacheManager.getInstance().list(
                        PluginType.BEFORE_MAIL_IMPORT, PluginType.INSTEAD_OF_MAIL_IMPORT, PluginType.AFTER_MAIL_IMPORT
                );
                List<AbstractPluginCacheItem> before = scripts.getOrDefault(PluginType.BEFORE_MAIL_IMPORT, Collections.EMPTY_LIST);
                for (AbstractPluginCacheItem script : before) {
                    triggers.mail(script, message, mi);
                }
                List<AbstractPluginCacheItem> insteadOf = scripts.getOrDefault(PluginType.INSTEAD_OF_MAIL_IMPORT, Collections.EMPTY_LIST);
                if (insteadOf.isEmpty()) {
                    if (taskId != null && !forcedCreateTask) {
                        log.debug("Subject contains a valid task number. Task won't be created. " + subject);
                        resultImport.appendTxtLn("Subject contains a valid task number. Task won't be created. " + subject);
                        if (sc.canAction(Action.createTaskMessageAttachments, taskId)) {
                            atts = StoreClass.getAttaches(message);
                            body = convertContentImg(body, atts);
                        }
                        log.debug("Try to do MailImport : " + mi + " action : create message taskId : " + taskId);
                        resultImport.appendTxtLn("Try to do MailImport : " + mi + " action : create message taskId : " + taskId);
                        String messageId = processMessage(sc, addCcInBody(message, body), mi.getMstatus().getId(), taskId, atts);
                        MailImportMXBeanImpl.getInstance().createMsg(message, mi, taskId, messageId);
                        log.debug("Try to do MailImport : " + mi + " action : create message done messageId : " + messageId);
                        resultImport.appendTxtLn("Try to do MailImport : " + mi + " action : create message done messageId : " + messageId);
                    } else {
                        if (sc.canAction(Action.createTaskAttachments, parentTaskId)) {
                            atts = StoreClass.getAttaches(message);
                            body = convertContentImg(body, atts);
                        }
                        log.debug("Try to do MailImport : " + mi + " action : create task parentId : " + parentTaskId);
                        resultImport.appendTxtLn("Try to do MailImport : " + mi + " action : create task parentId : " + parentTaskId);
                        subject = ParameterValidator.badTaskName(subject) ? ParameterValidator.cutToValidName(subject, mi.getName()) : subject;
                        final String createTaskId = processTask(sc, categoryId, subject, addCcInBody(message, body), stb, atts);
                        MailImportMXBeanImpl.getInstance().createTask(message, mi, createTaskId);
                        log.debug("Try to do MailImport : " + mi + " action : create task done taskId : " + createTaskId);
                        resultImport.appendTxtLn("Try to do MailImport : " + mi + " action : create task done taskId : " + createTaskId);
                    }
                } else {
                    for (AbstractPluginCacheItem script : insteadOf) {
                        triggers.mail(script, message, mi);
                    }
                }
                List<AbstractPluginCacheItem> after = scripts.getOrDefault(PluginType.AFTER_MAIL_IMPORT, Collections.EMPTY_LIST);
                for (AbstractPluginCacheItem script : after) {
                    triggers.mail(script, message, mi);
                }
                log.debug("uploading attachments");
                resultImport.appendTxtLn("uploading attachments");
                resultImport.setMsg(ResultImport.Message.OK);
                break;
            }
        } catch (UnsupportedEncodingException ee) {
            log.error("UnsupportedEncodingException", ee);
            resultImport.appendTxtLn("UnsupportedEncodingException : " + GranException.printStackTrace(ee));
        } catch (MessagingException me) {
            log.error("MessagingException", me);
            resultImport.appendTxtLn("MessagingException : " + GranException.printStackTrace(me));
        } catch (GranException ge) {
            resultImport.appendTxtLn("GranException : " + GranException.printStackTrace(ge));
            log.error("GranException", ge);
        }
        log.debug("CLOSE BEGIN MAIL IMPORT:");
    }

    private boolean usePre(String body) {
        Pattern pattern = Pattern.compile("(<meta)(.+)(content=\"Microsoft Word)");
        return !pattern.matcher(body).find();
    }

    private String addCcInBody(MimeMessage message, String body) throws MessagingException {
        List<InternetAddress> ccHeader = new ArrayList<InternetAddress>();
        try {
            String[] header = message.getHeader("Cc");
            if (header != null && header.length != 0 && header[0] != null) {
                for (String head : header) {
                    ccHeader.addAll(Arrays.asList(InternetAddress.parse(head)));
                }
                String users = "";
                for (InternetAddress internetAddress : ccHeader) {
                    String address = "email: " + internetAddress.getAddress();
                    String personal = internetAddress.getPersonal() != null ? ", name: " + internetAddress.getPersonal() : "";
                    users += address + personal + ";<br>";
                }
                body = "CC users:<br>" + users + "<br>" + body;
            }
        } catch (AddressException e) {
            log.debug("Invalid CC address: " + e.getMessage());
        }
        return body;
    }

    private HashMap<String, String> getUdfMap(SessionContext sc, SecuredTaskBean stb, String categoryId) throws GranException {
        HashMap<String, String> udfMap = new HashMap<String, String>();
        ArrayList<SecuredUDFBean> udfColl = stb.getUDFs(AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, categoryId).getWorkflowId());
        for (SecuredUDFBean uitem : udfColl) {
            if (!KernelManager.getUdf().isNewTaskUdfEditable(stb.getId(), sc.getUserId(), uitem.getUdfId(), stb.getStatusId(), sc.getUserId(), null))
                continue;
            udfMap.put(uitem.getCaption(), uitem.getDefaultUDF());
        }
        return udfMap;
    }

    private String processTask(SessionContext sc, String categoryId, String name, String body, SecuredTaskBean stb, List<AttachmentArray> atts) throws GranException {
        return TriggerManager.getInstance().createTask(
                sc, categoryId, "", name, body, null, stb.getDeadline(),
                null, stb.getId(), stb.getHandlerUserId(), stb.getHandlerGroupId(),
                true, Collections.EMPTY_MAP, null, atts, false);
    }

    private String processMessage(SessionContext sc, String body, String mStatusId, String taskId, List<AttachmentArray> atts) throws GranException {
        log.trace("processMessage");
        //������� ������ ������� � �������-�������
        int j;
        if ((j = body.indexOf(BOTTOM_BORDER)) != -1) {
            body = body.substring(0, j);
            if ((j = body.lastIndexOf('\n')) != -1)
                body = body.substring(0, j);
        }
        if ((j = body.indexOf(TOP_BORDER)) != -1) {
            body = body.substring(j);
            if ((j = body.indexOf('\n')) != -1)
                body = body.substring(j);
            if ((j = body.lastIndexOf(TOP_BORDER)) != -1)
                body = body.substring(j + TOP_BORDER.length());
        }
        SecuredTaskBean task = new SecuredTaskBean(taskId, sc);
        String handlerUserId = task.getHandlerUserId();
        log.debug("HandlerUserId = " + handlerUserId);

        String handlerGroupId = task.getHandlerGroupId();
        log.debug("HandlerGroupId = " + handlerGroupId);
        return TriggerManager.getInstance().createMessage(sc, taskId, mStatusId, body, null, handlerUserId, handlerGroupId, null, task.getPriorityId(), task.getDeadline(), task.getBudget(), UDFFormFillHelper.simplifyUdf(task), true, atts);
    }

    private static String convertStreamToString(InputStream is) {
        InputStreamReader isr;
        try {
            isr = new InputStreamReader(is, "iso-2022-jp");
        } catch (UnsupportedEncodingException uex) {
            isr = new InputStreamReader(is);
        }
        BufferedReader reader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            log.error("Exception ", e);
        }

        return sb.toString();
    }
}
