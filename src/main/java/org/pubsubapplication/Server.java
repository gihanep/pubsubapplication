package org.pubsubapplication;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The Server class that handles multiple concurrent client connections.
 */

public class Server {
    private static List<PrintWriter> publisherWriters = new ArrayList<>();
    private static List<PrintWriter> subscriberWriters = new ArrayList<>();

    public static void main(String[] args) {
        // Check the number of command-line arguments
        if (args.length != 1) {
            System.out.println("Usage: java Server <PORT>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port + "...");

            while (true) {
                // Accept a new client connection
                Socket clientSocket = serverSocket.accept();

                // Create a PrintWriter for the current client and add it to the appropriate list
                PrintWriter clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                String role = splitString(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine(), ":")[0];
                System.out.println("A "+ role + " connected to the server");


                if (role.equalsIgnoreCase("PUBLISHER")) {
                    publisherWriters.add(clientWriter);
                } else if (role.equalsIgnoreCase("SUBSCRIBER")) {
                    subscriberWriters.add(clientWriter);
                }

                // Create a new thread to handle the client
                Thread clientThread = new Thread(new ClientHandler(clientSocket, clientWriter, role));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter clientWriter;
        private String role;

        public ClientHandler(Socket clientSocket, PrintWriter clientWriter, String role) {
            this.clientSocket = clientSocket;
            this.clientWriter = clientWriter;
            this.role = role;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            ) {
                String message;
                while ((message = in.readLine()) != null) {
                    // Print received message from the client
                    System.out.println(message);

                    if (role.equalsIgnoreCase("PUBLISHER")) {
                        // Only publishers are allowed to send messages
                        // Send the message to all connected subscribers
                        for (PrintWriter subscriber : subscriberWriters) {
                            subscriber.println(message);
                        }
                    } else {
                        // Subscribers are not allowed to send messages
                        clientWriter.println("Subscribers are not allowed to send messages.");
                    }

                    // Terminate the process if the client sends "terminate"
                    if (splitString(message,":")[1].equalsIgnoreCase("terminate")) {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                    removeClient(clientWriter);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void removeClient(PrintWriter writer) {
        publisherWriters.remove(writer);
        subscriberWriters.remove(writer);
    }

    public static String[] splitString(String input, String delimiter) {
        return input.split(delimiter);
    }
}
