package org.example.project.repositories;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import java.io.ByteArrayInputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.project.config.MinioProperties;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

/**
 * Repository with images.
 */
@Repository
@RequiredArgsConstructor
public class ImageRepository {

  private final MinioClient client;
  private final MinioProperties properties;

  /**
   * Save image.
   *
   * @param image image
   * @return image id
   * @throws Exception save exception
   */
  public UUID loadImage(MultipartFile image) throws Exception {
    var fileId = UUID.randomUUID();

    var inputStream = new ByteArrayInputStream(image.getBytes());
    client.putObject(
        PutObjectArgs.builder().bucket(properties.getBucket()).object(fileId.toString())
            .stream(inputStream, image.getSize(), properties.getImageSize())
            .contentType(image.getContentType()).build());

    return fileId;
  }

  /**
   * Get image.
   *
   * @param id image id
   * @return image as bytes
   * @throws Exception get image exception
   */
  public byte[] downloadImage(UUID id) throws Exception {
    return client
        .getObject(
            GetObjectArgs.builder().bucket(properties.getBucket()).object(id.toString()).build())
        .readAllBytes();
  }

  /**
   * Delete image.
   *
   * @param id image id
   * @throws Exception delete image exception
   */
  public void deleteImage(UUID id) throws Exception {
    client.removeObject(
        RemoveObjectArgs.builder().bucket(properties.getBucket()).object(id.toString()).build());
  }

  public StatObjectResponse getImageMeta(UUID id) throws Exception {
    return client.statObject(
        StatObjectArgs.builder().bucket(properties.getBucket()).object(id.toString()).build());
  }
}
