package org.example.project.data.models.mappers;

import org.example.project.data.models.dto.MetaImageInfoResponse;
import org.example.project.data.models.models.MetaImageInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.web.multipart.MultipartFile;

/**
 * Map image dto, models, etc.
 */
@Mapper(componentModel = "spring")
public interface ImageMapper {

  ImageMapper INSTANCE = Mappers.getMapper(ImageMapper.class);

  /**
   * Map meta image info to response.
   *
   * @param image meta image info
   * @return meta image info for response
   */
  @Mapping(target = "filename", expression = "java(image.getName())")
  @Mapping(target = "imageId", expression = "java(image.getId().toString())")
  MetaImageInfoResponse toMetaImageInfoResponse(MetaImageInfo image);

  /**
   * Map image to meta image info.
   *
   * @param image image
   * @return image meta info
   */
  default MetaImageInfo toImageMetaInfo(MultipartFile image) {
    var metaInfo = new MetaImageInfo();

    metaInfo.setName(image.getOriginalFilename());
    metaInfo.setSize(image.getSize());

    return metaInfo;
  }
}
