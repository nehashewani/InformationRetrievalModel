package com.information.retrieval.text.retrieval.model.scoring;

import com.information.retrieval.text.retrieval.stopping.Stopping;
import com.information.retrieval.text.transformation.indexing.InvertedIndex;
import com.information.retrieval.text.transformation.pojo.Posting;
import javafx.geometry.Pos;

import java.util.*;

public class ProximityScoring implements DocumentScoringStrategy {

    int K;
    Map<String, Double> finalScores = new HashMap<>();

    private Map<String, List<List<Integer>>> docWithPos = new HashMap<>();

    @Override
    public Map<String, Double> documentScores(String query, InvertedIndex ie) {


        List<String> queryTerms = Arrays.asList(query.split(" "));
        Map<String, Integer> docLenMap = ie.documentLengthMapper();
        Map<String, Set<Posting>> invInd = ie.index();

        if (queryTerms.size() > 1) {

            //get all word pos for all doc and store in map
            docLenMap.entrySet().stream().forEach(doc -> {

                List<List<Integer>> listOfWordPos = new ArrayList<>();

                queryTerms.stream().filter(invInd::containsKey).forEach(q -> {
                    Set<Posting> docs = new HashSet<>(invInd.get(q));

                    for (Posting p : docs) {

                        if (p.documentID().equals(doc.getKey())) {
                            List<Integer> initWordPos;
                            initWordPos = p.wordPos();
                            listOfWordPos.add(initWordPos);
                        }
                    }
                    docWithPos.put(doc.getKey(), listOfWordPos);
                });
            });

            for (Map.Entry<String, List<List<Integer>>> docPos : docWithPos.entrySet()) {


                double score = 0;

                List<List<Integer>> wordPositions = new ArrayList<>(docPos.getValue());

                for (int j = 0; j < wordPositions.size() - 1; j++) {

                    List<Integer> currList = wordPositions.get(j);
                    List<Integer> nxtList = wordPositions.get(j + 1);
                    for (Integer aCurrList : currList) {

                        int pos = aCurrList;

                        for (Integer nextPos : nxtList) {

                            if (nextPos > pos && nextPos <= pos + 4) {
                                score++;
                                break;
                            }
                        }
                    }
                }

                finalScores.put(docPos.getKey(), score);
            }

        } else {
            //single query term implementation
        }

        return finalScores;
    }

    @Override
    public Map<String, Double> documentScoresWithStopping(String query, InvertedIndex ie, Stopping stopping) {

        List<String> queryTerms = Arrays.asList(query.split(" "));
        Map<String, Integer> docLenMap = ie.documentLengthMapper();
        Map<String, Set<Posting>> invInd = ie.index();

        if (queryTerms.size() > 1) {

            //get all word pos for all doc and store in map
            docLenMap.entrySet().stream().forEach(doc -> {

                List<List<Integer>> listOfWordPos = new ArrayList<>();

                queryTerms.stream()
                        .filter(queryTerm -> invInd.containsKey(queryTerm)
                                && !stopping.stopList().contains(queryTerm))
                        .forEach(q -> {
                            Set<Posting> docs = new HashSet<>(invInd.get(q));

                            for (Posting p : docs) {

                                if (p.documentID().equals(doc.getKey())) {
                                    List<Integer> initWordPos;
                                    initWordPos = p.wordPos();
                                    listOfWordPos.add(initWordPos);
                                }
                            }
                            docWithPos.put(doc.getKey(), listOfWordPos);
                        });
            });

            for (Map.Entry<String, List<List<Integer>>> docPos : docWithPos.entrySet()) {


                double score = 0;

                List<List<Integer>> wordPositions = new ArrayList<>(docPos.getValue());

                for (int j = 0; j < wordPositions.size() - 1; j++) {

                    List<Integer> currList = wordPositions.get(j);
                    List<Integer> nxtList = wordPositions.get(j + 1);
                    for (Integer aCurrList : currList) {

                        int pos = aCurrList;

                        for (Integer nextPos : nxtList) {

                            if (nextPos > pos && nextPos <= pos + 4) {
                                score++;
                                break;
                            }
                        }
                    }
                }

                finalScores.put(docPos.getKey(), score);
            }

        } else {
            //single query term implementation
        }

        return finalScores;
    }

    public void top(int K) {
        this.K = K;
    }
}
