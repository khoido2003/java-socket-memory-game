package dev.memory_game.controllers;

import com.google.gson.Gson;
import static spark.Spark.post;
import java.sql.Connection;
import dev.memory_game.DAO.UserDAO;
import dev.memory_game.models.User;
import dev.memory_game.utils.ApiResponse;
import dev.memory_game.utils.BcryptHash;
import dev.memory_game.utils.JwtUtil;

public class AuthController {
  private UserDAO userDAO;
  private Gson gson = new Gson();

  public AuthController(Connection connection) {
    this.userDAO = new UserDAO(connection);
    defineRoutes();
  }

  private void defineRoutes() {
    // Login
    post("/login", (req, res) -> {
      try {
        User loginRequest = gson.fromJson(req.body(), User.class);

        // Check if user exists
        User user = userDAO.getUserByEmail(loginRequest.getEmail());

        if (user != null && BcryptHash.verifyPassword(loginRequest.getPassword(), user.getPassword())) {
          // Generate JWT token and return it to the client
          String token = JwtUtil.generateToken(user.getUserId(), user.getEmail(), user.getUsername());

          res.status(200);
          res.type("application/json");
          return gson.toJson(new ApiResponse(true, "Logged in successfully", token));

        } else {
          res.status(401);
          res.type("application/json");
          return gson.toJson(new ApiResponse(false, "Invalid credentials", null));
        }

      } catch (Exception e) {
        // Log the error
        e.printStackTrace();

        res.status(500);
        res.type("application/json");
        return gson.toJson(new ApiResponse(false, "Internal server error", null));
      }
    });

    post("/signup", (req, res) -> {

      try {

        // Parse the user from the POST request
        User newUser = gson.fromJson(req.body(), User.class);

        // Check if the user is already existing
        User existingUser = userDAO.getUserByEmail(newUser.getEmail());

        if (existingUser != null) {
          res.status(409);
          res.type("application/json");
          return gson.toJson(new ApiResponse(false, "Email already in use", null));
        }

        // Create a new user
        boolean success = userDAO.createUser(newUser);

        if (success) {
          res.status(201);
          res.type("application/json");
          return gson.toJson(new ApiResponse(true, "User created", newUser));
        } else {
          res.status(500);
          res.type("application/json");
          return gson.toJson(new ApiResponse(false, "Failed to create user", null));
        }
      } catch (Exception e) {
        // Log the error
        e.printStackTrace();

        res.status(500);
        res.type("application/json");
        return gson.toJson(new ApiResponse(false, "Internal server error", null));
      }
    });

  }

}