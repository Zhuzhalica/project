package org.example.project.repositories;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.IOUtils;
import org.example.project.config.MinioProperties;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ImageRepository {

    private final MinioClient client;
    private final MinioProperties properties;

    public UUID loadImage(MultipartFile file) throws Exception {
        var fileId = UUID.randomUUID();

        var inputStream = new ByteArrayInputStream(file.getBytes());
        client.putObject(
                PutObjectArgs
                        .builder()
                        .bucket(properties.getBucket())
                        .object(fileId.toString())
                        .stream(inputStream, file.getSize(), properties.getImageSize())
                        .contentType(file.getContentType())
                        .build()
        );

        return fileId;
    }

    public byte[] downloadImage(UUID id) throws Exception {
        return IOUtils.toByteArray(
                client.getObject(
                        GetObjectArgs
                                .builder()
                                .bucket(properties.getBucket())
                                .object(id.toString())
                                .build()
                )
        );
    }

    public void deleteImage(UUID id) throws Exception {
        client.removeObject(
                RemoveObjectArgs
                        .builder()
                        .bucket(properties.getBucket())
                        .object(id.toString())
                        .build());
    }
}
