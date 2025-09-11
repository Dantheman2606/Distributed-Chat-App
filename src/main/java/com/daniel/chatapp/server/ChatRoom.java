package com.daniel.chatapp.server;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ChatRoom {

    private final String name;
    private final String password;

    private final Set<ClientHandler> members;

    public ChatRoom(String name, String password) {
        this.name = name;
        this.password = password;
        members = ConcurrentHashMap.newKeySet();
    }

    public String getName() {
        return name;
    }

    public boolean checkPassword(String input) {
        return password.equals(input);
    }

    public void join(ClientHandler client) {
        members.add(client);
        broadcast("[system]: " + client.getUsername() + " joined the room.");

    }

    public void leave(ClientHandler client) {
        if(members.remove(client)) {
            broadcast("[system]: " + client.getUsername() + " left the room.");
        }
    }

    public void broadcast(String message) {
        for(ClientHandler c: members) {
            c.sendMessage(message);
        }
    }

    public void userList(ClientHandler requester) {
        for(ClientHandler c: members) {
            if(c != requester) {
                requester.sendMessage(c.getUsername());
            }
            else {
                requester.sendMessage(c.getUsername() + " (YOU)");
            }
        }
    }

    public boolean isEmpty() {
        return members.isEmpty();
    }
}
