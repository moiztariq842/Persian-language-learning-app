package com.persianapp.presentationlayer;

import com.persianapp.businesslayer.Chapter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class ChapterPanel extends JPanel {

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font UI_FONT    = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font RTL_FONT   = new Font("Tahoma", Font.PLAIN, 15);

    private final ChapterController controller;
    private final DefaultListModel<Chapter> listModel = new DefaultListModel<>();
    private final JList<Chapter> chapterList = new JList<>(listModel);
    private final JLabel statusLabel = new JLabel("تیار — Ready");
    private Consumer<Chapter> selectionListener;

    // Constructs the UC-01 chapter management panel.
    public ChapterPanel(ChapterController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("ChapterController must not be null.");
        }
        this.controller = controller;
        buildLayout();
        refreshList();
    }

    // Sets a callback listener when a chapter is selected in the list.
    public void setOnChapterSelectedListener(Consumer<Chapter> listener) {
        this.selectionListener = listener;
    }

    // Returns the currently selected chapter if any.
    public Chapter getSelectedChapter() {
        return chapterList.getSelectedValue();
    }

    // Refreshes the list of chapters from the database.
    public void refreshList() {
        listModel.clear();
        List<Chapter> chapters = controller.listChapters();
        for (Chapter c : chapters) {
            listModel.addElement(c);
        }
        statusLabel.setText("کل ابواب: " + chapters.size() + " | Total Chapters: " + chapters.size());
    }

    // Builds the visual UI layout and controls.
    private void buildLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(14, 16, 14, 16));

        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("ابواب کا انتظام — Chapter Management (UC-01)");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(new Color(24, 43, 73));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Center List View
        chapterList.setFont(RTL_FONT);
        chapterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chapterList.setFixedCellHeight(36);
        chapterList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Chapter c) {
                    setText("  " + (index + 1) + ". " + c.getTitle());
                }
                return this;
            }
        });

        chapterList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && selectionListener != null) {
                Chapter sel = chapterList.getSelectedValue();
                if (sel != null) selectionListener.accept(sel);
            }
        });

        JScrollPane scrollPane = new JScrollPane(chapterList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("موجودہ ابواب کی فہرست — Saved Chapters (Oldest First)"));

        // Action Buttons Toolbar
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));

        JButton newBtn = new JButton("نیا باب — New Chapter (FR-CH-01)");
        newBtn.setFont(UI_FONT);
        newBtn.addActionListener(e -> handleCreate());

        JButton renameBtn = new JButton("نام تبدیل کریں — Rename (FR-CH-05)");
        renameBtn.setFont(UI_FONT);
        renameBtn.addActionListener(e -> handleRename());

        JButton deleteBtn = new JButton("حذف کریں — Delete (FR-CH-06)");
        deleteBtn.setFont(UI_FONT);
        deleteBtn.addActionListener(e -> handleDelete());

        JButton refreshBtn = new JButton("تازہ کریں — Refresh");
        refreshBtn.setFont(UI_FONT);
        refreshBtn.addActionListener(e -> refreshList());

        btnPanel.add(newBtn);
        btnPanel.add(renameBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        // Status bar
        JPanel southPanel = new JPanel(new BorderLayout());
        statusLabel.setFont(UI_FONT);
        statusLabel.setForeground(Color.DARK_GRAY);
        southPanel.add(btnPanel, BorderLayout.NORTH);
        southPanel.add(statusLabel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    // Handles chapter creation with modal validation dialog.
    private void handleCreate() {
        JTextField inputField = new JTextField(25);
        inputField.setFont(RTL_FONT);
        inputField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JPanel dialogPanel = new JPanel(new BorderLayout(5, 5));
        JLabel label = new JLabel("باب کا عنوان درج کریں — Enter Chapter Title:");
        label.setFont(UI_FONT);
        dialogPanel.add(label, BorderLayout.NORTH);
        dialogPanel.add(inputField, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
            this, dialogPanel, "نیا باب بنائیں — Create Chapter",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String title = inputField.getText();
            try {
                controller.createChapter(title);
                refreshList();
                statusLabel.setText("باب کامیابی سے بن گیا — Chapter created successfully.");
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        }
    }

    // Handles chapter renaming with pre-filled title.
    private void handleRename() {
        Chapter sel = chapterList.getSelectedValue();
        if (sel == null) {
            showWarning("براہ کرم فہرست سے پہلے ایک باب منتخب کریں — Please select a chapter first.");
            return;
        }

        JTextField inputField = new JTextField(sel.getTitle(), 25);
        inputField.setFont(RTL_FONT);
        inputField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JPanel dialogPanel = new JPanel(new BorderLayout(5, 5));
        JLabel label = new JLabel("نیا عنوان درج کریں — Enter New Title:");
        label.setFont(UI_FONT);
        dialogPanel.add(label, BorderLayout.NORTH);
        dialogPanel.add(inputField, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
            this, dialogPanel, "عنوان تبدیل کریں — Rename Chapter",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String newTitle = inputField.getText();
            try {
                controller.renameChapter(sel.getId(), newTitle);
                refreshList();
                statusLabel.setText("عنوان کامیابی سے اپ ڈیٹ ہو گیا — Chapter renamed successfully.");
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        }
    }

    // Handles chapter deletion with cascade confirmation dialog (BR-04, BR-05).
    private void handleDelete() {
        Chapter sel = chapterList.getSelectedValue();
        if (sel == null) {
            showWarning("براہ کرم حذف کرنے کے لیے ایک باب منتخب کریں — Please select a chapter first.");
            return;
        }

        String warningMessage = "کیا آپ واقعی یہ باب حذف کرنا چاہتے ہیں؟\n"
            + "Are you sure you want to delete chapter: '" + sel.getTitle() + "'?\n\n"
            + "انتباہ (Warning — BR-04, BR-05, FR-CH-07):\n"
            + "اس باب کو حذف کرنے سے اس کے تمام حصے (components) اور لغت (glossary) بھی مستقل طور پر حذف ہو جائیں گے!\n"
            + "Deleting this chapter will permanently remove ALL associated components, instructional content, and glossary entries!";

        int confirm = JOptionPane.showConfirmDialog(
            this, warningMessage, "حذف کی تصدیق — Confirm Delete (Cascade)",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteChapter(sel.getId());
            refreshList();
            statusLabel.setText("باب اور اس کے تمام حصے حذف کر دیے گئے — Chapter deleted.");
        }
    }

    // Shows validation error dialog (BR-07, NFR-US-06).
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "خرابی — Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    // Shows warning dialog for missing selection.
    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "توجہ فرمائیں — Warning", JOptionPane.WARNING_MESSAGE);
    }
}
