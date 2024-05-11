package org.pubsubapplication;

import java.io.*;
import java.net.*;

/**
 * The Client class
 */

public class Client {
    public static void main(String[] args) {
        if (args.length != 3) {
            // Check if the correct number of command-line arguments are provided
            System.out.println("Usage: java Client <Server IP> <Server PORT> <ROLE>");
            System.exit(1);
        }

        // Read server details and role from command-line arguments
        String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);
        String role = args[2];

        // Ensure the role is either "PUBLISHER" or "SUBSCRIBER"
        if (!role.equalsIgnoreCase("PUBLISHER") && !role.equalsIgnoreCase("SUBSCRIBER")) {
            System.out.println("Invalid role. Please use 'PUBLISHER' or 'SUBSCRIBER'.");
            System.exit(1);
        }

        // Create required references and make sure they are properly closed using try with resources
        try (Socket socket = new Socket(serverIP, serverPort);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println(role + " is connected to server at " + serverIP + ":" + serverPort);

            // Create a separate thread for reading messages from the server
            Thread serverListenerThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println("Server: " + serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            serverListenerThread.start();

            String message;
            while (true) {
                System.out.print(role + ": ");
                message = userInput.readLine();

                // Include the role in the message to send to server
                out.println(role + ":" + message);

                // Check whether the user terminates the program
                if (message.equalsIgnoreCase("terminate")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
