package com.persianapp.dataaccesslayer;

import com.persianapp.businesslayer.ChapterComponent;

import java.util.List;
import java.util.Optional;

/**
 * Data access contract for chapter components (UC-03 content editing).
 * UC-02 uses add(); UC-03 uses findByChapter(), findById(), and updateContent().
 */
public interface ComponentRepository {
    List<ChapterComponent> findByChapter(String chapterId);

    Optional<ChapterComponent> findById(String componentId);

    void add(ChapterComponent component);

    /**
     * @throws java.util.NoSuchElementException if no component has this id
     */
    void updateContent(String componentId, String newContent);
}
