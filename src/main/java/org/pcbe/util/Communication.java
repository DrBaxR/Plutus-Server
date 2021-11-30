package org.pcbe.util;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Arrays;

public class Communication {

    private static final String END_TOKEN = "transmission_over";

    public static void sendMessage(PrintWriter out, String message) {
        String[] splitMessage = message.split("\n");
        Arrays.stream(splitMessage).forEach(out::println);
        out.println(END_TOKEN);
    }

    public static void readMessage(BufferedReader in) {
        // TODO
    }

}
