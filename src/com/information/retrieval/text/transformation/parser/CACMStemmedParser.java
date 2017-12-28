package com.information.retrieval.text.transformation.parser;

import com.information.retrieval.fileio.FileUtility;
import com.information.retrieval.text.transformation.pojo.ParsedHtmlPage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/***
 
 */
public class CACMStemmedParser implements Parser {

    private boolean isCaseFolding = true;
    private boolean isPunctuationHandling = true;
    private List<ParsedHtmlPage> parsedHtmlPages = new ArrayList<>();

    @Override
    public List<ParsedHtmlPage> parsedHtmlPages() {
        return parsedHtmlPages;
    }

    @Override
    public void parseDocument(String documentFileLocation) {
        try {
            FileUtility.CustomFileReader cfr = new FileUtility.CustomFileReader(documentFileLocation);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = cfr.readLineFromFile()) != null) {
                sb.append(line);
            }

            String[] textualContents = sb.toString().split("#\\s+\\d+");

            final AtomicInteger atomicInteger = new AtomicInteger(1);
            Arrays.stream(textualContents).filter(text -> !"".equals(text)).forEach(textualContent ->
            {
                if (isCaseFolding) {
                    textualContent = textualContent.toLowerCase();
                }

                if (isPunctuationHandling) {
                    textualContent = punctuationHandler(textualContent);
                }

                ParsedHtmlPage parsedHtmlPage =
                        new ParsedHtmlPage(Integer.toString(atomicInteger.getAndIncrement()), textualContent.trim());

                parsedHtmlPages.add(parsedHtmlPage);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String punctuationHandler(String textualContent) {

        final String REGEX1 =
                "( *)([\".,\\/#!$%\\?\\^&\\*;:{}=_`\'~()\\[\\]])( *)([\".,\\/#!$%\\?\\^&\\*;:{}=_`\'~()\\[\\]])*(\\D)";
        final String REGEX2 =
                "(\\D)( *)([\".,\\/#!$%\\?\\^&\\*;:{}=_`~()\\[\\]])( *)([\".,\\/#!$%\\?\\^&\\*;:{}=\\-_`~()\\[\\]])*";

        final String REGEX3 = "(\\d+\\s+)+$";
        return textualContent
                .replaceAll(REGEX1, " $5")
                .replaceAll(REGEX2, "$1 ")
                .replaceAll(REGEX3, "");
    }

    @Override
    public void caseFolding(boolean caseFolding) {
        this.isCaseFolding = caseFolding;
    }

    @Override
    public void punctuationHandling(boolean punctuationHandler) {
        this.isPunctuationHandling = punctuationHandler;
    }

    public static void main(String args[]) {
        Parser parser = new CACMStemmedParser();
        parser.parseDocument("C:\\Users\\siben_000\\Documents\\Information_Retrieval\\Gitrepo" +
                "\\Graph_Builder\\CACM_STEM_TEXT\\cacm_stem");

        parser.storeParsedText("CACM_STEM_TEXT");
    }
}
