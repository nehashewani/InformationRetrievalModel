package com.information.retrieval.text.retrieval.evaluation.table;

import java.util.*;

/**
 *
 * A Pojo to store the results of
 * evaluating a IR model
 * @author  Neha Shewani
 */
public class EvaluationResultTable {
    // Stores the precision values for every query
    private Map<String, Set<RankValue>> queryPrecisionMap;
    // Stores the recall values for every query
    private Map<String, Set<RankValue>> queryRecallMap;

    public void setQueryPrecisionValue(Map<String, Set<RankValue>> queryPrecisionMap) {
        this.queryPrecisionMap = queryPrecisionMap;
    }

    public void setQueryRecallValue(Map<String, Set<RankValue>> queryRecallMap) {
        this.queryRecallMap = queryRecallMap;
    }

    /**
     *
     * @return MAP for the IR Model
     */
    public Double meanAveragePrecision() {

        Double d = queryPrecisionMap
                .entrySet()
                .stream()
                .mapToDouble(entry -> entry.getValue().stream()
                        .mapToDouble(rankValue -> Double.valueOf(rankValue.valueAtRank())).average().getAsDouble())
                .average().getAsDouble();

        return d;
    }

    /***
     *
     * @return MRR for the IR Model
     */
    public Double meanReciprocalRank() {
        return queryPrecisionMap.entrySet().stream().mapToDouble(entry ->
                entry.getValue().stream()
                        .filter(RankValue::relevant)
                        .findFirst()
                        .orElseGet(() -> {
                            return new RankValue(0, 0D, false);
                        })
                        .valueAtRank()).average().getAsDouble();

    }

    /***
     *
     * @param query
     * @param K
     * @return P@K for the query
     */
    public Double precisionAtK(String query, int K) {
        return queryPrecisionMap.getOrDefault(query, new HashSet<>()).stream()
                .skip(K - 1)
                .mapToDouble(RankValue::valueAtRank)
                .average()
                .getAsDouble();
    }

    /***
     * A textual representation of the EvaluationResultTable
     * @return textual representation of "this"
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        SortedSet<String> keys = new TreeSet<>(Comparator.comparing(Integer::valueOf));
        keys.addAll(queryPrecisionMap.keySet());

        keys.stream().forEach(key -> {

            sb.append("EVALUATION FOR QUERY ID: " + key + "\n");
            sb.append("\n");
            sb.append("PRECISION VALUES: " + "\n");
            sb.append("\n");
            Set<RankValue> precisionValue = queryPrecisionMap.get(key);
            precisionValue.stream().sorted(Comparator.comparing(RankValue::rank))
                    .forEach(rankValue -> sb.append(rankValue.valueAtRank() + " "));

            sb.append("\n");
            sb.append("\n");

            sb.append("RECALL VALUES: " + "\n");
            sb.append("\n");
            Set<RankValue> recallValue = queryRecallMap.get(key);
            recallValue.stream().sorted(Comparator.comparing(RankValue::rank))
                    .forEach(rankValue -> sb.append(rankValue.valueAtRank() + " "));

            sb.append("\n");
            sb.append("\n");

            sb.append("Precision at 5:" + "\n");

            sb.append(precisionAtK(key, 5));

            sb.append("\n");
            sb.append("\n");

            sb.append("Precision at 20:" + "\n");

            sb.append(precisionAtK(key, 20));

            sb.append("\n");
            sb.append("\n");

        });

        sb.append("MAP for the system: " + meanAveragePrecision());
        sb.append("\n");
        sb.append("\n");
        sb.append("MRR for the system: " + meanReciprocalRank());
        sb.append("\n");
        sb.append("\n");

        return sb.toString();
    }

}
