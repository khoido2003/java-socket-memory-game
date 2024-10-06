package dev.memory_game.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class SocketServer {
  private int port;
  private Connection connection;
  private Map<String, ClientHandler> clientSockets = new HashMap<>();
  private Map<String, Room> rooms = new HashMap<>();

  // Setup a thread pool to handle connections
  private final ExecutorService threadPool;

  public SocketServer(int port, ExecutorService threadPool, Connection connection) {
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

          System.out.println("New client connected: " + clientSocket.getInetAddress());

          // Create a separate thread for each client connection
          // NEVER use this way since it can overwhelm the system:

          // new ClientHandler(clientSocket, clientSockets).start();

          // Instead of init a new thread for each client connection, instead using
          // excutorService to control the thread for each client connection
          ClientHandler clientHandler = new ClientHandler(clientSocket, clientSockets, this, connection);
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

  public Map<String, ClientHandler> getClientSockets() {
    return clientSockets;
  }

  //////////////////////////////////////////////////////////////////////

  public Connection getConnection() {
    return connection;
  }

  ////////////////////////////////////////////////////////////////////

  // Control rooms
  public Room getRoom(String roomId) {
    return rooms.get(roomId);
  }

  public Room createRoom(int maxPlayers) {
    String id = UUID.randomUUID().toString();
    String roomId = "Room-" + id;
    Room room = new Room(roomId, maxPlayers);

    rooms.put(roomId, room);
    return room;
  }

  public void removeRoom(String roomId) {
    rooms.remove(roomId);
  }
}
