package com.shwandashop;

public class StockAlertObserver implements AlertObserver {
    private int lowStockThreshold = 10;

    @Override
    public void update(String alertMessage) {
        // Implementation to handle stock alert, e.g., log or notify UI
        System.out.println("Stock Alert: " + alertMessage);
    }

    public boolean checkStock(int quantity) {
        return quantity < lowStockThreshold;
    }
}
