package com.persianapp.businesslayer;

import java.util.Objects;

/**
 * Represents a single glossary entry: a Persian word and its Urdu meaning,
 * belonging to a specific chapter (UC-04).
 */
public class GlossaryEntry {

    private final String id;
    private final String chapterId;
    private final String persianWord;
    private final String urduMeaning;

    public GlossaryEntry(String id, String chapterId, String persianWord, String urduMeaning) {
        this.id          = id;
        this.chapterId   = chapterId;
        this.persianWord = persianWord;
        this.urduMeaning = urduMeaning;
    }

    public String getId()          { return id; }
    public String getChapterId()   { return chapterId; }
    public String getPersianWord() { return persianWord; }
    public String getUrduMeaning() { return urduMeaning; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(id, ((GlossaryEntry) o).id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "GlossaryEntry{id='" + id + "', persianWord='" + persianWord + "', urduMeaning='" + urduMeaning + "'}";
    }
}
