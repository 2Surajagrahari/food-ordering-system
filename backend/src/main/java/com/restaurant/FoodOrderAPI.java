package com.restaurant;

import com.google.gson.Gson;
import java.util.*;
import static spark.Spark.*;

public class FoodOrderAPI {
    private static final Gson gson = new Gson();
    private static final FoodOrderQueue orderQueue = new FoodOrderQueue();
    private static final Map<String, Double> menu = initializeMenu();

    public static void main(String[] args) {
        port(8080);

        // Enable CORS
        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.type("application/json");
        });

        // Handle preflight requests
        options("/*", (req, res) -> {
            return "OK";
        });

        // POST /orders endpoint
        post("/orders", (req, res) -> {
            try {
                OrderRequest orderReq = gson.fromJson(req.body(), OrderRequest.class);
                List<OrderItem> items = new ArrayList<>();
                
                for (Map.Entry<String, Integer> entry : orderReq.items.entrySet()) {
                    String itemName = entry.getKey();
                    if (menu.containsKey(itemName)) {
                        items.add(new OrderItem(itemName, menu.get(itemName), entry.getValue()));
                    }
                }

                Order order = new Order(orderReq.customerName, items, orderReq.isVIP);
                orderQueue.enqueue(order);
                
                Map<String, Object> response = new HashMap<>();
                response.put("orderId", order.orderId);
                response.put("totalAmount", order.totalAmount);
                return gson.toJson(response);
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("error", "Invalid order data"));
            }
        });

        // Get next order
        get("/next", (req, res) -> {
            Order next = orderQueue.peek();
            next.status = "preparing";
            return gson.toJson(next);
        });

        // Complete order
        post("/complete/:orderId", (req, res) -> {
            Order completed = orderQueue.dequeue();
            completed.status = "completed";
            return gson.toJson(completed);
        });

        // View queue
        get("/queue", (req, res) -> gson.toJson(orderQueue.getQueueSnapshot()));
    }

    static class OrderRequest {
        String customerName;
        Map<String, Integer> items;
        boolean isVIP;
    }
}