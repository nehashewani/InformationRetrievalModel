package com.information.retrieval.text.transformation.parser;

import com.information.retrieval.fileio.FileUtility;
import com.information.retrieval.text.transformation.pojo.HtmlPage;
import com.information.retrieval.text.transformation.pojo.ParsedHtmlPage;
import com.information.retrieval.utilities.DocumentUtility;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.util.*;

import static com.information.retrieval.text.transformation.constants.Constants.NEWLINE;
import static com.information.retrieval.text.transformation.constants.Constants.SPACE;

/**
 * Implementation of parser
 
 */
public class Parser1 implements Parser {

    // instance variables
    // Default value is true
    private Boolean isCaseFolding;
    // Default value is true
    private Boolean isPunctuationHandling;
    private List<ParsedHtmlPage> parsedHtmlPages;
    // Constants
    final private static String FILE_DIRECTORY_NAME = "Parsed Text";
    final private static String MAIN_CONTENT_CLASS = "mw-parser-output";
    final private static String PARAGRAPH_TAG = "p";
    final private static String FILE_SEPARATOR = "/";

    public Parser1() {
        this.isCaseFolding = true;
        this.isPunctuationHandling = true;
        this.parsedHtmlPages = new ArrayList<>();
    }

    @Override
    public List<ParsedHtmlPage> parsedHtmlPages() {
        return parsedHtmlPages;
    }

    /**
     * Converts raw Html pages into their parsed representation
     * whose punctuation have been removed.
     *
     * @param fileLocation The location of the file
     *                             where serialized HtmlPage objects are stored
     */
    public void parseDocument(String fileLocation) {
        List<Object> objects = FileUtility.readObjectsFromFile(fileLocation);
        new File(FILE_DIRECTORY_NAME).mkdir();

        // TODO: Parellize the processing of html parsing for each html page

        objects.parallelStream().forEach((object) -> {
            HtmlPage htmlPage = (HtmlPage) object;
            String title = DocumentUtility.extractDocumentID(htmlPage.getURL());
            String parsedText = parseTitle(title) + NEWLINE + parseHtmlText(htmlPage);
            parsedHtmlPages.add(new ParsedHtmlPage(title, parsedText));
        });
    }

    private String parseTitle(String title) {
        if (this.isCaseFolding) {
            title = title.toLowerCase();
        }

        if (this.isPunctuationHandling) {
            title = punctuationHandler(title);
        }

        return title;
    }

    /**
     * Returns the parsed form of Web Page Title
     *
     * @param s
     * @return A parsed form of String s
     */
    private String getTitle(String s) {
        if (this.isCaseFolding) {
            s = s.toLowerCase();
        }

        if (this.isPunctuationHandling) {
            s = punctuationHandler(s);
        }

        return s;
    }


    @Override
    public void caseFolding(boolean caseFolding) {
        this.isCaseFolding = caseFolding;
    }

    @Override
    public void punctuationHandling(boolean punctuationHandler) {
        this.isPunctuationHandling = punctuationHandler;
    }

    @Override
    public void storeParsedText() {
        // For each of the parsed page, store them in the file
        parsedHtmlPages.parallelStream().forEach(parsedHtmlPage -> {
            FileUtility.writeToFile(parsedHtmlPage, FILE_DIRECTORY_NAME + FILE_SEPARATOR + parsedHtmlPage.getTitle());
        });
    }

    /**
     * Returns the textual content of the web page
     *
     * @param htmlPage
     * @return the textual content of the web page.
     */
    private String parseHtmlText(HtmlPage htmlPage) {

        Document doc = Jsoup.parse(htmlPage.getRawHTML(), htmlPage.getURL());
        StringBuilder sb = new StringBuilder();
        doc.getElementsByClass(MAIN_CONTENT_CLASS).forEach(element -> {
            element.getElementsByTag(PARAGRAPH_TAG).forEach(paragraph_element -> {
                sb.append(paragraph_element.text()).append(SPACE);
            });
        });

        String parsedText = sb.toString();
        // IF case folding is set, perform it
        if (this.isCaseFolding) {
            parsedText = parsedText.toLowerCase();
        }

        // IF punctuation handling is set, perform it
        if (this.isPunctuationHandling) {
            parsedText = punctuationHandler(parsedText);
        }

        return parsedText;
    }

    /***
     * Handles and removes the punctuation from the provided String
     * @param parsedText
     * @return Returns string, handling the punctuation requirements
     */
    private String punctuationHandler(String parsedText) {

        final String REGEX1 =
                "( *)([\".,\\/#!$%\\?\\^&\\*;:{}=_`\'~()\\[\\]])( *)([\".,\\/#!$%\\?\\^&\\*;:{}=_`\'~()\\[\\]])*(\\D)";
        final String REGEX2 =
                "(\\D)( *)([\".,\\/#!$%\\?\\^&\\*;:{}=_`~()\\[\\]])( *)([\".,\\/#!$%\\?\\^&\\*;:{}=\\-_`~()\\[\\]])*";

        return parsedText.replaceAll(REGEX1, " $5")
                .replaceAll(REGEX2, "$1 ");
    }
}
