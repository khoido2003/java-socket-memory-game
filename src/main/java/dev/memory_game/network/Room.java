
package dev.memory_game.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Room {
  private int maxPlayers;
  private String roomId;
  private Set<ClientHandler> players = new HashSet<>();
  private int currentQuestionIndex = 0;
  private boolean gameInProgress = false;
  private List<String> questions = new ArrayList<>();
  private Map<ClientHandler, Integer> mapScore = new HashMap<>();
  private boolean waitingForAnswers = false;

  public Room(String roomId, int maxPlayers) {
    this.roomId = roomId;
    this.maxPlayers = maxPlayers;
  }

  public String getRoomId() {
    return roomId;
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
    for (int i = 0; i < 10; i++) {
      questions.add(generateRandomString(random));
    }
  }

  private String generateRandomString(Random random) {
    int length = 5;
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      char c = (char) (random.nextInt(26) + 'a');
      sb.append(c);
    }
    return sb.toString();
  }

  private void sendNextQuestion() {
    if (currentQuestionIndex < questions.size()) {
      String question = questions.get(currentQuestionIndex);
      broadcast("Question: " + question, null);
      // Start a timer for the question (10 seconds)

      waitingForAnswers = true;
      startTimer();
    } else {
      endGame();
    }
  }

  private void startTimer() {
    new Thread(() -> {
      try {
        Thread.sleep(10000); // Wait for 10 seconds
        // Notify players that time is up and ask for answers
        if (waitingForAnswers) { // Only notify if still waiting
          broadcast("Time's up! Please send your answers.", null);
          currentQuestionIndex++;
          waitingForAnswers = false; // Set state to no longer wait

          // Send the next question
          sendNextQuestion();
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }).start();
  }

  public void receiveAnswer(ClientHandler player, String answer) {
    if (!waitingForAnswers) {
      player.sendMessage("Too late! The next question is coming.");
      return;
    }

    // Check if the answer is correct
    String correctAnswer = questions.get(currentQuestionIndex);
    if (answer.equals(correctAnswer)) {
      mapScore.put(player, mapScore.get(player) + 1); // Increment player's score
      player.sendMessage("Correct! You have earned a point.");
    } else {
      player.sendMessage("Incorrect! The correct answer was: " + correctAnswer);
    }
  }

  private void endGame() {
    gameInProgress = false;
    broadcast("Game over! Final scores: ", null);
    // Reset the room or implement any end-of-game logic here
  }
}
