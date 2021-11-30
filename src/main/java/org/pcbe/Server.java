package org.pcbe;

import org.pcbe.util.Communication;

import java.net.*;
import java.io.*;

public class Server {

    private ServerSocket serverSocket;

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
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
                    String option = in.readLine();
                    int number = Integer.parseInt(option);
                    handleOption(number);
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

        private void handleOption(int pickedOption) {
            switch (pickedOption) {
                case 1:
                    Communication.sendMessage(out, "This would place a BUY order");
                    break;
                case 2:
                    Communication.sendMessage(out, "This would place a SELL order");
                    break;
                case 3:
                    Communication.sendMessage(out, "bye");
                    transmissionOver = true;
                    break;
            }
        }

    }

}
