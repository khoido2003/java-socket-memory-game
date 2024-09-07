package dev.memory_game.network;

import java.io.IOException;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.util.Set;

// import dev.memory_game.utils.JwtUtil;

public class ClientHandler extends Thread {
  private Socket clientSocket;
  private Set<Socket> clientSockets;
  protected PrintWriter out;
  private String clientID;
  private Room room;
  private SocketServer server;

  public ClientHandler(Socket socket, Set<Socket> clientSockets, SocketServer server) {
    this.clientSocket = socket;
    this.clientSockets = clientSockets;
    this.clientID = "ID1001";
    this.server = server;
  }

  public void run() {
    try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

      // Get the PrintWriter object to send messages to the client.
      out = new PrintWriter(clientSocket.getOutputStream(), true);

      ///////////////////////////////////////////////////////////

      // Handle authentication
      // String token = in.readLine();
      // if (!token.equals(SECRET_TOKEN)) {
      // out.println("Unauthorized");
      // clientSocket.close();
      // return;
      // } else {
      // out.println("Authenticated");
      // }

      //////////////////////////////////////////////////////////

      String message;

      while ((message = in.readLine()) != null) {

        System.out.println("Received from client " + clientID + ": " + message);

        // // Echo the message to the user
        // out.println("Echo: " + "Received mess");

        // Send message to all users
        // broadcastMessage(message);

        handleMessage(message);
      }

    } catch (IOException e) {
      System.out.println("Client handler exception: " + e.getMessage());
    } finally {
      cleanUp();
    }
  }

  private void handleMessage(String message) {
    if (message.startsWith("CREATE_ROOM")) {
      // Create a new room
      int maxPlayers = Integer.parseInt(message.split(" ")[1]);

      // Server creates a new room
      Room room = server.createRoom(maxPlayers);

      // add the current player to the room
      if (room.addPlayer(this)) {
        sendMessage("ROOM_CREATED: " + room.getRoomId());
      }
    } else if (message.startsWith("JOIN_ROOM")) {
      // Join an existing room
      String roomId = message.split(" ")[1];
      Room room = server.getRoom(roomId);

      if (room != null && room.addPlayer(this)) {
        sendMessage("JOINED_ROOM: " + roomId);
        room.broadcast(clientID + " has joined the room", this);
      } else {
        sendMessage("ROOM_FULL_OR_NOT_FOUND");
      }
    } else if (message.startsWith("LEAVE_ROOM")) {
      if (room != null) {
        room.removePlayer(this);
        room.broadcast(clientID + " has left the room", this);

        if (room.isFull()) {
          server.removeRoom(room.getRoomId());
        }
        room = null;
      }
    } else {
      if (room != null) {
        room.broadcast(message, this);
      }
    }
  }

  public void sendMessage(String message) {

    // Send to the corresponding player
    out.println(message);
    // Make sure the message is sent
    out.flush();
  }

  // If the user join a room, then use this to set the room to that user
  public void setRoom(Room room) {
    this.room = room;
  }

  //////////////////////////////////////////////////////////////

  // This method will send a message to all the players
  private void broadcastMessage(String message) {
    for (Socket clienSocket : clientSockets) {
      try {
        if (clienSocket != this.clientSocket) {
          PrintWriter clientOut = new PrintWriter(clienSocket.getOutputStream());

          clientOut.println(message);

          clientOut.flush(); // Make sure the message is sent
        }
      } catch (IOException e) {
        System.out.println("Error sending message to client: " + e.getMessage());
      }
    }
  }

  private void cleanUp() {
    try {
      if (clientSocket != null && !clientSocket.isClosed()) {
        clientSocket.close();
      }
      // Remove the client from the list of connected clients
      synchronized (clientSockets) {
        clientSockets.remove(clientSocket);
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      Thread.currentThread().interrupt();
    }
  }
}
