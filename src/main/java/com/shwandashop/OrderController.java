package com.shwandashop;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderController {
    // ...existing fields and methods...

    public void completeSale(Order order) {
        // ...existing sale completion logic...

        // Generate receipt after sale
        String orderId = order.getId();
        String customerName = order.getCustomerName();
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        double totalAmount = order.getTotalAmount();
        String items = order.getItemsDescription(); // Implement getItemsDescription() in Order if needed
        Receipt receipt = new Receipt(orderId, customerName, date, totalAmount, items);
        String receiptText = ReceiptGenerator.generateReceipt(receipt);
        System.out.println(receiptText); // Or display/save as needed
    }

    // ...existing fields and methods...
}