package com.information.retrieval;

import com.information.retrieval.text.retrieval.LuceneSearch;
import com.information.retrieval.text.retrieval.evaluation.model.EvaluationModel;
import com.information.retrieval.text.retrieval.evaluation.model.EvaluationModelImpl;
import com.information.retrieval.text.retrieval.model.LanguageModel;
import com.information.retrieval.text.retrieval.model.RetrievalModelContext;
import com.information.retrieval.text.retrieval.model.RetrievalModelContextImpl;
import com.information.retrieval.text.retrieval.model.scoring.*;
import com.information.retrieval.text.retrieval.snippet.generation.SnippetGeneration;
import com.information.retrieval.text.retrieval.stopping.Stopping;
import com.information.retrieval.text.retrieval.stopping.StoppingImpl;
import com.information.retrieval.text.transformation.indexing.DocumentFrequencyIndex;
import com.information.retrieval.text.transformation.indexing.InvertedIndex;
import com.information.retrieval.text.transformation.indexing.LuceneIndexer;
import com.information.retrieval.text.transformation.indexing.ProximityIndex;
import com.information.retrieval.text.transformation.parser.CACMStemmedParser;
import com.information.retrieval.text.transformation.parser.HtmlParser;
import com.information.retrieval.text.transformation.parser.Parser;
import com.information.retrieval.text.transformation.querybuilder.QueryBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/***
 * This is the entry point to the application code.
 *
 * Performs parsing, indexing, stopping, query refinement,
 * run retrieval models, and evaluation of the results
 * produced by Lucene
 *
 * @author  Neha Shewani
 */
public class Main {

    // PROJECT FILES OUTPUT ROOT
    private static final String PROJECT_ROOT = "Project_Files";
    // INDEX PATHS
    // NORMAL CORPUS
    private static final String INDEX_DIRECTORY = PROJECT_ROOT + File.separator + "CACM_CORPUS_INDEX";


    // QUERIES PATH
    // NON-STEMMMED, REFINED QUERIES
    private static final String LUCENE_QUERY_DIRECTORY =
            PROJECT_ROOT + File.separator + "CACM_QUERIES_LUCENE";
    private static final String QUERY_DIRECTORY = PROJECT_ROOT + File.separator + "CACM_QUERIES";
    // STEMMED QUERIES
    private static final String STEMMED_QUERY_DIRECTORY = PROJECT_ROOT + File.separator + "CACM_QUERIES_STEMMED";

    // BM25 RESULTS
    private static final String DOCUMENT_SCORING_RESULT_BM25 = PROJECT_ROOT + File.separator + "CACM_RESULTS_BM25";
    private static final String DOCUMENT_SCORING_RESULT_BM_25_STOPPED
            = PROJECT_ROOT + File.separator + "CACM_RESULTS_BM25_STOPPED";
    private static final String DOCUMENT_SCORING_RESULT_BM_25_STEMMED
            = PROJECT_ROOT + File.separator + "CACM_RESULTS_BM25_STEMMED";

    // TFIDF RESULTS
    private static final String DOCUMENT_SCORING_RESULT_TFIDF = PROJECT_ROOT + File.separator + "CACM_RESULTS_TFIDF";
    private static final String DOCUMENT_SCORING_RESULT_TFIDF_STOPPED
            = PROJECT_ROOT + File.separator + "CACM_RESULTS_TFIDF_STOPPED";
    private static final String DOCUMENT_SCORING_RESULT_TFIDF_STEMMED
            = PROJECT_ROOT + File.separator + "CACM_RESULTS_TFIDF_STEMMED";

    // PROXIMITY SEARCH RESULTS
    private static final String DOCUMENT_SCORING_RESULT_PROXIMITY
            = PROJECT_ROOT + File.separator + "CACM_RESULTS_PROXIMITY";

    private static final String DOCUMENT_SCORING_RESULT_PROXIMITY_STOPPED
            = PROJECT_ROOT + File.separator + "CACM_RESULTS_PROXIMITY_STOPPED";


    // LANGUAGE MODEL RESULTS
    private static final String DOCUMENT_SCORING_RESULT_QL_MODEL
            = PROJECT_ROOT + File.separator + "CACM_RESULTS_LANGUAGE_MODEL";

    private static final String DOCUMENT_SCORING_RESULT_LANGUAGE_MODEL_STOPPED
            = PROJECT_ROOT + File.separator + "CACM_RESULTS_LANGUAGE_MODEL_STOPPED";

