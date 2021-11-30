package org.pcbe;

import org.pcbe.communication.Queue;
import org.pcbe.dto.ClientMessage;
import org.pcbe.model.Order;
import org.pcbe.util.Communication;
import org.pcbe.util.StocksArray;

import java.net.*;
import java.io.*;
import java.util.Objects;

public class Server {

    private ServerSocket serverSocket;
    private static final int consumerThreadNumber = 1;

    private static final float SELL_PRICE_MULTIPLIER_PER_UNIT = 0.0005f;
    private static final float BUY_PRICE_MULTIPLIER_PER_UNIT = 0.001f;

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            for(int i = 0; i < consumerThreadNumber ; i++) {
                new ConsumerThread().start();
            }
            while (true)
                new ClientHandler(serverSocket.accept()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {

        private final Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        private boolean transmissionOver = false;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            System.out.println("Client connected...");
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                while(!transmissionOver) {
                    showOptions();
                    handleOption(Communication.readMessage(in));
                }

                System.out.println("Client disconnected...");
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void showOptions() {
            String options =
                "Pick one of the following:\n" +
                "\t1. Place a BUY order\n" +
                "\t2. Place a SELL order\n" +
                "\t3. Disconnect";
            Communication.sendMessage(out, options);
        }

        private void handleOption(ClientMessage message) {
            switch (message.getOption()) {
                case 1:
                    Order buyOrder = new Order(message.getStockName(), Order.OrderType.BUY, message.getQuantity());
                    Queue.newInstance().addOrder(buyOrder);

                    Communication.sendMessage(out, "This would place a BUY order");
                    break;
                case 2:
                    Order sellOrder = new Order(message.getStockName(), Order.OrderType.SELL, message.getQuantity());
                    Queue.newInstance().addOrder(sellOrder);

                    Communication.sendMessage(out, "This would place a SELL order");
                    break;
                case 3:
                    Communication.sendMessage(out, "bye");
                    transmissionOver = true;
                    break;
            }
        }

    }

    private static class ConsumerThread extends Thread{
        @Override
        public void run() {
            while(true) {
                var order = Queue.newInstance().getOrder();
                if(order != null) {
                    var orderStock = StocksArray.stocks
                            .stream()
                            .filter(stock -> Objects.equals(stock.getName(), order.getName()))
                            .findFirst()
                            .orElseThrow();
                    if((order.getType().equals(Order.OrderType.SELL)) || (order.getType().equals(Order.OrderType.BUY) && orderStock.getQuantity() > order.getQuantity())) {
                        orderStock.setPrice(
                            order.getType() == Order.OrderType.BUY
                                ? orderStock.getPrice() * (1f + order.getQuantity() * BUY_PRICE_MULTIPLIER_PER_UNIT)
                                : orderStock.getPrice() * (1f - order.getQuantity() * SELL_PRICE_MULTIPLIER_PER_UNIT)
                        );

                        orderStock.setQuantity(
                            orderStock.getQuantity() + (
                                order.getType() == Order.OrderType.SELL
                                    ? + order.getQuantity()
                                    : - order.getQuantity()
                            )
                        );
                        System.out.println(orderStock);
                    }
                }
            }
        }
    }

}
