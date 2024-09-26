package dev.memory_game.utils;

import java.util.HashSet;
import java.util.Set;

import dev.memory_game.models.User;

public class UserJsonUtil {

  // Serialize a list of User objects into a JSON array string
  public static String usersToJsonArray(Set<User> users) {
    StringBuilder jsonArray = new StringBuilder("[");

    for (User user : users) {
      jsonArray.append(user.toJson()).append(",");
    }

    // Remove the last comma and close the array
    if (jsonArray.length() > 1) {
      jsonArray.setLength(jsonArray.length() - 1);
    }

    jsonArray.append("]");
    return jsonArray.toString();
  }

  // Deserialize a JSON array string into a list of User objects
  public static Set<User> jsonArrayToUsers(String jsonArray) {
    Set<User> users = new HashSet<>();

    // Remove the square brackets and split by curly braces
    jsonArray = jsonArray.substring(1, jsonArray.length() - 1); // remove '[' and ']'
    String[] userStrings = jsonArray.split("(?<=\\}),");

    // Parse each User object from the JSON string
    for (String userString : userStrings) {
      users.add(User.fromJson(userString));
    }

    return users;
  }
}
