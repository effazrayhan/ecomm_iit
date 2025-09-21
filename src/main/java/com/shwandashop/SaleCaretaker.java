package com.shwandashop;

import java.util.ArrayList;
import java.util.List;

public class SaleCaretaker {
    private final List<SaleMemento> mementoList = new ArrayList<>();

    public void addMemento(SaleMemento memento) {
        mementoList.add(memento);
    }

    public SaleMemento getMemento(int index) {
        if (index >= 0 && index < mementoList.size()) {
            return mementoList.get(index);
        }
        return null;
    }

    public void clearMementos() {
        mementoList.clear();
    }

    public boolean hasMementos() {
        return !mementoList.isEmpty();
    }
}
