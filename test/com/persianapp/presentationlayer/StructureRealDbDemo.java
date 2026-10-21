package com.persianapp.presentationlayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.persianapp.businesslayer.ChapterStructureBao;
import com.persianapp.dataaccesslayer.ConnectionProvider;
import com.persianapp.dataaccesslayer.SqlChapterLookup;
import com.persianapp.dataaccesslayer.SqlStructureDao;
import com.persianapp.dataaccesslayer.StructureSchema;

/**
 * Manual try-out of the UC-02 screen on a real local MariaDB (XAMPP defaults).
 * Uses its own database, persian_app_demo. Not part of the application.
 */
public class StructureRealDbDemo {

    private static final String SERVER_URL = "jdbc:mariadb://localhost:3306/";
    private static final String DATABASE = "persian_app_demo";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static final String CHAPTER_ID = "demo-chapter";

    public static void main(String[] args) {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            prepareDemoDatabase();
        } catch (ClassNotFoundException e) {
            showError("The MariaDB driver jar is not on the classpath.\n" + e.getMessage());
            return;
        } catch (SQLException e) {
            showError("Could not reach MariaDB. Start MySQL in XAMPP.\n" + e.getMessage());
            return;
        }

        ConnectionProvider connections =
                () -> DriverManager.getConnection(SERVER_URL + DATABASE, USER, PASSWORD);

        SwingUtilities.invokeLater(() -> {
            ChapterStructureBao bao = new ChapterStructureBao(
                    new SqlStructureDao(connections), new SqlChapterLookup(connections));
            StructurePanel panel = new StructurePanel(new StructureController(bao));
            panel.loadChapter(CHAPTER_ID);

            JFrame frame = new JFrame("UC-02 demo (real MariaDB)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(panel);
            frame.setSize(760, 420);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "UC-02 demo", JOptionPane.ERROR_MESSAGE);
    }

    private static void prepareDemoDatabase() throws SQLException {
        try (Connection server = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
                Statement statement = server.createStatement()) {
            statement.execute("CREATE DATABASE IF NOT EXISTS " + DATABASE
                    + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        }
        try (Connection connection = DriverManager.getConnection(SERVER_URL + DATABASE, USER, PASSWORD);
                Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS chapters ("
                    + "id VARCHAR(36) NOT NULL PRIMARY KEY, "
                    + "title VARCHAR(255) NOT NULL) ENGINE=InnoDB");
            statement.execute("INSERT IGNORE INTO chapters (id, title) VALUES ('"
                    + CHAPTER_ID + "', 'Demo chapter')");
            StructureSchema.create(connection);
        }
    }
}