    private static final String DOCUMENT_SCORING_RESULT_LANGUAGE_MODEL_STEMMED
            = PROJECT_ROOT + File.separator + "CACM_RESULTS_LANGUAGE_MODEL_STEMMED";

    // PSEUDO RELEVANCE RESULTS FOR BM25
    private static final String DOCUMENT_SCORING_RESULT_PSEUDO_REL =
            PROJECT_ROOT + File.separator + "CACM_RESULTS_PSEUDO_REL";

    public static void main(String[] args) throws Exception {


//        runStemmedEvaluation();
//          Run only once
//        QueryBuilder.queryExtract();
//        QueryBuilder.queryExtractTransformed();
//
        //runBM25Evaluation();

        // Query parsing and refinement
        QueryBuilder.queryExtract();
        QueryBuilder.queryExtractTransformed();

//         Creates the lucene index files
        LuceneIndexer luceneIndexer = new LuceneIndexer();
        luceneIndexer.setFilesDirPath("Project_Files\\CACM_CORPUS");
        luceneIndexer.setLuceneIndexDirPath("Project_Files\\Lucene Index CACM");
        luceneIndexer.createIndexWithLucene();

        // Perform search operations for Queries against the
        // indexes created by Lucene
        LuceneSearch luceneSearch = new LuceneSearch();
        luceneSearch.setLuceneIndexDir("Project_Files\\Lucene Index CACM");
        luceneSearch.setQueryFilePath(LUCENE_QUERY_DIRECTORY);
        luceneSearch.performSearch();

        // Parses CACM unstemmed documents
        Parser cacmHtmlParser = new HtmlParser();
        cacmHtmlParser.parseDocument("Project_Files\\CACM_CORPUS");
        cacmHtmlParser.storeParsedText("Project_Files\\CACM_CORPUS_PARSED");

        // Creates the index for CACM CORPUS
        InvertedIndex indexer = new DocumentFrequencyIndex(1);
        // Sets the directory which contains a list of parsed html files
        indexer.createIndex("Project_Files\\CACM_CORPUS_PARSED");
        // Writes the index in the text file specified by the file locations
        indexer.writeThisObjectToFile(INDEX_DIRECTORY + "/" + "CACM_UNIGRAM_INDEX");

        indexer.writeToFile(INDEX_DIRECTORY + "/" + "CACM_UNIGRAM_INDEX_TEXT");

        // Scoring strategies
        DocumentScoringStrategy bm25 = new BM25Scoring();
        DocumentScoringStrategy tfIdfScoring = new TfIdfScoring();
        DocumentScoringStrategy queryLikelihoodStrategy = new QueryLikelihoodScoring();
        DocumentScoringStrategy proximityStrategy = new ProximityScoring();

        // BM25 run
        RetrievalModelContext bm25Model = new RetrievalModelContextImpl();
        bm25Model.indexPath(INDEX_DIRECTORY + File.separator + "CACM_UNIGRAM_INDEX");
        bm25Model.queryPath(QUERY_DIRECTORY);
        bm25Model.outputPath(DOCUMENT_SCORING_RESULT_BM25);
        DocumentScoringStrategy bm25Scoring = new BM25Scoring();

        bm25Model.calculateScores(bm25Scoring);

        // TFIDF Run
        RetrievalModelContext tfIdfModel = new RetrievalModelContextImpl();
        tfIdfModel.indexPath(INDEX_DIRECTORY + File.separator + "CACM_UNIGRAM_INDEX");
        tfIdfModel.queryPath(QUERY_DIRECTORY);
        tfIdfModel.outputPath(DOCUMENT_SCORING_RESULT_TFIDF);
        tfIdfModel.calculateScores(tfIdfScoring);
//
//        // QL model run
        RetrievalModelContext qLModel = new RetrievalModelContextImpl();
        qLModel.indexPath(INDEX_DIRECTORY + File.separator + "CACM_UNIGRAM_INDEX");
        qLModel.queryPath(QUERY_DIRECTORY);
        qLModel.outputPath(DOCUMENT_SCORING_RESULT_QL_MODEL);
        qLModel.calculateScores(queryLikelihoodStrategy);


        // This line of code runs the pseudo relevance feedback run
        // on BM25 Retrieval model run
        Stopping stoppingImpl = new StoppingImpl("Project_Files\\CACM_STOPLIST\\common_words");
//
//        This performs psuedo relevance using BM25 scoring algorithm
        RetrievalModelContext pseudoRelevanceModel = new RetrievalModelContextImpl();
        pseudoRelevanceModel.indexPath(INDEX_DIRECTORY + File.separator + "CACM_UNIGRAM_INDEX");
        pseudoRelevanceModel.queryPath(QUERY_DIRECTORY);
        pseudoRelevanceModel.topKDocument(100);

        pseudoRelevanceModel.setPseudoRelevanceFeedback(true);
        pseudoRelevanceModel.outputPath(DOCUMENT_SCORING_RESULT_PSEUDO_REL);
        pseudoRelevanceModel.calculateScoresWithRelevancy(bm25, stoppingImpl);


//
//        // This runs bm25 on stopped version
        RetrievalModelContext stoppedBM25RetrievalModel = new RetrievalModelContextImpl();
        stoppedBM25RetrievalModel.indexPath(INDEX_DIRECTORY + File.separator + "CACM_UNIGRAM_INDEX");
        stoppedBM25RetrievalModel.queryPath(QUERY_DIRECTORY);
        stoppedBM25RetrievalModel.topKDocument(100);
        stoppedBM25RetrievalModel.outputPath(DOCUMENT_SCORING_RESULT_BM_25_STOPPED);
        stoppedBM25RetrievalModel.calculateScoresWithStopping(bm25, stoppingImpl);
//
//        // This runs tfidf model on stopped version
        RetrievalModelContext stoppedTFIDFRetrievalModel = new RetrievalModelContextImpl();
        stoppedTFIDFRetrievalModel.indexPath(INDEX_DIRECTORY + File.separator + "CACM_UNIGRAM_INDEX");
        stoppedTFIDFRetrievalModel.queryPath(QUERY_DIRECTORY);
        stoppedTFIDFRetrievalModel.topKDocument(100);
        stoppedTFIDFRetrievalModel.outputPath(DOCUMENT_SCORING_RESULT_TFIDF_STOPPED);
        stoppedTFIDFRetrievalModel.calculateScoresWithStopping(tfIdfScoring, stoppingImpl);

        // This runs QL model on stopped version
        RetrievalModelContext stoppedLanguageModelRetrieval = new RetrievalModelContextImpl();
        stoppedLanguageModelRetrieval.indexPath(INDEX_DIRECTORY + File.separator + "CACM_UNIGRAM_INDEX");
        stoppedLanguageModelRetrieval.queryPath(QUERY_DIRECTORY);
        stoppedLanguageModelRetrieval.topKDocument(100);
        stoppedLanguageModelRetrieval.outputPath(DOCUMENT_SCORING_RESULT_LANGUAGE_MODEL_STOPPED);
        stoppedLanguageModelRetrieval.calculateScoresWithStopping(queryLikelihoodStrategy, stoppingImpl);

//        Parses the stemmed version of CACM corpus

//      Parses the stemmed corpus
        Parser cacmStemmedParser = new CACMStemmedParser();
        cacmStemmedParser.parseDocument("Project_Files\\CACM_STEMMED_CORPUS\\cacm_stem");
        cacmStemmedParser.storeParsedText("Project_Files\\CACM_STEMMED_CORPUS_PARSED");

        // Inverted index for stemmed corpus
        InvertedIndex stemmedIndexer = new DocumentFrequencyIndex(1);
        // Sets the directory which contains a list of parsed html files
        stemmedIndexer.createIndex("Project_Files\\CACM_STEMMED_CORPUS_PARSED");
        // Writes the index in the text file specified by the file locations
        stemmedIndexer.writeThisObjectToFile(INDEX_DIRECTORY + "/" + "CACM_STEMMED_UNIGRAM_INDEX");

        stemmedIndexer.writeToFile(INDEX_DIRECTORY + "/" + "CACM_STEMMED_UNIGRAM_INDEX_TEXT");
//
//
//        // This runs BM25 on stemmed version
        RetrievalModelContext bm25StemmedModel = new RetrievalModelContextImpl();
        bm25StemmedModel.indexPath(INDEX_DIRECTORY + File.separator + "CACM_STEMMED_UNIGRAM_INDEX");
        bm25StemmedModel.queryPath(STEMMED_QUERY_DIRECTORY);
        bm25StemmedModel.outputPath(DOCUMENT_SCORING_RESULT_BM_25_STEMMED);
        bm25StemmedModel.calculateScores(bm25);
//
//        // This runs TFIDF on stemmed version
        RetrievalModelContext tfIdfStemmed = new RetrievalModelContextImpl();
        tfIdfStemmed.indexPath(INDEX_DIRECTORY + File.separator + "CACM_STEMMED_UNIGRAM_INDEX");
        tfIdfStemmed.queryPath(STEMMED_QUERY_DIRECTORY);
        tfIdfStemmed.outputPath(DOCUMENT_SCORING_RESULT_TFIDF_STEMMED);
        tfIdfStemmed.calculateScores(tfIdfScoring);
//
//        // This runs Language model on stemmed version
        RetrievalModelContext lmStemmed = new RetrievalModelContextImpl();
        lmStemmed.indexPath(INDEX_DIRECTORY + File.separator + "CACM_UNIGRAM_INDEX");
        lmStemmed.queryPath(QUERY_DIRECTORY);
        lmStemmed.topKDocument(100);
        lmStemmed.outputPath(DOCUMENT_SCORING_RESULT_LANGUAGE_MODEL_STEMMED);
        lmStemmed.calculateScores(queryLikelihoodStrategy);

//        For printing language model results
        LanguageModel languageModelCreator = new LanguageModel();
        languageModelCreator.calcLangModel(stemmedIndexer);

//         Performs the snippet generation on BM25 run
        SnippetGeneration sg = new SnippetGeneration();
        sg.documentScoresPath("Project_Files/CACM_RESULTS_BM25");
        sg.queryIDPath("Project_Files/CACM_QUERIES");
        sg.generateSnippet();


        // 1st Evaluation
//        This runs the evaluation model for lucene search results
        EvaluationModel luceneEvaluationModel = new EvaluationModelImpl();
        luceneEvaluationModel.documentScores("Project_Files\\CACM_LUCENE_RESULTS");
        luceneEvaluationModel.relevanceInformation("Project_Files\\CACM_RELEVANCE_INFORMATION\\cacm.rel");
        luceneEvaluationModel.evaluateSystem("Project_Files\\Evaluation_Table_Lucene");

        // 2nd Evaluation
        // This runs the evaluation model for BM25
        EvaluationModel bm25Evaluation = new EvaluationModelImpl();
        bm25Evaluation.documentScores(DOCUMENT_SCORING_RESULT_BM25);
        bm25Evaluation.relevanceInformation("Project_Files\\CACM_RELEVANCE_INFORMATION\\cacm.rel");
        bm25Evaluation.evaluateSystem("Project_Files\\Evaluation_Table_BM25");

        // 3rd Evaluation
        // This runs the evaluation model for TfIdf
        EvaluationModel tfIdfEvaluation = new EvaluationModelImpl();
        tfIdfEvaluation.documentScores(DOCUMENT_SCORING_RESULT_TFIDF);
        tfIdfEvaluation.relevanceInformation("Project_Files\\CACM_RELEVANCE_INFORMATION\\cacm.rel");
        tfIdfEvaluation.evaluateSystem("Project_Files\\Evaluation_Table_TfIdf");

        // 4th Evaluation
        // This runs the evaluation model for QL
        EvaluationModel queryLikelihoodEvaluation = new EvaluationModelImpl();
        queryLikelihoodEvaluation.documentScores(DOCUMENT_SCORING_RESULT_QL_MODEL);
        queryLikelihoodEvaluation.relevanceInformation("Project_Files\\CACM_RELEVANCE_INFORMATION\\cacm.rel");
        queryLikelihoodEvaluation.evaluateSystem("Project_Files\\Evaluation_Table_QL");

        // 5th Evaluation
        // This runs the evaluation model for BM25 Stopped version
        EvaluationModel bm25StoppedEvaluation = new EvaluationModelImpl();
        bm25StoppedEvaluation.documentScores(DOCUMENT_SCORING_RESULT_BM_25_STOPPED);
        bm25StoppedEvaluation.relevanceInformation("Project_Files\\CACM_RELEVANCE_INFORMATION\\cacm.rel");
        bm25StoppedEvaluation.evaluateSystem("Project_Files\\Evaluation_Table_BM25_Stopped");

        // 6th Evaluation
        // This runs the evaluation model for TfIdf Stopped version
        EvaluationModel tfIdfStoppedEvaluation = new EvaluationModelImpl();
        tfIdfStoppedEvaluation.documentScores(DOCUMENT_SCORING_RESULT_TFIDF_STOPPED);
        tfIdfStoppedEvaluation.relevanceInformation("Project_Files\\CACM_RELEVANCE_INFORMATION\\cacm.rel");
        tfIdfStoppedEvaluation.evaluateSystem("Project_Files\\Evaluation_Table_TfIdf_Stopped");

        // 7th Evaluation
        // This runs the evaluation model for QL Stopped version
        EvaluationModel qLStoppedEvaluation = new EvaluationModelImpl();
        qLStoppedEvaluation.documentScores(DOCUMENT_SCORING_RESULT_LANGUAGE_MODEL_STOPPED);
        qLStoppedEvaluation.relevanceInformation("Project_Files\\CACM_RELEVANCE_INFORMATION\\cacm.rel");
        qLStoppedEvaluation.evaluateSystem("Project_Files\\Evaluation_Table_QL_Stopped");

        // 8th Evaluation
        // This runs the evaluation model for BM25 pseudo relevance
        EvaluationModel pseudoRelevanceEvaluation = new EvaluationModelImpl();
        pseudoRelevanceEvaluation.documentScores(DOCUMENT_SCORING_RESULT_PSEUDO_REL);
        pseudoRelevanceEvaluation.relevanceInformation("Project_Files\\CACM_RELEVANCE_INFORMATION\\cacm.rel");
        pseudoRelevanceEvaluation.evaluateSystem("Project_Files\\Evaluation_Table_Pseudo_Relevance_BM25");


        /***
         *  Extra credits runs
         */

        // First we create an indexer for Proximity Search
        InvertedIndex proximityIndex = new ProximityIndex(1);
        // Sets the directory which contains a list of parsed html files
        proximityIndex.createIndex("Project_Files\\CACM_CORPUS_PARSED");
        // Writes the index in the text file specified by the file locations
        proximityIndex.writeThisObjectToFile(INDEX_DIRECTORY + "/" + "CACM_UNIGRAM_INDEX_PROXIMITY");

        proximityIndex.writeToFile(INDEX_DIRECTORY + "/" + "CACM_UNIGRAM_INDEX_PROXIMITY_TEXT");


        // This runs on Proximity indexer
        RetrievalModelContext proximityRetrievalModel
                = new RetrievalModelContextImpl();

        proximityRetrievalModel.indexPath(INDEX_DIRECTORY + File.separator + "CACM_UNIGRAM_INDEX_PROXIMITY");
        proximityRetrievalModel.queryPath(QUERY_DIRECTORY);
        proximityRetrievalModel.outputPath(DOCUMENT_SCORING_RESULT_PROXIMITY);
        proximityRetrievalModel.calculateScores(proximityStrategy);

         // Evaluates Proximity Indexer
        EvaluationModel proximityModelEvaluation = new EvaluationModelImpl();
        proximityModelEvaluation.documentScores(DOCUMENT_SCORING_RESULT_PROXIMITY);
        proximityModelEvaluation.relevanceInformation("Project_Files\\CACM_RELEVANCE_INFORMATION\\cacm.rel");
        proximityModelEvaluation.evaluateSystem("Project_Files\\Evaluation_Table_Proximity_Model");

        // This runs on Proximity indexer and considers stopping
        RetrievalModelContext proximityStoppedModel
                = new RetrievalModelContextImpl();

        proximityStoppedModel.indexPath(INDEX_DIRECTORY + File.separator + "CACM_UNIGRAM_INDEX_PROXIMITY");
        proximityStoppedModel.queryPath(QUERY_DIRECTORY);
        proximityStoppedModel.outputPath(DOCUMENT_SCORING_RESULT_PROXIMITY_STOPPED);
        proximityStoppedModel.calculateScoresWithStopping(proximityStrategy, stoppingImpl);

        // Evaluates Proximity Indexer with stopping
        EvaluationModel proximityModelStoppingEvaluation = new EvaluationModelImpl();
        proximityModelStoppingEvaluation.documentScores(DOCUMENT_SCORING_RESULT_PROXIMITY_STOPPED);
        proximityModelStoppingEvaluation.relevanceInformation("Project_Files\\CACM_RELEVANCE_INFORMATION\\cacm.rel");
        proximityModelStoppingEvaluation.evaluateSystem("Project_Files\\Evaluation_Table_Proximity_Model");


        // Extra credits
        // First we create an indexer for Proximity Search
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

//        TaskRunner indexCreationTaskRunner = new TaskRunner();
//
//
////        indexCreationTaskRunner.addTask(() ->
////        {
////            InvertedIndex indexer = new DocumentFrequencyIndex(1);
////            // Sets the directory which contains a list of parsed html files
////            indexer.createIndex("Parsed Text");
////            // Writes the index in the text file specified by the file locations
////            indexer.writeThisObjectToFile(INDEX_DIRECTORY + "/" + "Document_Frequency_Index_Unigram");
////
////            indexer.writeToFile(INDEX_DIRECTORY + "/" + "Document_Frequency_Index_Unigram_Text");
////
////        });
////
////        indexCreationTaskRunner.addTask(() ->
////        {
////            InvertedIndex indexer2 = new DocumentFrequencyIndex(2);
////
////            indexer2.createIndex("Parsed Text");
////
////            indexer2.writeThisObjectToFile(INDEX_DIRECTORY + "/" + "Document_Frequency_Index_Bigram");
////
////            indexer2.writeToFile(INDEX_DIRECTORY + "/" + "Document_Frequency_Index_Bigram_Text");
////
////        });
////
////        indexCreationTaskRunner.addTask(() ->
////        {
////            InvertedIndex indexer3 = new DocumentFrequencyIndex(3);
////            indexer3.createIndex("Parsed Text");
////            indexer3.writeThisObjectToFile(INDEX_DIRECTORY + "/" + "Document_Frequency_Index_Trigram");
////            // Writes the count of number of tokens for a Document
////            FileUtility.
////                    writeToFile(indexer3.documentLengthMapper(), INDEX_DIRECTORY + "/" + "Document_Token_Count");
////
////            indexer3.writeToFile(INDEX_DIRECTORY + "/" + "Document_Frequency_Index_Trigram_Text");
////        });
////
////        indexCreationTaskRunner.runTasks();
////
////        indexCreationTaskRunner.clear();
////
////        Thread.sleep(6000);
//        final String TABLE_DIRECTORY = "table";
//        Runnable task4 = () ->
//        {
//            System.out.println("Running Task 4");
//            FrequencyTable frequencyTable = FrequencyTableFactory
//                    .createTermFrequencyTable(INDEX_DIRECTORY + "/" + "Document_Frequency_Index_Unigram");
//
//            frequencyTable.createFrequencyTable();
//            try {
//                frequencyTable.writeToFile(TABLE_DIRECTORY + "/" + "Term_Frequency_Table_Unigram");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        };
//
//        Runnable task5 = () ->
//        {
//            System.out.println("Running Task 5");
//            FrequencyTable frequencyTable2 = FrequencyTableFactory
//                    .createTermFrequencyTable(INDEX_DIRECTORY + "/" + "Document_Frequency_Index_Bigram");
//
//            frequencyTable2.createFrequencyTable();
//            try {
//                frequencyTable2.writeToFile(TABLE_DIRECTORY + "/" + "Term_Frequency_Table_Bigram");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        };
//
//        Runnable task6 = () ->
//        {
//            System.out.println("Running Task 6");
//            FrequencyTable frequencyTable3 = FrequencyTableFactory
//                    .createTermFrequencyTable(INDEX_DIRECTORY + "/" + "Document_Frequency_Index_Trigram");
//
//            frequencyTable3.createFrequencyTable();
//            try {
//                frequencyTable3.writeToFile(TABLE_DIRECTORY + "/" + "Term_Frequency_Table_Trigram");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        };
//
//        Runnable task7 = () ->
//        {
//            FrequencyTable frequencyTable4 = FrequencyTableFactory
//                    .createDocumentFrequencyTable(INDEX_DIRECTORY + "/" + "Document_Frequency_Index_Unigram");
//
//            frequencyTable4.createFrequencyTable();
//            try {
//                frequencyTable4.writeToFile(TABLE_DIRECTORY + "/" + "Document_Frequency_Table_Unigram");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        };
//
//        Runnable task8 = () ->
//        {
//            FrequencyTable frequencyTable5 = FrequencyTableFactory
//                    .createDocumentFrequencyTable(INDEX_DIRECTORY + "/" + "Document_Frequency_Index_Bigram");
//
//            System.out.println("Executing task 8");
//            frequencyTable5.createFrequencyTable();
//            try {
//                frequencyTable5.writeToFile(TABLE_DIRECTORY + "/" + "Document_Frequency_Table_Bigram");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        };
//
//        Runnable task9 = () ->
//        {
//            FrequencyTable frequencyTable6 = FrequencyTableFactory
//                    .createDocumentFrequencyTable(INDEX_DIRECTORY + "/" + "Document_Frequency_Index_Trigram");
//
//            frequencyTable6.createFrequencyTable();
//            try {
//                frequencyTable6.writeToFile(TABLE_DIRECTORY + "/" + "Document_Frequency_Table_Trigram");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        };
//
//        indexCreationTaskRunner.addTask(task4);
//        indexCreationTaskRunner.addTask(task5);
//        indexCreationTaskRunner.addTask(task6);
//
//        indexCreationTaskRunner.runTasks();
//
//        indexCreationTaskRunner.clear();
//
//        indexCreationTaskRunner.addTask(task7);
//        indexCreationTaskRunner.addTask(task8);
//        indexCreationTaskRunner.addTask(task9);
//
//        indexCreationTaskRunner.runTasks();
    }

