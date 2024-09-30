package dev.memory_game.controllers;

import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;

import dev.memory_game.DAO.UserDAO;
import dev.memory_game.models.Friend;
import dev.memory_game.models.User;
import dev.memory_game.network.ClientHandler;
import dev.memory_game.network.SocketServer;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

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

      System.out.println(friendListJson);
      this.clientHandler.sendMessage("RESPONSE_FRIEND_LIST: " + "\"" + friendListJson + "\"");
    }
    ;
  }
}