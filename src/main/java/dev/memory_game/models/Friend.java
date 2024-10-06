package dev.memory_game.models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Friend extends User {
  private String userId;
  private String friendId;
  private Timestamp createdAt;

  public Friend() {
  }

  // Parameterized constructor
  public Friend(String userId, String friendId) {
    this.userId = userId;
    this.friendId = friendId;
    this.createdAt = new Timestamp(System.currentTimeMillis());
  }

  @Override
  public String toJson() {
    return "{" +
        "\"userId\":\"" + userId + "\"," +
        "\"friendId\":\"" + friendId + "\"," +
        "\"createdAt\":\"" + createdAt + "\"," +
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
        '}';
  }

  // Method to de-serialize JSON string into Friend object
  public static Friend dejsonlizeObject(String jsonString) {
    jsonString = jsonString.replace("{", "").replace("}", "");
    String[] pairs = jsonString.split(",");

    String userId = null, friendId = null, email = null, username = null;
    Timestamp createdAt = null;
    int totalPoints = 0;

    for (String pair : pairs) {
      String[] keyValue = pair.split(":");
      String key = keyValue[0].trim().replace("\"", "");
      String value = keyValue[1].trim().replace("\"", "");

      switch (key) {
        case "userId":
          userId = value;
          break;
        case "friendId":
          friendId = value;
          break;
        case "email":
          email = value;
          break;
        case "username":
          username = value;
          break;
        case "totalPoints":
          totalPoints = Integer.parseInt(value);
          break;
        case "createdAt":
          createdAt = Timestamp.valueOf(value);
          break;
      }
    }
    // Create a new Friend object from the parsed values
    Friend friend = new Friend(userId, friendId);
    friend.setEmail(email);
    friend.setUsername(username);
    friend.setTotalPoints(totalPoints);
    friend.setCreatedAt(createdAt);
    return friend;
  }

  // Method to de-serialize JSON array into list of Friend objects
  public static List<Friend> dejsonlizeArray(String json) {
    List<Friend> friends = new ArrayList<>();

    if (json.equals("[]")) {
      return friends;
    }

    json = json.trim();
    json = json.substring(1, json.length() - 2);

    String[] objects = json.split("}, ");
    for (int i = 0; i < objects.length; i++) {
      if (!objects[i].endsWith("}")) {
        objects[i] = objects[i] + "}";
      }
      Friend friend = dejsonlizeObject(objects[i]);
      friends.add(friend);
    }
    return friends;
  }
}
