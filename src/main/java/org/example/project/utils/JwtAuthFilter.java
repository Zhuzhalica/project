package org.example.project.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtUtils jwtUtils;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    final String authTokenValue = request.getHeader("Authorization");

    if (authTokenValue == null || !authTokenValue.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    var jwtToken = authTokenValue.substring(7);
    var login = jwtUtils.getLogin(jwtToken);

    if (login != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      var userDetails = this.userDetailsService.loadUserByUsername(login);

      if (jwtUtils.isTokenValid(jwtToken, userDetails)) {
        var authToken = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }

    filterChain.doFilter(request, response);
  }
}
