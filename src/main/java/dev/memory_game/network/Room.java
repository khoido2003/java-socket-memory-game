
package dev.memory_game.network;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import dev.memory_game.DAO.UserDAO;

public class Room {
  private final int TOTAL_NUM = 5;
  private int maxPlayers;
  private String roomId;
  private Set<ClientHandler> players = new HashSet<>();
  private int currentQuestionIndex = 0;
  private boolean gameInProgress = false;
  private List<String> questions = new ArrayList<>();
  private Map<ClientHandler, Integer> mapScore = new HashMap<>();
  private boolean waitingForAnswers = false;
  private Connection connection;

  public Room(String roomId, int maxPlayers, Connection connection) {
    this.roomId = roomId;
    this.maxPlayers = maxPlayers;
    this.connection = connection;
  }

  public String getRoomId() {
    return roomId;
  }

  public Set<ClientHandler> getPlayers() {
    return players;
  }

  public synchronized boolean addPlayer(ClientHandler player) {
    if (players.size() < maxPlayers) {
      players.add(player);
      mapScore.put(player, 0);
      return true;
    }
    return false;
  }

  public void removePlayer(ClientHandler player) {
    players.remove(player);
  }

  public void removeAllPlayers() {
    players.clear();
    mapScore.clear();
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

  public boolean getGameInProgress() {
    return this.gameInProgress;
  }

  public void setGameInProgress(boolean gameInProgress) {
    this.gameInProgress = gameInProgress;
  }

  ////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////

  public void startGame() {
    gameInProgress = true;
    currentQuestionIndex = 0;
    generateQuestions();
    sendNextQuestion();

  }

  private void generateQuestions() {
    Random random = new Random();
    for (int i = 0; i < TOTAL_NUM; i++) {
      questions.add(generateRandomString(random, i));
    }
  }

  private String generateRandomString(Random random, int cnt) {
    int length = 2 + cnt; // Increase length as the index increases
    StringBuilder sb = new StringBuilder(length);

    // Adjust difficulty: use more letters and numbers as index increases
    for (int i = 0; i < length; i++) {
      if (random.nextBoolean()) {
        // Randomly add a letter (uppercase or lowercase)
        char letter = (char) (random.nextInt(26) + (random.nextBoolean() ? 'a' : 'A'));
        sb.append(letter);
      } else {
        // Randomly add a digit (0-9)
        char digit = (char) (random.nextInt(10) + '0');
        sb.append(digit);
      }
    }
    return sb.toString();
  }

  private void sendNextQuestion() {
    if (currentQuestionIndex < TOTAL_NUM && gameInProgress == true) {
      String question = questions.get(currentQuestionIndex);
      int curIndex = currentQuestionIndex + 1;
      broadcast("QUESTION: " + question + " " + curIndex, null);

      // Start the memorization timer (5 seconds)
      new Thread(() -> {
        try {
          Thread.sleep(6000); // Wait for 5 seconds
          hideQuestion(); // Hide the question after 5 seconds
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }).start();
    } else {
      endGame();
    }
  }

  private void hideQuestion() {
    broadcast("HIDE_QUESTION: ", null);
    waitingForAnswers = true;

    // Start the answer submission timer (10 seconds)
    new Thread(() -> {
      try {
        Thread.sleep(11000); // Wait for 10 seconds
        if (waitingForAnswers) {
          broadcast("NEXT_QUESTION: ", null);
          currentQuestionIndex++;
          waitingForAnswers = false;

          // Send the next question after this one is done
          sendNextQuestion();
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }).start();
  }

  public void receiveAnswer(ClientHandler player, String answer) {
    if (!waitingForAnswers) {
      player.sendMessage("TIME_UP: ");
      return;
    }
    // Check if the answer is correct
    String correctAnswer = questions.get(currentQuestionIndex);
    if (answer.equals(correctAnswer)) {
      mapScore.put(player, mapScore.get(player) + 1);
      player.sendMessage("CORRECT: " + currentQuestionIndex);
    } else {
      player.sendMessage("WRONG: " + correctAnswer);
    }
  }

  private void endGame() {
    gameInProgress = false;
    broadcast("END: Game over! ", null);
    // Reset the room or implement any end-of-game logic here
  }

  public void savePoint(ClientHandler clientHandler, int point) {
    mapScore.put(clientHandler, point);
  }

  public void comparePoint() {

    if (mapScore.size() < 2)
      return;

    int maxScore = 0;
    List<ClientHandler> winners = new ArrayList<>();
    List<ClientHandler> losers = new ArrayList<>();

    // Find the maximum score
    for (ClientHandler clientHandler : mapScore.keySet()) {
      int point = mapScore.get(clientHandler);
      if (point > maxScore) {
        maxScore = point;
      }
    }

    // Classify players as winners or losers based on the maximum score
    for (ClientHandler clientHandler : mapScore.keySet()) {
      int point = mapScore.get(clientHandler);
      if (point == maxScore) {
        winners.add(clientHandler);
      } else {
        losers.add(clientHandler);
      }
    }

    // If all players have the same score, it's a draw
    if (winners.size() == players.size()) {
      broadcast("RESULT: Draw matchup!", null);
    } else {
      UserDAO userDAO = new UserDAO(connection);
      // Announce winners and losers
      for (ClientHandler winner : winners) {
        winner.sendMessage("RESULT: You win!");
        String userID = winner.getClientID();

        userDAO.updateUserTotalPoint(userID, 30);

      }
      for (ClientHandler loser : losers) {
        loser.sendMessage("RESULT: You lose!");
        String userID = loser.getClientID();
        userDAO.updateUserTotalPoint(userID, -30);
      }
    }
  }
}
