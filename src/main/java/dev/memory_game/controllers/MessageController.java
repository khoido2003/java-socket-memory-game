package dev.memory_game.controllers;

import dev.memory_game.network.ClientHandler;
import dev.memory_game.network.Room;
import dev.memory_game.network.SocketServer;

public class MessageController {
  private ClientHandler clientHandler;
  private SocketServer socketServer;

  public MessageController(ClientHandler clientHandler, SocketServer socketServer) {
    this.clientHandler = clientHandler;
    this.socketServer = socketServer;

  }

  public void handleMessage(String message) {
    if (message.startsWith("CREATE_ROOM")) {
      createRoom(message);
    } else if (message.startsWith("JOIN_ROOM")) {
      joinRoom(message);
    } else if (message.startsWith("LEAVE_ROOM")) {
      leaveRoom();
    } else if (message.startsWith("SEND_MESSAGE")) {
      handleBroadcast(message);
    }
  }

  ////////////////////////////////////////////////////// S

  private void createRoom(String message) {
    int maxPlayers = Integer.parseInt(message.split(" ")[1]);
    Room room = socketServer.createRoom(maxPlayers);
    if (room.addPlayer(clientHandler)) {
      clientHandler.sendMessage("ROOM_CREATED: " + room.getRoomId());
      clientHandler.setRoom(room);
    }
  }

  private void joinRoom(String message) {
    String roomId = message.split(" ")[1];
    Room room = socketServer.getRoom(roomId);
    if (room != null && room.addPlayer(clientHandler)) {
      clientHandler.sendMessage("JOINED_ROOM: " + roomId);
      clientHandler.setRoom(room);
    } else {
      clientHandler.sendMessage("ROOM_FULL_OR_NOT_FOUND");
    }
  }

  private void leaveRoom() {
    Room room = socketServer.getRoom(clientHandler.getClientID());
    if (room != null) {
      room.removePlayer(clientHandler);
      clientHandler.setRoom(null);
      room.broadcast(clientHandler.getClientID() + " has left the room", clientHandler);
    }
  }

  private void handleBroadcast(String message) {
    Room room = socketServer.getRoom(clientHandler.getClientID());
    String realMessage = message.split(" ")[1];

    if (room != null) {
      room.broadcast(realMessage, clientHandler);
    }
  }
}
