package com.restaurant;

public class OrderItem {
    public final String name;
    public final double price;
    public final int quantity;

    public OrderItem(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}