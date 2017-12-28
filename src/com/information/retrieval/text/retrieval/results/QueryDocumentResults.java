package com.information.retrieval.text.retrieval.results;

import com.information.retrieval.text.transformation.pojo.DocumentInfo;
import com.information.retrieval.text.transformation.pojo.QueryInfo;
import org.apache.lucene.search.Query;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/***
 * A Pojo to store documents results for a list
 * of queries
 */
public class QueryDocumentResults {
    private Map<String, Set<DocumentInfo>> queryDocumentInfoMap;

    public Map<String, Set<DocumentInfo>> getQueryDocumentInfoMap() {
        return Collections.unmodifiableMap(queryDocumentInfoMap);
    }

    public QueryDocumentResults(Map<String, Set<DocumentInfo>> queryDocumentInfoMap) {
        this.queryDocumentInfoMap = queryDocumentInfoMap;
    }

}
