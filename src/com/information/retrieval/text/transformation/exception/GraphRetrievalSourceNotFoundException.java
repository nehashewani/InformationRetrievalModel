package com.information.retrieval.text.transformation.exception;

import java.io.FileNotFoundException;

public class GraphRetrievalSourceNotFoundException extends Exception {
    public GraphRetrievalSourceNotFoundException() {
        super("Source file not set for retrieving graph.Exiting!!!");
    }
}
