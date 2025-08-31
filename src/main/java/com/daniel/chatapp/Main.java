package com.daniel.chatapp;

import com.daniel.chatapp.client.ChatClient;
import com.daniel.chatapp.server.ChatServer;

public class Main {
    public static void main(String[] args) {
        if(args.length > 0 && args[0].equalsIgnoreCase("server")) {
            new ChatServer(12345).start();
        }
        else {
            new ChatClient("localhost", 12345, args[1]);
        }
    }
}