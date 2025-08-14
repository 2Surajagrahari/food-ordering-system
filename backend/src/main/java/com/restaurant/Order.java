package com.restaurant;

import java.util.UUID;
import java.util.List;

public class Order implements Comparable<Order> {
    public final String orderId;
    public final String customerName;
    public final List<OrderItem> items;
    public final boolean isVIP;
    public final long timestamp;
    public String status;
    public double totalAmount;

    public Order(String customerName, List<OrderItem> items, boolean isVIP) {
        this.orderId = UUID.randomUUID().toString();
        this.customerName = customerName;
        this.items = items;
        this.isVIP = isVIP;
        this.timestamp = System.currentTimeMillis();
        this.status = "waiting";
        this.totalAmount = calculateTotal();
    }

    private double calculateTotal() {
        return items.stream().mapToDouble(item -> item.price * item.quantity).sum();
    }

    @Override
    public int compareTo(Order other) {
        if (this.isVIP != other.isVIP) {
            return Boolean.compare(other.isVIP, this.isVIP);
        }
        return Long.compare(this.timestamp, other.timestamp);
    }
}