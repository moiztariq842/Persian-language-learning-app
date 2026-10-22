package com.persianapp.dataaccesslayer.inmemory;

import com.persianapp.businesslayer.Chapter;
import com.persianapp.dataaccesslayer.ChapterDao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of ChapterDao (UC-01).
 * Requires no external database. Thread-safe for UI access.
 */
public class InMemoryChapterDao implements ChapterDao {

    private final Map<String, Chapter> store = new LinkedHashMap<>();

    @Override
    public synchronized void insert(Chapter chapter) {
        if (chapter != null) {
            store.put(chapter.getId(), chapter);
        }
    }

    @Override
    public synchronized List<Chapter> selectAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public synchronized Optional<Chapter> selectById(String chapterId) {
        return Optional.ofNullable(store.get(chapterId));
    }

    @Override
    public synchronized void updateTitle(Chapter chapter) {
        if (chapter != null && store.containsKey(chapter.getId())) {
            store.put(chapter.getId(), chapter);
        }
    }

    @Override
    public synchronized void deleteById(String chapterId) {
        store.remove(chapterId);
    }

    public synchronized void clear() {
        store.clear();
    }
}
