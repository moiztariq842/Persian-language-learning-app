package com.persianapp.businesslayer;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory stub implementation of {@link GlossaryEntryDAO} for unit testing.
 *
 * <p>Uses a {@link LinkedHashMap} to preserve insertion order (simulates
 * {@code ORDER BY created_at ASC}). No Mockito required (NFR-MT-03).</p>
 */
public class InMemoryGlossaryEntryDAO implements GlossaryEntryDAO {

    private final Map<String, GlossaryEntry> store = new LinkedHashMap<>();
    private final Set<String> chapterIds = new HashSet<>();

    /**
     * Registers a chapter ID so that {@link #chapterExists} returns {@code true}.
     *
     * @param chapterId the chapter ID to register
     */
    public void addChapter(String chapterId) {
        chapterIds.add(chapterId);
    }

    @Override
    public void addEntry(GlossaryEntry entry) throws GlossaryException {
        if (store.containsKey(entry.getId())) {
            throw new GlossaryException("Duplicate entry ID: " + entry.getId());
        }
        store.put(entry.getId(), new GlossaryEntry(
                entry.getId(), entry.getChapterId(),
                entry.getPersianWord(), entry.getUrduMeaning()));
    }

    @Override
    public List<GlossaryEntry> getEntriesByChapterId(String chapterId) {
        return store.values().stream()
                .filter(e -> e.getChapterId().equals(chapterId))
                .collect(Collectors.toList());
    }

    @Override
    public GlossaryEntry getEntryById(String id) {
        GlossaryEntry e = store.get(id);
        if (e == null) return null;
        return new GlossaryEntry(e.getId(), e.getChapterId(),
                e.getPersianWord(), e.getUrduMeaning());
    }

    @Override
    public boolean updateEntry(GlossaryEntry entry) {
        if (!store.containsKey(entry.getId())) {
            return false;
        }
        store.put(entry.getId(), new GlossaryEntry(
                entry.getId(), entry.getChapterId(),
                entry.getPersianWord(), entry.getUrduMeaning()));
        return true;
    }

    @Override
    public void deleteEntry(String id) {
        store.remove(id);
    }

    @Override
    public void deleteEntriesByChapterId(String chapterId) {
        store.entrySet().removeIf(e -> e.getValue().getChapterId().equals(chapterId));
    }

    @Override
    public boolean chapterExists(String chapterId) {
        return chapterIds.contains(chapterId);
    }

    /**
     * Returns the current number of entries in the store.
     */
    public int size() {
        return store.size();
    }

    /**
     * Clears all entries and chapters.
     */
    public void clear() {
        store.clear();
        chapterIds.clear();
    }
}
