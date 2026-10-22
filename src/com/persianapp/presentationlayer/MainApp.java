package com.persianapp.presentationlayer;

import com.persianapp.businesslayer.GlossaryService;
import com.persianapp.dataaccesslayer.DatabaseManager;
import com.persianapp.dataaccesslayer.GlossaryEntryDAOImpl;

import javax.swing.*;
import java.awt.*;

/**
 * Application entry point for the Persian Language Learning App.
 *
 * <p>Opens a {@link JFrame} containing the {@link GlossaryPanel}
 * for UC-04 glossary management. A temporary chapter selector
 * is provided until the UC-01 chapter screen is available.</p>
 */
public class MainApp {

    public static void main(String[] args) {
        // Ensure Swing runs on the Event Dispatch Thread
        SwingUtilities.invokeLater(MainApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall back to default L&F
        }

        // Wire up the layers
        GlossaryEntryDAOImpl dao = new GlossaryEntryDAOImpl();
        GlossaryService service = new GlossaryService(dao);
        GlossaryPanel glossaryPanel = new GlossaryPanel(service);

        // Main frame
        JFrame frame = new JFrame("فارسی زبان سیکھنے کی ایپ — Persian Language Learning App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 600));

        // Top panel: temporary chapter ID input (until UC-01 screen is ready)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JLabel chapterLabel = new JLabel("باب ID (Chapter ID):");
        chapterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField chapterField = new JTextField(20);
        chapterField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton loadButton = new JButton("لوڈ — Load");
        loadButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loadButton.addActionListener(e -> {
            String chapterId = chapterField.getText().trim();
            if (!chapterId.isEmpty()) {
                glossaryPanel.setChapterId(chapterId);
            } else {
                JOptionPane.showMessageDialog(frame,
                        "براہ کرم باب ID درج کریں۔\n(Please enter a Chapter ID.)",
                        "خرابی — Error",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        topPanel.add(chapterLabel);
        topPanel.add(chapterField);
        topPanel.add(loadButton);

        frame.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(glossaryPanel, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
