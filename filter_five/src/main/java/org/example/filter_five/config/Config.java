package org.example.filter_five.config;

import io.minio.MinioClient;
import org.example.filter_five.settings.FilterSettings;
import org.example.filter_sdk.ImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

  @Bean
  public FilterSettings settings(@Value("${settings.number-color-levels}") int numberColorLevels) {
    return new FilterSettings(numberColorLevels);
  }

  @Bean
  public ImageRepository imageRepository(MinioProperties minioProperties, MinioClient minioClient) {
    return new ImageRepository(minioClient, minioProperties);
  }
}
