package dev.memory_game.utils;

public class ApiResponse {
  private boolean success;
  private String message;
  private Object data;

  public ApiResponse(boolean success, String message, Object data) {
    this.success = success;
    this.message = message;
    this.data = data;
  }

  // Getters
  public boolean isSuccess() {
    return success;
  }

  public String getMessage() {
    return message;
  }

  public Object getData() {
    return data;
  }
}
