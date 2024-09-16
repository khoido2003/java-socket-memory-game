package dev.memory_game.utils;

import dev.memory_game.DAO.UserDAO;
import dev.memory_game.models.JwtToken;
import dev.memory_game.models.User;

import static spark.Spark.before;
import static spark.Spark.halt;

import java.sql.Connection;

import com.google.gson.Gson;

public class AuthMiddleware {

  private UserDAO userDAO;
  private Gson gson = new Gson();

  public AuthMiddleware(Connection connection) {
    this.userDAO = new UserDAO(connection);
  }

  public void protectedRoute() {
    before((req, res) -> {
      try {
        String token = null;

        // Check if the token is provided in Authorization header as Bearer token
        String authHeader = req.headers("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
          token = authHeader.split(" ")[1];
        }

        // Or check if the token is in a cookie for web application
        else if (req.cookie("auth_token") != null) {
          token = req.cookie("auth_token");
        }

        if (token != null) {
          res.status(401);
          res.type("application/json");
          halt(gson.toJson(new ApiResponse(false, "Unauthorized", null)));
        }

        // Decoded the token and verify
        JwtToken decodedToken = JwtUtil.decodeToken(token);

        if (decodedToken == null) {
          res.status(401);
          res.type("application/json");
          halt(gson.toJson(new ApiResponse(false, "Unauthorized", null)));
        }

        User currentUser = userDAO.getUserByEmail(decodedToken.getEmail());

        if (currentUser == null) {
          res.status(401);
          res.type("application/json");
          halt(gson.toJson(new ApiResponse(false, "Unauthorized", null)));
        }

        // Store the currentUser in request attribute
        req.attribute("user", currentUser);

      } catch (Exception e) {

        e.printStackTrace();
        halt(500, "{\"error\":\"Internal Server Error\"}");
      }
    });
  }

}
