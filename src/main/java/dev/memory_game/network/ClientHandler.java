package dev.memory_game.network;

import java.io.IOException;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.util.Set;

public class ClientHandler extends Thread {
  private Socket clientSocket;
  private Set<Socket> clientSockets;
  protected PrintWriter out;
  private String clientID;

  public ClientHandler(Socket socket, Set<Socket> clientSockets) {
    this.clientSocket = socket;
    this.clientSockets = clientSockets;
    this.clientID = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();

  }

  public void run() {
    try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

      // Get the PrintWriter object to send messages to the client.
      out = new PrintWriter(clientSocket.getOutputStream(), true);

      String message;

      while ((message = in.readLine()) != null) {

        System.out.println("Received from client " + clientID + ": " + message);

        // // Echo the message to the user
        // out.println("Echo: " + "Received mess");

        // Send message to other user
        broadcastMessage(message);
      }

    } catch (IOException e) {
      System.out.println("Client handler exception: " + e.getMessage());
    } finally {
      try {

        // Close the socket
        clientSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

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
}
