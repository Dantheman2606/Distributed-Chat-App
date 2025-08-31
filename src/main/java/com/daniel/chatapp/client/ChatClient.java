package com.daniel.chatapp.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    public ChatClient(String serverAddress, int serverPort, String username) {
        try {
            this.socket = new Socket(serverAddress, serverPort);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.username = username;

            System.out.println("Connected to server at "+serverAddress+": "+serverPort);

            new Thread(new IncomingReader()).start();

            out.println(username);

            Scanner sc = new Scanner(System.in);
            while(true) {
                String message = sc.nextLine();
                out.println("["+username+"] : "+message);
            }
        }
        catch (IOException e) {
            System.out.println("Error connecting to server: "+e.getMessage());
        }
    }

    private class IncomingReader implements Runnable{
        public void run() {
            try{
                String msg;
                while((msg = in.readLine()) != null) {
                    System.out.println(msg);
                }
            }
            catch (IOException e) {
                System.out.println("Connection closed.");
            }
        }
    }
}