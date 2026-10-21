package com.persianapp.presentationlayer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.persianapp.businesslayer.ChapterLookup;
import com.persianapp.businesslayer.ChapterStructure;
import com.persianapp.businesslayer.ChapterStructureBao;
import com.persianapp.businesslayer.ComponentType;
import com.persianapp.businesslayer.DataAccessException;
import com.persianapp.businesslayer.StructureDao;
import com.persianapp.businesslayer.StructureErrorCode;

@DisplayName("UC-02: StructureController")
class StructureControllerTest {

    private static final String CHAPTER = "ch-1";

    private StubStructureDao dao;
    private StructureController controller;

    @BeforeEach
    void setUp() {
        dao = new StubStructureDao();
        ChapterStructureBao bao = new ChapterStructureBao(dao, new StubChapterLookup(CHAPTER));
        controller = new StructureController(bao);
    }

    @Test
    @DisplayName("Lists all seven available components")
    void listsAvailableComponents() {
        assertEquals(7, controller.getAvailableComponents().size());
    }

    @Test
    @DisplayName("Loads an empty selection for a chapter with no saved structure")
    void loadsEmptyWhenNothingSaved() {
        assertTrue(controller.loadSelected(CHAPTER).isEmpty());
    }

    @Test
    @DisplayName("Saves a valid selection and reports success")
    void savesValidSelection() {
        List<ComponentType> selection = Arrays.asList(
                ComponentType.PERSIAN_DIALOGUE, ComponentType.GLOSSARY);
        ActionResult result = controller.save(CHAPTER, selection);
        assertTrue(result.isSuccess());
        assertEquals(selection, controller.loadSelected(CHAPTER));
    }

    @Test
    @DisplayName("Saves an empty selection and clears the saved structure")
    void savesEmptySelectionAndClearsStructure() {
        controller.save(CHAPTER, Arrays.asList(ComponentType.EXAMPLES));

        ActionResult result = controller.save(CHAPTER, Collections.<ComponentType>emptyList());

        assertTrue(result.isSuccess());
        assertTrue(controller.loadSelected(CHAPTER).isEmpty());
    }

    @Test
    @DisplayName("Reports an error for a duplicate component and keeps the last valid structure")
    void reportsErrorForDuplicate() {
        List<ComponentType> valid = Arrays.asList(ComponentType.EXAMPLES);
        controller.save(CHAPTER, valid);

        ActionResult result = controller.save(CHAPTER,
                Arrays.asList(ComponentType.GLOSSARY, ComponentType.GLOSSARY));

        assertFalse(result.isSuccess());
        assertEquals(valid, controller.loadSelected(CHAPTER));
    }

    @Test
    @DisplayName("Reports an error for a chapter that does not exist")
    void reportsErrorForUnknownChapter() {
        ActionResult result = controller.save("missing", Arrays.asList(ComponentType.EXAMPLES));
        assertFalse(result.isSuccess());
        assertEquals(0, dao.saveCount);
    }

    @Test
    @DisplayName("moveUp moves a component one position earlier")
    void moveUpMovesEarlier() {
        List<ComponentType> current = Arrays.asList(
                ComponentType.LEARNING_OBJECTIVES, ComponentType.PERSIAN_DIALOGUE, ComponentType.GLOSSARY);
        assertEquals(Arrays.asList(
                ComponentType.LEARNING_OBJECTIVES, ComponentType.GLOSSARY, ComponentType.PERSIAN_DIALOGUE),
                controller.moveUp(current, ComponentType.GLOSSARY));
    }

    @Test
    @DisplayName("moveDown moves a component one position later")
    void moveDownMovesLater() {
        List<ComponentType> current = Arrays.asList(
                ComponentType.LEARNING_OBJECTIVES, ComponentType.PERSIAN_DIALOGUE, ComponentType.GLOSSARY);
        assertEquals(Arrays.asList(
                ComponentType.PERSIAN_DIALOGUE, ComponentType.LEARNING_OBJECTIVES, ComponentType.GLOSSARY),
                controller.moveDown(current, ComponentType.LEARNING_OBJECTIVES));
    }

