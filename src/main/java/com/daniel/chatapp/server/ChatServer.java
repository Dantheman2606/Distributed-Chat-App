package com.daniel.chatapp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
    private int port;
//    private CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private ServerSocket serverSocket;
    private boolean running = false;

    public ChatServer(int port) {
        this.port = port;

    }

    public void start() {
        running = true;
        System.out.println("Server started on port: " + port);

        try(ServerSocket server = new ServerSocket(port)) {
            this.serverSocket = server;

            while(running) {
                Socket socket = server.accept();
                System.out.println("New client connected!");

                ClientHandler handler = new ClientHandler(socket, this);

                new Thread(handler).start();
//                System.out.println(handler.getUsername()+handler);
                clients.put(handler.getUsername(), handler);

            }

        } catch (IOException e) {
            if(running) e.printStackTrace();
        }
    }

    public void broadcast(String message, ClientHandler sender) {
        for(ClientHandler client: clients.values()) {
            if(client != sender) {
                client.sendMessage(message);
            }
        }
        System.out.println(message); // server console log
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client.getUsername());
        if(client.getUsername() != null) {
            broadcast("[" + client.getUsername() + "] has left the chat.", null);
        }
    }

    public void listClients(ClientHandler requester) {
        for(ClientHandler client: clients.values()) {
            if(client == requester) {
                requester.sendMessage(requester.getUsername() + " (You)");
                continue;
            }
            requester.sendMessage(client.getUsername());
        }
    }

    public void stop() {
        running = false;
        try {
            for(ClientHandler client : clients.values()) {
                client.sendMessage("[Server] Server is shutting down.");
                client.close();
            }
            if(serverSocket != null) serverSocket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server stopped.");
    }
}
