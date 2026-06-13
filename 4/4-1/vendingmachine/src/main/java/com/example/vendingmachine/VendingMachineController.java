package com.example.vendingmachine;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.Map;

public class VendingMachineController {
    private VendingMachine vendingMachine;
    private final Map<String, Integer> purchasedMap = new HashMap<>();
    private final Map<Button, javafx.scene.control.Button> priceButtons = new HashMap<>();

    @FXML private GridPane productGrid;
    @FXML private ComboBox<Currency> currencyComboBox;
    @FXML private ComboBox<Money> denominationComboBox;
    @FXML private Label balanceLabel;
    @FXML private VBox productList;
    @FXML private VBox refundedList;

    private static final Map<Currency, Money[]> CURRENCY_MAP = Map.of(
            Currency.WON, new Money[]{Money.WON_100, Money.WON_500, Money.WON_1000, Money.WON_5000, Money.WON_10000, Money.WON_50000},
            Currency.YEN, new Money[]{Money.YEN_10, Money.YEN_50, Money.YEN_100, Money.YEN_500, Money.YEN_1000, Money.YEN_5000, Money.YEN_10000},
            Currency.DOLLAR, new Money[]{Money.DOLLAR_1, Money.DOLLAR_5, Money.DOLLAR_10, Money.DOLLAR_20, Money.DOLLAR_50, Money.DOLLAR_100}
    );

    @FXML
    private void initialize() {
        vendingMachine = new VendingMachine();
        initializeEvents();
        initializeProducts();
        initializeCurrencyDropdown();
    }

    private void initializeEvents() {
        ZonedDateTime start = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime end = start.plusDays(7);

        vendingMachine.registerEvent(
                new DiscountEvent(ProductType.ZERO_COKE, start, end, 10)
        );

        vendingMachine.registerEvent(
                new OnePlusOneEvent(ProductType.GREEN_TEA, start, end)
        );
    }

    private void initializeProducts() {
        for (int i = 0; i < ProductType.ALL.length; i++) {
            ProductType type = ProductType.ALL[i];

            Button domainButton = new Button(i);
            List<ProductItem> items = Collections.nCopies(10, new ProductItem(type, LocalDate.of(2028, 1, 6)));
            vendingMachine.register(domainButton, items);

            VBox productImage = new VBox();
            productImage.getStyleClass().add("product-image");
            productImage.getChildren().add(new Label(type.name()));

            javafx.scene.control.Button priceButton = new javafx.scene.control.Button(vendingMachine.getDisplayPrice(type) + "원");
            priceButton.setDisable(true);
            priceButton.getStyleClass().add("price-button");
            priceButton.setOnAction(e -> purchase(domainButton));

            priceButtons.put(domainButton, priceButton);

            VBox productBox = new VBox();
            productBox.getStyleClass().add("purchased-box");
            productBox.getChildren().addAll(productImage, priceButton);
            productGrid.add(productBox, i % 3, i / 3);
        }
    }

    private void initializeCurrencyDropdown() {
        StringConverter<Money> converter = new StringConverter<>() {
            @Override public String toString(Money money) { return money == null ? "" : money.displayName(); }
            @Override public Money fromString(String s) { return null; }
        };
        denominationComboBox.setConverter(converter);

        currencyComboBox.getItems().addAll(Currency.values());
        currencyComboBox.getSelectionModel().selectFirst();
        onCurrencySelected();
    }

    @FXML
    public void onCurrencySelected() {
        Currency currency = currencyComboBox.getValue();
        denominationComboBox.getItems().setAll(CURRENCY_MAP.get(currency));
        denominationComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    public void onInsert(ActionEvent actionEvent) {
        Money money = denominationComboBox.getValue();
        if (money != null) {
            insertCoin(money);
        }
    }

    private void updateButtonStates() {
        priceButtons.forEach((domainBtn, priceBtn) -> {
            if (!vendingMachine.isPurchasable(domainBtn)) {
                priceBtn.setText("품절");
                priceBtn.setDisable(true);
            } else {
                ProductType type = domainBtn.getRail().peek().getType();
                priceBtn.setDisable(vendingMachine.getBalance() < vendingMachine.getDisplayPrice(type));
            }
        });
    }

    private void insertCoin(Money money) {
        vendingMachine.insertCoin(money);
        balanceLabel.setText(vendingMachine.getBalance() + "원");
        updateButtonStates();
    }

    private void purchase(Button domainButton) {
        try {
            List<ProductItem> purchased = vendingMachine.pressButton(domainButton.getId());
            purchased.forEach(item -> purchasedMap.merge(item.getName(), 1, Integer::sum));

            productList.getChildren().clear();
            purchasedMap.forEach((name, count) ->
                    productList.getChildren().add(new Label(name + ": " + count)));

            balanceLabel.setText(vendingMachine.getBalance() + "원");
            updateButtonStates();

        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("구매 실패");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void refund(ActionEvent actionEvent) {
        try {
            Map<Money, Integer> refundMap = vendingMachine.refund();

            refundedList.getChildren().clear();
            refundMap.forEach((money, count) ->
                    refundedList.getChildren().add(new Label(money.getWonValue() + "원: " + count + "개")));

            balanceLabel.setText(vendingMachine.getBalance() + "원");
            updateButtonStates();

        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("반환 실패");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
