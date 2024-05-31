package org.example.imagga_filter.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.postgresql.Bucket4jPostgreSQL;
import io.minio.MinioClient;
import java.time.Duration;
import javax.sql.DataSource;
import org.example.filter_sdk.ImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

  @Bean
  public ImageRepository imageRepository(MinioProperties minioProperties, MinioClient minioClient) {
    return new ImageRepository(minioClient, minioProperties);
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
            Bandwidth.builder().capacity(10).refillGreedy(10, Duration.ofMinutes(1)).build())
        .build();

    return proxyManager.getProxy(1L, () -> configuration);
  }
}
