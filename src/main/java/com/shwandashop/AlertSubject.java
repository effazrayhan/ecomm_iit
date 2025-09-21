package com.shwandashop;

import java.util.ArrayList;
import java.util.List;

public class AlertSubject {
    private final List<AlertObserver> observers = new ArrayList<>();

    public void registerObserver(AlertObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(AlertObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String alertMessage) {
        for (AlertObserver observer : observers) {
            observer.update(alertMessage);
        }
    }
}
