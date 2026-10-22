package com.persianapp.dataaccesslayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.persianapp.businesslayer.ChapterLookup;
import com.persianapp.businesslayer.DataAccessException;

/**
 * Checks the chapters table (UC-01's data) for the UC-02 and UC-04 precondition.
 * Read-only — never modifies data.
 */
public class SqlChapterLookup implements ChapterLookup {

    private static final String EXISTS_SQL = "SELECT 1 FROM chapters WHERE id = ?";

    private final ConnectionProvider connections;

    public SqlChapterLookup(ConnectionProvider connections) {
        if (connections == null) {
            throw new IllegalArgumentException("Connection provider is required.");
        }
        this.connections = connections;
    }

    @Override
    public boolean exists(String chapterId) {
        try (Connection connection = connections.getConnection();
             PreparedStatement statement = connection.prepareStatement(EXISTS_SQL)) {
            statement.setString(1, chapterId);
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Could not check whether chapter " + chapterId + " exists", e);
        }
    }
}
