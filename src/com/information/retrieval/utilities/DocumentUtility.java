package com.information.retrieval.utilities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class DocumentUtility {
    public static String extractDocumentID(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}

