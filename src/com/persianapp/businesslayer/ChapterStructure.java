package com.persianapp.businesslayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The ordered set of components selected for one chapter (UC-02).
 * Immutable: every change returns a new instance, so a rejected change
 * can never alter the last valid structure (BR-06).
 */
public final class ChapterStructure {

    private final String chapterId;
    private final List<ComponentType> components;

    public ChapterStructure(String chapterId, List<ComponentType> components) {
        if (chapterId == null || chapterId.trim().isEmpty()) {
            throw new IllegalArgumentException("Chapter id must not be blank.");
        }
        if (components == null) {
            throw new IllegalArgumentException("Component list must not be null.");
        }
        Set<ComponentType> seen = new HashSet<>();
        for (ComponentType type : components) {
            if (type == null) {
                throw new IllegalArgumentException("Component must not be null.");
            }
            if (!seen.add(type)) {
                throw new IllegalArgumentException(
                        "Component " + type + " is selected more than once.");
            }
        }
        this.chapterId = chapterId;
        this.components = Collections.unmodifiableList(new ArrayList<>(components));
    }

    public static ChapterStructure empty(String chapterId) {
        return new ChapterStructure(chapterId, Collections.<ComponentType>emptyList());
    }

    public String getChapterId() {
        return chapterId;
    }

    /** Components in the administrator's chosen order (unmodifiable). */
    public List<ComponentType> getComponents() {
        return components;
    }

    public boolean contains(ComponentType type) {
        return components.contains(type);
    }

    public boolean isEmpty() {
        return components.isEmpty();
    }

    public ChapterStructure withAdded(ComponentType type) {
        List<ComponentType> updated = new ArrayList<>(components);
        updated.add(type);
        return new ChapterStructure(chapterId, updated);
    }

    public ChapterStructure withRemoved(ComponentType type) {
        if (!components.contains(type)) {
            throw new IllegalArgumentException("Component " + type + " is not in this chapter.");
        }
        List<ComponentType> updated = new ArrayList<>(components);
        updated.remove(type);
        return new ChapterStructure(chapterId, updated);
    }

    /** Moves a selected component to a new zero-based position. */
    public ChapterStructure withMoved(ComponentType type, int newIndex) {
        if (!components.contains(type)) {
            throw new IllegalArgumentException("Component " + type + " is not in this chapter.");
        }
        if (newIndex < 0 || newIndex >= components.size()) {
            throw new IllegalArgumentException("Position " + newIndex + " is out of range.");
        }
        List<ComponentType> updated = new ArrayList<>(components);
        updated.remove(type);
        updated.add(newIndex, type);
        return new ChapterStructure(chapterId, updated);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ChapterStructure)) {
            return false;
        }
        ChapterStructure that = (ChapterStructure) other;
        return chapterId.equals(that.chapterId) && components.equals(that.components);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chapterId, components);
    }
}