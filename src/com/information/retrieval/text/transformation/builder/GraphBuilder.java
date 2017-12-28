package com.information.retrieval.text.transformation.builder;

import com.information.retrieval.fileio.FileUtility;
import com.information.retrieval.text.transformation.pojo.HtmlPage;
import com.information.retrieval.text.transformation.pojo.Graph;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.*;

import static com.information.retrieval.text.transformation.constants.Constants.SPACE;

/**
 * Builder class for Graph
 */
public class GraphBuilder {
    private String graphCreateSource;
    private String graphOutputSource;

    public GraphBuilder() {

    }

    public void setGraphCreateSource(String graphCreateSource) {
        this.graphCreateSource = graphCreateSource;
    }

    public void setGraphOutputSource(String graphOutputSource) {
        this.graphOutputSource = graphOutputSource;
    }


    public void createGraph()   {
        Graph graph = constructGraph();
        FileUtility.writeToFile(graph , graphOutputSource);
    }

    public Graph retrieveGraph() throws Exception {
        final Map<String, Set<String>> inLinkMap = new HashMap<>();
        final Map<String, Set<String>> outLinkMap = new HashMap<>();

        FileUtility.CustomFileReader cfr = new FileUtility.CustomFileReader(graphOutputSource);
        String line = null;

        while ((line = cfr.readLineFromFile()) != null)  {
            String tokens[] = line.split(SPACE);
            inLinkMap.put(tokens[0], new LinkedHashSet<>());
            if (outLinkMap.get(tokens[0]) == null)
                outLinkMap.put(tokens[0], new LinkedHashSet<>());
            for ( int i = 1 ; i < tokens.length ; i++)  {
                inLinkMap.get(tokens[0]).add(tokens[i]);
                if ( outLinkMap.get(tokens[i]) == null)
                    outLinkMap.put(tokens[i] , new LinkedHashSet<>());
                outLinkMap.get(tokens[i]).add(tokens[0]);
            }
        }
        // Close the IO Stream
        cfr.close();
        return new Graph(inLinkMap,outLinkMap);
    }

    private Set<String> inLinks(String[] tokens) {
        Set<String> inLinks = new LinkedHashSet<>();
        for ( int itr = 1 ; itr < tokens.length ; itr++)
            inLinks.add(tokens[itr]);

        return inLinks;
    }

    private Graph constructGraph() {
        List<Object> objects = FileUtility.readObjectsFromFile(graphCreateSource);
        final Map<String, Set<String>> inLinkMap = new HashMap<>();
        final Map<String, Set<String>> outLinkMap = new HashMap<>();
        final Set<String> visitedURL = new HashSet<>();

        objects.forEach(object -> {
            HtmlPage htmlPage = (HtmlPage)object;
            visitedURL.add(htmlPage.getURL());
            inLinkMap.put(htmlPage.getURL() , new LinkedHashSet<>());
            outLinkMap.put(htmlPage.getURL() , new LinkedHashSet<>());
        });

        objects.forEach(object -> {
            HtmlPage htmlPage = (HtmlPage) object;
            Document doc = Jsoup.parse(htmlPage.getRawHTML() , htmlPage.getURL());
            doc.select("a[href]").forEach(element -> {
                String anchorLink = element.absUrl("href");
                if (visitedURL.contains(anchorLink)) {
                    inLinkMap.get(anchorLink).add(htmlPage.getURL());
                    outLinkMap.get(htmlPage.getURL()).add(anchorLink);
                }
            });
        });

        //final Map<String,Set<String>> filteredMap = filterUnvisitedUrlFromMap(inLinkMap , visitedURL);
        Graph graph = new Graph(inLinkMap,outLinkMap);
        return graph;
    }

    /**
    private Map<String, Set<String>> filterUnvisitedUrlFromMap(Map<String, Set<String>> inLinkMap, Set<String> visitedURL) {
        return inLinkMap.entrySet().stream()
                .filter((entry) ->{ return visitedURL.contains(entry.getKey());})
                .collect(Collectors.toMap( (p) -> {return p.getKey();} , (p) -> {return p.getValue();}));

    }**/
}
