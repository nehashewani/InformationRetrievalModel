package com.information.retrieval.tests;

import com.information.retrieval.text.transformation.factory.TokenizerFactory;
import com.information.retrieval.text.transformation.tokenize.Tokenizer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TokenizerTest {

    String textContent = "This is a test";

    @Test
    public void test1GramTokenizer()    {
        List<String> tokens = new ArrayList<>();
        tokens.add("This");
        tokens.add("is");
        tokens.add("a");
        tokens.add("test");
        TokenizerFactory tb = new TokenizerFactory(1);
        Tokenizer tk = tb.createTokenizer();
        List<String> actualTokens = tk.tokenize(textContent);
        tokens.removeAll(actualTokens);
        assertTrue(tokens.size() == 0);
    }

    @Test
    public void test2GramTokenizer()    {
        List<String> tokens = new ArrayList<>();
        tokens.add("This is");
        tokens.add("is a");
        tokens.add("a test");
        TokenizerFactory tb = new TokenizerFactory(2);
        Tokenizer tk = tb.createTokenizer();
        List<String> actualTokens = tk.tokenize(textContent);
        tokens.removeAll(actualTokens);
        assertTrue(tokens.size() == 0);
    }

    @Test
    public void test3GramTokenizer()    {
        List<String> tokens = new ArrayList<>();
        tokens.add("This is a");
        tokens.add("is a test");
        TokenizerFactory tb = new TokenizerFactory(3);
        Tokenizer tk = tb.createTokenizer();
        List<String> actualTokens = tk.tokenize(textContent);
        tokens.removeAll(actualTokens);
        assertTrue(tokens.size() == 0);
    }

}
