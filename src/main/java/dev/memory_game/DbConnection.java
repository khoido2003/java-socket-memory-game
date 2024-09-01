package dev.memory_game;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
  private static final String URL = "jdbc:mysql://localhost:3306/memorygame";
  private static final String USER = "root";
  private static final String PASSWORD = "";

  public static Connection getConnection() {
    Connection connection = null;

    try {

      // Load the MySQL JDBC driver (optional for newer versions)
      Class.forName("com.mysql.cj.jdbc.Driver");

      // Establish the connection
      connection = DriverManager.getConnection(URL, USER, PASSWORD);
      System.out.println("Connected to the database.");

    } catch (SQLException e) {
      e.printStackTrace();
      System.out.println("Connection failed: " + e.getMessage());
    } catch (ClassNotFoundException e) {
      System.out.println("MySQL JDBC Driver not found: " + e.getMessage());
    }

    return connection;
  }

}
