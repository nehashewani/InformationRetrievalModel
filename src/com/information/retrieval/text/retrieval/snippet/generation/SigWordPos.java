package com.information.retrieval.text.retrieval.snippet.generation;

/***

 */
public class SigWordPos {

    String word;
    boolean isSignificant;
    int wordPosition;

    SigWordPos(String word, boolean isSignificant, int wordPosition){

        this.word = word;
        this.isSignificant = isSignificant;
        this.wordPosition = wordPosition;

    }

    public String getWord(){
        return word;
    }

    public boolean getSignificance(){
        return isSignificant;
    }

    public void setSignificance(boolean significance){
        this.isSignificant = significance;
    }

    public int getWordPosition(){
        return wordPosition;
    }

    public void setWordPosition(int wordPosition){
        this.wordPosition = wordPosition;
    }
}
