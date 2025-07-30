package com.shwandashop;

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

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ProductListController implements Initializable {
    @FXML
    private TableView<Product> productTable;
    @FXML
    private Button returnButton;

    private ObservableList<Product> productList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (productTable.getColumns().isEmpty()) {
            TableColumn<Product, Integer> idCol = new TableColumn<>("Product ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            idCol.setPrefWidth(120);

            TableColumn<Product, String> nameCol = new TableColumn<>("Product Name");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            nameCol.setPrefWidth(300);

            TableColumn<Product, Double> priceCol = new TableColumn<>("Price");
            priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
            priceCol.setPrefWidth(120);

            TableColumn<Product, Integer> quantityCol = new TableColumn<>("Quantity");
            quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            quantityCol.setPrefWidth(120);

            TableColumn<Product, Boolean> onSaleCol = new TableColumn<>("On Sale");
            onSaleCol.setCellValueFactory(new PropertyValueFactory<>("onSale"));
            onSaleCol.setPrefWidth(120);

            productTable.getColumns().addAll(idCol, nameCol, priceCol, quantityCol, onSaleCol);
        }
        loadProductsFromDatabase();
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

    private void loadProductsFromDatabase() {
        productList.clear();
        String url = "jdbc:sqlite:identifier.sqlite";
        String sql = "SELECT id, name, price, quantity, onSale FROM Stock";
        System.out.println(sql);
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            System.out.println(rs.toString());
            while (rs.next()) {
                productList.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getBoolean("onSale")
                ));
            }

            productTable.setItems(productList);
            for(Product p : productTable.getItems()){
                System.out.println("Product ID: " + p.getId() + ", Name: " + p.getName() +
                        ", Price: " + p.getPrice() + ", Quantity: " + p.getQuantity() +
                        ", On Sale: " + p.getOnSale());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class Product {
        private final Integer id;
        private final String name;
        private final Double price;
        private final Integer quantity;
        private final Boolean onSale;

        public Product(Integer id, String name, Double price, Integer quantity, Boolean onSale) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.onSale = onSale;
        }

        public Integer getId() { return id; }
        public String getName() { return name; }
        public Double getPrice() { return price; }
        public Integer getQuantity() { return quantity; }
        public Boolean getOnSale() { return onSale; }
    }
}
