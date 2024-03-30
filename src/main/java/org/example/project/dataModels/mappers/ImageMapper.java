package org.example.project.dataModels.mappers;

import org.example.project.dataModels.dto.MetaImageInfoResponse;
import org.example.project.dataModels.models.MetaImageInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.web.multipart.MultipartFile;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    ImageMapper INSTANCE = Mappers.getMapper(ImageMapper.class);
    @Mapping(target = "filename", expression = "java(image.getName())")
    @Mapping(target = "imageId", expression = "java(image.getId().toString())")
    MetaImageInfoResponse toMetaImageInfoResponse(MetaImageInfo image);

    default MetaImageInfo toImageMetaInfo(MultipartFile image){
        var metaInfo = new MetaImageInfo();

        metaInfo.setName(image.getOriginalFilename());
        metaInfo.setSize(image.getSize());

        return metaInfo;
    }
}
