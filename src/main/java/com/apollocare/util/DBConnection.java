package com.apollocare.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * JDBC connection factory.
 *
 * Design note:
 * This uses DriverManager directly for interview clarity and simplicity.
 * In production, a JNDI DataSource (configured in Tomcat's context.xml)
 * would be used instead to benefit from connection pooling.
 *
 * Change USER and PASSWORD to match your MySQL installation.
 */
public class DBConnection {

    private static final String URL =
        "jdbc:mysql://localhost:3306/apollocare_db" +
        "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private static final String USER     = "root";
    private static final String PASSWORD = "tiger";   // change if needed

    static {
        try {
            // Explicitly load MySQL driver.
            // JDBC 4+ auto-loads from META-INF/services, but this makes
            // the dependency obvious and fails fast if the JAR is missing.
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(
                "MySQL JDBC Driver not found on classpath: " + e.getMessage());
        }
    }

    /**
     * Returns a new JDBC Connection.
     * Callers MUST close it — use try-with-resources:
     *   try (Connection conn = DBConnection.getConnection()) { ... }
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Prevent instantiation — this is a pure utility class
    private DBConnection() {}
}
