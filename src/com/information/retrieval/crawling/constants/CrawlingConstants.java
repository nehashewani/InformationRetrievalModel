package com.information.retrieval.crawling.constants;

/**
 * Interface for storing constants used during crawling process
 */
public interface CrawlingConstants {
    String HTTPS = "https";
    String BASE_URL = "en.wikipedia.org";
    String RELATIVE_URL = "/wiki/";
    String COLON_SLASH = "://";
    long POLITENESS_TIME = 10L;
    long URL_LIMIT = 1000;
    long MAX_DEPTH = 6;
}
