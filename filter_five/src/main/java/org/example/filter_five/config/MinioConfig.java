package org.example.filter_five.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketLifecycleArgs;
import io.minio.messages.AndOperator;
import io.minio.messages.Expiration;
import io.minio.messages.LifecycleConfiguration;
import io.minio.messages.LifecycleRule;
import io.minio.messages.RuleFilter;
import io.minio.messages.Status;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
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

    ZonedDateTime zonedDateTime = null;

    var tags = new HashMap<String, String>();
    tags.put("status", "wipImage");
    RuleFilter filter = new RuleFilter(new AndOperator("", tags));

    var lifecycleRules = new ArrayList<LifecycleRule>();
    lifecycleRules.add(new LifecycleRule(
        Status.ENABLED,
        null,
        new Expiration(zonedDateTime, 1, null),
        filter,
        "imageExpirationRule",
        null,
        null,
        null));
    var lifecycleConfig = new LifecycleConfiguration(lifecycleRules);

    var bucketExists = client.bucketExists(
        BucketExistsArgs.builder().bucket(minioProperties.getBucket()).build());
    if (!bucketExists) {
      client.makeBucket(MakeBucketArgs
          .builder()
          .bucket(minioProperties.getBucket())
          .build());
    }
    client.setBucketLifecycle(SetBucketLifecycleArgs
        .builder()
        .bucket(minioProperties.getBucket())
        .config(lifecycleConfig)
        .build());
    return client;
  }
}
