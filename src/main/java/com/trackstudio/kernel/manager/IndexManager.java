/*
 * @(#)IndexManager.java
 * Copyright 2009 TrackStudio, Inc. All rights reserved.
 */
package com.trackstudio.kernel.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.trackstudio.tools.BiFunEx;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LogByteSizeMergePolicy;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NIOFSDirectory;

import com.trackstudio.exception.GranException;
import com.trackstudio.index.IndexFields;
import com.trackstudio.index.SearcherIndex;
import com.trackstudio.kernel.cache.AttachmentCacheItem;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.model.Attachment;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.textfilter.MacrosUtil;

import net.jcip.annotations.Immutable;

import static com.trackstudio.index.DocumentBuilder.*;

/**
 * Класс IndexManager содержит методы для работы с индексами Lucene
 */
@Immutable
public class IndexManager extends KernelManager implements IndexFields {
    private static final LockManager lockManager = LockManager.getInstance();
    private static final String className = "IndexManager.";
    private static final Log log = LogFactory.getLog(IndexManager.class);
    public static final String SKIPFLAG = "skipindex.flag";
    public static final String[] SPECIALCHAR = {";", "#", "\\", "+", "-", "&&", "||", "!", "(", ")", "{", "}", "[", "]", "^", "~", "*", "?", ":"};
    public static final String[] SPECIALCHARREPLACE = {" ", "","\\\\", "\\+", "\\-", "\\&&", "\\||", "\\!", "\\(", "\\)", "\\{", "\\}", "\\[", "\\]", "\\^", "\\~", "*", "?", "\\:"};
    public static final int mergeFactor = 30;

    //выбираем первых 100, больше никому не нужно.
    public static final int LUCENE_MAX_VALUE = 100;

    public static final SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<cite>", "</cite>");
    private final ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final ScheduledExecutorService commitExec = Executors.newSingleThreadScheduledExecutor();
    private final IndexWriter writer;

    /**
     * Реализация синглетона через внутрениий класс.
     * Нужен для запуска тестов поиска люцен без загрузки настроик.
     */
    private static final class Holder  {
        private static final IndexManager INSTANCE = new IndexManager();
    }

