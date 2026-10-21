package com.persianapp.businesslayer.stubs;

import com.persianapp.businesslayer.Chapter;
import com.persianapp.dataaccesslayer.ChapterRepository;
import java.util.*;

public class InMemoryChapterRepository implements ChapterRepository {

    final Map<String, Chapter> store = new LinkedHashMap<>();

    // Saves a chapter in memory.
    @Override
    public void save(Chapter chapter) {
        store.put(chapter.getId(), chapter);
    }

    // Returns all stored chapters.
    @Override
    public List<Chapter> findAll() {
        return new ArrayList<>(store.values());
    }

    // Finds a chapter by ID in memory.
    @Override
    public Optional<Chapter> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    // Updates a chapter in memory.
    @Override
    public void update(Chapter chapter) {
        store.put(chapter.getId(), chapter);
    }

    // Deletes a chapter by ID from memory.
    @Override
    public void deleteById(String id) {
        store.remove(id);
    }

    // Returns the total number of stored chapters.
    public int size() {
        return store.size();
    }
}
