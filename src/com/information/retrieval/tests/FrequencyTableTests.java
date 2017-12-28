package com.information.retrieval.tests;

import com.information.retrieval.text.transformation.indexing.*;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.information.retrieval.text.transformation.constants.Constants.SPACE;
import static org.junit.Assert.assertTrue;

public class FrequencyTableTests {

    @Test
    public void testTermFrequencyTable() {
        final String FILELOCATION = "tests";
        InvertedIndex invertedIndex = new DocumentFrequencyIndex(1);
        invertedIndex.createIndex(FILELOCATION);
        String expectedTermFrequencyTable = getTermFrequencyTable();
        FrequencyTable ft = new TermFrequencyTable(invertedIndex);
        ft.createFrequencyTable();
        assertTrue(expectedTermFrequencyTable.equals(ft.toString()));
    }

    @Test
    public void testDocumentFrequencyTable() {
        final String FILELOCATION = "tests";
        InvertedIndex invertedIndex = new DocumentFrequencyIndex(1);
        invertedIndex.createIndex(FILELOCATION);
        String expectedTermFrequencyTable = getDocumentFrequencyTable();
        FrequencyTable ft = new DocumentFrequencyTable(invertedIndex);
        ft.createFrequencyTable();
        // TODO : Complete the test
        //assertTrue(expectedTermFrequencyTable.equals(ft.toString()));
    }

    private String getDocumentFrequencyTable()  {
        StringBuilder sb = new StringBuilder();
//        sb.append("This ")

        return sb.toString();
    }

    private String getTermFrequencyTable() {

        Map<String, Long> termFrequencyMapper = new HashMap<>();
        termFrequencyMapper.put("SampleDocument1", 1L);
        termFrequencyMapper.put("SampleDocument2", 1L);
        termFrequencyMapper.put("This", 1L);
        termFrequencyMapper.put("is", 1L);
        termFrequencyMapper.put("a", 1L);
        termFrequencyMapper.put("test", 1L);
        termFrequencyMapper.put("Dont", 1L);
        termFrequencyMapper.put("worry", 1L);
        termFrequencyMapper.put("Go", 1L);
        termFrequencyMapper.put("ahead", 1L);
        termFrequencyMapper.put("and", 2L);
        termFrequencyMapper.put("modify", 1L);
        termFrequencyMapper.put("it", 1L);
        termFrequencyMapper.put("Do", 1L);
        termFrequencyMapper.put("you", 1L);
        termFrequencyMapper.put("think", 1L);
        termFrequencyMapper.put("Java", 1L);
        termFrequencyMapper.put("can", 1L);
        termFrequencyMapper.put("read", 1L);
        termFrequencyMapper.put("understand", 1L);

        StringBuilder sb = new StringBuilder();
        termFrequencyMapper.entrySet()
                .stream()
                .sorted((entry1, entry2) -> {
                    String key1 = entry1.getKey();
                    Long value1 = entry1.getValue();
                    String key2 = entry2.getKey();
                    Long value2 = entry2.getValue();
                    if (value1.compareTo(value2) == 0) {
                        return key1.compareTo(key2);
                    } else return value2.compareTo(value1);
                }).forEach(entry -> sb.append(entry.getKey() + SPACE + entry.getValue() + "\n"));


        return sb.toString();
    }


}
