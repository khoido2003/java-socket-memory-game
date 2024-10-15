package dev.memory_game.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dev.memory_game.models.User;
import dev.memory_game.utils.BcryptHash;

public class UserDAO {

  private Connection connection;
  // private List<User> users = new ArrayList<User>();

  public UserDAO(Connection connection) {
    this.connection = connection;
  }

  // Create a user for testing purposes in dev mode with POST /users - in
  // production, use /sign up
  // route instead
  public boolean createUser(User user) {

    String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

    // Hash password before creating user
    String hashedPassword = BcryptHash.hashPassword(user.getPassword());

    try (PreparedStatement statement = connection.prepareStatement(sql)) {

      statement.setString(1, user.getUsername());
      statement.setString(2, hashedPassword);
      statement.setString(3, user.getEmail());
      // statement.setInt(4, user.getTotalPoints());
      // statement.setString(5, user.getStatus());

      int rowInserted = statement.executeUpdate();
      return rowInserted > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /////////////////////////////////////////////
  // Get all users
  public List<User> getAllUsers() {
    List<User> users = new ArrayList<User>();

    String sql = "SELECT * FROM users";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      ResultSet resultSet = statement.executeQuery();

      while (resultSet.next()) {

        // Create a new user object from the result set and add it to the list.
        User user = new User(
            resultSet.getString("user_id"),
            resultSet.getString("username"),
            resultSet.getString("password"),
            resultSet.getString("email"),
            resultSet.getInt("total_points"));

        users.add(user);

      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return users;
  }

  ///////////////////////////////////////////////////

  // Get a user by their ID
  public User getUserById(String userId) {
    String sql = "SELECT * FROM users WHERE user_id =?";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {

      statement.setString(1, userId);
      ResultSet resultSet = statement.executeQuery();

      if (resultSet.next()) {
        return new User(
            resultSet.getString("user_id"),
            resultSet.getString("username"),
            resultSet.getString("password"),
            resultSet.getString("email"),
            resultSet.getInt("total_points"));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public List<User> findUserByUsername(String username) {
    String sql = "SELECT * FROM users WHERE username LIKE ?";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, "%" + username + "%");
      ResultSet resultSet = statement.executeQuery();

      List<User> users = new ArrayList<>();
      while (resultSet.next()) {
        users.add(new User(
            resultSet.getString("user_id"),
            resultSet.getString("username"),
            resultSet.getString("password"),
            resultSet.getString("email"),
            resultSet.getInt("total_points")));
      }
      return users;

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  // Update a user
  public boolean updateUser(User user) {
    String sql = "UPDATE users SET username=?, password=?, email=?, total_points=? WHERE user_id=?";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, user.getUsername());
      statement.setString(2, user.getPassword());
      statement.setString(3, user.getEmail());
      statement.setInt(4, user.getTotalPoints());
      statement.setString(5, user.getUserId());
      int rowUpdated = statement.executeUpdate();
      return rowUpdated > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }

  }

  // Delete a user
  public boolean deleteUser(String userId) {
    String sql = "DELETE FROM Users WHERE user_id = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, userId);
      int rowsDeleted = statement.executeUpdate();
      return rowsDeleted > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // Get user by email
  public User getUserByEmail(String email) {
    String sql = "SELECT * FROM users WHERE email = ?";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {

      statement.setString(1, email);

      ResultSet resultSet = statement.executeQuery();

      if (resultSet.next()) {
        return new User(
            resultSet.getString("user_id"),
            resultSet.getString("username"),
            resultSet.getString("password"),
            resultSet.getString("email"),
            resultSet.getInt("total_points"));
      }

    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
    return null;
  }

  public boolean updateUserTotalPoint(String userID, int newPoint) {
    String getSql = "SELECT total_points FROM users WHERE user_id = ?";
    String updateSql = "UPDATE users SET total_points = ? WHERE user_id = ?";
    PreparedStatement getPs = null;
    PreparedStatement updatePs = null;
    ResultSet rs = null;

    try {
      // Step 1: Retrieve the current total points for the user
      getPs = connection.prepareStatement(getSql);
      getPs.setString(1, userID);
      rs = getPs.executeQuery();

      if (rs.next()) {
        int currentTotalPoint = rs.getInt("total_points");

        // Step 2: Calculate the updated total points
        int updatedTotalPoint = currentTotalPoint + newPoint;

        // Step 3: Ensure total points do not drop below 0
        if (updatedTotalPoint < 0) {
          updatedTotalPoint = 0; // Set total points to 0 if negative
        }

        // Step 4: Update the total points in the database
        updatePs = connection.prepareStatement(updateSql);
        updatePs.setInt(1, updatedTotalPoint);
        updatePs.setString(2, userID);

        int rowUpdated = updatePs.executeUpdate();
        return rowUpdated > 0; // Return true if the update was successful
      }

      // If userID is not found, return false
      return false;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    } finally {
      // Clean up resources
      try {
        if (rs != null)
          rs.close();
        if (getPs != null)
          getPs.close();
        if (updatePs != null)
          updatePs.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

}
