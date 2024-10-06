package dev.memory_game.controllers;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import dev.memory_game.DAO.FriendDAO;
import dev.memory_game.models.Friend;

public class FriendController {
  private FriendDAO friendDAO;
  private List<Friend> friends = new ArrayList<Friend>() {
  };

  public FriendController(Connection connection) {
    this.friendDAO = new FriendDAO(connection);
  }

  public List<Friend> getFriendListOfCurrentUser(String userId) {
    try {
      this.friends = this.friendDAO.getFriends(userId);
      return this.friends;
    } catch (Exception e) {
      System.out.println("Error retrieving friend list: " + e.getMessage());
      e.printStackTrace();
      return null;
    }
  }
}
