package com.information.retrieval.text.retrieval.model.scoring;

import com.information.retrieval.text.retrieval.stopping.Stopping;
import com.information.retrieval.text.transformation.indexing.InvertedIndex;
import com.information.retrieval.text.transformation.pojo.Posting;

import java.util.*;
import java.util.stream.Collectors;

/***
 *
 * Runs BM25 Algorithm for calculating document scores
 
 */
public class BM25Scoring implements DocumentScoringStrategy {


    // Total number of documents in the corpus
    private Double TOTAL_NO_DOCS;
    // Constants for BM25 Algorithm
    private static final Double k1 = 1.2D;
    private static final Double b = 0.75D;
    private static final Double k2 = 100D;

    // Maps documents to its length across corpus
    private Map<String, Integer> documentLengthMapper;

    // Average document length across entire corpus
    private Double averageDocumentLength;

    /***
     * Returns a Map representing document scores for query
     * @param query  The query
     * @param invertedIndex  Inverted index for the corpus
     *
     **/
    @Override
    public Map<String, Double> documentScores(String query,
                                              InvertedIndex invertedIndex) {

        documentLengthMapper = invertedIndex.documentLengthMapper();
        TOTAL_NO_DOCS = Double.valueOf(documentLengthMapper.size());
        averageDocumentLength = documentLengthMapper.entrySet()
                .parallelStream()
                .mapToInt(Map.Entry::getValue)
                .average().getAsDouble();

        String indexTerms[] = query.split("\\s+");

        Map<String, Integer> queryTermFrequencyMapper = new HashMap<>();

        // Calculate the query term frequencies
        Arrays.stream(indexTerms)
                .forEach(indexTerm ->
                {
                    queryTermFrequencyMapper.putIfAbsent(indexTerm, 0);
                    queryTermFrequencyMapper.put(indexTerm, queryTermFrequencyMapper.get(indexTerm) + 1);
                });

        // Stores the score for each document
        Map<String, Double> documentScoreMap = new HashMap<>();

        // For every index term present in the query
        // calculate the document scores
        Arrays.stream(indexTerms)
                .filter(indexTerm ->  invertedIndex.index().containsKey(indexTerm))
                .forEach(indexTerm ->
                {
                    Set<Posting> documentPosting = invertedIndex.index().get(indexTerm);

                    // For every document in the
                    // set of posting, calculate the score
                    // for each document

                    Double logScore = Math.log((TOTAL_NO_DOCS - documentPosting.size() + 0.5)
                            / (documentPosting.size() + 0.5));

                    documentPosting.stream().forEach(posting ->
                    {
                        documentScoreMap.putIfAbsent(posting.documentID(), 0D);

                        // Calculate the term weight for this document.
                        Integer termFrequency = posting.frequency();
                        Integer queryTermFrequency = queryTermFrequencyMapper.get(indexTerm);

                        Double documentScore = logScore *
                                (((k1 + 1) * termFrequency) / (computeK(posting.documentID()) + termFrequency))
                                    * (((k2 + 1) * queryTermFrequency) / (k2 + queryTermFrequency));

                        documentScoreMap.put(posting.documentID(),
                                documentScoreMap.get(posting.documentID()) + documentScore);

                    });

                });


        return  documentScoreMap;
    }

    /***
     * Returns a Map representing document scores for query
     * This method consider stopping for calculating scores
     * @param query  The query
     * @param invertedIndex  Inverted index for the corpus
     * @param stopping A container for holding the list of stop words
     * @return A Mapping for documents with their scores
     */
    @Override
    public Map<String, Double> documentScoresWithStopping(String query, InvertedIndex invertedIndex, Stopping stopping) {

        documentLengthMapper = invertedIndex.documentLengthMapperWithStopping(stopping);
        TOTAL_NO_DOCS = Double.valueOf(documentLengthMapper.size());
        averageDocumentLength = documentLengthMapper.entrySet()
                .parallelStream()
                .mapToInt(Map.Entry::getValue)
                .average().getAsDouble();

        String indexTerms[] = query.split("\\p{Z}+");

        Map<String, Integer> queryTermFrequencyMapper = new HashMap<>();

        // Calculate the query term frequencies
        Arrays.stream(indexTerms)
                .filter(indexTerm -> !stopping.stopList().contains(indexTerm))
                .forEach(indexTerm ->
                {
                    queryTermFrequencyMapper.putIfAbsent(indexTerm, 0);
                    queryTermFrequencyMapper.put(indexTerm, queryTermFrequencyMapper.get(indexTerm) + 1);
                });

        // Stores the score for each document
        Map<String, Double> documentScoreMap = new HashMap<>();

        // For every index term present in the query
        // calculate the document scores
        Arrays.stream(indexTerms)
                .filter(indexTerm -> !stopping.stopList().contains(indexTerm) &&
                        invertedIndex.index().containsKey(indexTerm))
                .forEach(indexTerm ->
                {
                    Set<Posting> documentPosting = invertedIndex.index().get(indexTerm);

                    // For every document in the
                    // set of posting, calculate the score
                    // for each document

                    Double logScore = Math.log((TOTAL_NO_DOCS - documentPosting.size() + 0.5)
                            / (documentPosting.size() + 0.5));

                    documentPosting.stream().forEach(posting ->
                    {
                        documentScoreMap.putIfAbsent(posting.documentID(), 0D);

                        // Calculate the term weight for this document.
                        Integer termFrequency = posting.frequency();
                        Integer queryTermFrequency = queryTermFrequencyMapper.get(indexTerm);

                        Double documentScore = logScore *
                                (((k1 + 1) * termFrequency) / (computeK(posting.documentID()) + termFrequency))
                                * (((k2 + 1) * queryTermFrequency) / (k2 + queryTermFrequency));

                        documentScoreMap.put(posting.documentID(),
                                documentScoreMap.get(posting.documentID()) + documentScore);

                    });

                });


        return  documentScoreMap;


    }

    /***
     * Compute K for this documentID
     * @param documentID
     * @return The value of K for this documentID
     */
    private double computeK(String documentID) {
        // Calculate K for the document
        return k1 * ((1 - b) + b * (documentLengthMapper.get(documentID) / averageDocumentLength));
    }
}
