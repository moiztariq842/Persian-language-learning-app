package com.persianapp.businesslayer;

// UC-01 Chapter entity. Package-private setter preserves encapsulation.
public class Chapter {

    private final String id;
    private String title;

    // Creates a chapter with an existing ID and title (loaded from DB).
    public Chapter(String id, String title) {
        this.id    = id;
        this.title = title;
    }

    // Returns the chapter ID.
    public String getId()    { return id; }

    // Returns the chapter title.
    public String getTitle() { return title; }

    // Updates the chapter title. Only called from ChapterBao.
    void setTitle(String title) { this.title = title; }

    // Returns the chapter title as string (used in JList / JComboBox).
    @Override
    public String toString() { return title; }
}
