package com.persianapp.dataaccesslayer;

import java.sql.Connection;
import java.sql.SQLException;

// Supplies database connections to DAO classes via dependency injection.
@FunctionalInterface
public interface ConnectionProvider {
    Connection getConnection() throws SQLException;
}
