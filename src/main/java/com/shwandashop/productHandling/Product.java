package com.shwandashop.productHandling;

public class Product {
    private String name;
    private int pid;
    private String category;
    private String color;
    private double price;

    public Product(String name, int pid, String category, String color, double price) {
        this.name = name;
        this.pid = pid;
        this.category = category;
        this.color = color;
        this.price = price;
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
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

}
