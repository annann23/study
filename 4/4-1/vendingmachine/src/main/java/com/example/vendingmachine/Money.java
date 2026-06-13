package com.example.vendingmachine;

public enum Money {
    WON_10(10, Currency.WON),
    WON_50(50, Currency.WON),
    WON_100(100, Currency.WON),
    WON_500(500, Currency.WON),
    WON_1000(1000, Currency.WON),
    WON_5000(5000, Currency.WON),
    WON_10000(10000, Currency.WON),
    WON_50000(50000, Currency.WON),

    YEN_10(10, Currency.YEN),
    YEN_50(50, Currency.YEN),
    YEN_100(100, Currency.YEN),
    YEN_500(500, Currency.YEN),
    YEN_1000(1000, Currency.YEN),
    YEN_5000(5000, Currency.YEN),
    YEN_10000(10000, Currency.YEN),

    DOLLAR_1(1, Currency.DOLLAR),
    DOLLAR_5(5, Currency.DOLLAR),
    DOLLAR_10(10, Currency.DOLLAR),
    DOLLAR_20(20, Currency.DOLLAR),
    DOLLAR_50(50, Currency.DOLLAR),
    DOLLAR_100(100, Currency.DOLLAR);

    private final int value;
    private final Currency currency;

    Money(int value, Currency currency) {
        this.value = value;
        this.currency = currency;
    }

    public org.joda.money.Money getAmount() {
        return currency.of(value);
    }

    public int getWonValue() {
        return currency.toWon(value).getAmountMajorInt();
    }

    public String displayName() { return currency.format(value); }
}
