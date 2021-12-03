package org.pcbe.communication;


import org.pcbe.model.Order;

import java.util.concurrent.*;

public class Queue {

    private static ConcurrentMap<Order.OrderType, BlockingQueue<Order>> orders;

    static {
        orders = new ConcurrentHashMap<>();
        orders.put(Order.OrderType.BUY, new LinkedBlockingDeque<>());
        orders.put(Order.OrderType.SELL, new LinkedBlockingQueue<>());
    }

    public static void placeOrder(Order.OrderType type, Order order) {
        try {
            orders.get(type).put(order);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Order getOrder(Order.OrderType type) {
        try {
            return orders.get(type).take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

}
