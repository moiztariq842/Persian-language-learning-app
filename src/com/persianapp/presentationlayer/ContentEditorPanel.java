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
 * Swing panel for UC-03. Embed it in the main window so it can be shown alongside UC-01 and UC-02.
 */
public class ContentEditorPanel extends JPanel {
    private static final Color ERROR_COLOR = new Color(0xA32D2D);
    private static final Color SUCCESS_COLOR = new Color(0x3B6D11);

    private final ContentEditorController controller;
    private final String chapterId;
    private final DefaultListModel<ChapterComponent> listModel = new DefaultListModel<>();
    private final JList<ChapterComponent> componentList = new JList<>(listModel);
    private final JTextArea editor = new JTextArea(14, 40);
    private final JLabel status = new JLabel(" ");

    public ContentEditorPanel(ContentEditorController controller, String chapterId) {
        super(new BorderLayout());
        this.controller = controller;
        this.chapterId = chapterId;
        buildUi();
        loadList();
    }

    private void buildUi() {
        componentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        componentList.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        componentList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            ChapterComponent chosen = componentList.getSelectedValue();
            if (chosen != null) {
                ChapterComponent loaded = controller.selectComponent(chosen.getId());
                editor.setText(loaded.getContent());
                showStatus("Editing: " + describe(loaded), null);
            }
        });

        editor.setFont(new Font("Noto Naskh Arabic", Font.PLAIN, 18));
        editor.setLineWrap(true);
        editor.setWrapStyleWord(true);
        editor.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JButton saveButton = new JButton("Save content");
        saveButton.addActionListener(e -> save());

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        bottom.add(status, BorderLayout.CENTER);
        bottom.add(saveButton, BorderLayout.EAST);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(componentList), new JScrollPane(editor));
        split.setResizeWeight(0.3);

        add(split, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadList() {
        listModel.clear();
        for (ChapterComponent component : controller.loadComponents(chapterId)) {
            listModel.addElement(component);
        }
    }

    private void save() {
        if (controller.getSelected() == null) {
            showStatus("Select a component first.", false);
            return;
        }
        ContentEditorController.EditResult result = controller.saveEdit(editor.getText());
        editor.setText(result.getEnteredText());
        showStatus(result.getMessage(), result.isSaved());
    }

    private void showStatus(String text, Boolean success) {
        status.setText(text);
        if (success == null) {
            status.setForeground(Color.DARK_GRAY);
        } else {
            status.setForeground(success ? SUCCESS_COLOR : ERROR_COLOR);
        }
    }

    private static String describe(ChapterComponent c) {
        String name = c.getType().name().replace('_', ' ').toLowerCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1) + " (" + (c.getPosition() + 1) + ")";
    }
}
