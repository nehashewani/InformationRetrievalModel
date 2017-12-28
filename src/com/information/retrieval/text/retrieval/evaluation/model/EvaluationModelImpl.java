package com.information.retrieval.text.retrieval.evaluation.model;

import com.information.retrieval.fileio.FileUtility;
import com.information.retrieval.text.retrieval.evaluation.table.EvaluationResultTable;
import com.information.retrieval.text.retrieval.evaluation.table.RankValue;
import com.information.retrieval.text.retrieval.results.QueryDocumentResults;
import com.information.retrieval.text.retrieval.results.QueryDocumentResultsParser;
import com.information.retrieval.text.transformation.statistics.QueryRelevanceInfo;
import com.information.retrieval.text.transformation.statistics.RelevanceStatistics;
import com.information.retrieval.text.transformation.statistics.RelevanceStatisticsImpl;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author  Neha Shewani
 */
public class EvaluationModelImpl implements EvaluationModel {

    private QueryDocumentResults queryDocumentResults;
    private QueryRelevanceInfo queryRelevanceInfo;
    private EvaluationResultTable evaluationResultTable;

    /***
     *
     * Creates a document result model for evaluating the
     * retrieval model system
     *
     * @param path  The directory path where result
     * document scores are stored
     * for queries.
     */

    @Override
    public void documentScores(String path) {
        QueryDocumentResultsParser queryDocumentResultsParser =
                new QueryDocumentResultsParser(path);

        queryDocumentResults =
                queryDocumentResultsParser.queryDocumentScores();
    }

    /***
     *
     * Creates a document relevancy model
     * for evaluating the retrieval model
     *
     * @param path The path where document relevancy information are stored
     * for queries
     */
    @Override
    public void relevanceInformation(String path) {
        RelevanceStatistics relevanceStatistics = new RelevanceStatisticsImpl();
        relevanceStatistics.textStatisticsInformation(path);
        this.queryRelevanceInfo = relevanceStatistics.queryRelevanceInfo();
    }

    /***
     * Runs the evaluation model using document result model
     * and relevancy model and writes to the specified path
     * @param directoryPath
     */
    @Override
    public void evaluateSystem(String directoryPath) {
        // Calculate Precision Scores for every Query
        Map<String, Set<RankValue>> queryPrecisionMap = new TreeMap<>();

        Map<String, Set<RankValue>> queryRecallMap = new TreeMap<>();

        // Calculates the precision and recall values per
        queryDocumentResults.getQueryDocumentInfoMap()
                .entrySet()
                .stream()
                .filter(entry -> queryRelevanceInfo.queryRelevantDocMap().containsKey(entry.getKey()))
                .forEach(entry ->
                {
                    final AtomicInteger retrievedDoc = new AtomicInteger(0);
                    final AtomicInteger relevantDocs = new AtomicInteger(0);
                    entry.getValue().stream()
                            .forEach(documentInfo ->
                            {
                                if (queryRelevanceInfo.queryRelevantDocMap().containsKey(entry.getKey())) {
                                    if (queryRelevanceInfo.queryRelevantDocMap()
                                            .get(entry.getKey()).contains(documentInfo)) {


                                        Double precisionValueAtThisPos = (double) relevantDocs.incrementAndGet() /
                                                retrievedDoc.incrementAndGet();

                                        RankValue precisionRankValue =
                                                new RankValue(retrievedDoc.get(), precisionValueAtThisPos, true);

                                        queryPrecisionMap.putIfAbsent(entry.getKey(), new HashSet<>());
                                        queryPrecisionMap.get(entry.getKey()).add(precisionRankValue);

                                        Double recallValueAtThisPos =
                                                (double) relevantDocs.get() /
                                                        queryRelevanceInfo.queryRelevantDocMap()
                                                                .get(entry.getKey()).size();

                                        RankValue recallRankValue =
                                                new RankValue(retrievedDoc.get(), recallValueAtThisPos, true);

                                        queryRecallMap.putIfAbsent(entry.getKey(), new HashSet<>());
                                        queryRecallMap.get(entry.getKey()).add(recallRankValue);

                                    } else {

                                        Double precisionValueAtThisPos =
                                                (double) relevantDocs.get() / retrievedDoc.incrementAndGet();

                                        RankValue precisionRankValue =
                                                new RankValue(retrievedDoc.get(), precisionValueAtThisPos, false);

                                        queryPrecisionMap.putIfAbsent(entry.getKey(), new HashSet<>());
                                        queryPrecisionMap.get(entry.getKey()).add(precisionRankValue);

                                        Double recallValueAtThisPos =
                                                (double) relevantDocs.get() /
                                                        queryRelevanceInfo.queryRelevantDocMap().get(entry.getKey()).size();

                                        RankValue recallRankValue =
                                                new RankValue(retrievedDoc.get(), recallValueAtThisPos, false);

                                        queryRecallMap.putIfAbsent(entry.getKey(), new HashSet<>());

                                        queryRecallMap.get(entry.getKey()).add(recallRankValue);
                                    }
                                }
                            });
                });

        evaluationResultTable = new EvaluationResultTable();

        evaluationResultTable.setQueryPrecisionValue(queryPrecisionMap);
        evaluationResultTable.setQueryRecallValue(queryRecallMap);
        writeToFile(evaluationResultTable,directoryPath);

    }

    /***
     * Writes the evaluation results to a file specified by @param filePath
     * @param evaluationResultTable
     * @param filePath
     */
    private void writeToFile(EvaluationResultTable evaluationResultTable, String filePath) {
        FileUtility.writeToFile(evaluationResultTable, filePath);
    }

    /***
     *
     * @return The MAP of the retrieval model system
     */
    @Override
    public Double meanAveragePrecision() {
        return evaluationResultTable.meanAveragePrecision();
    }

    /***
     *
     * @return The MRR of the retrieval model system
     */
    @Override
    public Double meanRankReciprocal() {
        return evaluationResultTable.meanReciprocalRank();
    }

    /***
     * The precision value at K for the results
     * against query
     * @param query
     * @param K
     * @return  P@K for query
     */
    @Override
    public Double precisionAtK(String query, int K) {
        return evaluationResultTable.precisionAtK(query, K);
    }

    // Main method
    public static void main(String args[]) {
//            QueryDocumentResultsParser parser =
//                    new QueryDocumentResultsParser("aaaa");
//
        // BM25 Evaluation
        EvaluationModel evaluationModel = new EvaluationModelImpl();
        evaluationModel.documentScores("Project_Files\\CACM_RESULTS_BM25");
        evaluationModel.relevanceInformation("Project_Files\\CACM_RELEVANCE_INFORMATION\\cacm.rel");
        evaluationModel.evaluateSystem("Project_Files\\Evaluation_Table_BM25");

    }
}
