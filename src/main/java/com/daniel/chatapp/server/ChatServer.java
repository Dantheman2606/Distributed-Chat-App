package com.daniel.chatapp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
    private int port;
    private CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();
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
                clients.add(handler);

                new Thread(handler).start();
            }

        } catch (IOException e) {
            if(running) e.printStackTrace();
        }
    }

    public void broadcast(String message, ClientHandler sender) {
        for(ClientHandler client: clients) {
            if(client != sender) {
                client.sendMessage(message);
            }
        }
        System.out.println(message); // server console log
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
        if(client.getUsername() != null) {
            broadcast("[" + client.getUsername() + "] has left the chat.", null);
        }
    }

    public void stop() {
        running = false;
        try {
            for(ClientHandler client : clients) {
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
