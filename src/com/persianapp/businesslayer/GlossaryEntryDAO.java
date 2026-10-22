package com.persianapp.businesslayer;

import java.util.List;

/**
 * Data Access Object interface for glossary entry persistence operations.
 *
 * <p>Defines the contract for CRUD operations on glossary entries.
 * This interface lives in the <strong>business layer</strong> so that the
 * dependency points inward: the data-access layer depends on the business
 * layer, never the other way around (Dependency Inversion Principle).</p>
 *
 * <p>All methods throw {@link GlossaryException} instead of
 * {@code java.sql.SQLException}, keeping the business layer free of
 * database imports.</p>
 *
 * @see GlossaryEntry
 * @see GlossaryService
 */
public interface GlossaryEntryDAO {

    /**
     * Persists a new glossary entry.
     *
     * @param entry the entry to add (FR-GL-02)
     * @throws GlossaryException if a persistence error occurs
     */
    void addEntry(GlossaryEntry entry) throws GlossaryException;

    /**
     * Retrieves all glossary entries belonging to a chapter,
     * ordered by {@code created_at} ascending.
     *
     * @param chapterId the parent chapter identifier (FR-GL-03)
     * @return list of entries for the chapter, possibly empty
     * @throws GlossaryException if a persistence error occurs
     */
    List<GlossaryEntry> getEntriesByChapterId(String chapterId) throws GlossaryException;

    /**
     * Retrieves a single glossary entry by its unique identifier.
     *
     * @param id the entry identifier
     * @return the entry, or {@code null} if not found
     * @throws GlossaryException if a persistence error occurs
     */
    GlossaryEntry getEntryById(String id) throws GlossaryException;

    /**
     * Updates an existing glossary entry's Persian word and Urdu meaning.
     *
     * @param entry the entry with updated values (FR-GL-04)
     * @return {@code true} if a row was updated, {@code false} if the id was not found
     * @throws GlossaryException if a persistence error occurs
     */
    boolean updateEntry(GlossaryEntry entry) throws GlossaryException;

    /**
     * Deletes a single glossary entry by its identifier.
     *
     * @param id the entry identifier (FR-GL-05)
     * @throws GlossaryException if a persistence error occurs
     */
    void deleteEntry(String id) throws GlossaryException;

    /**
     * Deletes all glossary entries belonging to a chapter.
     * Used when a chapter is deleted (FR-CH-07).
     *
     * @param chapterId the parent chapter identifier
     * @throws GlossaryException if a persistence error occurs
     */
    void deleteEntriesByChapterId(String chapterId) throws GlossaryException;

    /**
     * Checks whether a chapter with the given id exists.
     *
     * @param chapterId the chapter identifier to check (FR-GL-06)
     * @return {@code true} if the chapter exists
     * @throws GlossaryException if a persistence error occurs
     */
    boolean chapterExists(String chapterId) throws GlossaryException;
}
