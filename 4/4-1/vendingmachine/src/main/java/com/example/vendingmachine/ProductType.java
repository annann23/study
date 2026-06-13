package com.example.vendingmachine;

public record ProductType (
    String name,
    int price,
    int volume,
    Temperature temperature,
    Container container
) {
    public enum Temperature { HOT, COLD, AMBIENT }
    public enum Container { CAN, GLASS_BOTTLE, PET_BOTTLE }

    public static final ProductType COLA = new ProductType("콜라", 1200, 355, Temperature.COLD, Container.CAN);
    public static final ProductType ZERO_COKE = new ProductType("제로콜라", 1500, 355, Temperature.COLD, Container.CAN);
    public static final ProductType GREEN_TEA = new ProductType("녹차", 1000, 500, Temperature.COLD, Container.PET_BOTTLE);
    public static final ProductType HOT_COFFEE = new ProductType("칸타타", 1400, 400, Temperature.HOT, Container.CAN);
    public static final ProductType COLD_COFFEE = new ProductType("레쓰비", 1000, 175, Temperature.HOT, Container.CAN);
    public static final ProductType WATER = new ProductType("물", 600, 500, Temperature.COLD, Container.PET_BOTTLE);
    public static final ProductType MORNING_SUNLIGHT = new ProductType("아침햇살", 900, 480, Temperature.COLD, Container.PET_BOTTLE);
    public static final ProductType ALOE_JUICE = new ProductType("알로에주스", 1000, 480, Temperature.COLD, Container.PET_BOTTLE);
    public static final ProductType ORANGE_JUICE = new ProductType("오렌지주스", 1000, 355, Temperature.COLD, Container.GLASS_BOTTLE);

    public static final ProductType[] ALL = { COLA, ZERO_COKE, GREEN_TEA, HOT_COFFEE, COLD_COFFEE, WATER, MORNING_SUNLIGHT, ALOE_JUICE, ORANGE_JUICE };
}

