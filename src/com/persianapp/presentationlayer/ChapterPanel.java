package com.persianapp.presentationlayer;

import com.persianapp.businesslayer.Chapter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

// Presentation panel for chapter management.
public class ChapterPanel extends JPanel {

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font UI_FONT    = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font RTL_FONT   = new Font("Tahoma", Font.PLAIN, 15);

    private final ChapterController controller;
    private final DefaultListModel<Chapter> listModel = new DefaultListModel<>();
    private final JList<Chapter> chapterList = new JList<>(listModel);
    private final JLabel statusLabel = new JLabel("تیار — Ready");
    private Consumer<Chapter> selectionListener;

    // Constructs the chapter panel.
    public ChapterPanel(ChapterController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("ChapterController is required.");
        }
        this.controller = controller;
        buildLayout();
        refreshList();
    }

    // Sets callback for chapter selection.
    public void setOnChapterSelectedListener(Consumer<Chapter> listener) {
        this.selectionListener = listener;
    }

    // Returns the currently selected chapter.
    public Chapter getSelectedChapter() {
        return chapterList.getSelectedValue();
    }

    // Reloads all chapters into the list.
    public void refreshList() {
        listModel.clear();
        List<Chapter> chapters = controller.listChapters();
        for (Chapter c : chapters) {
            listModel.addElement(c);
        }
        statusLabel.setText("کل ابواب: " + chapters.size() + " | Total Chapters: " + chapters.size());
    }

    // Sets up UI components and layouts.
    private void buildLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel titleLabel = new JLabel("ابواب کا انتظام — Chapter Management (UC-01)");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(new Color(24, 43, 73));

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
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            "موجودہ ابواب کی فہرست — Saved Chapters (Oldest First)"));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));

        JButton newBtn    = buildButton("نیا باب — New Chapter", e -> handleCreate());
        JButton renameBtn = buildButton("نام تبدیل کریں — Rename",  e -> handleRename());
        JButton deleteBtn = buildButton("حذف کریں — Delete",      e -> handleDelete());
        JButton refreshBtn = buildButton("تازہ کریں — Refresh",   e -> refreshList());

        btnPanel.add(newBtn);
        btnPanel.add(renameBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        statusLabel.setFont(UI_FONT);
        statusLabel.setForeground(Color.DARK_GRAY);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(btnPanel, BorderLayout.NORTH);
        southPanel.add(statusLabel, BorderLayout.SOUTH);

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    // Opens creation dialog and triggers chapter creation.
    private void handleCreate() {
        JTextField inputField = new JTextField(25);
        inputField.setFont(RTL_FONT);
        inputField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JPanel dialog = new JPanel(new BorderLayout(5, 5));
        JLabel label  = new JLabel("باب کا عنوان درج کریں — Enter Chapter Title:");
        label.setFont(UI_FONT);
        dialog.add(label, BorderLayout.NORTH);
        dialog.add(inputField, BorderLayout.CENTER);

        int confirm = JOptionPane.showConfirmDialog(this, dialog,
            "نیا باب بنائیں — Create Chapter",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (confirm != JOptionPane.OK_OPTION) return;

        ActionResult result = controller.createChapter(inputField.getText());
        applyResult(result);
    }

    // Opens rename dialog and triggers chapter renaming.
    private void handleRename() {
        Chapter sel = chapterList.getSelectedValue();
        if (sel == null) { showWarning(ChapterMessages.SELECT_FIRST); return; }

        JTextField inputField = new JTextField(sel.getTitle(), 25);
        inputField.setFont(RTL_FONT);
        inputField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JPanel dialog = new JPanel(new BorderLayout(5, 5));
        JLabel label  = new JLabel("نیا عنوان درج کریں — Enter New Title:");
        label.setFont(UI_FONT);
        dialog.add(label, BorderLayout.NORTH);
        dialog.add(inputField, BorderLayout.CENTER);

        int confirm = JOptionPane.showConfirmDialog(this, dialog,
            "عنوان تبدیل کریں — Rename Chapter",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (confirm != JOptionPane.OK_OPTION) return;

        ActionResult result = controller.renameChapter(sel.getId(), inputField.getText());
        applyResult(result);
    }

    // Opens confirmation dialog and triggers chapter deletion.
    private void handleDelete() {
        Chapter sel = chapterList.getSelectedValue();
        if (sel == null) { showWarning(ChapterMessages.SELECT_FIRST); return; }

        Object[] options  = { ChapterMessages.CONFIRM_YES, ChapterMessages.CONFIRM_NO };
        JLabel   msgLabel = new JLabel(
            "<html>کیا آپ واقعی باب '<b>" + sel.getTitle() + "</b>' حذف کرنا چاہتے ہیں؟<br><br>"
            + "<b>انتباہ:</b><br>"
            + "اس کے تمام حصے اور لغت بھی مستقل طور پر حذف ہو جائیں گے!</html>");
        msgLabel.setFont(UI_FONT);

        JOptionPane pane = new JOptionPane(msgLabel, JOptionPane.WARNING_MESSAGE,
            JOptionPane.YES_NO_OPTION, null, options, options[1]);
        JDialog dlg = pane.createDialog(this, ChapterMessages.CONFIRM_DELETE_TITLE);
        dlg.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        dlg.setVisible(true);
        dlg.dispose();

        if (!ChapterMessages.CONFIRM_YES.equals(pane.getValue())) return;

        ActionResult result = controller.deleteChapter(sel.getId());
        applyResult(result);
    }

    // Updates UI state according to action result.
    private void applyResult(ActionResult result) {
        if (result.isSuccess()) {
            refreshList();
            statusLabel.setForeground(new Color(25, 120, 40));
        } else {
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, result.getMessage(),
                "خرابی — Validation Error", JOptionPane.ERROR_MESSAGE);
        }
        statusLabel.setText(result.getMessage());
    }

    // Displays a warning message dialog.
    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message,
            "توجہ فرمائیں — Warning", JOptionPane.WARNING_MESSAGE);
    }

    // Helper to instantiate a configured button.
    private JButton buildButton(String label, java.awt.event.ActionListener action) {
        JButton btn = new JButton(label);
        btn.setFont(UI_FONT);
        btn.addActionListener(action);
        return btn;
    }
}
