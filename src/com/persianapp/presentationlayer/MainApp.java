package com.persianapp.presentationlayer;

import com.persianapp.businesslayer.Chapter;
import com.persianapp.businesslayer.ChapterBao;
import com.persianapp.businesslayer.ChapterLookup;
import com.persianapp.businesslayer.ChapterStructureBao;
import com.persianapp.businesslayer.ContentService;
import com.persianapp.businesslayer.ContentValidator;
import com.persianapp.businesslayer.GlossaryEntryDAO;
import com.persianapp.businesslayer.GlossaryService;
import com.persianapp.businesslayer.StructureDao;
import com.persianapp.dataaccesslayer.ChapterDao;
import com.persianapp.dataaccesslayer.ComponentRepository;
import com.persianapp.dataaccesslayer.ConnectionProvider;
import com.persianapp.dataaccesslayer.DatabaseManager;
import com.persianapp.dataaccesslayer.GlossaryEntryDAOImpl;
import com.persianapp.dataaccesslayer.SqlChapterDao;
import com.persianapp.dataaccesslayer.SqlChapterLookup;
import com.persianapp.dataaccesslayer.SqlComponentRepository;
import com.persianapp.dataaccesslayer.SqlStructureDao;
import com.persianapp.dataaccesslayer.inmemory.InMemoryChapterDao;
import com.persianapp.dataaccesslayer.inmemory.InMemoryGlossaryEntryDAO;
import com.persianapp.dataaccesslayer.inmemory.InMemoryStructureAndComponentStore;
import com.persianapp.dataaccesslayer.inmemory.SampleDataSeeder;

import javax.swing.*;
import java.awt.*;

/**
 * Application entry point for the Persian Language Learning App.
 *
 * Supports two operational modes:
 *   1. MySQL / MariaDB Mode (default when database is running on localhost:3306)
 *   2. In-Memory Mode (runs without any database, pre-seeded with sample data)
 *
 * Wires together all four use cases into a single tabbed window:
 *   Tab 1 (UC-01): Chapter Management
 *   Tab 2 (UC-02): Chapter Structure Configuration
 *   Tab 3 (UC-03): Content Editing
 *   Tab 4 (UC-04): Glossary Management
 */
public class MainApp extends JFrame {

    public static void main(String[] args) {
        boolean forceNoDb = false;
        for (String arg : args) {
            if ("--no-db".equalsIgnoreCase(arg) || "-no-db".equalsIgnoreCase(arg)
                    || "--in-memory".equalsIgnoreCase(arg) || "-in-memory".equalsIgnoreCase(arg)) {
                forceNoDb = true;
                break;
            }
        }
        if (System.getProperty("no-db") != null || "memory".equalsIgnoreCase(System.getProperty("db.mode"))) {
            forceNoDb = true;
        }

        final boolean inMemory = forceNoDb || !DatabaseManager.isAvailable();

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            new MainApp(inMemory).setVisible(true);
        });
    }

    public MainApp() {
        this(false);
    }

    public MainApp(boolean inMemoryMode) {
        super(inMemoryMode
                ? "فارسی زبان سیکھنے کی ایپ — Persian Language Learning App [In-Memory Mode]"
                : "فارسی زبان سیکھنے کی ایپ — Persian Language Learning App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 700);
        setMinimumSize(new Dimension(850, 580));
        setLocationRelativeTo(null);

        ChapterDao          chapterDao;
        ChapterLookup       chapterLookup;
        StructureDao        structureDao;
        ComponentRepository componentRepo;
        GlossaryEntryDAO    glossaryDao;

        if (inMemoryMode) {
            InMemoryChapterDao memChapterDao = new InMemoryChapterDao();
            InMemoryStructureAndComponentStore memStore = new InMemoryStructureAndComponentStore();
            ChapterLookup memLookup = id -> memChapterDao.selectById(id).isPresent();
            InMemoryGlossaryEntryDAO memGlossaryDao = new InMemoryGlossaryEntryDAO(memLookup);

            SampleDataSeeder.seed(memChapterDao, memStore, memGlossaryDao);

            chapterDao    = memChapterDao;
            chapterLookup = memLookup;
            structureDao  = memStore;
            componentRepo = memStore;
            glossaryDao   = memGlossaryDao;
        } else {
            ConnectionProvider connections = DatabaseManager::getConnection;
            chapterDao    = new SqlChapterDao(connections);
            chapterLookup = new SqlChapterLookup(connections);
            structureDao  = new SqlStructureDao(connections);
            componentRepo = new SqlComponentRepository();
            glossaryDao   = new GlossaryEntryDAOImpl();
        }

        // ── UC-01 wiring ────────────────────────────────────────
        ChapterBao        chapterBao   = new ChapterBao(chapterDao);
        ChapterController chapterCtrl  = new ChapterController(chapterBao);
        ChapterPanel      chapterPanel = new ChapterPanel(chapterCtrl);

        // ── UC-02 wiring ────────────────────────────────────────
        ChapterStructureBao structureBao  = new ChapterStructureBao(structureDao, chapterLookup);
        StructureController structureCtrl = new StructureController(structureBao);
        StructurePanel    structurePanel  = new StructurePanel(structureCtrl);

        // ── UC-03 wiring ────────────────────────────────────────
        ContentService          contentService = new ContentService(componentRepo, new ContentValidator());
        ContentEditorController editorCtrl     = new ContentEditorController(contentService);
        ContentEditorPanel      editorPanel    = new ContentEditorPanel(editorCtrl, null);

        // ── UC-04 wiring ────────────────────────────────────────
        GlossaryService glossaryService = new GlossaryService(glossaryDao);
        GlossaryPanel   glossaryPanel   = new GlossaryPanel(glossaryService);

        // ── Cross-tab chapter selection listener ─────────────────
        chapterPanel.setOnChapterSelectedListener((Chapter selected) -> {
            String id = selected.getId();
            structurePanel.loadChapter(id);
            editorPanel.refreshForChapter(id);
            glossaryPanel.setChapterId(id);
        });

        // ── Tabbed pane ──────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tabs.addTab("UC-01 — ابواب (Chapters)",   chapterPanel);
        tabs.addTab("UC-02 — ساخت (Structure)",  structurePanel);
        tabs.addTab("UC-03 — مواد (Content)",    editorPanel);
        tabs.addTab("UC-04 — لغت (Glossary)",    glossaryPanel);

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

        // ── Mode status banner ───────────────────────────────────
        JPanel banner = new JPanel(new BorderLayout());
        JLabel statusText = new JLabel();
        statusText.setFont(new Font("Segoe UI", Font.BOLD, 12));

        if (inMemoryMode) {
            banner.setBackground(new Color(230, 245, 255));
            banner.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(175, 215, 245)),
                    BorderFactory.createEmptyBorder(7, 14, 7, 14)
            ));
            statusText.setForeground(new Color(15, 75, 140));
            statusText.setText("⚡ چل رہا ہے: اندرونی میموری موڈ (ڈیٹا بیس کے بغیر) — Running in In-Memory Mode (No Database Required) — All 4 Use Cases Active");
        } else {
            banner.setBackground(new Color(236, 248, 236));
            banner.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(180, 225, 180)),
                    BorderFactory.createEmptyBorder(7, 14, 7, 14)
            ));
            statusText.setForeground(new Color(25, 100, 25));
            statusText.setText("🟢 متصل: MySQL ڈیٹا بیس (localhost:3306/persian_app) — Connected to MySQL Database");
        }
        banner.add(statusText, BorderLayout.WEST);

        // ── Root container ───────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout());
        root.add(banner, BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);

        setContentPane(root);
    }
}
