package com.restaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FoodOrderQueue {
    private final List<Order> heap;

    public FoodOrderQueue() {
        this.heap = new ArrayList<>();
    }

    public void enqueue(Order order) {
        heap.add(order);
        siftUp(heap.size() - 1);
    }

    public Order dequeue() {
        if (heap.isEmpty()) throw new IllegalStateException("Queue is empty");
        Order order = heap.get(0);
        heap.set(0, heap.get(heap.size() - 1));
        heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) siftDown(0);
        return order;
    }

    public Order peek() {
        if (heap.isEmpty()) throw new IllegalStateException("Queue is empty");
        return heap.get(0);
    }

    public List<Order> getQueueSnapshot() {
        List<Order> snapshot = new ArrayList<>(heap);
        Collections.sort(snapshot);
        return snapshot;
    }

    private void siftUp(int index) {
        int parent = (index - 1) / 2;
        while (index > 0 && heap.get(index).compareTo(heap.get(parent)) < 0) {
            swap(index, parent);
            index = parent;
            parent = (index - 1) / 2;
        }
    }

    private void siftDown(int index) {
        int left = 2 * index + 1;
        int right = 2 * index + 2;
        int smallest = index;

        if (left < heap.size() && heap.get(left).compareTo(heap.get(smallest)) < 0) {
            smallest = left;
        }
        if (right < heap.size() && heap.get(right).compareTo(heap.get(smallest)) < 0) {
            smallest = right;
        }

        if (smallest != index) {
            swap(index, smallest);
            siftDown(smallest);
        }
    }

    private void swap(int i, int j) {
        Order temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
}