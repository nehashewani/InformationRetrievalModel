package com.information.retrieval.text.transformation.tokenize;

import java.util.ArrayList;
import java.util.List;

import static com.information.retrieval.text.transformation.constants.Constants.SPACE;

public class Tokenizer2Gram implements Tokenizer {
    //Here a token is a N-Gram of length 2
    private List<String> tokenList;
    @Override
    public List<String> tokenize(String content) {
        final List<String> tokenList = new ArrayList<>();
        String[] tokens = content.split(SPLIT_REGEX);
        for ( int itr = 0 ; itr < tokens.length - 1; itr++) {
            String word1 = tokens[itr];
            String word2 = tokens[itr + 1];
            tokenList.add(word1 + SPACE + word2);
        }
        return tokenList;
    }
}
