package com.shwandashop;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

public class CreateSaleController {
    @FXML
    private TableView<SaleProduct> productTableView;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Button createOrderButton;

    private ObservableList<SaleProduct> productList = FXCollections.observableArrayList();

    private SaleOriginator originator = new SaleOriginator();
    private SaleCaretaker caretaker = new SaleCaretaker();

    @FXML
    public void initialize() {
        TableColumn<SaleProduct, Boolean> selectCol = new TableColumn<>("Select");
        selectCol.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectCol.setCellFactory(tc -> new CheckBoxTableCell<SaleProduct, Boolean>() {
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    setSelectedStateCallback(index -> productList.get(index).selectedProperty());
                }
            }
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

        // Restore draft if exists
        if (caretaker.hasMementos()) {
            SaleMemento memento = caretaker.getMemento(0);
            originator.restoreFromMemento(memento);
            productList.setAll(originator.getState());
            productTableView.setItems(productList);
            updateTotalPrice();
        }
    }

    private void loadProducts() {
        productList.clear();
        String sql = "SELECT p.pid, p.name, p.color, p.category, p.price, COALESCE(s.quantity, 0) AS quantity FROM Product p LEFT JOIN Stock s ON p.pid = s.pid";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            boolean found = false;
            while (rs.next()) {
                found = true;
                productList.add(new SaleProduct(
                        rs.getInt("pid"),
                        rs.getString("name"),
                        rs.getString("color"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                ));
            }
            if (!found) {
                System.err.println("No products found in database. Check Product and Stock tables.");
            }
        } catch (SQLException e) {
            System.err.println("Error loading products: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Loaded products: " + productList.size());
    }

    private void updateTotalPrice() {
        double total = productList.stream()
                .filter(SaleProduct::isSelected)
                .mapToDouble(p -> p.getPrice() * p.getOrderQuantity())
                .sum();
        totalPriceLabel.setText(String.format("Total Price: $%.2f", total));
    }

    private boolean hasUnsavedChanges() {
        for (SaleProduct p : productList) {
            if (p.isSelected() && p.getOrderQuantity() > 0) {
                return true;
            }
        }
        return false;
    }

    private void saveDraft() {
        originator.setState(productList);
        SaleMemento memento = originator.saveToMemento();
        caretaker.clearMementos();
        caretaker.addMemento(memento);
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

        createOrderButton.setDisable(true);

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

                int customerId = saveCustomerToDatabase(customer);
                if (customerId > 0) {
                    String productsSummary = productList.stream()
                            .filter(p -> p.isSelected() && p.getOrderQuantity() > 0)
                            .map(p -> String.format("%s(x%d)", p.getName(), p.getOrderQuantity()))
                            .reduce((a, b) -> a + ", " + b).orElse("");
                    String datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                    try (Connection conn = DBConnect.getConnection()) {
                        try {
                            conn.setAutoCommit(false);

                            String insertOrder = "INSERT INTO `Orders` (customer_id, products, total_price, datetime) VALUES (?, ?, ?, ?)";
                            try (PreparedStatement pstmt = conn.prepareStatement(insertOrder)) {
                                pstmt.setInt(1, customerId);
                                pstmt.setString(2, productsSummary);
                                pstmt.setDouble(3, totalOrderPrice);
                                pstmt.setString(4, datetime);
                                pstmt.executeUpdate();
                            }

                            for (SaleProduct p : productList) {
                                if (p.isSelected() && p.getOrderQuantity() > 0) {
                                    String updateStock = "UPDATE Stock SET quantity = quantity - ? WHERE pid = ? AND quantity >= ?";
                                    try (PreparedStatement pstmt = conn.prepareStatement(updateStock)) {
                                        pstmt.setInt(1, p.getOrderQuantity());
                                        pstmt.setInt(2, p.getPid());
                                        pstmt.setInt(3, p.getOrderQuantity());
                                        int affected = pstmt.executeUpdate();
                                        if (affected == 0) {
                                            throw new SQLException("Not enough stock for product: " + p.getName());
                                        }
                                    }
                                }
                            }

                            conn.commit();

                            showAlert("Order placed successfully!");
                            loadProducts();
                            updateTotalPrice();

                            productList.forEach(p -> {
                                p.selectedProperty().set(false);
                                p.setOrderQuantity(0);
                            });
                        } catch (SQLException e) {
                            conn.rollback();
                            showAlert("Order failed: " + e.getMessage());
                        } finally {
                            conn.setAutoCommit(true);
                        }
                    } catch (SQLException e) {
                        showAlert("Database error: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            showAlert("Error opening customer popup: " + e.getMessage());
        } finally {
            createOrderButton.setDisable(false);
        }
    }

    private int saveCustomerToDatabase(com.shwandashop.Customer customer) {
        String sql = "INSERT INTO Customer (name, email, phone, address) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhone());
            pstmt.setString(4, customer.getAddress());
            pstmt.executeUpdate();

            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int customerId = generatedKeys.getInt(1);
                DatabaseManager.commitTransaction(conn);
                return customerId;
            }
        } catch (SQLException e) {
            if (conn != null) {
                DatabaseManager.rollbackTransaction(conn);
            }
            showAlert("Error saving customer: " + e.getMessage());
            return -1;
        } finally {
            // Clean up resources
            if (generatedKeys != null) {
                try {
                    generatedKeys.close();
                } catch (SQLException e) {
                    System.err.println("Error closing ResultSet: " + e.getMessage());
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    System.err.println("Error closing PreparedStatement: " + e.getMessage());
                }
            }
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

            Stage stage = (Stage) productTableView.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(dashboardTitle);
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
