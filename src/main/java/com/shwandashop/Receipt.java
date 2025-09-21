package com.shwandashop;

public class Receipt {
    private String orderId;
    private String customerName;
    private String date;
    private double totalAmount;
    private String items;

    public Receipt(String orderId, String customerName, String date, double totalAmount, String items) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.date = date;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    public String getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getDate() { return date; }
    public double getTotalAmount() { return totalAmount; }
    public String getItems() { return items; }
}
