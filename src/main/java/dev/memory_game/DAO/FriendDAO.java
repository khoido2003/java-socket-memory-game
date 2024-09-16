package dev.memory_game.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import dev.memory_game.models.Friend;
import dev.memory_game.models.User;

public class FriendDAO {
  private Connection connection;

  public FriendDAO(Connection connection) {
    this.connection = connection;
  }

  // Add friend
  public void addFriend(Connection connection, Friend friend) throws SQLException {
    String sql = "INSERT INTO Friends(user_id, friend_id, status, created_at) VALUES (?, ?, ?, ?)";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, friend.getUserId());
      pstmt.setString(2, friend.getFriendId());
      pstmt.setString(3, friend.getStatus().getStatus());
      pstmt.setTimestamp(4, friend.getCreatedAt());
      pstmt.executeUpdate();
    }
  }

  // Remove friend
  public void removeFriend(Connection connection, String userId, String friendId) throws SQLException {
    String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, userId);
      pstmt.setString(2, friendId);
      pstmt.executeUpdate();
    }
  }

  // Update friend status
  public void updateFriendStatus(Connection connection, String userId, String friendId, Friend.Status status)
      throws SQLException {
    String sql = "UPDATE friends SET status = ? WHERE user_id = ? AND friend_id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, status.getStatus());
      pstmt.setString(2, userId);
      pstmt.setString(3, friendId);
      pstmt.executeUpdate();
    }
  }

  // Method to get list friends of a user
  public Set<Friend> getFriends(Connection connection, String userId) throws SQLException {
    Set<Friend> friends = new HashSet<>();

    String sql = "SELECT u.*, f.* FROM friends f JOIN users u ON f.friend_id = u.user_id WHERE f.user_id =? AND f.status = 'accepted'";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, userId);

      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {

          User friendUser = new User();
          friendUser.setUserId(rs.getString("friend_id"));
          friendUser.setUsername(rs.getString("username"));
          friendUser.setEmail(rs.getString("email"));
          friendUser.setTotalPoints(rs.getInt("total_points"));

          Friend friend = new Friend();

          friend.setUserId(rs.getString("user_id"));
          friend.setFriendId(rs.getString("friend_id"));
          friends.add(friend);
        }
      }
    }
    return friends;
  }

  // Method to get friend requests of a user
  public Set<Friend> getFriendRequests(Connection connection, String userId) throws SQLException {
    Set<Friend> friendRequests = new HashSet<>();

    String sql = "SELECT u.*, f.* FROM friends f JOIN users u ON f.friend_id = u.user_id WHERE f.user_id =? AND f.status = 'pending'";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, userId);

      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {

          User friendUser = new User();
          friendUser.setUserId(rs.getString("friend_id"));
          friendUser.setUsername(rs.getString("username"));
          friendUser.setEmail(rs.getString("email"));
          friendUser.setTotalPoints(rs.getInt("total_points"));

          Friend friend = new Friend();

          friend.setUserId(rs.getString("user_id"));
          friend.setFriendId(rs.getString("friend_id"));
          friend.setStatus(Friend.Status.valueOf(rs.getString("status")));

          friendRequests.add(friend);
        }

      }

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

}