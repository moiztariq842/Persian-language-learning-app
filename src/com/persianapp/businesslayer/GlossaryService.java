package com.persianapp.businesslayer;

import java.util.List;
import java.util.UUID;

/**
 * Business service for glossary management (UC-04).
 * Enforces all validation and business rules before delegating persistence to the injected DAO.
 *
 * Business rules:
 * - BR-03: A glossary entry shall contain both a Persian word and an Urdu meaning.
 * - BR-06: A failed validation shall not replace previously saved valid data.
 * - BR-07: The application shall reject invalid data with a clear message.
 */
public class GlossaryService {

    private final GlossaryEntryDAO dao;

    public GlossaryService(GlossaryEntryDAO dao) {
        this.dao = dao;
    }

    /** Adds a new glossary entry after validation (FR-GL-02). */
    public GlossaryEntry addEntry(String chapterId, String persianWord, String urduMeaning)
            throws GlossaryException {

        validateChapterId(chapterId);
        validatePersianWord(persianWord);
        validateUrduMeaning(urduMeaning);

        // FR-GL-06: verify the chapter actually exists
        if (!dao.chapterExists(chapterId.trim())) {
            throw new IllegalArgumentException(
                    "مذکورہ باب موجود نہیں ہے۔ براہ کرم درست باب منتخب کریں۔\n"
                    + "(The specified chapter does not exist. Please select a valid chapter.)");
        }

        GlossaryEntry entry = new GlossaryEntry(
                UUID.randomUUID().toString(),
                chapterId.trim(),
                persianWord.trim(),
                urduMeaning.trim()
        );

        dao.addEntry(entry);
        return entry;
    }

    /** Retrieves all glossary entries for a chapter (FR-GL-03). */
    public List<GlossaryEntry> getEntries(String chapterId) throws GlossaryException {
        validateChapterId(chapterId);
        return dao.getEntriesByChapterId(chapterId);
    }

    /** Retrieves a single glossary entry by its identifier. */
    public GlossaryEntry getEntryById(String id) throws GlossaryException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Entry ID must not be blank.");
        }
        return dao.getEntryById(id);
    }

    /** Updates an existing glossary entry after validation (FR-GL-04). */
    public void updateEntry(GlossaryEntry entry) throws GlossaryException {
        if (entry == null) {
            throw new IllegalArgumentException("Entry must not be null.");
        }
        if (entry.getId() == null || entry.getId().isBlank()) {
            throw new IllegalArgumentException("Entry ID must not be blank.");
        }
        validatePersianWord(entry.getPersianWord());
        validateUrduMeaning(entry.getUrduMeaning());

        // Build a new object instead of mutating the caller's (BR-06)
        GlossaryEntry trimmed = new GlossaryEntry(
                entry.getId(),
                entry.getChapterId(),
                entry.getPersianWord().trim(),
                entry.getUrduMeaning().trim()
        );

        boolean updated = dao.updateEntry(trimmed);
        if (!updated) {
            throw new IllegalArgumentException(
                    "اندراج نہیں ملا۔ ممکن ہے یہ پہلے سے حذف ہو چکا ہو۔\n"
                    + "(Entry not found. It may have been deleted.)");
        }
    }

    /** Deletes a glossary entry by its identifier (FR-GL-05). */
    public void deleteEntry(String id) throws GlossaryException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Entry ID must not be blank.");
        }
        dao.deleteEntry(id);
    }

    /** Deletes all glossary entries for a chapter (FR-CH-07). */
    public void deleteEntriesByChapterId(String chapterId) throws GlossaryException {
        validateChapterId(chapterId);
        dao.deleteEntriesByChapterId(chapterId);
    }

    // ── Validation helpers (BR-03, BR-07) ────────────────────

    private void validateChapterId(String chapterId) {
        if (chapterId == null || chapterId.isBlank()) {
            throw new IllegalArgumentException("Chapter ID must not be blank.");
        }
    }

    private void validatePersianWord(String persianWord) {
        if (persianWord == null || persianWord.isBlank()) {
            throw new IllegalArgumentException(
                    "فارسی لفظ خالی نہیں ہو سکتا۔ براہ کرم فارسی لفظ درج کریں۔");
        }
    }

    private void validateUrduMeaning(String urduMeaning) {
        if (urduMeaning == null || urduMeaning.isBlank()) {
            throw new IllegalArgumentException(
                    "اردو معنی خالی نہیں ہو سکتا۔ براہ کرم اردو معنی درج کریں۔");
        }
    }
}
