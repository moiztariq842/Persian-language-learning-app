package com.persianapp.businesslayer;

import java.util.Objects;

/**
 * Represents a single glossary entry: a Persian word and its Urdu meaning,
 * belonging to a specific chapter.
 *
 * <p>This is an immutable domain object (POJO) with no database or UI logic.
 * Fields are final — once created, an entry's data cannot be changed.
 * To "update" an entry, create a new instance with the desired values.
 * It maps to Data Requirement 6.2 in the SRS.</p>
 *
 * @see GlossaryEntryDAO
 * @see GlossaryService
 */
public class GlossaryEntry {

    private final String id;
    private final String chapterId;
    private final String persianWord;
    private final String urduMeaning;

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
