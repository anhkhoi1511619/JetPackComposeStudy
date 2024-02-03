package com.example.jetpackcomposeexample.model.history;

public class PostHistoryData {
    long date;
    String author;
    String title;

    public PostHistoryData(long date, String author, String title) {
        this.date = date;
        this.author = author;
        this.title = title;
    }

    public long getDate() {
        return date;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }
}
