package com.persianapp.businesslayer.stubs;

import com.persianapp.businesslayer.Chapter;
import com.persianapp.dataaccesslayer.ChapterDao;
import java.util.*;

// In-memory chapter DAO for testing.
public class InMemoryChapterDao implements ChapterDao {

    private final Map<String, Chapter> store = new LinkedHashMap<>();

    @Override
    public void insert(Chapter chapter) {
        store.put(chapter.getId(), chapter);
    }

    @Override
    public List<Chapter> selectAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Chapter> selectById(String chapterId) {
        return Optional.ofNullable(store.get(chapterId));
    }

    @Override
    public void updateTitle(Chapter chapter) {
        store.put(chapter.getId(), chapter);
    }

    @Override
    public void deleteById(String chapterId) {
        store.remove(chapterId);
    }
}
