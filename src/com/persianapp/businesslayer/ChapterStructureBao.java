package com.persianapp.businesslayer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Business logic for UC-02: configure the structure of a chapter.
 * Dependencies are injected through the constructor.
 */
public class ChapterStructureBao {

    private final StructureDao dao;
    private final ChapterLookup chapterLookup;

    public ChapterStructureBao(StructureDao dao, ChapterLookup chapterLookup) {
        if (dao == null || chapterLookup == null) {
            throw new IllegalArgumentException("Dao and chapter lookup are required.");
        }
        this.dao           = dao;
        this.chapterLookup = chapterLookup;
    }

    /** The components an administrator can choose from (FR-CS-01). */
    public List<ComponentType> getSupportedComponents() {
        return Collections.unmodifiableList(Arrays.asList(ComponentType.values()));
    }

    /** The saved structure of a chapter, or an empty one if none is saved yet. */
    public ChapterStructure getStructure(String chapterId) {
        requireExistingChapter(chapterId);
        return dao.findByChapterId(chapterId)
                .orElse(ChapterStructure.empty(chapterId));
    }

    /**
     * Validates and saves the selected components in the given order.
     * An empty selection is allowed and clears the chapter's structure.
     * Validation runs before anything is stored, so a rejected request
     * leaves the last valid structure untouched (BR-06, NFR-RL-02).
     */
    public ChapterStructure saveStructure(String chapterId, List<ComponentType> components) {
        requireExistingChapter(chapterId);
        ChapterStructure structure = new ChapterStructure(chapterId, components);
        dao.save(structure);
        return structure;
    }

    private void requireExistingChapter(String chapterId) {
        if (chapterId == null || chapterId.trim().isEmpty()) {
            throw new StructureException(StructureErrorCode.BLANK_CHAPTER_ID,
                    "Chapter id must not be blank.");
        }
        if (!chapterLookup.exists(chapterId)) {
            throw new StructureException(StructureErrorCode.CHAPTER_NOT_FOUND,
                    "Chapter " + chapterId + " does not exist.");
        }
    }
}
