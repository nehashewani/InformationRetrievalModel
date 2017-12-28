package com.information.retrieval.text.transformation.parser;

import com.information.retrieval.fileio.FileUtility;
import com.information.retrieval.text.transformation.pojo.ParsedHtmlPage;

import java.io.File;
import java.util.List;

/***
 
 */
public interface Parser {

    // Returns the list of parsedHtmlP
    public List<ParsedHtmlPage> parsedHtmlPages();

    public void parseDocument(String documentFileLocation);

    /**
     * Enables/Disables the case folding options
     * @param caseFolding
     */
    public void caseFolding(boolean caseFolding);

    /**
     * Enables/ Disables the punctuation handler
     * @param punctuationHandler
     */
    public void punctuationHandling(boolean punctuationHandler);

    /**
     * Stores the parsed form of Html pages
     * in a file
     */
    default public void storeParsedText()   {

    }

    /**
     * Stores the parsed form of Html pages
     * in a directory specified by path
     */
    default void storeParsedText(String path)   {
        File file = new File(path);

        if ( !file.exists())
            file.mkdir();

        parsedHtmlPages().stream().forEach(parsedHtmlPage -> {
            FileUtility.writeToFile(parsedHtmlPage, path + File.separator + parsedHtmlPage.getTitle());
        });

    }
}
