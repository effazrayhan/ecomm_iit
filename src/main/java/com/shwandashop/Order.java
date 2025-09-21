package com.shwandashop;

public class Order {
    private String id;
    private String customerName;
    private double totalAmount;
    private String itemsDescription;

    public Order(String id, String customerName, double totalAmount, String itemsDescription) {
        this.id = id;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.itemsDescription = itemsDescription;
    }

    public String getId() { return id; }
    public String getCustomerName() { return customerName; }
    public double getTotalAmount() { return totalAmount; }
    public String getItemsDescription() { return itemsDescription; }
}
