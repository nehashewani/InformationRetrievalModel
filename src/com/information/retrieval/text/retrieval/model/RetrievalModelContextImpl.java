package com.information.retrieval.text.retrieval.model;

import com.information.retrieval.fileio.FileUtility;
import com.information.retrieval.text.retrieval.evaluation.model.EvaluationModel;
import com.information.retrieval.text.retrieval.evaluation.model.EvaluationModelImpl;
import com.information.retrieval.text.retrieval.model.scoring.DocumentScoringStrategy;
import com.information.retrieval.text.retrieval.model.scoring.ProximityScoring;
import com.information.retrieval.text.retrieval.model.scoring.TfIdfScoring;
import com.information.retrieval.text.retrieval.feedback.RelevanceFeedbackWithStopping;
import com.information.retrieval.text.retrieval.stopping.Stopping;
import com.information.retrieval.text.transformation.indexing.InvertedIndex;
import com.information.retrieval.text.transformation.indexing.ProximityIndex;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.information.retrieval.text.transformation
        .constants.Constants.NEWLINE;
import static com.information.retrieval.text.transformation.constants.Constants.SPACE;

/***
 * An Implementation of Retrieval Model
 * This scoring strategy used by the model depends
 * upon DocumentScoringStrategy.
 *
 *
 */

public class RetrievalModelContextImpl implements RetrievalModelContext {

    private Boolean isPseudoRelevanceRequired = false;

    private static final String SYSTEM_NAME = "SIBENDU_SYSTEM";
    private static final String Q0 = "Q0";
    // Stores the list of queries
    // obtained from text file
    private List<String> queries = new ArrayList<>();

    // Reference to inverted index
    private InvertedIndex invertedIndex;

    // Output path for Document scores
    private String outputPath;
    // Default value unless initialized
    private int K = 100;

    @Override
    public void setPseudoRelevanceFeedback(Boolean isPseudoRelevanceRequired) {
        this.isPseudoRelevanceRequired = isPseudoRelevanceRequired;
    }


    @Override
    public void topKDocument(int K) {
        this.K = K;
    }

    /***
     * Sets the path where query/queries file can be found
     * and builds a list of queries
     * @param queryPath
     */
    public void queryPath(String queryPath) {
        buildQueryList(queryPath);
    }

    /***
     * Builds a list of queries from the files stored in queryPath
     * @param queryPath
     */
    private void buildQueryList(String queryPath) {
        File fileDir = new File(queryPath);

        if (fileDir.isDirectory()) {
            File[] files = fileDir.listFiles();
            Arrays.stream(files).forEach(file -> buildQueryListFromFile(file));
        } else
            buildQueryListFromFile(fileDir);
    }

