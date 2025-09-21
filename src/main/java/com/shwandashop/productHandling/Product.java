package com.shwandashop.productHandling;

import java.time.LocalDate;

public class Product {
    private String name;
    private int pid;
    private String category;
    private String color;
    private double buyingPrice;
    private double sellingPrice;
    private LocalDate expiryDate;
    private int quantity;

    public Product(String name, int pid, String category, String color, double buyingPrice, double sellingPrice, LocalDate expiryDate, int quantity) {
        this.name = name;
        this.pid = pid;
        this.category = category;
        this.color = color;
        this.buyingPrice = buyingPrice;
        this.sellingPrice = sellingPrice;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
    }

    // Constructor for new products without pid (auto-generated)
    public Product(String name, String category, String color, double buyingPrice, double sellingPrice, LocalDate expiryDate, int quantity) {
        this.name = name;
        this.pid = -1; // Will be set after database insertion
        this.category = category;
        this.color = color;
        this.buyingPrice = buyingPrice;
        this.sellingPrice = sellingPrice;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
    }

    // Legacy constructor for backward compatibility
    public Product(String name, int pid, String category, String color, double price, int quantity) {
        this.name = name;
        this.pid = pid;
        this.category = category;
        this.color = color;
        this.buyingPrice = price;
        this.sellingPrice = price;
        this.expiryDate = null;
        this.quantity = quantity;
    }

    // Legacy constructor for new products without pid
    public Product(String name, String category, String color, double price, int quantity) {
        this.name = name;
        this.pid = -1;
        this.category = category;
        this.color = color;
        this.buyingPrice = price;
        this.sellingPrice = price;
        this.expiryDate = null;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getPid() {
        return pid;
    }
    public void setPid(int pid) {
        this.pid = pid;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public double getBuyingPrice() {
        return buyingPrice;
    }
    public void setBuyingPrice(double buyingPrice) {
        this.buyingPrice = buyingPrice;
    }
    public double getSellingPrice() {
        return sellingPrice;
    }
    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
    public LocalDate getExpiryDate() {
        return expiryDate;
    }
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    // Legacy getter for backward compatibility
    public double getPrice() {
        return sellingPrice;
    }

    // Legacy setter for backward compatibility
    public void setPrice(double price) {
        this.sellingPrice = price;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
