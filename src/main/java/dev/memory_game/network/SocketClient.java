package dev.memory_game.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {
  private String serverAddress;
  private int serverPort;
  private static final String SECRET_TOKEN = "test_token";

  public SocketClient(String serverAddress, int serverPort) {
    this.serverAddress = serverAddress;
    this.serverPort = serverPort;
  }

  public void start() {
    try (Socket socket = new Socket(serverAddress, serverPort)) {

      // Write the data to the server
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

      // Read data from the server
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      // Send the authorization token
      out.println(SECRET_TOKEN);

      // Wait for server response to authorization
      String serverResponse = in.readLine();

      if ("Unauthorized".equals(serverResponse)) {
        System.out.println("Connection refused by server: Unauthorized");
        return; // Exit if unauthorized
      } else {
        System.out.println("Connected and authorized by server.");
      }

      // Get user input from the terminal
      BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
      String userInputStr;

      // Start a thread to listen for incoming messages
      new Thread(new IncomingMessageHandler(socket, in)).start();

      System.out.println("Connected to server. Type a message to send:");
      while ((userInputStr = userInput.readLine()) != null) {
        if (userInputStr.trim().isEmpty()) {
          continue; // Skip empty input
        }
        // Send message to server
        out.println(userInputStr);
        out.flush();
      }

    } catch (IOException e) {
      System.out.println("Client exception: " + e.getMessage());
    }
  }

  private static class IncomingMessageHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;

    public IncomingMessageHandler(Socket socket, BufferedReader in) {
      this.socket = socket;
      this.in = in;
    }

    public void run() {
      try {
        String message;
        while ((message = in.readLine()) != null) {
          System.out.println("Received from server: " + message);
        }
      } catch (IOException e) {
        System.out.println("Error reading from server: " + e.getMessage());
      }
    }
  }
}
