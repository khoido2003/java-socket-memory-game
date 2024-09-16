package dev.memory_game.network;

import java.io.IOException;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.util.Set;

import dev.memory_game.DAO.FriendDAO;
import dev.memory_game.models.Friend;
import dev.memory_game.models.JwtToken;
import dev.memory_game.utils.JwtUtil;

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
    this.clientID = "";
    this.server = server;
  }

  @Override
  public void run() {
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      // Get the PrintWriter object to send messages to the client.
      out = new PrintWriter(clientSocket.getOutputStream(), true);

      ////////////////////////////////////////////////////////////////

      // Read the first message (the JWT token)
      String jwtToken = in.readLine();

      // Before processing the request, check the jwt token if it is valid
      boolean result = JwtUtil.isValidToken(jwtToken);

      if (!result) {
        out.println("Unauthorized! Your jwt token is invalid.");
        clientSocket.close();
        return;
      } else {
        out.println("Authorized");

        // Decode the token to get the user ID and store that to the list of online
        // users
        JwtToken token = JwtUtil.decodeToken(jwtToken);

        // Store the online users
        server.addUserOnline(token.getUserId(), this);

        // Store the userID
        this.clientID = token.getUserId();

        // After adding the user to the online list, send them the list of online
        // friends
        sendOnlineFriendsList(token.getUserId());

      }

      ///////////////////////////////////////////////////////////////

      // Process the request

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
      System.out.println("Client disconnected: " + e.getMessage());
      cleanUp();
    } finally {
      cleanUp();
    }
  }

  //////////////////////////////////////////////////////////////////

  // Send friend status online or offline in the first time connect

  private void sendOnlineFriendsList(String userId) {
    FriendDAO friendDAO = new FriendDAO(server.getConnection());

    try {
      // Get all friends of the current user
      Set<Friend> friends = friendDAO.getFriends(server.getConnection(), userId);

      StringBuilder onlineFriends = new StringBuilder("ONLINE_FRIENDS:");

      for (Friend friend : friends) {
        if (server.isUserOnline(friend.getFriendId())) {
          onlineFriends.append(friend.getFriendId()).append(",");
        }
      }

      // Remove the last comma and send the list to the user
      if (onlineFriends.length() > 14) { // "ONLINE_FRIENDS:" has 14 characters
        onlineFriends.setLength(onlineFriends.length() - 1); // remove the trailing comma
        sendMessage(onlineFriends.toString());
      } else {
        sendMessage("ONLINE_FRIENDS:None");
      }

    } catch (Exception e) {
      System.out.println("Error sending online friends list: " + e.getMessage());
      e.printStackTrace();
    }
  }

  ////////////////////////////////////////////////////////////

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
      server.removeUserOffline(clientID, this);

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      Thread.currentThread().interrupt();
    }
  }
}