    @Test
    @DisplayName("Moving the first item up or the last item down changes nothing")
    void movingPastTheEndsChangesNothing() {
        List<ComponentType> current = Arrays.asList(
                ComponentType.LEARNING_OBJECTIVES, ComponentType.GLOSSARY);
        assertEquals(current, controller.moveUp(current, ComponentType.LEARNING_OBJECTIVES));
        assertEquals(current, controller.moveDown(current, ComponentType.GLOSSARY));
    }

    @Test
    @DisplayName("Moving a component that is not in the list changes nothing")
    void movingUnknownComponentChangesNothing() {
        List<ComponentType> current = Arrays.asList(ComponentType.LEARNING_OBJECTIVES);
        assertEquals(current, controller.moveUp(current, ComponentType.EXAMPLES));
        assertEquals(current, controller.moveDown(current, ComponentType.EXAMPLES));
    }

    @Test
    @DisplayName("Moving does not modify the list that was passed in")
    void movingDoesNotModifyInput() {
        List<ComponentType> current = Arrays.asList(
                ComponentType.LEARNING_OBJECTIVES, ComponentType.GLOSSARY);
        controller.moveDown(current, ComponentType.LEARNING_OBJECTIVES);
        assertEquals(Arrays.asList(ComponentType.LEARNING_OBJECTIVES, ComponentType.GLOSSARY), current);
    }

    @Test
    @DisplayName("Requires a business object")
    void requiresBao() {
        assertThrows(IllegalArgumentException.class, () -> new StructureController(null));
    }

    @Test
    @DisplayName("Reports the Urdu message that matches the error code")
    void reportsUrduMessageForErrorCode() {
        ActionResult result = controller.save(CHAPTER,
                Arrays.asList(ComponentType.GLOSSARY, ComponentType.GLOSSARY));
        assertEquals(StructureMessages.forCode(StructureErrorCode.DUPLICATE_COMPONENT), result.getMessage());
    }

    @Test
    @DisplayName("Reports the Urdu success message after saving")
    void reportsUrduSuccessMessage() {
        ActionResult result = controller.save(CHAPTER, Arrays.asList(ComponentType.EXAMPLES));
        assertEquals(StructureMessages.SAVED, result.getMessage());
    }

    @Test
    @DisplayName("Reports a storage failure while saving as an Urdu error instead of throwing")
    void reportsStorageFailureOnSave() {
        StructureController failing = new StructureController(
                new ChapterStructureBao(new FailingStructureDao(), new StubChapterLookup(CHAPTER)));

        ActionResult result = failing.save(CHAPTER, Arrays.asList(ComponentType.EXAMPLES));

        assertFalse(result.isSuccess());
        assertEquals(StructureMessages.STORAGE_FAILED, result.getMessage());
    }

    @Test
    @DisplayName("Loading lets a storage failure reach the screen")
    void loadPassesStorageFailureOn() {
        StructureController failing = new StructureController(
                new ChapterStructureBao(new FailingStructureDao(), new StubChapterLookup(CHAPTER)));

        assertThrows(DataAccessException.class, () -> failing.loadSelected(CHAPTER));
    }

    // ---- hand-written stubs (no mocking framework) ----

    private static class StubStructureDao implements StructureDao {
        private final Map<String, ChapterStructure> stored = new HashMap<>();
        int saveCount = 0;

        @Override
        public Optional<ChapterStructure> findByChapterId(String chapterId) {
            return Optional.ofNullable(stored.get(chapterId));
        }

        @Override
        public void save(ChapterStructure structure) {
            saveCount++;
            stored.put(structure.getChapterId(), structure);
        }
    }

    private static class FailingStructureDao implements StructureDao {
        @Override
        public Optional<ChapterStructure> findByChapterId(String chapterId) {
            throw new DataAccessException("database down", new RuntimeException("down"));
        }

        @Override
        public void save(ChapterStructure structure) {
            throw new DataAccessException("database down", new RuntimeException("down"));
        }
    }

    private static class StubChapterLookup implements ChapterLookup {
        private final Set<String> existing = new HashSet<>();

        StubChapterLookup(String... ids) {
            existing.addAll(Arrays.asList(ids));
        }

        @Override
        public boolean exists(String chapterId) {
            return existing.contains(chapterId);
        }
    }
}