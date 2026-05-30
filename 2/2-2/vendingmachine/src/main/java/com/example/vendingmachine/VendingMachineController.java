package com.example.vendingmachine;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class VendingMachineController {
    private VendingMachine vendingMachine;
    private final Map<String, Integer> purchasedMap = new HashMap<>();
    private final Map<Product, Button> priceButtons = new HashMap<>();

    @FXML private GridPane productGrid;
    @FXML private GridPane moneyGrid;
    @FXML private Label balanceLabel;
    @FXML private VBox productList;
    @FXML private VBox refundedList;

    @FXML
    private void initialize() {
        this.initializeProducts();
        this.initializeMoneyButtons();
    }

    private void initializeProducts() {
        Product[] products = {
                new Product("콜라", 1000, 10),
                new Product("알로에주스", 900, 5),
                new Product("T.O.P", 1200, 3),
                new Product("아침햇살", 800, 1),
                new Product("사이다", 1000, 1),
                new Product("제로콜라", 1300, 2),
                new Product("물", 700, 4),
                new Product("포카리스웨트", 1300, 0),
                new Product("오렌지주스", 1000, 1),
        };

        vendingMachine = new VendingMachine(products);

        for(int i = 0; i<products.length; i++) {
            VBox productImage = new VBox();
            VBox productBox = new VBox();

            productBox.getStyleClass().add("purchased-box");

            Label productName = new Label(products[i].getName());
            productImage.getChildren().add(productName);

            Button productPrice;

            if(products[i].isSoldOut()) {
                productPrice = new Button("품절");
            } else {
                productPrice = new Button(products[i].getPrice() + "원");
            }

            productPrice.setDisable(true); //처음에는 넣은 금액이 없으므로 다 disabled

            priceButtons.put(products[i], productPrice);

            productImage.getStyleClass().add("product-image");
            productPrice.getStyleClass().add("price-button");

            Product product = products[i];
            productPrice.setOnAction(e -> purchase(product));

            productBox.getChildren().addAll(productImage, productPrice);
            productGrid.add(productBox, i%3, i/3);
        }
    }

    private void initializeMoneyButtons() {
        Money[] monies = {Money.WON_50000, Money.WON_10000, Money.WON_5000,
                Money.WON_1000, Money.WON_500, Money.WON_100};

        for(int i = 0; i<monies.length; i++){
            Button moneyButton = new Button(Integer.toString(monies[i].value()) + '₩');
            moneyButton.getStyleClass().add("money-button");

            Money money = monies[i];
            moneyButton.setOnAction(e -> insertCoin(money));
            moneyGrid.add(moneyButton, i%2, i/2);
        }
    }

    private void updateButtonStates() {
        priceButtons.forEach((p, btn) -> {
            if (p.isSoldOut()) {
                btn.setDisable(true);
            } else {
                btn.setDisable(!vendingMachine.isPurchasable(p));
            }
        });
    }

    private void insertCoin(Money money) {
        vendingMachine.insertCoin(money);
        balanceLabel.setText(vendingMachine.getBalance() + "원");

        updateButtonStates();
    }

    private void purchase(Product product) {
        try {
            vendingMachine.purchase(product);
            purchasedMap.put(product.getName(), purchasedMap.getOrDefault(product.getName(), 0) + 1);

            productList.getChildren().clear();
            purchasedMap.forEach((name, count) -> productList.getChildren().add(new Label(name + ": " + count)));

            balanceLabel.setText(vendingMachine.getBalance()+ "원");

            if(product.isSoldOut()) {
                priceButtons.get(product).setText("품절");
                priceButtons.get(product).setDisable(true);
            }

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
            refundMap.forEach((money, amount) -> {
                refundedList.getChildren().add(new Label(money.value() + "원:" + amount + "개"));
            });

            balanceLabel.setText(vendingMachine.getBalance() + "원");

            updateButtonStates();
        } catch(IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("반환 실패");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
