package com.example.vendingmachine.event;

import com.example.vendingmachine.product.ProductType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EventManager {
    private final List<Event> events = new ArrayList<>();

    public void register(Event event) {
        events.add(event);
    }

    private Optional<Event> findActive(ProductType productType) {
        return events.stream()
                .filter(e -> e.getTarget() == productType && e.isActive())
                .findFirst();
    }

    public int getDisplayPrice(ProductType productType) {
        return findActive(productType)
                .map(e -> e.getPrice(productType.price()))
                .orElse(productType.price());
    }

    public int getQuantity(ProductType productType) {
        return findActive(productType)
                .map(Event::getQuantity)
                .orElse(1);
    }

    public List<Event> getActiveEvents() {
        return events.stream()
                .filter(Event::isActive)
                .collect(Collectors.toList());
    }
}
