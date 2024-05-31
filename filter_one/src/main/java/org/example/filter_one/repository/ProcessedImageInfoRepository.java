package org.example.filter_one.repository;


import org.example.filter_one.data.model.ProcessedImage;
import org.example.filter_sdk.ProcessedImageId;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository with filter processed image.
 */
public interface ProcessedImageInfoRepository extends
    JpaRepository<ProcessedImage, ProcessedImageId> {

}
