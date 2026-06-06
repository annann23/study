package com.example.vendingmachine;

public enum Currency {
    WON(1, "원") {
        @Override public String format(int value) { return value + "원"; }
    },
    YEN(10, "엔") {
        @Override public String format(int value) { return value + "¥"; }
    },
    DOLLAR(1000, "달러") {
        @Override public String format(int value) { return "$" + value; }
    };

    private final int rate;
    private final String label;

    Currency(int rate, String label) {
        this.rate = rate;
        this.label = label;
    }

    public abstract String format(int value);

    public int toWon(int value) { return value * rate; }

    @Override
    public String toString() { return label; }
}
