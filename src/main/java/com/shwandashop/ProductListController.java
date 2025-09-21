package com.shwandashop;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.shwandashop.productHandling.Product;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ProductListController implements Initializable {
    @FXML
    private TableView<Product> productTable;
    @FXML
    private Button returnButton;

    private ObservableList<Product> productList = FXCollections.observableArrayList();

    @FXML
    private TableColumn<Product, Integer> idCol;
    
    @FXML
    private TableColumn<Product, String> nameCol;
    
    @FXML
    private TableColumn<Product, Double> priceCol;
    
    @FXML
    private TableColumn<Product, Integer> quantityCol;
    
    @FXML
    private TableColumn<Product, String> categoryCol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("pid"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        loadProductsFromDatabase();
    }

    @FXML
    private void onReturnClick(ActionEvent event) {
        try {
            SessionManager sessionManager = SessionManager.getInstance();
            String userRole = sessionManager.getCurrentUserRole();

            FXMLLoader loader;
            String dashboardTitle;

            if ("admin".equals(userRole)) {
                loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
                dashboardTitle = "Admin Dashboard";
            } else {
                loader = new FXMLLoader(getClass().getResource("EmployeeDashboard.fxml"));
                dashboardTitle = "Employee Dashboard";
            }

            Parent root = loader.load();
            Stage stage = (Stage) returnButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1600, 900));
            stage.setTitle(dashboardTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadProductsFromDatabase() {
        productList.clear();
        String sql = "SELECT p.pid, p.name, p.category, p.color, p.price, IFNULL(s.quantity, 0) as quantity FROM Product p LEFT JOIN Stock s ON p.pid = s.pid";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                productList.add(new Product(
                        rs.getString("name"),
                        rs.getInt("pid"),
                        rs.getString("category"),
                        rs.getString("color"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                ));
            }
            productTable.setItems(productList);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Clean up resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
