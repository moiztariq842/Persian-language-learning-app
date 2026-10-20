package com.persianapp.presentationlayer;

import com.persianapp.businesslayer.GlossaryEntry;
import com.persianapp.businesslayer.GlossaryService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
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

    // ── RTL Font for Persian / Urdu ──────────────────────────

    private static final Font RTL_FONT = new Font("Tahoma", Font.PLAIN, 16);
    private static final Font UI_FONT = new Font("Tahoma", Font.PLAIN, 14);

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
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
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
        } catch (SQLException ex) {
            showError("لغت لوڈ کرنے میں خرابی: " + ex.getMessage());
        }
    }

    // ── Event Handlers ───────────────────────────────────────

    /**
     * Handles the Add button click (FR-GL-02).
     * Shows a dialog for entering a new Persian word and Urdu meaning.
     */
    private void handleAdd() {
        if (currentChapterId == null || currentChapterId.isBlank()) {
            showError("براہ کرم پہلے ایک باب منتخب کریں۔\n(Please select a chapter first.)");
            return;
        }

        JPanel inputPanel = createInputDialog("", "");
        JTextField persianField = (JTextField) inputPanel.getComponent(1);
        JTextField urduField = (JTextField) inputPanel.getComponent(3);

        int result = JOptionPane.showConfirmDialog(
                this, inputPanel,
                "نیا لغت اندراج — New Glossary Entry",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                glossaryService.addEntry(
                        currentChapterId,
                        persianField.getText(),
                        urduField.getText()
                );
                loadGlossaryEntries();
                setStatus("اندراج کامیابی سے شامل ہو گیا۔ — Entry added successfully.");
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            } catch (SQLException ex) {
                showError("محفوظ کرنے میں خرابی: " + ex.getMessage());
            }
        }
    }

    /**
     * Handles the Edit button click (FR-GL-04).
     * Populates the dialog with the selected entry's values.
     */
    private void handleEdit() {
        int selectedRow = glossaryTable.getSelectedRow();
        if (selectedRow < 0) {
            showError("براہ کرم ترمیم کے لیے ایک اندراج منتخب کریں۔\n(Please select an entry to edit.)");
            return;
        }

        String entryId = (String) tableModel.getValueAt(selectedRow, 0);
        String currentPersian = (String) tableModel.getValueAt(selectedRow, 1);
        String currentUrdu = (String) tableModel.getValueAt(selectedRow, 2);

        JPanel inputPanel = createInputDialog(currentPersian, currentUrdu);
        JTextField persianField = (JTextField) inputPanel.getComponent(1);
        JTextField urduField = (JTextField) inputPanel.getComponent(3);

        int result = JOptionPane.showConfirmDialog(
                this, inputPanel,
                "لغت اندراج میں ترمیم — Edit Glossary Entry",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                GlossaryEntry entry = new GlossaryEntry(
                        entryId,
                        currentChapterId,
                        persianField.getText(),
                        urduField.getText()
                );
                glossaryService.updateEntry(entry);
                loadGlossaryEntries();
                setStatus("اندراج کامیابی سے اپ ڈیٹ ہو گیا۔ — Entry updated successfully.");
            } catch (IllegalArgumentException ex) {
                // BR-06: failed validation does not replace saved data
                showError(ex.getMessage());
            } catch (SQLException ex) {
                showError("اپ ڈیٹ کرنے میں خرابی: " + ex.getMessage());
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
        String urdu = (String) tableModel.getValueAt(selectedRow, 2);

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
            } catch (SQLException ex) {
                showError("حذف کرنے میں خرابی: " + ex.getMessage());
            }
        }
        // If NO is selected, do nothing — entry remains unchanged
    }

    // ── UI Helpers ───────────────────────────────────────────

    /**
     * Creates an input dialog panel with two RTL text fields
     * for Persian word and Urdu meaning.
     */
    private JPanel createInputDialog(String persianValue, String urduValue) {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));

        JLabel persianLabel = new JLabel("فارسی لفظ (Persian Word):");
        persianLabel.setFont(UI_FONT);
        JTextField persianField = new JTextField(persianValue, 25);
        persianField.setFont(RTL_FONT);
        persianField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel urduLabel = new JLabel("اردو معنی (Urdu Meaning):");
        urduLabel.setFont(UI_FONT);
        JTextField urduField = new JTextField(urduValue, 25);
        urduField.setFont(RTL_FONT);
        urduField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        panel.add(persianLabel);
        panel.add(persianField);
        panel.add(urduLabel);
        panel.add(urduField);

        return panel;
    }

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
}
