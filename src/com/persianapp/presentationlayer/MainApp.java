package com.persianapp.presentationlayer;

import com.persianapp.businesslayer.ChapterService;
import com.persianapp.dataaccesslayer.ChapterRepository;
import com.persianapp.dataaccesslayer.MySQLChapterRepository;

import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame {

    // Launches the UC-01 Chapter Management desktop application.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            ChapterRepository repository = new MySQLChapterRepository();
            ChapterService service       = new ChapterService(repository);
            ChapterController controller = new ChapterController(service);

            new MainApp(controller).setVisible(true);
        });
    }

    // Constructs the UC-01 Chapter application window.
    public MainApp(ChapterController controller) {
        super("Persian Language Learning App — آموختن زبان فارسی (UC-01)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(780, 520);
        setMinimumSize(new Dimension(650, 400));
        setLocationRelativeTo(null);

        ChapterPanel panel = new ChapterPanel(controller);
        setContentPane(panel);
    }
}
