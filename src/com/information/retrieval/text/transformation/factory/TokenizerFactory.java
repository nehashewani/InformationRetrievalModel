package com.information.retrieval.text.transformation.factory;

import com.information.retrieval.text.transformation.exception.TokenizerCreationFailedException;
import com.information.retrieval.text.transformation.tokenize.Tokenizer;
import com.information.retrieval.text.transformation.tokenize.Tokenizer1Gram;
import com.information.retrieval.text.transformation.tokenize.Tokenizer2Gram;
import com.information.retrieval.text.transformation.tokenize.Tokenizer3Gram;

public class TokenizerFactory {

    private int nGram;
    public TokenizerFactory(int n)  {
        this.nGram = n;
    }

    public Tokenizer createTokenizer()  {
        switch (nGram)  {
            case 1:
                return new Tokenizer1Gram();
            case 2:
                return new Tokenizer2Gram();
            case 3:
                return new Tokenizer3Gram();

            default:
                throw new TokenizerCreationFailedException("Incorrect value for N Gram");
        }
    }
}
