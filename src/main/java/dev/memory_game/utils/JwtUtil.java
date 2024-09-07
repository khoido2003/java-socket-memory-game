package dev.memory_game.utils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import dev.memory_game.models.JwtToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
// import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.Base64;

public class JwtUtil {

    // Generate a secret key
    // private static final SecretKey key =
    // Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private static String encodedKey = "MdqpgWh/uaoVDaO2SzQqd4j9ZFcLzzl8xKjhue5pANEHy7xjYXyryjb9Z0XZqTEg0Pq6zi2NQR2fJaGOXxyocA==";

    // Decode the base64 encoded string into a byte array
    private static byte[] decodedKey = Base64.getDecoder().decode(encodedKey);

    // Create a SecretKey from the byte array, specifying the algorithm
    private static SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

    public static String generateToken(String userId, String email, String username) {

        // Expiration time: 2592000000L milliseconds (30 days)
        return Jwts.builder().setSubject(userId).claim("email", email).claim("name", username).setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 2592000000L)).signWith(secretKey).compact();
    }

    // Take out the data in the token
    public static JwtToken decodeToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();

            String userId = claims.getSubject();
            String email = (String) claims.get("email");
            String username = (String) claims.get("username");

            return new JwtToken(userId, email, username);

        } catch (ExpiredJwtException e) {
            // If the token is outdated then it will go into here
            System.out.println("Token has expired: " + token);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Validate base on the userID
    public static boolean validateToken(String token, String clientId) {
        try {
            // Verify the signature and expiration time of the token
            Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();

            return claims.getSubject().equals(clientId);
        } catch (Exception e) {
            return false;
        }
    }
}