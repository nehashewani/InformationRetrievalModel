package com.information.retrieval.text.transformation.statistics;

import com.information.retrieval.fileio.FileUtility;
import com.information.retrieval.text.transformation.pojo.DocumentInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.information.retrieval.text.transformation.constants.Constants.SPACE;

/**
 * @author  Neha Shewani
 */
public class RelevanceStatisticsImpl implements RelevanceStatistics {

    String filePath;

    @Override
    public void textStatisticsInformation(String path) {
        this.filePath = path;
    }

    @Override
    public QueryRelevanceInfo queryRelevanceInfo() {
        return new RelevanceInfoParserImpl(filePath).parseRelevanceInfo();
    }

    private static class RelevanceInfoParserImpl {
        private String relevanceInfoFilePath;

        RelevanceInfoParserImpl(String filePath) {
            this.relevanceInfoFilePath = filePath;
        }

        QueryRelevanceInfo parseRelevanceInfo() {
            try {
                FileUtility.CustomFileReader customFileReader
                        = new FileUtility.CustomFileReader(relevanceInfoFilePath);

                Map<String, Set<DocumentInfo>> queryRelevanceInfoMap = new HashMap<>();

                customFileReader.readLinesFromFile().forEach(line ->
                {
                    String[] splitLine = line.split(SPACE);
                    final String QUERY_ID = splitLine[0];
                    final String DOCUMENT_ID = splitLine[2];
                    queryRelevanceInfoMap.putIfAbsent(QUERY_ID, new HashSet<>());
                    queryRelevanceInfoMap.get(QUERY_ID)
                            .add(new DocumentInfo(DOCUMENT_ID));
                });

                return new QueryRelevanceInfo(queryRelevanceInfoMap);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Exception !!! Returning null;
            return null;
        }

    }
}
