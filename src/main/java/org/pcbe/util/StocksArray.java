package org.pcbe.util;

import org.pcbe.model.Stock;
import java.util.concurrent.ConcurrentLinkedDeque;

public class StocksArray implements Runnable{
    private ConcurrentLinkedDeque<Stock> stocks;

    public void addTask(ConcurrentLinkedDeque<Stock> list) {
        this.stocks = list;
    }

    @Override
    public void run() {
        stocks.add(new Stock("STK1",100 ,20 ));
        stocks.add(new Stock("STK2",140 ,40 ));
        stocks.add(new Stock("STK3",120 ,25 ));
        stocks.add(new Stock("STK4",150 ,30 ));
    }
}
