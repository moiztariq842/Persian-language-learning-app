package com.persianapp.presentationlayer;

import com.persianapp.businesslayer.Chapter;
import com.persianapp.businesslayer.ChapterService;
import com.persianapp.businesslayer.stubs.InMemoryChapterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ChapterController — UC-01 Presentation Controller Tests")
class ChapterControllerTest {

    private ChapterService service;
    private ChapterController controller;

    @BeforeEach
    void setUp() {
        InMemoryChapterRepository repo = new InMemoryChapterRepository();
        service    = new ChapterService(repo);
        controller = new ChapterController(service);
    }

    @Test
    @DisplayName("Create chapter through controller returns valid chapter")
    void createChapter_validTitle_returnsChapter() {
        Chapter c = controller.createChapter("درس اول: سلام");
        assertNotNull(c.getId());
        assertEquals("درس اول: سلام", c.getTitle());
    }

    @Test
    @DisplayName("List chapters returns all created chapters in order")
    void listChapters_returnsAll() {
        controller.createChapter("Chapter 1");
        controller.createChapter("Chapter 2");

        List<Chapter> list = controller.listChapters();
        assertEquals(2, list.size());
        assertEquals("Chapter 1", list.get(0).getTitle());
        assertEquals("Chapter 2", list.get(1).getTitle());
    }

    @Test
    @DisplayName("Rename chapter updates chapter title successfully")
    void renameChapter_validNewTitle_updatesTitle() {
        Chapter c = controller.createChapter("Old Title");
        Chapter updated = controller.renameChapter(c.getId(), "New Title");

        assertEquals("New Title", updated.getTitle());
        assertEquals("New Title", controller.getChapter(c.getId()).getTitle());
    }

    @Test
    @DisplayName("Delete chapter removes chapter from controller listing")
    void deleteChapter_existingId_removesChapter() {
        Chapter c = controller.createChapter("To Delete");
        controller.deleteChapter(c.getId());

        assertEquals(0, controller.listChapters().size());
    }

    @Test
    @DisplayName("Null service throws IllegalArgumentException")
    void constructor_nullService_throws() {
        assertThrows(IllegalArgumentException.class, () -> new ChapterController(null));
    }
}
