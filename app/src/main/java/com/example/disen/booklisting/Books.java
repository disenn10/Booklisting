package com.example.disen.booklisting;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by disen on 7/6/2017.
 */

public class Books implements Serializable {
    private String author;
    private String title;
    private String image;
    private double ratings;
    private String date;
    private String description;
    private String weblink;

    public Books(String author, String title, String image, double rating, String date, String description, String weblink) {
        this.author = author;
        this.title = title;
        this.image = image;
        this.ratings = rating;
        this.date = date;
        this.description = description;
        this.weblink = weblink;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public double getRatings() {
        return ratings;
    }

    public String getDate() {
        return date;
    }

    public String getWeblink() {
        return weblink;
    }

    public String getDescription() {
        return description;
    }
}
