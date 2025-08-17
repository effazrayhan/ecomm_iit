package com.shwandashop;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        nameCol.setPrefWidth(250);

        TableColumn<Customer, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(350);

        TableColumn<Customer, String> phoneCol = new TableColumn<>("Mobile Number");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setPrefWidth(200);

        TableColumn<Customer, Integer> ordersCol = new TableColumn<>("Total Orders");
        ordersCol.setCellValueFactory(new PropertyValueFactory<>("orderCount"));
        ordersCol.setPrefWidth(150);

        customerTable.getColumns().setAll(nameCol, emailCol, phoneCol, ordersCol);
        
        // Load customer data
        loadCustomers();
    }

    private void loadCustomers() {
    customerList.clear();
    String sql = "SELECT c.id, c.name, c.email, c.phone, c.address, " +
                 "       COALESCE(COUNT(o.order_id), 0) AS order_count " +
                 "FROM Customer c " +
                 "LEFT JOIN Orders o ON c.id = o.customer_id " +   // ✅ Fix join condition
                 "GROUP BY c.id, c.name, c.email, c.phone, c.address " +
                 "ORDER BY c.name";
    
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
            customer.setCustomerId(rs.getInt("id"));
            customer.setOrderCount(rs.getInt("order_count"));
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
