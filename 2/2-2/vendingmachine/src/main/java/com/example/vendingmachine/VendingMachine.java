package com.example.vendingmachine;
import java.util.LinkedHashMap;
import java.util.Map;

public class VendingMachine {
    private final Product[] product;
    private int balance;

    public VendingMachine(Product[] product) {
        this.product = product;
        balance = 0;
    }

    public Product[] getProduct() {
        return product;
    }

    public int getBalance() {
        return balance;
    }

    public void insertCoin(Money money) {
        this.balance += money.value();
    }

    public void purchase(Product product) {
        if(!this.isPurchasable(product)) throw new IllegalArgumentException("잔액이 부족합니다.");
        if(product.isSoldOut()) throw new IllegalArgumentException("상품이 품절되었습니다.");

        product.sell();

        balance -= product.getPrice();
    }

    public Map<Money, Integer> refund() {
        if(balance == 0) throw new IllegalArgumentException("반환할 금액이 없습니다.");;

        Map<Money, Integer> refundAmount = new LinkedHashMap<>();

        Money[] monies = {Money.WON_50000, Money.WON_10000, Money.WON_5000,
                Money.WON_1000, Money.WON_500, Money.WON_100};

        for(Money money : monies) {
            int count = balance/ money.value();
            balance %= money.value();
            if(count > 0) refundAmount.put(money, count);
        }

        return refundAmount;
    }

    public boolean isPurchasable(Product product) {
        return product.getPrice() <= balance;
    }
}
