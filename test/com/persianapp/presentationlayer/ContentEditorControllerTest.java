package com.persianapp.presentationlayer;

import com.persianapp.businesslayer.ChapterComponent;
import com.persianapp.businesslayer.ComponentType;
import com.persianapp.businesslayer.ContentService;
import com.persianapp.businesslayer.ContentValidator;
import com.persianapp.testsupport.StubComponentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UC-03: ContentEditorController")
class ContentEditorControllerTest {
    private static final String CHAPTER = "chapter-1";

    private static ChapterComponent dialogue() {
        return new ChapterComponent("c1", CHAPTER, ComponentType.PERSIAN_DIALOGUE, 0, "سلام");
    }

    private static ContentEditorController controllerWith(StubComponentRepository repo) {
        return new ContentEditorController(new ContentService(repo, new ContentValidator()));
    }

    @Test
    @DisplayName("Selecting a component and saving valid text succeeds")
    void selectThenSaveSucceeds() {
        StubComponentRepository repo = new StubComponentRepository(dialogue());
        ContentEditorController controller = controllerWith(repo);

        controller.selectComponent("c1");
        ContentEditorController.EditResult result = controller.saveEdit("خوش آمدید");

        assertTrue(result.isSaved());
        assertEquals("Content saved.", result.getMessage());
        assertEquals("خوش آمدید", repo.peek("c1").getContent());
    }

    @Test
    @DisplayName("A rejected edit keeps the typed text and the previous saved content")
    void rejectedEditKeepsTextAndPreviousContent() {
        StubComponentRepository repo = new StubComponentRepository(dialogue());
        ContentEditorController controller = controllerWith(repo);
        controller.selectComponent("c1");

        ContentEditorController.EditResult result = controller.saveEdit("   ");

        assertFalse(result.isSaved());
        assertTrue(result.getMessage().startsWith("Component content:"));
        assertEquals("   ", result.getEnteredText());
        assertEquals("سلام", repo.peek("c1").getContent());
    }

    @Test
    @DisplayName("Saving without a selected component is not allowed")
    void saveWithoutSelectionFails() {
        ContentEditorController controller = controllerWith(new StubComponentRepository(dialogue()));
        assertThrows(IllegalStateException.class, () -> controller.saveEdit("متن"));
    }

    @Test
    @DisplayName("Loading components clears the previous selection")
    void loadClearsSelection() {
        ContentEditorController controller = controllerWith(new StubComponentRepository(dialogue()));
        controller.selectComponent("c1");

        controller.loadComponents(CHAPTER);

        assertNull(controller.getSelected());
    }
}
