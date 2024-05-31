package org.example.filter_one.config;


import io.minio.MinioClient;
import org.example.filter_sdk.ImageRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

  @Bean
  public ImageRepository imageRepository(MinioProperties minioProperties, MinioClient minioClient) {
    return new ImageRepository(minioClient, minioProperties);
  }
}
