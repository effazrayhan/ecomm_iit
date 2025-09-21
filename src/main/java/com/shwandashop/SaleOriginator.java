package com.shwandashop;

import java.util.List;

public class SaleOriginator {
    private List<CreateSaleController.SaleProduct> currentState;

    public void setState(List<CreateSaleController.SaleProduct> state) {
        this.currentState = state;
    }

    public SaleMemento saveToMemento() {
        return new SaleMemento(currentState);
    }

    public void restoreFromMemento(SaleMemento memento) {
        this.currentState = memento.getSavedProducts();
    }

    public List<CreateSaleController.SaleProduct> getState() {
        return currentState;
    }
}
