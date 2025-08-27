package com.trackstudio.kernel.manager;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LogByteSizeMergePolicy;
import org.apache.lucene.store.NIOFSDirectory;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@Ignore
public class IndexManagerTest {
    @Test
    public void whenHugeText() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        LogByteSizeMergePolicy mergePolicy = new LogByteSizeMergePolicy();
        mergePolicy.setMergeFactor(30);
        config.setMergePolicy(mergePolicy);
        IndexWriter writer = new IndexWriter(NIOFSDirectory.open(new File("./index/").toPath()), config);
        List<Document> docs = new ArrayList<>();
        String text = "Дайтемнеещеэтихмягкихфранцускихбулочек";
        for (int i = 0; i < 100_000_000; i++) {
            Document doc = new Document();
            doc.add(new TextField("name", text, Field.Store.YES));
            docs.add(doc);
        }
        writer.addDocuments(docs);
        writer.commit();
        writer.close();
    }
}