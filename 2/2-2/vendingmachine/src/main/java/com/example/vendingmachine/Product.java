package com.example.vendingmachine;

public class Product {
    private final String name;
    private final int price;
    private int amount;

    public Product(String name, int price, int amount) {
        this.name = name;
        this.price = price;
        this.amount = amount;
    }

    public void sell() {
        if(this.amount == 0) throw new IllegalStateException("품절된 상품입니다.");
        this.amount--;
    }

    public boolean isSoldOut() {
        return this.amount == 0;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}
