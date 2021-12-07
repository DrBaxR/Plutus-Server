package org.pcbe;

import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.pcbe.communication.Queue;
import org.pcbe.dto.ClientMessage;
import org.pcbe.model.Order;
import org.pcbe.model.Stock;
import org.pcbe.util.Communication;
import org.pcbe.util.StocksArray;

import java.net.*;
import java.io.*;
import java.util.Iterator;
import java.util.Objects;
import java.util.Properties;

public class Server {
    public static Producer<String, String> producer;

    private ServerSocket serverSocket;
    private static final int consumerThreadNumber = 2;

    private static final float SELL_PRICE_MULTIPLIER_PER_UNIT = 0.0005f;
    private static final float BUY_PRICE_MULTIPLIER_PER_UNIT = 0.0006f;

    public void start(int port) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);
        initialiseStocksUI();
//        for (int i = 0; i < 100; i++)
//            producer.send(new ProducerRecord<String, String>("test-topic", Integer.toString(i), Integer.toString(i)));

        try {
            serverSocket = new ServerSocket(port);
            for (int i = 0; i < consumerThreadNumber; i++) {
                new ConsumerThread(Order.OrderType.BUY).start();
                new ConsumerThread(Order.OrderType.SELL).start();
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

                while (!transmissionOver) {
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
                    Queue.placeOrder(buyOrder.getType(), buyOrder);

                    Communication.sendMessage(out, "BUY placed");
                    break;
                case 2:
                    Order sellOrder = new Order(message.getStockName(), Order.OrderType.SELL, message.getQuantity());
                    Queue.placeOrder(sellOrder.getType(), sellOrder);

                    Communication.sendMessage(out, "SELL placed");
                    break;
                case 3:
                    Communication.sendMessage(out, "bye");
                    transmissionOver = true;
                    break;
            }
        }

    }

    private static class ConsumerThread extends Thread {
        private final Order.OrderType type;

        public ConsumerThread(Order.OrderType type) {
            this.type = type;
        }

        @Override
        public void run() {
            while (true) {
                var order = Queue.getOrder(type);
                if (order != null) {
                    var orderStock = StocksArray.stocks
                        .stream()
                        .filter(stock -> Objects.equals(stock.getName(), order.getName()))
                        .findFirst()
                        .orElseThrow();
                    if ((order.getType().equals(Order.OrderType.SELL)) || (order.getType().equals(Order.OrderType.BUY) && orderStock.getQuantity() > order.getQuantity())) {
                        orderStock.setPrice(
                            order.getType() == Order.OrderType.BUY
                                ? orderStock.getPrice() * (1f + order.getQuantity() * BUY_PRICE_MULTIPLIER_PER_UNIT)
                                : orderStock.getPrice() * (1f - order.getQuantity() * SELL_PRICE_MULTIPLIER_PER_UNIT)
                        );

                        orderStock.setQuantity(
                            orderStock.getQuantity() + (
                                order.getType() == Order.OrderType.SELL
                                    ? +order.getQuantity()
                                    : -order.getQuantity()
                            )
                        );
                        System.out.println(orderStock);

                        TopicMessagePayload messagePayload = new TopicMessagePayload(orderStock.getQuantity(), orderStock.getPrice());
                        producer.send(new ProducerRecord<String, String>("plutus", orderStock.getName(), new Gson().toJson(messagePayload)));
                    }
                }
            }
        }

        private static class TopicMessagePayload {
            private int quantity;
            private float price;

            public TopicMessagePayload() {
            }

            public TopicMessagePayload(int quantity, float price) {
                this.quantity = quantity;
                this.price = price;
            }

            public int getQuantity() {
                return quantity;
            }

            public void setQuantity(int quantity) {
                this.quantity = quantity;
            }

            public float getPrice() {
                return price;
            }

            public void setPrice(float price) {
                this.price = price;
            }
        }
    }
    public static void initialiseStocksUI() {

        Iterator<Stock> i = StocksArray.stocks.iterator();

        while(i.hasNext()) {

            ConsumerThread.TopicMessagePayload messagePayload = new ConsumerThread.TopicMessagePayload(i.next().getQuantity(), i.next().getPrice());
            producer.send(new ProducerRecord<String, String>("plutus", i.next().getName(), new Gson().toJson(messagePayload)));

        }

    }

}
