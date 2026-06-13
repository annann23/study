package com.example.vendingmachine;

import java.time.ZonedDateTime;

public class OnePlusOneEvent extends Event{

    public OnePlusOneEvent(ProductType target, ZonedDateTime startDate, ZonedDateTime endDate) {
        super(target, startDate, endDate);
    }

    @Override
    public int getPrice(int price) {
        return price;
    }

    @Override
    public int getQuantity() {
        return 2;
    }
}
