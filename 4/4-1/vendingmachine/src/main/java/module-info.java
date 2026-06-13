module com.example.vendingmachine {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.joda.money;


    opens com.example.vendingmachine to javafx.fxml;
    exports com.example.vendingmachine;
    exports com.example.vendingmachine.event;
    opens com.example.vendingmachine.event to javafx.fxml;
    exports com.example.vendingmachine.product;
    opens com.example.vendingmachine.product to javafx.fxml;
    exports com.example.vendingmachine.money;
    opens com.example.vendingmachine.money to javafx.fxml;
}