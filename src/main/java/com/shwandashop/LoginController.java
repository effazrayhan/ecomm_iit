package com.shwandashop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    private String checkCredentials(String username, String password) {
        String url = "jdbc:sqlite:identifier.sqlite";
        String sql = "SELECT pass, role FROM Credentials WHERE eid = ?";
        try (Connection conn = DriverManager.getConnection(url)) {
            String storedPassword = null;
            String role = null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    storedPassword = rs.getString("pass");
                    role = rs.getString("role");
                }
            }
            if (storedPassword != null && storedPassword.equals(password)) {
                return role != null ? role : "employee"; // Default to employee if no role
            }
            return null;
        } catch (SQLException s) {
            System.out.println("Connection to database failed: " + s.getMessage());
            return null;
        }
    }

    private String getUserName(String username) {
        String url = "jdbc:sqlite:identifier.sqlite";
        String sql = "SELECT name FROM Employee WHERE eid = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException s) {
            System.out.println("Connection to database failed: " + s.getMessage());
        }
        return username; // Fallback to username if name not found
    }

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username and password cannot be empty.");
        } else {
            String role = checkCredentials(username, password);
            if (role != null) {
                try {
                    // Initialize session manager with user information
                    SessionManager sessionManager = SessionManager.getInstance();
                    sessionManager.setCurrentUser(username, role, getUserName(username));

                    Parent dashboardRoot;
                    String dashboardTitle;

                    if ("admin".equals(role)) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
                        dashboardRoot = loader.load();
                        dashboardTitle = "Admin Dashboard";
                    } else {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("EmployeeDashboard.fxml"));
                        dashboardRoot = loader.load();
                        EmployeeController controller = loader.getController();
                        controller.setCurrentUserEid(username);
                        dashboardTitle = "Employee Dashboard";
                    }

                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    stage.setScene(new Scene(dashboardRoot, 1600, 900));
                    stage.setTitle(dashboardTitle);
                } catch (Exception e) {
                    errorLabel.setText("Failed to load dashboard: " + e.getMessage());
                }
            } else {
                errorLabel.setText("Invalid username or password.");
            }
        }
    }
}
