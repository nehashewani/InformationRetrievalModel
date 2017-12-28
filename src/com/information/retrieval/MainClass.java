package com.information.retrieval;

import com.information.retrieval.text.retrieval.*;
import com.information.retrieval.text.retrieval.model.RetrievalModelContext;
import com.information.retrieval.text.retrieval.model.RetrievalModelContextImpl;
import com.information.retrieval.text.retrieval.model.scoring.BM25Scoring;
import com.information.retrieval.text.retrieval.model.scoring.DocumentScoringStrategy;
import com.information.retrieval.text.transformation.indexing.LuceneIndexer;

import java.io.IOException;

public class MainClass {
    public static void main(String args[]) throws IOException {

        // Creates the lucene index files
        LuceneIndexer luceneIndexer = new LuceneIndexer();
        luceneIndexer.setFilesDirPath("PARSED TEXT");
        luceneIndexer.setLuceneIndexDirPath("Lucene Index");
        luceneIndexer.createIndexWithLucene();

        // Perform search operations for Queries against the
        // indexes created by Lucene
        LuceneSearch luceneSearch = new LuceneSearch();
        luceneSearch.setLuceneIndexDir("Lucene Index");
        luceneSearch.setQueryFilePath("Queries");
        luceneSearch.performSearch();

        // Perform search operations for Queries against the
        // indexes created for HW3 Assignment
        RetrievalModelContext bm25 = new RetrievalModelContextImpl();
        bm25.indexPath("index/Document_Frequency_Index_Unigram");
        bm25.queryPath("Queries");
        DocumentScoringStrategy bm25Scoring
                = new BM25Scoring();

        bm25.calculateScores(bm25Scoring);

    }
}
