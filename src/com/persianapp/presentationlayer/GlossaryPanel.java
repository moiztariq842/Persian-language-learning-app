package com.persianapp.presentationlayer;

import com.persianapp.businesslayer.GlossaryEntry;
import com.persianapp.businesslayer.GlossaryException;
import com.persianapp.businesslayer.GlossaryService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Swing panel for managing a chapter's glossary (UC-04).
 * Provides a table view of Persian words and Urdu meanings with Add/Edit/Delete.
 * Supports RTL text for Persian and Urdu (NFR-US-02, NFR-US-04).
 * Destructive actions require confirmation (BR-04, NFR-US-05).
 */
public class GlossaryPanel extends JPanel {

    private final GlossaryService glossaryService;
    private String currentChapterId;

    private DefaultTableModel tableModel;
    private JTable glossaryTable;
    private JLabel statusLabel;

    private static final Font RTL_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font UI_FONT  = new Font("Segoe UI", Font.PLAIN, 14);

    public GlossaryPanel(GlossaryService glossaryService) {
        this.glossaryService = glossaryService;
        initializeUI();
    }

    /** Called from MainApp when a chapter is selected in the UC-01 tab. */
    public void setChapterId(String chapterId) {
        this.currentChapterId = chapterId;
        loadGlossaryEntries();
    }

