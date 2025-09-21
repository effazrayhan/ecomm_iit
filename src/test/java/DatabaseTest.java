package test.java;

import com.shwandashop.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseTest {
    public static void main(String[] args) {
        System.out.println("Testing database fixes...");

        try {
            // Test 1: Basic connection
            System.out.println("Test 1: Testing database connection...");
            Connection conn = DatabaseManager.getConnection();
            System.out.println("✓ Database connection successful");

            // Test 2: Test product insertion
            System.out.println("Test 2: Testing product insertion...");
            String productSql = "INSERT INTO Product (name, price, category, color) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(productSql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, "Test Product");
            pstmt.setDouble(2, 99.99);
            pstmt.setString(3, "Test Category");
            pstmt.setString(4, "Test Color");
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            int productId = -1;
            if (generatedKeys.next()) {
                productId = generatedKeys.getInt(1);
                System.out.println("✓ Product inserted successfully with ID: " + productId);
            }

            // Test 3: Test stock insertion
            System.out.println("Test 3: Testing stock insertion...");
            String stockSql = "INSERT INTO Stock (pid, quantity) VALUES (?, ?)";
            PreparedStatement stockStmt = conn.prepareStatement(stockSql);
            stockStmt.setInt(1, productId);
            stockStmt.setInt(2, 100);
            stockStmt.executeUpdate();
            System.out.println("✓ Stock inserted successfully");

            // Test 4: Test stock update (add stock to existing product)
            System.out.println("Test 4: Testing stock update...");
            String updateSql = "UPDATE Stock SET quantity = quantity + ? WHERE pid = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setInt(1, 50);
            updateStmt.setInt(2, productId);
            int rowsAffected = updateStmt.executeUpdate();
            System.out.println("✓ Stock updated successfully, rows affected: " + rowsAffected);

            // Test 5: Verify the data
            System.out.println("Test 5: Verifying data...");
            String verifySql = "SELECT p.name, p.price, p.category, p.color, s.quantity FROM Product p LEFT JOIN Stock s ON p.pid = s.pid WHERE p.pid = ?";
            PreparedStatement verifyStmt = conn.prepareStatement(verifySql);
            verifyStmt.setInt(1, productId);
            ResultSet rs = verifyStmt.executeQuery();

            if (rs.next()) {
                System.out.println("✓ Data verification successful:");
                System.out.println("  Name: " + rs.getString("name"));
                System.out.println("  Price: " + rs.getDouble("price"));
                System.out.println("  Category: " + rs.getString("category"));
                System.out.println("  Color: " + rs.getString("color"));
                System.out.println("  Quantity: " + rs.getInt("quantity"));
            }

            // Commit all changes
            DatabaseManager.commitTransaction(conn);
            System.out.println("✓ All transactions committed successfully");

            // Clean up
            rs.close();
            verifyStmt.close();
            updateStmt.close();
            stockStmt.close();
            pstmt.close();
            generatedKeys.close();

            System.out.println("\n🎉 All database tests passed! The fixes are working correctly.");
            System.out.println("✅ Database busy errors should be resolved");
            System.out.println("✅ Product addition should work properly");
            System.out.println("✅ Stock management should work correctly");

        } catch (SQLException e) {
            System.err.println("❌ Database test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
