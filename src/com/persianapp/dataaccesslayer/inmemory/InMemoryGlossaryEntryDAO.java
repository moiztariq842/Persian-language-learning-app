package com.persianapp.dataaccesslayer.inmemory;

import com.persianapp.businesslayer.ChapterLookup;
import com.persianapp.businesslayer.GlossaryEntry;
import com.persianapp.businesslayer.GlossaryEntryDAO;
import com.persianapp.businesslayer.GlossaryException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * In-memory implementation of GlossaryEntryDAO (UC-04).
 * Requires no external database. Thread-safe for UI access.
 */
public class InMemoryGlossaryEntryDAO implements GlossaryEntryDAO {

    private final Map<String, GlossaryEntry> store = new LinkedHashMap<>();
    private final ChapterLookup chapterLookup;

    public InMemoryGlossaryEntryDAO(ChapterLookup chapterLookup) {
        this.chapterLookup = chapterLookup;
    }

    @Override
    public synchronized void addEntry(GlossaryEntry entry) throws GlossaryException {
        if (store.containsKey(entry.getId())) {
            throw new GlossaryException("Duplicate entry ID: " + entry.getId());
        }
        store.put(entry.getId(), new GlossaryEntry(
                entry.getId(), entry.getChapterId(),
                entry.getPersianWord(), entry.getUrduMeaning()));
    }

    @Override
    public synchronized List<GlossaryEntry> getEntriesByChapterId(String chapterId) {
        return store.values().stream()
                .filter(e -> e.getChapterId().equals(chapterId))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized GlossaryEntry getEntryById(String id) {
        GlossaryEntry e = store.get(id);
        if (e == null) return null;
        return new GlossaryEntry(e.getId(), e.getChapterId(),
                e.getPersianWord(), e.getUrduMeaning());
    }

    @Override
    public synchronized boolean updateEntry(GlossaryEntry entry) {
        if (!store.containsKey(entry.getId())) {
            return false;
        }
        store.put(entry.getId(), new GlossaryEntry(
                entry.getId(), entry.getChapterId(),
                entry.getPersianWord(), entry.getUrduMeaning()));
        return true;
    }

    @Override
    public synchronized void deleteEntry(String id) {
        store.remove(id);
    }

    @Override
    public synchronized void deleteEntriesByChapterId(String chapterId) {
        store.entrySet().removeIf(e -> e.getValue().getChapterId().equals(chapterId));
    }

    @Override
    public synchronized boolean chapterExists(String chapterId) {
        return chapterLookup != null && chapterLookup.exists(chapterId);
    }

    public synchronized int size() {
        return store.size();
    }
}
