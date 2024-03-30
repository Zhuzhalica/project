package org.example.project.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public class JWTUtils {
    private final String jwtSecret;

    public Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        var login = getLogin(token);

        return login.equals(userDetails.getUsername());
    }

    public String getLogin(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
