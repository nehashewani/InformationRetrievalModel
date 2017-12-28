package com.information.retrieval.text.transformation.indexing;

import com.information.retrieval.fileio.FileUtility;
import com.information.retrieval.text.retrieval.stopping.Stopping;
import com.information.retrieval.text.transformation.pojo.Posting;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public interface InvertedIndex extends Serializable {

    /**
     * Sets the N-Gram
     * @param n
     */
    public void nGram(int n);

    /**
     *
     * @return Immutable Map representing the term along with posting
     */
    public Map<String, Set<Posting>> index();

    /**
     * Creates the index()
     * @param fileLocation The location of the file path which contains
     *                     the list of parsed html files.
     */
    public void createIndex(String fileLocation);

    /**
     * Returns a map mapping DocumentID to the number
     * of tokens it holds.
     * @return A Map
     */
    public Map<String, Integer> documentLengthMapper();

    /***
     * Write this instance in serialized form
     * @param fileLocation The path of the file to be written
     */

    public default void writeThisObjectToFile(String fileLocation)   {
        try {
            FileUtility.CustomObjectWriter customObjectWriter = new FileUtility.CustomObjectWriter(fileLocation + ".txt");
            customObjectWriter.writeObjectToFile(this);
            customObjectWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Map<String, Integer> documentLengthMapperWithStopping(Stopping stopping);

    /**
     * Writes the String returned by toString() method
     * to a file
     * @param fileLocation The path of the file to be written
     */
    public default void writeToFile(String fileLocation)    {
        FileUtility.writeToFile(this, fileLocation);
    }
}
