package org.pcbe.util;

import com.google.gson.Gson;
import org.pcbe.dto.ClientMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class Communication {

    private static final String END_TOKEN = "transmission_over";

    public static void sendMessage(PrintWriter out, String message) {
        String[] splitMessage = message.split("\n");
        Arrays.stream(splitMessage).forEach(out::println);
        out.println(END_TOKEN);
    }

    public static ClientMessage readMessage(BufferedReader in) {
        ClientMessage message = null;
        try {
            String serializedMessage = in.readLine();
            message = new Gson().fromJson(serializedMessage, ClientMessage.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

}
