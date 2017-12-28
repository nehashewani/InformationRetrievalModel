package com.information.retrieval.text.retrieval.model.scoring;

import com.information.retrieval.text.retrieval.stopping.Stopping;
import com.information.retrieval.text.transformation.indexing.InvertedIndex;
import com.information.retrieval.text.transformation.pojo.Posting;

import java.util.*;

/***
 *
 * Runs TfIdf Algorithm for calculating document scores
 * @author Neha Shewani
 */
public class TfIdfScoring implements DocumentScoringStrategy {
    // Stores a map of DocumentId and their lengths
    private Map<String, Integer> documentLengthMapper;

    /***
     * Returns a Map representing document scores for query
     * @param query  The query
     * @param invertedIndex  Inverted index for the corpus
     *
     **/
    @Override
    public Map<String, Double> documentScores(String query, InvertedIndex invertedIndex) {

        String indexTerms[] = query.split("\\p{Z}+");
        Map<String, Set<Posting>> index = invertedIndex.index();
        documentLengthMapper = invertedIndex.documentLengthMapper();
        Map<String, Double> documentScoreMap = new HashMap<>();

        Arrays.stream(indexTerms)
                .filter(indexTerm -> invertedIndex.index().containsKey(indexTerm))
                .forEach(indexTerm ->
                        {
                            int noOfDocuments = documentLengthMapper.size();
                            Double idf = Math.log(noOfDocuments / index.get(indexTerm).size());
                            index.get(indexTerm).forEach(posting -> {
                                String documentId = posting.documentID();
                                Double tf = (double) posting.frequency() / documentLengthMapper.get(documentId);
                                Double documentScore = tf * idf;

                                documentScoreMap.putIfAbsent(documentId, 0D);
                                documentScoreMap.put(documentId, documentScoreMap.get(documentId) + documentScore);

                            });
                        }
                );

        return documentScoreMap;
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
        String indexTerms[] = query.split("\\p{Z}+");
        Map<String, Set<Posting>> ie = invertedIndex.index();
        documentLengthMapper = invertedIndex.documentLengthMapperWithStopping(stopping);
        Map<String, Double> documentScoreMap = new HashMap<>();

        Arrays.stream(indexTerms)
                .filter(indexTerm -> !stopping.stopList().contains(indexTerm) &&
                        invertedIndex.index().containsKey(indexTerm))
                .forEach(indexTerm ->
                        {
                            if (ie.containsKey(indexTerm)) {
                                int noOfDocuments = documentLengthMapper.size();
                                Double idf = Math.log(noOfDocuments / ie.get(indexTerm).size());
                                ie.get(indexTerm).forEach(posting -> {
                                    String documentId = posting.documentID();
                                    Double tf = (double) posting.frequency() / documentLengthMapper.get(documentId);
                                    Double documentScore = tf * idf;
                                    documentScoreMap.putIfAbsent(documentId, 0D);
                                    documentScoreMap.put(documentId, documentScoreMap.get(documentId) + documentScore);
                                });
                            }
                        }
                );

        return documentScoreMap;

    }

}
