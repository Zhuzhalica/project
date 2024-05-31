package org.example.project.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Minio project configs.
 */
@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfig {

  /**
   * Build minio client bean by properties.
   *
   * @param minioProperties properties minio client
   * @return minio client
   * @throws Exception minio client builder exceptions
   */
  @Bean
  public MinioClient minoClient(MinioProperties minioProperties) throws Exception {
    var client = MinioClient.builder()
        .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
        .endpoint(minioProperties.getUrl(), minioProperties.getPort(), minioProperties.isSecure())
        .build();

    var bucketExists = client.bucketExists(
        BucketExistsArgs.builder().bucket(minioProperties.getBucket()).build());
    if (!bucketExists) {
      client.makeBucket(MakeBucketArgs
          .builder()
          .bucket(minioProperties.getBucket())
          .build());
    }

    return client;
  }
}
