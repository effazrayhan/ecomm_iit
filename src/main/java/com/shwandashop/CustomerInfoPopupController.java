package com.shwandashop;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CustomerInfoPopupController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextArea addressArea;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    
    private com.shwandashop.Customer customer;
    private boolean saved = false;
    
    public void initialize() {
        this.customer = new com.shwandashop.Customer("", "", "", "");
    }
    
    public void setCustomer(com.shwandashop.Customer customer) {
        this.customer = customer;
        nameField.setText(customer.getName());
        emailField.setText(customer.getEmail());
        phoneField.setText(customer.getPhone());
        addressArea.setText(customer.getAddress());
    }
    
    public com.shwandashop.Customer getCustomer() {
        return customer;
    }
    
    public boolean isSaved() {
        return saved;
    }
    
    @FXML
    private void onSave() {
        if (validateInputs()) {
            customer.setName(nameField.getText());
            customer.setEmail(emailField.getText());
            customer.setPhone(phoneField.getText());
            customer.setAddress(addressArea.getText());
            saved = true;
            closeWindow();
        }
    }
    
    @FXML
    private void onCancel() {
        saved = false;
        closeWindow();
    }
    
    private boolean validateInputs() {
        if (nameField.getText().trim().isEmpty()) {
            showAlert("Name is required");
            return false;
        }
        if (emailField.getText().trim().isEmpty()) {
            showAlert("Email is required");
            return false;
        }
        if (phoneField.getText().trim().isEmpty()) {
            showAlert("Phone is required");
            return false;
        }
        return true;
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
