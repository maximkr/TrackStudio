package com.trackstudio.index;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntRange;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;

import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.Pair;

import net.jcip.annotations.Immutable;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NIOFSDirectory;

import static com.trackstudio.index.DocumentBuilder.ALL_FIELDS;
import static com.trackstudio.index.DocumentBuilder.ATTACH_ALL_FIELDS;
import static com.trackstudio.index.DocumentBuilder.ATTACH_DESC;
import static com.trackstudio.index.DocumentBuilder.ATTACH_ID;
import static com.trackstudio.index.DocumentBuilder.ATTACH_NAME;
import static com.trackstudio.index.DocumentBuilder.*;
import static com.trackstudio.index.DocumentBuilder.REF_BY_TASK_FOR_USER;
import static com.trackstudio.index.DocumentBuilder.REF_BY_USER_FOR_USER;
import static com.trackstudio.index.DocumentBuilder.TASK_DESC;
import static com.trackstudio.index.DocumentBuilder.TASK_ID;
import static com.trackstudio.index.DocumentBuilder.TASK_MSGS;
import static com.trackstudio.index.DocumentBuilder.TASK_NAME;
import static com.trackstudio.index.DocumentBuilder.USER_COMPANY;
import static com.trackstudio.index.DocumentBuilder.USER_ID;
import static com.trackstudio.index.DocumentBuilder.USER_LOGIN;
import static com.trackstudio.index.DocumentBuilder.USER_NAME;
import static com.trackstudio.index.DocumentBuilder.analyzer;
import static com.trackstudio.kernel.manager.IndexManager.*;

@Immutable
public class SearcherIndex {
    private static final Log log = LogFactory.getLog(DocumentBuilder.class);
    private static final float lowScore = 0.05f;

    public static Query buildMultiFieldsQuery(String key, String[] fields) {
        log.debug("Search : key : " + key + " fields : " + Arrays.asList(fields));
        BooleanQuery.setMaxClauseCount(2048);
        if (key.startsWith("*") || key.startsWith("?")) key = '\'' + key;
        key = key.toLowerCase();
        BooleanQuery.Builder query = new BooleanQuery.Builder();
        for (String field : fields) {
            BooleanQuery.Builder phrase = new BooleanQuery.Builder();
            for (String term : key.split(" ")) {
                phrase.add(new WildcardQuery(new Term(field, term)), BooleanClause.Occur.MUST);
            }

            query.add(phrase.build(), BooleanClause.Occur.SHOULD);
        }
        return query.build();
    }

    /**
     * This method builds a query for similar's search
     * @param text searching text
     * @param fields fields which should be checked
     * @return Query
     */
    private static Query buildSimilarQuery(String text, String[] fields) {
        List<Query> queries = new ArrayList<Query>();
        text = text.toLowerCase();
        for (String value : text.split(" ")) {
            value = (value.startsWith("*") || value.startsWith("?")) ? value.substring(1) : value;
            for (String field : fields) {
                queries.add(new WildcardQuery(new Term(field, value)));
            }
        }
        BooleanQuery.setMaxClauseCount(queries.size());
        BooleanQuery.Builder query = new BooleanQuery.Builder();
        for (Query innerQuery : queries) {
            query.add(innerQuery, BooleanClause.Occur.SHOULD);
        }
        return query.build();
    }

    public static EggBasket<String, String> getReferenced(final IndexWriter writer, final String forId, final String where, final String KEY_ID) throws GranException {
        log.trace("getReferenced");
        final EggBasket<String, String> basket = new EggBasket<String, String>();
        try {
            if (forId == null)
                return basket;
            final String thisId = (new StringBuffer(" ")).append(forId).append(' ').toString();
            Query query = new QueryParser(where, analyzer).parse(thisId);
            SearcherIndex.search(writer, query, LUCENE_MAX_VALUE,
                    (doc, score) -> {
                        String id = doc.get(KEY_ID);
                        if (id != null) {
                            String refs = doc.get(where);
                            String[] udfs = refs.split("\\|");
                            for (String udf : udfs) {
                                //Below values have follow format: udfId (taskId taskNumber taskName)
                                String[] values = udf.trim().split(" ", 2);
                                if (values.length > 1 && values[1].contains(forId)) {
                                    basket.putItem(values[0], id);
                                }
                            }
                        }
                        return null;
                    }
            );
        } catch (Exception e) {
            log.error(" caught a " + e.getClass() + "\n ", e);
            throw new GranException(e);
        }
        return basket;

    }

