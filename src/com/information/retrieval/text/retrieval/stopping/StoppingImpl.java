package com.information.retrieval.text.retrieval.stopping;

import com.information.retrieval.fileio.FileUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/***
 * A Container for stop list
 *
 
 *
 **/
public class StoppingImpl implements Stopping {
    // Stores the path of the file which contains the stop list words
    String filePath;
    List<String> stopWordList;
    public StoppingImpl(String filePath) {
        this.filePath = filePath;
        stopWordList = new ArrayList<>();
        generateStopList();
    }

    private void generateStopList() {
        try {
            FileUtility.CustomFileReader cfr =
                    new FileUtility.CustomFileReader(filePath);

            String stopWord;
            while ( (stopWord = cfr.readLineFromFile()) != null)    {
                stopWordList.add(stopWord);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public List<String> stopList() {
        return Collections.unmodifiableList(stopWordList);
    }
}
