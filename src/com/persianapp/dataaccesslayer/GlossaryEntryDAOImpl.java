package com.persianapp.dataaccesslayer;

import com.persianapp.businesslayer.GlossaryEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * SQLite implementation of {@link GlossaryEntryDAO}.
 *
 * <p>All queries use {@link PreparedStatement} for safety and performance.
 * The connection is obtained from the shared {@link DatabaseManager}.</p>
 */
public class GlossaryEntryDAOImpl implements GlossaryEntryDAO {

    // ── SQL Constants ────────────────────────────────────────

    private static final String SQL_INSERT =
            "INSERT INTO glossary_entries (id, chapter_id, persian_word, urdu_meaning) VALUES (?, ?, ?, ?)";

    private static final String SQL_SELECT_BY_CHAPTER =
            "SELECT id, chapter_id, persian_word, urdu_meaning FROM glossary_entries WHERE chapter_id = ?";

    private static final String SQL_SELECT_BY_ID =
            "SELECT id, chapter_id, persian_word, urdu_meaning FROM glossary_entries WHERE id = ?";

    private static final String SQL_UPDATE =
            "UPDATE glossary_entries SET persian_word = ?, urdu_meaning = ? WHERE id = ?";

    private static final String SQL_DELETE_BY_ID =
            "DELETE FROM glossary_entries WHERE id = ?";

    private static final String SQL_DELETE_BY_CHAPTER =
            "DELETE FROM glossary_entries WHERE chapter_id = ?";

    // ── CRUD Operations ──────────────────────────────────────

    @Override
    public void addEntry(GlossaryEntry entry) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            ps.setString(1, entry.getId());
            ps.setString(2, entry.getChapterId());
            ps.setString(3, entry.getPersianWord());
            ps.setString(4, entry.getUrduMeaning());
            ps.executeUpdate();
        }
    }

    @Override
    public List<GlossaryEntry> getEntriesByChapterId(String chapterId) throws SQLException {
        List<GlossaryEntry> entries = new ArrayList<>();
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_CHAPTER)) {
            ps.setString(1, chapterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    entries.add(mapRow(rs));
                }
            }
        }
        return entries;
    }

    @Override
    public GlossaryEntry getEntryById(String id) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public void updateEntry(GlossaryEntry entry) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, entry.getPersianWord());
            ps.setString(2, entry.getUrduMeaning());
            ps.setString(3, entry.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteEntry(String id) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE_BY_ID)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteEntriesByChapterId(String chapterId) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE_BY_CHAPTER)) {
            ps.setString(1, chapterId);
            ps.executeUpdate();
        }
    }

    // ── Helper ───────────────────────────────────────────────

    /**
     * Maps a {@link ResultSet} row to a {@link GlossaryEntry} object.
     */
    private GlossaryEntry mapRow(ResultSet rs) throws SQLException {
        return new GlossaryEntry(
                rs.getString("id"),
                rs.getString("chapter_id"),
                rs.getString("persian_word"),
                rs.getString("urdu_meaning")
        );
    }
}
