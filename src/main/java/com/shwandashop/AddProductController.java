package com.shwandashop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddProductController {
    @FXML
    private TextField productId;
    @FXML
    private TextField productName;
    @FXML
    private TextField productQuantity;
    @FXML
    private TextField productBuyingPrice;
    @FXML
    private TextField productSellingPrice;
    @FXML
    private TextField productCategory;
    @FXML
    private TextField productColor;
    @FXML
    private TextField productExpiryDate;
    @FXML
    private Button addProductButton;
    @FXML
    private Button returnButton;

    @FXML
    protected void initialize() {
        addProductButton.setOnAction(this::handleAddProduct);
        returnButton.setOnAction(this::onReturnClick);

        // Add listener for product name field to auto-search
        productName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() >= 2) { // Start searching after 2 characters
                searchProductByName(newValue);
            }
        });
    }

    private void searchProductByName(String productName) {
        String sql = "SELECT pid, name, buying_price, selling_price, category, color, expiry_date FROM Product WHERE name LIKE ? LIMIT 1";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + productName + "%");
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Product found, autofill the fields
                productId.setText(String.valueOf(rs.getInt("pid")));
                productBuyingPrice.setText(String.valueOf(rs.getDouble("buying_price")));
                productSellingPrice.setText(String.valueOf(rs.getDouble("selling_price")));
                productCategory.setText(rs.getString("category"));
                productColor.setText(rs.getString("color"));
                productExpiryDate.setText(rs.getString("expiry_date"));
            }
        } catch (SQLException e) {
            System.out.println("Error searching product: " + e.getMessage());
        } finally {
            // Clean up resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    private void handleAddProduct(ActionEvent event) {
        Connection conn = null;

        try {
            String name = productName.getText();
            String category = productCategory.getText();
            String color = productColor.getText();
            double buyingPrice = Double.parseDouble(productBuyingPrice.getText());
            double sellingPrice = Double.parseDouble(productSellingPrice.getText());
            String expiryDateStr = productExpiryDate.getText();
            int quantity = Integer.parseInt(productQuantity.getText());

            if (name.isEmpty() || category.isEmpty() || color.isEmpty() ||
                productBuyingPrice.getText().isEmpty() || productSellingPrice.getText().isEmpty()) {
                showAlert("Please fill in all required fields.");
                return;
            }

            if (buyingPrice <= 0 || sellingPrice <= 0) {
                showAlert("Prices must be greater than zero.");
                return;
            }

            if (buyingPrice > sellingPrice) {
                showAlert("Buying price cannot be greater than selling price.");
                return;
            }

            // Get database connection
            conn = DatabaseManager.getConnection();

            // Check if product with same name already exists
            String checkSql = "SELECT pid, name FROM Product WHERE name = ?";
            PreparedStatement pstmt = null;
            ResultSet rs = null;

            try {
                pstmt = conn.prepareStatement(checkSql);
                pstmt.setString(1, name);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    // Product exists - ask user if they want to add stock to existing product
                    int existingPid = rs.getInt("pid");
                    String existingName = rs.getString("name");

                    Alert confirmation = new Alert(AlertType.CONFIRMATION);
                    confirmation.setTitle("Product Already Exists");
                    confirmation.setHeaderText("A product with the name '" + existingName + "' already exists.");
                    confirmation.setContentText("Do you want to add stock to this existing product?");

                    // Make variables effectively final for lambda
                    final int finalExistingPid = existingPid;
                    final int finalQuantity = quantity;
                    final Connection finalConn = conn;

                    confirmation.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            // User wants to add stock to existing product
                            addStockToExistingProduct(finalConn, finalExistingPid, finalQuantity);
                        } else {
                            // User cancelled - do nothing
                            showAlert("Operation cancelled.");
                        }
                    });
                    return;
                }
            } finally {
                // Clean up resources
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        System.out.println("Error closing ResultSet: " + e.getMessage());
                    }
                }
                if (pstmt != null) {
                    try {
                        pstmt.close();
                    } catch (SQLException e) {
                        System.out.println("Error closing PreparedStatement: " + e.getMessage());
                    }
                }
            }

            // Product doesn't exist - create new product
            createNewProduct(conn, name, category, color, buyingPrice, sellingPrice, expiryDateStr, quantity);

        } catch (NumberFormatException e) {
            showAlert("Please enter valid numbers for price and quantity.");
        } catch (SQLException e) {
            showAlert("Database error: " + e.getMessage());
            if (conn != null) {
                DatabaseManager.rollbackTransaction(conn);
            }
        } catch (Exception e) {
            showAlert("Error: " + e.getMessage());
            if (conn != null) {
                DatabaseManager.rollbackTransaction(conn);
            }
        }
    }

    private void createNewProduct(Connection conn, String name, String category, String color, double buyingPrice, double sellingPrice, String expiryDateStr, int quantity) {
        // Insert product into Product table (without specifying pid - let it auto-generate)
        String productSql = "INSERT INTO Product (name, buying_price, selling_price, category, color, expiry_date) VALUES (?, ?, ?, ?, ?, ?)";
        int generatedPid = -1;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;

        try {
            pstmt = conn.prepareStatement(productSql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, name);
            pstmt.setDouble(2, buyingPrice);
            pstmt.setDouble(3, sellingPrice);
            pstmt.setString(4, category);
            pstmt.setString(5, color);
            pstmt.setString(6, expiryDateStr.isEmpty() ? null : expiryDateStr);
            pstmt.executeUpdate();

            // Get the auto-generated product ID
            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                generatedPid = generatedKeys.getInt(1);
                System.out.println("Product added successfully with ID: " + generatedPid);
            }
        } catch (SQLException e) {
            showAlert("Database error while adding product: " + e.getMessage());
            DatabaseManager.rollbackTransaction(conn);
            return;
        } finally {
            // Clean up resources
            if (generatedKeys != null) {
                try {
                    generatedKeys.close();
                } catch (SQLException e) {
                    System.out.println("Error closing ResultSet: " + e.getMessage());
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    System.out.println("Error closing PreparedStatement: " + e.getMessage());
                }
            }
        }

        // Insert stock into Stock table using the auto-generated product ID
        String stockSql = "INSERT INTO Stock (pid, quantity) VALUES (?, ?)";
        PreparedStatement stockStmt = null;

        try {
            stockStmt = conn.prepareStatement(stockSql);
            stockStmt.setInt(1, generatedPid);
            stockStmt.setInt(2, quantity);
            stockStmt.executeUpdate();
            System.out.println("Stock added successfully!");

            // Commit the transaction
            DatabaseManager.commitTransaction(conn);
        } catch (SQLException e) {
            showAlert("Database error while adding stock: " + e.getMessage());
            DatabaseManager.rollbackTransaction(conn);
            return;
        } finally {
            if (stockStmt != null) {
                try {
                    stockStmt.close();
                } catch (SQLException e) {
                    System.out.println("Error closing PreparedStatement: " + e.getMessage());
                }
            }
        }

        showAlert("Product and stock added successfully! Product ID: " + generatedPid);

        // Navigate to Product List
        navigateToProductList();
    }

    private void addStockToExistingProduct(Connection conn, int productId, int additionalQuantity) {
        // Update existing stock by adding the additional quantity
        String updateSql = "UPDATE Stock SET quantity = quantity + ? WHERE pid = ?";
        PreparedStatement pstmt = null;

        try {
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setInt(1, additionalQuantity);
            pstmt.setInt(2, productId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // Commit the transaction
                DatabaseManager.commitTransaction(conn);
                showAlert("Stock added successfully! Added " + additionalQuantity + " units to existing product (ID: " + productId + ").");
                navigateToProductList();
            } else {
                showAlert("Error: Could not find stock record for the product.");
                DatabaseManager.rollbackTransaction(conn);
            }
        } catch (SQLException e) {
            showAlert("Database error while updating stock: " + e.getMessage());
            DatabaseManager.rollbackTransaction(conn);
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    System.out.println("Error closing PreparedStatement: " + e.getMessage());
                }
            }
        }
    }

    private void navigateToProductList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ProductList.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) addProductButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1600, 900));
            stage.setTitle("Product List");
        } catch (Exception e) {
            showAlert("Error navigating to product list: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onReturnClick(ActionEvent event) {
        try {
            SessionManager sessionManager = SessionManager.getInstance();
            String userRole = sessionManager.getCurrentUserRole();

            FXMLLoader loader;
            String dashboardTitle;

            if ("admin".equals(userRole)) {
                loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
                dashboardTitle = "Admin Dashboard";
            } else {
                loader = new FXMLLoader(getClass().getResource("EmployeeDashboard.fxml"));
                dashboardTitle = "Employee Dashboard";
            }

            Parent root = loader.load();
            Stage stage = (Stage) returnButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1600, 900));
            stage.setTitle(dashboardTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
