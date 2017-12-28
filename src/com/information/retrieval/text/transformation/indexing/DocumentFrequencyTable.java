package com.information.retrieval.text.transformation.indexing;

import com.information.retrieval.fileio.FileUtility;
import com.information.retrieval.text.transformation.pojo.Posting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.information.retrieval.text.transformation.constants.Constants.NEWLINE;
import static com.information.retrieval.text.transformation.constants.Constants.SPACE;

/***
 * An implementation of Frequency Table
 */
public class DocumentFrequencyTable implements FrequencyTable {

    // Stores the inverted index
    private InvertedIndex invertedIndex;
    // Stores the rows of tables
    private List<FrequencyTableRow> nodeList;

    public DocumentFrequencyTable(InvertedIndex invertedIndex) {
        this.invertedIndex = invertedIndex;
        nodeList = new ArrayList<>();
    }

    @Override
    public InvertedIndex invertedIndex() {
        return invertedIndex;
    }

    @Override
    public void createFrequencyTable() {
        nodeList = invertedIndex.index().entrySet()
                .stream()
                .map(entry -> new DocumentFrequencyTableRow(entry.getKey(), entry.getValue().stream()
                        .map(Posting::documentID)
                        .collect(Collectors.toList()),
                        entry.getValue().size()))
                .collect(Collectors.toList());
    }

    /**
     * An efficient way of writing the textual representation
     * of this table to a file
     *
     * @param fileLocation The file to which it is written
     * @throws Exception
     */
    @Override
    public void writeToFile(String fileLocation) throws Exception {

        FileUtility.CustomFileWriter customFileWriter = new FileUtility.CustomFileWriter(fileLocation);
        // Sort lexicographically based on term
        nodeList.stream()
                .sorted((node1, node2) ->
                {
                    String term1 = ((DocumentFrequencyTableRow) node1).getTerm();
                    String term2 = ((DocumentFrequencyTableRow) node2).getTerm();
                    return term1.compareTo(term2);
                })
                // Write to file
                .forEach((sortedNode) ->
                {
                    customFileWriter.writeLineToFile(sortedNode.toString() + NEWLINE);
                });

        customFileWriter.close();
    }

    /**
     * A Textual representation of the Frequency table
     * Use this, if the table is smaller in size
     * or increase the heap size
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        nodeList.stream()
                .sorted((node1, node2) ->
                {
                    String term1 = ((DocumentFrequencyTableRow) node1).getTerm();
                    String term2 = ((DocumentFrequencyTableRow) node2).getTerm();
                    return term1.compareTo(term2);
                })
                .forEach((sortedNode) ->
                {
                    sb.append(((DocumentFrequencyTableRow) sortedNode).getTerm())
                            .append(SPACE)
                            .append(((DocumentFrequencyTableRow) sortedNode).getDocumentIDs())
                            .append(SPACE)
                            .append(((DocumentFrequencyTableRow) sortedNode).getFrequency())
                            .append("\n");
                });

        return sb.toString();
    }


    private static class DocumentFrequencyTableRow implements FrequencyTableRow {
        private String term;
        private List<String> documentIDs;
        private long frequency;

        public String getTerm() {
            return term;
        }

        public List<String> getDocumentIDs() {
            return documentIDs;
        }

        public long getFrequency() {
            return frequency;
        }

        public DocumentFrequencyTableRow(String term, List<String> documentIDs, long frequency) {
            this.term = term;
            this.documentIDs = documentIDs;
            this.frequency = frequency;
        }

        @Override
        public String toString() {
            return term + SPACE + documentIDs + SPACE + frequency;
        }
    }
}
