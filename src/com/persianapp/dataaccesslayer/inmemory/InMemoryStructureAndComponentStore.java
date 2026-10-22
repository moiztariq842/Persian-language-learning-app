package com.persianapp.dataaccesslayer.inmemory;

import com.persianapp.businesslayer.ChapterComponent;
import com.persianapp.businesslayer.ChapterStructure;
import com.persianapp.businesslayer.ComponentType;
import com.persianapp.businesslayer.StructureDao;
import com.persianapp.dataaccesslayer.ComponentRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

/**
 * Unified in-memory store for UC-02 (Chapter Structure) and UC-03 (Component Content).
 * Implements both StructureDao and ComponentRepository so changes in structure
 * immediately reflect in content editing and vice-versa, without requiring MySQL.
 */
public class InMemoryStructureAndComponentStore implements StructureDao, ComponentRepository {

    // componentId -> ChapterComponent
    private final Map<String, ChapterComponent> components = new LinkedHashMap<>();

    // ── StructureDao methods (UC-02) ──────────────────────────

    @Override
    public synchronized Optional<ChapterStructure> findByChapterId(String chapterId) {
        List<ChapterComponent> chapterComps = findByChapter(chapterId);
        if (chapterComps.isEmpty()) {
            return Optional.empty();
        }
        List<ComponentType> types = new ArrayList<>();
        for (ChapterComponent c : chapterComps) {
            types.add(c.getType());
        }
        return Optional.of(new ChapterStructure(chapterId, types));
    }

    @Override
    public synchronized void save(ChapterStructure structure) {
        String chapterId = structure.getChapterId();
        List<ComponentType> newTypes = structure.getComponents();

        // Collect existing components for this chapter to preserve typed content
        Map<ComponentType, String> existingContent = new LinkedHashMap<>();
        for (ChapterComponent c : findByChapter(chapterId)) {
            existingContent.put(c.getType(), c.getContent());
        }

        // Remove old components for this chapter
        components.entrySet().removeIf(e -> e.getValue().getChapterId().equals(chapterId));

        // Re-create components according to the new structure order
        int position = 0;
        for (ComponentType type : newTypes) {
            String savedContent = existingContent.getOrDefault(type, "");
            String id = UUID.randomUUID().toString();
            ChapterComponent comp = new ChapterComponent(id, chapterId, type, position++, savedContent);
            components.put(id, comp);
        }
    }

    // ── ComponentRepository methods (UC-03) ───────────────────

    @Override
    public synchronized List<ChapterComponent> findByChapter(String chapterId) {
        List<ChapterComponent> list = new ArrayList<>();
        for (ChapterComponent c : components.values()) {
            if (c.getChapterId().equals(chapterId)) {
                list.add(c);
            }
        }
        list.sort(Comparator.comparingInt(ChapterComponent::getPosition));
        return list;
    }

    @Override
    public synchronized Optional<ChapterComponent> findById(String componentId) {
        return Optional.ofNullable(components.get(componentId));
    }

    @Override
    public synchronized void add(ChapterComponent component) {
        if (component != null) {
            components.put(component.getId(), component);
        }
    }

    @Override
    public synchronized void updateContent(String componentId, String newContent) {
        ChapterComponent existing = components.get(componentId);
        if (existing == null) {
            throw new NoSuchElementException("Component not found: " + componentId);
        }
        components.put(componentId, existing.withContent(newContent));
    }

    public synchronized void removeByChapter(String chapterId) {
        components.entrySet().removeIf(e -> e.getValue().getChapterId().equals(chapterId));
    }
}