    /**
     * Конструктор по умолчанию
     */
    private IndexManager() {
        try {
            String dir = Config.getInstance().getIndexDir();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            LogByteSizeMergePolicy mergePolicy = new LogByteSizeMergePolicy();
            mergePolicy.setMergeFactor(mergeFactor);
            config.setMergePolicy(mergePolicy);

            writer = new IndexWriter(NIOFSDirectory.open(new File(dir).toPath()), config);
            if (!new File(dir + '/' + SKIPFLAG).exists()) {
                createIndex();
                if (!new File(dir + '/' + SKIPFLAG).createNewFile()) {
                    log.error("Could not create skip flag");
                }
            }
            BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
            commitExec.scheduleAtFixedRate(() -> {try { writer.commit(); } catch (IOException e) { e.printStackTrace();}}, 60,60, TimeUnit.SECONDS);

        } catch (Exception e) {
            throw new IllegalStateException("Error occurs when TrackStudio tries to init an Index! Details : ", e);
        }
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return Экземпляр IndexManager
     */
    public static IndexManager getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Метод производит индексацию данных для TS
     *
     * @throws GranException при необходимости
     */
    public void createIndex() throws GranException {
        log.info("index");
        boolean w = lockManager.acquireConnection(className);
        try {
            Long time = System.currentTimeMillis();
            indexTasks(writer);
            log.info("index tasks was done");
            indexUsers(writer);
            log.info("index users was done");
            indexAttachments(writer);
            log.info("index attachments was done");
            writer.commit();
            writer.forceMerge(1);
            log.info("Index time: " + String.valueOf(System.currentTimeMillis() - time) + " ms");
        } catch (IOException e) {
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Индексирует задачи
     *
     * @param writer Объект Writer, производящий индексацию
     * @throws GranException при необходимости
     */
    private void indexTasks(IndexWriter writer) throws GranException {
        List<TaskRelatedInfo> tree = TaskRelatedManager.getInstance().getCacheContents();
        for (TaskRelatedInfo tci : tree) {
            try {
                writer.addDocument(buildTask(tci));
            } catch (IOException e) {
                log.error(String.format("Index crash task id %s", tci.getId()), e);
            }
        }
    }

    /**
     * Индексирует пользователей
     *
     * @param writer Объект Writer, производящий индексацию
     * @throws GranException при необходимости
     */
    private void indexUsers(IndexWriter writer) throws GranException {
        List<UserRelatedInfo> tree = UserRelatedManager.getInstance().getCacheContents();
        for (UserRelatedInfo uri : tree) {
            try {
                writer.addDocument(buildUser(uri));
            } catch (IOException e) {
                log.error("Error", e);
            }
        }
    }

    /**
     * Индексирует приложенные задачи
     *
     * @param writer Объект Writer, производящий индексацию
     * @throws GranException при необходимости
     */
    private void indexAttachments(IndexWriter writer) throws GranException {
        List<AttachmentCacheItem> collAll = AttachmentManager.getInstance().getAllAttachmentList();
        for (AttachmentCacheItem att : collAll) {
            try {
                writer.addDocument(buildAttachment(att.getId(), att.getName(), att.getDescription()));
            } catch (IOException e) {
                log.error("Error", e);
            }
        }
    }

    /**
     * Ищет задачи по ключевому слову
     *
     * @param keyword ключевое слово
     * @return Карта найденных задач
     * @throws GranException пр инеобходимости
     */
    public Map<String, String> searchTasksWithHighLight(String keyword) throws GranException {
        return searchTasks(keyword, SearcherIndex::searchTasksWithHighLight);
    }

    public Map<String, String> searchTasks(String keyword) throws GranException {
        return searchTasks(keyword, (index, key) -> SearcherIndex.searchTasks(index, key));
    }

    private Map<String, String> searchTasks(String keyword, BiFunEx<IndexWriter, String, Map<String, String>> fun) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            if (keyword != null && keyword.trim().length() != 0) {
                Map<String, String> ret = fun.apply(writer, keyword);
                if (!ret.isEmpty())
                    return ret;

                // we cannot find anything, now try to change keyboard layout
                String keyword1 = MacrosUtil.convertWord(keyword, MacrosUtil.InputLocal.RU);
                if (!keyword.equals(keyword1)) { // transform really change the word
                    Map<String, String> ret1  = fun.apply(writer, keyword1);
                    if (!ret1.isEmpty())
                        return ret1;
                }

                // and try again, another direction
                String keyword2 = MacrosUtil.convertWord(keyword, MacrosUtil.InputLocal.US);
                if (!keyword.equals(keyword2)) { // transform really change the word
                    Map<String, String> ret2  = fun.apply(writer, keyword2);
                    if (!ret2.isEmpty())
                        return ret2;
                }

                // if found nothing in any case - return original empty result
                return ret;
            }
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
        return null;
    }

    /**
     * Ищет пользователей по ключевому слову
     *
     * @param keyword ключевое слово
     * @return Карта найденных пользователей
     * @throws GranException пр инеобходимости
     */
    public HashMap<String, String> searchUsers(String keyword, int limit) throws GranException {
        log.trace("search, keyword=" + keyword);
        boolean r = lockManager.acquireConnection(className);
        try {
            if (keyword != null && keyword.trim().length() != 0) {
                return SearcherIndex.searchUsers(writer, keyword, limit);
            }
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
        return null;
    }

    public HashMap<String, String> searchUsersWithHighLight(String keyword) throws GranException {
        log.trace("search, keyword=" + keyword);
        boolean r = lockManager.acquireConnection(className);
        try {
            if (keyword != null && keyword.trim().length() != 0) {
                return SearcherIndex.searchUsersWithHighLight(writer, keyword);
            }
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
        return null;
    }

    /**
     * Ищет прилоежнные файлы по ключевому слову
     *
     * @param keyword ключевое слово
     * @return Карта найденных прилоежнных файлов
     * @throws GranException пр инеобходимости
     */
    public HashMap<String, String> searchAttachments(String keyword) throws GranException {
        log.trace("search, keyword=" + keyword);
        boolean r = lockManager.acquireConnection(className);
        try {
            if (keyword != null && keyword.trim().length() != 0) {
                return SearcherIndex.searchKeyForAttachments(writer, keyword);
            }
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
        return null;
    }


    protected class Reindexer implements Runnable {
        private String id;
        private Document doc;

        public Reindexer(TaskRelatedInfo tri) throws GranException {
            boolean r = lockManager.acquireConnection(className);
            try {
                id = tri.getId();
                doc = buildTask(tri);
            } catch (Exception gr) {
                throw new GranException(gr);
            } finally {
                if (r) lockManager.releaseConnection(className);
            }
        }

        public void run() {
            try {
                writer.updateDocument(new Term(TASK_ID, id), doc);
            } catch (Exception g) {
                log.error("ReInderTask", g);
            }
        }
    }

    /**
     * Производит переиндексацию задачи
     *
     * @param task задачи, которую переиндексируем
     * @throws GranException при необходимости
     */
    public void reIndexTask(TaskRelatedInfo task) throws GranException {
        exec.execute(new Reindexer(task));
    }


    /**
     * Производит переиндексацию пользователя
     *
     * @param userId ID пользователя, которого переиндексируем
     * @throws GranException при необходимости
     */
    public void reIndexUser(String userId) throws GranException {
        log.trace("reIndexUser userId=" + userId);
        boolean w = lockManager.acquireConnection(className);
        try {
            UserRelatedInfo uci = UserRelatedManager.getInstance().find(userId);
            writer.updateDocument(new Term(USER_ID, userId), buildUser(uci));
        } catch (Exception g) {
            log.error("reIndexUser", g);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Производит переиндексацию прилоежнныхфайлов
     *
     * @param attachmentId ID прилоежнного файла, который переиндексируем
     * @throws GranException при необходимости
     */
    public void reIndexAttachment(String attachmentId) throws GranException {
        log.trace("reIndexUser attachmentId=" + attachmentId);
        boolean w = lockManager.acquireConnection(className);
        try {
            Attachment att = KernelManager.getFind().findAttachment(attachmentId);
            if (att != null) {
                writer.updateDocument(new Term(ATTACH_ID, attachmentId), buildAttachment(att.getId(), att.getName(), att.getDescription()));
            }
        } catch (Exception g) {
            log.error("reIndexUser", g);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удаляет задачу из индекса
     *
     * @param taskId ID удаляемой задачи
     * @throws GranException при необходимости
     */
    public void deleteTask(String taskId) throws GranException {
        log.trace("deleteTask taskId = " + taskId);
        boolean w = lockManager.acquireConnection(className);
        try {
            writer.deleteDocuments(new Term(TASK_ID, taskId));
        } catch (IOException e) {
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удаляет пользователя из индекса
     *
     * @param userId ID удаляемого пользователя
     * @throws GranException при необходимости
     */
    public void deleteUser(String userId) throws GranException {
        log.trace("deleteUser userId = " + userId);
        boolean w = lockManager.acquireConnection(className);
        try {
            writer.deleteDocuments(new Term(USER_ID, userId));
        } catch (IOException e) {
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удаляет прилоежнный файл из индекса
     *
     * @param attachmentId ID удаляемого файла
     * @throws GranException пр необхзодимости
     */
    public void deleteAttachment(String attachmentId) throws GranException {
        log.trace("deleteUser attachmentId = " + attachmentId);
        boolean w = lockManager.acquireConnection(className);
        try {
            writer.deleteDocuments(new Term(ATTACH_ID, attachmentId));
        } catch (IOException e) {
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список задач, которые ссылаются на указанную задачу
     *
     * @param taskId ID задачи
     * @return список задач
     * @throws GranException при необходимости
     * @see com.trackstudio.tools.EggBasket
     */
    public EggBasket<String, String> getReferencedTasksForTask(String taskId) throws GranException {
        return SearcherIndex.getReferenced(writer, taskId, REF_BY_TASK_FOR_TASK, TASK_ID);
    }

    /**
     * Возвращает список пользователей, которые ссылаются на указанную задачу
     *
     * @param taskId ID задачи
     * @return список задач
     * @throws GranException при необходимости
     * @see com.trackstudio.tools.EggBasket
     */
    public EggBasket<String, String> getReferencedUsersForTask(String taskId) throws GranException {
        return SearcherIndex.getReferenced(writer, taskId, REF_BY_TASK_FOR_USER, TASK_ID);
    }

    /**
     * Возвращает список задач, которые ссылаются на указанного пользователя
     *
     * @param userId ID пользователя
     * @return список задач
     * @throws GranException при необходимости
     * @see com.trackstudio.tools.EggBasket
     */
    public EggBasket<String, String> getReferencedTasksForUser(String userId) throws GranException {
        return SearcherIndex.getReferenced(writer, userId, REF_BY_USER_FOR_TASK, USER_ID);
    }

    /**
     * Возвращает список пользователей, которые ссылаются на указанного пользователя
     *
     * @param userId ID пользователя
     * @return список задач
     * @throws GranException при необходимости
     * @see com.trackstudio.tools.EggBasket
     */
    public EggBasket<String, String> getReferencedUsersForUser(String userId) throws GranException {
        return SearcherIndex.getReferenced(writer, userId, REF_BY_USER_FOR_USER, USER_ID);
    }

    /**
     * Заменяет символы, которые являются "опасными" для хранения в индексе люцена на их безопасные варианты
     *
     * @param s Исходная строка
     * @return "Безопасная" строка
     * @throws GranException при необходимости
     */
    public static String checkLuceneCharacter(String s) throws GranException {
        if (s != null) {
            StringBuffer res = new StringBuffer(s);
            for (int i = 0; i < SPECIALCHAR.length; i++) {
                if (s.indexOf(SPECIALCHAR[i]) != -1) {
                    String source = res.toString();
                    int fromIndex = source.indexOf(SPECIALCHAR[i]);
                    while (source.indexOf(SPECIALCHAR[i], fromIndex) > -1) {
                        res = res.replace(source.indexOf(SPECIALCHAR[i], fromIndex), source.indexOf(SPECIALCHAR[i], fromIndex) + SPECIALCHAR[i].length(), SPECIALCHARREPLACE[i]);
                        fromIndex = source.indexOf(SPECIALCHAR[i], fromIndex) + SPECIALCHARREPLACE[i].length();
                        source = res.toString();
                    }
                }
            }
            return res.toString();
        } else
            return s;
    }

    public void closeIndex() {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}