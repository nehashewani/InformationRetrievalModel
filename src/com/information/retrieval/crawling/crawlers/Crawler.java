package com.information.retrieval.crawling.crawlers;

import com.information.retrieval.fileio.FileUtility;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.information.retrieval.crawling.constants.CrawlingConstants.*;

/**
 * An abstract class For different implementations of Crawling
 */
public abstract class Crawler {

    // Set to keep track of visited valid/relevant URL
    protected Set<String> visitedValidURL = new HashSet<>();

    // Set to keep track of all the URL(s) to avoid adding the URL to frontier
    protected Set<String> visitedURL = new HashSet<>();

    // Url argument passed for running the test
    protected String url;

    // The keyword for searching relevant hyperlinks
    protected String keyword;

    // For writing objects to file
    protected FileUtility.CustomObjectWriter customObjectWriter;

    protected Crawler(String url, String keyword) {
        this.url = url;
        this.keyword = keyword;
    }

    public abstract void crawl() throws IOException;

    /***
     * Finds whether the url can be choosen for crawling
     * @param url
     * @return boolean value indicating whether the url is valid for crawling
     */
    protected static boolean isValid(String url) {
        return (url.indexOf(HTTPS + COLON_SLASH + BASE_URL + RELATIVE_URL) == 0) &&
                !(url.substring(url.indexOf(BASE_URL)).contains(":")) &&
                !isLinkWithinSamePage(url) &&
                (url.indexOf("https://en.wikipedia.org/wiki/Main_Page") == -1);
    }

    // Finds links with the same page
    protected static boolean isLinkWithinSamePage(String url) {
        return (url.indexOf("#") != -1);
    }

    /***
     * Creates the HTML Connection for the url
     * @param url
     * @return HTTP Response object returned after setting up the HTTP Connection
     *         on the specified url
     * @throws IOException
     */

    protected static Connection.Response getResponse(String url) throws IOException {
        Connection conn = Jsoup.connect(url);
        Connection.Response resp = conn.followRedirects(true).execute();
        return resp;
    }
}
