package com.persianapp.presentationlayer;

import com.persianapp.businesslayer.ChapterBao;
import com.persianapp.businesslayer.stubs.InMemoryChapterDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ChapterController Tests")
class ChapterControllerTest {

    private ChapterController controller;

    @BeforeEach
    void setUp() {
        ChapterBao bao = new ChapterBao(new InMemoryChapterDao());
        controller = new ChapterController(bao);
    }

    @Test
    @DisplayName("Create chapter with valid title returns success")
    void createChapter_validTitle_returnsSuccess() {
        ActionResult result = controller.createChapter("درس اول: سلام");
        assertTrue(result.isSuccess());
        assertEquals(ChapterMessages.CREATED, result.getMessage());
    }

    @Test
    @DisplayName("Create chapter with blank title returns error")
    void createChapter_blankTitle_returnsError() {
        ActionResult result = controller.createChapter("  ");
        assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
    }

    @Test
    @DisplayName("List chapters returns all created chapters")
    void listChapters_returnsAll() {
        controller.createChapter("Chapter 1");
        controller.createChapter("Chapter 2");
        List<?> list = controller.listChapters();
        assertEquals(2, list.size());
    }

    @Test
    @DisplayName("Rename chapter with valid title returns success")
    void renameChapter_validTitle_returnsSuccess() {
        controller.createChapter("Old Title");
        String id = controller.listChapters().get(0).getId();
        ActionResult result = controller.renameChapter(id, "New Title");
        assertTrue(result.isSuccess());
        assertEquals(ChapterMessages.RENAMED, result.getMessage());
    }

    @Test
    @DisplayName("Rename chapter with blank title returns error")
    void renameChapter_blankTitle_returnsError() {
        controller.createChapter("Stable");
        String id = controller.listChapters().get(0).getId();
        ActionResult result = controller.renameChapter(id, "");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("Delete existing chapter returns success")
    void deleteChapter_existingId_returnsSuccess() {
        controller.createChapter("To Delete");
        String id = controller.listChapters().get(0).getId();
        ActionResult result = controller.deleteChapter(id);
        assertTrue(result.isSuccess());
        assertEquals(ChapterMessages.DELETED, result.getMessage());
        assertEquals(0, controller.listChapters().size());
    }

    @Test
    @DisplayName("chapterExists returns true for valid ID and false for unknown ID")
    void chapterExists_correctResponse() {
        controller.createChapter("Exists");
        String id = controller.listChapters().get(0).getId();
        assertTrue(controller.chapterExists(id));
        assertFalse(controller.chapterExists("ghost-id"));
    }

    @Test
    @DisplayName("Null BAO in constructor throws IllegalArgumentException")
    void constructor_nullBao_throws() {
        assertThrows(IllegalArgumentException.class, () -> new ChapterController(null));
    }
}
