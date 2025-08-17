package com.shwandashop;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CreateSaleController {
    @FXML
    private TableView<SaleProduct> productTableView;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Button createOrderButton;

    private ObservableList<SaleProduct> productList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        TableColumn<SaleProduct, Boolean> selectCol = new TableColumn<>("Select");
        selectCol.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectCol.setCellFactory(tc -> {
            CheckBoxTableCell<SaleProduct, Boolean> cell = new CheckBoxTableCell<>();
            cell.setSelectedStateCallback(index -> productList.get(index).selectedProperty());
            return cell;
        });
        selectCol.setPrefWidth(50);

        TableColumn<SaleProduct, Integer> idCol = new TableColumn<>("Product ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("pid"));
        idCol.setPrefWidth(88);

        TableColumn<SaleProduct, String> nameCol = new TableColumn<>("Product Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<SaleProduct, String> colorCol = new TableColumn<>("Color");
        colorCol.setCellValueFactory(new PropertyValueFactory<>("color"));
        colorCol.setPrefWidth(100);

        TableColumn<SaleProduct, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(100);

        TableColumn<SaleProduct, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(88);

        TableColumn<SaleProduct, Integer> quantityCol = new TableColumn<>("Stock");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setPrefWidth(88);

        TableColumn<SaleProduct, Integer> orderQtyCol = new TableColumn<>("Order Qty");
        orderQtyCol.setCellValueFactory(cellData -> cellData.getValue().orderQuantityProperty().asObject());
        orderQtyCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        orderQtyCol.setOnEditCommit(event -> {
            SaleProduct product = event.getRowValue();
            int newQty = event.getNewValue() != null ? event.getNewValue() : 0;
            if (newQty >= 0 && newQty <= product.getQuantity()) {
                product.setOrderQuantity(newQty);
            } else {
                product.setOrderQuantity(0);
            }
            updateTotalPrice();
        });
        orderQtyCol.setPrefWidth(88);

        productTableView.getColumns().setAll(selectCol, idCol, nameCol, colorCol, categoryCol, priceCol, quantityCol, orderQtyCol);
        productTableView.setEditable(true);
        loadProducts();
        productTableView.setItems(productList);
        productList.forEach(p -> p.selectedProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice()));
        updateTotalPrice();
    }

    private void loadProducts() {
        productList.clear();
        String sql = "SELECT p.pid, p.name, p.color, p.category, p.price, s.quantity FROM Product p LEFT JOIN Stock s ON p.pid = s.pid";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                productList.add(new SaleProduct(
                        rs.getInt("pid"),
                        rs.getString("name"),
                        rs.getString("color"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTotalPrice() {
        double total = productList.stream()
                .filter(SaleProduct::isSelected)
                .mapToDouble(p -> p.getPrice() * p.getOrderQuantity())
                .sum();
        totalPriceLabel.setText(String.format("Total Price: $%.2f", total));
    }

    @FXML
    private void onCreateOrder(ActionEvent event) {
        double totalOrderPrice = productList.stream()
                .filter(p -> p.isSelected() && p.getOrderQuantity() > 0)
                .mapToDouble(p -> p.getPrice() * p.getOrderQuantity())
                .sum();
        if (totalOrderPrice == 0) {
            showAlert("No products selected or order quantity is zero.");
            return;
        }
        
        // Create customer popup to collect customer information
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerInfoPopup.fxml"));
            Parent root = loader.load();
            CustomerInfoPopupController controller = loader.getController();
            
            Stage stage = new Stage();
            stage.setTitle("Customer Information");
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();
            
            if (controller.isSaved()) {
                com.shwandashop.Customer customer = controller.getCustomer();
                
                // Save customer to database if new
                int customerId = saveCustomerToDatabase(customer);
                if (customerId > 0) {
                    String productsSummary = productList.stream()
                            .filter(p -> p.isSelected() && p.getOrderQuantity() > 0)
                            .map(p -> String.format("%s(x%d)", p.getName(), p.getOrderQuantity()))
                            .reduce((a, b) -> a + ", " + b).orElse("");
                    String datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    
                    try (Connection conn = DBConnect.getConnection()) {
                        // Insert order into Order table
                        String insertOrder = "INSERT INTO `Orders` (customer_id, products, total_price, datetime) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement pstmt = conn.prepareStatement(insertOrder)) {
                            pstmt.setInt(1, customerId);
                            pstmt.setString(2, productsSummary);
                            pstmt.setDouble(3, totalOrderPrice);
                            pstmt.setString(4, datetime);
                            pstmt.executeUpdate();
                        }
                        
                        // Subtract stock for each product
                        for (SaleProduct p : productList) {
                            if (p.isSelected() && p.getOrderQuantity() > 0) {
                                String updateStock = "UPDATE Stock SET quantity = quantity - ? WHERE pid = ? AND quantity >= ?";
                                try (PreparedStatement pstmt = conn.prepareStatement(updateStock)) {
                                    pstmt.setInt(1, p.getOrderQuantity());
                                    pstmt.setInt(2, p.getPid());
                                    pstmt.setInt(3, p.getOrderQuantity());
                                    int affected = pstmt.executeUpdate();
                                    if (affected == 0) {
                                        showAlert("Not enough stock for product: " + p.getName());
                                    }
                                }
                            }
                        }
                        
                        showAlert("Order placed successfully!");
                        loadProducts();
                        updateTotalPrice();
                    } catch (SQLException e) {
                        showAlert("Database error: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            showAlert("Error opening customer popup: " + e.getMessage());
        }
    }
    
    private int saveCustomerToDatabase(com.shwandashop.Customer customer) {
        String sql = "INSERT INTO Customer (name, email, phone, address) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhone());
            pstmt.setString(4, customer.getAddress());
            pstmt.executeUpdate();
            
            try (java.sql.ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            showAlert("Error saving customer: " + e.getMessage());
            return -1;
        }
        return -1;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onReturnToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) productTableView.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class SaleProduct {
        private final SimpleIntegerProperty pid;
        private final SimpleStringProperty name;
        private final SimpleStringProperty color;
        private final SimpleStringProperty category;
        private final SimpleDoubleProperty price;
        private final SimpleIntegerProperty quantity;
        private final BooleanProperty selected;
        private final SimpleIntegerProperty orderQuantity = new SimpleIntegerProperty(0);

        public SaleProduct(int pid, String name, String color, String category, double price, int quantity) {
            this.pid = new SimpleIntegerProperty(pid);
            this.name = new SimpleStringProperty(name);
            this.color = new SimpleStringProperty(color);
            this.category = new SimpleStringProperty(category);
            this.price = new SimpleDoubleProperty(price);
            this.quantity = new SimpleIntegerProperty(quantity);
            this.selected = new SimpleBooleanProperty(false);
        }
        public int getPid() { return pid.get(); }
        public String getName() { return name.get(); }
        public String getColor() { return color.get(); }
        public String getCategory() { return category.get(); }
        public double getPrice() { return price.get(); }
        public int getQuantity() { return quantity.get(); }
        public boolean isSelected() { return selected.get(); }
        public BooleanProperty selectedProperty() { return selected; }
        public int getOrderQuantity() { return orderQuantity.get(); }
        public void setOrderQuantity(int value) { orderQuantity.set(value); }
        public SimpleIntegerProperty orderQuantityProperty() { return orderQuantity; }
    }
}
