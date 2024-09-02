package dev.memory_game.models;

public class GameModel {
  private int score = 0;
  private String gameState;

  public GameModel() {
    this.score = 0;
    this.gameState = "Not Started";
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public String getGameState() {
    return this.gameState;
  }

  public void setGameState(String gameState) {
    this.gameState = gameState;
  }

}
