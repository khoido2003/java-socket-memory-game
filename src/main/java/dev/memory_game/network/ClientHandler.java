package dev.memory_game.network;

import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.util.Map;

import dev.memory_game.controllers.MessageController;
import dev.memory_game.models.JwtToken;
import dev.memory_game.utils.JwtUtil;

// import dev.memory_game.utils.JwtUtil;

public class ClientHandler extends Thread {
  protected PrintWriter out;
  protected BufferedReader in;

  private Connection connection;
  private Socket clientSocket;
  private Map<String, ClientHandler> clientSockets;
  private String clientID;
  private SocketServer server;
  private MessageController messageController;

  public ClientHandler(Socket clientSocket, Map<String, ClientHandler> clientSockets, SocketServer server,
      Connection connection) {
    this.clientSocket = clientSocket;
    this.clientSockets = clientSockets;
    this.clientID = "";
    this.server = server;
    this.connection = connection;
    this.messageController = new MessageController(this, this.server, connection,
        this.server.getMatchMakingController());
  }

  @Override
  public void run() {
    try {

      // Io socket
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
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

        // Store the userID
        this.clientID = token.getUserId();

        // Add clientHandler to the list of online users
        clientSockets.put(clientID, this);

        System.out.println("Client ID: " + this.clientID);
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

  public String getClientID() {
    return clientID;
  }

  public void sendMessage(String message) {
    out.println(message);
    out.flush();
  }

  //////////////////////////////////////////////////////////////

  // This method will send a message to all the players
  // private void broadcastMessage(String message) {
  // for (Socket clienSocket : clientSockets) {
  // try {
  // if (clienSocket != this.clientSocket) {
  // PrintWriter clientOut = new PrintWriter(clienSocket.getOutputStream());

  // clientOut.println(message);
  // clientOut.flush();
  // }
  // } catch (IOException e) {
  // System.out.println("Error sending message to client: " + e.getMessage());
  // }
  // }
  // }

  private void cleanUp() {
    try {
      if (clientSocket != null && !clientSocket.isClosed()) {
        clientSocket.close();
      }
      // Remove the client from the list of connected clients
      synchronized (clientSockets) {
        clientSockets.remove(this.clientID);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
