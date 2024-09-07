package dev.memory_game.controllers;

import java.sql.Connection;
import java.util.concurrent.ExecutorService;;

public class RoutesController {

  public RoutesController(Connection connection, ExecutorService threadPool) {

    new UserController(connection);
    new AuthController(connection);

    System.out.println("HTTP server start on port 8081");
  }

}
