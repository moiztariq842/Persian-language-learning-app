package com.persianapp.businesslayer;

import com.persianapp.dataaccesslayer.ChapterRepository;
import java.util.List;

public class ChapterService {

    private final ChapterRepository repository;

    // Initializes the service with a chapter repository.
    public ChapterService(ChapterRepository repository) {
        this.repository = repository;
    }

    // Creates and saves a new chapter.
    public Chapter createChapter(String title) {
        ChapterValidator.validateTitle(title);
        Chapter chapter = new Chapter(title);
        repository.save(chapter);
        return chapter;
    }

    // Returns all chapters.
    public List<Chapter> listChapters() {
        return repository.findAll();
    }

    // Finds a chapter by its ID.
    public Chapter openChapter(String chapterId) {
        return repository.findById(chapterId)
            .orElseThrow(() ->
                new IllegalArgumentException("Chapter not found: " + chapterId));
    }

    // Renames an existing chapter.
    public Chapter renameChapter(String chapterId, String newTitle) {
        ChapterValidator.validateTitle(newTitle);
        Chapter chapter = openChapter(chapterId);
        chapter.setTitle(newTitle);
        repository.update(chapter);
        return chapter;
    }

    // Deletes a chapter by its ID.
    public void deleteChapter(String chapterId) {
        repository.deleteById(chapterId);
    }
}
