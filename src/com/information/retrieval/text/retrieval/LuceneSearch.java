package com.information.retrieval.text.retrieval;

import com.information.retrieval.fileio.FileUtility;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.information.retrieval.text.transformation.constants.Constants.NEWLINE;
import static com.information.retrieval.text.transformation.constants.Constants.SPACE;

/**
 * This class performs the search operation
 * based on lucene implementation
 */

public class LuceneSearch {

    // Stores the output directory for Search_Result
    private static final String SEARCH_RESULT_DIR = "Project_Files\\CACM_LUCENE_RESULTS";
    private static final String Q0 = "Q0";

    // Stores the directory where lucene index will be created
    private String luceneIndexDir;
    // Stores the
    private String queryFilePath;

    private Analyzer analyzer;
    private static IndexSearcher searcher;
    private static TopScoreDocCollector collector;

    public void setLuceneIndexDir(String luceneIndexDir) {
        this.luceneIndexDir = luceneIndexDir;
    }

    public void setQueryFilePath(String queryFilePath) {
        this.queryFilePath = queryFilePath;
    }


    /**
     * Performs the search for each query present in the file
     * specified by queryFilePath
     * @throws IOException
     */
    public void performSearch() throws IOException {
        configuration();

        File fileDir = new File(queryFilePath);
        if (fileDir.isDirectory()) {
            File[] files = fileDir.listFiles();
            Arrays.stream(files).forEach(file -> performSearchUsingFileContents(file));
        }
    }

    /**
     * Reads the query strings from files,
     * Calculates the score for each document for a query
     * and writes it to a file
     * @param file
     */
    private void performSearchUsingFileContents(File file) {
        try {
            FileUtility.CustomFileReader customFileReader =
                    new FileUtility.CustomFileReader(file.getAbsolutePath()
                            .replace(".txt", ""));

            String queryString = null; int i = 1;
            while ((queryString = customFileReader.readLineFromFile()) != null) {
            	
                Query q = new QueryParser(Version.LUCENE_47, "contents",
                        analyzer).parse(QueryParser.escape(queryString));

                collector = TopScoreDocCollector
                        .create(100, true);
                searcher.search(q, collector);
                ScoreDoc[] hits = collector.topDocs().scoreDocs;

                Map<String, Float> documentScoreMapper =
                        new HashMap<>();
                Arrays.stream(hits).forEach(hit ->
                {
                    int docId = hit.doc;
                    Document d = null;
                    try {
                        d = searcher.doc(docId);
                        documentScoreMapper.put(d.get("path"), hit.score);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                SearchResult searchResult =
                        new SearchResult(String.valueOf(i), documentScoreMapper);

                FileUtility.writeToFile(searchResult, SEARCH_RESULT_DIR + File.separator + i);
                i++;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Initial configuration
    // required for lucene indexer
    private void configuration() throws IOException {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(
                luceneIndexDir)));

        analyzer = new SimpleAnalyzer(Version.LUCENE_47);
        searcher = new IndexSearcher(reader);

        // Creates the directory for Storing the search results.
        File file = new File(SEARCH_RESULT_DIR);
        file.mkdir();

    }

    // Main method
    public static void main(String args[]) throws IOException {
        LuceneSearch luceneSearch = new LuceneSearch();
        luceneSearch.setLuceneIndexDir("Lucene Index");
        luceneSearch.setQueryFilePath("Queries");
        luceneSearch.performSearch();

    }

    // Nested class to store document scores per query
    private static class SearchResult {
        private static final String SYSTEM_NAME = "SIBENDU_SYSTEM";
        private String queryText;
        private Map<String, Float> documentScoreMapper;

        SearchResult(String queryText,
                     Map<String, Float> documentScoreMapper) {

            this.queryText = queryText;
            this.documentScoreMapper = documentScoreMapper;
        }


        @Override
        public String toString() {
            final AtomicInteger rank = new AtomicInteger(1);
            return documentScoreMapper.entrySet()
                    .stream()
                    .sorted((entry1 , entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                    .collect(StringBuilder::new,
                            (sb, entry) ->
                            {
                                sb.append(queryText + SPACE + Q0 + SPACE);
                                sb.append(entry.getKey().substring(entry.getKey().lastIndexOf("\\")+1)
                                        + SPACE + rank.getAndIncrement() + SPACE +
                                        entry.getValue() + SPACE + SYSTEM_NAME);
                                sb.append(NEWLINE);
                            },
                            (sb1,sb2) -> sb1.append(sb2)).toString();
        }
    }

}
