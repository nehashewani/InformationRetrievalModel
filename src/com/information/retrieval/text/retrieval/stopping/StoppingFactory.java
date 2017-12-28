package com.information.retrieval.text.retrieval.stopping;

/***
 * A Factory pattern class for creating Stopping objects
 */
public class StoppingFactory {

    public static Stopping createStopping(String filePath) {
        return new StoppingImpl(filePath);
    }
}
