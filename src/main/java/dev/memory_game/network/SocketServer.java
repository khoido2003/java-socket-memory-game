package dev.memory_game.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class SocketServer {
  private int port;
  private Set<Socket> clientSockets = new HashSet<>();

  public SocketServer(
      int port) {
    this.port = port;
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
        new ClientHandler(clientSocket, clientSockets).start();

      }
    } catch (IOException e) {
      System.out.println("Server exception: " + e.getMessage());

      e.printStackTrace();
    }
  }

}
