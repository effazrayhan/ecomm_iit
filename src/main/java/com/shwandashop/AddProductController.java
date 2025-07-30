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
    private Button addProductButton;
    @FXML
    private Button returnButton;

    @FXML
    protected void initialize() {
        addProductButton.setOnAction(this::handleAddProduct);
        returnButton.setOnAction(this::onReturnClick);
    }

    private void handleAddProduct(ActionEvent event) {
        Product p = new Product(productName.getText(), Integer.parseInt(productId.getText()), "", "", Double.parseDouble(productPrice.getText()));
        String sql = "INSERT INTO Product (id, name, price, category, color) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, p.getPid());
            pstmt.setString(2, p.getName());
            pstmt.setInt(3, 0);
            pstmt.setDouble(4, p.getPrice());
            pstmt.executeUpdate();
            System.out.println("Product added successfully!");
            showAlert("Product added successfully!");
            // Optionally, redirect to product list
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ProductList.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) addProductButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1600, 900));
            stage.setTitle("Product List");
        } catch (SQLException e) {
            showAlert("Database error: " + e.getMessage());
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
