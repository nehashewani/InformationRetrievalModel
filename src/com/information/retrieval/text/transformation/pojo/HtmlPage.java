package com.information.retrieval.text.transformation.pojo;

import java.io.Serializable;

/***
 * Serializable Class to store url and raw html in webpage
 */

public class HtmlPage implements Serializable {
    private String URL;
    private String rawHTML;

    public HtmlPage() {

    }

    public HtmlPage(String URL, String rawHTML) {
        this.URL = URL;
        this.rawHTML = rawHTML;
    }

    public String getURL() {
        return URL;
    }

    /*public void setURL(String URL) {
        this.URL = URL;
    }*/

    public String getRawHTML() {
        return rawHTML;
    }

    /*public void setRawHTML(String rawHTML) {
        this.rawHTML = rawHTML;
    }*/

}

