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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class EmployeeController {
    @FXML
    private Button logoutButton;
    @FXML
    private Button createOrderButton;
    @FXML
    private Button viewCustomersButton;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label totalOrdersLabel;

    private String currentUserEid;

    public void setCurrentUserEid(String eid) {
        this.currentUserEid = eid;
        loadEmployeeInfo();
        loadTotalOrders();
    }

    private void loadEmployeeInfo() {
        if (currentUserEid != null && welcomeLabel != null) {
            String sql = "SELECT name FROM Employee WHERE eid = ?";
            try (Connection conn = DBConnect.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, currentUserEid);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String name = rs.getString("name");
                    welcomeLabel.setText("Welcome, " + name + " (Employee)");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                welcomeLabel.setText("Welcome, Employee");
            }
        }
    }

    private String getCurrentUserName() {
        if (currentUserEid != null) {
            String sql = "SELECT name FROM Employee WHERE eid = ?";
            try (Connection conn = DBConnect.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, currentUserEid);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("name");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return currentUserEid; // Fallback to eid if name not found
    }

    private void loadTotalOrders() {
        String sql = "SELECT COUNT(*) as total_orders FROM Orders";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                int totalOrders = rs.getInt("total_orders");
                if (totalOrdersLabel != null) {
                    totalOrdersLabel.setText("Total Orders Handled: " + totalOrders);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if (totalOrdersLabel != null) {
                totalOrdersLabel.setText("Total Orders Handled: Error loading");
            }
        }
    }

    @FXML
    protected void onCreateOrderClick(ActionEvent event) {
        try {
            // Set session information for the CreateSale controller
            SessionManager sessionManager = SessionManager.getInstance();
            sessionManager.setCurrentUser(currentUserEid, "employee", getCurrentUserName());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateSale.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1600, 900));
            stage.setTitle("Create Sale");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onViewCustomersClick(ActionEvent event) {
        try {
            // Set session information for the CustomerList controller
            SessionManager sessionManager = SessionManager.getInstance();
            sessionManager.setCurrentUser(currentUserEid, "employee", getCurrentUserName());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerList.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1600, 900));
            stage.setTitle("Customer List");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onAddProductClick(ActionEvent event) {
        try {
            // Set session information for the AddProduct controller
            SessionManager sessionManager = SessionManager.getInstance();
            sessionManager.setCurrentUser(currentUserEid, "employee", getCurrentUserName());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddProduct.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1600, 900));
            stage.setTitle("Add Product");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onLogoutClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(loginRoot, 1600, 900));
            stage.setTitle("Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
