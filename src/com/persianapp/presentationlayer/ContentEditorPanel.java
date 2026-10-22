package com.persianapp.presentationlayer;

import com.persianapp.businesslayer.ChapterComponent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;

/**
 * Swing panel for UC-03 (edit chapter component content).
 * Embed it in any window alongside UC-01 and UC-02 panels.
 * The chapterId is supplied at construction time; call refreshForChapter() to reload.
 */
public class ContentEditorPanel extends JPanel {
    private static final Color ERROR_COLOR   = new Color(0xA32D2D);
    private static final Color SUCCESS_COLOR = new Color(0x3B6D11);

    private final ContentEditorController controller;
    private String chapterId;
    private final DefaultListModel<ChapterComponent> listModel = new DefaultListModel<>();
    private final JList<ChapterComponent> componentList = new JList<>(listModel);
    private final JTextArea editor = new JTextArea(14, 40);
    private final JLabel status = new JLabel(" ");

    public ContentEditorPanel(ContentEditorController controller, String chapterId) {
        super(new BorderLayout());
        this.controller = controller;
        this.chapterId  = chapterId;
        buildUi();
        if (chapterId != null && !chapterId.isBlank()) loadList();
    }

    /** Reloads the component list for a new chapter (called from MainApp on selection). */
    public void refreshForChapter(String newChapterId) {
        this.chapterId = newChapterId;
        editor.setText("");
        showStatus("باب منتخب ہو گیا — Chapter loaded.", null);
        loadList();
    }

    private void buildUi() {
        componentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        componentList.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        componentList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            ChapterComponent chosen = componentList.getSelectedValue();
            if (chosen != null) {
                ChapterComponent loaded = controller.selectComponent(chosen.getId());
                editor.setText(loaded.getContent());
                showStatus("Editing: " + describe(loaded), null);
            }
        });

        editor.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        editor.setLineWrap(true);
        editor.setWrapStyleWord(true);
        editor.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JButton saveButton = new JButton("محفوظ کریں — Save Content");
        saveButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        saveButton.addActionListener(e -> save());

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        bottom.add(status,      BorderLayout.CENTER);
        bottom.add(saveButton,  BorderLayout.EAST);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(componentList), new JScrollPane(editor));
        split.setResizeWeight(0.3);

        JLabel title = new JLabel("مواد میں ترمیم — Edit Content (UC-03)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));

        add(title,  BorderLayout.NORTH);
        add(split,  BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadList() {
        listModel.clear();
        try {
            for (ChapterComponent c : controller.loadComponents(chapterId)) {
                listModel.addElement(c);
            }
        } catch (Exception e) {
            showStatus("حصے لوڈ نہیں ہو سکے — Could not load components.", false);
        }
    }

    private void save() {
        if (controller.getSelected() == null) {
            showStatus("پہلے ایک حصہ منتخب کریں — Select a component first.", false);
            return;
        }
        ContentEditorController.EditResult result = controller.saveEdit(editor.getText());
        editor.setText(result.getEnteredText());
        showStatus(result.getMessage(), result.isSaved());
    }

    private void showStatus(String text, Boolean success) {
        status.setText(text);
        if (success == null)       status.setForeground(Color.DARK_GRAY);
        else if (success)          status.setForeground(SUCCESS_COLOR);
        else                       status.setForeground(ERROR_COLOR);
    }

    private static String describe(ChapterComponent c) {
        String name = c.getType().name().replace('_', ' ').toLowerCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1) + " (" + (c.getPosition() + 1) + ")";
    }
}
