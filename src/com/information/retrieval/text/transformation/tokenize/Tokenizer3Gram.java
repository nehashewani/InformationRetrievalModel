package com.information.retrieval.text.transformation.tokenize;

import java.util.ArrayList;
import java.util.List;

import static com.information.retrieval.text.transformation.constants.Constants.SPACE;

public class Tokenizer3Gram implements Tokenizer {
    //Here a token is a N-Gram of length 3

    @Override
    public List<String> tokenize(String content) {
        List<String> tokenList = new ArrayList<>();
        String[] tokens = content.split(SPLIT_REGEX);
        for ( int itr = 0 ; itr < tokens.length - 2; itr++) {
            String word1 = tokens[itr];
            String word2 = tokens[itr + 1];
            String word3 = tokens[itr + 2];
            tokenList.add(word1 + SPACE + word2 + SPACE + word3);
        }
        return tokenList;
    }
}