    public static HashMap<String, String> searchKeyForAttachments(final IndexWriter writer, String word) throws GranException {
        var result = new HashMap<String, String>();
        try {
            Query query = buildMultiFieldsQuery(checkLuceneCharacter(word), new String[]{ATTACH_ID, ATTACH_NAME, ATTACH_DESC, ATTACH_ALL_FIELDS});
            final Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query, ALL_FIELDS));
            SearcherIndex.search(writer, query, LUCENE_MAX_VALUE, new SearcherIndex.BuildEntity<Object>() {
                @Override
                public Object make(Document doc, Float score) {
                    String attachmentId = doc.get(ATTACH_ID);
                    String mes = doc.get(ATTACH_ALL_FIELDS);
                    String surroundText = null;
                    try {
                        TokenStream tokenStream = analyzer.tokenStream(ATTACH_ALL_FIELDS, new StringReader(mes));
                        surroundText = highlighter.getBestFragments(tokenStream, mes, 3, "...");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (attachmentId != null) {
                        result.put(attachmentId, surroundText);
                    }
                    return attachmentId;
                }
            });
            return result;
        } catch (Exception e) {
            log.error(" caught a " + e.getClass() + "\n", e);
            throw new GranException(e);
        }
    }

    public static HashMap<String, String> searchUsersWithHighLight(final IndexWriter writer, String word) throws GranException {
        var result = new HashMap<String, String>();
        try {
            Query query = buildMultiFieldsQuery(checkLuceneCharacter(word), new String[]{USER_ID, USER_NAME, USER_LOGIN, USER_COMPANY, REF_BY_TASK_FOR_USER, REF_BY_USER_FOR_USER, ALL_FIELDS});
            final Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query, ALL_FIELDS));
            SearcherIndex.search(writer, query, LUCENE_MAX_VALUE, new SearcherIndex.BuildEntity<Object>() {
                @Override
                public Object make(Document doc, Float score) {
                    String userId = doc.get(USER_ID);
                    String mes = doc.get(ALL_FIELDS);

                    try {
                        if (userId != null && UserRelatedManager.getInstance().isUserExists(userId)) {
                            TokenStream tokenStream = analyzer.tokenStream(ALL_FIELDS, new StringReader(mes));
                            String surroundText = highlighter.getBestFragments(tokenStream, mes, 3, "...");
                            result.put(userId, surroundText);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });
            return result;
        } catch (Exception e) {
            log.error(" caught a " + e.getClass() + "\n", e);
            throw new GranException(e);
        }
    }

    public static HashMap<String, String> searchUsers(final IndexWriter writer, String word, int limit) throws GranException {
        var result = new HashMap<String, String>();
        try {
            Query query = buildMultiFieldsQuery(
                    checkLuceneCharacter(word),
                    new String[] {
                            USER_ID, USER_NAME, USER_LOGIN, USER_COMPANY
                    }
            );
            SearcherIndex.search(
                    writer, query, limit,
                    (doc, score) -> result.put(doc.get(USER_ID), null)
            );
            return result;
        } catch (Exception e) {
            throw new GranException(e);
        }
    }

    public static Map<String, String> searchTasks(final IndexWriter writer, String word) throws GranException {
        try {
            final var rsl = new HashMap<String, String>();
            Query query = buildMultiFieldsQuery(
                    checkLuceneCharacter(word),
                    new String[] {
                            TASK_NUMBER, TASK_NAME, TASK_DESC, TASK_UPDATE, TASK_MSGS
                    }
            );
            SearcherIndex.search(
                    writer, query, LUCENE_MAX_VALUE,
                    (doc, score) ->  rsl.put(doc.get(TASK_ID), null),
                    TASK_UPDATE
            );
            return rsl;
        } catch (Exception e) {
            throw new GranException(e);
        }
    }

    public static HashMap<String, String> searchTasksWithHighLight(final IndexWriter writer, String word) throws GranException {
        var result = new HashMap<String, String>();
        try {
            Query query = buildMultiFieldsQuery(checkLuceneCharacter(word), new String[]{TASK_NUMBER, TASK_NAME, TASK_DESC, TASK_UPDATE, TASK_MSGS, ALL_FIELDS});
            final Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query, ALL_FIELDS));
            SearcherIndex.search(writer, query, LUCENE_MAX_VALUE,
                    (doc, score) -> {
                        String taskId = doc.get(TASK_ID);
                        try {
                            if (taskId != null && TaskRelatedManager.getInstance().isTaskExists(taskId)) {
                                String mes = doc.get(ALL_FIELDS);
                                result.put(taskId, highlighter.getBestFragments(
                                        analyzer.tokenStream(ALL_FIELDS, new StringReader(mes)), mes, 3, "...")
                                );
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    },
                    TASK_UPDATE);
            return result;
        } catch (Exception e) {
            throw new GranException(e);
        }
    }

    public interface BuildEntity<T> {
        T make(Document doc, Float score);
    }

    public static <T> List<T> search(final IndexWriter writer, Query query, int limit, BuildEntity<T> builder) throws IOException {
        return search(writer, query, limit, builder, null);
    }

    public static <T> List<T> search(final IndexWriter writer, Query query, int limit, BuildEntity<T> builder, String sortBy) throws IOException {
        List<T> list = new ArrayList<T>();
        DirectoryReader indexReader = DirectoryReader.open(writer);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        Sort sort = new Sort();
        if (sortBy != null) {
            sort = new Sort(new SortField(sortBy, SortField.Type.LONG, true));
        }
        TopDocsCollector collector = TopFieldCollector.create(
                sort, limit, false, true, true, false);
        searcher.search(query, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;
        for (ScoreDoc hit : hits) {
            Document doc = searcher.doc(hit.doc);
            T t = builder.make(doc, hit.score);
            if (t != null) {
                list.add(t);
            }
        }
        indexReader.close();
        return list;
    }
}
