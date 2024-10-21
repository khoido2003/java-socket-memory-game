package dev.memory_game.controllers;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.Queue;

import dev.memory_game.network.ClientHandler;
import dev.memory_game.network.Room;
import dev.memory_game.network.SocketServer;

public class MatchMakingController {

  private Connection connection;
  private ClientHandler clientHandler;
  private SocketServer socketServer;
  private Queue<ClientHandler> waitingPlayers = new LinkedList<>();

  public MatchMakingController(Connection connection, SocketServer socketServer) {
    this.connection = connection;
    this.socketServer = socketServer;
  }

  public void setClientHandler(ClientHandler clientHandler) {
    this.clientHandler = clientHandler;
  }

  public synchronized void joinMatchMaking() {

    logQueueState();

    if (!waitingPlayers.isEmpty()) {
      System.out.println("RUNNING 1");
      ClientHandler opponent = waitingPlayers.poll();

      Room room = this.socketServer.createRoom(2);
      room.addPlayer(clientHandler);
      room.addPlayer(opponent);

      room.broadcast("RESPONSE_JOIN_NEW_MATCH: " + room.getRoomId(), null);

    } else {
      System.out.println("RUNNING 2");

      waitingPlayers.add(clientHandler);
      System.out.println(clientHandler.getClientID() + " is waiting to join a match.");
    }
  }

  public synchronized void leaveMatchMaking(ClientHandler curPlayer) {
    waitingPlayers.remove(curPlayer);
    System.out.println(curPlayer.getClientID() + " left the match making queue.");
  }

  public synchronized void logQueueState() {
    System.out.println("Current Waiting Queue: ");
    for (ClientHandler player : waitingPlayers) {
      System.out.println(player.getClientID());
    }
  }

}