    private static void runBM25Evaluation() {
        // BM25 run
        RetrievalModelContext bm25Model = new RetrievalModelContextImpl();
        bm25Model.indexPath(INDEX_DIRECTORY + File.separator + "CACM_UNIGRAM_INDEX");
        bm25Model.queryPath(QUERY_DIRECTORY);
        bm25Model.outputPath(DOCUMENT_SCORING_RESULT_BM25);
        DocumentScoringStrategy bm25Scoring = new BM25Scoring();

        bm25Model.calculateScores(bm25Scoring);

        // 2nd Evaluation
        // This runs the evaluation model for BM25
        EvaluationModel bm25Evaluation = new EvaluationModelImpl();
        bm25Evaluation.documentScores(DOCUMENT_SCORING_RESULT_BM25);
        bm25Evaluation.relevanceInformation("Project_Files\\CACM_RELEVANCE_INFORMATION\\cacm.rel");
        bm25Evaluation.evaluateSystem("Project_Files\\Evaluation_Table_BM25");


    }

    public static void runStemmedEvaluation()   {
        EvaluationModel bm25StemmedEvaluation = new EvaluationModelImpl();
        bm25StemmedEvaluation.documentScores(DOCUMENT_SCORING_RESULT_BM_25_STEMMED);
        bm25StemmedEvaluation.relevanceInformation("Project_Files\\CACM_RELEVANCE_INFORMATION\\cacm.rel");
        bm25StemmedEvaluation.evaluateSystem("Project_Files\\Evaluation_Table_BM25_Stemmed");

        EvaluationModel tfIdfStemmedEvaluation = new EvaluationModelImpl();
        tfIdfStemmedEvaluation.documentScores(DOCUMENT_SCORING_RESULT_TFIDF_STEMMED);
        tfIdfStemmedEvaluation.relevanceInformation("Project_Files\\CACM_RELEVANCE_INFORMATION\\cacm.rel");
        tfIdfStemmedEvaluation.evaluateSystem("Project_Files\\Evaluation_Table_TFIdf_Stemmed");

        EvaluationModel qLStemmedEvaluation = new EvaluationModelImpl();
        qLStemmedEvaluation.documentScores(DOCUMENT_SCORING_RESULT_TFIDF_STEMMED);
        qLStemmedEvaluation.relevanceInformation("Project_Files\\CACM_RELEVANCE_INFORMATION\\cacm.rel");
        qLStemmedEvaluation.evaluateSystem("Project_Files\\Evaluation_Table_QL_Stemmed");

    }
    private static class TaskRunner {

        List<Runnable> runnableList = new ArrayList<>();

        public void addTask(Runnable task) {
            runnableList.add(task);
        }

        public void runTasks() {
            ExecutorService es = Executors.newFixedThreadPool(6);
            runnableList.forEach(runnable -> {
                System.out.println("Running Task: " + runnableList);
                es.execute(runnable);
            });

            es.shutdown();
            while (!es.isTerminated()) ;

        }

        public void clear() {
            runnableList.clear();
        }

    }

}
