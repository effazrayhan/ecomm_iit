package com.shwandashop;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.time.LocalDate;

public class AlertManager {
    private static AlertManager instance;
    private final AlertSubject alertSubject;
    private final StockAlertObserver stockObserver;
    private final ExpiryAlertObserver expiryObserver;
    private final ScheduledExecutorService scheduler;

    private AlertManager() {
        alertSubject = new AlertSubject();
        stockObserver = new StockAlertObserver();
        expiryObserver = new ExpiryAlertObserver();
        alertSubject.registerObserver(stockObserver);
        alertSubject.registerObserver(expiryObserver);
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public static synchronized AlertManager getInstance() {
        if (instance == null) {
            instance = new AlertManager();
        }
        return instance;
    }

    public void startBackgroundService() {
        scheduler.scheduleAtFixedRate(this::checkAlerts, 0, 1, TimeUnit.HOURS);
    }

    public void stopBackgroundService() {
        scheduler.shutdown();
    }

    private void checkAlerts() {
        // Check low stock and expired products
        List<com.shwandashop.productHandling.Product> products = fetchAllProducts();

        for (com.shwandashop.productHandling.Product product : products) {
            if (stockObserver.checkStock(product.getQuantity())) {
                alertSubject.notifyObservers("Low stock for product: " + product.getName());
            }
            if (product.getExpiryDate() != null && expiryObserver.checkExpiry(product.getExpiryDate())) {
                alertSubject.notifyObservers("Product expired: " + product.getName());
            }
        }
    }

    private List<com.shwandashop.productHandling.Product> fetchAllProducts() {
        // Fetch all products from database or cache
        // Placeholder implementation, should be replaced with actual DB call
        return com.shwandashop.DatabaseManager.getAllProducts();
    }
}
