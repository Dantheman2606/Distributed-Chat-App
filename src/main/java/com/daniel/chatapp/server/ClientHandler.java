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

    private ChatRoom currentRoom;

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

            currentRoom = server.getOrCreateRoom("global", "");
            currentRoom.join(this);

            currentRoom.broadcast("[" + username + "] has joined the chat.");

            String message;
            while((message = in.readLine()) != null) {
                if(message.startsWith("/")) {
                    // Logic for /quit /users /msg
                    handleCommand(message);
                }
                else {
                    LocalDateTime dt = LocalDateTime.now();
                    DateTimeFormatter dt_format = DateTimeFormatter.ofPattern("dd/MM HH:mm:ss");
                    String formatted_dt = dt.format(dt_format);
                    currentRoom.broadcast("[" + username + "] : " +formatted_dt+" : "+ message);
                }
//                if(message.equalsIgnoreCase("/quit")) break;
//
//                if(message.equalsIgnoreCase("/users")) {
//                    listUsers();
//                    continue;
//                }
            }

        } catch(IOException e) {
            if(!regularDisconnection) {
                currentRoom.broadcast(username + " disconnected unexpectedly!");
            }
        } finally {
            try {
                close();
            } catch(IOException e) {
                e.printStackTrace();
            }
            if(currentRoom != null) {
                currentRoom.leave(this);
            }
            server.removeClient(this);
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void listUsers() {
        currentRoom.userList(this);
    }

    public void close() throws IOException {
        if(socket != null && !socket.isClosed()) {
            regularDisconnection = true;
            out.println("/quit");
//            out.println("Disconnected from server.");

            socket.close();
        }
    }

    private void handleCommand(String message) {
        if (message.equalsIgnoreCase("/quit")) {
            disconnect();
            return;
        }

        if (message.equalsIgnoreCase("/users")) {
            listUsers();
            return;
        }

        if (message.startsWith("/msg ")) {
            handlePrivateMessage(message);
            return;
        }

        if (message.startsWith("/createroom ")) {
            String[] parts = message.split(" ", 3);
            if (parts.length < 2) {
                out.println("Usage: /createroom <roomName> [password]");
                return;
            }
            String roomName = parts[1];
            String password = (parts.length == 3) ? parts[2] : "";
            ChatRoom room = server.getOrCreateRoom(roomName, password);
            switchRoom(room);
            return;
        }
        if (message.startsWith("/listrooms")) {
            server.listRooms(this);
            return;
        }

        if (message.startsWith("/joinroom ")) {
            String[] parts = message.split(" ", 3);
            if (parts.length < 2) {
                out.println("Usage: /joinroom <roomName> [password]");
                return;
            }
            String roomName = parts[1];
            String password = (parts.length == 3) ? parts[2] : "";
            ChatRoom room = server.getRoom(roomName);
            if (room == null) {
                out.println("Room does not exist.");
                return;
            }
            if (!room.checkPassword(password)) {
                out.println("Incorrect password.");
                return;
            }
            switchRoom(room);
            return;
        }

        if (message.equalsIgnoreCase("/leaveroom")) {
            // return to global room
            ChatRoom global = server.getOrCreateRoom("global", "");
            switchRoom(global);
            return;
        }

        out.println("Unknown command: " + message);
    }

    private void switchRoom(ChatRoom newRoom) {
        if (currentRoom != null) {
            currentRoom.leave(this);
        }
        currentRoom = newRoom;
        currentRoom.join(this);
        out.println("You are now in room: " + currentRoom.getName());
    }

    private void disconnect() {
        if (socket != null && !socket.isClosed()) {
            regularDisconnection = true;
            out.println("You have been disconnected.");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handlePrivateMessage(String message) {
        String[] parts = message.split(" ", 3);
        if (parts.length < 3) {
            out.println("Usage: /msg <username> <message>");
            return;
        }
        String targetUser = parts[1];
        String privateMessage = parts[2];
        boolean sent = server.sendPrivateMessage(this, targetUser, privateMessage);
        if (!sent) {
            out.println("User '" + targetUser + "' not found or not online.");
        }
    }

}
