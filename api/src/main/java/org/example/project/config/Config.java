package org.example.project.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.postgresql.Bucket4jPostgreSQL;
import java.time.Duration;
import java.util.Set;
import javax.sql.DataSource;
import org.example.project.settings.ProjectSettings;
import org.example.project.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
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

  @Bean
  public DataSource dataSource(
      @Value("${spring.datasource.url}") String dataSourceUrl,
      @Value("${spring.datasource.username}") String dataSourceUsername,
      @Value("${spring.datasource.password}") String dataSourcePassword
  ) {
    return DataSourceBuilder.create()
        .url(dataSourceUrl)
        .username(dataSourceUsername)
        .password(dataSourcePassword)
        .build();
  }

  @Bean
  public static Bucket createBucket(DataSource dataSource) {
    ProxyManager<Long> proxyManager = Bucket4jPostgreSQL.selectForUpdateBasedBuilder(dataSource)
        .build();
    var configuration = BucketConfiguration.builder()
        .addLimit(
            Bandwidth.builder().capacity(10).refillGreedy(10, Duration.ofMinutes(2)).build())
        .build();

    return proxyManager.getProxy(1L, () -> configuration);
  }
}
