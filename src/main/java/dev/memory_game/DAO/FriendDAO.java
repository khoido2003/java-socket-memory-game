package dev.memory_game.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dev.memory_game.models.Friend;

public class FriendDAO {
  private Connection connection;

  public FriendDAO(Connection connection) {
    this.connection = connection;
  }

  // Add friend
  public void addFriend(Friend friend) throws SQLException {
    String sql = "INSERT INTO Friends(user_id, friend_id, status, created_at) VALUES (?, ?, ?, ?)";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, friend.getUserId());
      pstmt.setString(2, friend.getFriendId());
      pstmt.setString(3, "pending");
      pstmt.setTimestamp(4, friend.getCreatedAt());
      pstmt.executeUpdate();
    }
  }

  // Remove friend
  public void removeFriend(String userId, String friendId) throws SQLException {
    String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, userId);
      pstmt.setString(2, friendId);
      pstmt.executeUpdate();
    }
  }

  // Update friend status
  public void updateFriendStatus(String userId, String friendId, String status)
      throws SQLException {
    String sql = "UPDATE friends SET status = ? WHERE user_id = ? AND friend_id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, status);
      pstmt.setString(2, userId);
      pstmt.setString(3, friendId);
      pstmt.executeUpdate();
    }
  }

  // Method to get list friends of a user
  public List<Friend> getFriends(String userId) throws SQLException {
    List<Friend> friends = new ArrayList<Friend>();

    String sql = "SELECT u.*, f.* FROM friends f JOIN users u ON f.friend_id = u.user_id WHERE f.user_id =? AND f.status = 'accepted'";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, userId);

      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {

          Friend friend = new Friend();
          friend.setUserId(rs.getString("user_id"));
          friend.setFriendId(rs.getString("friend_id"));
          friend.setUsername(rs.getString("username"));
          friend.setTotalPoints(rs.getInt("total_points"));
          friend.setEmail(rs.getString("email"));
          friend.setCreatedAt(rs.getTimestamp("created_at"));

          friends.add(friend);
        }
      }
    }
    return friends;
  }

  // Method to get friend requests of a user
  public List<Friend> getFriendRequests(String userId) {
    List<Friend> friendRequests = new ArrayList<>();

    String sql = "SELECT u.*, f.* FROM friends f JOIN users u ON f.friend_id = u.user_id WHERE f.user_id =? AND f.status = 'pending'";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, userId);

      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {

          Friend friend = new Friend();

          friend.setUserId(rs.getString("user_id"));
          friend.setFriendId(rs.getString("friend_id"));
          friend.setUsername(rs.getString("username"));
          friend.setTotalPoints(rs.getInt("total_points"));
          friend.setEmail(rs.getString("email"));
          friendRequests.add(friend);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return friendRequests;
  }

  // Check if two users are friends
  public boolean areFriends(String userId, String friendId) throws SQLException {
    String sql = "SELECT COUNT(*) FROM friends WHERE user_id = ? AND friend_id = ? AND status = 'accepted'";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, userId);
      pstmt.setString(2, friendId);

      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return rs.getInt(1) > 0;
        }
      }
    }
    return false;
  }

  public boolean acceptFriendRequest(String userId, String friendId) {
    try {
      String sql = "UPDATE friends SET status = 'accepted' WHERE user_id = ? AND friend_id = ? AND status = 'pending'";

      PreparedStatement pstmt = connection.prepareStatement(sql);
      pstmt.setString(1, userId);
      pstmt.setString(2, friendId);
      int rowsUpdated = pstmt.executeUpdate();

      return rowsUpdated > 0;

    } catch (Exception e) {

      e.printStackTrace();
      return false;
    }

  }
}
