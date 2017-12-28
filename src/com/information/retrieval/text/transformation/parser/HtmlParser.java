package com.information.retrieval.text.transformation.parser;

import com.information.retrieval.fileio.FileUtility;
import com.information.retrieval.text.transformation.pojo.ParsedHtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.information.retrieval.text.transformation.constants.Constants.SPACE;

/**
 
 */
public class HtmlParser implements Parser {

    private static final String PRE_TAG = "pre";
    final private static String FILE_DIRECTORY_NAME = "CACM_PARSED_TEXT";
    List<ParsedHtmlPage> parsedHtmlPageList = new ArrayList<>();
    private boolean isCaseFolding = true;
    private boolean isPunctuationHandling = true;

    @Override
    public List<ParsedHtmlPage> parsedHtmlPages() {
        return parsedHtmlPageList;
    }

    @Override
    public void parseDocument(String htmlPageFileLocation) {
        File file = new File(htmlPageFileLocation);

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            List<File> fileList = Arrays.asList(files);
            fileList.stream().forEach(htmlFile -> {
                ParsedHtmlPage parsedHtmlPage = getParsedHtmlPage(htmlFile);
                parsedHtmlPageList.add(parsedHtmlPage);
            });
        }
    }

    private ParsedHtmlPage getParsedHtmlPage(File file) {

        try {
            Document doc = null;
            String parsedText = null;
            doc = Jsoup.parse(file, null);
            StringBuilder sb = new StringBuilder();

            doc.getElementsByTag(PRE_TAG).forEach(element -> {
                sb.append(element.text()).append(SPACE);
            });

            parsedText = sb.toString();
            // IF case folding is set, perform it
            if (this.isCaseFolding) {
                parsedText = parsedText.toLowerCase();
            }

            // IF punctuation handling is set, perform it
            if (this.isPunctuationHandling) {
                parsedText = punctuationHandler(parsedText);
            }

            return new ParsedHtmlPage(file.getName(), parsedText);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String punctuationHandler(String parsedText) {

        final String REGEX1 =
                "( *)([\".,\\/#!$%\\?\\^&\\*;:{}=_`\'~()\\[\\]])( *)([\".,\\/#!$%\\?\\^&\\*;:{}=_`\'~()\\[\\]])*(\\D)";
        final String REGEX2 =
                "(\\D)( *)([\".,\\/#!$%\\?\\^&\\*;:{}=_`~()\\[\\]])( *)([\".,\\/#!$%\\?\\^&\\*;:{}=\\-_`~()\\[\\]])*";

        final String REGEX3 = "(\\d+\\s+)+$";

        return parsedText.replaceAll(REGEX1, " $5")
                .replaceAll(REGEX2, "$1 ")
                .replaceAll(REGEX3, "");

//        return parsedText;

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
        parsedHtmlPageList.parallelStream().forEach(parsedHtmlPage -> {
            FileUtility.writeToFile(parsedHtmlPage,
                    FILE_DIRECTORY_NAME + File.separator + parsedHtmlPage.getTitle());
        });
    }


    // Test the parsing process
    public static void main(String args[]) {

        Parser htmlParser = new HtmlParser();
        htmlParser.parseDocument("C:\\Users\\siben_000\\Documents" +
                "\\Information_Retrieval\\Project\\cacm.tar\\cacm");

        htmlParser.storeParsedText();

    }
}
