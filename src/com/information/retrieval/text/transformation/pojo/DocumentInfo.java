package com.information.retrieval.text.transformation.pojo;

public class DocumentInfo {

    final String documentID;
    int documentRank;

    public DocumentInfo(String documentID){
        this.documentID = documentID;
    }

    public DocumentInfo(String documentID, int documentRank)  {
        this.documentID = documentID;
        this.documentRank = documentRank;

    }

    public String getDocumentID()   {
        return documentID;
    }

    public int getDocumentRank(){ return documentRank; }

    @Override
    public boolean equals(Object obj) {
        if ( obj != null && obj instanceof DocumentInfo)    {
           return (((DocumentInfo) obj).documentID.equals(this.documentID));
        }

        return false;
    }

    @Override
    public int hashCode() {
        return documentID.hashCode();
    }
}
