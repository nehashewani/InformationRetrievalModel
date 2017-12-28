package com.information.retrieval.text.retrieval.model;

import com.information.retrieval.text.transformation.indexing.InvertedIndex;
import com.information.retrieval.text.transformation.pojo.Posting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/***
 * A Pojo for writing the language models of the
 * documents across corpus to files

 *
 */
public class LanguageModel {

    private Map<String, Integer> docLengthMap;
    final String filePath = "Project_Files\\LanguageModels";

    /***
     * Calculates the language model for
     * every documents across the corpus
     * @param ie
     */
    public void calcLangModel(InvertedIndex ie){
        File file = new File(filePath);
        if ( !file.exists()) file.mkdir();

        Map<String, Set<TermProbability>> langModel = new HashMap<>();

        Map<String, Set<Posting>> invIndex = new HashMap<>(ie.index());
        docLengthMap = ie.documentLengthMapper();

        invIndex.entrySet().stream().forEach( invList -> {

            String queryTerm = invList.getKey();

            Set<Posting> docList = invList.getValue();

            for(Posting p : docList){

                String docID = p.documentID();
                String newDocID = docID.substring(0,docID.indexOf("."));
                int termFreq = p.frequency();
                double docLength = docLengthMap.get(docID);
                double termProbability = termFreq/docLength;
                langModel.putIfAbsent(newDocID, new HashSet<>());
                langModel.get(newDocID).add(new TermProbability(queryTerm, termProbability));

            }
        });

        writeLangModel(langModel, filePath);



    }

    /***
     * Writes the language model to file storage
     * @param langModel
     * @param filePath
     */
    private void writeLangModel(Map<String, Set<TermProbability>> langModel, String filePath) {


        langModel.entrySet().stream().forEach( doc -> {

            try {

                PrintWriter writer = new PrintWriter(filePath+"/"+doc.getKey()+".txt", "UTF-8");

                Set<TermProbability> termProbSet = new HashSet<>(doc.getValue());

                termProbSet.stream().forEach( tP -> {
                    writer.println(tP.termName+"  ->  "+tP.termProbability);
                });

                writer.close();
                writer.flush();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });



    }

    /***
     * A Pojo for storing the probability of a term
     * of occurring in a document
     */
    private static class TermProbability {

        String termName;
        double termProbability;

        TermProbability(String termName, Double termProbability){
            this.termName = termName;
            this.termProbability = termProbability;
        }

        public String getTermName(){
            return termName;
        }

        public Double getTermProbability(){
            return termProbability;
        }


    }

}
