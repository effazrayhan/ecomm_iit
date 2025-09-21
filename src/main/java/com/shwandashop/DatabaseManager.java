package com.shwandashop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.shwandashop.productHandling.Product;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:identifier.sqlite";
    private static final Logger logger = Logger.getLogger(DatabaseManager.class.getName());

    // Connection pool - simple implementation for SQLite
    private static Connection connection = null;

    public static synchronized Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL);
                connection.setAutoCommit(false); // Enable transaction support
                logger.info("Database connection established");
            }
            return connection;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get database connection", e);
            throw e;
        }
    }

    public static synchronized void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.commit(); // Commit any pending transactions
                connection.close();
                connection = null;
                logger.info("Database connection closed");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error closing database connection", e);
        }
    }

    public static synchronized void commitTransaction(Connection conn) throws SQLException {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.commit();
                logger.info("Transaction committed successfully");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to commit transaction", e);
            throw e;
        }
    }

    public static synchronized void rollbackTransaction(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.rollback();
                logger.info("Transaction rolled back");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to rollback transaction", e);
        }
    }

    public static synchronized Savepoint setSavepoint(Connection conn, String name) throws SQLException {
        try {
            if (conn != null && !conn.isClosed()) {
                Savepoint savepoint = conn.setSavepoint(name);
                logger.info("Savepoint set: " + name);
                return savepoint;
            }
            return null;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to set savepoint", e);
            throw e;
        }
    }

    public static synchronized void rollbackToSavepoint(Connection conn, Savepoint savepoint) throws SQLException {
        try {
            if (conn != null && !conn.isClosed() && savepoint != null) {
                conn.rollback(savepoint);
                logger.info("Rolled back to savepoint");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to rollback to savepoint", e);
            throw e;
        }
    }

    public static List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.pid, p.name, p.color, p.category, p.price, COALESCE(s.quantity, 0) AS quantity FROM Product p LEFT JOIN Stock s ON p.pid = s.pid";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Product product = new Product(
                    rs.getString("name"),
                    rs.getInt("pid"),
                    rs.getString("category"),
                    rs.getString("color"),
                    rs.getDouble("price"),
                    0.0,
                    null,
                    rs.getInt("quantity")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to fetch products", e);
        }
        return products;
    }

    public static void runSqlScript(String resourcePath) {
        try (Connection conn = getConnection()) {
            java.io.InputStream is = DatabaseManager.class.getClassLoader().getResourceAsStream(resourcePath);
            if (is == null) {
                logger.severe("SQL script not found: " + resourcePath);
                return;
            }
            java.util.Scanner scanner = new java.util.Scanner(is).useDelimiter(";");
            java.sql.Statement stmt = conn.createStatement();
            while (scanner.hasNext()) {
                String sqlStatement = scanner.next().trim();
                if (!sqlStatement.isEmpty()) {
                    stmt.execute(sqlStatement);
                }
            }
            stmt.close();
            conn.commit();
            logger.info("Executed SQL script: " + resourcePath);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to execute SQL script: " + resourcePath, e);
        }
    }

    public static void initializeDatabase() {
        runSqlScript("sql/DatabaseInitializer.sql");
        runSqlScript("sql/ProductSeed.sql");
    }
}
