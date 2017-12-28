package com.information.retrieval.text.transformation.querybuilder;

import java.io.File;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import com.information.retrieval.fileio.FileUtility;

public class QueryBuilder {

    final private static String FILE_DIRECTORY_NAME = "Project_Files\\CACM_QUERIES_LUCENE";
    final private static String FILE_DIRECTORY_NAME_TR = "Project_Files\\CACM_QUERIES";

    final static String REGEX1 =
            "( *)([\".,\\/#!$%\\?\\^&\\*;:{}=_`\'~()\\[\\]])( *)([\".,\\/#!$%\\?\\^&\\*;:{}=_`\'~()\\[\\]])*(\\D)";
    final static String REGEX2 =
            "(\\D)( *)([\".,\\/#!$%\\?\\^&\\*;:{}=_`~()\\[\\]])( *)([\".,\\/#!$%\\?\\^&\\*;:{}=\\-_`~()\\[\\]])*";

    final static String REGEX3 = "(\\d+\\s+)+$";

    public static void queryExtract() throws Exception {
    	makeDirectory(FILE_DIRECTORY_NAME);
        String sc = new Scanner(new File("Project_Files\\CACM_UNPROCESSED_QUERIES\\cacm.query.txt"))
                .useDelimiter("\\Z").next();
        Document doc = Jsoup.parse(sc, "UTF-8", Parser.xmlParser());
        Elements docs = doc.getElementsByTag("doc");
        for (Element elem : docs) {
            String text = elem.text().substring(elem.text().indexOf(" ") + 1);
            FileUtility.appendToFile(text,
            		FILE_DIRECTORY_NAME + File.separator + "cacm.query.processed");
        }
    }

    public static void queryExtractTransformed() throws Exception {
    	makeDirectory(FILE_DIRECTORY_NAME_TR);
        String sc = new Scanner(new File("Project_Files\\CACM_UNPROCESSED_QUERIES\\cacm.query.txt"))
                .useDelimiter("\\Z").next();
        Document doc = Jsoup.parse(sc, "UTF-8", Parser.xmlParser());
        Elements docs = doc.getElementsByTag("doc");
        for (Element elem : docs) {
            String text = elem.text().substring(elem.text().indexOf(" ") + 1).toLowerCase()
                    .replaceAll(REGEX1, " $5")
                    .replaceAll(REGEX2, "$1 ")
                    .replaceAll(REGEX3, "");
            FileUtility.appendToFile(text,
                    FILE_DIRECTORY_NAME_TR + File.separator + "cacm.query.transformed.processed");
        }
    }

    private static void makeDirectory(String dirPath) {
        // Creates the directory if not present
        File fileDir = new File(dirPath);

        if (!fileDir.exists()) {
            fileDir.mkdir();
        }

    }
    public static void main(String args[]) throws Exception {
        queryExtract();
        queryExtractTransformed();
    }
}
