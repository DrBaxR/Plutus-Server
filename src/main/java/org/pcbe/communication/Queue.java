package org.pcbe.communication;

import org.pcbe.model.Order;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Queue {

    private final ConcurrentLinkedQueue<Order> queue;

    private static Queue singleton;

    private Queue() {
        queue = new ConcurrentLinkedQueue<>();
    }

    // may have concurrency issues, need to check + also check behavior
    public Order getOrder(Order.OrderType type) {
        Order peek = queue.peek();
        while(peek != null && peek.getType() != type) {
            queue.poll();
            peek = queue.peek();
        }
        return queue.poll();
    }

    public void addOrder(Order order) {
        queue.add(order);
    }

    public static Queue newInstance() {
        if (singleton != null) {
            singleton = new Queue();
        }
        return singleton;
    }

}
