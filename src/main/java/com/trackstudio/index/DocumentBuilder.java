package com.trackstudio.index;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.*;

import com.trackstudio.app.UdfValue;
import com.trackstudio.app.udf.FloatValue;
import com.trackstudio.app.udf.GenericValue;
import com.trackstudio.app.udf.LinkValue;
import com.trackstudio.app.udf.ListMultiValue;
import com.trackstudio.app.udf.ListValue;
import com.trackstudio.app.udf.NumericValue;
import com.trackstudio.app.udf.StringValue;
import com.trackstudio.app.udf.TaskValue;
import com.trackstudio.app.udf.UserValue;
import com.trackstudio.containers.Link;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.MessageCacheItem;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.manager.IndexManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Task;
import com.trackstudio.model.User;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

@Immutable
public class DocumentBuilder implements IndexFields {
    private static final Log log = LogFactory.getLog(DocumentBuilder.class);

    public static Document buildAttachment(String id, String attName, String attDescription) throws GranException {
        Document doc = new Document();
        doc.add(new StringField(ATTACH_ID, id, Field.Store.YES));
        doc.add(new TextField(ATTACH_NAME, attName != null ? attName : "", Field.Store.YES));
        doc.add(new TextField(ATTACH_DESC, attDescription != null ? attDescription : "", Field.Store.YES));
        doc.add(new TextField(ATTACH_ALL_FIELDS, attName + " " + attDescription, Field.Store.YES));
        log.trace("buildAttachment : " + doc);
        return doc;
    }

    public static Document buildUser(UserRelatedInfo uci) throws GranException {
        Document doc = new Document();
        String userName = uci.getName();
        String userLogin = uci.getLogin();
        String userCompany = uci.getLogin();
        doc.add(new StringField(USER_ID, uci.getId(), Field.Store.YES));
        doc.add(new TextField(USER_NAME, userName != null ? userName : "", Field.Store.YES));
        doc.add(new TextField(USER_LOGIN, userLogin != null ? userLogin : "", Field.Store.YES));
        doc.add(new TextField(USER_COMPANY, userCompany != null ? userCompany : "", Field.Store.YES));
        List<UdfValue> ul = uci.getUDFValues();
        fillUdfs(ul, doc, userName, userLogin, userCompany, false);
        log.trace("buildUser : " + doc);
        return doc;
    }

    public static Document buildTask(TaskRelatedInfo tci) throws GranException {
        Document doc = new Document();
        String taskName = tci.getName();
        String taskTextDescription = tci.getTextDescription();
        doc.add(new StringField(TASK_ID, tci.getId(), Field.Store.YES));
        doc.add(new StringField(TASK_NUMBER, tci.getNumber(), Field.Store.NO));
        doc.add(new NumericDocValuesField(TASK_UPDATE, tci.getLastUpdateDateMsec()));
        doc.add(new TextField(TASK_NAME, taskName != null ? taskName : "", Field.Store.NO));
        doc.add(new TextField(TASK_DESC, taskTextDescription != null ? taskTextDescription : "", Field.Store.NO));
        StringBuilder text = new StringBuilder(10000);
        for (MessageCacheItem msg : tci.getMessages()) {
            if (msg.getTextDescription() != null)
                text.append(msg.getTextDescription()).append('\n');
        }
        doc.add(new TextField(TASK_MSGS, text.toString(), Field.Store.NO));
        List<UdfValue> ul = TaskRelatedManager.getInstance().getUDFValues(tci.getId());
        fillUdfs(ul, doc, taskName, taskTextDescription, text.toString(), true);
        log.trace("buildTask : " + doc);
        return doc;
    }