    /***
     * Builds a list of queries from the file
     * @param file
     */
    private void buildQueryListFromFile(File file) {
        try {
            FileUtility.CustomFileReader customFileReader =
                    new FileUtility.CustomFileReader(file.getAbsolutePath().replace(".txt", ""));

            customFileReader.readLinesFromFile().forEach(query -> queries.add(query));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the path where serialized object of Inverted Index
     * can be found
     *
     * @param indexPath
     */
    public void indexPath(String indexPath) {
        fetchInvertedIndex(indexPath);
    }

    /***
     * Fetches the inverted index from the filePath
     * @param indexPath
     */
    private void fetchInvertedIndex(String indexPath) {
        List<Object> objects = FileUtility.readObjectsFromFile(indexPath);
        // Fetch one inverted index. Multiple Inverted indexes are
        // not expected to be present in the same file.

        invertedIndex = (InvertedIndex) objects.get(0);
    }

    /**
     * Calculate the document scores based on DocumentScoringStrategy
     *
     * @param strategy Scoring depends on the Algorithm used
     */
    @Override
    public void calculateScores(DocumentScoringStrategy strategy) {
        File fileDir = new File(outputPath);
        if (!fileDir.exists()) fileDir.mkdir();

        final AtomicInteger QUERY_ID = new AtomicInteger(1);
        queries.stream().forEach(query ->
        {
            // Scoring depends on the strategy.
            Map<String, Double> documentScoreMap = strategy.documentScores(query, invertedIndex);
            writeToFile(documentScoreMap, QUERY_ID);
        });

    }

    /***
     * Calculate the document scores based on DocumentScoringStrategy
     * Consider relevancy information.
     * An implementation of RelevanceFeedbackWithStopping
     * is required for calculating the list
     * of top weighted terms for initially calculated top K documents
     *
     * @param strategy Scoring depends on the Algorithm used
     * @param stopping An container for stop words
     ***/

    @Override
    public void calculateScoresWithRelevancy(DocumentScoringStrategy strategy, Stopping stopping) {
        File fileDir = new File(outputPath);
        if (!fileDir.exists()) fileDir.mkdir();

        final AtomicInteger QUERY_ID = new AtomicInteger(1);
        queries.stream().forEach(query ->
        {
            // Scoring depends on the strategy.
            Map<String, Double> documentScoreMap = strategy.documentScores(query, invertedIndex);

            if (isPseudoRelevanceRequired) {
                RelevanceFeedbackWithStopping relevanceFeedBack = getPseudoFeedBackWithStopping();
                List<String> frequentTerms
                        = relevanceFeedBack.expandedQuery(query, documentScoreMap, invertedIndex, stopping);

                final StringBuilder expandedQuery = new StringBuilder();

                frequentTerms.stream().forEach(term -> {
                    if (!query.contains(term)) {
                        expandedQuery.append(term + SPACE);
                    }
                });

                expandedQuery.append(query);

                documentScoreMap = strategy.documentScores(expandedQuery.toString(), invertedIndex);
            }

            writeToFile(documentScoreMap, QUERY_ID);
        });

    }

    /***
     * Calculate the document scores based on DocumentScoringStrategy
     * Stopwords are considered in this implementation
     *
     * @param strategy Scoring depends on the Algorithm used
     * @param stopping An container for stop words     *
     *
     ***/
    @Override
    public void calculateScoresWithStopping(DocumentScoringStrategy strategy, Stopping stopping) {

        final AtomicInteger QUERY_ID = new AtomicInteger(1);

        queries.stream().forEach(query ->
        {
            // Scoring depends on the strategy.
            Map<String, Double> documentScoreMap =
                    strategy.documentScoresWithStopping(query, invertedIndex, stopping);

            writeToFile(documentScoreMap, QUERY_ID);
        });
    }


    /***
     *
     * Takes of performing pseudo relevance feedback using Rocchio Algorithm
     * @return Implementation of RelevanceFeedbackWithStopping
     *
     */
    private RelevanceFeedbackWithStopping getPseudoFeedBackWithStopping() {
        return (query, documentScoreMap, invertedIndex, stopping) -> {

            final int K = 10;
            // Set of relevant docs as per pseudo relevance logic
            Set<String> relevantDocs =
                    documentScoreMap.entrySet()
                            .stream()
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                            .limit(K)
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toCollection(LinkedHashSet::new));

            // Set of nonrelevant docs as per pseudo relevance logic
            Set<String> nonRelevantDocs =
                    documentScoreMap.entrySet()
                            .stream()
                            .sorted(Map.Entry.comparingByValue())
                            .limit(documentScoreMap.size() - K)
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toCollection(LinkedHashSet::new));


            String queryTerms[] = query.split("\\s+");

            Map<String, Integer> queryTermFrequency = new HashMap<>();
            Arrays.stream(queryTerms).forEach(queryTerm -> queryTermFrequency
                    .put(queryTerm, Collections.frequency(Arrays.asList(queryTerms), queryTerm)));

            Set<String> wordsInRelevantDocs = invertedIndex.index()
                    .entrySet()
                    .stream()
                    .filter(entry -> !stopping.stopList().contains(entry.getKey()))
                    .collect(HashSet<String>::new,
                            (container, termPostingMap) -> {
                                termPostingMap.getValue().forEach((posting) ->
                                {
                                    if (relevantDocs.contains(posting.documentID())) {
                                        container.add(termPostingMap.getKey());

                                    }
                                });
                            }, (container1, container2) -> container1.addAll(container2));


            Map<String, Double> wordsQueryWeightMap =
                    wordsInRelevantDocs.stream()
                            .collect(Collectors.toMap(term -> term, term -> tfIdfWeighting(queryTermFrequency,
                                    relevantDocs, nonRelevantDocs, term, invertedIndex)));

            List<String> topWeightedTerms
                    = wordsQueryWeightMap.entrySet()
                    .stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .limit(10)
                    .collect(ArrayList::new,
                            (al, stringIntegerEntry) -> al.add(stringIntegerEntry.getKey()),
                            (al1, al2) -> al1.addAll(al2));

            return topWeightedTerms;
        };
    }

