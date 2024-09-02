package dev.memory_game;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dev.memory_game.network.SocketServer;
import dev.memory_game.network.SocketClient;

public class Main {

  public static void main(String[] args) {
    // Start the database connection
    Connection connection = DbConnection.getConnection();
    ResultSet resultSet = null;
    Statement statement = null;

    if (connection != null) {
      try {
        statement = connection.createStatement();
        String query = "SELECT * FROM users"; // Replace with your actual table name
        resultSet = statement.executeQuery(query);
        // Process the results
        while (resultSet.next()) {
          // Assuming your table has columns 'id' and 'name'
          String id = resultSet.getString("id");
          String name = resultSet.getString("name");
          System.out.println("ID: " + id + ", Name: " + name);
        }
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println("SQL error: " + e.getMessage());
      } finally {
        // Close resources
        try {
          if (resultSet != null)
            resultSet.close();
          if (statement != null)
            statement.close();
          if (connection != null)
            connection.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    } else {
      System.out.println("Connection failed");
    }

    // Start the socket server
    int port = 8080; // Choose your desired port
    SocketServer server = new SocketServer(port);
    new Thread(() -> server.start()).start();

    // // Start the socket client
    SocketClient client = new SocketClient("127.0.0.1", port);
    new Thread(() -> client.start()).start();
  }
}
