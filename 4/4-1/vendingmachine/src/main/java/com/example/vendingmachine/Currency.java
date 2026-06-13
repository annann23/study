package com.example.vendingmachine;

import org.joda.money.CurrencyUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;

public enum Currency {
    WON(CurrencyUnit.of("KRW"), new BigDecimal("1"), "원") {
        @Override public String format(int value) { return value + "원"; }
    },
    YEN(CurrencyUnit.of("JPY"), new BigDecimal("10"), "엔") {
        @Override public String format(int value) { return value + "¥"; }
    },
    DOLLAR(CurrencyUnit.of("USD"), new BigDecimal("1000"), "달러") {
        @Override public String format(int value) { return "$" + value; }
    };

    private final CurrencyUnit currencyUnit;
    private final BigDecimal rateToWon;
    private final String label;

    Currency(CurrencyUnit currencyUnit, BigDecimal rateToWon, String label) {
        this.currencyUnit = currencyUnit;
        this.rateToWon = rateToWon;
        this.label = label;
    }

    public abstract String format(int value);

    public org.joda.money.Money of(int value) {
        return org.joda.money.Money.of(currencyUnit, BigDecimal.valueOf(value));
    }

    public org.joda.money.Money toWon(int value) {
        BigDecimal krwAmount = BigDecimal.valueOf(value).multiply(rateToWon);
        return org.joda.money.Money.of(CurrencyUnit.of("KRW"), krwAmount, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() { return label; }
}
