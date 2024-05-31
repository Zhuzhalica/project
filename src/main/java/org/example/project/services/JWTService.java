package org.example.project.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.example.project.utils.JWTUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class JWTService {
    private final JWTUtils jwtUtils;
    public String getToken(String login) {
        return Jwts
                .builder()
                .setSubject(login)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(jwtUtils.getKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
