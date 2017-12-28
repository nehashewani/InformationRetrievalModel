package com.information.retrieval.text.retrieval.results;

import com.information.retrieval.fileio.FileUtility;
import com.information.retrieval.text.transformation.pojo.DocumentInfo;
import com.information.retrieval.text.transformation.pojo.QueryInfo;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.information.retrieval.text.transformation.constants.Constants.SPACE;

/***
 * A Parser class for retrieving document results
 * for a list of queries.
 *
 * @author  Neha Shewani
 */
public class QueryDocumentResultsParser {
    // File path where document scores can be retrieved
    String filePath;

    // Maps a Query to its results
    Map<String, Set<DocumentInfo>> queryDocumentInfoMap = new HashMap<>();

    public QueryDocumentResultsParser(String filePath) {
        this.filePath = filePath;
    }

    /***
     *
     * @return Returns a model of Documents results produced
     * by an IR model
     *
     */
    public QueryDocumentResults queryDocumentScores() {
        File file = new File(filePath);

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            Arrays.asList(files)
                    .stream()
                    .forEach(this::parseDocument);
        }

        return new QueryDocumentResults(queryDocumentInfoMap);
    }

    /***
     * Parses the file for document results
     * @param file
     */
    private void parseDocument(File file) {
        try {
            FileUtility.CustomFileReader cfr
                    = new FileUtility.CustomFileReader(
                            file.getAbsolutePath().replace(".txt", ""));

            // Tracks the document rank
            final AtomicInteger i = new AtomicInteger(1);

            cfr.readLinesFromFile().filter(line -> !line.equals("")).forEach(line ->
            {
                String splitLine[] = line.split(SPACE);
                final String QUERY_ID = splitLine[0];
                final String DOCUMENT_ID_WITH_FILE_EXTENSION = splitLine[2];
                final String DOCUMENT_ID = DOCUMENT_ID_WITH_FILE_EXTENSION.substring(0,
                        DOCUMENT_ID_WITH_FILE_EXTENSION.indexOf("."));
//                QueryInfo queryInfo = new QueryInfo(QUERY_ID, QUERY);
                queryDocumentInfoMap.putIfAbsent(QUERY_ID, new HashSet<>());
                //System.out.println(DOCUMENT_ID);
                queryDocumentInfoMap.get(QUERY_ID).add(new DocumentInfo(DOCUMENT_ID, i.getAndIncrement()));
                //});
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
