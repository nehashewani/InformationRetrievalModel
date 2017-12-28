package com.information.retrieval.text.transformation.factory;

import com.information.retrieval.fileio.FileUtility;
import com.information.retrieval.text.transformation.exception.IndexRetrievalFailedException;
import com.information.retrieval.text.transformation.indexing.DocumentFrequencyTable;
import com.information.retrieval.text.transformation.indexing.FrequencyTable;
import com.information.retrieval.text.transformation.indexing.InvertedIndex;
import com.information.retrieval.text.transformation.indexing.TermFrequencyTable;

import java.util.List;

public class FrequencyTableFactory {

    public static FrequencyTable createDocumentFrequencyTable(String fileLocation) {
        InvertedIndex invertedIndex = getInvertedIndexFromFile(fileLocation);
        if (invertedIndex == null)
            throw new IndexRetrievalFailedException("InvertedIndex cannot be retrieved from provided file: "
                    + fileLocation);

        return new DocumentFrequencyTable(invertedIndex);
    }

    public static FrequencyTable createTermFrequencyTable(String fileLocation) {
        InvertedIndex invertedIndex = getInvertedIndexFromFile(fileLocation);
        if (invertedIndex == null)
            throw new IndexRetrievalFailedException("InvertedIndex cannot be retrieved from provided file: "
                    + fileLocation);

        return new TermFrequencyTable(invertedIndex);
    }

    private static InvertedIndex getInvertedIndexFromFile(String fileLocation) {
        long currentTime = System.currentTimeMillis();
        InvertedIndex invertedIndex = null;
        List<Object> objects = FileUtility.readObjectsFromFile(fileLocation);
        for (Object object : objects) {
            if (object instanceof InvertedIndex) {
                invertedIndex = (InvertedIndex) object;
                return invertedIndex;
            }
        }
        return invertedIndex;
    }
}
