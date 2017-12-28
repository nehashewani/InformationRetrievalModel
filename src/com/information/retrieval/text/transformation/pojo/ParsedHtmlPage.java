package com.information.retrieval.text.transformation.pojo;

public class ParsedHtmlPage {
    private String title;
    private String textualContent;

    public String getTitle() {
        return title;
    }

    public String getTextualContent() {
        return textualContent;
    }

    public ParsedHtmlPage(String title, String textualContent) {
        this.title = title;
        this.textualContent = textualContent;
    }

    @Override
    public String toString() {
        return getTextualContent();
    }

    @Override
    public boolean equals(Object obj) {
        ParsedHtmlPage htmlPage = (ParsedHtmlPage) obj;
        return (this.getTitle() == htmlPage.getTitle() &&
                this.getTextualContent() == htmlPage.getTextualContent());
    }
}
