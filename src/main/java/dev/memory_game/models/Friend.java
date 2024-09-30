package dev.memory_game.models;

import java.sql.Timestamp;

public class Friend extends User {
  private String userId;
  private String friendId;
  private Timestamp createdAt;

  public Friend() {
  }

  // Parameterized constructor
  public Friend(String userId, String friendId, Timestamp createdAt, User friendUser) {
    this.userId = userId;
    this.friendId = friendId;
    this.createdAt = createdAt;
  }

  @Override
  public String toJson() {
    return "{" +
        "\"userId\":\"" + friendId + "\"," +
        "\"email\":\"" + this.getEmail() + "\"," +
        "\"username\":\"" + this.getUsername() + "\"," +
        "\"totalPoints\":" + this.getTotalPoints() +
        "}";
  }

  // Getters and Setters
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getFriendId() {
    return friendId;
  }

  public void setFriendId(String friendId) {
    this.friendId = friendId;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Timestamp createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public String toString() {
    return "Friend{" +
        "userId='" + userId + '\'' +
        ", friendId='" + friendId + '\'' +
        ", createdAt=" + createdAt +
        +'}';
  }
}
