package com.information.retrieval.text.retrieval.feedback;

import com.information.retrieval.text.transformation.indexing.InvertedIndex;

import java.util.List;
import java.util.Map;

/**
 * The primary job of this function is to
 * return a list of top weighted words
 * according to the implementation of
 * Relevance Feedback
 *
 
 */
@FunctionalInterface
public interface RelevanceFeedBack {
    List<String> expandedQuery(Map<String, Double> documentScoreMap,
                               InvertedIndex index,
                               String initialQuery);

}
