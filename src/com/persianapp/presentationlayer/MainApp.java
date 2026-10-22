package com.persianapp.presentationlayer;

import com.persianapp.businesslayer.Chapter;
import com.persianapp.businesslayer.ChapterBao;
import com.persianapp.businesslayer.ChapterStructureBao;
import com.persianapp.businesslayer.ContentService;
import com.persianapp.businesslayer.ContentValidator;
import com.persianapp.businesslayer.GlossaryService;
import com.persianapp.dataaccesslayer.ComponentRepository;
import com.persianapp.dataaccesslayer.ConnectionProvider;
import com.persianapp.dataaccesslayer.DatabaseManager;
import com.persianapp.dataaccesslayer.GlossaryEntryDAOImpl;
import com.persianapp.dataaccesslayer.SqlChapterDao;
import com.persianapp.dataaccesslayer.SqlChapterLookup;
import com.persianapp.dataaccesslayer.SqlComponentRepository;
import com.persianapp.dataaccesslayer.SqlStructureDao;

import javax.swing.*;
import java.awt.*;

/**
 * Application entry point for the Persian Language Learning App.
 *
 * Wires together all four use cases into a single tabbed window:
 *   Tab 1 (UC-01): Chapter Management
 *   Tab 2 (UC-02): Chapter Structure Configuration
 *   Tab 3 (UC-03): Content Editing
 *   Tab 4 (UC-04): Glossary Management
 *
 * Selecting a chapter in Tab 1 automatically propagates the chapter ID
 * to the Structure, Content, and Glossary tabs.
 */
public class MainApp extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            new MainApp().setVisible(true);
        });
    }

    public MainApp() {
        super("فارسی زبان سیکھنے کی ایپ — Persian Language Learning App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 680);
        setMinimumSize(new Dimension(800, 550));
        setLocationRelativeTo(null);

        // ── Shared connection provider ──────────────────────────
        ConnectionProvider connections = DatabaseManager::getConnection;

        // ── UC-01 wiring ────────────────────────────────────────
        SqlChapterDao    chapterDao = new SqlChapterDao(connections);
        ChapterBao       chapterBao = new ChapterBao(chapterDao);
        ChapterController chapterCtrl = new ChapterController(chapterBao);
        ChapterPanel     chapterPanel = new ChapterPanel(chapterCtrl);

        // ── UC-02 wiring ────────────────────────────────────────
        SqlChapterLookup  chapterLookup   = new SqlChapterLookup(connections);
        SqlStructureDao   structureDao    = new SqlStructureDao(connections);
        ChapterStructureBao structureBao  = new ChapterStructureBao(structureDao, chapterLookup);
        StructureController structureCtrl = new StructureController(structureBao);
        StructurePanel    structurePanel  = new StructurePanel(structureCtrl);

        // ── UC-03 wiring ────────────────────────────────────────
        ComponentRepository  componentRepo  = new SqlComponentRepository();
        ContentService       contentService = new ContentService(componentRepo, new ContentValidator());
        ContentEditorController editorCtrl  = new ContentEditorController(contentService);
        ContentEditorPanel   editorPanel    = new ContentEditorPanel(editorCtrl, null);

        // ── UC-04 wiring ────────────────────────────────────────
        GlossaryEntryDAOImpl glossaryDao    = new GlossaryEntryDAOImpl();
        GlossaryService      glossaryService = new GlossaryService(glossaryDao);
        GlossaryPanel        glossaryPanel   = new GlossaryPanel(glossaryService);

        // ── Cross-tab chapter selection listener ─────────────────
        // When a chapter is selected in the UC-01 panel, propagate it to
        // the Structure (UC-02), Content (UC-03), and Glossary (UC-04) tabs.
        chapterPanel.setOnChapterSelectedListener((Chapter selected) -> {
            String id = selected.getId();
            structurePanel.loadChapter(id);
            editorPanel.refreshForChapter(id);
            glossaryPanel.setChapterId(id);
        });

        // ── Tabbed pane ──────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tabs.addTab("UC-01 — ابواب", chapterPanel);
        tabs.addTab("UC-02 — ساخت",  structurePanel);
        tabs.addTab("UC-03 — مواد",   editorPanel);
        tabs.addTab("UC-04 — لغت",    glossaryPanel);

        // Show a hint when switching to a use-case tab without a chapter selected
        tabs.addChangeListener(e -> {
            int idx = tabs.getSelectedIndex();
            if (idx > 0 && chapterPanel.getSelectedChapter() == null) {
                JOptionPane.showMessageDialog(this,
                        "براہ کرم پہلے 'UC-01 — ابواب' ٹیب میں ایک باب منتخب کریں۔\n"
                        + "(Please select a chapter in the 'UC-01 — Chapters' tab first.)",
                        "باب منتخب کریں — Select a Chapter",
                        JOptionPane.INFORMATION_MESSAGE);
                tabs.setSelectedIndex(0);
            }
        });

        setContentPane(tabs);
    }
}
