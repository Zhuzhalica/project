package org.example.project.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.example.project.utils.JwtUtils;
import org.springframework.stereotype.Service;

/**
 * Service for work with jwt tokens.
 */
@Service
@RequiredArgsConstructor
public class JwtService {

  private final JwtUtils jwtUtils;

  /**
   * Get token from user login.
   *
   * @param login user login
   * @return jwt-token
   */
  public String getToken(String login) {
    return Jwts
        .builder()
        .setSubject(login)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .signWith(jwtUtils.getKey(), SignatureAlgorithm.HS256)
        .compact();
  }
}
