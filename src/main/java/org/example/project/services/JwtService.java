package org.example.project.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.example.project.utils.JwtUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

  private final JwtUtils jwtUtils;

  public String getToken(String login) {
    return Jwts
        .builder()
        .setSubject(login)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .signWith(jwtUtils.getKey(), SignatureAlgorithm.HS256)
        .compact();
  }
}
