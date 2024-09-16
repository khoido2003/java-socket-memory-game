package dev.memory_game.utils;

import org.mindrot.jbcrypt.BCrypt;

public class BcryptHash {

  // Hash a plain text password with BCrypt algorithm
  public static String hashPassword(String plainPassword) {
    return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
  }

  // Verify a plain text password with BCrypt algorithm
  public static boolean verifyPassword(String plainPassword, String hashedPassword) {
    return BCrypt.checkpw(plainPassword, hashedPassword);
  }

}
