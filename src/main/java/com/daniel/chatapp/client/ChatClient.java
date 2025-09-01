package com.daniel.chatapp.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    private boolean running;

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
            while(running) {
//                System.out.println(socket.isClosed());
//
//                if(socket.isClosed()) {
//                    System.out.println(socket.isClosed());
//                    System.out.println("Disconnected from server.");
//                    break;
//                }
////                System.out.print("["+username+"]");
                String message = sc.nextLine();
                if(message.equalsIgnoreCase("/quit")) {
                    out.println("/quit");
                    socket.close();
//                    System.out.println("Disconnected from server.");
                    break;
                }
                out.println(message); // server prepends username
            }
        } catch(IOException e) {
            System.out.println("Connection closed.");
        }
    }

    private class IncomingReader implements Runnable {
        public void run() {
            try {
                String msg;
                while((msg = in.readLine()) != null) {

                    if(msg.equalsIgnoreCase("/quit")) {
                        socket.close();
                        running = false;
                        System.out.println("Disconnected from server.");
                        System.exit(0);
                        break;
                    }
                    System.out.println(msg);
                }
            } catch(IOException e) {
                System.out.println("Connection closed.");
            }
        }
    }
}
