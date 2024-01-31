package com.example.jetpackcomposeexample.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PostHistoryEntity {
    @PrimaryKey(autoGenerate = true)
    int no;
    long date;
    String author;
    String title;

    public PostHistoryEntity(long date, String author, String title) {
        this.date = date;
        this.author = author;
        this.title = title;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
