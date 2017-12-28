package com.information.retrieval.text.transformation.pojo;

public class QueryInfo {

    String queryID;
    String queryName;

    public QueryInfo(String queryID, String queryName){
        this.queryID = queryID;
        this.queryName = queryName;
    }

    public String getQueryID(){
        return queryID;
    }

    public String getQueryName(){
        return queryName;
    }

}
