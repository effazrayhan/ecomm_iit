package com.shwandashop;

import java.util.ArrayList;
import java.util.List;

public class SaleMemento {
    private final List<CreateSaleController.SaleProduct> savedProducts;

    public SaleMemento(List<CreateSaleController.SaleProduct> productsToSave) {
        // Deep copy to avoid external modification
        savedProducts = new ArrayList<>();
        for (CreateSaleController.SaleProduct p : productsToSave) {
            CreateSaleController.SaleProduct copy = new CreateSaleController.SaleProduct(
                p.getPid(),
                p.getName(),
                p.getColor(),
                p.getCategory(),
                p.getPrice(),
                p.getQuantity()
            );
            copy.setOrderQuantity(p.getOrderQuantity());
            if (p.isSelected()) {
                copy.selectedProperty().set(true);
            }
            savedProducts.add(copy);
        }
    }

    public List<CreateSaleController.SaleProduct> getSavedProducts() {
        return savedProducts;
    }
}
