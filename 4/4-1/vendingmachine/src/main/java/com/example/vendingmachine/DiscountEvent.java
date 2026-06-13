package com.example.vendingmachine;

import java.time.ZonedDateTime;

public class DiscountEvent extends Event{

    private final int discountRate;

    public DiscountEvent(ProductType target, ZonedDateTime startDate, ZonedDateTime endDate, int discountRate) {
        super(target, startDate, endDate);
        this.discountRate = discountRate;
    }

    @Override
    public int getPrice(int price) {
        return (int) Math.floor((double) (price * (100 - discountRate)) / 100);
    }

    @Override
    public int getQuantity() {
        return 1;
    }

    @Override
    public String getDescription() {
        return getTarget().name() + " " + discountRate + "% 할인";
    }
}
