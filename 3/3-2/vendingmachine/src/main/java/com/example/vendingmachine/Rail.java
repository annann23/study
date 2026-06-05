package com.example.vendingmachine;

import java.util.*;

class Rail {
    private final Queue<ProductItem> products;

    public Rail() {
        this.products = new ArrayDeque<>();
    }

    public void load(ProductItem product) {
        products.add(product);
    }

    public ProductItem dispense() {
        if (isSoldOut()) throw new IllegalStateException("품절입니다." );
        return products.poll();
    }

    public boolean isSoldOut() { return products.isEmpty(); }

    public ProductItem peek() {
        return products.peek();
    }
}