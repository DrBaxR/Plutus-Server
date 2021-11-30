package org.pcbe.util;

import org.pcbe.model.Stock;
import java.util.concurrent.ConcurrentLinkedDeque;

public class StocksArray {
    public static ConcurrentLinkedDeque<Stock> stocks = new ConcurrentLinkedDeque<>();

    static {
        stocks.add(new Stock("STK1", 100, 20));
        stocks.add(new Stock("STK2", 140, 40));
        stocks.add(new Stock("STK3", 120, 25));
        stocks.add(new Stock("STK4", 150, 30));
    }
}
