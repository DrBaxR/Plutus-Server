package org.pcbe.model;

public class Order {

    private String name;
    private OrderType type;
    private Integer quantity;

    public enum OrderType {
        BUY,
        SELL
    }

    public Order(String name, OrderType type, Integer quantity) {
        this.name = name;
        this.type = type;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
