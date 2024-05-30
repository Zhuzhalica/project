package org.example.filter_sdk;

import lombok.Getter;
import lombok.Setter;

/**
 * Minio project properties.
 */
@Getter
@Setter
public class MinioProperties {

  private String url;
  private int port;
  private String accessKey;
  private String secretKey;
  private boolean secure;
  private String bucket;
  private long imageSize;
}
