package dev.memory_game.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {
  private int port;
  private Set<Socket> clientSockets = new HashSet<>();
  private Map<String, Room> rooms = new HashMap<>();

  // Setup a thread pool to handle connections
  private ExecutorService threadPool;

  public SocketServer(
      int port) {
    this.port = port;
    this.threadPool = Executors.newFixedThreadPool(16);
  }

  public void start() {
    // Start the socket server here

    try (ServerSocket serverSocket = new ServerSocket(port)) {

      while (true) {

        Socket clientSocket = serverSocket.accept();

        // Add the client socket to the list of user connected.
        clientSockets.add(clientSocket);

        System.out.println("New client connected: " + clientSocket.getInetAddress());

        // Create a separate thread for each client connection
        // new ClientHandler(clientSocket, clientSockets).start();
        threadPool.submit(new ClientHandler(clientSocket, clientSockets, this));

      }
    } catch (IOException e) {
      System.out.println("Server exception: " + e.getMessage());

      e.printStackTrace();
    }
  }

  public Room createRoom(int maxPlayers) {
    String roomId = "Room-" + (rooms.size() + 1);
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

}
