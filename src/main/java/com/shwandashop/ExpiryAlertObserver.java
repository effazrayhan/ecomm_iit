package com.shwandashop;

import java.time.LocalDate;

public class ExpiryAlertObserver implements AlertObserver {

    @Override
    public void update(String alertMessage) {
        // Implementation to handle expiry alert, e.g., log or notify UI
        System.out.println("Expiry Alert: " + alertMessage);
    }

    public boolean checkExpiry(LocalDate expiryDate) {
        return expiryDate.isBefore(LocalDate.now()) || expiryDate.isEqual(LocalDate.now());
    }
}
