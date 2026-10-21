package com.persianapp.dataaccesslayer;

import com.persianapp.businesslayer.Chapter;
import java.util.List;
import java.util.Optional;

// Data access operations for chapters.
public interface ChapterDao {

    // Inserts a new chapter.
    void insert(Chapter chapter);

    // Returns all chapters.
    List<Chapter> selectAll();

    // Finds a chapter by ID.
    Optional<Chapter> selectById(String chapterId);

    // Updates an existing chapter title.
    void updateTitle(Chapter chapter);

    // Deletes a chapter by ID.
    void deleteById(String chapterId);
}
