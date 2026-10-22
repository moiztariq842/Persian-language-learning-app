package com.persianapp.presentationlayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import com.persianapp.businesslayer.ComponentType;
import com.persianapp.businesslayer.DataAccessException;
import com.persianapp.businesslayer.StructureException;

/**
 * Right-to-left Swing screen for UC-02 (configure chapter structure).
 * Contains no business rules: every action goes through StructureController.
 * It is a plain JPanel so it can be placed in any window the group builds.
 */
public class StructurePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Font  UI_FONT      = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Color SUCCESS_COLOR = new Color(0, 128, 0);
    private static final Color ERROR_COLOR   = new Color(176, 0, 32);
    private static final Color INFO_COLOR    = new Color(180, 90, 0);

    private final StructureController controller;
    private final DefaultListModel<ComponentType> availableModel = new DefaultListModel<>();
    private final DefaultListModel<ComponentType> selectedModel  = new DefaultListModel<>();
    private final JList<ComponentType> availableList = new JList<>(availableModel);
    private final JList<ComponentType> selectedList  = new JList<>(selectedModel);
    private final JLabel statusLabel = new JLabel(" ");
    private String chapterId;

    public StructurePanel(StructureController controller) {
        if (controller == null) throw new IllegalArgumentException("Controller is required.");
        this.controller = controller;
        buildLayout();
        applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    }

    /** Shows the saved structure of the given chapter. */
    public void loadChapter(String chapterId) {
        this.chapterId = chapterId;
        try {
            showSelection(controller.loadSelected(chapterId));
            setStatus(" ", Color.DARK_GRAY);
        } catch (StructureException e) {
            this.chapterId = null;
            showSelection(Collections.<ComponentType>emptyList());
            setStatus(StructureMessages.forCode(e.getCode()), ERROR_COLOR);
        } catch (DataAccessException e) {
            this.chapterId = null;
            showSelection(Collections.<ComponentType>emptyList());
            setStatus(StructureMessages.STORAGE_FAILED, ERROR_COLOR);
        }
    }

    private void buildLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("باب کی ساخت ترتیب دیں — Configure Chapter Structure (UC-02)", SwingConstants.CENTER);
        title.setFont(UI_FONT.deriveFont(Font.BOLD, 20f));
        add(title, BorderLayout.NORTH);

        configureList(availableList, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        configureList(selectedList,  ListSelectionModel.SINGLE_SELECTION);

        JPanel buttons = new JPanel(new GridLayout(0, 1, 0, 8));
        buttons.add(button("شامل کریں",  e -> addSelected()));
        buttons.add(button("ہٹائیں",     e -> removeSelected()));
        buttons.add(button("اوپر ↑",     e -> move(true)));
        buttons.add(button("نیچے ↓",    e -> move(false)));
        JPanel buttonColumn = new JPanel(new BorderLayout());
        buttonColumn.add(buttons, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 3, 10, 0));
        center.add(titledScroll("دستیاب حصے", availableList));
        center.add(buttonColumn);
        center.add(titledScroll("منتخب حصے (ترتیب کے مطابق)", selectedList));
        add(center, BorderLayout.CENTER);

        statusLabel.setFont(UI_FONT);
        JPanel bottom = new JPanel(new BorderLayout(10, 0));
        bottom.add(button("محفوظ کریں — Save", e -> save()), BorderLayout.LINE_START);
        bottom.add(statusLabel, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void configureList(JList<ComponentType> list, int selectionMode) {
        list.setFont(UI_FONT);
        list.setSelectionMode(selectionMode);
        list.setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getListCellRendererComponent(JList<?> source, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(source, value, index, isSelected, cellHasFocus);
                setText(ComponentLabels.labelFor((ComponentType) value));
                return this;
            }
        });
    }

    private JPanel titledScroll(String heading, JList<ComponentType> list) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        JLabel label = new JLabel(heading);
        label.setFont(UI_FONT);
        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        return panel;
    }

    private JButton button(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(UI_FONT);
        button.addActionListener(action);
        return button;
    }

    private void addSelected() {
        List<ComponentType> chosen = availableList.getSelectedValuesList();
        if (chosen.isEmpty()) { setStatus("پہلے دستیاب حصوں میں سے کوئی حصہ چنیں", INFO_COLOR); return; }
        List<ComponentType> updated = currentSelection();
        updated.addAll(chosen);
        showSelection(updated);
        setStatus(" ", Color.DARK_GRAY);
    }

    private void removeSelected() {
        List<ComponentType> chosen = selectedList.getSelectedValuesList();
        if (chosen.isEmpty()) { setStatus("پہلے منتخب حصوں میں سے کوئی حصہ چنیں", INFO_COLOR); return; }
        List<ComponentType> updated = currentSelection();
        updated.removeAll(chosen);
        showSelection(updated);
        setStatus(" ", Color.DARK_GRAY);
    }

    private void move(boolean up) {
        if (selectedModel.size() < 2) { setStatus("ترتیب بدلنے کے لیے کم از کم دو حصے ہونے چاہئیں", INFO_COLOR); return; }
        ComponentType type = selectedList.getSelectedValue();
        if (type == null) { setStatus("پہلے منتخب حصوں میں سے ایک حصہ چنیں", INFO_COLOR); return; }
        int index = selectedModel.indexOf(type);
        if (up  && index == 0)                          { setStatus("یہ حصہ پہلے ہی سب سے اوپر ہے",  INFO_COLOR); return; }
        if (!up && index == selectedModel.size() - 1)   { setStatus("یہ حصہ پہلے ہی سب سے نیچے ہے", INFO_COLOR); return; }
        List<ComponentType> current = currentSelection();
        showSelection(up ? controller.moveUp(current, type) : controller.moveDown(current, type));
        selectedList.setSelectedValue(type, true);
        setStatus(" ", Color.DARK_GRAY);
    }

    private void save() {
        if (chapterId == null) { setStatus("پہلے باب منتخب کریں", ERROR_COLOR); return; }
        List<ComponentType> selection = currentSelection();
        if (controller.savingRemovesComponents(chapterId, selection) && !confirmRemoval()) {
            setStatus(StructureMessages.SAVE_CANCELLED, INFO_COLOR);
            return;
        }
        ActionResult result = controller.save(chapterId, selection);
        setStatus(result.getMessage(), result.isSuccess() ? SUCCESS_COLOR : ERROR_COLOR);
    }

    private boolean confirmRemoval() {
        JLabel message  = new JLabel(StructureMessages.CONFIRM_REMOVE_ALL);
        message.setFont(UI_FONT);
        Object[] options = { StructureMessages.CONFIRM_YES, StructureMessages.CONFIRM_NO };
        JOptionPane pane = new JOptionPane(message, JOptionPane.WARNING_MESSAGE,
                JOptionPane.YES_NO_OPTION, null, options, options[1]);
        JDialog dialog = pane.createDialog(this, StructureMessages.CONFIRM_TITLE);
        dialog.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        dialog.setVisible(true);
        dialog.dispose();
        return StructureMessages.CONFIRM_YES.equals(pane.getValue());
    }

    private List<ComponentType> currentSelection() {
        List<ComponentType> list = new ArrayList<>();
        for (int i = 0; i < selectedModel.size(); i++) list.add(selectedModel.get(i));
        return list;
    }

    private void showSelection(List<ComponentType> selected) {
        selectedModel.clear();
        availableModel.clear();
        for (ComponentType type : selected) selectedModel.addElement(type);
        for (ComponentType type : controller.getAvailableComponents()) {
            if (!selected.contains(type)) availableModel.addElement(type);
        }
    }

    private void setStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }
}
