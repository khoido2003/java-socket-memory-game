package dev.memory_game.network;

import java.io.IOException;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import dev.memory_game.DAO.FriendDAO;
import dev.memory_game.DAO.UserDAO;
import dev.memory_game.controllers.MessageController;
import dev.memory_game.models.Friend;
import dev.memory_game.models.JwtToken;
import dev.memory_game.models.User;
import dev.memory_game.utils.JwtUtil;
import dev.memory_game.utils.UserJsonUtil;

// import dev.memory_game.utils.JwtUtil;

public class ClientHandler extends Thread {
  private Socket clientSocket;
  private Set<Socket> clientSockets;
  protected PrintWriter out;
  private String clientID;
  private SocketServer server;
  private MessageController messageController;
  private Room room;

  public ClientHandler(Socket socket, Set<Socket> clientSockets, SocketServer server) {
    this.clientSocket = socket;
    this.clientSockets = clientSockets;
    this.clientID = "";
    this.server = server;
    this.messageController = new MessageController(this, this.server);
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

        System.out.println(this.clientID);

        // After adding the user to the online list, send them the list of online
        // friends
        sendOnlineFriendsList(token.getUserId());
      }

      ///////////////////////////////////////////////////////////////

      // Process the request

      String message;
      while ((message = in.readLine()) != null) {

        System.out.println("Received from client " + clientID + ": " + message);

        messageController.handleMessage(message);
      }

    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Client disconnected: " + e.getMessage());
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

      Set<User> onlineFriends = new HashSet<>();

      for (Friend friend : friends) {
        if (server.isUserOnline(friend.getFriendId())) {

          UserDAO userDao = new UserDAO(server.getConnection());
          User user = userDao.getUserById(friend.getFriendId());

          friend.setFriendUser(user);
          onlineFriends.add(user);
        }
      }

      String jsonOnlineFriends = UserJsonUtil.usersToJsonArray(onlineFriends);

      sendMessage("ONLINE_FRIENDS:" + jsonOnlineFriends);

    } catch (Exception e) {
      System.out.println("Error sending online friends list: " + e.getMessage());
      e.printStackTrace();
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
    }
  }

  public String getClientID() {
    return clientID;
  }
}
