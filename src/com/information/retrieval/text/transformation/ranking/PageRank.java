package com.information.retrieval.text.transformation.ranking;

public interface PageRank {

    public void pageRankAlgorithm(String perplexityOutputfileLocation) throws Exception;
    public void setTeleportationFactor(double teleportationFactor);

    /**
     * Calculates the ranks of the Pages using PageRank score
     * @return String containing Document ID in higher pagerank order
     */

    public String ranks();

    public String top(int limit);
}
