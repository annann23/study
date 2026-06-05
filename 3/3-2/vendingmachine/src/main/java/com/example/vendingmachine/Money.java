package com.example.vendingmachine;

public enum Money {
    WON_100(100),
    WON_500(500),
    WON_1000(1000),
    WON_5000(5000),
    WON_10000(10000),
    WON_50000(50000);

    private final int value;

    Money(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
