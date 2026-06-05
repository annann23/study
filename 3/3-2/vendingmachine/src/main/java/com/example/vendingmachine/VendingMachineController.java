package com.example.vendingmachine;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.*;

public class VendingMachineController {
    private VendingMachine vendingMachine;
    private final Map<String, Integer> purchasedMap = new HashMap<>();
    private final Map<Button, javafx.scene.control.Button> priceButtons = new HashMap<>();

    @FXML private GridPane productGrid;
    @FXML private GridPane moneyGrid;
    @FXML private Label balanceLabel;
    @FXML private VBox productList;
    @FXML private VBox refundedList;

    @FXML
    private void initialize() {
        vendingMachine = new VendingMachine();
        initializeProducts();
        initializeMoneyButtons();
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

            javafx.scene.control.Button priceButton = new javafx.scene.control.Button(type.price() + "원");
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

    private void initializeMoneyButtons() {
        Money[] monies = {Money.WON_50000, Money.WON_10000, Money.WON_5000,
                Money.WON_1000, Money.WON_500, Money.WON_100};

        for (int i = 0; i < monies.length; i++) {
            javafx.scene.control.Button moneyButton = new javafx.scene.control.Button(monies[i].value() + "₩");
            moneyButton.getStyleClass().add("money-button");

            Money money = monies[i];
            moneyButton.setOnAction(e -> insertCoin(money));
            moneyGrid.add(moneyButton, i % 2, i / 2);
        }
    }

    private void updateButtonStates() {
        priceButtons.forEach((domainBtn, priceBtn) -> {
            Rail rail = domainBtn.getRail();
            if (rail.isSoldOut()) {
                priceBtn.setText("품절");
                priceBtn.setDisable(true);
            } else {
                priceBtn.setDisable(vendingMachine.getBalance() < rail.peek().getPrice());
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
            ProductItem purchased;
            purchased = vendingMachine.pressButton(domainButton.getId());
            purchasedMap.merge(purchased.getName(), 1, Integer::sum);

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
                    refundedList.getChildren().add(new Label(money.value() + "원: " + count + "개")));

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
