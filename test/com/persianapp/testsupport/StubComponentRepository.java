package com.persianapp.testsupport;

import com.persianapp.businesslayer.ChapterComponent;
import com.persianapp.dataaccesslayer.ComponentRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/** Hand-written in-memory stub. Records writes so tests can prove invalid data was never saved. */
public class StubComponentRepository implements ComponentRepository {
    private final Map<String, ChapterComponent> components = new LinkedHashMap<>();
    private int updateCount;

    public StubComponentRepository(ChapterComponent... initial) {
        for (ChapterComponent c : initial) {
            components.put(c.getId(), c);
        }
    }

    @Override
    public List<ChapterComponent> findByChapter(String chapterId) {
        List<ChapterComponent> result = new ArrayList<>();
        for (ChapterComponent c : components.values()) {
            if (c.getChapterId().equals(chapterId)) {
                result.add(c);
            }
        }
        result.sort(Comparator.comparingInt(ChapterComponent::getPosition));
        return result;
    }

    @Override
    public Optional<ChapterComponent> findById(String componentId) {
        return Optional.ofNullable(components.get(componentId));
    }

    @Override
    public void add(ChapterComponent component) {
        components.put(component.getId(), component);
    }

    @Override
    public void updateContent(String componentId, String newContent) {
        ChapterComponent existing = components.get(componentId);
        if (existing == null) {
            throw new NoSuchElementException(componentId);
        }
        components.put(componentId, existing.withContent(newContent));
        updateCount++;
    }

    public int getUpdateCount() { return updateCount; }

    public ChapterComponent peek(String componentId) { return components.get(componentId); }
}
