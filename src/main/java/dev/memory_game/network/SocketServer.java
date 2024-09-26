package dev.memory_game.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import dev.memory_game.DAO.FriendDAO;
import dev.memory_game.DAO.UserDAO;
import dev.memory_game.models.Friend;
import dev.memory_game.models.User;

import java.util.concurrent.ConcurrentHashMap;

public class SocketServer {
  private Connection connection;
  private int port;
  private Set<Socket> clientSockets = new HashSet<>();
  private Map<String, Room> rooms = new HashMap<>();

  private Map<String, ClientHandler> onlineUsers = new ConcurrentHashMap<>();
  private Map<String, User> onlineUsersInfo = new HashMap<>();

  // Setup a thread pool to handle connections
  private final ExecutorService threadPool;

  public SocketServer(
      int port, ExecutorService threadPool, Connection connection) {
    this.port = port;
    this.connection = connection;
    this.threadPool = threadPool;
  }

  public void start() {
    // Start the socket server here

    try (ServerSocket serverSocket = new ServerSocket(port)) {

      while (true) {
        try {
          // Accept socket connection from the client
          Socket clientSocket = serverSocket.accept();

          // Add the client socket to the list of user connected.
          clientSockets.add(clientSocket);

          System.out.println("New client connected: " + clientSocket.getInetAddress());

          // Create a separate thread for each client connection
          // NEVER use this way since it can overwhelm the system:

          // new ClientHandler(clientSocket, clientSockets).start();

          // Instead of init a new thread for each client connection, instead using
          // excutorService to control the thread for each client connection
          ClientHandler clientHandler = new ClientHandler(clientSocket, clientSockets, this);
          threadPool.submit(() -> clientHandler.start());

        } catch (Exception e) {
          e.printStackTrace();
          System.out.println("Error connection: " + e.getMessage());
        }

      }
    } catch (IOException e) {
      System.out.println("Server exception: " + e.getMessage());

      e.printStackTrace();
    }
  }

  ////////////////////////////////////////////////////////////////////

  // Control users online status

  public void addUserOnline(String userId, ClientHandler client) {
    onlineUsers.put(userId, client);
    UserDAO userDAO = new UserDAO(connection);
    User user = userDAO.getUserById(userId);

    if (user != null) {
      onlineUsersInfo.put(userId, user);
    } else {
      System.out.println("User not found: " + userId);
    }

    notifyUserStatusChange(userId, true);
  }

  public void removeUserOffline(String userId, ClientHandler client) {
    System.out.println("Remove " + userId);
    onlineUsers.remove(userId);
    client.setRoom(null); // Remove the client from room when they disconnect

    onlineUsersInfo.remove(userId);
    notifyUserStatusChange(userId, false);
  }

  // Trigger when user connect or disconnect (realtime update)
  private void notifyUserStatusChange(String userId, boolean isOnline) {
    FriendDAO friendDAO = new FriendDAO(this.connection);

    Set<Friend> friends = null;

    try {
      friends = friendDAO.getFriends(connection, userId);

    } catch (Exception e) {
      System.out.println("Error notifying user status change: " + e.getMessage());
      e.printStackTrace();
    }

    if (friends != null) {

      for (Friend friend : friends) {
        ClientHandler friendHandler = onlineUsers.get(friend.getFriendId());

        if (friendHandler != null) {
          String statusMessage = "FRIEND_STATUS_CHANGE:" + userId + ":" + (isOnline ? "ONLINE" : "OFFLINE");
          friendHandler.sendMessage(statusMessage);
        }
      }
    }
  }

  public boolean isUserOnline(String userId) {
    return onlineUsers.containsKey(userId);
  }

  /////////////////////////////////////////////////////////////////

  // Control rooms

  public Room createRoom(int maxPlayers) {

    String id = UUID.randomUUID().toString();

    String roomId = "Room-" + id;
    Room room = new Room(roomId, maxPlayers);

    rooms.put(roomId, room);

    return room;
  }

  public Room getRoom(String roomId) {
    return rooms.get(roomId);
  }

  public void removeRoom(String roomId) {
    rooms.remove(roomId);
  }

  public Connection getConnection() {
    return connection;
  }
}
