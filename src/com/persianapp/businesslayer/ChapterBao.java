package com.persianapp.businesslayer;

import com.persianapp.dataaccesslayer.ChapterDao;
import java.util.List;
import java.util.UUID;

// Business access object for chapter management.
public class ChapterBao implements ChapterLookup {

    private final ChapterDao dao;

    // Initializes the BAO with the chapter DAO.
    public ChapterBao(ChapterDao dao) {
        if (dao == null) {
            throw new IllegalArgumentException("ChapterDao is required.");
        }
        this.dao = dao;
    }

    // Creates a new chapter with a generated ID.
    public Chapter createChapter(String title) {
        validateTitle(title);
        Chapter chapter = new Chapter(UUID.randomUUID().toString(), title);
        dao.insert(chapter);
        return chapter;
    }

    // Returns all chapters.
    public List<Chapter> getAllChapters() {
        return dao.selectAll();
    }

    // Finds a chapter by ID.
    public Chapter findChapter(String chapterId) {
        return dao.selectById(chapterId)
            .orElseThrow(() -> new ChapterException(
                ChapterErrorCode.CHAPTER_NOT_FOUND,
                "Chapter not found: " + chapterId));
    }

    // Renames an existing chapter.
    public Chapter renameChapter(String chapterId, String newTitle) {
        validateTitle(newTitle);
        Chapter chapter = findChapter(chapterId);
        chapter.setTitle(newTitle);
        dao.updateTitle(chapter);
        return chapter;
    }

    // Deletes a chapter by ID.
    public void deleteChapter(String chapterId) {
        requireExists(chapterId);
        dao.deleteById(chapterId);
    }

    // Validates that title is not null or blank.
    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new ChapterException(ChapterErrorCode.BLANK_TITLE,
                "Chapter title must not be blank.");
        }
    }

    // Ensures the chapter exists before performing an action.
    private void requireExists(String chapterId) {
        if (chapterId == null || chapterId.isBlank()) {
            throw new ChapterException(ChapterErrorCode.CHAPTER_NOT_FOUND,
                "Chapter ID must not be blank.");
        }
        findChapter(chapterId);
    }

    // Checks whether a chapter with the given ID exists.
    @Override
    public boolean exists(String chapterId) {
        if (chapterId == null || chapterId.isBlank()) {
            return false;
        }
        return dao.selectById(chapterId).isPresent();
    }
}
