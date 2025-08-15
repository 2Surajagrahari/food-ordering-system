package com.restaurant;

import static spark.Spark.*;
import com.google.gson.Gson;
import java.util.*;

public class FoodOrderAPI {

    private static Map<String, Double> menu = new HashMap<>();
    private static Queue<Order> queue = new LinkedList<>();
    private static Order currentOrder = null;
    private static Gson gson = new Gson();

    public static void main(String[] args) {

        port(8080);

        // ===== CORS =====
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type");
        });

        options("/*", (request, response) -> {
            String reqHeaders = request.headers("Access-Control-Request-Headers");
            if (reqHeaders != null) {
                response.header("Access-Control-Allow-Headers", reqHeaders);
            }
            String reqMethod = request.headers("Access-Control-Request-Method");
            if (reqMethod != null) {
                response.header("Access-Control-Allow-Methods", reqMethod);
            }
            return "OK";
        });

        // ===== Menu =====
        menu.put("Burger", 5.99);
        menu.put("Pizza", 8.49);
        menu.put("Salad", 4.25);
        menu.put("Pasta", 7.35);
        menu.put("Fries", 2.99);

        get("/menu", (req, res) -> {
            res.type("application/json");
            return gson.toJson(menu);
        });

        // ===== Place Order =====
        post("/orders", (req, res) -> {
            res.type("application/json");
            OrderRequest orderReq = gson.fromJson(req.body(), OrderRequest.class);

            if (orderReq.customerName == null || orderReq.customerName.isEmpty()) {
                res.status(400);
                return gson.toJson(Map.of("message", "Customer name is required"));
            }
            if (orderReq.items == null || orderReq.items.isEmpty()) {
                res.status(400);
                return gson.toJson(Map.of("message", "Order must have at least one item"));
            }

            String orderId = UUID.randomUUID().toString();
            double totalAmount = 0.0;

            for (Map.Entry<String, Integer> entry : orderReq.items.entrySet()) {
                double price = menu.getOrDefault(entry.getKey(), 0.0);
                totalAmount += price * entry.getValue();
            }

            Order order = new Order(orderId, orderReq.customerName, orderReq.items, orderReq.isVIP, totalAmount);
            if (order.isVIP) {
                LinkedList<Order> temp = new LinkedList<>(queue);
                temp.addFirst(order);
                queue = temp;
            } else {
                queue.add(order);
            }

            return gson.toJson(Map.of("orderId", orderId, "totalAmount", totalAmount));
        });

        // ===== Queue =====
        get("/queue", (req, res) -> {
            res.type("application/json");
            return gson.toJson(queue);
        });

        // ===== Next Order =====
        get("/next", (req, res) -> {
            res.type("application/json");
            currentOrder = queue.poll();
            if (currentOrder == null) {
                res.status(404);
                return gson.toJson(Map.of("message", "No orders in queue"));
            }
            return gson.toJson(currentOrder);
        });

        // ===== Complete Order =====
        get("/complete/:id", (req, res) -> {
            res.type("application/json");
            String id = req.params(":id");
            if (currentOrder != null && currentOrder.orderId.equals(id)) {
                currentOrder = null;
                return gson.toJson(Map.of("message", "Order completed"));
            } else {
                res.status(404);
                return gson.toJson(Map.of("message", "No matching current order"));
            }
        });
    }

    // ===== Order Classes =====
    static class OrderRequest {
        String customerName;
        Map<String, Integer> items;
        boolean isVIP;
    }

    static class Order {
        String orderId;
        String customerName;
        List<Item> items;
        boolean isVIP;
        double totalAmount;

        Order(String orderId, String customerName, Map<String, Integer> itemsMap, boolean isVIP, double totalAmount) {
            this.orderId = orderId;
            this.customerName = customerName;
            this.items = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : itemsMap.entrySet()) {
                this.items.add(new Item(entry.getKey(), entry.getValue()));
            }
            this.isVIP = isVIP;
            this.totalAmount = totalAmount;
        }
    }

    static class Item {
        String name;
        int quantity;

        Item(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
        }
    }
}
