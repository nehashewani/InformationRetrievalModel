package com.information.retrieval.text.transformation.pojo;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static com.information.retrieval.utilities.DocumentUtility.extractDocumentID;

/**
 * A Class to represent in-link and out-link representation of the url(s)
 * collected during crawling process
 */
public class Graph {

    // In-link representation by DocID, Set of strings representating URL pointing towards DocID
    private Map<String, Set<String>> inLinkMap;
    // Out-link representation by DocID, Set of strings representating URL  pointed by DocID.
    private Map<String, Set<String>> outLinkMap;

    public Map<String, Set<String>> getInLinkMap() {
        return inLinkMap;
    }

    public Map<String, Set<String>> getOutLinkMap() {
        return outLinkMap;
    }

    public Graph(Map<String, Set<String>> inLinkMap,
                 Map<String, Set<String>> outLinkMap) {
        this.inLinkMap = inLinkMap;
        this.outLinkMap = outLinkMap;
    }

    // Returns the list of source nodes for the Graph
    public List<String> sourceNodes()   {
        return inLinkMap.entrySet().stream()
                .filter((entry) -> { return (entry.getValue().size() == 0);})
                .map((entry) -> entry.getKey())
                    .collect(Collectors.toList());
    }

    // Returns the list of sink nodes for the Graph
    public List<String> sinkNodes()   {
        return outLinkMap.entrySet().stream()
                .filter((entry) -> { return (entry.getValue().size() == 0);})
                .map((entry) -> entry.getKey())
                .collect(Collectors.toList());
    }

    /**
     * This method creates and returns a string representation of
     * the Top 10 links by in-link count
     * @param limit
     * @return A String
     */

    public String top_in_link_count(int limit)  {
            StringBuilder top_pages = new StringBuilder();
            List<Map.Entry<String, Set<String>>> sorted_top_pages_in_link_count_list =
                    inLinkMap.entrySet()
                    .stream()
                    .sorted((entry1, entry2) ->
                    {
                        if (entry1.getValue().size() < entry2.getValue().size())
                            return 1;
                        else if (entry1.getValue().size() > entry2.getValue().size())
                            return -1;
                        else
                            return 0;
                    }).collect(Collectors.toList());

            for ( int i = 0 ; i < sorted_top_pages_in_link_count_list.size() && i < limit ; i++)
                top_pages.append(i + " "
                        + sorted_top_pages_in_link_count_list.get(i).getKey()
                        + " "
                        + sorted_top_pages_in_link_count_list.get(i).getValue().size()
                        + "\n");

            return top_pages.toString();
    }


    public int getNoOfVertices()    {
        return inLinkMap.size();
    }

    /**
     * This returns the textual in-link representation of the Graph
     * @return String
     */
    public String toInLinkRepresentation() {
        final StringBuilder documentText = new StringBuilder();
        inLinkMap.entrySet()
                .forEach( entry -> {
                    documentText.append(extractDocumentID(entry.getKey() + " "));
                    entry.getValue().forEach( url -> {
                        documentText.append(extractDocumentID(url))
                                .append(" ");
                    });
                    documentText.append("\n");
                });

        return documentText.toString();
    }

    @Override
    public String toString() {
        return toInLinkRepresentation();
    }
}
