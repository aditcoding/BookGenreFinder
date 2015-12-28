package com.bookbub.hw;

/**
 * Created by adi on 12/9/15.
 */
public class Book {

    private final String title;
    private final String description;

    public Book(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

}
