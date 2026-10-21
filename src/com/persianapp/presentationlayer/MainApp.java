package com.persianapp.presentationlayer;

import com.persianapp.businesslayer.ChapterBao;
import com.persianapp.dataaccesslayer.ConnectionProvider;
import com.persianapp.dataaccesslayer.DatabaseManager;
import com.persianapp.dataaccesslayer.SqlChapterDao;

import javax.swing.*;
import java.awt.*;

// Desktop application entry point.
public class MainApp extends JFrame {

    // Launches the application window.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            ConnectionProvider connections = DatabaseManager::getConnection;
            SqlChapterDao      dao         = new SqlChapterDao(connections);
            ChapterBao         bao         = new ChapterBao(dao);
            ChapterController  controller  = new ChapterController(bao);

            new MainApp(controller).setVisible(true);
        });
    }

    // Creates the main application frame.
    public MainApp(ChapterController controller) {
        super("Persian Language Learning App — آموختن زبان فارسی (UC-01)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(780, 520);
        setMinimumSize(new Dimension(650, 400));
        setLocationRelativeTo(null);
        setContentPane(new ChapterPanel(controller));
    }
}
