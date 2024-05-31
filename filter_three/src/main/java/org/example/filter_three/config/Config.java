package org.example.filter_three.config;

import io.minio.MinioClient;
import org.example.filter_sdk.ImageRepository;
import org.example.filter_three.settings.FilterSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

  @Bean
  public FilterSettings settings(@Value("${settings.block-size}") int blockSize) {
    return new FilterSettings(blockSize);
  }

  @Bean
  public ImageRepository imageRepository(MinioProperties minioProperties, MinioClient minioClient) {
    return new ImageRepository(minioClient, minioProperties);
  }
}
