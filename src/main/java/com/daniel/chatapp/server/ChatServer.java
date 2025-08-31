package com.daniel.chatapp.server;

//import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
    private int port;
    private CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() {
        System.out.println("Server started on port: "+port);

        try(ServerSocket serverSocket = new ServerSocket(port)) {
            while(true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected!");

                ClientHandler handler = new ClientHandler(socket, this);
                clients.add(handler);

                new Thread(handler).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcast(String message, ClientHandler sender) {
        for(ClientHandler client: clients) {
            if(client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }
}