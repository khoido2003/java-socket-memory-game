package dev.memory_game.models;

public class JwtToken {
  public String userId;
  public String email;
  public String name;

  public JwtToken(String userId, String email, String name) {
    this.userId = userId;
    this.email = email;
    this.name = name;
  }
}
