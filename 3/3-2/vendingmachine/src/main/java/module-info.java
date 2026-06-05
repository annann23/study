module com.example.vendingmachine {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.vendingmachine to javafx.fxml;
    exports com.example.vendingmachine;
}