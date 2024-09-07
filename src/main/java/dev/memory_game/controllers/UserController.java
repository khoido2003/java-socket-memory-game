package dev.memory_game.controllers;

import static spark.Spark.*;

import com.google.gson.Gson;

import dev.memory_game.DAO.UserDAO;
import dev.memory_game.models.User;
import dev.memory_game.utils.ApiResponse;
import dev.memory_game.utils.AuthMiddleware;

import java.sql.Connection;
import java.util.List;

public class UserController {

  private UserDAO userDAO;
  private Gson gson = new Gson();
  private AuthMiddleware authMiddleware;

  public UserController(Connection connection) {
    this.userDAO = new UserDAO(connection);
    defineRoutes();
    authMiddleware = new AuthMiddleware(connection);
  }

  private void defineRoutes() {

    // Create users in dev mode - in production mode use /signup instead
    post("/users", (req, res) -> {
      User user = gson.fromJson(req.body(), User.class);
      boolean success = userDAO.createUser(user);

      res.status(success ? 201 : 500);
      res.type("application/json");
      return gson
          .toJson(new ApiResponse(success, success ? "User created" : "User creation failed", success ? user : null));
    });

    // Get all users
    get("/users", (req, res) -> {
      List<User> users = userDAO.getAllUsers();
      if (users == null || users.isEmpty()) {
        res.status(404);
        res.type("application/json");
        return gson.toJson(new ApiResponse(false, "No users found", null));
      }
      res.type("application/json");
      return gson.toJson(new ApiResponse(true, "Users retrieved successfully", users));
    });

    // Get a user by their ID
    get("/users/:id", (req, res) -> {
      String userId = req.params(":id");
      User user = userDAO.getUserById(userId);
      if (user == null) {
        res.status(404);
        res.type("application/json");
        return gson.toJson(new ApiResponse(false, "No user found with ID: " + userId, null));
      }
      res.status(200);
      res.type("application/json");
      return gson.toJson(new ApiResponse(true, "User retrieved successfully", user));
    });

    // Update a user by their ID
    patch("/users/:id", (req, res) -> {
      String userId = req.params(":id");
      User updatedUser = gson.fromJson(req.body(), User.class);
      updatedUser.setUserId(userId);
      boolean success = userDAO.updateUser(updatedUser);

      res.status(success ? 200 : 500);
      res.type("application/json");
      return gson.toJson(new ApiResponse(success, success ? "User updated successfully" : "User update failed", null));
    });

    // Delete a user by their ID
    delete("/users/:id", (req, res) -> {
      String userId = req.params(":id");
      boolean success = userDAO.deleteUser(userId);
      res.status(success ? 200 : 500);
      res.type("application/json");
      return gson
          .toJson(new ApiResponse(success, success ? "User deleted successfully" : "User deletion failed", null));
    });
  }

}
