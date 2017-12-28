package com.information.retrieval.text.retrieval.stopping;

import java.util.List;

/***
 * An interface for creating containers
 * of a list of stop words
 */
public interface Stopping {
    // The function assumes that the file
    // contains a single stop word per line.
    public List<String> stopList();
}