    /***
     * Performs the tfIdf query term weighting used
     * for query expansion
     * @param queryTermFrequency
     * @param relevantDocs
     * @param nonRelevantDocs
     * @param term
     * @param invertedIndex
     * @return
     */
    private Double tfIdfWeighting(Map<String, Integer> queryTermFrequency,
                                  Set<String> relevantDocs,
                                  Set<String> nonRelevantDocs,
                                  String term,
                                  InvertedIndex invertedIndex) {
        final int alpha = 8;
        final int beta = 16;
        final int gamma = 4;

        final Map<String, Integer> documentLengthMapper = invertedIndex.documentLengthMapper();
        double idfForTerm = invertedIndex.index().get(term).size() / documentLengthMapper.size();
        int initialQueryValue = alpha * queryTermFrequency.getOrDefault(term, 0);

        Double relevantScoreValue = beta * (invertedIndex.index()
                .get(term).stream().filter(posting -> relevantDocs.contains(posting.documentID()))
                .mapToDouble(posting -> posting.frequency() * idfForTerm)
                .reduce(0, (left, right) -> left + right)) / 10;

        Double nonRelevantScoreValue = gamma * (invertedIndex.index()
                .get(term).stream().filter(posting -> nonRelevantDocs.contains(posting.documentID()))
                .mapToDouble(posting -> posting.frequency() * idfForTerm)
                .reduce(0, (left, right) -> left + right)) / 90;

        return initialQueryValue + relevantScoreValue - nonRelevantScoreValue;

    }

    /***
     * The output path where socres are to be stored
     * @param outputPath
     */
    @Override
    public void outputPath(String outputPath) {
        this.outputPath = outputPath;
        File file = new File(outputPath);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    /***
     * Writes the document scores to file for the query
     **/
    private void writeToFile(Map<String, Double> documentScoreMap,
                             AtomicInteger QUERY_ID) {
        try {
            FileUtility.CustomFileWriter cfr = new FileUtility.CustomFileWriter(
                    outputPath + File.separator + QUERY_ID.get());

            final AtomicInteger rank = new AtomicInteger(1);

            documentScoreMap.entrySet()
                    .stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .limit(K)
                    .forEach(entry ->
                    {
                        //final String QUERY_ID =  String.valueOf(rank.get());
                        cfr.writeLineToFile(QUERY_ID.get() + SPACE + Q0 + SPACE);
                        cfr.writeLineToFile(entry.getKey() + SPACE + rank.getAndIncrement() + SPACE
                                + entry.getValue() + SPACE + SYSTEM_NAME);
                        cfr.writeLineToFile(NEWLINE);
                    });

            QUERY_ID.incrementAndGet();
            cfr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Main method
    public static void main(String args[]) {


        final String PROJECT_ROOT = "Project_Files";
        final String INDEX_DIRECTORY = PROJECT_ROOT + File.separator + "CACM_CORPUS_INDEX";
        final String QUERY_DIRECTORY = PROJECT_ROOT + File.separator + "CACM_QUERIES";
        final String DOCUMENT_SCORING_RESULT_PROXIMITY
                = PROJECT_ROOT + File.separator + "CACM_RESULTS_PROXIMITY";

        DocumentScoringStrategy proximityStrategy = new ProximityScoring();
//        // First we create an indexer for Proximity Search
//        InvertedIndex indexer = new ProximityIndex(1);
//        // Sets the directory which contains a list of parsed html files
//        indexer.createIndex("Project_Files\\CACM_CORPUS_PARSED");
//        // Writes the index in the text file specified by the file locations
//        indexer.writeThisObjectToFile(INDEX_DIRECTORY + "/" + "CACM_UNIGRAM_INDEX_PROXIMITY");
//
//        indexer.writeToFile(INDEX_DIRECTORY + "/" + "CACM_UNIGRAM_INDEX_PROXIMITY_TEXT");


        // This runs on Proximity
//        RetrievalModelContext proximityRetrievalModel
//                = new RetrievalModelContextImpl();
//
//        proximityRetrievalModel.indexPath(INDEX_DIRECTORY + File.separator + "CACM_UNIGRAM_INDEX_PROXIMITY");
//        proximityRetrievalModel.queryPath(QUERY_DIRECTORY);
//        proximityRetrievalModel.outputPath(DOCUMENT_SCORING_RESULT_PROXIMITY);
//        proximityRetrievalModel.calculateScores(proximityStrategy);

        EvaluationModel bm25Evaluation = new EvaluationModelImpl();
        bm25Evaluation.documentScores(DOCUMENT_SCORING_RESULT_PROXIMITY);
        bm25Evaluation.relevanceInformation("Project_Files\\CACM_RELEVANCE_INFORMATION\\cacm.rel");
        bm25Evaluation.evaluateSystem("Project_Files\\Evaluation_Table_Proximity");


    }
}