    private static void fillUdfs(List<UdfValue> ul, final Document doc, String taskName, String taskTextDescription, String text, boolean isTask) throws GranException {
        StringBuffer sbTasks = new StringBuffer("");
        StringBuffer sbUsers = new StringBuffer("");
        StringBuffer udfs = new StringBuffer("");
        for (UdfValue uval : ul) {
            if (uval.getUdfScript() != null)
                continue;

            GenericValue val = uval.getValueContainer();
            if (val.getValue(null) == null) continue;
            switch (uval.getType()) {
                case UdfValue.TASK: {
                    StringBuffer query = new StringBuffer("");
                    for (String str : ((TaskValue) val).getValue(null)) {
                        Task task = KernelManager.getFind().findTask(str);
                        query.append(task.getId()).append(' ');
                        query.append(task.getNumber()).append(' ');
                        query.append(IndexManager.checkLuceneCharacter(task.getName())).append(' ');
                    }
                    String result = query.toString();
                    if (!result.isEmpty()) {
                        sbTasks.append(' ').append(IndexManager.checkLuceneCharacter(uval.getUdfId())).append(' ');
                        sbTasks.append(result);
                        sbTasks.append('|');
                    }
                    break;
                }
                case UdfValue.USER: {
                    StringBuffer query = new StringBuffer("");
                    for (String str : ((UserValue) val).getValue(null)) {
                        User user = KernelManager.getFind().findUser(str);
                        query.append(IndexManager.checkLuceneCharacter(user.getId())).append(' ');
                        query.append(IndexManager.checkLuceneCharacter(user.getLogin())).append(' ');
                        query.append(IndexManager.checkLuceneCharacter(user.getName())).append(' ');
                    }
                    String result = query.toString();
                    if (!result.isEmpty()) {
                        sbUsers.append(' ').append(IndexManager.checkLuceneCharacter(uval.getUdfId())).append(' ');
                        sbUsers.append(result);
                        sbUsers.append('|');
                    }
                    break;
                }
                case UdfValue.STRING: {
                    String v = ((StringValue) val).getValue(null);
                    if (v != null) {
                        udfs.append(v).append(' ');
                        log.trace("adding udf value to the index: " + v);
                    }
                    break;
                }
                case UdfValue.LIST: {
                    Pair v = ((ListValue) val).getValue(null);
                    if (v != null) {
                        udfs.append(v.getValue()).append(' ');
                        log.trace("adding udf value to the index: " + v.getValue());
                    }
                    break;
                }
                case UdfValue.URL: {
                    Link v = ((LinkValue) val).getValue(null);
                    if (v != null) {
                        udfs.append(v.getDescription()).append(' ');
                        udfs.append(v.getLink()).append(' ');
                    }
                    break;
                }
                case UdfValue.MEMO: {
                    String v = HTMLEncoder.stripHtmlTags(((StringValue) val).getValue(null));
                    if (v != null && v.length() > 0) {
                        udfs.append(v).append(' ');
                    }
                    break;
                }
                case UdfValue.MULTILIST: {
                    List<Pair> v = ((ListMultiValue) val).getValue(null);
                    for (Pair p : v) {
                        udfs.append(p.getValue()).append(' ');
                    }
                    break;
                }
                case UdfValue.INTEGER: {
                    Integer v = ((NumericValue) val).getValue(null);
                    if (v != null) {
                        udfs.append(v).append(' ');
                    }
                    break;
                }
                case UdfValue.FLOAT: {
                    Double v = ((FloatValue) val).getValue(null);
                    if (v != null) {
                        udfs.append(v).append(' ');
                    }
                    break;
                }
            }
        }
        if (udfs.length() > 0)
            udfs.setLength(udfs.length() - 1);
        if (sbTasks.length() > 0)
            sbTasks.setLength(sbTasks.length() - 1);
        if (sbUsers.length() > 0)
            sbUsers.setLength(sbUsers.length() - 1);
        doc.add(new TextField(isTask ? REF_BY_TASK_FOR_TASK : REF_BY_TASK_FOR_USER, sbTasks.toString(), Field.Store.YES));
        doc.add(new TextField(isTask ? REF_BY_USER_FOR_TASK : REF_BY_USER_FOR_USER, sbUsers.toString(), Field.Store.YES));
        doc.add(new TextField(ALL_FIELDS, taskName + " " + taskTextDescription + " " + text + " " + udfs.toString(), Field.Store.YES));
    }
}
