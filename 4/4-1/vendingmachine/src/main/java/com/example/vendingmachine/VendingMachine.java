package com.example.vendingmachine;

import java.util.*;

public class VendingMachine {
    private int balance;
    private final List<Button> buttons = new ArrayList<>();
    private final EventManager eventManager = new EventManager();

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

    public void registerEvent(Event event) {
        eventManager.register(event);
    }

    public List<Event> getActiveEvents() {
        return eventManager.getActiveEvents();
    }

    public int getDisplayPrice(ProductType productType) {
        return eventManager.getDisplayPrice(productType);
    }

    public int getQuantity(ProductType productType) {
        return eventManager.getQuantity(productType);
    }

    public boolean isPurchasable(Button button) {
        Rail rail = button.getRail();

        if (rail.isSoldOut()) return false;

        ProductType type = rail.peek().getType();
        return rail.getQuantity() >= eventManager.getQuantity(type);
    }

    public void insertCoin(Money money) {
        this.balance += money.getWonValue();
    }

    public List<ProductItem> pressButton(int buttonId) {
        Button button = getButton(buttonId);
        ProductItem product = button.getRail().peek();
        ProductType type = product.getType();

        if(!isPurchasable(button))
            throw new IllegalArgumentException("상품이 품절되었습니다.");

        ProductType type = button.getRail().peek().getType();
        int finalPrice = getDisplayPrice(type);

        if (balance < finalPrice)
            throw new IllegalArgumentException("금액이 부족합니다.");

        balance -= finalPrice;

        List<ProductItem> result = new ArrayList<>();

        for (int i = 0; i < getQuantity(type); i++) {
            result.add(button.getRail().dispense());
        }

        return result;
    }

    public Map<Money, Integer> refund() {
        if(balance <= 0) throw new IllegalArgumentException("반환할 금액이 없습니다.");

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
