package com.persianapp.presentationlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.persianapp.businesslayer.ChapterStructure;
import com.persianapp.businesslayer.ChapterStructureBao;
import com.persianapp.businesslayer.StructureDao;

/** Manual try-out of the UC-02 screen with in-memory storage. Not part of the application. */
public class StructureDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChapterStructureBao bao = new ChapterStructureBao(new InMemoryStructureDao(), chapterId -> true);
            StructurePanel panel = new StructurePanel(new StructureController(bao));
            panel.loadChapter("demo-chapter");

            JFrame frame = new JFrame("UC-02 demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(panel);
            frame.setSize(760, 420);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static class InMemoryStructureDao implements StructureDao {
        private final Map<String, ChapterStructure> stored = new HashMap<>();

        @Override
        public Optional<ChapterStructure> findByChapterId(String chapterId) {
            return Optional.ofNullable(stored.get(chapterId));
        }

        @Override
        public void save(ChapterStructure structure) {
            stored.put(structure.getChapterId(), structure);
        }
    }
}