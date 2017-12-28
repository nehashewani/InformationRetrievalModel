package com.information.retrieval.text.retrieval.model;

import com.information.retrieval.text.retrieval.model.scoring.DocumentScoringStrategy;
import com.information.retrieval.text.retrieval.stopping.Stopping;

/**
 * An interface for defining the contract of retrieval model
 *
 *
 */
public interface RetrievalModelContext {

    /***
     * Sets the path where query file can be found
     * @param queryPath
     */
    public void queryPath(String queryPath);
    /**
     * Set the path where serialized object of Inverted Index
     * can be found
     * @param indexPath
     */
    public void indexPath(String indexPath);

    /**
     * Calculate the document scores based on DocumentScoringStrategy
     *
     */
    public void calculateScores(DocumentScoringStrategy documentScoringStrategy);

    /***
     * Calculate the document scores based on DocumentScoringStrategy
     * Consider relevancy information.
     * An implementation of PseudoRelevanceWithStopping
     * is required for calculating the list
     * of top weighted terms for initially calculated top K documents
     *
     ***/
    void calculateScoresWithRelevancy(DocumentScoringStrategy strategy, Stopping stopping);

    /***
     * Calculate the document scores based on DocumentScoringStrategy
     * Stopwords are considered in this implementation
     *
     *
     *
     ***/
    void calculateScoresWithStopping(DocumentScoringStrategy strategy, Stopping stopping);

    /**
     * Takes the input of the outputPath where
     * document scores are to be written
     *
     * The files are named according
     * to the query ID
     *
     * @param outputPath
     */
    public void outputPath(String outputPath);

    /***
     * Turns on pseudo relevance feedback
     * @param isPseudoRelevanceRequired
     */
    public void setPseudoRelevanceFeedback(Boolean isPseudoRelevanceRequired);

    /***
     * The top K documents to be retrieved
     * where K <= Total number of documents in the corpus
     * @param K
     */
    void topKDocument(int K);
}
