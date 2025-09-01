package com.daniel.chatapp.server;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ChatServer server;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    private boolean regularDisconnection = false;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public String getUsername() {
//        while(username == null) {
//            try {
//                wait();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
        return username;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // first message from client is username
            this.username = in.readLine();
//            notifyAll();  // only after username is acquired, the getUsername function can work

            out.println("Welcome to the Chat, " + username + "!");
            server.broadcast("[" + username + "] has joined the chat.", this);

            String message;
            while((message = in.readLine()) != null) {
                if(message.equalsIgnoreCase("/quit")) break;

                if(message.equalsIgnoreCase("/users")) {
                    listUsers();
                    continue;
                }

                LocalDateTime dt = LocalDateTime.now();
                DateTimeFormatter dt_format = DateTimeFormatter.ofPattern("dd/MM HH:mm:ss");
                String formatted_dt = dt.format(dt_format);
                server.broadcast("[" + username + "] : " +formatted_dt+" : "+ message, this);
            }

        } catch(IOException e) {
            if(!regularDisconnection) {
                server.broadcast(username + " disconnected unexpectedly!", this);
            }
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

    public void listUsers() {
        server.listClients(this);
    }

    public void close() throws IOException {
        if(socket != null && !socket.isClosed()) {
            regularDisconnection = true;
            out.println("/quit");
//            out.println("Disconnected from server.");

            socket.close();
        }
    }
}