    // ── UI Initialization ─────────────────────────────────────

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("لغت کا انتظام — Glossary Management (UC-04)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        setupTable();
        add(new JScrollPane(glossaryTable), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private void setupTable() {
        String[] columns = {"ID", "فارسی لفظ (Persian Word)", "اردو معنی (Urdu Meaning)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        glossaryTable = new JTable(tableModel);
        glossaryTable.setFont(RTL_FONT);
        glossaryTable.setRowHeight(30);
        glossaryTable.getTableHeader().setFont(UI_FONT);
        glossaryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Hide internal ID column
        glossaryTable.getColumnModel().getColumn(0).setMinWidth(0);
        glossaryTable.getColumnModel().getColumn(0).setMaxWidth(0);
        glossaryTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        // RTL alignment for Persian and Urdu columns
        DefaultTableCellRenderer rtlRenderer = new DefaultTableCellRenderer();
        rtlRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        rtlRenderer.setFont(RTL_FONT);
        glossaryTable.getColumnModel().getColumn(1).setCellRenderer(rtlRenderer);
        glossaryTable.getColumnModel().getColumn(2).setCellRenderer(rtlRenderer);
    }

    private JPanel createButtonPanel() {
        JButton addButton     = new JButton("شامل کریں — Add");
        JButton editButton    = new JButton("ترمیم — Edit");
        JButton deleteButton  = new JButton("حذف — Delete");
        JButton refreshButton = new JButton("تازہ کریں — Refresh");

        for (JButton b : new JButton[]{addButton, editButton, deleteButton, refreshButton}) {
            b.setFont(UI_FONT);
        }

        addButton.addActionListener(e    -> handleAdd());
        editButton.addActionListener(e   -> handleEdit());
        deleteButton.addActionListener(e -> handleDelete());
        refreshButton.addActionListener(e -> loadGlossaryEntries());

        statusLabel = new JLabel("تیار — Ready");
        statusLabel.setFont(UI_FONT);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnRow.add(addButton);
        btnRow.add(editButton);
        btnRow.add(deleteButton);
        btnRow.add(refreshButton);

        JPanel south = new JPanel(new BorderLayout());
        south.add(btnRow,       BorderLayout.NORTH);
        south.add(statusLabel,  BorderLayout.SOUTH);
        return south;
    }

    // ── Data loading ──────────────────────────────────────────

    private void loadGlossaryEntries() {
        tableModel.setRowCount(0);
        if (currentChapterId == null || currentChapterId.isBlank()) {
            setStatus("براہ کرم پہلے باب منتخب کریں — Please select a chapter first.");
            return;
        }
        try {
            List<GlossaryEntry> entries = glossaryService.getEntries(currentChapterId);
            for (GlossaryEntry e : entries) {
                tableModel.addRow(new Object[]{e.getId(), e.getPersianWord(), e.getUrduMeaning()});
            }
            setStatus("کل اندراجات: " + entries.size() + " — Total entries: " + entries.size());
        } catch (GlossaryException ex) {
            showError("لغت لوڈ کرنے میں خرابی۔\n(Error loading glossary.)");
        }
    }

    // ── Event Handlers ────────────────────────────────────────

    private void handleAdd() {
        if (currentChapterId == null || currentChapterId.isBlank()) {
            showError("براہ کرم پہلے ایک باب منتخب کریں۔\n(Please select a chapter first.)");
            return;
        }
        String persian = "", urdu = "";
        while (true) {
            GlossaryEntryDialog dialog = new GlossaryEntryDialog(persian, urdu);
            int result = JOptionPane.showConfirmDialog(this, dialog,
                    "نیا لغت اندراج — New Glossary Entry",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) return;
            persian = dialog.getPersianWord();
            urdu    = dialog.getUrduMeaning();
            try {
                glossaryService.addEntry(currentChapterId, persian, urdu);
                loadGlossaryEntries();
                setStatus("اندراج کامیابی سے شامل ہو گیا — Entry added successfully.");
                return;
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            } catch (GlossaryException ex) {
                showError("محفوظ کرنے میں خرابی۔\n(Error saving entry.)");
                return;
            }
        }
    }

    private void handleEdit() {
        int selectedRow = glossaryTable.getSelectedRow();
        if (selectedRow < 0) {
            showError("براہ کرم ترمیم کے لیے ایک اندراج منتخب کریں۔\n(Please select an entry to edit.)");
            return;
        }
        String entryId = (String) tableModel.getValueAt(selectedRow, 0);
        String persian = (String) tableModel.getValueAt(selectedRow, 1);
        String urdu    = (String) tableModel.getValueAt(selectedRow, 2);
        while (true) {
            GlossaryEntryDialog dialog = new GlossaryEntryDialog(persian, urdu);
            int result = JOptionPane.showConfirmDialog(this, dialog,
                    "لغت اندراج میں ترمیم — Edit Glossary Entry",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) return;
            persian = dialog.getPersianWord();
            urdu    = dialog.getUrduMeaning();
            try {
                GlossaryEntry entry = new GlossaryEntry(entryId, currentChapterId, persian, urdu);
                glossaryService.updateEntry(entry);
                loadGlossaryEntries();
                setStatus("اندراج کامیابی سے اپ ڈیٹ ہو گیا — Entry updated successfully.");
                return;
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            } catch (GlossaryException ex) {
                showError("اپ ڈیٹ کرنے میں خرابی۔\n(Error updating entry.)");
                return;
            }
        }
    }

    private void handleDelete() {
        int selectedRow = glossaryTable.getSelectedRow();
        if (selectedRow < 0) {
            showError("براہ کرم حذف کرنے کے لیے ایک اندراج منتخب کریں۔\n(Please select an entry to delete.)");
            return;
        }
        String entryId = (String) tableModel.getValueAt(selectedRow, 0);
        String persian = (String) tableModel.getValueAt(selectedRow, 1);
        String urdu    = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "کیا آپ واقعی یہ لغت اندراج حذف کرنا چاہتے ہیں؟\n"
                + "(Are you sure you want to delete this glossary entry?)\n\n"
                + "فارسی: " + persian + "\nاردو: " + urdu,
                "حذف کی تصدیق — Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                glossaryService.deleteEntry(entryId);
                loadGlossaryEntries();
                setStatus("اندراج کامیابی سے حذف ہو گیا — Entry deleted successfully.");
            } catch (GlossaryException ex) {
                showError("حذف کرنے میں خرابی۔\n(Error deleting entry.)");
            }
        }
    }

    // ── UI Helpers ────────────────────────────────────────────

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "خرابی — Error", JOptionPane.ERROR_MESSAGE);
    }

    private void setStatus(String message) { statusLabel.setText(message); }

    // ── Inner Dialog ──────────────────────────────────────────

    private static class GlossaryEntryDialog extends JPanel {
        private final JTextField persianField;
        private final JTextField urduField;

        GlossaryEntryDialog(String persianValue, String urduValue) {
            super(new GridLayout(4, 1, 5, 5));

            JLabel persianLabel = new JLabel("فارسی لفظ (Persian Word):");
            persianLabel.setFont(UI_FONT);
            persianField = new JTextField(persianValue, 25);
            persianField.setFont(RTL_FONT);
            persianField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

            JLabel urduLabel = new JLabel("اردو معنی (Urdu Meaning):");
            urduLabel.setFont(UI_FONT);
            urduField = new JTextField(urduValue, 25);
            urduField.setFont(RTL_FONT);
            urduField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

            add(persianLabel);
            add(persianField);
            add(urduLabel);
            add(urduField);
        }

        String getPersianWord()  { return persianField.getText(); }
        String getUrduMeaning() { return urduField.getText(); }
    }
}
