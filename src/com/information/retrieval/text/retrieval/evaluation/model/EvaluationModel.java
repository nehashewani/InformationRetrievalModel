package com.information.retrieval.text.retrieval.evaluation.model;

/**
 *
 * An interface for implementation of Evaluation Models
 * @author  Neha Shewani
 */
public interface EvaluationModel {
    /***
     *
     * Creates a document result model for evaluating the
     * retrieval model system
     *
     * @param path  The directory path where result
     * document scores are stored
     * for queries.
     */
    void documentScores(String path);

    /***
     *
     * Creates a document relevancy model
     * for evaluating the retrieval model
     *
     * @param path The path where document relevancy information are stored
     * for queries
     */
    void relevanceInformation(String path);

    /***
     * Runs the evaluation model using document result model
     * and relevancy model
     * @param directoryPath
     */
    void evaluateSystem(String directoryPath);

    /***
     *
     * @return The MAP of the retrieval model system
     */
    Double meanAveragePrecision();

    /***
     *
     * @return  The MRR of the retrieval model system
     */
    Double meanRankReciprocal();

    /***
     * The precision value at K for the results
     * against query
     * @param query
     * @param K
     * @return  P@K for query
     */
    Double precisionAtK(String query, int K);
}
