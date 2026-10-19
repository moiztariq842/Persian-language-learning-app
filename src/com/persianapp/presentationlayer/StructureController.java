package com.persianapp.presentationlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.persianapp.businesslayer.ChapterStructureBao;
import com.persianapp.businesslayer.ComponentType;

/**
 * Presentation-layer controller for UC-02 (configure chapter structure).
 * Receives the administrator's actions from the screen and delegates to the service.
 */
public class StructureController {

    private final ChapterStructureBao service;

    public StructureController(ChapterStructureBao service) {
        if (service == null) {
            throw new IllegalArgumentException("Service is required.");
        }
        this.service = service;
    }

    /** All components the administrator can choose from. */
    public List<ComponentType> getAvailableComponents() {
        return service.getSupportedComponents();
    }

    /**
     * The components currently saved for a chapter, in order.
     * The screen picks the chapter from an existing list, so an unknown id is a programming error.
     */
    public List<ComponentType> loadSelected(String chapterId) {
        return service.getStructure(chapterId).getComponents();
    }

    /** Validates and saves; the result carries a message to show the administrator. */
    public ActionResult save(String chapterId, List<ComponentType> selected) {
        try {
            service.saveStructure(chapterId, selected);
            return ActionResult.ok("Chapter structure saved.");
        } catch (IllegalArgumentException e) {
            return ActionResult.error(e.getMessage());
        }
    }

    /** Returns a new list with the component one position earlier; unchanged if not possible. */
    public List<ComponentType> moveUp(List<ComponentType> current, ComponentType type) {
        return move(current, type, -1);
    }

    /** Returns a new list with the component one position later; unchanged if not possible. */
    public List<ComponentType> moveDown(List<ComponentType> current, ComponentType type) {
        return move(current, type, 1);
    }

    private List<ComponentType> move(List<ComponentType> current, ComponentType type, int offset) {
        List<ComponentType> result = new ArrayList<>(current);
        int index = result.indexOf(type);
        int target = index + offset;
        if (index < 0 || target < 0 || target >= result.size()) {
            return result;
        }
        Collections.swap(result, index, target);
        return result;
    }
}