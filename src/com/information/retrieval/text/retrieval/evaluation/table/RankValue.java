package com.information.retrieval.text.retrieval.evaluation.table;


/**
 * A Pojo to store the precision/recall value
 * at every rank of a query result
 *
 * @author  Neha Shewani
 */
public class RankValue {
    // Stores the rank of the query result
    private Integer rank;
    // Stores the recall/precision value at this rank
    private Double valueAtRank;
    // Stores the relevancy information at this rank
    private Boolean relevant;

    public Boolean relevant() {
        return relevant;
    }

    public Integer rank() {
        return rank;
    }

    public Double valueAtRank() {
        return valueAtRank;
    }

    public RankValue(Integer rank, Double valueAtRank, Boolean relevant) {
        this.rank = rank;
        this.valueAtRank = valueAtRank;
        this.relevant = relevant;
    }
}
