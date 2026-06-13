package com.example.vendingmachine;

import java.util.*;

public class VendingMachine {
    private int balance;
    private final List<Button> buttons = new ArrayList<>();
    private final List<Event> events = new ArrayList<>();  // 추가

    public VendingMachine() {
        balance = 0;
    }

    public int getBalance() {
        return balance;
    }

    public void register(Button button, List<ProductItem> products) {
        Rail rail = new Rail();
        products.forEach(rail::load);
        button.connectRail(rail);
        buttons.add(button);
    }

    private Button getButton(int buttonId) {
        return buttons.stream()
                .filter(button -> button.getId() == buttonId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("버튼이 없습니다: " + buttonId));
    }

    public boolean isPurchasable(Button button) {
        Rail rail = button.getRail();

        if (rail.isSoldOut()) return false;

        ProductType type = rail.peek().getType();
        int requiredQuantity = findEvent(type)
                .filter(e -> e instanceof OnePlusOneEvent)
                .map(Event::getQuantity)
                .orElse(1);

        return rail.getQuantity() >= requiredQuantity;
    }

    public void registerEvent(Event event) {
        events.add(event);
    }

    private Optional<Event> findEvent(ProductType productType) {
        return events.stream()
                .filter(e -> e.getTarget() == productType && e.isActive())
                .findFirst();
    }

    public int getDisplayPrice(ProductType productType) {
        return findEvent(productType)
                .map(e -> e.getPrice(productType.price()))
                .orElse(productType.price());
    }

    public void insertCoin(Money money) {
        this.balance += money.getWonValue();
    }

    public ProductItem pressButton(int buttonId) {
        Button button = getButton(buttonId);
        ProductItem product = button.getRail().peek();

        if (balance < product.getPrice())
            throw new IllegalArgumentException("금액이 부족합니다.");

        balance -= product.getPrice();
        return button.getRail().dispense();
    }

    public Map<Money, Integer> refund() {
        if(balance <= 0) throw new IllegalArgumentException("반환할 금액이 없습니다.");;

        Map<Money, Integer> refundAmount = new LinkedHashMap<>();

        Money[] monies = {Money.WON_50000, Money.WON_10000, Money.WON_5000,
                Money.WON_1000, Money.WON_500, Money.WON_100};

        for(Money money : monies) {
            int count = balance/ money.getWonValue();
            balance %= money.getWonValue();
            if(count > 0) refundAmount.put(money, count);
        }

        return refundAmount;
    }
}
