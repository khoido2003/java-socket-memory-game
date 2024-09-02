package dev.memory_game.controllers;

import dev.memory_game.models.GameModel;
import dev.memory_game.views.GameView;

public class GameController {
  private GameModel model;
  private GameView view;

  public GameController(GameModel model, GameView view) {
    this.model = model;
    this.view = view;
  }

  public void startGame() {
    model.setGameState("STARTED");
    view.displayMessage("Game has started!");
  }

  public void updateScore(int points) {
    model.setScore(model.getScore() + points);
    view.displayScore(model.getScore());
  }

  // Additional methods to handle game logic
}