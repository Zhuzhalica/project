package org.example.filter_three.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Minio project properties.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "minio")
public class MinioProperties extends org.example.filter_sdk.MinioProperties {

}
