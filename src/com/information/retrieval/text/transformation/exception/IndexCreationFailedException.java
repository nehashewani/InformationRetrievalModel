package com.information.retrieval.text.transformation.exception;

import com.information.retrieval.text.transformation.indexing.InvertedIndex;

public class IndexCreationFailedException extends RuntimeException{
    public IndexCreationFailedException(InvertedIndex ie) {
        super("Failed to Create inverted index of type " + ie.getClass().getName());
    }
}
