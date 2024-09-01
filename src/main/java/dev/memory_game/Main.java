package dev.memory_game;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {

  @SuppressWarnings("null")
  public static void main(String[] args) {

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

  }
}