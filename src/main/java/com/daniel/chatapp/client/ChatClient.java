package com.daniel.chatapp.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final String username;

    private boolean running;
    private volatile String currentRoom = "global"; // start in global by default

    public ChatClient(String serverAddress, int serverPort, String username) throws IOException {
        this.socket = new Socket(serverAddress, serverPort);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.username = username;
        this.running = true;
    }

    public void start() {
        System.out.println("Connected to server as " + username);

        out.println(username); // send username first

        // Thread to read incoming messages
        new Thread(new IncomingReader()).start();

        // Main input loop
        Scanner sc = new Scanner(System.in);
        try {
            while (running) {
                System.out.print("[" + username + "@" + currentRoom + "] > ");
                String message = sc.nextLine();

                if (message.equalsIgnoreCase("/quit")) {
                    out.println("/quit");
                    socket.close();
                    break;
                }

                out.println(message);
            }
        } catch (IOException e) {
            System.out.println("Connection closed.");
        }
    }

    private class IncomingReader implements Runnable {
        public void run() {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    // Look for special messages from server telling us the current room
                    if (msg.startsWith("You are now in room: ")) {
                        currentRoom = msg.substring("You are now in room: ".length()).trim();
                    }

                    if (msg.equalsIgnoreCase("/quit")) {
                        socket.close();
                        running = false;
                        System.out.println("Disconnected from server.");
                        System.exit(0);
                        break;
                    }
                    System.out.println(msg);
                }
            } catch (IOException e) {
                System.out.println("Connection closed.");
            }
        }
    }
}
