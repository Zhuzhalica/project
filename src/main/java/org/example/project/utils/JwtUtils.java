package org.example.project.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Frequent methods for work with jwt.
 */
@Getter
@RequiredArgsConstructor
public class JwtUtils {

  private final String jwtSecret;

  /**
   * Get decode key.
   *
   * @return key
   */
  public Key getKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);

    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * Check valid token.
   *
   * @param token       token
   * @param userDetails user details service
   * @return valid
   */
  public boolean isTokenValid(String token, UserDetails userDetails) {
    var login = getLogin(token);

    return login.equals(userDetails.getUsername());
  }

  /**
   * Get user login from token.
   *
   * @param token token
   * @return user login
   */
  public String getLogin(String token) {
    return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody()
        .getSubject();
  }
}
