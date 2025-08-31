package com.daniel.chatapp.server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private Socket socket;
    private ChatServer server;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {

        try{
            in = new BufferedReader(new InputStreamReader((socket.getInputStream())));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Welcome to the Chat!");

            String message;

            while((message = in.readLine()) != null) {
                System.out.println(message);
                server.broadcast(message, this);
            }
        }
        catch (IOException e) {
            System.out.println("Client disconnected!");
        }
        finally {
            try{
                socket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            server.removeClient(this);
        }

    }

    public void sendMessage(String message) {
        out.println(message);
    }
}