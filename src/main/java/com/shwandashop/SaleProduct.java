package com.shwandashop;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Represents a product in a sale with order quantity and selection state
 */
public class SaleProduct {
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

    // Property getters for JavaFX
    public SimpleIntegerProperty pidProperty() { return pid; }
    public SimpleStringProperty nameProperty() { return name; }
    public SimpleStringProperty colorProperty() { return color; }
    public SimpleStringProperty categoryProperty() { return category; }
    public SimpleDoubleProperty priceProperty() { return price; }
    public SimpleIntegerProperty quantityProperty() { return quantity; }
}
