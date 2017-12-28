package com.information.retrieval.text.retrieval.snippet.generation;

import com.information.retrieval.fileio.FileUtility;
import com.information.retrieval.text.retrieval.results.QueryDocumentResults;
import com.information.retrieval.text.retrieval.results.QueryDocumentResultsParser;
import com.information.retrieval.text.transformation.pojo.DocumentInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/***

 */
public class SnippetGeneration {

    private List<String> queryTerms = new LinkedList<>();

    private Map<String, Set<DocumentInfo>> topDocs = new HashMap<>();
    private List<String> queries = new ArrayList<>();
    // File path where document scores are stored
//    private String filePath;

    public SnippetGeneration() {
    }


    public void documentScoresPath(String filePath) {
//        this.filePath = filePath;
        QueryDocumentResults queryDocumentResults = new QueryDocumentResultsParser(filePath).queryDocumentScores();
        topDocs = queryDocumentResults.getQueryDocumentInfoMap();
    }

    public void queryIDPath(String filePath) {

        File fileDir = new File(filePath);

        if (fileDir.isDirectory()) {
            File[] files = fileDir.listFiles();
            Arrays.stream(files).forEach(this::buildQueryListFromFile);
        } else
            buildQueryListFromFile(fileDir);
    }

    private void buildQueryListFromFile(File file) {
        try {
            FileUtility.CustomFileReader customFileReader =
                    new FileUtility.CustomFileReader(file.getAbsolutePath().replace(".txt", ""));


            customFileReader.readLinesFromFile().forEach(query -> queries.add(query));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateSnippet() throws FileNotFoundException, UnsupportedEncodingException {


        File file = new File("Project_Files/SnippetGeneration");
        if (!file.exists()) file.mkdir();

        topDocs.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(queryInfo -> {


            int queryID = Integer.parseInt(queryInfo.getKey());

            String query = queries.get(queryID - 1);

            queryTerms = Arrays.asList(query.split(" +"));

            final PrintWriter writer;
            try {
                writer = new PrintWriter("Project_Files/SnippetGeneration/" + queryID + "_snippetResults.html", "UTF-8");
                writer.println("<html><body><ol>");

                writer.println("<h2>Information Retrieval System</h2><h3>Showing Top 100 results for <i>' " + query + "'</i></h3>");


                List<DocumentInfo> docInfo = new LinkedList<>(queryInfo.getValue());

                docInfo.sort(Comparator.comparing(DocumentInfo::getDocumentRank));

                for (DocumentInfo d : docInfo) {


                    String topDoc = d.getDocumentID();

                    //read the raw html doc

                    File input = new File("Project_Files/CACM_CORPUS/" + topDoc + ".html");
                    Document doc = null;
                    try {
                        doc = Jsoup.parse(input, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    org.jsoup.select.Elements el = doc.select("pre");

                    //get text from doc
                    String docText = el.text();

                    String REGEX1 = ",";
                    String REGEX2 = "\\.";
                    String REGEX3 = "(\\d+\\s+\\d+\\s+\\d)";

                    docText = docText.replaceAll("\r\n\r\n","\n\n")
                            .replaceAll("\r\n"," ")
                            .replaceAll("[a-z]\\.\\s+", "\n\n");

                    List<String> oldLines = Arrays.asList(docText.split("\n\n"));

                    docText = docText.toLowerCase();
//                            .replaceAll(REGEX3, "")
//                            .replaceAll(REGEX2, "")
//                            .replaceAll(REGEX1, "");

                    Map<String, Double> lineScoring = new HashMap<>();

                    // Let there be a map of old strings and new strings
                    Map<String, String> oldLineNewLineMapper =
                            oldLines.stream()
                                    .collect(Collectors.toMap(line -> line, line -> line.toLowerCase()
                                            .replaceAll(REGEX3, "")
                                            .replaceAll(REGEX2, "")
                                            .replaceAll(REGEX1, ""), (line1, line2) -> line1));

                    List<String> lines = Arrays.asList(docText.split("\n\n"));

                    //int noOfSentence = lines.size();
                    int noOfSentence = oldLineNewLineMapper.size();

                    List<String> allWords = new LinkedList<>();

                    for (String line : oldLineNewLineMapper.values()) {
                        List<String> lineWords = Arrays.asList(line.split(" "));
                        allWords.addAll(lineWords);
                    }

                    int lineCount = 0;

                    oldLineNewLineMapper.entrySet().stream()
                            .forEach(entry -> {
                                List<SigWordPos> wordPosList = new LinkedList<>();
                                List<Integer> swPos = new LinkedList<>();

                                int pos = 0;
                                for (String word : entry.getValue().split(" +")) {
                                    SigWordPos sp = new SigWordPos(word, false, pos);
                                    wordPosList.add(sp);
                                    pos++;
                                }

                                for (SigWordPos s : wordPosList) {

                                    boolean val = getSignificance(s.getWord(), noOfSentence, allWords);
                                    s.setSignificance(val);
                                    if (val) {
                                        swPos.add(s.getWordPosition());
                                    }
                                }

                                if (swPos.size() > 0) {

                                    int firstIndex = swPos.get(0);
                                    int lastIndex = swPos.get(swPos.size() - 1);

                                    double denom;
                                    if (lastIndex == firstIndex) denom = 1.0;
                                    else denom = lastIndex - firstIndex;

                                    double num = swPos.size();

                                    double sigFactor = (num * num) / denom;


                                    lineScoring.putIfAbsent(entry.getKey(), sigFactor);

                                } else
                                    lineScoring.put(entry.getKey(), 0.0);
                            });

                    // Harsh code
//                    for (String line : lines) {
//
//                        List<SigWordPos> wordPosList = new LinkedList<>();
//                        List<Integer> swPos = new LinkedList<>();
//
//                        int pos = 0;
//                        for (String word : line.split(" +")) {
//                            SigWordPos sp = new SigWordPos(word, false, pos);
//                            wordPosList.add(sp);
//                            pos++;
//                        }
//
//                        for (SigWordPos s : wordPosList) {
//
//                            boolean val = getSignificance(s.getWord(), noOfSentence, allWords);
//                            s.setSignificance(val);
//                            if (val) {
//                                swPos.add(s.getWordPosition());
//                            }
//                        }
//
//                        if (swPos.size() > 0) {
//
//                            int firstIndex = swPos.get(0);
//                            int lastIndex = swPos.get(swPos.size() - 1);
//
//                            double denom;
//                            if (lastIndex == firstIndex) denom = 1.0;
//                            else denom = lastIndex - firstIndex;
//
//                            double num = swPos.size();
//
//                            double sigFactor = (num * num) / denom;
//
//                            lineScoring.put(oldLines.get(lineCount), sigFactor);
//
//
//                        } else
//                            lineScoring.put(oldLines.get(lineCount), 0.0);
//
//                        lineCount++;
//                    }

                    StringBuilder snippet = new StringBuilder();

                    lineScoring.entrySet()
                            .stream()
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                            .filter( stringDoubleEntry -> !(stringDoubleEntry.getValue() == 0D))
                            .limit(3)
                            .forEach(entry -> snippet.append(entry.getKey()).append(". "));


                    String snip = snippet.toString();

                    for (String term : new HashSet<>(queryTerms)) {

                        snip = snip.replaceAll("(?i)\\b" + term + "\\b", "<b>" + term + "</b>");

                    }

                    writer.println("<li><h3><b><u>" + topDoc + "</u></b></h3></li>");
                    writer.println("<ul><li><p>" + snip + "</p></li></ul>");

                }


                writer.println("</ol></body></html>");

                writer.close();
                writer.flush();

            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }


        });


    }

    private boolean getSignificance(String word, int sd, List<String> wordList) {


        if (!queryTerms.contains(word)) {
            double freqWord = Collections.frequency(wordList, word);

            double significanceFactor;

            if (sd < 25.0) {

                significanceFactor = 7 - 0.1 * (25 - sd);

            } else if (sd <= 40.0) {

                significanceFactor = 7;

            } else {

                significanceFactor = 7 + 0.1 * (sd - 40);

            }


            return (freqWord >= significanceFactor);
        } else return true;

    }

}
