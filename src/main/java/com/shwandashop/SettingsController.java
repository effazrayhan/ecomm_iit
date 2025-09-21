package com.shwandashop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class SettingsController {
    @FXML private TextField currentUsernameField;
    @FXML private PasswordField currentPasswordField;
    @FXML private Button updateUsernameButton;
    @FXML private Button updatePasswordButton;
    @FXML private Button addUserButton;
    @FXML private Button backButton;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        // Initialize session manager to ensure admin access
        SessionManager sessionManager = SessionManager.getInstance();
        if (sessionManager.getCurrentUserRole() == null) {
            // If no session exists, set as admin (for direct access)
            sessionManager.setCurrentUser("admin", "admin", "Administrator");
        }

        // Check if current user is admin
        if (!sessionManager.isAdmin()) {
            statusLabel.setText("Access denied: Admin privileges required.");
            updateUsernameButton.setDisable(true);
            updatePasswordButton.setDisable(true);
            addUserButton.setDisable(true);
        }
    }

    @FXML
    protected void onUpdateUsernameClick() {
        // Verify admin access before proceeding
        SessionManager sessionManager = SessionManager.getInstance();
        if (!sessionManager.isAdmin()) {
            statusLabel.setText("Access denied: Admin privileges required.");
            return;
        }

        String currentUsername = currentUsernameField.getText().trim();
        String currentPassword = currentPasswordField.getText();

        // Validation
        if (currentUsername.isEmpty() || currentPassword.isEmpty()) {
            statusLabel.setText("Please fill in both current username and password.");
            return;
        }

        // Verify current credentials
        if (!verifyCurrentCredentials(currentUsername, currentPassword)) {
            statusLabel.setText("Current username or password is incorrect.");
            return;
        }

        // Show dialog to get new username
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Update Username");
        dialog.setHeaderText("Enter New Username");
        dialog.setContentText("New Username:");

        dialog.showAndWait().ifPresent(newUsername -> {
            if (newUsername.trim().isEmpty()) {
                statusLabel.setText("Username cannot be empty.");
                return;
            }

            if (newUsername.length() < 3) {
                statusLabel.setText("Username must be at least 3 characters long.");
                return;
            }

            // Check if new username already exists
            if (isUsernameExists(newUsername)) {
                statusLabel.setText("Username already exists. Please choose a different username.");
                return;
            }

            // Update username in database
            if (updateUsernameInDatabase(currentUsername, newUsername)) {
                statusLabel.setText("Username updated successfully!");
                clearFields();
            } else {
                statusLabel.setText("Failed to update username. Please try again.");
            }
        });
    }

    @FXML
    protected void onUpdatePasswordClick() {
        // Verify admin access before proceeding
        SessionManager sessionManager = SessionManager.getInstance();
        if (!sessionManager.isAdmin()) {
            statusLabel.setText("Access denied: Admin privileges required.");
            return;
        }

        String currentUsername = currentUsernameField.getText().trim();
        String currentPassword = currentPasswordField.getText();

        // Validation
        if (currentUsername.isEmpty() || currentPassword.isEmpty()) {
            statusLabel.setText("Please fill in both current username and password.");
            return;
        }

        // Verify current credentials
        if (!verifyCurrentCredentials(currentUsername, currentPassword)) {
            statusLabel.setText("Current username or password is incorrect.");
            return;
        }

        // Show dialog to get new password
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Update Password");
        dialog.setHeaderText("Enter New Password");
        dialog.setContentText("New Password (min 6 characters):");

        dialog.showAndWait().ifPresent(newPassword -> {
            if (newPassword.trim().isEmpty()) {
                statusLabel.setText("Password cannot be empty.");
                return;
            }

            if (newPassword.length() < 6) {
                statusLabel.setText("Password must be at least 6 characters long.");
                return;
            }

            // Update password in database
            if (updatePasswordInDatabase(currentUsername, newPassword)) {
                statusLabel.setText("Password updated successfully!");
                clearFields();
            } else {
                statusLabel.setText("Failed to update password. Please try again.");
            }
        });
    }

    @FXML
    protected void onAddUserClick() {
        // Verify admin access before proceeding
        SessionManager sessionManager = SessionManager.getInstance();
        if (!sessionManager.isAdmin()) {
            statusLabel.setText("Access denied: Admin privileges required.");
            return;
        }

        // Show choice dialog for user type
        ChoiceDialog<String> dialog = new ChoiceDialog<>("employee", "employee", "admin");
        dialog.setTitle("Add User");
        dialog.setHeaderText("Select User Type");
        dialog.setContentText("Choose user type:");

        dialog.showAndWait().ifPresent(userType -> {
            // Show dialog to get username and password
            Dialog<ButtonType> userDialog = new Dialog<>();
            userDialog.setTitle("Add " + userType.substring(0, 1).toUpperCase() + userType.substring(1));

            // Create the form
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            TextField usernameField = new TextField();
            usernameField.setPromptText("Enter unique username");
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Enter password (min 6 chars)");

            grid.add(new Label("Username:"), 0, 0);
            grid.add(usernameField, 1, 0);
            grid.add(new Label("Password:"), 0, 1);
            grid.add(passwordField, 1, 1);

            userDialog.getDialogPane().setContent(grid);
            userDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            userDialog.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) {
                    String username = usernameField.getText().trim();
                    String password = passwordField.getText();

                    // Validation
                    if (username.isEmpty() || password.isEmpty()) {
                        statusLabel.setText("Please fill in both username and password.");
                        return;
                    }

                    if (password.length() < 6) {
                        statusLabel.setText("Password must be at least 6 characters long.");
                        return;
                    }

                    // Check if username already exists
                    if (isUsernameExists(username)) {
                        statusLabel.setText("Username already exists. Please choose a different username.");
                        return;
                    }

                    // Add user to database
                    if (addUserToDatabase(username, password, userType)) {
                        statusLabel.setText(userType.substring(0, 1).toUpperCase() + userType.substring(1) + " added successfully!");
                        clearFields();
                    } else {
                        statusLabel.setText("Failed to add " + userType + ". Please try again.");
                    }
                }
            });
        });
    }

    private boolean verifyCurrentCredentials(String username, String password) {
        String sql = "SELECT COUNT(*) FROM Credentials WHERE eid = ? AND pass = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM Credentials WHERE eid = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean updateUsernameInDatabase(String oldUsername, String newUsername) {
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);

            // Update Credentials table
            String sql1 = "UPDATE Credentials SET eid = ? WHERE eid = ?";
            try (PreparedStatement pstmt1 = conn.prepareStatement(sql1)) {
                pstmt1.setString(1, newUsername);
                pstmt1.setString(2, oldUsername);
                pstmt1.executeUpdate();
            }

            // Update Employee table
            String sql2 = "UPDATE Employee SET eid = ? WHERE eid = ?";
            try (PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {
                pstmt2.setString(1, newUsername);
                pstmt2.setString(2, oldUsername);
                pstmt2.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean updatePasswordInDatabase(String username, String newPassword) {
        String sql = "UPDATE Credentials SET pass = ? WHERE eid = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean addUserToDatabase(String username, String password, String role) {
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);

            // Insert into Credentials table
            String sql1 = "INSERT INTO Credentials (eid, pass, role) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt1 = conn.prepareStatement(sql1)) {
                pstmt1.setString(1, username);
                pstmt1.setString(2, password);
                pstmt1.setString(3, role);
                pstmt1.executeUpdate();
            }

            // Insert into Employee table with default values
            String sql2 = "INSERT INTO Employee (eid, name, email, phone, address, hire_date, salary, status) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, 'active')";
            try (PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {
                pstmt2.setString(1, username);
                pstmt2.setString(2, "New " + role.substring(0, 1).toUpperCase() + role.substring(1)); // Default name
                pstmt2.setString(3, ""); // Default email (empty)
                pstmt2.setString(4, ""); // Default phone (empty)
                pstmt2.setString(5, ""); // Default address (empty)
                pstmt2.setString(6, LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                pstmt2.setDouble(7, 0.0); // Default salary
                pstmt2.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void clearFields() {
        currentUsernameField.clear();
        currentPasswordField.clear();
    }

    @FXML
    protected void onBackClick() {
        try {
            SessionManager sessionManager = SessionManager.getInstance();
            if (sessionManager.isAdmin()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
                Parent dashboardRoot = loader.load();
                Stage stage = (Stage) backButton.getScene().getWindow();
                stage.setScene(new Scene(dashboardRoot, 1600, 900));
                stage.setTitle("Admin Dashboard");
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("EmployeeDashboard.fxml"));
                Parent dashboardRoot = loader.load();
                Stage stage = (Stage) backButton.getScene().getWindow();
                stage.setScene(new Scene(dashboardRoot, 1600, 900));
                stage.setTitle("Employee Dashboard");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Failed to go back: " + e.getMessage());
        }
    }
}
