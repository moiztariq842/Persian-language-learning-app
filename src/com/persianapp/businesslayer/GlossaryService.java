package com.persianapp.businesslayer;

import java.util.List;
import java.util.UUID;

/**
 * Business service for glossary management (UC-04).
 *
 * <p>Enforces all validation and business rules before delegating
 * persistence to the injected {@link GlossaryEntryDAO}.</p>
 *
 * <h3>Business rules enforced:</h3>
 * <ul>
 *   <li>BR-03: A glossary entry shall contain both a Persian word and an Urdu meaning.</li>
 *   <li>BR-06: A failed validation shall not replace previously saved valid data.</li>
 *   <li>BR-07: The application shall reject invalid data with a clear message.</li>
 * </ul>
 */
public class GlossaryService {

    private final GlossaryEntryDAO dao;

    /**
     * Constructs the service with the given DAO.
     * Production code passes {@code GlossaryEntryDAOImpl};
     * tests may pass a custom stub (NFR-MT-03).
     *
     * @param dao the data access object for glossary entries
     */
    public GlossaryService(GlossaryEntryDAO dao) {
        this.dao = dao;
    }

    /**
     * Adds a new glossary entry after validation (FR-GL-02).
     *
     * @param chapterId   the parent chapter identifier
     * @param persianWord the Persian word
     * @param urduMeaning the Urdu meaning
     * @return the created entry with a system-generated id
     * @throws IllegalArgumentException if validation fails (BR-03, BR-07)
     * @throws GlossaryException        if a persistence error occurs
     */
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

    /**
     * Retrieves all glossary entries for a chapter, ordered by creation time (FR-GL-03).
     *
     * @param chapterId the parent chapter identifier
     * @return list of entries, possibly empty
     * @throws GlossaryException if a persistence error occurs
     */
    public List<GlossaryEntry> getEntries(String chapterId) throws GlossaryException {
        validateChapterId(chapterId);
        return dao.getEntriesByChapterId(chapterId);
    }

    /**
     * Retrieves a single glossary entry by its identifier.
     *
     * @param id the entry identifier
     * @return the entry, or {@code null} if not found
     * @throws GlossaryException if a persistence error occurs
     */
    public GlossaryEntry getEntryById(String id) throws GlossaryException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Entry ID must not be blank.");
        }
        return dao.getEntryById(id);
    }

    /**
     * Updates an existing glossary entry after validation (FR-GL-04).
     *
     * <p>If validation fails, the previously saved valid data is not
     * replaced (BR-06). A new object is built for the DAO so the caller's
     * object is never mutated as a side effect.</p>
     *
     * @param entry the entry with updated values
     * @throws IllegalArgumentException if validation fails or entry not found
     * @throws GlossaryException        if a persistence error occurs
     */
    public void updateEntry(GlossaryEntry entry) throws GlossaryException {
        if (entry == null) {
            throw new IllegalArgumentException("Entry must not be null.");
        }
        if (entry.getId() == null || entry.getId().isBlank()) {
            throw new IllegalArgumentException("Entry ID must not be blank.");
        }
        validatePersianWord(entry.getPersianWord());
        validateUrduMeaning(entry.getUrduMeaning());

        // Build a new object instead of mutating the caller's (issue #8)
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

    /**
     * Deletes a glossary entry by its identifier (FR-GL-05).
     *
     * <p>This method should only be called after the presentation layer
     * has obtained user confirmation (BR-04).</p>
     *
     * @param id the entry identifier
     * @throws GlossaryException if a persistence error occurs
     */
    public void deleteEntry(String id) throws GlossaryException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Entry ID must not be blank.");
        }
        dao.deleteEntry(id);
    }

    /**
     * Deletes all glossary entries for a chapter (FR-CH-07).
     *
     * @param chapterId the parent chapter identifier
     * @throws GlossaryException if a persistence error occurs
     */
    public void deleteEntriesByChapterId(String chapterId) throws GlossaryException {
        validateChapterId(chapterId);
        dao.deleteEntriesByChapterId(chapterId);
    }

    // ── Validation Helpers (BR-03, BR-07) ────────────────────

    private void validateChapterId(String chapterId) {
        if (chapterId == null || chapterId.isBlank()) {
            throw new IllegalArgumentException("Chapter ID must not be blank.");
        }
    }

    private void validatePersianWord(String persianWord) {
        if (persianWord == null || persianWord.isBlank()) {
            throw new IllegalArgumentException(
                    "فارسی لفظ خالی نہیں ہو سکتا۔ براہ کرم فارسی لفظ درج کریں۔"
                    // "Persian word cannot be empty. Please enter a Persian word."
            );
        }
    }

    private void validateUrduMeaning(String urduMeaning) {
        if (urduMeaning == null || urduMeaning.isBlank()) {
            throw new IllegalArgumentException(
                    "اردو معنی خالی نہیں ہو سکتا۔ براہ کرم اردو معنی درج کریں۔"
                    // "Urdu meaning cannot be empty. Please enter an Urdu meaning."
            );
        }
    }
}
