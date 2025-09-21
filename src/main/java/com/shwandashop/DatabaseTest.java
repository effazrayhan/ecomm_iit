package com.shwandashop;

import com.shwandashop.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseTest {
    public static void main(String[] args) {
        System.out.println("Testing database operations...");

        try {
            // Test 1: Add new product
            System.out.println("\n=== Test 1: Adding new product ===");
            testAddNewProduct();

            // Test 2: Add stock to existing product
            System.out.println("\n=== Test 2: Adding stock to existing product ===");
            testAddStockToExistingProduct();

            // Test 3: Verify database state
            System.out.println("\n=== Test 3: Verifying database state ===");
            verifyDatabaseState();

            System.out.println("\n✅ All database tests completed successfully!");

        } catch (SQLException e) {
            System.err.println("❌ Database test failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseManager.closeConnection();
        }
    }

    private static void testAddNewProduct() throws SQLException {
        Connection conn = DatabaseManager.getConnection();

        // Insert a test product
        String insertProductSql = "INSERT INTO Product (name, category, color, price) VALUES (?, ?, ?, ?)";
        String insertStockSql = "INSERT INTO Stock (pid, quantity) VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(insertProductSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, "Test Laptop");
            pstmt.setString(2, "Electronics");
            pstmt.setString(3, "Silver");
            pstmt.setDouble(4, 1299.99);
            pstmt.executeUpdate();

            // Get generated product ID
            int productId;
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    productId = rs.getInt(1);
                    System.out.println("✅ Product added successfully with ID: " + productId);
                } else {
                    throw new SQLException("Failed to get generated product ID");
                }
            }

            // Add stock for the product
            try (PreparedStatement stockStmt = conn.prepareStatement(insertStockSql)) {
                stockStmt.setInt(1, productId);
                stockStmt.setInt(2, 25);
                stockStmt.executeUpdate();
                System.out.println("✅ Stock added successfully: 25 units");
            }

            DatabaseManager.commitTransaction(conn);
        }
    }

    private static void testAddStockToExistingProduct() throws SQLException {
        Connection conn = DatabaseManager.getConnection();

        // First, find an existing product
        String findProductSql = "SELECT pid FROM Product WHERE name = 'Test Laptop'";
        String updateStockSql = "UPDATE Stock SET quantity = quantity + ? WHERE pid = ?";

        try (PreparedStatement findStmt = conn.prepareStatement(findProductSql);
             ResultSet rs = findStmt.executeQuery()) {

            if (rs.next()) {
                int productId = rs.getInt("pid");

                // Add more stock to existing product
                try (PreparedStatement updateStmt = conn.prepareStatement(updateStockSql)) {
                    updateStmt.setInt(1, 10);
                    updateStmt.setInt(2, productId);
                    int rowsAffected = updateStmt.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("✅ Stock updated successfully: Added 10 more units to product ID " + productId);
                    } else {
                        System.out.println("❌ No stock record found for product ID " + productId);
                    }
                }

                DatabaseManager.commitTransaction(conn);
            } else {
                System.out.println("❌ Test product not found");
            }
        }
    }

    private static void verifyDatabaseState() throws SQLException {
        Connection conn = DatabaseManager.getConnection();

        // Check products
        String productSql = "SELECT COUNT(*) as product_count FROM Product";
        String stockSql = "SELECT p.name, s.quantity FROM Product p JOIN Stock s ON p.pid = s.pid WHERE p.name = 'Test Laptop'";

        try (PreparedStatement pstmt = conn.prepareStatement(productSql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                System.out.println("✅ Total products in database: " + rs.getInt("product_count"));
            }
        }

        // Check specific product stock
        try (PreparedStatement pstmt = conn.prepareStatement(stockSql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                System.out.println("✅ Test product stock: " + rs.getString("name") + " - " + rs.getInt("quantity") + " units");
            }
        }
    }
}
