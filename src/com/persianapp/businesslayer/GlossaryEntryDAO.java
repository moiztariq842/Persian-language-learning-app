package com.persianapp.businesslayer;

import java.util.List;

/**
 * Data Access Object interface for glossary entry persistence operations (UC-04).
 * Lives in the business layer so the dependency points inward (DIP).
 * All methods throw GlossaryException instead of SQLException.
 */
public interface GlossaryEntryDAO {

    void addEntry(GlossaryEntry entry) throws GlossaryException;

    List<GlossaryEntry> getEntriesByChapterId(String chapterId) throws GlossaryException;

    GlossaryEntry getEntryById(String id) throws GlossaryException;

    boolean updateEntry(GlossaryEntry entry) throws GlossaryException;

    void deleteEntry(String id) throws GlossaryException;

    void deleteEntriesByChapterId(String chapterId) throws GlossaryException;

    boolean chapterExists(String chapterId) throws GlossaryException;
}
