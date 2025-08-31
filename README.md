# Java CLI Chat Application

![Java](https://img.shields.io/badge/Java-17+-blue) ![Maven](https://img.shields.io/badge/Maven-3.9+-green) 

A **multi-client, socket-based chat application** built in **Java** with a Command-Line Interface (CLI). Users can communicate in real-time through private messages or chat rooms.

---

## Features

- **Multi-client support** – multiple users can connect simultaneously.
- **CLI interface** – lightweight and easy to use.
- **Authentication (planned)** – register and login securely.
- **Private messaging (planned)** – send direct messages to other users.
- **Chat rooms (planned)** – join or create group chat rooms.
- **File transfer (future)** – send files between clients.

---

## Tech Stack

- **Language:** Java
- **Networking:** `java.net` sockets
- **Threading:** `java.lang.Thread` for concurrent client handling
- **Build Tool:** Maven
---

## Folder Structure
```bash
ChatApp/
│
├── src/
│ └── main/java/com/daniel/chatapp/
│ ├── server/
│ │ ├── Server.java
│ │ └── ClientHandler.java
│ └── client/
│ └── Client.java
│
├── pom.xml
└── README.md
```
---


## Getting Started

### Prerequisites

- Java JDK 17+ installed
- Maven installed (verify with `mvn -v`)

### Build the Project

Clone the repository and build the jar using Maven:

```bash
git clone https://github.com/Dantheman2606/Distributed-Chat-App.git
cd Distributed-Chat-App
mvn clean package
```
This will generate the jar file in the target/ directory, e.g., chatapp-1.2.jar.

### Run the Executable
```bash
java -jar target/chatapp-1.2.jar
```

### Usage
For running the server
```bash
Start as server or client? (server/client):
server
Enter port to run server on: 12345
Server started on port: 12345
```

For running as a client
```bash
Start as server or client? (server/client):
client
Enter server address: localhost
Enter server port: 12345
Enter your username: Dan
Connected to server as Dan
```
