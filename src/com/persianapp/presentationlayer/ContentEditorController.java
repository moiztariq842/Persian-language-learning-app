package com.persianapp.presentationlayer;

import com.persianapp.businesslayer.ChapterComponent;
import com.persianapp.businesslayer.ContentService;
import com.persianapp.businesslayer.ContentValidationException;

import java.util.List;
import java.util.Objects;

/**
 * Presentation logic for UC-03: tracks the selected component, calls the business layer,
 * and returns messages for the view. It holds no SQL and no validation rules.
 */
public class ContentEditorController {
    private final ContentService service;
    private ChapterComponent selected;

    public ContentEditorController(ContentService service) {
        this.service = Objects.requireNonNull(service, "service must not be null");
    }

    public List<ChapterComponent> loadComponents(String chapterId) {
        selected = null;
        return service.loadComponents(chapterId);
    }

    public ChapterComponent selectComponent(String componentId) {
        selected = service.loadComponent(componentId);
        return selected;
    }

    public ChapterComponent getSelected() { return selected; }

    public EditResult saveEdit(String enteredText) {
        if (selected == null) {
            throw new IllegalStateException("Select a component before saving.");
        }
        try {
            selected = service.saveContent(selected.getId(), enteredText);
            return EditResult.saved(enteredText);
        } catch (ContentValidationException e) {
            return EditResult.rejected(e.getResult().getField(), e.getResult().getMessage(), enteredText);
        }
    }

    public static final class EditResult {
        private final boolean saved;
        private final String  message;
        private final String  enteredText;

        private EditResult(boolean saved, String message, String enteredText) {
            this.saved       = saved;
            this.message     = message;
            this.enteredText = enteredText;
        }

        static EditResult saved(String enteredText) {
            return new EditResult(true, "Content saved.", enteredText);
        }

        static EditResult rejected(String field, String reason, String enteredText) {
            return new EditResult(false, field + ": " + reason, enteredText);
        }

        public boolean isSaved()      { return saved; }
        public String  getMessage()   { return message; }
        public String  getEnteredText() { return enteredText; }
    }
}
