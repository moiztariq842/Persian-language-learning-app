package com.persianapp.presentationlayer;

import com.persianapp.businesslayer.Chapter;
import com.persianapp.businesslayer.ChapterService;
import java.util.List;

public class ChapterController {

    private final ChapterService chapterService;

    // Initializes the controller with the chapter service.
    public ChapterController(ChapterService chapterService) {
        if (chapterService == null) {
            throw new IllegalArgumentException("ChapterService must not be null.");
        }
        this.chapterService = chapterService;
    }

    // Creates a new chapter with the given title.
    public Chapter createChapter(String title) {
        return chapterService.createChapter(title);
    }

    // Renames an existing chapter by ID.
    public Chapter renameChapter(String chapterId, String newTitle) {
        return chapterService.renameChapter(chapterId, newTitle);
    }

    // Deletes a chapter by its ID.
    public void deleteChapter(String chapterId) {
        chapterService.deleteChapter(chapterId);
    }

    // Returns the list of all chapters ordered by creation.
    public List<Chapter> listChapters() {
        return chapterService.listChapters();
    }

    // Retrieves a chapter by its ID.
    public Chapter getChapter(String chapterId) {
        return chapterService.openChapter(chapterId);
    }
}
