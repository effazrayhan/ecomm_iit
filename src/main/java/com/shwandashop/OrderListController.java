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

public class OrderListController {

    @FXML
    private TableView<Order> orderTableView;

    @FXML
    private TableColumn<Order, String> orderIdCol;

    @FXML
    private TableColumn<Order, String> customerNameCol;

    @FXML
    private TableColumn<Order, String> orderDateCol;

    @FXML
    private TableColumn<Order, Double> totalAmountCol;

    @FXML
    private TableColumn<Order, String> itemsCol;

    @FXML
    private Button returnToDashboardButton;

    private ObservableList<Order> orderList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        orderDateCol.setCellValueFactory(new PropertyValueFactory<>("datetime"));
        totalAmountCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        itemsCol.setCellValueFactory(new PropertyValueFactory<>("products"));

        loadOrders();
        orderTableView.setItems(orderList);
    }

    private void loadOrders() {
        orderList.clear();
        String sql = "SELECT order_id, customer_name, datetime, total_price, products FROM Orders ORDER BY datetime DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Order order = new Order(
                        rs.getString("order_id"),
                        rs.getString("customer_name"),
                        rs.getDouble("total_price"),
                        rs.getString("products")
                );
                orderList.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onReturnToDashboard() {
        try {
            FXMLLoader loader;
            String dashboardTitle;

            SessionManager sessionManager = SessionManager.getInstance();
            String userRole = sessionManager.getCurrentUserRole();

            if ("admin".equals(userRole)) {
                loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
                dashboardTitle = "Admin Dashboard";
            } else {
                loader = new FXMLLoader(getClass().getResource("EmployeeDashboard.fxml"));
                dashboardTitle = "Employee Dashboard";
            }

            Parent root = loader.load();

            Stage stage = (Stage) returnToDashboardButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(dashboardTitle);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
