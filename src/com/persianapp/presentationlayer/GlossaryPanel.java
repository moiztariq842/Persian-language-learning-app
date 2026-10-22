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
 *
 * <p>Provides a table view of Persian words and their Urdu meanings,
 * with buttons to add, edit, and delete entries. Supports right-to-left
 * display for Persian and Urdu text (NFR-US-02, NFR-US-04).</p>
 *
 * <p>Destructive actions (delete) always require confirmation (BR-04, NFR-US-05).
 * Validation errors are shown with clear messages (NFR-US-06).</p>
 */
public class GlossaryPanel extends JPanel {

    private final GlossaryService glossaryService;
    private String currentChapterId;

    // ── UI Components ────────────────────────────────────────

    private DefaultTableModel tableModel;
    private JTable glossaryTable;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JLabel statusLabel;

    // ── RTL Font for Persian / Urdu (NFR-US-04, issue #11) ──

    private static final Font RTL_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font UI_FONT  = new Font("Segoe UI", Font.PLAIN, 14);

    /**
     * Constructs the glossary panel.
     *
     * @param glossaryService the business service for glossary operations
     */
    public GlossaryPanel(GlossaryService glossaryService) {
        this.glossaryService = glossaryService;
        initializeUI();
    }

    // ── UI Initialization ────────────────────────────────────

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title label
        JLabel titleLabel = new JLabel("لغت کا انتظام — Glossary Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Table
        setupTable();
        JScrollPane scrollPane = new JScrollPane(glossaryTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupTable() {
        String[] columns = {"ID", "فارسی لفظ (Persian Word)", "اردو معنی (Urdu Meaning)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only table; editing via dialog
            }
        };

        glossaryTable = new JTable(tableModel);
        glossaryTable.setFont(RTL_FONT);
        glossaryTable.setRowHeight(30);
        glossaryTable.getTableHeader().setFont(UI_FONT);
        glossaryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Hide the ID column (used internally only)
        glossaryTable.getColumnModel().getColumn(0).setMinWidth(0);
        glossaryTable.getColumnModel().getColumn(0).setMaxWidth(0);
        glossaryTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        // Right-to-left alignment for Persian and Urdu columns (NFR-US-02)
        DefaultTableCellRenderer rtlRenderer = new DefaultTableCellRenderer();
        rtlRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        rtlRenderer.setFont(RTL_FONT);
        glossaryTable.getColumnModel().getColumn(1).setCellRenderer(rtlRenderer);
        glossaryTable.getColumnModel().getColumn(2).setCellRenderer(rtlRenderer);

        // Apply RTL component orientation to the table
        glossaryTable.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        addButton = new JButton("نیا اندراج — Add");
        addButton.setFont(UI_FONT);
        addButton.addActionListener(e -> handleAdd());

        editButton = new JButton("ترمیم — Edit");
        editButton.setFont(UI_FONT);
        editButton.addActionListener(e -> handleEdit());

        deleteButton = new JButton("حذف — Delete");
        deleteButton.setFont(UI_FONT);
        deleteButton.setForeground(Color.RED);
        deleteButton.addActionListener(e -> handleDelete());

        refreshButton = new JButton("تازہ — Refresh");
        refreshButton.setFont(UI_FONT);
        refreshButton.addActionListener(e -> loadGlossaryEntries());

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UI_FONT);

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(refreshButton);
        panel.add(statusLabel);

        return panel;
    }

    // ── Public API ───────────────────────────────────────────

    /**
     * Loads the glossary for the specified chapter.
     *
     * @param chapterId the chapter whose glossary to display
     */
    public void setChapterId(String chapterId) {
        this.currentChapterId = chapterId;
        loadGlossaryEntries();
    }

    /**
     * Returns the currently loaded chapter ID.
     */
    public String getCurrentChapterId() {
        return currentChapterId;
    }

    // ── Data Loading ─────────────────────────────────────────

    /**
     * Loads glossary entries from the service and populates the table (FR-GL-03).
     */
    private void loadGlossaryEntries() {
        tableModel.setRowCount(0); // clear existing rows

        if (currentChapterId == null || currentChapterId.isBlank()) {
            setStatus("براہ کرم پہلے ایک باب منتخب کریں۔ (Please select a chapter first.)");
            return;
        }

        try {
            List<GlossaryEntry> entries = glossaryService.getEntries(currentChapterId);
            for (GlossaryEntry entry : entries) {
                tableModel.addRow(new Object[]{
                        entry.getId(),
                        entry.getPersianWord(),
                        entry.getUrduMeaning()
                });
            }
            setStatus("اندراجات: " + entries.size() + " — Entries loaded: " + entries.size());
        } catch (GlossaryException ex) {
            showError("لغت لوڈ کرنے میں خرابی۔\n(Error loading glossary.)");
        }
    }

    // ── Event Handlers ───────────────────────────────────────

