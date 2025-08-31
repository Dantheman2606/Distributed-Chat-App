package com.daniel.chatapp.server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ChatServer server;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // first message from client is username
            this.username = in.readLine();
            out.println("Welcome to the Chat, " + username + "!");
            server.broadcast("[" + username + "] has joined the chat.", this);

            String message;
            while((message = in.readLine()) != null) {
                if(message.equalsIgnoreCase("/quit")) break;
                server.broadcast("[" + username + "] " + message, this);
            }

        } catch(IOException e) {
            System.out.println("Client disconnected unexpectedly!");
        } finally {
            try {
                close();
            } catch(IOException e) {
                e.printStackTrace();
            }
            server.removeClient(this);
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void close() throws IOException {
        if(socket != null && !socket.isClosed()) socket.close();
    }
}
