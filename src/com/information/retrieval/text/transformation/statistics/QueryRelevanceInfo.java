package com.information.retrieval.text.transformation.statistics;

import com.information.retrieval.text.transformation.pojo.DocumentInfo;

import java.util.*;

/**
 * @author  Neha Shewani
 */
public class QueryRelevanceInfo {

    Map<String, Set<DocumentInfo>> queryRelevantDocMap;

    public Map<String, Set<DocumentInfo>> queryRelevantDocMap() {
        return Collections.unmodifiableMap(queryRelevantDocMap);
    }

    QueryRelevanceInfo(Map<String, Set<DocumentInfo>> queryRelevantDocMap) {
        this.queryRelevantDocMap = queryRelevantDocMap;
    }

    public int relevantDocs(String term) {
        return queryRelevantDocMap.entrySet()
                .stream()
                .filter(entry -> entry.getKey().contains(term))
                .collect(HashSet::new,
                        (set, entry) -> set.addAll(entry.getValue()),
                        (set1, set2) -> set1.addAll(set2))
                .size();
    }
}
