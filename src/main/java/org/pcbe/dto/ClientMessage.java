package org.pcbe.dto;

import java.io.Serializable;

public class ClientMessage implements Serializable {

    private int option;
    private String stockName;
    private Integer quantity;

    public ClientMessage() {
    }

    public ClientMessage(int option, String stockName, Integer quantity) {
        this.option = option;
        this.stockName = stockName;
        this.quantity = quantity;
    }

    public int getOption() {
        return option;
    }

    public void setOption(int option) {
        this.option = option;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
