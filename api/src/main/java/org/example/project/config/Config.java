package org.example.project.config;

import java.util.Set;
import org.example.project.settings.ProjectSettings;
import org.example.project.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Custom project configs.
 */
@Configuration
public class Config {

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public JwtUtils jwtUtils(@Value("${jwt.secret}") String secret) {
    return new JwtUtils(secret);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public ProjectSettings settings(@Value("${settings.maxImageSize}") Long maxImageSize,
      @Value("${settings.imageContentTypes}")
      Set<String> imageContentTypes) {
    return new ProjectSettings(maxImageSize, imageContentTypes);
  }
}
