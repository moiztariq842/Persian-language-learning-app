package com.persianapp.businesslayer;

import java.util.Optional;

/**
 * Storage contract for chapter structures (UC-02).
 * Implemented in the data access layer; the business layer depends only on this interface.
 */
public interface StructureDao {

    /**
     * Returns the saved structure of the given chapter,
     * or empty if none has been saved for it yet.
     */
    Optional<ChapterStructure> findByChapterId(String chapterId);

    /**
     * Saves the structure, replacing any previously saved structure for the same chapter
     * (selection and order are stored together, FR-CS-06).
     */
    void save(ChapterStructure structure);
}