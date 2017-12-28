package com.information.retrieval.text.transformation.tokenize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.information.retrieval.text.transformation.constants.Constants.SPACE;

public class Tokenizer1Gram implements Tokenizer {

    @Override
    public List<String> tokenize(String content) {
        String[] tokens = content.split(SPLIT_REGEX );
        final List<String> tokenList = Arrays.asList(tokens);
        return tokenList;
    }
}
