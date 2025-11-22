package com.solid.srp.bad;

public class LineItem {
    private String name;
    private double price;
    private int quantity;

    public LineItem(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}
