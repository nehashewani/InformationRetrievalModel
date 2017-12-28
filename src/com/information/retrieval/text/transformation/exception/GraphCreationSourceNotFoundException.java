package com.information.retrieval.text.transformation.exception;

public class GraphCreationSourceNotFoundException extends Exception {
    public GraphCreationSourceNotFoundException(String message) {
        super("Source file location not set for creating graph!!!");
    }
}
