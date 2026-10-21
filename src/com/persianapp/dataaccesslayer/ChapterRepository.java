package com.persianapp.dataaccesslayer;

import com.persianapp.businesslayer.Chapter;
import java.util.List;
import java.util.Optional;

public interface ChapterRepository {

    // Saves a chapter to the database.
    void save(Chapter chapter);

    // Returns all chapters.
    List<Chapter> findAll();

    // Finds a chapter by its ID.
    Optional<Chapter> findById(String chapterId);

    // Updates an existing chapter in the database.
    void update(Chapter chapter);

    // Deletes a chapter by its ID.
    void deleteById(String chapterId);
}
