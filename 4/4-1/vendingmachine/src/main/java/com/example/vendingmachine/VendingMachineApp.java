package com.example.vendingmachine;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VendingMachineApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("vending-machine-view.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("자판기");
        stage.setScene(scene);
        stage.show();
    }
}