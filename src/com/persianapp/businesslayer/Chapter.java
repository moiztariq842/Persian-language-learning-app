package com.persianapp.businesslayer;

import java.util.UUID;

public class Chapter {

    private final String id;
    private String title;

    // Creates a new chapter with a unique ID.
    public Chapter(String title) {
        this.id    = UUID.randomUUID().toString();
        this.title = title;
    }

    // Creates a chapter with an existing ID and title.
    public Chapter(String id, String title) {
        this.id    = id;
        this.title = title;
    }

    // Returns the chapter ID.
    public String getId()    { return id; }

    // Returns the chapter title.
    public String getTitle() { return title; }

    // Updates the chapter title.
    void setTitle(String title) { this.title = title; }

    // Returns the chapter title as a string.
    @Override
    public String toString() { return title; }
}
