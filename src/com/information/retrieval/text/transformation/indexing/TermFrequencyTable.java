package com.information.retrieval.text.transformation.indexing;

import com.information.retrieval.fileio.FileUtility;

import java.util.List;
import java.util.stream.Collectors;

import static com.information.retrieval.text.transformation.constants.Constants.SPACE;

public class TermFrequencyTable implements FrequencyTable {

    private InvertedIndex invertedIndex;
    private List<FrequencyTableRow> nodeList;

    public TermFrequencyTable(InvertedIndex invertedIndex) {
        this.invertedIndex = invertedIndex;
    }

    @Override
    public InvertedIndex invertedIndex() {
        return this.invertedIndex;
    }

    @Override
    public void createFrequencyTable() {
        nodeList = invertedIndex.index()
                .entrySet()
                .stream()
                .map(stringSetEntry ->
                        new TermFrequencyTableRow(stringSetEntry.getKey(),
                                stringSetEntry.getValue()
                                        .stream()
                                        .mapToLong(posting -> posting.frequency())
                                        .sum()))
                .collect(Collectors.toList());
    }

    /**
     * A textual representation of this table
     * @param fileLocation The path of the file to which it is written
     * @throws Exception
     */
    @Override
    public void writeToFile(String fileLocation) throws Exception {

        final FileUtility.CustomFileWriter fcw = new FileUtility.CustomFileWriter(fileLocation);
        nodeList.stream()
                // Sorted from most frequent to least
                .sorted((node1, node2) -> {
                    String term1 = ((TermFrequencyTableRow) node1).getTerm();
                    Long freq1 = ((TermFrequencyTableRow) node1).getFrequency();
                    String term2 = ((TermFrequencyTableRow) node2).getTerm();
                    Long freq2 = ((TermFrequencyTableRow) node2).getFrequency();
                    if (freq1.compareTo(freq2) == 0) {
                        return term1.compareTo(term2);
                    } else return freq2.compareTo(freq1);
                })
                // Write to file
                .forEach(node ->
                {
                    fcw.writeLineToFile(node.toString() + "\n");
                });

        fcw.close();
    }

    /**
     * A Textual representation of the Frequency table
     * Use it if you have a bigger heap memory
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        nodeList.stream()
                .sorted((node1, node2) -> {
                    String term1 = ((TermFrequencyTableRow) node1).getTerm();
                    Long freq1 = ((TermFrequencyTableRow) node1).getFrequency();
                    String term2 = ((TermFrequencyTableRow) node2).getTerm();
                    Long freq2 = ((TermFrequencyTableRow) node2).getFrequency();
                    if (freq1.compareTo(freq2) == 0) {
                        return term1.compareTo(term2);
                    } else return freq2.compareTo(freq1);
                })
                .forEach(node -> sb.append(((TermFrequencyTableRow) node).getTerm() + SPACE +
                        ((TermFrequencyTableRow) node).getFrequency() + "\n"));

        return sb.toString();
    }

    /**
     * A static class implementation of FrequencyTableRow
     */
    private static class TermFrequencyTableRow implements FrequencyTableRow {
        private String term;
        private Long frequency;

        public String getTerm() {
            return term;
        }

        public Long getFrequency() {
            return frequency;
        }

        public TermFrequencyTableRow(String term, Long frequency) {
            this.term = term;
            this.frequency = frequency;
        }

        @Override
        public String toString() {
            return term + " " + frequency;
        }
    }

}
