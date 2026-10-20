package com.persianapp.businesslayer;

import java.util.Objects;

/**
 * Represents a single glossary entry: a Persian word and its Urdu meaning,
 * belonging to a specific chapter.
 *
 * <p>This is a pure domain object (POJO) with no database or UI logic.
 * It maps to Data Requirement 6.2 in the SRS.</p>
 *
 * @see com.persianapp.dataaccesslayer.GlossaryEntryDAO
 * @see GlossaryService
 */
public class GlossaryEntry {

    private String id;
    private String chapterId;
    private String persianWord;
    private String urduMeaning;

    /**
     * Default constructor.
     */
    public GlossaryEntry() {
    }

    /**
     * Full constructor.
     *
     * @param id          system-generated unique identifier
     * @param chapterId   identifier of the parent chapter (FR-GL-06)
     * @param persianWord the Persian word introduced in this chapter
     * @param urduMeaning the Urdu meaning of the Persian word
     */
    public GlossaryEntry(String id, String chapterId, String persianWord, String urduMeaning) {
        this.id = id;
        this.chapterId = chapterId;
        this.persianWord = persianWord;
        this.urduMeaning = urduMeaning;
    }

    // ── Getters ──────────────────────────────────────────────

    public String getId() {
        return id;
    }

    public String getChapterId() {
        return chapterId;
    }

    public String getPersianWord() {
        return persianWord;
    }

    public String getUrduMeaning() {
        return urduMeaning;
    }

    // ── Setters ──────────────────────────────────────────────

    public void setId(String id) {
        this.id = id;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public void setPersianWord(String persianWord) {
        this.persianWord = persianWord;
    }

    public void setUrduMeaning(String urduMeaning) {
        this.urduMeaning = urduMeaning;
    }

    // ── equals / hashCode (identity based on id) ─────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GlossaryEntry that = (GlossaryEntry) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "GlossaryEntry{" +
                "id='" + id + '\'' +
                ", chapterId='" + chapterId + '\'' +
                ", persianWord='" + persianWord + '\'' +
                ", urduMeaning='" + urduMeaning + '\'' +
                '}';
    }
}
