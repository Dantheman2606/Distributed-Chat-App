package com.daniel.chatapp;

import com.daniel.chatapp.client.ChatClient;
import com.daniel.chatapp.server.ChatServer;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Start as server or client? (server/client): ");
        String mode = sc.nextLine().trim();

        if(mode.equalsIgnoreCase("server")) {
            System.out.print("Enter port to run server on: ");
            int port = Integer.parseInt(sc.nextLine());
            ChatServer server = new ChatServer(port);

            // Start server in a separate thread
            new Thread(server::start).start();

            // Server console input loop
            while(true) {
//                System.out.print("[Server]");

                String msg = sc.nextLine();
                if(msg.equalsIgnoreCase("/quit")) {
                    server.stop();
                    break;
                }
                server.broadcast("[Server] " + msg, null);
            }

        } else if(mode.equalsIgnoreCase("client")) {
            System.out.print("Enter server address: ");
            String serverAddress = sc.nextLine();
            System.out.print("Enter server port: ");
            int port = Integer.parseInt(sc.nextLine());
            System.out.print("Enter your username: ");
            String username = sc.nextLine();

            try {
                new ChatClient(serverAddress, port, username).start();
            } catch (Exception e) {
                System.out.println("Error connecting to server: " + e.getMessage());
            }

        } else {
            System.out.println("Invalid mode. Please run again.");
        }
    }
}
