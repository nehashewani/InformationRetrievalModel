package com.information.retrieval.text.retrieval.model.scoring;

import com.information.retrieval.text.retrieval.stopping.Stopping;
import com.information.retrieval.text.transformation.indexing.InvertedIndex;
import com.information.retrieval.text.transformation.pojo.Posting;
import org.apache.lucene.index.Term;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

/***
 *
 * Runs QueryLikelihood Algorithm for calculating document scores

 */

public class QueryLikelihoodScoring implements DocumentScoringStrategy {

    private Map<String, Integer> docLengthMap;
    private final double lambda = 0.35;

    /***
     * Returns a Map representing document scores for query
     * @param query  The query
     * @param ie     Inverted index for the corpus
     *
     **/
    @Override
    public Map<String, Double> documentScores(String query, InvertedIndex ie) {

        Map<String, Double> finalScores = new HashMap<>();
        List<String> queryTerms = Arrays.asList(query.split(" "));
        Map<String, Set<Posting>> invInd = ie.index();
        docLengthMap = ie.documentLengthMapper();

        double tC = 0;
        for (Integer i : docLengthMap.values()) {
            tC += i;
        }
        double totalSizeOfCorpus = tC;

        queryTerms.forEach(queryTerm -> {


            if (invInd.containsKey(queryTerm)) {


                Set<Posting> postings = invInd.get(queryTerm);

                double tfAcrossCorpus = 0.0;

                for (Posting p : postings) {
                    tfAcrossCorpus += p.frequency();
                }

                //calculate scores for the term for this query term
                for (Posting posting : postings) {

                    String docName = posting.documentID();
                    double tfInDoc = posting.frequency();
                    double totalWordsInDoc = docLengthMap.get(docName);
                    double languageModel = tfInDoc / totalWordsInDoc;

                    //formula
                    double result = Math.log(((1.0 - lambda) * languageModel)
                            + (lambda * (tfAcrossCorpus / totalSizeOfCorpus)));

                    if (result < 0) {
                        result = -result;
                    }


                    if (finalScores.containsKey(docName)) {
                        finalScores.put(docName, finalScores.get(docName) + result);
                    } else {
                        finalScores.put(docName, result);
                    }

                }

            }
        });

        return finalScores;

    }

    /***
     * Returns a Map representing document scores for query
     * This method consider stopping for calculating scores
     * @param query  The query
     * @param ie  Inverted index for the corpus
     * @param stopping A container for holding the list of stop words
     * @return A Mapping for documents with their scores
     */
    @Override
    public Map<String, Double> documentScoresWithStopping(String query
            , InvertedIndex ie
            , Stopping stopping) {
        //estimate language model for each document

        Map<String, Double> finalScores = new HashMap<>();
        List<String> queryTerms = Arrays.asList(query.split(" "));
        Map<String, Set<Posting>> invInd = ie.index();
        docLengthMap = ie.documentLengthMapperWithStopping(stopping);

        double tC = 0;
        for (Integer i : docLengthMap.values()) {
            tC += i;
        }
        double totalSizeOfCorpus = tC;

        queryTerms
                .stream()
                .filter(queryTerm -> !stopping.stopList().contains(queryTerm))
                .forEach(queryTerm -> {

            if (invInd.containsKey(queryTerm)) {

                Set<Posting> postings = invInd.get(queryTerm);

                double tfAcrossCorpus = 0.0;

                for (Posting p : postings) {
                    tfAcrossCorpus += p.frequency();
                }

                //calculate scores for the term for this query term
                for (Posting posting : postings) {

                    String docName = posting.documentID();

                    double tfInDoc = posting.frequency();
                    double totalWordsInDoc = docLengthMap.get(docName);

                    double languageModel = tfInDoc / totalWordsInDoc;

                    //formula
                    double result = Math.log(((1.0 - lambda) * languageModel)
                            + (lambda * (tfAcrossCorpus / totalSizeOfCorpus)));

                    if (result < 0) {
                        result = -result;
                    }


                    if (finalScores.containsKey(docName)) {
                        finalScores.put(docName, finalScores.get(docName) + result);
                    } else {
                        finalScores.put(docName, result);
                    }

                }

            }
        });

        return finalScores;
    }

}

