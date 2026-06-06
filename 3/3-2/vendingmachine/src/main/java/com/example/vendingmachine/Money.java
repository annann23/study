package com.example.vendingmachine;

public enum Money {
    // 한화
    WON_100(100, "100원"),
    WON_500(500, "500원"),
    WON_1000(1000, "1000원"),
    WON_5000(5000, "5000원"),
    WON_10000(10000, "10000원"),
    WON_50000(50000, "50000원"),

    // 엔화 (1엔 = 10원)
    YEN_10(100, "10¥"),
    YEN_50(500, "50¥"),
    YEN_100(1000, "100¥"),
    YEN_500(5000, "500¥"),
    YEN_1000(10000, "1000¥"),
    YEN_5000(50000, "5000¥"),
    YEN_10000(100000, "10000¥"),

    // 달러 (1달러 = 1000원)
    DOLLAR_1(1000, "$1"),
    DOLLAR_5(5000, "$5"),
    DOLLAR_10(10000, "$10"),
    DOLLAR_20(20000, "$20"),
    DOLLAR_50(50000, "$50"),
    DOLLAR_100(100000, "$100");

    private final int wonValue;
    private final String displayName;

    Money(int wonValue, String displayName) {
        this.wonValue = wonValue;
        this.displayName = displayName;
    }

    public int value() {
        return wonValue;
    }

    public String displayName() {
        return displayName;
    }
}
