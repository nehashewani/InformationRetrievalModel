package com.information.retrieval.text.retrieval.feedback;

import com.information.retrieval.text.retrieval.stopping.Stopping;
import com.information.retrieval.text.transformation.indexing.InvertedIndex;

import java.util.List;
import java.util.Map;

/**
 * The primary job of this function is to
 * return a list of top weighted words
 * according to the implementation of
 * Relevance Feedback
 *
 * This implementation should consider stopping
 * for evaluating the list of top
 * weighted words
 *
 
 */
@FunctionalInterface
public interface RelevanceFeedbackWithStopping {
    public List<String> expandedQuery(String query,
                                      Map<String, Double> documentScoreMap,
                                      InvertedIndex index,
                                      Stopping stopping);
}
