package com.persianapp.presentationlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.persianapp.businesslayer.ChapterStructureBao;
import com.persianapp.businesslayer.ComponentType;
import com.persianapp.businesslayer.StructureException;

/**
 * Presentation-layer controller for UC-02 (configure chapter structure).
 * Receives the administrator's actions from the screen and delegates to the business object.
 */
public class StructureController {

    private final ChapterStructureBao bao;

    public StructureController(ChapterStructureBao bao) {
        if (bao == null) {
            throw new IllegalArgumentException("Business object is required.");
        }
        this.bao = bao;
    }

    /** All components the administrator can choose from. */
    public List<ComponentType> getAvailableComponents() {
        return bao.getSupportedComponents();
    }

    /**
     * The components currently saved for a chapter, in order.
     * Throws StructureException if the chapter is unknown; the screen shows its Urdu message.
     */
    public List<ComponentType> loadSelected(String chapterId) {
        return bao.getStructure(chapterId).getComponents();
    }

    /** Validates and saves; the result carries an Urdu message to show the administrator. */
    public ActionResult save(String chapterId, List<ComponentType> selected) {
        try {
            bao.saveStructure(chapterId, selected);
            return ActionResult.ok(StructureMessages.SAVED);
        } catch (StructureException e) {
            return ActionResult.error(StructureMessages.forCode(e.getCode()));
        } catch (IllegalArgumentException e) {
            return ActionResult.error(StructureMessages.UNEXPECTED);
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