package com.information.retrieval.text.transformation.pojo;

import java.io.Serializable;
import java.util.List;

public interface Posting extends Serializable{
    public String documentID();
    public Integer frequency();
    public List<Integer> wordPos();
}
