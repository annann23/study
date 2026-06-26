package com.example.vendingmachine;

import com.example.vendingmachine.product.ProductItem;

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

    public int getQuantity() {
        return products.size();
    }

    public boolean isSoldOut() { return products.isEmpty(); }

    public ProductItem peek() {
        return products.peek();
    }
}