package com.information.retrieval.text.transformation.tokenize;

import java.util.List;

public interface Tokenizer {

    String SPLIT_REGEX = "\\s+";
    public List<String> tokenize(String content);

}
