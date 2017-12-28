package com.information.retrieval.crawling.crawlers;

import com.information.retrieval.fileio.FileUtility;
import com.information.retrieval.text.transformation.pojo.HtmlPage;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

import static com.information.retrieval.crawling.constants.CrawlingConstants.MAX_DEPTH;
import static com.information.retrieval.crawling.constants.CrawlingConstants.POLITENESS_TIME;
import static com.information.retrieval.crawling.constants.CrawlingConstants.URL_LIMIT;
import static com.information.retrieval.fileio.FileUtility.appendToFile;

public class DFSCrawler extends Crawler {

    public DFSCrawler(String url, String keyword) throws IOException {
        super(url, keyword);
        customObjectWriter = new FileUtility.CustomObjectWriter("Html_pages_dfs.txt");
    }

    @Override
    public void crawl() throws IOException {
        dfsCrawl(url, 1);
        customObjectWriter.flush();
    }

    private void dfsCrawl(String url, long depth) {
        if (visitedValidURL.size() >= URL_LIMIT || depth > MAX_DEPTH) {
            return;
        }

        Set<String> hyperLinks = downloadAndParseDocument(url);
        hyperLinks.forEach((hyperlink) -> {
            if (!visitedURL.contains(hyperlink))
                dfsCrawl(hyperlink, depth + 1);
        });
    }

    private Set<String> downloadAndParseDocument(String url) {
        final Set<String> hyperlinks = new LinkedHashSet<>();

        visitedURL.add(url);
        if (isValid(url) &&
                (visitedValidURL.size() < URL_LIMIT)) {
            try {
                Connection.Response resp = getResponse(url);
                // This checks the language of the website if it has provided the information
                // using the HTTP header field
                if (resp.header("content-language").equals("en")) {
                    Document doc = resp.parse();
                    try {
                        visitedValidURL.add(url);
                        appendToFile(url, "Task_1-E_DFS");
                        // Stores the url and HTML contents
                        //createHTMLPage(url.toString(), doc.html());
                        customObjectWriter.writeObjectToFile(new HtmlPage(url, doc.html()));
                        // For politeness, consecutive http request will have at least 1000ms delay
                        Thread.sleep(POLITENESS_TIME);
                        Elements elements = doc.select("a[href]");
                        elements.forEach((element) -> {
                            String anchorURL = element.absUrl("href");
                            hyperlinks.add(anchorURL);
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                return hyperlinks;
            }
        }
        return hyperlinks;
    }
}
