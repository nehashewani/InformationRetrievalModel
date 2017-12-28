package com.information.retrieval.text.retrieval.model.scoring;

import com.information.retrieval.text.retrieval.stopping.Stopping;
import com.information.retrieval.text.transformation.indexing.InvertedIndex;

import java.util.Map;

/**
 * An Interface for Document Scoring Strategy
 * Different algorithms for scoring document should implement this
 * interface
 *
 * @author  Neha Shewani
 */
public interface DocumentScoringStrategy {
    /***
     * Returns a Map representing document scores for query
     *
     **/
    Map<String, Double> documentScores(String query, InvertedIndex ie);

    /***
     *  Returns a Map representing document scores for query
     *  This method consider stopping for calculating scores
     *
     **/
    Map<String, Double> documentScoresWithStopping(String query,
                                                   InvertedIndex ie,
                                                   Stopping stopping);

}