    /**
     * Handles the Add button click (FR-GL-02).
     * Shows a dialog for entering a new Persian word and Urdu meaning.
     * BR-07/BR-09: re-opens the dialog with entered values when validation fails.
     */
    private void handleAdd() {
        if (currentChapterId == null || currentChapterId.isBlank()) {
            showError("براہ کرم پہلے ایک باب منتخب کریں۔\n(Please select a chapter first.)");
            return;
        }

        String persian = "";
        String urdu = "";

        // Loop until valid or cancelled (BR-07 / issue #9)
        while (true) {
            GlossaryEntryDialog dialog = new GlossaryEntryDialog(persian, urdu);

            int result = JOptionPane.showConfirmDialog(
                    this, dialog,
                    "نیا لغت اندراج — New Glossary Entry",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) {
                return; // User cancelled
            }

            persian = dialog.getPersianWord();
            urdu = dialog.getUrduMeaning();

            try {
                glossaryService.addEntry(currentChapterId, persian, urdu);
                loadGlossaryEntries();
                setStatus("اندراج کامیابی سے شامل ہو گیا۔ — Entry added successfully.");
                return;
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
                // Loop continues — dialog re-opens with entered values
            } catch (GlossaryException ex) {
                showError("محفوظ کرنے میں خرابی۔\n(Error saving entry.)");
                return;
            }
        }
    }

    /**
     * Handles the Edit button click (FR-GL-04).
     * Populates the dialog with the selected entry's values.
     * BR-07/BR-09: re-opens the dialog with entered values when validation fails.
     */
    private void handleEdit() {
        int selectedRow = glossaryTable.getSelectedRow();
        if (selectedRow < 0) {
            showError("براہ کرم ترمیم کے لیے ایک اندراج منتخب کریں۔\n(Please select an entry to edit.)");
            return;
        }

        String entryId = (String) tableModel.getValueAt(selectedRow, 0);
        String persian = (String) tableModel.getValueAt(selectedRow, 1);
        String urdu    = (String) tableModel.getValueAt(selectedRow, 2);

        // Loop until valid or cancelled (BR-07 / issue #9)
        while (true) {
            GlossaryEntryDialog dialog = new GlossaryEntryDialog(persian, urdu);

            int result = JOptionPane.showConfirmDialog(
                    this, dialog,
                    "لغت اندراج میں ترمیم — Edit Glossary Entry",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) {
                return; // User cancelled
            }

            persian = dialog.getPersianWord();
            urdu = dialog.getUrduMeaning();

            try {
                GlossaryEntry entry = new GlossaryEntry(
                        entryId, currentChapterId, persian, urdu);
                glossaryService.updateEntry(entry);
                loadGlossaryEntries();
                setStatus("اندراج کامیابی سے اپ ڈیٹ ہو گیا۔ — Entry updated successfully.");
                return;
            } catch (IllegalArgumentException ex) {
                // BR-06: failed validation does not replace saved data
                showError(ex.getMessage());
                // Loop continues — dialog re-opens with entered values
            } catch (GlossaryException ex) {
                showError("اپ ڈیٹ کرنے میں خرابی۔\n(Error updating entry.)");
                return;
            }
        }
    }

    /**
     * Handles the Delete button click (FR-GL-05).
     * Requests confirmation before deletion (BR-04, NFR-US-05).
     */
    private void handleDelete() {
        int selectedRow = glossaryTable.getSelectedRow();
        if (selectedRow < 0) {
            showError("براہ کرم حذف کرنے کے لیے ایک اندراج منتخب کریں۔\n(Please select an entry to delete.)");
            return;
        }

        String entryId = (String) tableModel.getValueAt(selectedRow, 0);
        String persian = (String) tableModel.getValueAt(selectedRow, 1);
        String urdu    = (String) tableModel.getValueAt(selectedRow, 2);

        // BR-04: Confirmation before deletion
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "کیا آپ واقعی یہ لغت اندراج حذف کرنا چاہتے ہیں؟\n"
                        + "(Are you sure you want to delete this glossary entry?)\n\n"
                        + "فارسی: " + persian + "\n"
                        + "اردو: " + urdu,
                "حذف کی تصدیق — Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                glossaryService.deleteEntry(entryId);
                loadGlossaryEntries();
                setStatus("اندراج کامیابی سے حذف ہو گیا۔ — Entry deleted successfully.");
            } catch (GlossaryException ex) {
                showError("حذف کرنے میں خرابی۔\n(Error deleting entry.)");
            }
        }
        // If NO is selected, do nothing — entry remains unchanged
    }

    // ── UI Helpers ───────────────────────────────────────────

    /**
     * Shows an error message dialog (NFR-US-06).
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this, message,
                "خرابی — Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Updates the status bar label.
     */
    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    // ── Inner Dialog Class (issue #10) ──────────────────────

    /**
     * A small dialog panel with named fields for Persian word and Urdu meaning.
     * Replaces the fragile {@code getComponent()} approach.
     */
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

        String getPersianWord() {
            return persianField.getText();
        }

        String getUrduMeaning() {
            return urduField.getText();
        }
    }
}
