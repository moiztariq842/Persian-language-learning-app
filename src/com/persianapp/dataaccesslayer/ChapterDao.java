package com.persianapp.dataaccesslayer;

import com.persianapp.businesslayer.Chapter;
import java.util.List;
import java.util.Optional;

// Data access operations for chapters (UC-01).
public interface ChapterDao {

    void insert(Chapter chapter);

    List<Chapter> selectAll();

    Optional<Chapter> selectById(String chapterId);

    void updateTitle(Chapter chapter);

    void deleteById(String chapterId);
}
