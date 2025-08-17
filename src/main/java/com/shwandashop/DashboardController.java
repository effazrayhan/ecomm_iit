package com.shwandashop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DashboardController {
    @FXML
    private Button logoutButton;

    @FXML
    protected void onCreateOrderClick(ActionEvent event) {
        try {
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
    protected void onProductListClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ProductList.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1600, 900));
            stage.setTitle("Product List");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onCustomerListClick(ActionEvent event) {
        try {
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
    protected void onAddEmployeeClick(ActionEvent event) {
        // TODO: Implement navigation to AddEmployee.fxml
    }

    @FXML
    protected void onLogoutClick(ActionEvent event) {
        // TODO: Implement logout logic
    }
}
