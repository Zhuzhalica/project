package org.example.filter_three.repository;


import org.example.filter_sdk.ProcessedImageId;
import org.example.filter_three.data.model.ProcessedImage;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository with filter processed image.
 */
public interface ProcessedImageInfoRepository extends
    JpaRepository<ProcessedImage, ProcessedImageId> {

}
