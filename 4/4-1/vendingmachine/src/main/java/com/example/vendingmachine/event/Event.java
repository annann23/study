package com.example.vendingmachine.event;

import com.example.vendingmachine.product.ProductType;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public abstract class Event {
    private final ProductType target;
    private final ZonedDateTime startDate;
    private final ZonedDateTime endDate;

    public Event(ProductType target, ZonedDateTime startDate, ZonedDateTime endDate) {
        this.target = target;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public abstract int getPrice(int price);
    public abstract int getQuantity();
    public abstract String getDescription();

    public ProductType getTarget() { return target; }
    public boolean isActive() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }
    public ZonedDateTime getStartDate() {
        return startDate;
    }
    public ZonedDateTime getEndDate() {
        return endDate;
    }
}
