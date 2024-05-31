package org.example.project.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lombok.RequiredArgsConstructor;
import org.example.project.settings.ProjectSettings;
import org.example.project.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

import java.util.List;
import java.util.Set;

@Configuration
public class Config {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JWTUtils jwtUtils(@Value("${jwt.secret}") String secret) {
        return new JWTUtils(secret);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ProjectSettings settings(@Value("${settings.maxImageSize}") Long maxImageSize, @Value("${settings.imageContentTypes}") Set<String> imageContentTypes) {
        return new ProjectSettings(maxImageSize, imageContentTypes);
    }
}
