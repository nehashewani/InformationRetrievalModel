package com.information.retrieval.text.transformation.pojo;

import java.util.List;

public class ProximityInfo implements Posting {

    String docName;
    List<Integer> wordPos;
    int tF;

    public ProximityInfo(String docName, int tF, List<Integer> wordPos){

        this.docName = docName;
        this.wordPos = wordPos;
        this.tF = tF;

    }

    @Override
    public String documentID() {
        return docName;
    }

    @Override
    public Integer frequency() {
        return tF;
    }

    public List<Integer> wordPos(){
        return wordPos;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProximityInfo){

            ProximityInfo p = (ProximityInfo) obj;

            return p.documentID() == this.documentID();

        }
        else return false;
    }

    @Override
    public int hashCode() {
        return documentID().hashCode();
    }
}
