package com.shwandashop;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerListController {

    @FXML
    private Button returnToDashboardButton;

    @FXML
    private TableView<Customer> customerTable;

    private ObservableList<Customer> customerList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set up table columns
        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(300);

        TableColumn<Customer, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(450);

        TableColumn<Customer, String> phoneCol = new TableColumn<>("Contact Number");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setPrefWidth(450);

        customerTable.getColumns().setAll(nameCol, emailCol, phoneCol);
        
        // Load customer data
        loadCustomers();
    }

    private void loadCustomers() {
        customerList.clear();
        String sql = "SELECT customer_id, name, email, phone, address FROM Customer ORDER BY name";
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Customer customer = new Customer(
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address")
                );
                customer.setCustomerId(rs.getInt("customer_id"));
                customerList.add(customer);
            }
            
            customerTable.setItems(customerList);
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error loading customers: " + e.getMessage());
        }
    }

    @FXML
    private void onReturnToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) returnToDashboardButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
