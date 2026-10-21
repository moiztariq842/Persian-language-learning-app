package com.persianapp.businesslayer;

import java.util.Objects;

/**
 * One configured component of a chapter. Immutable: changing content returns a new instance.
 */
public final class ChapterComponent {
    private final String id;
    private final String chapterId;
    private final ComponentType type;
    private final int position;
    private final String content;

    public ChapterComponent(String id, String chapterId, ComponentType type, int position, String content) {
        this.id = requireText(id, "id");
        this.chapterId = requireText(chapterId, "chapterId");
        this.type = Objects.requireNonNull(type, "type must not be null");
        if (position < 0) {
            throw new IllegalArgumentException("position must not be negative");
        }
        this.position = position;
        this.content = content == null ? "" : content;
    }

    public ChapterComponent withContent(String newContent) {
        return new ChapterComponent(id, chapterId, type, position, newContent);
    }

    public String getId() { return id; }
    public String getChapterId() { return chapterId; }
    public ComponentType getType() { return type; }
    public int getPosition() { return position; }
    public String getContent() { return content; }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
    @Override
    public String toString() {
        String name = type.name().replace('_', ' ').toLowerCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1) + " (" + (position + 1) + ")";
    }
}
