package dev.memory_game.controllers;

import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;

import dev.memory_game.DAO.FriendDAO;
import dev.memory_game.DAO.UserDAO;
import dev.memory_game.models.Friend;
import dev.memory_game.models.User;
import dev.memory_game.network.ClientHandler;
import dev.memory_game.network.SocketServer;
import java.io.BufferedReader;
import java.io.PrintWriter;

public class MessageController {
  private ClientHandler clientHandler;
  private SocketServer socketServer;
  private Connection connection;
  protected PrintWriter out;
  protected BufferedReader in;

  public MessageController(ClientHandler clientHandler, SocketServer socketServer, Connection connection) {
    this.clientHandler = clientHandler;
    this.socketServer = socketServer;
    this.connection = connection;
  }

  public void handleMessage(String message) {

    // Request to get list of friend
    if (message.startsWith("REQUEST_FRIEND_LIST:")) {
      String userId = message.split(" ")[1];
      FriendController friendController = new FriendController(connection);

      List<Friend> friendList = friendController.getFriendListOfCurrentUser(userId);
      List<String> friendListJson = new ArrayList<>();

      for (Friend friend : friendList) {
        if (this.socketServer.getClientSockets().containsKey(friend.getUserId())) {
          friendListJson.add(friend.toJson());

          ClientHandler client = this.socketServer.getClientSockets().get(friend.getUserId());

          // Get the information about the current client
          UserDAO userDAO = new UserDAO(connection);
          User user = userDAO.getUserById(userId);

          // Then nottice their friend that this client online
          client.sendMessage("UPDATE_FRIEND_ONLINE: " + user.toJson());
        }

      }
      this.clientHandler.sendMessage("RESPONSE_FRIEND_LIST: " + "\"" + friendListJson + "\"");
    }
    ;

    if (message.startsWith("REQUEST_FIND_USER_LIST:")) {
      String requestName = message.substring(24);
      requestName.trim();
      UserDAO userDAO = new UserDAO(connection);
      List<User> users = userDAO.findUserByUsername(requestName);

      List<String> usersJson = new ArrayList<>();
      for (User user : users) {
        usersJson.add(user.toJson());
      }
      this.clientHandler.sendMessage("RESPONSE_FIND_USER_LIST: " + "\"" + usersJson + "\"");
    }

    if (message.startsWith("CREATE_FRIEND_REQUEST: ")) {
      String friendID = message.split(" ")[1];

      Friend friend1 = new Friend(this.clientHandler.getClientID(), friendID);
      Friend friend2 = new Friend(friendID, this.clientHandler.getClientID());
      try {
        FriendDAO friendDAO = new FriendDAO(connection);
        friendDAO.addFriend(friend1);
        friendDAO.addFriend(friend2);

        ClientHandler friendHandler = this.socketServer.getClientSockets().get(friendID);

        if (friendHandler != null) {
          UserDAO userDAO = new UserDAO(connection);
          User curUserSendReq = userDAO.getUserById(this.clientHandler.getClientID());
          String curUserSendReqJson = curUserSendReq.toJson();

          System.out.println(curUserSendReqJson);

          if (friendHandler.getClientID() != "") {
            friendHandler.sendMessage("NEW_FRIEND_REQUEST: " + curUserSendReqJson);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    if (message.startsWith("GET_PENDING_FRIEND_LIST:")) {
      String userId = message.split(" ")[1];

      FriendDAO friendDAO = new FriendDAO(connection);

      List<Friend> pendingFriendList = friendDAO.getFriendRequests(userId);

      for (Friend friend : pendingFriendList) {

        System.out.println("friend: " + friend.getUsername() + " " + friend.getFriendId());
      }

      List<String> pendingFriendListJson = new ArrayList<>();
      for (User user : pendingFriendList) {
        pendingFriendListJson.add(user.toJson());
      }
      this.clientHandler.sendMessage("RESPONSE_PENDING_FRIEND_REQUEST_LIST: " + "\"" + pendingFriendListJson + "\"");
    }

    if (message.startsWith("REQUEST_ACCEPT_FRIEND_REQUEST:")) {
      String userId = message.split(" ")[1];
      String friendId = message.split(" ")[2];

      FriendDAO friendDAO = new FriendDAO(connection);
      boolean result1 = friendDAO.acceptFriendRequest(userId, friendId);
      boolean result2 = friendDAO.acceptFriendRequest(friendId, userId);

      if (result1 && result2) {
        UserDAO userDAO = new UserDAO(connection);
        User user = userDAO.getUserById(userId);
        String userJson = user.toJson();

        ClientHandler friendHandler = this.socketServer.getClientSockets().get(friendId);

        System.out.println(userJson);

        friendHandler.sendMessage("RESPONSE_ACCEPT_FRIEND_REQUEST: " + userJson);
      }

    }
  }
}