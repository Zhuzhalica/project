package org.example.imagga_filter;

import io.github.bucket4j.Bucket;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.minio.StatObjectResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.function.Supplier;
import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ImmagaClient {

  @Value("${settings.immaga.apikey}")
  private String apiKey;
  @Value("${settings.immaga.secret}")
  private String immagaSecret;
  private final CircuitBreaker circuitBreaker;
  private final RateLimiter rateLimiter;
  private final Retry retry;
  private final Bucket bucket;

  public ImmagaClient(Bucket bucket) {
    this.bucket = bucket;

    var circuitBreakerRegistry = CircuitBreakerRegistry.ofDefaults();
    this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("imaggaService");

    var rateLimiterConfig = RateLimiterConfig.custom()
        .limitRefreshPeriod(Duration.ofSeconds(5))
        .limitForPeriod(10)
        .timeoutDuration(Duration.ofSeconds(10))
        .build();
    var rateLimiterRegistry = RateLimiterRegistry.of(rateLimiterConfig);
    this.rateLimiter = rateLimiterRegistry.rateLimiter("imaggaService");

    var retryConfig = RetryConfig.custom()
        .maxAttempts(3)
        .waitDuration(Duration.ofMillis(1000))
        .retryOnException(this::isRetryableException)
        .build();
    var retryRegistry = RetryRegistry.of(retryConfig);
    this.retry = retryRegistry.retry("imaggaService");
  }

  public String uploadImage(byte[] image, StatObjectResponse metaInfo) {
    var canSendRequest = bucket.tryConsume(1);
    if (!canSendRequest) {
      throw new RuntimeException();
    }

    Supplier<String> uploadImageSupplier = () -> {
      try {
        return uploadImageInternal(image, metaInfo);
      } catch (IOException | ParseException e) {
        throw new RuntimeException(e);
      }
    };

    return Decorators.ofSupplier(uploadImageSupplier)
        .withCircuitBreaker(circuitBreaker)
        .withRateLimiter(rateLimiter)
        .withRetry(retry)
        .get();
  }

  private String uploadImageInternal(byte[] image, StatObjectResponse metaInfo)
      throws IOException, ParseException {
    var canSendRequest = bucket.tryConsume(1);
    if (!canSendRequest) {
      throw new RuntimeException();
    }

    var client = HttpClients.createDefault();
    var post = new HttpPost("https://api.imagga.com/v2/uploads");

    var entity = MultipartEntityBuilder.create()
        .addBinaryBody("image", image, ContentType.create(metaInfo.contentType()),
            metaInfo.object())
        .build();
    post.setEntity(entity);
    post.addHeader("Authorization",
        "Basic " + Base64.getEncoder().encodeToString((apiKey + ":" + immagaSecret).getBytes()));

    var response = client.execute(post);
    if (response.getCode() >= 400) {
      throw new HttpResponseException(response.getCode(), response.getReasonPhrase());
    }

    var jsonResponse = EntityUtils.toString(response.getEntity());
    client.close();

    var jsonObject = new JSONObject(jsonResponse);
    if (jsonObject.has("result")) {
      return jsonObject.getJSONObject("result").getString("upload_id");
    }

    return null;
  }

  public String[] getTopTags(String uploadId) {
    Supplier<String[]> getTopTagsSupplier = () -> {
      try {
        return getTopTagsInternal(uploadId);
      } catch (IOException | ParseException e) {
        throw new RuntimeException(e);
      }
    };

    return Decorators.ofSupplier(getTopTagsSupplier)
        .withCircuitBreaker(circuitBreaker)
        .withRateLimiter(rateLimiter)
        .withRetry(retry)
        .get();
  }

  private String[] getTopTagsInternal(String uploadId) throws IOException, ParseException {
    var client = HttpClients.createDefault();
    var get = new HttpGet("https://api.imagga.com/v2/tags?image_upload_id=" + uploadId);
    get.addHeader("Authorization",
        "Basic " + Base64.getEncoder().encodeToString((apiKey + ":" + immagaSecret).getBytes()));

    var response = client.execute(get);
    if (response.getCode() >= 400) {
      throw new HttpResponseException(response.getCode(), response.getReasonPhrase());
    }

    var jsonResponse = EntityUtils.toString(response.getEntity());
    client.close();

    var jsonObject = new JSONObject(jsonResponse);
    if (jsonObject.has("result")) {
      var tags = jsonObject.getJSONObject("result").getJSONArray("tags");

      var topTags = new String[3];
      for (var i = 0; i < 3 && i < tags.length(); i++) {
        topTags[i] = tags.getJSONObject(i).getJSONObject("tag").getString("en");
      }

      return topTags;
    }

    return null;
  }

  private boolean isRetryableException(Throwable throwable) {
    if (throwable.getCause() instanceof HttpResponseException) {
      int statusCode = ((HttpResponseException) throwable.getCause()).getStatusCode();
      return statusCode == 429 || (statusCode >= 500 && statusCode < 600);
    }
    return false;
  }
}
