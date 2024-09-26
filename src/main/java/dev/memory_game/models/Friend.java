package dev.memory_game.models;

import java.sql.Timestamp;

public class Friend {
  private String userId;
  private String friendId;
  private Status status;
  private Timestamp createdAt;
  private User friendUser;

  // Enum for friendship status
  public enum Status {
    PENDING("pending"),
    ACCEPTED("accepted"),
    REJECTED("rejected");

    private final String status;

    Status(String status) {
      this.status = status;
    }

    public String getStatus() {
      return status;
    }

    public static Status fromString(String status) {
      for (Status s : Status.values()) {
        if (s.status.equalsIgnoreCase(status)) {
          return s;
        }
      }
      throw new IllegalArgumentException("Unknown status: " + status);
    }
  }

  // Default constructor
  public Friend() {
  }

  // Parameterized constructor
  public Friend(String userId, String friendId, Status status, Timestamp createdAt, User friendUser) {
    this.userId = userId;
    this.friendId = friendId;
    this.status = status;
    this.createdAt = createdAt;
    this.friendUser = friendUser; // Set friend's user details
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

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Timestamp createdAt) {
    this.createdAt = createdAt;
  }

  public User getFriendUser() {
    return friendUser;
  }

  public void setFriendUser(User friendUser) {
    this.friendUser = friendUser;
  }

  @Override
  public String toString() {
    return "Friend{" +
        "userId='" + userId + '\'' +
        ", friendId='" + friendId + '\'' +
        ", status=" + status +
        ", createdAt=" + createdAt +
        ", friendUser=" + friendUser +
        '}';
  }
}
