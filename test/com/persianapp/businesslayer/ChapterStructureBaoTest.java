package com.persianapp.businesslayer;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

@DisplayName("UC-02: ChapterStructureBao")
class ChapterStructureBaoTest {

    private static final String CHAPTER = "ch-1";
    private static final String OTHER_CHAPTER = "ch-2";

    private StubStructureDao dao;
    private StubChapterLookup lookup;
    private ChapterStructureBao bao;

    @BeforeEach
    void setUp() {
        dao = new StubStructureDao();
        lookup = new StubChapterLookup(CHAPTER, OTHER_CHAPTER);
        bao = new ChapterStructureBao(dao, lookup);
    }

    @Test
    @DisplayName("Offers all seven supported components")
    void offersAllComponents() {
        assertEquals(7, bao.getSupportedComponents().size());
        assertTrue(bao.getSupportedComponents().contains(ComponentType.GLOSSARY));
    }

    @Test
    @DisplayName("Returns an empty structure when none has been saved")
    void emptyWhenNothingSaved() {
        assertTrue(bao.getStructure(CHAPTER).isEmpty());
    }

    @Test
    @DisplayName("Saves a valid selection in the chosen order")
    void savesValidSelection() {
        List<ComponentType> selection = Arrays.asList(
                ComponentType.PERSIAN_DIALOGUE, ComponentType.LEARNING_OBJECTIVES, ComponentType.GLOSSARY);
        bao.saveStructure(CHAPTER, selection);
        assertEquals(selection, bao.getStructure(CHAPTER).getComponents());
    }

    @Test
    @DisplayName("Saving again replaces the previous structure")
    void saveReplacesPrevious() {
        bao.saveStructure(CHAPTER, Arrays.asList(ComponentType.EXAMPLES));
        bao.saveStructure(CHAPTER, Arrays.asList(ComponentType.GLOSSARY, ComponentType.EXAMPLES));
        assertEquals(Arrays.asList(ComponentType.GLOSSARY, ComponentType.EXAMPLES),
                bao.getStructure(CHAPTER).getComponents());
    }

    @Test
    @DisplayName("Different chapters can have different structures (FR-CS-05)")
    void chaptersAreIndependent() {
        bao.saveStructure(CHAPTER, Arrays.asList(ComponentType.EXAMPLES));
        bao.saveStructure(OTHER_CHAPTER, Arrays.asList(ComponentType.GLOSSARY));
        assertEquals(Arrays.asList(ComponentType.EXAMPLES), bao.getStructure(CHAPTER).getComponents());
        assertEquals(Arrays.asList(ComponentType.GLOSSARY), bao.getStructure(OTHER_CHAPTER).getComponents());
    }

    @Test
    @DisplayName("Saving an empty selection clears the saved structure")
    void savingEmptySelectionClearsStructure() {
        bao.saveStructure(CHAPTER, Arrays.asList(ComponentType.EXAMPLES, ComponentType.GLOSSARY));

        bao.saveStructure(CHAPTER, Collections.<ComponentType>emptyList());

        assertTrue(bao.getStructure(CHAPTER).isEmpty());
        assertEquals(2, dao.saveCount);
    }

    @Test
    @DisplayName("Rejects a null selection")
    void rejectsNullSelection() {
        assertThrows(IllegalArgumentException.class, () -> bao.saveStructure(CHAPTER, null));
        assertEquals(0, dao.saveCount);
    }

    @Test
    @DisplayName("A rejected change keeps the last valid structure (BR-06)")
    void keepsLastValidStructure() {
        List<ComponentType> valid = Arrays.asList(ComponentType.LEARNING_OBJECTIVES, ComponentType.EXAMPLES);
        bao.saveStructure(CHAPTER, valid);

        assertThrows(IllegalArgumentException.class, () -> bao.saveStructure(CHAPTER,
                Arrays.asList(ComponentType.EXAMPLES, ComponentType.EXAMPLES)));

        assertEquals(valid, bao.getStructure(CHAPTER).getComponents());
        assertEquals(1, dao.saveCount);
    }

    @Test
    @DisplayName("Rejects a chapter that does not exist and stores nothing")
    void rejectsUnknownChapter() {
        assertThrows(IllegalArgumentException.class,
                () -> bao.saveStructure("missing", Arrays.asList(ComponentType.EXAMPLES)));
        assertThrows(IllegalArgumentException.class, () -> bao.getStructure("missing"));
        assertEquals(0, dao.saveCount);
    }

    @Test
    @DisplayName("Rejects a blank chapter id")
    void rejectsBlankChapterId() {
        assertThrows(IllegalArgumentException.class,
                () -> bao.saveStructure("  ", Arrays.asList(ComponentType.EXAMPLES)));
        assertThrows(IllegalArgumentException.class, () -> bao.getStructure(null));
    }

    @Test
    @DisplayName("Requires both collaborators")
    void requiresCollaborators() {
        assertThrows(IllegalArgumentException.class, () -> new ChapterStructureBao(null, lookup));
        assertThrows(IllegalArgumentException.class, () -> new ChapterStructureBao(dao, null));
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