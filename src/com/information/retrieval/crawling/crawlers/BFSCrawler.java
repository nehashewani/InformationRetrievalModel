package com.information.retrieval.crawling.crawlers;

import com.information.retrieval.fileio.FileUtility;
import com.information.retrieval.text.transformation.pojo.HtmlPage;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import static com.information.retrieval.crawling.constants.CrawlingConstants.*;
import static com.information.retrieval.fileio.FileUtility.appendToFile;

import java.io.*;
import java.util.*;

public class BFSCrawler extends Crawler {

    public BFSCrawler(String url, String keyword) throws IOException {
       super(url , keyword);
       customObjectWriter = new FileUtility.CustomObjectWriter("Html_pages_bfs.txt");
    }

    /**
     * Method to start crawl beginning with seed url
     *
     */
    @Override
    public void crawl() throws IOException {
        final String seedURL = url;
        Queue<String> currentDepthQueue = new LinkedList<>();
        currentDepthQueue.add(seedURL);
        crawl(visitedURL, 1, currentDepthQueue);
        customObjectWriter.flush();
    }

    /*public void createGraph() {

        // Removed entries where url keys are not visited/ crawled
        adjacencyListMap = adjacencyListMap.entrySet().stream()
                .filter((entry) ->{ return visitedValidURL.contains(entry.getKey());})
                .collect(Collectors.toMap( (p) -> {return p.getKey();} , (p) -> {return p.getValue();}));

        // Creates a graph by iterating through map entries and storing them in a file
        adjacencyListMap.entrySet()
                .forEach( entry -> {
                    StringBuilder documentLine = new StringBuilder(extractDocumentID(entry.getKey() + " "));
                    entry.getValue().forEach( url -> {
                        documentLine.append(extractDocumentID(url))
                                .append(" ");
                        //documentLine.append(url)
                          //      .append(" ");
                    });
                    appendToFile( documentLine.toString() , "Graph");
                });
    }*/

    /**
     * Method performs an BFS based traversal of all the hyperlinks present in the web page
     * @param visitedURL
     * @param depth
     * @param currentDepthQueue
     */

    private void crawl(Set<String> visitedURL, int depth, Queue<String> currentDepthQueue) {
        if (depth > MAX_DEPTH || visitedValidURL.size() >= URL_LIMIT)
            return;

        Queue<String> nextDepthQueue = new LinkedList<>();

        currentDepthQueue.forEach((link) -> {
            buildQueueForNextBreadthTraversal(visitedURL, link ,nextDepthQueue);
        });

        crawl(visitedURL, depth + 1, nextDepthQueue);
    }

    private void buildQueueForNextBreadthTraversal(Set<String> visitedURL, String link, Queue<String> nextDepthQueue) {
        StringBuilder url = new StringBuilder(link);
        if (visitedURL.contains(url.toString().toLowerCase()))
            return;

        String urlString = url.toString();
        visitedURL.add(urlString.toLowerCase());
        if (isValid(urlString) &&
                (visitedValidURL.size() < URL_LIMIT)) {
            try {
                Connection.Response resp = getResponse(urlString);
                // This checks the language of the website if it has provided the information
                // using the HTTP header field
                if (resp.header("content-language").equals("en")) {
                    Document doc = resp.parse();
                    try {
                        visitedValidURL.add(urlString.toLowerCase());
                        appendToFile(urlString, "Task_1-E");
                        // Stores the url and HTML contents
                        //createHTMLPage(url.toString(), doc.html());
                        customObjectWriter.writeObjectToFile(new HtmlPage(urlString , doc.html()));
                        // For politeness, consecutive http request will have at least 1000ms delay
                        Thread.sleep(POLITENESS_TIME);
                        Elements elements = doc.select("a[href]");
                        elements.forEach((element) -> {
                            String anchorURL = element.absUrl("href");
                            //buildInLinkForAnchorURL(urlString, anchorURL);
                            if (!visitedURL.contains(anchorURL)) {
                                nextDepthQueue.add(anchorURL);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                return;
            }
        }
    }

    /*private void buildInLinkForAnchorURL(String sourceURL, String endURL) {
        if (adjacencyListMap.get(endURL) == null)    {
            Set<String> inLinks = new HashSet<>();
            inLinks.add(sourceURL);
            adjacencyListMap.put(endURL , inLinks);
        }
        else    {
            adjacencyListMap.get(endURL).add(sourceURL);
        }
    }*/


}
