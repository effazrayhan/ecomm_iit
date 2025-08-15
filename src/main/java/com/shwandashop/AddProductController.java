package com.shwandashop;

import com.shwandashop.productHandling.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddProductController {
    @FXML
    private TextField productId;
    @FXML
    private TextField productName; // Product ID
    @FXML
    private TextField productQuantity; // Quantity
    @FXML
    private TextField productPrice; // Price
    @FXML
    private TextField productCategory; // Category
    @FXML
    private TextField productColor;
    @FXML
    private Button addProductButton;
    @FXML
    private Button returnButton;

    @FXML
    protected void initialize() {
        addProductButton.setOnAction(this::handleAddProduct);
        returnButton.setOnAction(this::onReturnClick);
    }

    private void handleAddProduct(ActionEvent event) {
        try {
            int pid = Integer.parseInt(productId.getText());
            String name = productName.getText();
            String category = productCategory.getText();
            String color = productColor.getText();
            double price = Double.parseDouble(productPrice.getText());
            int quantity = Integer.parseInt(productQuantity.getText());
            Product p = new Product(name, pid, category, color, price, quantity);
            String sql = "INSERT INTO Product (pid, name, price, category, color) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = DBConnect.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, p.getPid());
                pstmt.setString(2, p.getName());
                pstmt.setDouble(3, p.getPrice());
                pstmt.setString(4, p.getCategory());
                pstmt.setString(5, p.getColor());
                pstmt.executeUpdate();
                System.out.println("Product added successfully!");
                showAlert("Product added successfully!");
            }
        catch (SQLException e) {
            showAlert("Database error: " + e.getMessage());
        }
            sql = "INSERT INTO Stock (pid, quantity) VALUES (?, ?)";
            try (Connection conn = DBConnect.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, p.getPid());
                pstmt.setInt(2, p.getQuantity());
                pstmt.executeUpdate();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ProductList.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) addProductButton.getScene().getWindow();
                stage.setScene(new Scene(root, 1600, 900));
                stage.setTitle("Product List");
            }
        } catch (Exception e) {
            showAlert("Error: " + e.getMessage());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) returnButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1600, 900));
            stage.setTitle("Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
