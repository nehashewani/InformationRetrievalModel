package com.information.retrieval.text.transformation.indexing;

import com.information.retrieval.fileio.FileUtility;
import com.information.retrieval.text.transformation.indexing.InvertedIndex;

public interface FrequencyTable {

    /**
     * Returns the inverted index on which
     * table has been built
     */
    public InvertedIndex invertedIndex();

    /**
     * Creates the frequency table using
     * invertedIndex()
     */
    public void createFrequencyTable();

    /**
     * Default implementation for writing toString()
     * to file
     * @param fileLocation
     * @throws Exception
     */
    public default void writeToFile(String fileLocation) throws Exception {
        FileUtility.writeToFile(this, fileLocation);
    }

}
