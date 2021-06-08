package com.example.searchengine;

public class CellItem {

    private String title;
    private String url;
    private String description;

    public CellItem(String text1, String text2, String text3) {

        title = text1;
        url = text2;
        description = text3;
    }
    public String getTitle() {
        return title;
    }
    public String getUrl() {
        return url;
    }
    public String getDescription() {
        return description;
    }
}
