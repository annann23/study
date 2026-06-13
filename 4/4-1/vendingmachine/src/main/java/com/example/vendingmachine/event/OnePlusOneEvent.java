package com.example.vendingmachine.event;

import com.example.vendingmachine.product.ProductType;

import java.time.ZonedDateTime;

public class OnePlusOneEvent extends Event {

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

    @Override
    public String getDescription() {
        return getTarget().name() + " 1+1";
    }
}
