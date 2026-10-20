package com.persianapp.dataaccesslayer;

import com.persianapp.businesslayer.GlossaryEntry;

import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object interface for glossary entry persistence operations.
 *
 * <p>Defines the contract for CRUD operations on glossary entries.
 * The business layer depends on this interface (Dependency Inversion Principle),
 * allowing test stubs to replace the real implementation (NFR-MT-03).</p>
 *
 * @see GlossaryEntryDAOImpl
 */
public interface GlossaryEntryDAO {

    /**
     * Persists a new glossary entry.
     *
     * @param entry the entry to add (FR-GL-02)
     * @throws SQLException if a database error occurs
     */
    void addEntry(GlossaryEntry entry) throws SQLException;

    /**
     * Retrieves all glossary entries belonging to a chapter.
     *
     * @param chapterId the parent chapter identifier (FR-GL-03)
     * @return list of entries for the chapter, possibly empty
     * @throws SQLException if a database error occurs
     */
    List<GlossaryEntry> getEntriesByChapterId(String chapterId) throws SQLException;

    /**
     * Retrieves a single glossary entry by its unique identifier.
     *
     * @param id the entry identifier
     * @return the entry, or {@code null} if not found
     * @throws SQLException if a database error occurs
     */
    GlossaryEntry getEntryById(String id) throws SQLException;

    /**
     * Updates an existing glossary entry's Persian word and Urdu meaning.
     *
     * @param entry the entry with updated values (FR-GL-04)
     * @throws SQLException if a database error occurs
     */
    void updateEntry(GlossaryEntry entry) throws SQLException;

    /**
     * Deletes a single glossary entry by its identifier.
     *
     * @param id the entry identifier (FR-GL-05)
     * @throws SQLException if a database error occurs
     */
    void deleteEntry(String id) throws SQLException;

    /**
     * Deletes all glossary entries belonging to a chapter.
     * Used when a chapter is deleted (FR-CH-07).
     *
     * @param chapterId the parent chapter identifier
     * @throws SQLException if a database error occurs
     */
    void deleteEntriesByChapterId(String chapterId) throws SQLException;
}
