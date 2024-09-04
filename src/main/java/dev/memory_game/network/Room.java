package dev.memory_game.network;

import java.util.HashSet;
import java.util.Set;

public class Room {
  private String roomId;
  private Set<ClientHandler> players = new HashSet<>();
  private int maxPlayers;

  public Room(String roomId, int maxPlayers) {
    this.roomId = roomId;
    this.maxPlayers = maxPlayers;
  }

  public synchronized boolean addPlayer(ClientHandler player) {
    if (players.size() < maxPlayers) {
      players.add(player);
      player.setRoom(this);

      return true;
    }
    return false;
  }

  public void removePlayer(ClientHandler player) {
    players.remove(player);
    player.setRoom(null);
  }

  public void broadcast(String message, ClientHandler sender) {
    for (ClientHandler player : players) {
      if (player != sender) {
        player.sendMessage(message);
      }
    }
  }

  public boolean isFull() {
    return players.size() == maxPlayers;
  }

  public String getRoomId() {
    return roomId;
  }
}
