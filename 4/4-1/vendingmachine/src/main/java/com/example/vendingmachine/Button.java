package com.example.vendingmachine;

public class Button {
    private final int id;
    private Rail rail;

    public Button(int id) {
        this.id = id;
    }

    void connectRail(Rail rail) {
        this.rail = rail;
    }

    Rail getRail() {
        return rail;
    }

    public int getId()  { return id; }
}