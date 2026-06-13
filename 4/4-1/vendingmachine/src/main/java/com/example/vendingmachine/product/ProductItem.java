package com.example.vendingmachine.product;

import java.time.LocalDate;

public class ProductItem {
    private final ProductType type;
    private final LocalDate expiredDate;

    public ProductItem(ProductType type, LocalDate expiredDate) {
        this.type = type;
        this.expiredDate = expiredDate;
    }

    public ProductType getType() {
        return type;
    }

    public LocalDate getExpiredDate() {
        return expiredDate;
    }

    public int getPrice() {
       return type.price();
    }

    public String getName() {
        return type.name();
    }
}
