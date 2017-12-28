package com.information.retrieval.text.transformation.indexing;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * Uses Lucene API to create indexes for HTML Documents
 */
public class LuceneIndexer {

    private String luceneIndexDirPath;
    private String filesDirPath;
    private IndexWriter writer;

    // Sets the output path for lucene index
    public void setLuceneIndexDirPath(String luceneIndexDirPath) {
        this.luceneIndexDirPath = luceneIndexDirPath;

        // Creates the directory if not present
        File fileDir = new File(luceneIndexDirPath);

        if (!fileDir.exists()) {
            fileDir.mkdir();
        }

    }

    // Sets the file directory for parsing documents
    public void setFilesDirPath(String filesDirPath) {
        this.filesDirPath = filesDirPath;
    }

    /**
     * Creates the indexes with lucene api
     *
     * @throws IOException
     */
    public void createIndexWithLucene() throws IOException {
        // Configures lucene
        configLucene();

        // Creates the index
        File fileDir = new File(filesDirPath);


        if (fileDir.isDirectory()) {
            File[] files = fileDir.listFiles();
            Arrays.stream(files).forEach(file -> createIndexForFile(file));
        } else {
            createIndexForFile(fileDir);
        }

        writer.close();
    }

    /**
     * Creates the index for this file
     *
     * @param file
     */
    private void createIndexForFile(File file) {
        Document doc = new Document();

        FileReader fr = null;
        try {
            fr = new FileReader(file);
            doc.add(new TextField("contents", fr));
            doc.add(new StringField("path", file.getPath(), Field.Store.YES));
            doc.add(new StringField("filename", file.getName(),
                    Field.Store.YES));
            writer.addDocument(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs the initial configuration for Lucene indexer
     *
     * @throws IOException
     */
    private void configLucene() throws IOException {


        Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_47);

        FSDirectory dir = FSDirectory.open(new File(luceneIndexDirPath));

        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47,
                analyzer);

        writer = new IndexWriter(dir, config);

    }

    // Main method
    public static void main(String args[]) throws IOException {
        LuceneIndexer luceneIndexer = new LuceneIndexer();
        luceneIndexer.setFilesDirPath("Parsed Text");
        luceneIndexer.setLuceneIndexDirPath("Lucene Index");
        luceneIndexer.createIndexWithLucene();
    }
}
