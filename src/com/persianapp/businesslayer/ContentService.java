package com.persianapp.businesslayer;

import com.persianapp.dataaccesslayer.ComponentRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * UC-03 use-case logic: load a chapter's components in order and save validated content.
 * Invalid content is never passed to the data layer, so the last valid content is kept (BR-06).
 */
public class ContentService {
    private final ComponentRepository repository;
    private final ContentValidator validator;

    public ContentService(ComponentRepository repository, ContentValidator validator) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
        this.validator  = Objects.requireNonNull(validator,  "validator must not be null");
    }

    public List<ChapterComponent> loadComponents(String chapterId) {
        if (chapterId == null || chapterId.isBlank()) {
            throw new IllegalArgumentException("chapterId must not be blank");
        }
        return repository.findByChapter(chapterId);
    }

    public ChapterComponent loadComponent(String componentId) {
        return repository.findById(componentId)
                .orElseThrow(() -> new NoSuchElementException("No component with id " + componentId));
    }

    public ChapterComponent saveContent(String componentId, String newContent) {
        ChapterComponent current = loadComponent(componentId);
        ValidationResult result  = validator.validate(newContent);
        if (!result.isValid()) {
            throw new ContentValidationException(result);
        }
        repository.updateContent(componentId, newContent);
        return current.withContent(newContent);
    }
}
