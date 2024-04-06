package org.example.project.config;

import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.messages.Bucket;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Minio project configs.
 */
@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfig {

  @Bean
  public MinioClient minoClient(MinioProperties minioProperties) throws Exception {
    var client = MinioClient.builder()
        .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
        .endpoint(minioProperties.getUrl(), minioProperties.getPort(), minioProperties.isSecure())
        .build();
    var buckets = client.listBuckets().stream().map(Bucket::name).collect(Collectors.toSet());
    if (!buckets.contains(minioProperties.getBucket())) {
      client.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getBucket()).build());
    }
    return client;
  }
}
