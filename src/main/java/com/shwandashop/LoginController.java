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

    private boolean checkCredentials(String username, String password) {
        String url = "jdbc:sqlite:identifier.sqlite";
        String sql = "SELECT pass FROM Credentials WHERE eid = ?";
        try (Connection conn = DriverManager.getConnection(url)) {
            // String hashedPassword = password;
            String storedPassword = null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    storedPassword = rs.getString("pass");
                }
            }
            if (storedPassword != null && storedPassword.equals(password)) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException s) {
            System.out.println("Connection to database failed: " + s.getMessage());
            return false;
        }
    }

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username and password cannot be empty.");
        } else if (checkCredentials(username, password)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
                Parent dashboardRoot = loader.load();
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(new Scene(dashboardRoot, 1600, 900));
                stage.setTitle("Dashboard");
            } catch (Exception e) {
                errorLabel.setText("Failed to load dashboard: " + e.getMessage());
            }
        } else {
            errorLabel.setText("Invalid username or password.");
        }
    }
}
