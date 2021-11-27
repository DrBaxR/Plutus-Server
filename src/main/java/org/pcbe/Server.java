package org.pcbe;

import java.net.*;
import java.io.*;
import java.util.Scanner;

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
                out.println("bye");

                System.out.println("Client disconnected...");
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void showOptions() {
            out.println("Pick 1, 2 or 3");
        }

        private void handleOption(int pickedOption) {
            switch (pickedOption) {
                case 1:
                    out.println("This would place a BUY order");
                    break;
                case 2:
                    out.println("This would place a SELL order");
                    break;
                case 3:
                    transmissionOver = true;
                    break;
            }
        }

    }

}
