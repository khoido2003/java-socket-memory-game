package dev.memory_game.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {
  private String serverAddress;
  private int serverPort;

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

      // Get user input from the terminal
      BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

      String userInputStr;
      System.out.println("Connected to server. Type a message to send:");

      // Start a thread to listen for incoming messages
      new Thread(new IncomingMessageHandler(socket)).start();

      while ((userInputStr = userInput.readLine()) != null) {

        // Send message to server
        out.println(userInputStr);
        out.flush();

        // Wait for response from server
        String response = in.readLine();
        System.out.println("Server response: " + response);
      }

    } catch (IOException e) {
      System.out.println("Client exception: " + e.getMessage());
    }
  }

  private static class IncomingMessageHandler implements Runnable {
    private Socket socket;

    public IncomingMessageHandler(Socket socket) {
      this.socket = socket;
    }

    public void run() {

      try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
        String message;
        while ((message = in.readLine()) != null) {
          System.out.println("Received from client: " + message);

        }
      } catch (IOException e) {
        System.out.println("Error reading from client: " + e.getMessage());
      }

    }

  }

}
