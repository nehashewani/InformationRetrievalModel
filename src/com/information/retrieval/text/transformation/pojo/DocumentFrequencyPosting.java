package com.information.retrieval.text.transformation.pojo;

import java.util.List;

public class DocumentFrequencyPosting implements Posting {
    private String documentID;
    private Integer frequency;

    public DocumentFrequencyPosting(String documentID, Integer frequency) {
        this.documentID = documentID;
        this.frequency = frequency;
    }

    @Override
    public boolean equals(Object obj) {
        return ( obj instanceof DocumentFrequencyPosting
                && this.documentID == ((DocumentFrequencyPosting)obj).documentID
                && this.frequency == ((DocumentFrequencyPosting)obj).frequency);

    }

    @Override
    public int hashCode() {
        return documentID.hashCode() + frequency.hashCode();
    }

    @Override
    public String documentID() {
        return documentID;
    }

    @Override
    public Integer frequency() {
        return frequency;
    }

    @Override
    public List<Integer> wordPos() {
        return null;
    }
}
